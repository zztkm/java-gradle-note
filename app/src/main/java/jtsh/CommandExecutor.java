package jtsh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandExecutor {
    public static int execute(String cmd, long timeoutSec) {
        List<String> cmdList = Arrays.asList(cmd);
        ProcessBuilder builder = new ProcessBuilder(cmdList);
        builder.redirectErrorStream(true);  // 標準エラー出力の内容を標準出力にマージする

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
            boolean end = process.waitFor(timeoutSec, TimeUnit.SECONDS);
            if (end) {
                exitCode = process.exitValue();
            } else {
                throw new CommandExecuteFailedException("Command timeout. [CommandPath: " + cmdList + "]");
            }

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