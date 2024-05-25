package fr.mazure.textimprover;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class TextImprover {

    interface Assistant {

        String chat(String userMessage);
    }

    public static void main(final String[] args) {
        String sysPrompt = "You are a helpful assistant.";
        String userPrompt = "Hello, how are you?";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--system-prompt-string")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --system-prompt-string");
                    System.exit(1);
                }
                sysPrompt = args[i + 1];
                i++;
            } else if (args[i].equals("--user-prompt-string")) {
                if ((i + 1 ) >= args.length) {
                    System.err.println("Missing argument for --user-prompt-string");
                    displayHelpAndExit(1);
                }
                userPrompt = args[i + 1];
                i++;
            } else if (args[i].equals("--help")) {
                displayHelpAndExit(0);
            } else {
                System.err.println("Unknown argument: " + args[i]);
                displayHelpAndExit(1);
            }
        }

        final SystemMessage systemPrompt = new SystemMessage(sysPrompt);

        final String answerWithName = perform(systemPrompt, userPrompt);
        System.out.println(answerWithName);
    }

    private static void displayHelpAndExit(final int exitCode) {
        final String executableName = "textimprover.jar";
        System.err.println("Usage: java -jar " + executableName + " --system-prompt-string <system-prompt-string> --user-prompt-string <user-prompt-string>");
        System.exit(exitCode);
    }

    private static String perform(final SystemMessage systemPrompt, final String message) {
        final OpenAiChatModel model = OpenAiChatModel.withApiKey("demo");
        final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(2);

        memory.add(systemPrompt);
        final Assistant assistant = AiServices.builder(Assistant.class)
                                              .chatLanguageModel(model)
                                              .chatMemory(memory)
                                              .build();

        final String answerWithName = assistant.chat(message);
        return answerWithName;
    }
}
