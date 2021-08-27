package org.habv.wait4j;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

import java.net.InetSocketAddress;

public class InetSocketAddressConverter implements ITypeConverter<InetSocketAddress> {

    @Override
    public InetSocketAddress convert(String value) throws Exception {
        int pos = value.lastIndexOf(':');
        if (pos < 0) {
            throw new TypeConversionException(
                    "Invalid format: must be 'host:port' but was '" + value + "'");
        }
        String adr = value.substring(0, pos);
        int port = Integer.parseInt(value.substring(pos + 1));
        return new InetSocketAddress(adr, port);
    }
}
