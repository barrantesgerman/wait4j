package org.habv.wait4j;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static picocli.CommandLine.ExitCode.SOFTWARE;

/**
 * Main class to launch the application.
 *
 * @author Herman Barrantes
 */
@Command(name = "wait4j",
        version = "@|yellow wait4j 1.0.0|@",
        description = "Wait for availability of multiple services.",
        showEndOfOptionsDelimiterInUsageHelp = true,
        mixinStandardHelpOptions = true)
public class Launcher implements Callable<Integer> {

    @Option(names = {"-v", "--verbose"},
            description = "Print more details (default: ${DEFAULT-VALUE}).")
    private boolean verbose;

    @Option(names = {"-t", "--timeout"},
            description = "Set the timeout (default: ${DEFAULT-VALUE}).")
    private int timeout = 30;

    @Option(names = {"-a", "--address"},
            description = "One or more addresses to check, in%nformat: 'host:port'.",
            paramLabel = "address",
            required = true,
            split = ",",
            converter = InetSocketAddressConverter.class)
    private Set<InetSocketAddress> addresses;

    @Parameters(paramLabel = "COMMAND",
            description = "Command to execute.",
            arity = "1..*")
    private List<String> command;

    /**
     * Main method to launch the application.
     *
     * @param args list of command line arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Launcher()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Start the program.
     */
    @Override
    public Integer call() throws Exception {

        int threads = addresses.size();

        ExecutorService service = Executors.newFixedThreadPool(threads);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        addresses
                .stream()
                .map((address) -> new Checker(address, verbose, start, done))
                .forEach(service::submit);

        start.countDown();
        boolean allDone = true;
        if (timeout == 0) {
            done.await();
        } else {
            allDone = done.await(timeout, TimeUnit.SECONDS);
        }
        service.shutdown();

        if (allDone) {
            return new ProcessRunner().run(command);
        } else {
            if (verbose) {
                System.err.printf("Timeout occurred after waiting %d seconds%n", timeout);
            }
            return SOFTWARE;
        }

    }
}
