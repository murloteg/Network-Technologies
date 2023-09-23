package ru.nsu.bolotov.utils;

public final class UtilConsts {
    public static final class TimeConsts {
        public static final long DELAY_TIME_BETWEEN_CHECKING_IN_MSEC = 500;
        public static final long DELAY_TIME_TO_SENDER_IN_MSEC = 1000;
        public static final long DELAY_TIME_TO_RECEIVER_IN_MSEC = 2000;
        public static final long CONNECTION_LIFETIME_IN_MSEC = 5000;
        public static final long LIMIT_TIME_IN_SEC = 45;

        private TimeConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class SizeConsts {
        public static final int DATA_ARRAY_SIZE = 1024;

        private SizeConsts() {
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
