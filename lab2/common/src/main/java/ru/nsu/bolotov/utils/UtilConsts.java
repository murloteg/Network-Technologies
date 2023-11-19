package ru.nsu.bolotov.utils;

public final class UtilConsts {
    public static final class TimeConsts {
        public static final int DELAY_TIME_BETWEEN_CHECKS_IN_SEC = 3;

        private TimeConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class ConnectionConsts {
        public static final int BUFFER_SIZE = 30;

        private ConnectionConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class ConversionConsts {
        public static final int ONE_KILOBYTE = 1024;

        private ConversionConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class StringConsts {
        public static final String INSTANTIATION_MESSAGE = "Instantiation of util class";

        private StringConsts() {
            throw new IllegalStateException(INSTANTIATION_MESSAGE);
        }
    }

    private  UtilConsts() {
        throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
    }
}
