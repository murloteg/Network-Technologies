package ru.nsu.bolotov.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.bolotov.exception.FailedDirectoryCreationException;
import ru.nsu.bolotov.exception.FailedSocketException;
import ru.nsu.bolotov.handler.ClientHandlerThread;
import ru.nsu.bolotov.model.ServerInputData;
import ru.nsu.bolotov.parser.CmdLineParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        ServerInputData serverInputData = CmdLineParser.parseCmdLineForServer(args);
        LOGGER.info("Server listens on PORT: {}", serverInputData.port());
        prepareDirectory();
        try (ServerSocket serverSocket = new ServerSocket(serverInputData.port())) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (clientSocket.isConnected()) {
                    LOGGER.info("Server accepted new client!");
                    handleClient(clientSocket);
                }
            }
        } catch (IOException exception) {
            LOGGER.error("IO Exception while server processing client");
            throw new FailedSocketException();
        }
    }

    private static void handleClient(Socket clientSocket) {
        ClientHandlerThread handler = new ClientHandlerThread(clientSocket);
        Thread handlerThread = new Thread(handler);
        handlerThread.start();
    }

    private static void prepareDirectory() {
        try {
            Path directoryPath = Path.of("uploads");
            if (!Files.exists(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
        } catch (IOException exception) {
            LOGGER.error("Cannot create directory");
            throw new FailedDirectoryCreationException();
        }
    }
}
