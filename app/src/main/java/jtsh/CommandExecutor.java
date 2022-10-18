package jtsh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class CommandExecutor {
    public static int execute(String[] cmd) {
        List<String> cmdList = Arrays.asList(cmd);
        ProcessBuilder builder = new ProcessBuilder(cmdList);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.redirectInput(ProcessBuilder.Redirect.INHERIT);

        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new CommandExecuteFailedException("Command launch failed. [cmd: " + cmdList + "]", e);
        }

        int exitCode;
        try {
            // 標準出力をすべて読み込む
            try (InputStream stream = process.getInputStream()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            exitCode = process.waitFor();

        } catch (InterruptedException e) {
            throw new CommandExecuteFailedException("Command interrupted. [CommandPath: " + cmdList + "]", e);
        } finally {
            if (process.isAlive()) {
                process.destroy(); // プロセスを強制終了
            }
        }

        return exitCode;
    }

    private CommandExecutor() {
    }
}