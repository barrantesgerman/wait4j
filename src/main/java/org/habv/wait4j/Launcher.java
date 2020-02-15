package org.habv.wait4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class to launch the application.
 *
 * @author Herman Barrantes
 */
public class Launcher {

    /**
     * Main method to launch the application.
     *
     * @param args list of command line arguments
     */
    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        launcher.start(args);
    }

    /**
     * Analyze the arguments provided by the command line and start the program.
     *
     * @param args list of command line arguments
     */
    public void start(String[] args) {
        try {
            Arguments arguments = Arguments.parse(args);
            int threads = arguments.getHostPorts().size();
            boolean verbose = arguments.isVerbose();

            ExecutorService service = Executors.newFixedThreadPool(threads);

            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(threads);

            arguments.getHostPorts()
                    .stream()
                    .map((host) -> new Checker(host.getHost(), host.getPort(), verbose, start, done))
                    .forEach(service::submit);

            start.countDown();
            int timeout = arguments.getTimeout();
            boolean allDone = true;
            if (timeout == 0) {
                done.await();
            } else {
                allDone = done.await(timeout, TimeUnit.SECONDS);
            }
            service.shutdown();

            if (allDone) {
                System.exit(new ProcessRunner().run(arguments.getCommand()));
            } else {
                if (verbose) {
                    System.err.printf("Timeout occurred after waiting %d seconds%n", arguments.getTimeout());
                }
                System.exit(1);
            }
        } catch (InterruptedException | IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

}
