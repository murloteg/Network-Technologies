package ru.nsu.bolotov.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.bolotov.exception.FailedFileSearchException;
import ru.nsu.bolotov.exception.FailedSocketException;
import ru.nsu.bolotov.utils.UtilConsts;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class ClientHandlerThread implements Runnable {
    private final Socket clientSocket;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandlerThread.class);

    public ClientHandlerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        DataInputStream clientSocketInputStream;
        try {
            clientSocketInputStream = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException exception) {
            throw new FailedSocketException();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                long fileNameLength = clientSocketInputStream.readLong();
                if (fileNameLength > 0) {
                    String fileName = clientSocketInputStream.readUTF();
                    Path pathToFile = Path.of("uploads/" + fileName);
                    if (!Files.exists(pathToFile)) {
                        Files.createFile(pathToFile);
                    }
                    long fileDataLength = clientSocketInputStream.readLong();
                    receiveFileData(clientSocketInputStream, pathToFile, fileDataLength);
                    clientSocketInputStream.close();
                    clientSocket.close();
                }
                Thread.currentThread().interrupt();
            } catch (IOException exception) {
                throw new FailedSocketException();
            }
        }
    }

    private void receiveFileData(DataInputStream clientSocketInputStream, Path pathToFile, long fileDataLength) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(pathToFile.toFile());
        } catch (FileNotFoundException exception) {
            throw new FailedFileSearchException();
        }
        byte[] buffer = new byte[UtilConsts.ConnectionConsts.BUFFER_SIZE];
        BaudRateThread baudRate = new BaudRateThread(pathToFile);
        Thread baudRateThread = new Thread(baudRate);
        baudRateThread.start();
        long receivedBytes = 0;
        try {
            while (receivedBytes < fileDataLength) {
                receivedBytes += UtilConsts.ConnectionConsts.BUFFER_SIZE;
                if (receivedBytes > fileDataLength) {
                    int missedBytesCounter = (int) fileDataLength % UtilConsts.ConnectionConsts.BUFFER_SIZE;
                    byte[] missedBytes = new byte[missedBytesCounter];
                    clientSocketInputStream.readNBytes(missedBytes, 0, missedBytesCounter);
                    fileOutputStream.write(missedBytes, 0, missedBytesCounter);
                    baudRate.setReceivedBytes(missedBytesCounter);
                    LOGGER.trace("Server received last portion of data! Total received size: {}", fileDataLength);
                    break;
                }
                clientSocketInputStream.readNBytes(buffer, 0, UtilConsts.ConnectionConsts.BUFFER_SIZE);
                fileOutputStream.write(buffer);
                baudRate.setReceivedBytes(UtilConsts.ConnectionConsts.BUFFER_SIZE);
                LOGGER.trace("Server received new portion of data! Total received size: {}", receivedBytes);
            }
            fileOutputStream.close();
        } catch (IOException exception) {
            LOGGER.error("IO Exception while client handler thread processing client");
            throw new FailedSocketException();
        }
        baudRate.notifyThatClientHandlerFinished();
        if (!baudRate.isBaudRateThreadFinished()) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(UtilConsts.TimeConsts.DELAY_TIME_BETWEEN_CHECKS_IN_SEC));
            } catch (InterruptedException exception) {
                LOGGER.error("Interrupt exception during wait baud rate thread");
                Thread.currentThread().interrupt();
            }
        }
        baudRateThread.interrupt();
        LOGGER.info("Client handler for [{}] successfully finished job!", pathToFile);
    }
}
