package ru.nsu.bolotov.proxy;

import ru.nsu.bolotov.util.UtilConsts;

import static ru.nsu.bolotov.util.UtilConsts.NetworkConsts.ZERO_BYTE;
import static ru.nsu.bolotov.util.UtilConsts.SocksConfiguration.IPV4_ADDRESS;
import static ru.nsu.bolotov.util.UtilConsts.SocksConfiguration.PROTOCOL_VERSION;

public final class ProxyResponse {
    public static final byte[] CONNECTION_GRANTED = new byte[] {
            PROTOCOL_VERSION,
            ZERO_BYTE,
            ZERO_BYTE,
            IPV4_ADDRESS,
            ZERO_BYTE,
            ZERO_BYTE,
            ZERO_BYTE,
            ZERO_BYTE,
            ZERO_BYTE,
            ZERO_BYTE
    };

    public static final byte[] NO_AUTH_RESPONSE = new byte[] {
            UtilConsts.SocksConfiguration.PROTOCOL_VERSION,
            UtilConsts.SocksConfiguration.NO_AUTH_METHOD
    };

    private ProxyResponse() {
        throw new IllegalStateException(UtilConsts.StringConsts.INSTANTIATION_MESSAGE);
    }
}
