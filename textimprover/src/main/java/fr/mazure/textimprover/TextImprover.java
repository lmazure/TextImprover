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
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel.MistralAiChatModelBuilder;
import dev.langchain4j.service.AiServices;

public class TextImprover {
    
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
                System.exit(ExitCode.FILE_ERROR.getCode());
            }
        }

        PrintStream error = System.err;
        if (parameters.errorFile().isPresent()) {
            try {
                error = new PrintStream(Files.newOutputStream(parameters.errorFile().get(), StandardOpenOption.CREATE_NEW));
            } catch (final IOException e) {
                System.err.println("Error: Unable to write error file: " + parameters.errorFile().get().toString());
                System.exit(ExitCode.FILE_ERROR.getCode());
            }
        }

        final Optional<SystemMessage> systemPrompt = parameters.sysPrompt().map(SystemMessage::new);

        final String answerWithName = perform(systemPrompt, parameters.userPrompt(), error, parameters.provider(), parameters.model(), parameters.apiKey(), parameters.temperature(), parameters.seed());

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
                                  final Provider provider,
                                  final Optional<String> modelName,
                                  final Optional<String> apiKey,
                                  final Optional<Double> temperature,
                                  final Optional<Integer> seed) {

        final ChatLanguageModel model =
            switch (provider) {
                case Provider.HUGGING_FACE -> buildHuggingFaceChatModel(modelName, apiKey, temperature);
                case Provider.MISTRAL_AI -> buildMistralAiChatModel(modelName, apiKey, temperature, seed);
            };
        
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
            System.exit(ExitCode.MODEL_ERROR.getCode());
            return null;
        }
    }

    private static ChatLanguageModel buildHuggingFaceChatModel(final Optional<String> modelName,
                                                               final Optional<String> apiKey,
                                                               final Optional<Double> temperature) {
        final HuggingFaceChatModel.Builder builder = HuggingFaceChatModel.builder();
        if (apiKey.isPresent()) {
            builder.accessToken(apiKey.get());
        }
        if (modelName.isPresent()) {
            builder.modelId(modelName.get());
        }
        if (temperature.isPresent()) {
            builder.temperature(temperature.get());
        }
        return builder.maxNewTokens(2048)  // TODO remove this
                      .build();
    }

    private static ChatLanguageModel buildMistralAiChatModel(final Optional<String> modelName,
                                                             final Optional<String> apiKey,
                                                             final Optional<Double> temperature,
                                                             final Optional<Integer> seed) {
        final MistralAiChatModelBuilder builder = MistralAiChatModel.builder();

        if (apiKey.isPresent()) {
            builder.apiKey(apiKey.get());
        }
        if (modelName.isPresent()) {
            builder.modelName(modelName.get());
        }
        if (temperature.isPresent()) {  
            builder.temperature(temperature.get());
        }
        if (seed.isPresent()) {
            builder.randomSeed(seed.get());
        }

        return builder.build();
    }
}
