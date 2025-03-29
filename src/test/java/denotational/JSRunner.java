package denotational;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import static java.util.stream.Collectors.joining;

class JSRunner {
    static String run(String code, Map<String, Object> env) {
        var jsEnv = env.isEmpty()?"": env.entrySet().stream()
                .map(kv -> String.format("'%s',%s", kv.getKey(), kv.getValue()))
                .collect(joining(",", "[", "]"));

        return run(String.format("console.log((%s)(new Map([%s])))", code, jsEnv));
    }

   private static String run(String code) {
        try {
            var process = new ProcessBuilder("node", "-e", code).start();

            int result = process.waitFor();
            if (result != 0) {
                var stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                System.err.println(stdErr.lines().collect(joining("\n")));
            }
            var stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return stdInput.lines().collect(joining());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
