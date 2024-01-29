package rs3.unpack.script;

import rs3.unpack.VarDomain;
import rs3.util.Packet;

import java.util.ArrayList;

import static rs3.unpack.script.Command.*;

public class CompiledScript {
    public String name;
    public int localCountInt;
    public int localCountObject;
    public int localCountLong;
    public int argumentCountInt;
    public int argumentCountObject;
    public int argumentCountLong;
    public Instruction[] code;

    public static CompiledScript decode(byte[] data) {
        var packet = new Packet(data);

        packet.pos = packet.arr.length - 2;
        var headerSize = packet.g2();
        var headerPos = packet.arr.length - 2 - headerSize - 16;
        packet.pos = headerPos;
        var script = new CompiledScript();

        script.code = new Instruction[packet.g4s()];

        script.localCountInt = packet.g2();
        script.localCountObject = packet.g2();
        script.localCountLong = packet.g2();

        script.argumentCountInt = packet.g2();
        script.argumentCountObject = packet.g2();
        script.argumentCountLong = packet.g2();

        var switchCount = packet.g1();
        var switchValue = new int[switchCount][];
        var switchOffset = new int[switchCount][];

        for (var i = 0; i < switchCount; i++) {
            var caseCount = packet.g2();
            switchValue[i] = new int[caseCount];
            switchOffset[i] = new int[caseCount];

            for (var j = 0; j < caseCount; j++) {
                switchValue[i][j] = packet.g4s();
                switchOffset[i][j] = packet.g4s();
            }
        }

        packet.pos = 0;
        script.name = packet.gjstrnull();
        var index = 0;

        while (packet.pos < headerPos) {
            var command = byId(packet.g2());
            script.code[index++] = new Instruction(command, decodeOperand(command, packet, index, switchValue, switchOffset));
        }

        return script;
    }

    private static Object decodeOperand(Command command, Packet packet, int index, int[][] switchValue, int[][] switchOffset) {
        if (command == PUSH_CONSTANT_INT) {
            return packet.g4s(); // int
        } else if (command == PUSH_LONG_CONSTANT) {
            return packet.g4s(); // long
        } else if (command == PUSH_CONSTANT_STRING) {
            return switch (packet.g1()) {
                case 0 -> packet.g4s(); // int
                case 1 -> packet.g8s(); // long
                case 2 -> packet.gjstr(); // string
                default -> throw new IllegalStateException("unsupported base type");
            };
        } else if (command == PUSH_INT_LOCAL || command == POP_INT_LOCAL || command == PUSH_STRING_LOCAL || command == POP_STRING_LOCAL || command == PUSH_LONG_LOCAL || command == POP_LONG_LOCAL) {
            return packet.g4s(); // local
        } else if (command == PUSH_VAR || command == POP_VAR) {
            return new VarReference(VarDomain.byID(packet.g1()), packet.g2(), packet.g1() == 1); // var
        } else if (command == PUSH_VARBIT || command == POP_VARBIT) {
            return new VarBitReference(packet.g2(), packet.g1() == 1); // varbit
        } else if (command == BRANCH || command == BRANCH_NOT || command == BRANCH_EQUALS || command == BRANCH_LESS_THAN || command == BRANCH_GREATER_THAN || command == BRANCH_LESS_THAN_OR_EQUALS || command == BRANCH_GREATER_THAN_OR_EQUALS || command == LONG_BRANCH_NOT || command == LONG_BRANCH_EQUALS || command == LONG_BRANCH_LESS_THAN || command == LONG_BRANCH_GREATER_THAN || command == LONG_BRANCH_LESS_THAN_OR_EQUALS || command == LONG_BRANCH_GREATER_THAN_OR_EQUALS || command == BRANCH_IF_TRUE || command == BRANCH_IF_FALSE) {
            return index + packet.g4s(); // branch
        } else if (command == SWITCH) {
            var i = packet.g4s();
            var operand = new ArrayList<SwitchCase>();

            for (var j = 0; j < switchValue[i].length; j++) {
                operand.add(new SwitchCase(switchValue[i][j], index + switchOffset[i][j]));
            }

            return operand; // value-branch map
        } else if (command == JOIN_STRING) {
            return packet.g4s(); // count
        } else if (command == GOSUB_WITH_PARAMS) {
            return packet.g4s(); // script
        } else if (command == DEFINE_ARRAY || command == PUSH_ARRAY_INT || command == POP_ARRAY_INT || command == PUSH_ARRAY_INT_LEAVE_INDEX_ON_STACK || command == PUSH_ARRAY_INT_AND_INDEX || command == POP_ARRAY_INT_LEAVE_VALUE_ON_STACK) {
            return packet.g4s(); // array
        }

        return packet.g1();
    }

}
