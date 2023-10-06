package ru.nsu.bolotov.parser;

import org.apache.commons.cli.*;
import ru.nsu.bolotov.exception.FailedParsingException;
import ru.nsu.bolotov.model.ClientInputData;
import ru.nsu.bolotov.model.ServerInputData;
import ru.nsu.bolotov.utils.UtilConsts;

public final class CmdLineParser {
    public static ServerInputData parseCmdLineForServer(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(getOptionsForServerCmdLine(), args);
        } catch (ParseException exception) {
            throw new FailedParsingException();
        }
        if (commandLine.hasOption("p")) {
            return new ServerInputData(Integer.parseInt(commandLine.getOptionValue("p")));
        } else {
            throw new FailedParsingException();
        }
    }

    public static ClientInputData parseCmdLineForClient(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(getOptionsForClientCmdLine(), args);
        } catch (ParseException exception) {
            throw new FailedParsingException();
        }
        if (commandLine.hasOption("i") && commandLine.hasOption("p") && commandLine.hasOption("f")) {
            return new ClientInputData(
                    commandLine.getOptionValue("i"),
                    Integer.parseInt(commandLine.getOptionValue("p")),
                    commandLine.getOptionValue("f")
            );
        } else {
            throw new FailedParsingException();
        }
    }

    private static Options getOptionsForServerCmdLine() {
        Options options = new Options();
        Option portOption = new Option("p", "port", true, "Port number for server");
        portOption.setRequired(true);
        options.addOption(portOption);
        return options;
    }

    private static Options getOptionsForClientCmdLine() {
        Options options = new Options();
        Option ipAddrOption = new Option("i", "ip", true, "Server IP-address for client");
        Option portOption = new Option("p", "port", true, "Port number for server");
        Option pathOption = new Option("f", "file_path", true, "Absolute path to file");

        ipAddrOption.setRequired(true);
        portOption.setRequired(true);
        pathOption.setRequired(true);
        options.addOption(ipAddrOption);
        options.addOption(portOption);
        options.addOption(pathOption);
        return options;
    }

    private CmdLineParser() {
        throw new IllegalStateException(UtilConsts.StringConsts.INSTANTIATION_MESSAGE);
    }
}
