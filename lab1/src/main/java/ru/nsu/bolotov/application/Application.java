package ru.nsu.bolotov.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.bolotov.exception.FailedSocketCreation;
import ru.nsu.bolotov.model.ApplicationEntity;
import ru.nsu.bolotov.model.InputData;
import ru.nsu.bolotov.parser.CmdLineParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        InputData input = CmdLineParser.parseInputData(args);
        InetSocketAddress group = new InetSocketAddress(input.ipAddr(), input.port());
        checkIPAddress(group);
        try (MulticastSocket socket = new MulticastSocket(input.port())) {
            LOGGER.info("Socket was created on port: {}", input.port());
            ApplicationEntity applicationEntity = new ApplicationEntity();
            LOGGER.info("Application UUID: {} ; Timestamp: {}", applicationEntity.getUUID(), applicationEntity.getDate());
            NetworkInterface networkInterface = NetworkInterface.getNetworkInterfaces().nextElement();
            socket.joinGroup(group, networkInterface);
        } catch (IOException exception) {
            throw new FailedSocketCreation();
        }
    }

    private static void checkIPAddress(InetSocketAddress inetSocketAddress) {
        if (!inetSocketAddress.getAddress().isMulticastAddress()) {
            LOGGER.error("IP-address {} isn't multicast address", inetSocketAddress.getAddress());
            throw new IllegalArgumentException("Not a multicast IP-address!");
        }
    }
}
