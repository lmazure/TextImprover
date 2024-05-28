package fr.mazure.textimprover;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

public class CommandLine {
    
    public record Input(Optional<String> sysPrompt, String userPrompt, Optional<String> outputFile) {}

    public static Input parseCommandLine(final String[] args) {
        String sysPrompt = null;
        String userPrompt = null;
        String outputFile = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--system-prompt-string")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --system-prompt-string");
                    System.exit(TextImprover.INVALID_COMMAND_LINE);
                }
                if (Objects.nonNull(sysPrompt)) {
                    System.err.println("System prompt already set");
                    System.exit(TextImprover.INVALID_COMMAND_LINE);
                }
                sysPrompt = args[i + 1];
                i++;
                continue;
            }
            if (args[i].equals("--user-prompt-string")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --user-prompt-string");
                    System.exit(TextImprover.INVALID_COMMAND_LINE);
                }
                if (Objects.nonNull(userPrompt)) {
                    System.err.println("User prompt already set");
                    System.exit(TextImprover.INVALID_COMMAND_LINE);
                }
                userPrompt = args[i + 1];
                i++;
                continue;
            }
            if (args[i].equals("--system-prompt-file")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --system-prompt-file");
                    System.exit(TextImprover.INVALID_COMMAND_LINE);
                }
                if (Objects.nonNull(sysPrompt)) {
                    System.err.println("System prompt already set");
                    System.exit(TextImprover.INVALID_COMMAND_LINE);
                }
                sysPrompt = slurpFile(args[i + 1]);
                i++;
                continue;
            }
            if (args[i].equals("--user-prompt-file")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --user-prompt-file");
                    System.exit(TextImprover.INVALID_COMMAND_LINE);
                }
                if (Objects.nonNull(userPrompt)) {
                    System.err.println("User prompt already set");
                    displayHelpAndExit(i);
                }
                userPrompt = slurpFile(args[i + 1]);
                i++;
                continue;
            }
            if (args[i].equals("--output-file")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --output-file");
                    System.exit(TextImprover.INVALID_COMMAND_LINE);
                }
                outputFile = args[i + 1];
                i++;
                continue;
            }
            if (args[i].equals("--help")) {
                System.exit(TextImprover.SUCCESS);
            }
            System.err.println("Unknown argument: " + args[i]);
            displayHelpAndExit(1);
        }
        if (Objects.isNull(userPrompt)) {
            System.err.println("Missing user prompt");
            displayHelpAndExit(1);
        }
        return new Input(Optional.ofNullable(sysPrompt), userPrompt, Optional.ofNullable(outputFile));
    }

    private static void displayHelpAndExit(final int exitCode) {
        final String executableName = "textimprover.jar";
        System.err.println("Usage: java -jar " +
                           executableName +
                           " {--user-prompt-string <user-prompt-string>|--user-prompt-file <user-prompt-file>} [--system-prompt-string <system-prompt-string>]  [--system-prompt-file <system-prompt-file>] [--help]");
        System.err.println(
            """
            --system-prompt-string <system-prompt-string> system prompt as a string
            --system-prompt-file <system-prompt-file>     system prompt as the content of a file
            --user-prompt-string <user-prompt-string>     user prompt as a string
            --user-prompt-file <user-prompt-file>         user prompt as the content of a file
            --output-file output-file>                    output file (stdout by default)
            --help
            """
        );
        System.exit(exitCode);
    }

    private static String slurpFile(final String path)  {
        try {
            return Files.readString(Paths.get(path));
        }
        catch (final IOException e) {
            System.err.println("Error: Unable to read file: " + path);
            System.exit(TextImprover.FILE_ERROR);
            return null;
        }
    }
}
