package ru.nsu.bolotov.proxy;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.Section;
import ru.nsu.bolotov.exception.ProxyException;
import ru.nsu.bolotov.util.UtilConsts;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class SocksProxyServer implements Runnable {
    private final String hostname;
    private final int port;
    private static final Logger LOGGER = LoggerFactory.getLogger(SocksProxyServer.class);

    @Override
    public void run() {
        try (Selector selector = SelectorProvider.provider().openSelector()) {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(hostname, port));
            serverSocketChannel.register(selector, serverSocketChannel.validOps());

            while (!Thread.currentThread().isInterrupted()) {
                selector.select();
                Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                while (selectionKeys.hasNext()) {
                    SelectionKey nextKey = selectionKeys.next();
                    selectionKeys.remove();
                    handleSelectionKey(nextKey);
                }
            }
        } catch (IOException exception) {
            throw new ProxyException(exception.getMessage());
        }
    }

    private void handleSelectionKey(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            return;
        }
        if (key.isAcceptable()) {
            handleAccept(key);
        } else if (key.isConnectable()) {
            handleConnect(key);
        } else if (key.isReadable()) {
            handleRead(key);
        } else if (key.isWritable()) {
            handleWrite(key);
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        LOGGER.info("Trying to accept client");
        SocketChannel acceptedChannel = ((ServerSocketChannel) key.channel()).accept();
        acceptedChannel.configureBlocking(false);
        acceptedChannel.register(key.selector(), SelectionKey.OP_READ);
    }

    private void handleConnect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ProxyPeerUnit proxyPeerUnit = (ProxyPeerUnit) key.attachment();
        channel.finishConnect();

        proxyPeerUnit.setInput(ByteBuffer.allocate(UtilConsts.NetworkConsts.BUFFER_SIZE));
        proxyPeerUnit.getInput().put(ProxyResponse.CONNECTION_GRANTED).flip();

        proxyPeerUnit.setOutput(((ProxyPeerUnit) proxyPeerUnit.getPeerKey().attachment()).getInput());
        ((ProxyPeerUnit) proxyPeerUnit.getPeerKey().attachment()).setOutput(proxyPeerUnit.getInput());

        proxyPeerUnit.getPeerKey().interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        key.interestOps(0);
    }

    private void handleRead(SelectionKey key) throws IOException {
        ProxyPeerUnit proxyPeerUnit = (ProxyPeerUnit) key.attachment();
        if (Objects.isNull(proxyPeerUnit)) {
            proxyPeerUnit = new ProxyPeerUnit();
            proxyPeerUnit.setInput(ByteBuffer.allocate(UtilConsts.NetworkConsts.BUFFER_SIZE));
            proxyPeerUnit.setRequestType(RequestType.REQUEST_READ);
            key.attach(proxyPeerUnit);
            LOGGER.info("Proxy peer unit was created!");
        }
        if (proxyPeerUnit.getRequestType() == RequestType.REQUEST_DNS_READ) {
            handleDNSRead(key);
        } else {
            handleNonDNSRead(key);
        }
    }

    private void handleDNSRead(SelectionKey key) throws IOException {
        ProxyPeerUnit proxyPeerUnit = (ProxyPeerUnit) key.attachment();
        DatagramChannel channel = (DatagramChannel) key.channel();
        if (channel.read(proxyPeerUnit.getInput()) <= 0) {
            cancelSelectionKey(key);
            throw new IllegalStateException("Error while handling DNS");
        } else {
            Message message = new Message(proxyPeerUnit.getInput().array());
            Optional<Record> record = message.getSection(Section.ANSWER).stream().findAny();
            if (record.isEmpty()) {
                cancelSelectionKey(key);
                throw new IllegalArgumentException("Hostname cannot be resolved");
            }
            InetAddress ipAddress = InetAddress.getByName(record.get().rdataToString());
            configurePeerProxyUnit(proxyPeerUnit.getPeerKey(), ipAddress, proxyPeerUnit.getConnectionPort());
            key.interestOps(0);
            key.cancel();
            key.channel().close();
        }
    }

    private void handleNonDNSRead(SelectionKey key) throws IOException {
        ProxyPeerUnit proxyPeerUnit = (ProxyPeerUnit) key.attachment();
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.read(proxyPeerUnit.getInput()) <= 0) {
            LOGGER.info("All data was received");
            cancelSelectionKey(key);
        } else if (proxyPeerUnit.getRequestType() == RequestType.REQUEST_READ) {
            LOGGER.info("Trying to parse request header");
            processingClientRequest(key);
        } else if (Objects.isNull(proxyPeerUnit.getPeerKey())) {
            validateConnectionRequest(key);
        } else {
            proxyPeerUnit.getPeerKey().interestOps(proxyPeerUnit.getPeerKey().interestOps() | SelectionKey.OP_WRITE);
            key.interestOps(key.interestOps() ^ SelectionKey.OP_READ);
            proxyPeerUnit.getInput().flip();
        }
    }

    private void processingClientRequest(SelectionKey key) {
        ProxyPeerUnit proxyPeerUnit = (ProxyPeerUnit) key.attachment();
        int length = proxyPeerUnit.getInput().position();
        if (length < 2) {
            return;
        }

        byte[] headerBuffer = proxyPeerUnit.getInput().array();
        if (headerBuffer[0] != UtilConsts.SocksConfiguration.PROTOCOL_VERSION) {
            throw new UnsupportedOperationException("Unsupported version of SOCKS protocol");
        }

        byte numberOfAuthenticationMethods = headerBuffer[1];
        if (length - 2 != numberOfAuthenticationMethods) {
            LOGGER.info("Waiting length of number of authentication methods");
            return;
        }

        if (!isNoAuthMethodPresents(headerBuffer, numberOfAuthenticationMethods)) {
            throw new UnsupportedOperationException("Unsupported authentication method");
        }

        proxyPeerUnit.setOutput(proxyPeerUnit.getInput());
        proxyPeerUnit.getOutput().clear();
        proxyPeerUnit.getOutput().put(ProxyResponse.NO_AUTH_RESPONSE).flip();
        proxyPeerUnit.setRequestType(RequestType.REQUEST_WRITE);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private boolean isNoAuthMethodPresents(byte[] headerBuffer, byte numberOfAuthenticationMethods) {
        for (int i = 0; i < numberOfAuthenticationMethods; ++i) {
            if (headerBuffer[i + 2] == UtilConsts.SocksConfiguration.NO_AUTH_METHOD) {
                return true;
            }
        }
        return false;
    }

    private void validateConnectionRequest(SelectionKey key) throws IOException {
        ProxyPeerUnit proxyPeerUnit = (ProxyPeerUnit) key.attachment();
        int length = proxyPeerUnit.getInput().position();
        if (length < 4) {
            return;
        }

        byte[] headerBuffer = proxyPeerUnit.getInput().array();
        if (headerBuffer[0] != UtilConsts.SocksConfiguration.PROTOCOL_VERSION) {
            throw new UnsupportedOperationException("Unsupported version of SOCKS protocol");
        }

        if (headerBuffer[1] != UtilConsts.SocksConfiguration.CONNECT) {
            throw new UnsupportedOperationException("Unsupported command");
        }

        if (headerBuffer[3] == UtilConsts.SocksConfiguration.IPV4_ADDRESS) {
            validateConnectionRequestForIPv4Address(key, headerBuffer);
        } else if (headerBuffer[3] == UtilConsts.SocksConfiguration.HOSTNAME_ADDRESS) {
            validateConnectionRequestForHostnameAddress(key, headerBuffer, length);
        }
    }

    private void validateConnectionRequestForIPv4Address(SelectionKey key, byte[] headerBuffer) throws IOException {
        byte[] bytesOfIPAddress = new byte[] {
                headerBuffer[4],
                headerBuffer[5],
                headerBuffer[6],
                headerBuffer[7]
        };
        InetAddress ipAddress = InetAddress.getByAddress(bytesOfIPAddress);
        int parsedPort = ((headerBuffer[8] & 0xFF) << 8) + (headerBuffer[9] & 0xFF);
        configurePeerProxyUnit(key, ipAddress, parsedPort);
        key.interestOps(0);
    }

    private void configurePeerProxyUnit(SelectionKey originKey, InetAddress ipAddress, int parsedPort) throws IOException {
        LOGGER.info("Connecting to address {}:{}", ipAddress, parsedPort);
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(ipAddress, parsedPort));
        SelectionKey currentKey = channel.register(originKey.selector(), SelectionKey.OP_CONNECT);

        ((ProxyPeerUnit) originKey.attachment()).setPeerKey(currentKey);
        ProxyPeerUnit proxyPeerUnit = new ProxyPeerUnit();
        proxyPeerUnit.setPeerKey(originKey);
        currentKey.attach(proxyPeerUnit);
        ((ProxyPeerUnit) originKey.attachment()).getInput().clear();
    }

    private void validateConnectionRequestForHostnameAddress(SelectionKey key, byte[] headerBuffer, int length) throws IOException {
        byte lengthOfHostname = headerBuffer[4];
        int startPositionOfHostname = 5;
        if (length < lengthOfHostname + startPositionOfHostname + 2) {
            return;
        } else if (length > lengthOfHostname + startPositionOfHostname + 2) {
            throw new ProxyException("Invalid request for hostname address");
        }

        String currentHostname = new String((Arrays.copyOfRange(headerBuffer, startPositionOfHostname, lengthOfHostname + startPositionOfHostname)));
        int startPositionOfPort = lengthOfHostname + startPositionOfHostname;
        int parsedPort = ((headerBuffer[startPositionOfPort] & 0xFF) << 8) + (headerBuffer[startPositionOfPort + 1] & 0xFF);
        LOGGER.info("Host was resolved: {}:{}", currentHostname, parsedPort);
        key.interestOps(0);
        configureHostnamePeerProxyUnit(key, currentHostname, parsedPort);
    }

    private void configureHostnamePeerProxyUnit(SelectionKey key, String hostname, int parsedPort) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.connect(ResolverConfig.getCurrentConfig().server());
        channel.configureBlocking(false);

        SelectionKey currentKey = channel.register(key.selector(), SelectionKey.OP_WRITE);

        ProxyPeerUnit proxyPeerUnit = new ProxyPeerUnit();
        proxyPeerUnit.setRequestType(RequestType.REQUEST_DNS_WRITE);
        proxyPeerUnit.setPeerKey(key);
        proxyPeerUnit.setConnectionPort(parsedPort);
        proxyPeerUnit.setInput(ByteBuffer.allocate(UtilConsts.NetworkConsts.BUFFER_SIZE));

        Message message = new Message();
        Record record = Record.newRecord(Name.fromString(hostname + '.').canonicalize(), org.xbill.DNS.Type.A, DClass.IN);
        message.addRecord(record, Section.QUESTION);

        Header header = message.getHeader();
        header.setFlag(Flags.AD);
        header.setFlag(Flags.RD);

        proxyPeerUnit.getInput().put(message.toWire());
        proxyPeerUnit.getInput().flip();
        proxyPeerUnit.setOutput(proxyPeerUnit.getInput());
        currentKey.attach(proxyPeerUnit);
        LOGGER.info("Registered write to server DNS request");
    }

    private void handleWrite(SelectionKey key) throws IOException {
        ProxyPeerUnit proxyPeerUnit = (ProxyPeerUnit) key.attachment();
        if (proxyPeerUnit.getRequestType() == RequestType.REQUEST_DNS_WRITE) {
            handleDNSWrite(key);
        } else {
            handleNonDNSWrite(key);
        }
    }

    private void handleDNSWrite(SelectionKey key) throws IOException {
        ProxyPeerUnit proxyPeerUnit = (ProxyPeerUnit) key.attachment();
        DatagramChannel channel = (DatagramChannel) key.channel();
        if (channel.write(proxyPeerUnit.getOutput()) == -1) {
            cancelSelectionKey(key);
        } else if (proxyPeerUnit.getOutput().remaining() == 0) {
            LOGGER.info("Sent DNS request");
            proxyPeerUnit.getOutput().clear();
            proxyPeerUnit.setRequestType(RequestType.REQUEST_DNS_READ);
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
        }
    }

    private void handleNonDNSWrite(SelectionKey key) throws IOException {
        ProxyPeerUnit proxyPeerUnit = (ProxyPeerUnit) key.attachment();
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.write(proxyPeerUnit.getOutput()) == -1) {
            cancelSelectionKey(key);
        } else if (proxyPeerUnit.getOutput().remaining() == 0) {
            if (proxyPeerUnit.getRequestType() == RequestType.REQUEST_WRITE) {
                proxyPeerUnit.getOutput().clear();
                key.interestOps(SelectionKey.OP_READ);
                proxyPeerUnit.setRequestType(RequestType.REQUEST_COMMON_READ);
            } else if (Objects.isNull(proxyPeerUnit.getPeerKey())) {
                cancelSelectionKey(key);
            } else {
                proxyPeerUnit.getOutput().clear();
                proxyPeerUnit.getPeerKey().interestOps(proxyPeerUnit.getPeerKey().interestOps() | SelectionKey.OP_READ);
                key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
            }
        }
    }

    private void cancelSelectionKey(SelectionKey key) throws IOException {
        key.cancel();
        key.channel().close();

        SelectionKey peerKey = ((ProxyPeerUnit) key.attachment()).getPeerKey();
        if (Objects.nonNull(peerKey)) {
            ((ProxyPeerUnit) peerKey.attachment()).setPeerKey(null);
            if ((peerKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
                ((ProxyPeerUnit) peerKey.attachment()).getOutput().flip();
            }
            peerKey.interestOps(SelectionKey.OP_WRITE);
        }
    }
}
