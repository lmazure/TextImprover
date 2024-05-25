package fr.mazure.textimprover;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class TextImprover {

    private static final int SUCCESS = 0;
    private static final int INVALID_COMMAND_LINE = 1;
    private static final int FILE_NOT_FOUND = 2;
    
    interface Assistant {
        String chat(String userMessage);
    }

    record Input(String sysPrompt, String userPrompt) {}

    public static void main(final String[] args) {
        final Input input = parseCommandLine(args);

        final SystemMessage systemPrompt = Objects.isNull(input.sysPrompt) ? null : new SystemMessage(input.sysPrompt);

        final String answerWithName = perform(systemPrompt, input.userPrompt);
        System.out.println(answerWithName);
    }

    private static Input parseCommandLine(final String[] args) {
        String sysPrompt = null;
        String userPrompt = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--system-prompt-string")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --system-prompt-string");
                    System.exit(INVALID_COMMAND_LINE);
                }
                if (Objects.nonNull(sysPrompt)) {
                    System.err.println("System prompt already set");
                    System.exit(INVALID_COMMAND_LINE);
                }
                sysPrompt = args[i + 1];
                i++;
                continue;
            }
            if (args[i].equals("--user-prompt-string")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --user-prompt-string");
                    System.exit(INVALID_COMMAND_LINE);
                }
                if (Objects.nonNull(userPrompt)) {
                    System.err.println("User prompt already set");
                    System.exit(INVALID_COMMAND_LINE);
                }
                userPrompt = args[i + 1];
                i++;
                continue;
            }
            if (args[i].equals("--system-prompt-file")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --system-prompt-file");
                    System.exit(INVALID_COMMAND_LINE);
                }
                if (Objects.nonNull(sysPrompt)) {
                    System.err.println("System prompt already set");
                    System.exit(INVALID_COMMAND_LINE);
                }
                sysPrompt = slurpFile(args[i + 1]);
                i++;
                continue;
            }
            if (args[i].equals("--user-prompt-file")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --user-prompt-file");
                    System.exit(INVALID_COMMAND_LINE);
                }
                if (Objects.nonNull(userPrompt)) {
                    System.err.println("User prompt already set");
                    displayHelpAndExit(i);
                }
                userPrompt = slurpFile(args[i + 1]);
                i++;
                continue;
            }
            if (args[i].equals("--help")) {
                System.exit(SUCCESS);
            }
            System.err.println("Unknown argument: " + args[i]);
            displayHelpAndExit(1);
        }
        if (Objects.isNull(userPrompt)) {
            System.err.println("Missing user prompt");
            displayHelpAndExit(1);
        }
        return new Input(sysPrompt, userPrompt);
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
            System.exit(FILE_NOT_FOUND);
            return null;
        }
    }

    private static String perform(final SystemMessage systemPrompt,
                                  final String message) {
        final OpenAiChatModel model = OpenAiChatModel.withApiKey("demo");
        final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(2);

        if (!Objects.isNull(systemPrompt)) {
            memory.add(systemPrompt);
        }

        final Assistant assistant = AiServices.builder(Assistant.class)
                                              .chatLanguageModel(model)
                                              .chatMemory(memory)
                                              .build();

        final String answerWithName = assistant.chat(message);
        return answerWithName;
    }
}
