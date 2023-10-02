package ru.nsu.bolotov.handler;

import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.bolotov.utils.UtilConsts;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class BaudRateThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaudRateThread.class);
    private final Path pathToFile;
    private long elapsedTimeInSec;
    private long totalReceivedBytes;
    private long lastReceivedBytes;
    private boolean isClientHandlerFinished;
    private boolean isBaudRateThreadFinished;

    public BaudRateThread(Path pathToFile) {
        this.pathToFile = pathToFile;
        elapsedTimeInSec = 0;
        isClientHandlerFinished = false;
        isBaudRateThreadFinished = false;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(UtilConsts.TimeConsts.DELAY_TIME_BETWEEN_CHECKS_IN_SEC));
                elapsedTimeInSec += UtilConsts.TimeConsts.DELAY_TIME_BETWEEN_CHECKS_IN_SEC;
                LOGGER.info("[{}] >>> Average speed: [{} Kb/s] ; Instant speed: [{} Kb/s]", pathToFile, getAverageSpeed(), getInstantSpeed());
                lastReceivedBytes = 0;
                if (isClientHandlerFinished) {
                    isBaudRateThreadFinished = true;
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void notifyThatClientHandlerFinished() {
        isClientHandlerFinished = true;
    }

    public boolean isBaudRateThreadFinished() {
        return isBaudRateThreadFinished;
    }

    public void setReceivedBytes(long lastReceivedBytes) {
        totalReceivedBytes += lastReceivedBytes;
        this.lastReceivedBytes += lastReceivedBytes;
    }

    private double getInstantSpeed() {
        return Precision.round(((double) lastReceivedBytes / UtilConsts.TimeConsts.DELAY_TIME_BETWEEN_CHECKS_IN_SEC) / UtilConsts.ConversionConsts.ONE_KILOBYTE, 2);
    }

    private double getAverageSpeed() {
        return Precision.round(((double) totalReceivedBytes / elapsedTimeInSec) / UtilConsts.ConversionConsts.ONE_KILOBYTE, 2);
    }
}
