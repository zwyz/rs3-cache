package rs3.unpack.script;

import rs3.unpack.ScriptTrigger;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;

import java.util.*;

import static rs3.unpack.script.Command.*;

public class ScriptUnpacker {
    public static final boolean DISASSEMBLE_ONLY = false;
    public static final boolean KEEP_LABELS = false;
    public static final boolean ASSUME_UNKNOWN_TYPES_ARE_BASE = true;
    public static final boolean OUTPUT_TYPE_ALIASES = false;
    public static final boolean ASSUME_UNUSED_IS_CLIENTSCRIPT = true;
    public static final boolean FORMAT_HOOKS = true;
    public static final boolean CHECK_NONEMPTY_STACK = true;
    public static final boolean CHECK_EMPTY_ARGUMENT = true;
    public static final boolean CHECK_PARTIALLY_USED_ARGUMENT = true;
    public static final boolean ERROR_ON_TYPE_CONFLICT = true;
    public static final Map<Integer, CompiledScript> SCRIPTS = new HashMap<>();
    public static final Map<Integer, List<Expression>> SCRIPTS_DECOMPILED = new HashMap<>();
    public static final Map<Integer, Integer> SCRIPT_PARAMETER_COUNT = new HashMap<>();
    public static final Map<Integer, List<Type>> SCRIPT_RETURN_TYPES = new HashMap<>();
    public static final Map<Integer, List<Type>> SCRIPT_PARAMETERS = new HashMap<>();
    public static final Map<Integer, List<Type>> SCRIPT_RETURNS = new HashMap<>();
    public static final Map<Integer, Integer> SCRIPT_LEGACY_ARRAY_PARAMETER = new HashMap<>();
    public static final Map<Integer, Map<LocalReference, Type>> SCRIPT_LOCALS = new HashMap<>();
    public static final Set<Integer> CALLED = new LinkedHashSet<>();
    public static final Map<Integer, ScriptTrigger> SCRIPT_TRIGGERS = new LinkedHashMap<>();

    public static void reset() {
        SCRIPTS.clear();
        SCRIPTS_DECOMPILED.clear();
        SCRIPT_PARAMETER_COUNT.clear();
        SCRIPT_RETURN_TYPES.clear();
        SCRIPT_PARAMETERS.clear();
        SCRIPT_RETURNS.clear();
        CALLED.clear();
        SCRIPT_TRIGGERS.clear();
    }

    public static void load(int id, byte[] data) {
        var script = CompiledScript.decode(data);
        SCRIPTS.put(id, script);
    }

    public static int getParameterCount(int script) {
        return SCRIPT_PARAMETER_COUNT.get(script);
    }

    public static int getParameterCount(int script, LocalDomain domain) {
        return switch (domain) {
            case INTEGER -> SCRIPTS.get(script).argumentCountInt;
            case LONG -> SCRIPTS.get(script).argumentCountLong;
            case OBJECT -> SCRIPTS.get(script).argumentCountObject;
            case ARRAY -> 0;
        };
    }

    public static List<Type> getReturnTypes(int script) {
        return SCRIPT_RETURN_TYPES.get(script);
    }

    public static void decompile() {
        if (DISASSEMBLE_ONLY) {
            return;
        }

        // compute parameter/return counts
        for (var id : SCRIPTS.keySet()) {
            var script = SCRIPTS.get(id);
            SCRIPT_PARAMETER_COUNT.put(id, script.argumentCountInt + script.argumentCountObject + script.argumentCountLong);
            var returnTypes = new ArrayList<Type>();

            for (var i = script.code.length - 2; i >= 0; i--) {
                var command = script.code[i].command();
                var operand = script.code[i].operand();

                if (command != PUSH_CONSTANT_INT && command != PUSH_CONSTANT_STRING) {
                    break;
                }

                if (Objects.equals(operand, 0)) {
                    returnTypes.addFirst(Type.INT);
                } else if (Objects.equals(operand, -1)) {
                    returnTypes.addFirst(Type.UNKNOWN_INT_NOTINT);
                } else if (Objects.equals(operand, "")) {
                    returnTypes.addFirst(Type.UNKNOWN_OBJECT); // todo
                } else if (Objects.equals(operand, -1L)) {
                    returnTypes.addFirst(Type.UNKNOWN_LONG); // todo
                } else {
                    throw new IllegalStateException("invalid default return: " + operand);
                }
            }

            SCRIPT_RETURN_TYPES.put(id, returnTypes);
        }

        // decompile
        for (var id : SCRIPTS.keySet()) {
            var script = SCRIPTS.get(id);
            var decompiled = new SyntaxBuilder(id).build(script.code);
            SCRIPTS_DECOMPILED.put(id, decompiled);
        }

        // propagate types
        new LegacyArrayParameterInference().run(SCRIPTS_DECOMPILED.keySet());
        var propagator = new TypePropagator();

        for (var id : SCRIPTS_DECOMPILED.keySet()) {
            var script = SCRIPTS_DECOMPILED.get(id);
            propagator.run(id, script);
        }

        propagator.finish(SCRIPTS_DECOMPILED.keySet());
    }

    public static List<String> unpack(int id) {
        var script = SCRIPTS_DECOMPILED.get(id);

        if (script == null) {
            return List.of();
        }

        return CodeFormatter.formatScript(Unpacker.getScriptName(id), SCRIPT_PARAMETERS.get(id), SCRIPT_RETURNS.get(id), SCRIPT_LOCALS.get(id), script).lines().toList();
    }

    public static Type chooseDisplayType(Type type) {
        if (ASSUME_UNKNOWN_TYPES_ARE_BASE) {
            if (type == Type.UNKNOWN_INT) return Type.INT_INT; // todo: could assume boolean
            if (type == Type.UNKNOWN_INT_NOTINT) return Type.BOOLEAN;
            if (type == Type.UNKNOWN_INT_NOTBOOLEAN) return Type.INT_INT;
            if (type == Type.UNKNOWN_INT_NOTINT_NOTBOOLEAN) return Type.INT_INT; // todo: can format this specially
            if (type == Type.UNKNOWN_LONG) return Type.LONG;
            if (type == Type.UNKNOWN_OBJECT) return Type.STRING;
            if (type == Type.UNKNOWN_INTARRAY) return Type.INTARRAY;
            if (type == Type.UNKNOWN_INT_NOTINTARRAY) return Type.INTARRAY;
            if (type == Type.UNKNOWN_INT_NOTBOOLEANARRAY) return Type.INTARRAY;
            if (type == Type.UNKNOWN_INT_NOTINT_NOTBOOLEANARRAY) return Type.INTARRAY;
            if (type == Type.UNKNOWNARRAY) return Type.INTARRAY;
        }

        return type;
    }
}
