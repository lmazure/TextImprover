package fr.mazure.textimprover;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandLine {
    
    public record Parameters(Optional<String> sysPrompt,
                             String userPrompt,
                             Optional<Path> outputFile,
                             Optional<Path> errorFile,
                             Provider provider,
                             Optional<String> model,
                             Optional<String> apiKey) {}

    public static Parameters parseCommandLine(final String[] args) {
        String sysPrompt = null;
        String userPrompt = null;
        Path outputFile = null;
        Path errorFile = null;
        Provider provider = null;
        String model = null;
        String apiKey = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--system-prompt-string")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --system-prompt-string");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                if (Objects.nonNull(sysPrompt)) {
                    System.err.println("System prompt already set");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                sysPrompt = args[i + 1];
                i++;
                continue;
            }
            if (args[i].equals("--user-prompt-string")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --user-prompt-string");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                if (Objects.nonNull(userPrompt)) {
                    System.err.println("User prompt already set");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                userPrompt = args[i + 1];
                i++;
                continue;
            }
            if (args[i].equals("--system-prompt-file")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --system-prompt-file");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                if (Objects.nonNull(sysPrompt)) {
                    System.err.println("System prompt already set");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                sysPrompt = slurpFile(Paths.get(args[i + 1]));
                i++;
                continue;
            }
            if (args[i].equals("--user-prompt-file")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --user-prompt-file");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                if (Objects.nonNull(userPrompt)) {
                    System.err.println("User prompt already set");
                    displayHelpAndExit(i);
                }
                userPrompt = slurpFile(Paths.get( args[i + 1]));
                i++;
                continue;
            }
            if (args[i].equals("--output-file")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --output-file");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                outputFile = Paths.get(args[i + 1]);
                i++;
                continue;
            }
            if (args[i].equals("--error-file")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --error-file");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                errorFile = Paths.get(args[i + 1]);
                i++;
                continue;
            }
            if (args[i].equals("--provider")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --provider");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                try {
                    provider = Provider.fromString(args[i + 1]);
                } catch (final IllegalArgumentException e) {
                    System.err.println("Unknown provider: " + args[i + 1]);
                    displayHelpAndExit(1);
                }
                i++;
                continue;
            }
            if (args[i].equals("--model")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --model");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                model = args[i + 1];
                i++;
                continue;
            }
            if (args[i].equals("--api-key")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --api-key");
                    System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
                }
                apiKey = args[i + 1];
                i++;
                continue;
            }
            if (args[i].equals("--help")) {
                System.exit(ExitCode.INVALID_COMMAND_LINE.getCode());
            }
            System.err.println("Unknown argument: " + args[i]);
            displayHelpAndExit(1);
        }
        if (Objects.isNull(userPrompt)) {
            System.err.println("Missing user prompt");
            displayHelpAndExit(1);
        }
        if (Objects.isNull(provider)) {
            System.err.println("Missing provider");
            displayHelpAndExit(1);
        }
        return new Parameters(Optional.ofNullable(sysPrompt), userPrompt, Optional.ofNullable(outputFile), Optional.ofNullable(errorFile), provider, Optional.ofNullable(model), Optional.ofNullable(apiKey));
    }

    private static void displayHelpAndExit(final int exitCode) {
        final String executableName = "textimprover.jar";
        System.err.println("Usage: java -jar " +
                           executableName +
                           " {--user-prompt-string <user-prompt-string>|--user-prompt-file <user-prompt-file>}\n" +
                           "    [--system-prompt-string <system-prompt-string>]  [--system-prompt-file <system-prompt-file>]\n" +
                           "    [--model <model>] [--api-key <api-key>] [--help]");
        System.err.println(
            """
            --system-prompt-string <system-prompt-string> system prompt as a string
            --system-prompt-file <system-prompt-file>     system prompt as the content of a file
            --user-prompt-string <user-prompt-string>     user prompt as a string
            --user-prompt-file <user-prompt-file>         user prompt as the content of a file
            --output-file output-file>                    output file (stdout by default)
            --error-file error-file>                      error file (stderr by default)
            --provider <provider>                         provider
            --model <model>                               model name
            --api-key <api-key>                           api key
            --help
            """
        );
        System.err.println("Available providers: " + Arrays.stream(Provider.values()).map(Enum::toString).collect(Collectors.joining(", ")));
        // TODO add temperature, maxtokens, randomseed
        System.exit(exitCode);
    }

    private static String slurpFile(final Path path)  {
        try {
            return Files.readString(path);
        }
        catch (final IOException e) {
            System.err.println("Error: Unable to read file: " + path);
            System.exit(ExitCode.FILE_ERROR.getCode());
            return null;
        }
    }
}
