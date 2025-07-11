package rs3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CommandUpdater {
    private static final int VERSION = 942;
    private static final int MODE = 1;
    private static boolean MANGLED = true;

    public static void main() throws IOException {
        var groups = new HashMap<String, String>();

        for (var path : Files.list(Path.of("data/commands")).toList()) {
            var group = convertCase(path.getFileName().toString().split("\\.")[0]);

            for (var line : Files.readAllLines(path)) {
                if (line.startsWith("[command,")) {
                    line = line.substring("[command,".length());
                    line = line.substring(0, line.indexOf("]"));
                    groups.put(line, group);
                }
            }
        }

        var names = Files.readAllLines(Path.of("data/opcodes-" + VERSION + ".txt"))
                .stream()
                .map(l -> l.split(",")[0])
                .collect(Collectors.toList());

        var index = 0;

        for (var command : Files.readAllLines(Path.of("order.txt"))) {
            var parts = command.split(" ");
            var opcode = parts[0];
            var handler = parts[1];
            var name = index >= names.size() ? "unknown_command_" + index++ : names.get(index++);

            if (MODE == 0) {
                // update file
                System.out.println(name + "," + opcode + "," + handler);
            } else if (MODE == 1) {
                // update idb
                var group = groups.getOrDefault(name, "TODO");

                if (MANGLED) {
                    var mangled = "_ZN3jag6opcode" + group.length() + group + name.length() + name + "E";
                    mangled += "PNS_6ClientE"; // jag::Client*
                    mangled += "PNS_17ClientScriptStateE"; // jag::ClientScriptState*
                    System.out.println("set_name(" + handler + ", \"" + mangled + "\")");
                } else {
                    System.out.println("set_name(" + handler + ", \"jag::opcode::" + groups.getOrDefault(name, "TODO") + "::" + name + "\")");
                }

                System.out.println("apply_type(" + handler + ", parse_decl(\"__int64 __fastcall f(jag::Client *client, jag::ClientScriptState *state)\", 0), 1)");
            }
        }
    }

    private static String convertCase(String s) {
        return Arrays.stream(s.split("_")).map(part -> part.substring(0, 1).toUpperCase() + part.substring(1)).collect(Collectors.joining());
    }
}
