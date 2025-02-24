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

    // custom decompiler commands
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

    // core commands
    public static Command PUSH_CONSTANT_INT;
    public static Command PUSH_VARC_INT;
    public static Command POP_VARC_INT;
    public static Command PUSH_VARC_STRING;
    public static Command POP_VARC_STRING;
    public static Command PUSH_VAR;
    public static Command POP_VAR;
    public static Command PUSH_CONSTANT_STRING;
    public static Command BRANCH;
    public static Command BRANCH_NOT;
    public static Command BRANCH_EQUALS;
    public static Command BRANCH_LESS_THAN;
    public static Command BRANCH_GREATER_THAN;
    public static Command BRANCH_LESS_THAN_OR_EQUALS;
    public static Command BRANCH_GREATER_THAN_OR_EQUALS;
    public static Command RETURN;
    public static Command PUSH_VARBIT;
    public static Command POP_VARBIT;
    public static Command PUSH_INT_LOCAL;
    public static Command POP_INT_LOCAL;
    public static Command PUSH_STRING_LOCAL;
    public static Command POP_STRING_LOCAL;
    public static Command JOIN_STRING;
    public static Command POP_INT_DISCARD;
    public static Command POP_STRING_DISCARD;
    public static Command GOSUB_WITH_PARAMS;
    public static Command DEFINE_ARRAY;
    public static Command PUSH_ARRAY_INT;
    public static Command POP_ARRAY_INT;
    public static Command SWITCH;
    public static Command PUSH_LONG_CONSTANT;
    public static Command POP_LONG_DISCARD;
    public static Command PUSH_LONG_LOCAL;
    public static Command POP_LONG_LOCAL;
    public static Command LONG_BRANCH_NOT;
    public static Command LONG_BRANCH_EQUALS;
    public static Command LONG_BRANCH_LESS_THAN;
    public static Command LONG_BRANCH_GREATER_THAN;
    public static Command LONG_BRANCH_LESS_THAN_OR_EQUALS;
    public static Command LONG_BRANCH_GREATER_THAN_OR_EQUALS;
    public static Command PUSH_ARRAY_INT_LEAVE_INDEX_ON_STACK;
    public static Command PUSH_ARRAY_INT_AND_INDEX;
    public static Command POP_ARRAY_INT_LEAVE_VALUE_ON_STACK;
    public static Command BRANCH_IF_TRUE;
    public static Command BRANCH_IF_FALSE;
    public static Command PUSH_VARCLAN;
    public static Command PUSH_VARCLANBIT;
    public static Command PUSH_VARCLAN_LONG;
    public static Command PUSH_VARCLAN_STRING;
    public static Command PUSH_VARCLANSETTING;
    public static Command PUSH_VARCLANSETTINGBIT;
    public static Command PUSH_VARCLANSETTING_LONG;
    public static Command PUSH_VARCLANSETTING_STRING;
    public static Command OPCOUNT;
    public static Command VAR_REFERENCE_GET;

    // commands with special behavior
    public static Command ADD;
    public static Command SUB;

    public static Command ENUM;
    public static Command ENUM_STRING;
    public static Command ENUM_HASOUTPUT;
    public static Command ENUM_GETREVERSECOUNT;
    public static Command ENUM_GETREVERSEINDEX;
    public static Command ENUM_GETREVERSEINDEX_STRING;

    public static Command LC_PARAM;
    public static Command NC_PARAM;
    public static Command OC_PARAM;
    public static Command SEQ_PARAM;
    public static Command STRUCT_PARAM;
    public static Command MEC_PARAM;
    public static Command QUEST_PARAM;
    public static Command CC_PARAM;
    public static Command CC_SETPARAM;
    public static Command PLAYER_GROUP_MEMBER_GET_SAME_WORLD_VAR;
    public static Command DB_FIND;
    public static Command DB_FIND_WITH_COUNT;
    public static Command DB_FIND_REFINE;
    public static Command DB_GETFIELD;
    public static Command RUNJAVASCRIPT;

    // load commands
    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\[command,(?<name>[a-zA-Z0-9_]+)](?:\\((?<arguments>[a-zA-Z0-9_]+\\s+\\$[a-zA-Z0-9_]+(?:\\s*,\\s*[a-zA-Z0-9_]+\\s+\\$[a-zA-Z0-9_]+)*)?\\))?(?:\\((?<returns>[a-zA-Z0-9_]+(?:\\s*, ?\\s*[a-zA-Z0-9_]+)*)?\\))?(?: (?<version>[0-9]+))?");

    public static void reset() {
        BY_ID.clear();
        BY_NAME.clear();

        try {
            var opcodes = new HashMap<String, Integer>();

            for (var line : Files.readAllLines(Path.of("data/opcodes-" + (Unpack.VERSION < 685 ? "unscrambled" : Unpack.VERSION) + ".txt"))) {
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
//                        System.err.println("no opcode for " + name); // todo: add versions to commands
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

        // core commands
        PUSH_CONSTANT_INT = findCommand("push_constant_int");
        PUSH_VARC_INT = findCommand("push_varc_int");
        POP_VARC_INT = findCommand("pop_varc_int");
        PUSH_VARC_STRING = findCommand("push_varc_string");
        POP_VARC_STRING = findCommand("pop_varc_string");
        PUSH_VAR = findCommand("push_var");
        POP_VAR = findCommand("pop_var");
        PUSH_CONSTANT_STRING = findCommand("push_constant_string");
        BRANCH = findCommand("branch");
        BRANCH_NOT = findCommand("branch_not");
        BRANCH_EQUALS = findCommand("branch_equals");
        BRANCH_LESS_THAN = findCommand("branch_less_than");
        BRANCH_GREATER_THAN = findCommand("branch_greater_than");
        BRANCH_LESS_THAN_OR_EQUALS = findCommand("branch_less_than_or_equals");
        BRANCH_GREATER_THAN_OR_EQUALS = findCommand("branch_greater_than_or_equals");
        RETURN = findCommand("return");
        PUSH_VARBIT = findCommand("push_varbit");
        POP_VARBIT = findCommand("pop_varbit");
        PUSH_INT_LOCAL = findCommand("push_int_local");
        POP_INT_LOCAL = findCommand("pop_int_local");
        PUSH_STRING_LOCAL = findCommand("push_string_local");
        POP_STRING_LOCAL = findCommand("pop_string_local");
        JOIN_STRING = findCommand("join_string");
        POP_INT_DISCARD = findCommand("pop_int_discard");
        POP_STRING_DISCARD = findCommand("pop_string_discard");
        GOSUB_WITH_PARAMS = findCommand("gosub_with_params");
        DEFINE_ARRAY = findCommand("define_array");
        PUSH_ARRAY_INT = findCommand("push_array_int");
        POP_ARRAY_INT = findCommand("pop_array_int");
        SWITCH = findCommand("switch");
        PUSH_LONG_CONSTANT = findCommand("push_long_constant");
        POP_LONG_DISCARD = findCommand("pop_long_discard");
        PUSH_LONG_LOCAL = findCommand("push_long_local");
        POP_LONG_LOCAL = findCommand("pop_long_local");
        LONG_BRANCH_NOT = findCommand("long_branch_not");
        LONG_BRANCH_EQUALS = findCommand("long_branch_equals");
        LONG_BRANCH_LESS_THAN = findCommand("long_branch_less_than");
        LONG_BRANCH_GREATER_THAN = findCommand("long_branch_greater_than");
        LONG_BRANCH_LESS_THAN_OR_EQUALS = findCommand("long_branch_less_than_or_equals");
        LONG_BRANCH_GREATER_THAN_OR_EQUALS = findCommand("long_branch_greater_than_or_equals");
        PUSH_ARRAY_INT_LEAVE_INDEX_ON_STACK = findCommand("push_array_int_leave_index_on_stack");
        PUSH_ARRAY_INT_AND_INDEX = findCommand("push_array_int_and_index");
        POP_ARRAY_INT_LEAVE_VALUE_ON_STACK = findCommand("pop_array_int_leave_value_on_stack");
        BRANCH_IF_TRUE = findCommand("branch_if_true");
        BRANCH_IF_FALSE = findCommand("branch_if_false");
        PUSH_VARCLAN = findCommand("push_varclan");
        PUSH_VARCLANBIT = findCommand("push_varclanbit");
        PUSH_VARCLAN_LONG = findCommand("push_varclan_long");
        PUSH_VARCLAN_STRING = findCommand("push_varclan_string");
        PUSH_VARCLANSETTING = findCommand("push_varclansetting");
        PUSH_VARCLANSETTINGBIT = findCommand("push_varclansettingbit");
        PUSH_VARCLANSETTING_LONG = findCommand("push_varclansetting_long");
        PUSH_VARCLANSETTING_STRING = findCommand("push_varclansetting_string");
        OPCOUNT = findCommand("opcount");
        VAR_REFERENCE_GET = findCommand("var_reference_get");

        // commands with special behavior
        ADD = findCommand("add");
        SUB = findCommand("sub");

        ENUM = findCommand("enum");
        ENUM_STRING = findCommand("enum_string");
        ENUM_HASOUTPUT = findCommand("enum_hasoutput");
        ENUM_GETREVERSECOUNT = findCommand("enum_getreversecount");
        ENUM_GETREVERSEINDEX = findCommand("enum_getreverseindex");
        ENUM_GETREVERSEINDEX_STRING = findCommand("enum_getreverseindex_string");

        LC_PARAM = findCommand("lc_param");
        NC_PARAM = findCommand("nc_param");
        OC_PARAM = findCommand("oc_param");
        SEQ_PARAM = findCommand("seq_param");
        STRUCT_PARAM = findCommand("struct_param");
        MEC_PARAM = findCommand("mec_param");
        QUEST_PARAM = findCommand("quest_param");
        CC_PARAM = findCommand("cc_param");
        CC_SETPARAM = findCommand("cc_setparam");
        PLAYER_GROUP_MEMBER_GET_SAME_WORLD_VAR = findCommand("player_group_member_get_same_world_var");
        DB_FIND = findCommand("db_find");
        DB_FIND_WITH_COUNT = findCommand("db_find_with_count");
        DB_FIND_REFINE = findCommand("db_find_refine");
        DB_GETFIELD = findCommand("db_getfield");
        RUNJAVASCRIPT = findCommand("runjavascript");
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
