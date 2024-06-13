package rs3.unpack.script;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.VarDomain;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class Command {
    private static final Map<Integer, Command> BY_ID = new HashMap<>();
    private static final Map<String, Command> BY_NAME = new HashMap<>();
    public final String name;
    public final List<Type> arguments;
    public final List<Type> returns;

    Command(String name, List<Type> arguments, List<Type> returns) {
        this.name = name;
        this.arguments = arguments;
        this.returns = returns;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Command byId(int id) {
        return Objects.requireNonNull(BY_ID.get(id));
    }

    private static Command defineCommand(String name) {
        var command = new Command(name, null, null);
        BY_NAME.put(name, command);
        return command;
    }

    private static Command defineCommand(String name, int id, List<Type> arguments, List<Type> returns) {
        var command = new Command(name, arguments, returns);
        BY_NAME.put(name, command);
        BY_ID.put(id, command);
        return command;
    }

    private static Command findCommand(String name) {
        return BY_NAME.get(name);
    }

    // define decompiler commands
    public static final Command LABEL = defineCommand("label");
    public static final Command BRANCHIF = defineCommand("branchif");
    public static final Command FLOW_ASSIGN = defineCommand("flow_assign"); // ..., ..., ... = ..., ..., ...
    public static final Command FLOW_LOAD = defineCommand("flow_load"); // ..., ..., ... = ..., ..., ...
    public static final Command FLOW_IF = defineCommand("flow_if"); // if (...) ...
    public static final Command FLOW_IFELSE = defineCommand("flow_ifelse"); // if (...) ... else ...
    public static final Command FLOW_WHILE = defineCommand("flow_while"); // while (...) ...
    public static final Command FLOW_SWITCH = defineCommand("flow_switch"); // switch (...) ...
    public static final Command FLOW_AND = defineCommand("flow_and"); // ... & ...
    public static final Command FLOW_OR = defineCommand("flow_or"); // ... | ...
    public static final Command FLOW_NE = defineCommand("flow_ne"); // ... != ...
    public static final Command FLOW_EQ = defineCommand("flow_eq"); // ... == ...
    public static final Command FLOW_LT = defineCommand("flow_lt"); // ... < ...
    public static final Command FLOW_GT = defineCommand("flow_gt"); // ... > ...
    public static final Command FLOW_LE = defineCommand("flow_le"); // ... <= ...
    public static final Command FLOW_GE = defineCommand("flow_ge"); // ... >= ...
    public static final Command FLOW_PREINC = defineCommand("flow_preinc"); // ++$x
    public static final Command FLOW_PREDEC = defineCommand("flow_predec"); // --$x
    public static final Command FLOW_POSTINC = defineCommand("flow_postinc"); // $x++
    public static final Command FLOW_POSTDEC = defineCommand("flow_postdec"); // $x--

    // load normal commands
    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\[command,(?<name>[a-zA-Z0-9_]+)](?:\\((?<arguments>[a-zA-Z0-9_]+\\s+\\$[a-zA-Z0-9_]+(?:\\s*,\\s*[a-zA-Z0-9_]+\\s+\\$[a-zA-Z0-9_]+)*)?\\))?(?:\\((?<returns>[a-zA-Z0-9_]+(?:\\s*, ?\\s*[a-zA-Z0-9_]+)*)?\\))?(?: (?<version>[0-9]+))?");

    static {
        try {
            var opcodes = new HashMap<String, Integer>();

            for (var line : Files.readAllLines(Path.of("data/opcodes-" + (Unpack.VERSION < 669 ? "unscrambled" : Unpack.VERSION) + ".txt"))) {
                var parts = line.split(",");

                if (parts.length >= 3) {
                    if (Integer.parseInt(parts[2]) > Unpack.VERSION) {
                        continue; // override for higher version
                    }
                }

                opcodes.put(parts[0], Integer.parseInt(parts[1]));
            }

            for (var file : Files.list(Path.of("data/commands")).toList()) {
                for (var line : Files.readAllLines(file)) {
                    if (line.isBlank() || line.startsWith("//")) {
                        continue;
                    }

                    var matcher = COMMAND_PATTERN.matcher(line);
                    var missingTypes = false;

                    if (!matcher.matches()) {
                        matcher = COMMAND_PATTERN.matcher(line.substring(0, line.lastIndexOf(']') + 1));
                        missingTypes = true;

                        if (!matcher.matches()) {
                            throw new IllegalStateException("invalid line " + line);
                        }
                    }

                    if (matcher.group("version") != null) {
                        var version = Integer.parseInt(matcher.group("version"));

                        if (version > Unpack.VERSION) {
                            continue; // overrides for higher versions
                        }
                    }

                    var name = matcher.group("name");
                    var id = opcodes.get(name);

                    if (id == null) {
                        System.err.println("no opcode for " + name); // todo: add versions to commands
                        continue;
                    }

                    if (missingTypes) {
                        defineCommand(name, id, null, null);
                    } else {
                        var arguments = matcher.group("arguments") == null ? List.<Type>of() : Arrays.stream(matcher.group("arguments").split(",")).map(s -> parseType(s.trim().split(" ")[0])).toList();
                        var returns = matcher.group("returns") == null ? List.<Type>of() : Arrays.stream(matcher.group("returns").split(",")).map(s -> parseType(s.trim())).toList();
                        defineCommand(name, id, arguments, returns);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Type parseType(String name) {
        if (name.equals("anyint")) {
            return Type.INT;
        }

        for (var type : Type.values()) {
            if (type == Type.INT) continue; // subdivided, int refers to int_int

            if (Objects.equals(type.name, name)) {
                return type;
            }
        }

        throw new IllegalStateException("invalid type: " + name);
    }

    // core commands
    public static final Command PUSH_CONSTANT_INT = findCommand("push_constant_int");
    public static final Command PUSH_VARC_INT = findCommand("push_varc_int");
    public static final Command POP_VARC_INT = findCommand("pop_varc_int");
    public static final Command PUSH_VARC_STRING = findCommand("push_varc_string");
    public static final Command POP_VARC_STRING = findCommand("pop_varc_string");
    public static final Command PUSH_VAR = findCommand("push_var");
    public static final Command POP_VAR = findCommand("pop_var");
    public static final Command PUSH_CONSTANT_STRING = findCommand("push_constant_string");
    public static final Command BRANCH = findCommand("branch");
    public static final Command BRANCH_NOT = findCommand("branch_not");
    public static final Command BRANCH_EQUALS = findCommand("branch_equals");
    public static final Command BRANCH_LESS_THAN = findCommand("branch_less_than");
    public static final Command BRANCH_GREATER_THAN = findCommand("branch_greater_than");
    public static final Command BRANCH_LESS_THAN_OR_EQUALS = findCommand("branch_less_than_or_equals");
    public static final Command BRANCH_GREATER_THAN_OR_EQUALS = findCommand("branch_greater_than_or_equals");
    public static final Command RETURN = findCommand("return");
    public static final Command PUSH_VARBIT = findCommand("push_varbit");
    public static final Command POP_VARBIT = findCommand("pop_varbit");
    public static final Command PUSH_INT_LOCAL = findCommand("push_int_local");
    public static final Command POP_INT_LOCAL = findCommand("pop_int_local");
    public static final Command PUSH_STRING_LOCAL = findCommand("push_string_local");
    public static final Command POP_STRING_LOCAL = findCommand("pop_string_local");
    public static final Command JOIN_STRING = findCommand("join_string");
    public static final Command POP_INT_DISCARD = findCommand("pop_int_discard");
    public static final Command POP_STRING_DISCARD = findCommand("pop_string_discard");
    public static final Command GOSUB_WITH_PARAMS = findCommand("gosub_with_params");
    public static final Command DEFINE_ARRAY = findCommand("define_array");
    public static final Command PUSH_ARRAY_INT = findCommand("push_array_int");
    public static final Command POP_ARRAY_INT = findCommand("pop_array_int");
    public static final Command SWITCH = findCommand("switch");
    public static final Command PUSH_LONG_CONSTANT = findCommand("push_long_constant");
    public static final Command POP_LONG_DISCARD = findCommand("pop_long_discard");
    public static final Command PUSH_LONG_LOCAL = findCommand("push_long_local");
    public static final Command POP_LONG_LOCAL = findCommand("pop_long_local");
    public static final Command LONG_BRANCH_NOT = findCommand("long_branch_not");
    public static final Command LONG_BRANCH_EQUALS = findCommand("long_branch_equals");
    public static final Command LONG_BRANCH_LESS_THAN = findCommand("long_branch_less_than");
    public static final Command LONG_BRANCH_GREATER_THAN = findCommand("long_branch_greater_than");
    public static final Command LONG_BRANCH_LESS_THAN_OR_EQUALS = findCommand("long_branch_less_than_or_equals");
    public static final Command LONG_BRANCH_GREATER_THAN_OR_EQUALS = findCommand("long_branch_greater_than_or_equals");
    public static final Command PUSH_ARRAY_INT_LEAVE_INDEX_ON_STACK = findCommand("push_array_int_leave_index_on_stack");
    public static final Command PUSH_ARRAY_INT_AND_INDEX = findCommand("push_array_int_and_index");
    public static final Command POP_ARRAY_INT_LEAVE_VALUE_ON_STACK = findCommand("pop_array_int_leave_value_on_stack");
    public static final Command BRANCH_IF_TRUE = findCommand("branch_if_true");
    public static final Command BRANCH_IF_FALSE = findCommand("branch_if_false");
    public static final Command PUSH_VARCLAN = findCommand("push_varclan");
    public static final Command PUSH_VARCLANBIT = findCommand("push_varclanbit");
    public static final Command PUSH_VARCLAN_LONG = findCommand("push_varclan_long");
    public static final Command PUSH_VARCLAN_STRING = findCommand("push_varclan_string");
    public static final Command PUSH_VARCLANSETTING = findCommand("push_varclansetting");
    public static final Command PUSH_VARCLANSETTINGBIT = findCommand("push_varclansettingbit");
    public static final Command PUSH_VARCLANSETTING_LONG = findCommand("push_varclansetting_long");
    public static final Command PUSH_VARCLANSETTING_STRING = findCommand("push_varclansetting_string");
    public static final Command OPCOUNT = findCommand("opcount");
    public static final Command VAR_REFERENCE_GET = findCommand("var_reference_get");

    // commands with special behavior
    public static final Command ADD = findCommand("add");
    public static final Command SUB = findCommand("sub");

    public static final Command ENUM = findCommand("enum");
    public static final Command ENUM_STRING = findCommand("enum_string");
    public static final Command ENUM_HASOUTPUT = findCommand("enum_hasoutput");
    public static final Command ENUM_GETREVERSECOUNT = findCommand("enum_getreversecount");
    public static final Command ENUM_GETREVERSEINDEX = findCommand("enum_getreverseindex");
    public static final Command ENUM_GETREVERSEINDEX_STRING = findCommand("enum_getreverseindex_string");

    public static final Command LC_PARAM = findCommand("lc_param");
    public static final Command NC_PARAM = findCommand("nc_param");
    public static final Command OC_PARAM = findCommand("oc_param");
    public static final Command SEQ_PARAM = findCommand("seq_param");
    public static final Command STRUCT_PARAM = findCommand("struct_param");
    public static final Command MEC_PARAM = findCommand("mec_param");
    public static final Command QUEST_PARAM = findCommand("quest_param");
    public static final Command CC_PARAM = findCommand("cc_param");
    public static final Command CC_SETPARAM = findCommand("cc_setparam");
    public static final Command PLAYER_GROUP_MEMBER_GET_SAME_WORLD_VAR = findCommand("player_group_member_get_same_world_var");
    public static final Command DB_FIND = findCommand("db_find");
    public static final Command DB_FIND_WITH_COUNT = findCommand("db_find_with_count");
    public static final Command DB_FIND_REFINE = findCommand("db_find_refine");
    public static final Command DB_GETFIELD = findCommand("db_getfield");
    public static final Command RUNJAVASCRIPT = findCommand("runjavascript");

    public boolean hasHook() {
        return arguments != null && arguments.contains(Type.HOOK);
    }

    public record Instruction(Command command, Object operand) {
        @Override
        public String toString() {
            return command + " " + operand;
        }
    }

    // switch
    public record SwitchCase(int value, int target) {

    }

    // push_var, pop_var
    public record VarReference(VarDomain domain, int var, boolean secondary) {

    }

    // push_varbit, pop_varbit
    public record VarBitReference(int var, boolean secondary) {

    }

    // push_varc_int, pop_varc_int
    public record VarClientReference(int var) {

    }

    // push_varc_string, pop_varc_string
    public record VarClientStringReference(int var) {

    }

    // branchif
    public record BranchIfTarget(int a, int b) {

    }

    // flow_ifelse
    public record IfElseBranches(List<Expression> trueBranch, List<Expression> falseBranche) {

    }

    // flow_switch
    public record SwitchBranch(List<Integer> values, List<Expression> branch) {

    }

    // flow_assign
    public record LocalReference(LocalDomain domain, int local) {

    }

    public enum LocalDomain {
        INTEGER, LONG, OBJECT
    }
}
