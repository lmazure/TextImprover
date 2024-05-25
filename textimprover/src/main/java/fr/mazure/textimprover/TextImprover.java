package fr.mazure.textimprover;

import java.util.Optional;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class TextImprover {

    public static final int SUCCESS = 0;
    public static final int INVALID_COMMAND_LINE = 1;
    public static final int FILE_NOT_FOUND = 2;
    
    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(final String[] args) {
        final CommandLine.Input input = CommandLine.parseCommandLine(args);

        final Optional<SystemMessage> systemPrompt = input.sysPrompt().map(SystemMessage::new);

        final String answerWithName = perform(systemPrompt, input.userPrompt());
        System.out.println(answerWithName);
    }


    private static String perform(final Optional<SystemMessage> systemPrompt,
                                  final String message) {
        final OpenAiChatModel model = OpenAiChatModel.withApiKey("demo");
        final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(2);

        if (systemPrompt.isPresent()) {
            memory.add(systemPrompt.get());
        }

        final Assistant assistant = AiServices.builder(Assistant.class)
                                              .chatLanguageModel(model)
                                              .chatMemory(memory)
                                              .build();

        final String answerWithName = assistant.chat(message);
        return answerWithName;
    }
}
