package ru.nsu.bolotov.parser;

import org.apache.commons.cli.*;
import ru.nsu.bolotov.exception.FailedParsingException;
import ru.nsu.bolotov.model.InputParameters;
import ru.nsu.bolotov.util.UtilConsts;

public final class CmdLineParser {
    public static InputParameters parseCmdLine(String[] args) {
        CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(getOptions(), args);
        } catch (ParseException exception) {
            throw new FailedParsingException();
        }
        if (commandLine.hasOption("p")) {
            return new InputParameters(Integer.parseInt(commandLine.getOptionValue("p")));
        } else {
            throw new FailedParsingException();
        }
    }

    private static Options getOptions() {
        Option portOption = new Option("p", "port", true, "This option presents port");
        portOption.setRequired(true);

        Options options = new Options();
        options.addOption(portOption);
        return options;
    }

    private CmdLineParser() {
        throw new IllegalStateException(UtilConsts.StringConsts.INSTANTIATION_MESSAGE);
    }
}
