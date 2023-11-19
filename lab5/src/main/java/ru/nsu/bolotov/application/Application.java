package ru.nsu.bolotov.application;

import ru.nsu.bolotov.model.InputParameters;
import ru.nsu.bolotov.parser.CmdLineParser;
import ru.nsu.bolotov.proxy.SocksProxyServer;
import ru.nsu.bolotov.util.UtilConsts;

public class Application {
    public static void main(String[] args) {
        InputParameters inputParameters = CmdLineParser.parseCmdLine(args);
        SocksProxyServer proxyServer = new SocksProxyServer(UtilConsts.NetworkConsts.HOSTNAME, inputParameters.port());
        Thread proxyThread = new Thread(proxyServer);
        proxyThread.start();
    }
}
