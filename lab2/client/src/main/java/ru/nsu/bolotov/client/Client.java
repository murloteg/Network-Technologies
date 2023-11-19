package ru.nsu.bolotov.client;

import com.google.common.net.InetAddresses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.bolotov.exception.FailedSocketException;
import ru.nsu.bolotov.model.ClientInputData;
import ru.nsu.bolotov.parser.CmdLineParser;
import ru.nsu.bolotov.utils.UtilConsts;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        ClientInputData clientInputData = CmdLineParser.parseCmdLineForClient(args);
        LOGGER.info("Client parsed CMD Line >>> IP: {}, PORT: {}, FILE PATH: {}", clientInputData.ipAddr(), clientInputData.port(), clientInputData.absolutePathToFile());
        validateIPAddress(clientInputData.ipAddr());
        checkIfFileDoesntExist(clientInputData.absolutePathToFile());
        sendFileToServer(clientInputData);
    }

    private static void sendFileToServer(ClientInputData clientInputData) {
        try (Socket clientSocket = new Socket(InetAddress.getByName(clientInputData.ipAddr()), clientInputData.port())) {
            LOGGER.info("Client IP address: {}", InetAddress.getLocalHost());
            File fileToTransfer = new File(clientInputData.absolutePathToFile());
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            sendRequiredInfoAboutFile(dataOutputStream, fileToTransfer);

            FileInputStream fileInputStream = new FileInputStream(fileToTransfer);
            byte[] buffer = new byte[UtilConsts.ConnectionConsts.BUFFER_SIZE];
            long sentBytes = 0;
            long fileDataLength = fileToTransfer.length();
            while (sentBytes < fileDataLength) {
                sentBytes += UtilConsts.ConnectionConsts.BUFFER_SIZE;
                if (sentBytes > fileDataLength) {
                    int missedBytesCounter = (int) fileDataLength % UtilConsts.ConnectionConsts.BUFFER_SIZE;
                    byte[] missedBytes = new byte[missedBytesCounter];
                    fileInputStream.readNBytes(missedBytes, 0, missedBytesCounter);
                    dataOutputStream.write(missedBytes, 0, missedBytesCounter);
                    LOGGER.trace("Client sent last portion of data! Total sent bytes: {}", fileDataLength);
                    break;
                }
                fileInputStream.readNBytes(buffer, 0, UtilConsts.ConnectionConsts.BUFFER_SIZE);
                dataOutputStream.write(buffer);
                LOGGER.trace("Client sent new portion of data! Total sent bytes: {}", sentBytes);
            }
            dataOutputStream.close();
            fileInputStream.close();
        } catch (IOException exception) {
            LOGGER.error("IO Exception while client sending data to server: {}", exception.getMessage());
            throw new FailedSocketException();
        }
        LOGGER.info("Client finished job successfully!");
    }

    private static void sendRequiredInfoAboutFile(DataOutputStream dataOutputStream, File fileToTransfer) throws IOException {
        String fileName = fileToTransfer.getName();
        long fileNameLength = fileName.length();
        long fileDataLength = fileToTransfer.length();
        dataOutputStream.writeLong(fileNameLength);
        dataOutputStream.writeUTF(fileName);
        dataOutputStream.writeLong(fileDataLength);
        LOGGER.trace("Client sent required information about file");
    }

    private static void validateIPAddress(String ip) {
        if (!InetAddresses.isInetAddress(ip)) {
            LOGGER.error("IP-address {} is not valid!", ip);
            throw new IllegalArgumentException("Invalid IP address");
        }
    }

    private static void checkIfFileDoesntExist(String absolutePathToFile) {
        File fileToTransfer = new File(absolutePathToFile);
        if (!fileToTransfer.exists()) {
            LOGGER.error("File with absolute path {} doesn't exists!", absolutePathToFile);
            throw new IllegalArgumentException("File doesn't exists!");
        }
    }
}
