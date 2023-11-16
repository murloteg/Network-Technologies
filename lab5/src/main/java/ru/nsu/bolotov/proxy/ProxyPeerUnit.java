package ru.nsu.bolotov.proxy;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

@Data
@NoArgsConstructor
public class ProxyPeerUnit {
    private int connectionPort;
    private ByteBuffer input;
    private ByteBuffer output;
    private SelectionKey peerKey;
    private RequestType requestType;
}
