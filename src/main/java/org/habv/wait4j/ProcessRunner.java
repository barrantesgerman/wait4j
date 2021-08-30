package org.habv.wait4j;

import java.io.IOException;
import java.util.List;

import static picocli.CommandLine.ExitCode.SOFTWARE;

/**
 * Run a background process, redirect the output and get the exit code.
 *
 * @author Herman Barrantes
 */
public class ProcessRunner {

    /**
     * Run a background process, redirect the output and get the exit code.
     *
     * @param command command to be execute
     * @return the exit code
     */
    public int run(List<String> command) {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = builder.start();
            return process.waitFor();
        } catch (IOException | InterruptedException ex) {
            return SOFTWARE;
        }
    }
}
