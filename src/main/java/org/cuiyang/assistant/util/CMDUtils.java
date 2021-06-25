package org.cuiyang.assistant.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class CMDUtils {

    public static void run(Process process, Consumer<String> function) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            if (function != null) {
                function.accept(line);
            }
        }
    }

    public static void run(String cmd, Consumer<String> function) throws IOException {
        Process process = Runtime.getRuntime().exec(cmd);
        run(process, function);
    }

    public static String run(String cmd) throws IOException {
        StringBuilder sb = new StringBuilder();
        run(cmd, s -> sb.append(s).append("\n"));
        return sb.toString().trim();
    }
}
