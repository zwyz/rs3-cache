package rs3.unpack.script;

import rs3.Unpack;
import rs3.unpack.ScriptTrigger;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.VarDomain;

import java.util.*;
import java.util.stream.IntStream;

import static rs3.unpack.script.Command.*;

// 1. Visit the code, generating a type-valued variable for each code element whose type has to
//    be inferred, along with constraints between these variables based on data flow. The types
//    form a lattice of elements where lower elements represent more knowledge (for example, loc
//    < unknown_int < unknown).
//
// 2. Iteratively apply local rules to propagate knowledge across constraints, replacing the
//    type of vertices connected by an edge with the meet of the previous types, but with the
//    exception that namedobj -> ? only propagates forward as namedobj -> obj to allow for
//    upcasting when necessary. When two alias types conflict, their meet is the int_int type
//    type which then propagates across the entire connected component to erase the bad alias.
//    Constraints are re-processed when one of their variables changes type, and is repeated
//    until no more constraints are left to process.
//
// 3. Read the values of the type variables to set expression types, parameter types, etc. used
//    by other parts of the unpacker.
public class TypePropagator {
    private final Map<Node, Type> vars = new LinkedHashMap<>();
    private final Set<Constraint> constraints = new LinkedHashSet<>();

    public void run(int script, List<Expression> expressions) {
        for (var expression : expressions) {
            run(script, expression);
        }
    }

    public void run(int script, Expression expression) {
        // initial types from commands
        var types = expression.type;

        for (int i = 0; i < types.size(); i++) {
            emitAssign(type(expression, i), types.get(i));
        }

        // arguments
        if (expression.command == GOSUB_WITH_PARAMS) {
            var otherScript = (int) expression.operand;
            ScriptUnpacker.CALLED.add(otherScript);
            var index = 0;

            for (var i = 0; i < expression.arguments.size(); i++) {
                var arg = expression.arguments.get(i);

                for (var j = 0; j < arg.type.size(); j++) {
                    emitAssign(type(arg, j), parameter(otherScript, index++));
                }
            }

            for (var i = 0; i < expression.type.size(); i++) {
                emitEqual(type(expression, i), result(otherScript, i));
            }
        }

        if (expression.command.hasHook()) {
            var hookStart = expression.command.arguments.indexOf(Type.HOOK);
            var hookEnd = hookStart + expression.arguments.size() - (expression.command.arguments.size() - 1);

            var otherScript = (int) expression.arguments.get(hookStart++).operand;
            var signature = (String) expression.arguments.get(hookEnd-- - 1).operand;

            if (signature.endsWith("Y")) {
                var transmitListCount = (int) expression.arguments.get(hookEnd - 1).operand;
                hookEnd -= 1 + transmitListCount;
            }

            if (otherScript != -1) {
                ScriptUnpacker.CALLED.add(otherScript);
                ScriptUnpacker.SCRIPT_TRIGGERS.put(otherScript, ScriptTrigger.CLIENTSCRIPT);
                var index = 0;

                for (var i = hookStart; i < hookEnd; i++) {
                    var arg = expression.arguments.get(i);

                    if (arg.command == PUSH_CONSTANT_INT || arg.command == PUSH_CONSTANT_STRING) {
                        if (Objects.equals(arg.operand, "event_opbase")) emitAssign(type(arg, 0), Type.STRING); // event_opbase
                        if (Objects.equals(arg.operand, "event_text")) emitAssign(type(arg, 0), Type.STRING); // event_text
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 1)) emitAssign(type(arg, 0), Type.INT_INT); // event_mousex
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 2)) emitAssign(type(arg, 0), Type.INT_INT); // event_mousey
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 3)) emitAssign(type(arg, 0), Type.COMPONENT); // event_com
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 4)) emitAssign(type(arg, 0), Type.INT_INT); // event_op
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 5)) emitAssign(type(arg, 0), Type.INT_INT); // event_comsubid
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 6)) emitAssign(type(arg, 0), Type.COMPONENT); // event_com2
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 7)) emitAssign(type(arg, 0), Type.INT_INT); // event_comsubid2
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 8)) emitAssign(type(arg, 0), Type.INT_KEY); // event_keycode
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 9)) emitAssign(type(arg, 0), Type.CHAR); // event_keychar
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 10)) emitAssign(type(arg, 0), Type.INT_INT); // event_gamepadvalue
                        if (Objects.equals(arg.operand, Integer.MIN_VALUE + 11)) emitAssign(type(arg, 0), Type.INT_INT); // event_gamepadbutton
                    }

                    for (var j = 0; j < arg.type.size(); j++) {
                        emitAssign(type(arg, j), parameter(otherScript, index++));
                    }
                }
            }
        }

        if (expression.command == RETURN) {
            var index = 0;

            for (var i = 0; i < expression.arguments.size(); i++) {
                var arg = expression.arguments.get(i);

                for (var j = 0; j < arg.type.size(); j++) {
                    emitAssign(type(arg, j), result(script, index++));
                }
            }
        }

        // locals
        if (expression.command == FLOW_LOAD) {
            if (expression.operand instanceof LocalReference local) {
                emitEqual(type(expression, 0), local(script, local.domain(), local.local()));
            }

            if (expression.operand instanceof VarReference var) {
                if (Unpack.VERSION >= 742) {
                    emitAssign(type(expression, 0), Unpacker.getVarType(var.domain(), var.var()));
                }

                emitEqual(type(expression, 0), var(var.domain(), var.var()));
            }

            if (expression.operand instanceof VarBitReference var) {
                emitEqual(type(expression, 0), varbit(var.var()));
            }

            if (expression.operand instanceof VarClientReference var) {
                emitEqual(type(expression, 0), varclient(var.var()));
            }
        }

        if (expression.command == FLOW_ASSIGN) {
            var targets = (List<Object>) expression.operand;

            for (var i = 0; i < targets.size(); i++) {
                if (targets.get(i) instanceof LocalReference local) {
                    emitAssign(arg(expression, i), local(script, local.domain(), local.local()));
                }

                if (targets.get(i) instanceof VarReference var) {
                    emitAssign(arg(expression, i), var(var.domain(), var.var()));
                }

                if (targets.get(i) instanceof VarBitReference var) {
                    emitAssign(arg(expression, i), varbit(var.var()));
                }

                if (targets.get(i) instanceof VarClientReference var) {
                    emitAssign(arg(expression, i), varclient(var.var()));
                }
            }
        }

        // enums
        if (expression.command == ENUM) {
            var inputtype = expression.arguments.get(0);
            var outputtype = expression.arguments.get(1);
            var key = expression.arguments.get(3);
            emitAssign(type(key, 0), Type.byID((int) inputtype.operand));
            emitEqual(type(expression, 0), Type.byID((int) outputtype.operand));
        }

        if (expression.command == ENUM_STRING) {
            var enum_ = expression.arguments.get(0);
            var key = expression.arguments.get(1);
            emitAssign(type(key, 0), Unpacker.getEnumInputType((int) enum_.operand));
        }

        if (expression.command == ENUM_HASOUTPUT) {
            var inputtype = expression.arguments.get(0);
            var enum_ = expression.arguments.get(1);
            var key = expression.arguments.get(2);
            emitAssign(type(key, 0), Type.byID((int) inputtype.operand));
        }

        if (expression.command == ENUM_GETREVERSECOUNT) {
            var outputtype = expression.arguments.get(0);
            var enum_ = expression.arguments.get(1);
            var value = expression.arguments.get(2);
            emitAssign(type(value, 0), Type.byID((int) outputtype.operand));
        }

        if (expression.command == ENUM_GETREVERSEINDEX) {
            var outputtype = expression.arguments.get(0);
            var inputtype = expression.arguments.get(1);
            var enum_ = expression.arguments.get(2);
            var value = expression.arguments.get(3);
            var index = expression.arguments.get(4);
            emitAssign(type(value, 0), Type.byID((int) outputtype.operand));
            emitEqual(type(expression, 0), Type.byID((int) inputtype.operand));
        }

        if (expression.command == ENUM_GETREVERSEINDEX_STRING) {
            var outputtype = expression.arguments.get(0);
            var enum_ = expression.arguments.get(1);
            var value = expression.arguments.get(2);
            var index = expression.arguments.get(3);
            emitEqual(type(expression, 0), Type.byID((int) outputtype.operand));
        }

        // params todo: can use a node to allow alias propagation through params
        if (expression.command == NC_PARAM || expression.command == LC_PARAM || expression.command == OC_PARAM || expression.command == SEQ_PARAM || expression.command == STRUCT_PARAM || expression.command == MEC_PARAM || expression.command == QUEST_PARAM) {
            var param = expression.arguments.get(1);
            emitEqual(type(expression, 0), Unpacker.getParamType((int) param.operand));
        }

        if (expression.command == CC_PARAM) {
            var param = expression.arguments.get(0);
            emitEqual(type(expression, 0), Unpacker.getParamType((int) param.operand));
        }

        if (expression.command == CC_SETPARAM) {
            var param = expression.arguments.get(0);
            var value = expression.arguments.get(1);
            emitEqual(type(value, 0), Unpacker.getParamType((int) param.operand));
        }

        if (expression.command == PLAYER_GROUP_MEMBER_GET_SAME_WORLD_VAR) {
            var kind = expression.arguments.get(1);
            var var = expression.arguments.get(2);

            if ((int) kind.operand == 1) {
                emitEqual(type(var, 0), Type.VAR_PLAYER);
                emitEqual(type(expression, 0), var(VarDomain.PLAYER, (int) var.operand));
            } else {
                emitEqual(type(var, 0), Type.VARBIT);
                emitEqual(type(expression, 0), varbit((int) var.operand));
            }
        }

        // dbtables todo: can use a node to allow alias propagation through dbtables
        if (expression.command == DB_FIND || expression.command == DB_FIND_WITH_COUNT || expression.command == DB_FIND_REFINE || expression.command == DB_FILTER_VALUE) {
            var column = (int) expression.arguments.get(0).operand;
            var value = expression.arguments.get(1);
            emitEqual(type(value, 0), Unpacker.getDBColumnTypeTupleAssertSingle(column));
        }

        // arrays
        if (expression.command == PUSH_ARRAY_INT) {
            emitIsArray(local(script, LocalDomain.ARRAY, (int) expression.operand), type(expression, 0));
        }

        if (expression.command == POP_ARRAY_INT) {
            emitArrayStore(arg(expression, 1), local(script, LocalDomain.ARRAY, (int) expression.operand));
        }

        if (expression.command == DEFINE_ARRAY) {
            var index = (int) expression.operand >> 16;
            var type = Type.byID((int) expression.operand & 0xffff);
            emitEqual(local(script, LocalDomain.ARRAY, index), type.array());
        }

        // equality
        if (expression.command == FLOW_EQ || expression.command == FLOW_NE) {
            emitCompare(arg(expression, 0), arg(expression, 1));
        }

        // visit children
        expression.visitChildren(c -> run(script, c));
    }

    public void finish(Set<Integer> scripts) {
        // propagate types
        propagateUntilStable();

        // merge parameters with locals
        for (var script : scripts) {
            var parameterCountInt = ScriptUnpacker.getParameterCount(script, LocalDomain.INTEGER);
            var parameterCountLong = ScriptUnpacker.getParameterCount(script, LocalDomain.LONG);
            var parameterCountObject = ScriptUnpacker.getParameterCount(script, LocalDomain.OBJECT);
            var parameterCount = parameterCountInt + parameterCountLong + parameterCountObject;

            var indexInt = 0;
            var indexLong = 0;
            var indexObject = 0;

            for (var i = 0; i < parameterCount; i++) {
                var parameter = parameter(script, i);

                if (indexLong == parameterCountLong && indexObject == parameterCountObject) { // only ints left, it's an int
                    if (ScriptUnpacker.SCRIPT_LEGACY_ARRAY_PARAMETER.getOrDefault(script, -1) == indexInt) {
                        emitEqual(parameter, local(script, LocalDomain.ARRAY, 0));
                        emitEqual(parameter, Type.UNKNOWNARRAY);
                    }

                    emitEqual(parameter, local(script, LocalDomain.INTEGER, indexInt++));
                    emitEqual(parameter, Type.UNKNOWN_INT);
                } else if (indexInt == parameterCountInt && indexObject == parameterCountObject) { // only longs left, it's a long
                    emitEqual(parameter, local(script, LocalDomain.LONG, indexLong++));
                    emitEqual(parameter, Type.UNKNOWN_LONG);
                } else if (indexInt == parameterCountInt && indexLong == parameterCountLong) { // only objects left, it's an object
                    emitEqual(parameter, local(script, LocalDomain.OBJECT, indexObject++));
                    emitEqual(parameter, Type.UNKNOWN_OBJECT);
                } else {
                    var type = typeof(parameter);

                    if (Type.LATTICE.test(type, Type.UNKNOWN_INT)) { // inferred it's an int
                        if (ScriptUnpacker.SCRIPT_LEGACY_ARRAY_PARAMETER.getOrDefault(script, -1) == indexInt) {
                            emitEqual(parameter, local(script, LocalDomain.ARRAY, 0));
                            emitEqual(parameter, Type.UNKNOWNARRAY);
                        }

                        emitEqual(parameter, local(script, LocalDomain.INTEGER, indexInt++));
                        emitEqual(parameter, Type.UNKNOWN_INT);
                    } else if (Type.LATTICE.test(type, Type.UNKNOWN_LONG)) { // inferred it's a long
                        emitEqual(parameter, local(script, LocalDomain.LONG, indexLong++));
                        emitEqual(parameter, Type.UNKNOWN_LONG);
                    } else if (Type.LATTICE.test(type, Type.UNKNOWN_OBJECT)) { // inferred it's an object
                        emitEqual(parameter, local(script, LocalDomain.OBJECT, indexObject++));
                        emitEqual(parameter, Type.UNKNOWN_OBJECT);
                    } else { // not enough info (script not called, guess the order as ints,longs,objects)
                        if (indexInt < parameterCountInt) {
                            if (ScriptUnpacker.SCRIPT_LEGACY_ARRAY_PARAMETER.getOrDefault(script, -1) == indexInt) {
                                emitEqual(parameter, local(script, LocalDomain.ARRAY, 0));
                                emitEqual(parameter, Type.UNKNOWNARRAY);
                            }

                            emitEqual(parameter, local(script, LocalDomain.INTEGER, indexInt++));
                            emitEqual(parameter, Type.UNKNOWN_INT);
                        } else if (indexLong < parameterCountLong) {
                            emitEqual(parameter, local(script, LocalDomain.LONG, indexLong++));
                            emitEqual(parameter, Type.UNKNOWN_LONG);
                        } else if (indexObject < parameterCountObject) {
                            emitEqual(parameter, local(script, LocalDomain.OBJECT, indexObject++));
                            emitEqual(parameter, Type.UNKNOWN_OBJECT);
                        }
                    }
                }
            }
        }

        // propagate again with newly generated constraints
        propagateUntilStable();

        // output script signatures
        for (var script : scripts) {
            ScriptUnpacker.SCRIPT_PARAMETERS.put(script, IntStream.range(0, ScriptUnpacker.getParameterCount(script)).mapToObj(i -> typeof(parameter(script, i))).toList());
            ScriptUnpacker.SCRIPT_RETURNS.put(script, IntStream.range(0, ScriptUnpacker.getReturnTypes(script).size()).mapToObj(i -> typeof(result(script, i))).toList());
        }

        // assume unused is clientscript
        if (ScriptUnpacker.ASSUME_UNUSED_IS_CLIENTSCRIPT) {
            for (var script : scripts) {
                if (!ScriptUnpacker.CALLED.contains(script) && ScriptUnpacker.SCRIPT_RETURNS.get(script).isEmpty()) {
                    ScriptUnpacker.SCRIPT_TRIGGERS.put(script, ScriptTrigger.CLIENTSCRIPT);
                }
            }
        }

        // output types
        for (var node : vars.keySet()) {
            if (node instanceof Node.ExpressionType(var expression, var index)) {
                expression.type.set(index, typeof(node));
            }

            if (node instanceof Node.LocalType(var script, var domain, var index)) {
                ScriptUnpacker.SCRIPT_LOCALS.computeIfAbsent(script, _ -> new HashMap<>()).put(new LocalReference(domain, index), typeof(node));
            }

            if (node instanceof Node.VarType(VarDomain domain, var id) && Unpack.VERSION < 742) {
                Unpacker.setVarType(domain, id, ScriptUnpacker.chooseDisplayType(typeof(node)));
            }
        }
    }

    private Type typeof(Node node) {
        return vars.getOrDefault(node, node instanceof Node.ConstantType(var t) ? t : Type.UNKNOWN);
    }

    private void emitAssign(Node a, Type b) {
        emitAssign(a, new Node.ConstantType(b));
    }

    private void emitAssign(Node a, Node b) {
        constraints.add(new Constraint(ConstraintKind.ASSIGN, a, b));
    }

    private void emitCompare(Node a, Node b) {
        constraints.add(new Constraint(ConstraintKind.COMPARE, a, b));
    }

    private void emitIsArray(Node a, Node b) {
        constraints.add(new Constraint(ConstraintKind.ISARRAY, a, b));
    }

    private void emitArrayStore(Node a, Node b) { // exists t, a < t and isarray(b, t)
        var t = new Node.TemporaryType();
        constraints.add(new Constraint(ConstraintKind.ASSIGN, a, t));
        constraints.add(new Constraint(ConstraintKind.ISARRAY, b, t));
    }

    private void emitEqual(Node a, Node b) {
        emitAssign(a, b);
        emitAssign(b, a);
    }

    private void emitEqual(Node a, Type b) {
        emitEqual(a, new Node.ConstantType(b));
    }

    // nicer syntax for nodes
    private Node parameter(int script, int index) {
        return new Node.ParameterType(script, index);
    }

    private Node serverParameter(int script, int index) {
        return new Node.ServerParameterType(script, index);
    }

    private Node result(int script, int index) {
        return new Node.ReturnType(script, index);
    }

    private Node local(int script, LocalDomain domain, int index) {
        return new Node.LocalType(script, domain, index);
    }

    private Node var(VarDomain domain, int index) {
        return new Node.VarType(domain, index);
    }

    private Node varbit(int index) {
        return new Node.VarPlayerBitType(index);
    }

    private Node varclient(int index) {
        return new Node.VarClientType(index);
    }

    private Node type(Expression expression, int index) {
        return new Node.ExpressionType(expression, index);
    }

    private Node arg(Expression expression, int index) {
        for (var argument : expression.arguments) {
            if (index < argument.type.size()) {
                return type(argument, index);
            }

            index -= argument.type.size();
        }

        throw new IllegalArgumentException();
    }

    private void propagateUntilStable() {
        // build var -> incident constraints lookup table
        var incident = new HashMap<Node, Set<Constraint>>();

        for (var constraint : constraints) {
            incident.computeIfAbsent(constraint.a(), _ -> new HashSet<>()).add(constraint);
            incident.computeIfAbsent(constraint.b(), _ -> new HashSet<>()).add(constraint);
        }

        // apply local consistency rules until everything converges to a global solution
        var remaining = new LinkedHashSet<>(constraints);

        while (!remaining.isEmpty()) {
            var constraint = remaining.removeFirst();
            var kind = constraint.kind();
            var a = constraint.a();
            var b = constraint.b();
            var prevA = typeof(a);
            var prevB = typeof(b);
            var typeA = prevA;
            var typeB = prevB;

            // process the constraint
            if (kind == ConstraintKind.ASSIGN || kind == ConstraintKind.COMPARE) {
                if (typeA == typeB) {
                    // nothing to do
                } else if (typeA == Type.NAMEDOBJ && Type.LATTICE.test(typeA, typeB)) {
                    typeB = Type.OBJ;
                } else if (typeB == Type.NAMEDOBJ && Type.LATTICE.test(typeB, typeA) && kind == ConstraintKind.COMPARE) {
                    typeA = Type.OBJ; // comparison requires one to subtype the other, so only propagate obj across it
                } else {
                    var meet = Type.LATTICE.meet(typeA, typeB);

                    if (ScriptUnpacker.ERROR_ON_TYPE_CONFLICT && meet == Type.CONFLICT) {
                        System.err.println("Types " + typeA + " and " + typeB + " conflict. Paste the following into graphviz to see the data flow graph:");
                        printConnectedComponent(constraint.a());
                        throw new IllegalStateException("type conflict");
                    }

                    typeA = meet;
                    typeB = meet;
                }
            } else if (kind == ConstraintKind.ISARRAY) {
                var elementTypeA = typeA.element();

                if (elementTypeA == null) {
                    elementTypeA = Type.UNKNOWN;
                }

                var meet = Type.LATTICE.meet(elementTypeA, typeB);

                if (ScriptUnpacker.ERROR_ON_TYPE_CONFLICT && meet == Type.CONFLICT) {
                    System.err.println("Types " + typeA + " and " + typeB + " conflict. Paste the following into graphviz to see the data flow graph:");
                    printConnectedComponent(constraint.a());
                    throw new IllegalStateException("type conflict");
                }

                typeA = Type.LATTICE.meet(typeA, meet.array());
                typeB = Type.LATTICE.meet(typeB, meet);
            }

            // update values and re-queue incident constraints that may need reprocessing
            if (prevA != typeA) {
                vars.put(a, typeA);
                remaining.addAll(incident.get(a));
            }

            if (prevB != typeB) {
                vars.put(b, typeB);
                remaining.addAll(incident.get(b));
            }
        }
    }

    private void printConnectedComponent(Node start) { // export to graphviz for debugging
        var incident = new HashMap<Node, Set<Constraint>>();

        for (var constraint : constraints) {
            incident.computeIfAbsent(constraint.a(), _ -> new HashSet<>()).add(constraint);
            incident.computeIfAbsent(constraint.b(), _ -> new HashSet<>()).add(constraint);
        }

        var componentNodes = new HashSet<Node>();
        var componentEdges = new HashSet<Constraint>();

        var queue = new ArrayDeque<Node>();
        componentNodes.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            var node = queue.removeFirst();
            var edges = incident.getOrDefault(node, Set.of());
            componentEdges.addAll(edges);

            for (var edge : edges) {
                if (componentNodes.add(edge.a())) queue.addLast(edge.a());
                if (componentNodes.add(edge.b())) queue.addLast(edge.b());
            }
        }

        var componentNodeIndices = new HashMap<Node, Integer>();
        var nextComponentNodeIndices = 0;

        System.out.println("digraph G {");

        for (var node : componentNodes) {
            var index = nextComponentNodeIndices++;
            componentNodeIndices.put(node, index);

            System.out.println("    " + index + " " + switch (node) {
                case Node.ConstantType(var t) -> "[shape=diamond,label=\"" + t + "\"]";
                case Node.ExpressionType(var e, var i) -> "[shape=box,label=\"" + e.toString().replace("\\", "\\\\").replace("\"", "\\\"") + " #" + i + "\\n" + typeof(node) + "\"]";
                case Node.LocalType(var s, var d, var i) -> "[shape=box,label=\"script_" + s + ".local" + d.name().toLowerCase() + i + "\\n" + typeof(node) + "\"]";
                case Node.ParameterType(var s, var i) -> "[shape=box,label=\"script_" + s + ".param" + i + "\\n" + typeof(node) + "\"]";
                case Node.ServerParameterType(var s, var i) -> "[shape=box,label=\"serverscript_" + s + ".param" + i + "\\n" + typeof(node) + "\"]";
                case Node.ReturnType(var s, var i) -> "[shape=box,label=\"script_" + s + ".result" + i + "\\n" + typeof(node) + "\"]";
                case Node.VarType(VarDomain domain, var i) -> "[shape=box,label=\"var" + domain.name().toLowerCase() + "_" + i + "\\n" + typeof(node) + "\"]";
                case Node.VarPlayerBitType(var i) -> "[shape=box,label=\"varplayerbit_" + i + "\\n" + typeof(node) + "\"]";
                case Node.VarClientType(var i) -> "[shape=box,label=\"varclient_" + i + "\\n" + typeof(node) + "\"]";
                case Node.TemporaryType() -> "[shape=box,color=gray,fontcolor=gray,label=\"" + typeof(node) + "\"]";
            });
        }

        for (var edge : componentEdges) {
            var ia = componentNodeIndices.get(edge.a());
            var ib = componentNodeIndices.get(edge.b());

            switch (edge.kind()) {
                case ASSIGN -> System.out.println("    " + ia + " -> " + ib);
                case COMPARE -> System.out.println("    " + ia + " -> " + ib + " [dir=none,style=dashed]");
                case ISARRAY -> System.out.println("    " + ia + " -> " + ib + " [style=bold,color=blue,style=dashed]");
            }
        }

        System.out.println("}");
    }

    private sealed interface Node {
        record ConstantType(Type type) implements Node {
            public boolean equals(Object that) {
                return this == that;
            }

            public int hashCode() {
                return System.identityHashCode(this);
            }
        }

        record ExpressionType(Expression expression, int index) implements Node {}
        record LocalType(int script, Command.LocalDomain domain, int index) implements Node {}
        record ParameterType(int script, int index) implements Node {}
        record ServerParameterType(int script, int index) implements Node {}
        record ReturnType(int script, int index) implements Node {}
        record VarType(VarDomain domain, int id) implements Node {}
        record VarPlayerBitType(int id) implements Node {}
        record VarClientType(int id) implements Node {}

        record TemporaryType() implements Node {
            public boolean equals(Object that) {
                return this == that;
            }

            public int hashCode() {
                return System.identityHashCode(this);
            }
        }
    }

    private record Constraint(ConstraintKind kind, Node a, Node b) {}

    enum ConstraintKind {
        ASSIGN, // a < b
        COMPARE, // (a < b) or (b < a)
        ISARRAY, // a == array(b)
    }
}
