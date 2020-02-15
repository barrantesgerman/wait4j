package org.habv.wait4j;

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
     * Host name to check.
     */
    private final String host;
    /**
     * Port number to check.
     */
    private final int port;
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
     * @param host    host name
     * @param port    port number
     * @param verbose verbose indicator
     * @param start   start countdown
     * @param done    done countdown
     */
    public Checker(String host, int port, boolean verbose, CountDownLatch start, CountDownLatch done) {
        this.host = host;
        this.port = port;
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
                System.out.printf("Connecting with %s:%d%n", host, port);
            }
            boolean retry = !isAvailable();
            while (retry) {
                retry = !isAvailable();
            }
            if (verbose) {
                System.out.printf("Connection to %s:%d succeeded!%n", host, port);
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
            socket.connect(new InetSocketAddress(host, port), 1000);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

}
