package ru.nsu.bolotov.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.bolotov.exception.FailedSocketCreation;
import ru.nsu.bolotov.exception.FailedThreadInterruptException;
import ru.nsu.bolotov.model.ApplicationEntity;
import ru.nsu.bolotov.model.InputData;
import ru.nsu.bolotov.parser.CmdLineParser;
import ru.nsu.bolotov.thread.TimerThread;
import ru.nsu.bolotov.utils.UtilConsts;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final Map<ApplicationEntity, Long> CONNECTIONS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        InputData input = CmdLineParser.parseInputData(args);
        InetSocketAddress group = new InetSocketAddress(input.ipAddr(), input.port());
        checkIPAddress(group);
        LOGGER.info("IP: {} is valid multicast IP", group.getAddress());

        try (MulticastSocket socket = new MulticastSocket(group.getPort())) {
            LOGGER.info("Socket was created on port: {}", group.getPort());
            ApplicationEntity applicationEntity = new ApplicationEntity();
            LOGGER.info("Application UUID: {}", applicationEntity.getUUID());
            socket.joinGroup(group.getAddress());

            TimerThread timerThread = new TimerThread();
            Thread thread = new Thread(timerThread);
            thread.start();
            while (!timerThread.isApplicationStopped()) {
                boolean isSender = timerThread.getSenderStatus();
                if (isSender) {
                    LOGGER.trace("Try to send datagram with application entity");
                    sendData(socket, group, applicationEntity);
                } else {
                    LOGGER.trace("Try to receive datagram with application entity");
                    ApplicationEntity entity = receiveData(socket, group);
                    if (!isConnectionExists(entity)) {
                        CONNECTIONS.put(entity, UtilConsts.TimeConsts.CONNECTION_LIFETIME_IN_MSEC);
                    } else {
                        resetLifetimeOfConnection(entity);
                    }
                }
                viewAliveConnections();
                updateAndCheckConnections(UtilConsts.TimeConsts.DELAY_TIME_BETWEEN_CHECKING_IN_MSEC);
                Thread.sleep(UtilConsts.TimeConsts.DELAY_TIME_BETWEEN_CHECKING_IN_MSEC);
            }
            socket.leaveGroup(group.getAddress());
            LOGGER.info("Application finished job");
        } catch (IOException exception) {
            LOGGER.error(exception.getMessage());
            throw new FailedSocketCreation();
        } catch (InterruptedException exception) {
            LOGGER.error(exception.getMessage());
            Thread.currentThread().interrupt();
            throw new FailedThreadInterruptException();
        }
    }

    private static void sendData(MulticastSocket socket, InetSocketAddress group, ApplicationEntity entity) throws IOException {
        byte[] data = prepareDataToSend(entity);
        socket.send(new DatagramPacket(data, data.length, group.getAddress(), group.getPort()));
    }

    private static ApplicationEntity receiveData(MulticastSocket socket, InetSocketAddress group) throws IOException {
        byte[] data = new byte[UtilConsts.SizeConsts.DATA_ARRAY_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length, group.getAddress(), group.getPort());
        socket.receive(packet);
        return parseDataAfterReceive(data);
    }

    private static byte[] prepareDataToSend(ApplicationEntity entity) {
        return entity.getUUID().toString().getBytes();
    }

    private static ApplicationEntity parseDataAfterReceive(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (byte partOfData: data) {
            if (partOfData != 0) {
                builder.append(Character.toChars(partOfData));
            }
        }
        return new ApplicationEntity(UUID.fromString(builder.toString()));
    }

    private static boolean isConnectionExists(ApplicationEntity entity) {
        for (Map.Entry<ApplicationEntity, Long> item : CONNECTIONS.entrySet()) {
            if (item.getKey().getUUID().equals(entity.getUUID())) {
                return true;
            }
        }
        return false;
    }

    private static void resetLifetimeOfConnection(ApplicationEntity entity) {
        Set<ApplicationEntity> entities = CONNECTIONS.keySet();
        for (ApplicationEntity item : entities) {
            if (item.getUUID().equals(entity.getUUID())) {
                CONNECTIONS.replace(item, UtilConsts.TimeConsts.CONNECTION_LIFETIME_IN_MSEC);
                return;
            }
        }
    }

    private static void updateAndCheckConnections(long elapsedTimeInMsec) {
        for (Map.Entry<ApplicationEntity, Long> item : CONNECTIONS.entrySet()) {
            CONNECTIONS.replace(item.getKey(), item.getValue() - elapsedTimeInMsec);
            if (item.getValue() <= 0) {
                CONNECTIONS.remove(item.getKey());
            }
        }
    }

    private static void viewAliveConnections() {
        StringBuilder builder = new StringBuilder();
        builder.append("ALIVE CONNECTIONS:\n");
        int index = 0;
        for (Map.Entry<ApplicationEntity, Long> item : CONNECTIONS.entrySet()) {
            builder.append("\t\t")
                    .append(index)
                    .append(")\t")
                    .append(item.getKey())
                    .append("\t\t")
                    .append(item.getValue())
                    .append(" ms\n");
            ++index;
        }
        LOGGER.info("{}", builder);
    }

    private static void checkIPAddress(InetSocketAddress inetSocketAddress) {
        if (!inetSocketAddress.getAddress().isMulticastAddress()) {
            LOGGER.error("IP-address {} isn't multicast address", inetSocketAddress.getAddress());
            throw new IllegalArgumentException("Not a multicast IP-address!");
        }
    }
}
