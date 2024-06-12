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

        final String answerWithName = perform(systemPrompt, parameters.userPrompt(), error, parameters.provider(), parameters.project(), parameters.model(), parameters.apiKey(), parameters.temperature(), parameters.seed());

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
                                  final ProviderEnum provider,
                                  final Optional<String> project,
                                  final Optional<String> modelName,
                                  final Optional<String> apiKey,
                                  final Optional<Double> temperature,
                                  final Optional<Integer> seed) {

        final IChatLanguageModelBuilder builder =
            switch (provider) {
                case ProviderEnum.HUGGING_FACE -> new HuggingFaceChatLanguageModelBuilder();
                case ProviderEnum.MISTRAL_AI -> new MistralAiChatLanguageModelBuilder();
                case ProviderEnum.VERTEX_AI_GEMINI -> new VertexAiGeminiChatLanguageModelBuilder();
            };
        
        final ChatLanguageModel model = builder.build(modelName, project, apiKey, temperature, seed);

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
}
