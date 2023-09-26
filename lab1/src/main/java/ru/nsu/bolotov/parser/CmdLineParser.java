package ru.nsu.bolotov.parser;

import org.apache.commons.cli.*;
import ru.nsu.bolotov.exception.FailedCmdLineParseException;
import ru.nsu.bolotov.model.InputData;
import ru.nsu.bolotov.utils.UtilConsts;

public final class CmdLineParser {
    public static InputData parseInputData(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = parser.parse(getOptions(), args);
        } catch (ParseException exception) {
            throw new FailedCmdLineParseException();
        }
        if (commandLine.hasOption("i") && commandLine.hasOption("p")) {
            return new InputData(commandLine.getOptionValue("i"), Integer.parseInt(commandLine.getOptionValue("p")));
        } else {
            throw new FailedCmdLineParseException();
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        Option ipAddrOption = new Option("i", "ip", true, "This option presents IP-address");
        Option portOption = new Option("p", "port", true, "This option presents port");
        ipAddrOption.setRequired(true);
        portOption.setRequired(true);

        options.addOption(ipAddrOption);
        options.addOption(portOption);
        return options;
    }

    private CmdLineParser() {
        throw new IllegalStateException(UtilConsts.StringConsts.INSTANTIATION_MESSAGE);
    }
}
