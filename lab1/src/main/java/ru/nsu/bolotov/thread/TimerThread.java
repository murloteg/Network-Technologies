package ru.nsu.bolotov.thread;

import ru.nsu.bolotov.exception.FailedThreadInterruptException;
import ru.nsu.bolotov.utils.UtilConsts;

import java.util.concurrent.TimeUnit;

public class TimerThread implements Runnable {
    private long elapsedTimeInSec;
    private boolean isSender;
    private boolean isApplicationStopped;

    public TimerThread() {
        elapsedTimeInSec = 0;
        isSender = true;
        isApplicationStopped = false;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(UtilConsts.TimeConsts.TIMER_THREAD_DELAY_TIME_IN_MSEC);
                ++elapsedTimeInSec;
                if (isSender && TimeUnit.SECONDS.toMillis(elapsedTimeInSec) % UtilConsts.TimeConsts.DURATION_OF_SENDER_IN_MSEC == 0) {
                    isSender = false;
                } else if (!isSender && TimeUnit.SECONDS.toMillis(elapsedTimeInSec) % UtilConsts.TimeConsts.DURATION_OF_RECEIVER_IN_MSEC == 0) {
                    isSender = true;
                }
                if (elapsedTimeInSec >= UtilConsts.TimeConsts.APPLICATION_LIFETIME_IN_SEC) {
                    isApplicationStopped = true;
                    Thread.currentThread().interrupt();
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new FailedThreadInterruptException();
            }
        }
    }

    public boolean getSenderStatus() {
        return isSender;
    }

    public boolean isApplicationStopped() {
        return isApplicationStopped;
    }
}
