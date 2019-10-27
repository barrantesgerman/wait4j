package org.habv.wait4j;

import java.util.*;

/**
 * Parse the command line arguments and store them.
 *
 * @author Herman Barrantes
 */
public class Arguments {

    /**
     * List of host and port to be checked.
     */
    private final Set<HostPort> hostPorts;
    /**
     * Maximum time to wait for host and port availability in seconds.
     */
    private final int timeout;
    /**
     * How the output should be, if it is true it will be verbose, if it is
     * false it will be quiet.
     */
    private final boolean verbose;

    /**
     * List of arguments to be executed later in case of success.
     */
    private final List<String> command;

    /**
     * Create an instance that represents the command line arguments.
     *
     * @param hostPorts list of host and port to be checked
     * @param timeout maximum time to wait for host and port availability in
     * seconds
     * @param verbose how the output should be, if it is true it will be
     * verbose, if it is false it will be quiet
     */
    private Arguments(Set<HostPort> hostPorts, int timeout, boolean verbose, List<String> command) {
        this.hostPorts = hostPorts;
        this.timeout = timeout;
        this.verbose = verbose;
        this.command = command;
    }

    /**
     * Get the list of host and port indicated by the command line to be
     * checked.
     *
     * @return list of host and port to be checked
     */
    public Set<HostPort> getHostPorts() {
        return hostPorts;
    }

    /**
     * Get the timeout indicated by the command line, if not indicated the
     * default value is 30 seconds.
     *
     * @return timeout in seconds
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Get how the output should be, if it is true it will be verbose, if it is
     * false it will be quiet.
     *
     * @return true if output will be verbose
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * List of arguments to be executed later in case of success.
     *
     * @return list of arguments to be executed later in case of success
     */
    public List<String> getCommand() {
        return command;
    }

    /**
     * Parse the command line arguments and store them.
     *
     * @param args command line arguments
     * @return instance that represents the command line arguments
     */
    public static Arguments parse(String[] args) {
        Set<HostPort> hostPorts = new HashSet<>();
        int timeout = 30;
        boolean verbose = true;
        boolean isCommand = false;
        List<String> command = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(isCommand) {
                command.add(arg);
            } else if (arg.equals("--")) {
                isCommand = true;
            } else if (arg.equals("-t")) {
                if(i == args.length - 1) {
                    throw new IllegalArgumentException("You must provide a timeout value");
                }
                timeout = toInt(args[++i], "Timeout");
            } else if (arg.startsWith("--timeout=")) {
                timeout = toInt(arg.substring(10), "Timeout");
            } else if (arg.equals("-q") || arg.equals("--quiet")) {
                verbose = false;
            } else {
                hostPorts.add(toHostPort(arg));
            }
        }
        if (hostPorts.isEmpty()) {
            throw new IllegalArgumentException("You must provide at least one host:port");
        }
        if (command.isEmpty()) {
            throw new IllegalArgumentException("You must provide at least one command parameter");
        }
        return new Arguments(Collections.unmodifiableSet(hostPorts), timeout, verbose, Collections.unmodifiableList(command));
    }

    /**
     * Convert a string to a host and port.
     *
     * @param value string to be converted
     * @return representation as host and port
     */
    private static HostPort toHostPort(String value) {
        if (value.contains(":")) {
            String[] parts = value.split(":", 2);
            String host = parts[0];
            int port = toInt(parts[1], "Port");
            return new HostPort(host, port);
        } else {
            throw new IllegalArgumentException("You must provide a host name in the format host:port instead of " + value);
        }
    }

    /**
     * Convert a string to int.
     *
     * @param value string to be converted
     * @param type type for the error message
     * @return string converted to int
     */
    private static int toInt(String value, String type) {
        try {
            int integer = Integer.parseInt(value);
            if (integer < 0) {
                throw new IllegalArgumentException(type + " must be a positive integer instead of " + value);
            }
            return integer;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(type + " must be a positive integer instead of " + value, ex);
        }
    }

}
