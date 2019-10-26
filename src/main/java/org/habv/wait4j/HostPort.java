package org.habv.wait4j;

import java.util.Objects;

/**
 * Represents a host and port to verify if available.
 *
 * @author Herman Barrantes
 */
public class HostPort {

    /**
     * Host name.
     */
    private final String host;
    /**
     * Port number.
     */
    private final int port;

    /**
     * Create a new host and port instance.
     *
     * @param host host name
     * @param port port number
     */
    public HostPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Get the host name.
     *
     * @return host name
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the port number.
     *
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.host);
        hash = 17 * hash + this.port;
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HostPort other = (HostPort) obj;
        if (this.port != other.port) {
            return false;
        }
        if (!Objects.equals(this.host, other.host)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the values in format <code>host:port</code>.
     *
     * @return <code>host:port</code>
     */
    @Override
    public String toString() {
        return host + ":" + port;
    }

}
