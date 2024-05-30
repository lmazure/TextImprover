package fr.mazure.textimprover;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
//import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class TextImprover {

    public static final int SUCCESS = 0;
    public static final int INVALID_COMMAND_LINE = 1;
    public static final int FILE_ERROR = 2;
    public static final int MODEL_ERROR = 3;
    
    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(final String[] args) {
        final CommandLine.Parameters parameters = CommandLine.parseCommandLine(args);

        PrintStream output = System.out;
        if (parameters.outputFile().isPresent()) {
            try {
                output = new PrintStream(Files.newOutputStream(parameters.outputFile().get(), StandardOpenOption.CREATE_NEW));
            } catch (final IOException e) {
                System.err.println("Error: Unable to write output file: " + parameters.outputFile().get().toString());
                System.exit(FILE_ERROR);
            }
        }

        PrintStream error = System.err;
        if (parameters.errorFile().isPresent()) {
            try {
                error = new PrintStream(Files.newOutputStream(parameters.errorFile().get(), StandardOpenOption.CREATE_NEW));
            } catch (final IOException e) {
                System.err.println("Error: Unable to write error file: " + parameters.errorFile().get().toString());
                System.exit(FILE_ERROR);
            }
        }

        final Optional<SystemMessage> systemPrompt = parameters.sysPrompt().map(SystemMessage::new);

        final String answerWithName = perform(systemPrompt, parameters.userPrompt(), error, parameters.model(), parameters.apiKey());

        output.println(answerWithName);

        if (output != System.out) {
            output.close();
        }
        if (error != System.err) {
            error.close();
        }
    }

    private static String perform(final Optional<SystemMessage> systemPrompt,
                                  final String message,
                                  final PrintStream error,
                                  final Optional<String> modelName,
                                  final Optional<String> apiKey) {
        //final ChatLanguageModel model = OpenAiChatModel.withApiKey("demo");
        final ChatLanguageModel model = HuggingFaceChatModel.builder()
                                                            .accessToken(apiKey.get())
                                                            .modelId(modelName.get())
                                                            .maxNewTokens(1024)
                                                            .build();
        final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(2);

        if (systemPrompt.isPresent()) {
            memory.add(systemPrompt.get());
        }

        final Assistant assistant = AiServices.builder(Assistant.class)
                                              .chatLanguageModel(model)
                                              .chatMemory(memory)
                                              .build();

        try {
            return assistant.chat(message);
        } catch (final RuntimeException e) {
            error.println("Model failure");
            e.printStackTrace(error);
            System.exit(MODEL_ERROR);
            return null;
        }
    }
}
