package org.habv.wait4j;

import picocli.CommandLine.Help.Ansi;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * Thread responsible for checking the availability of a host and port.
 *
 * @author Herman Barrantes
 */
public class Checker implements Runnable {

    /**
     * Address to check.
     */
    private final InetSocketAddress address;

    /**
     * Indicates whether status messages should be printed.
     */
    private final boolean verbose;

    /**
     * To synchronize the start of the threads.
     */
    private final CountDownLatch start;

    /**
     * To notify the main thread when the verification is successfully
     * completed.
     */
    private final CountDownLatch done;

    /**
     * Create a new thread to verify the availability of a host:port.
     *
     * @param address address to check
     * @param verbose verbose indicator
     * @param start   start countdown
     * @param done    done countdown
     */
    public Checker(InetSocketAddress address, boolean verbose, CountDownLatch start, CountDownLatch done) {
        this.address = address;
        this.verbose = verbose;
        this.start = start;
        this.done = done;
    }

    /**
     * Check if the connection to the host:port is available and notifies the
     * main thread, if it is not available, retries until the timeout occurs.
     */
    @Override
    public void run() {
        try {
            start.await();
            if (verbose) {
                System.out.printf(
                        Ansi.AUTO.string("@|yellow [➡]️|@ Connecting with @|cyan,bold %s:%d|@%n"),
                        address.getHostName(),
                        address.getPort());
            }
            boolean retry = !isAvailable();
            while (retry) {
                retry = !isAvailable();
            }
            if (verbose) {
                System.out.printf(
                        Ansi.AUTO.string("@|green [✔]|@ Connection to @|cyan,bold %s:%d|@ succeeded!%n"),
                        address.getHostName(),
                        address.getPort());
            }
            done.countDown();
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Try to connect to the host:port with a timeout of one second, if the
     * connection is successful it returns true, if the connection fails or the
     * timeout occurs it returns false.
     *
     * @return true if the connection is successful before a second otherwise
     * false
     */
    private boolean isAvailable() {
        try (Socket socket = new Socket()) {
            socket.connect(address, 1000);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

}
