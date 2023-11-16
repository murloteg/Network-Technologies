package ru.nsu.bolotov.util;

public final class UtilConsts {
    public static final class NetworkConsts {
        public static final String HOSTNAME = "127.0.0.1";
        public static final int BUFFER_SIZE = 65536;
        public static final byte ZERO_BYTE = 0x00;

        private NetworkConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class SocksConfiguration {
        public static final byte PROTOCOL_VERSION = 0x05;
        public static final byte CONNECT = 0x01;
        public static final byte IPV4_ADDRESS = 0x01;
        public static final byte HOSTNAME_ADDRESS = 0x03;
        public static final byte NO_AUTH_METHOD = 0x00;

        private SocksConfiguration() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class StringConsts {
        public static final String INSTANTIATION_MESSAGE = "Instantiation of util class";

        private StringConsts() {
            throw new IllegalStateException(INSTANTIATION_MESSAGE);
        }
    }

    private UtilConsts() {
        throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
    }
}
