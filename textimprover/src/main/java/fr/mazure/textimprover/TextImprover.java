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

        final OpenAiChatModel model = OpenAiChatModel.withApiKey("demo");
        final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(2);

        final SystemMessage systemPrompt = new SystemMessage("You will be provided an English text. You have to translate it into French. You should not add any other text.");
        final String message = "Your case is typical and you are right to fight for compensation.";

        memory.add(systemPrompt);
        final Assistant assistant = AiServices.builder(Assistant.class)
                                              .chatLanguageModel(model)
                                              .chatMemory(memory)
                                              .build();

        final String answerWithName = assistant.chat(message);
        System.out.println(answerWithName);
    }
}
