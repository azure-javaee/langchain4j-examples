package dev.langchain4j.example.chat;

import static java.time.Duration.ofSeconds;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@ApplicationScoped
public class ChatAgent {

    private static Logger logger = Logger.getLogger(ChatAgent.class.getName());    
    
    @Inject
    @ConfigProperty(name = "azure.openai.api.key")
    private String AZURE_OPENAI_API_KEY;
    
    @Inject
    @ConfigProperty(name = "azure.openai.deployment.name")
    private String AZURE_OPENAI_DEPLOYMENT_NAME;

    @Inject
    @ConfigProperty(name = "azure.openai.endpoint")
    private String AZURE_OPENAI_ENDPOINT;

    @Inject
    @ConfigProperty(name = "azure.openai.api.key")
    private String AZURE_OPENAI_API_KEY;
    
    @Inject
    @ConfigProperty(name = "azure.openai.deployment.name")
    private String AZURE_OPENAI_DEPLOYMENT_NAME;

    @Inject
    @ConfigProperty(name = "azure.openai.endpoint")
    private String AZURE_OPENAI_ENDPOINT;

    @Inject
    @ConfigProperty(name = "hugging.face.api.key")
    private String HUGGING_FACE_API_KEY;

    @Inject
    @ConfigProperty(name = "chat.model.id")
    private String CHAT_MODEL_ID;

    @Inject
    @ConfigProperty(name = "chat.model.timeout")
    private Integer TIMEOUT;

    @Inject
    @ConfigProperty(name = "chat.model.max.token")
    private Integer MAX_NEW_TOKEN;

    @Inject
    @ConfigProperty(name = "chat.model.temperature")
    private Double TEMPERATURE;

    @Inject
    @ConfigProperty(name = "chat.memory.max.messages")
    private Integer MAX_MESSAGES;

    interface Assistant {
       String chat(@MemoryId String sessionId, @UserMessage String userMessage);
    }

    private Assistant assistant = null;

    public Assistant getAssistant() {
        if (assistant == null) {
            HuggingFaceChatModel model = HuggingFaceChatModel.builder()
                .accessToken(HUGGING_FACE_API_KEY)
                .modelId(CHAT_MODEL_ID)
                .timeout(ofSeconds(TIMEOUT))
                .temperature(TEMPERATURE)
                .maxNewTokens(MAX_NEW_TOKEN)
                .waitForModel(true)
                .build();
            assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(
                    sessionId -> MessageWindowChatMemory.withMaxMessages(MAX_MESSAGES))
                .build();
        }
        if (!isOnline()) {
            logger.severe("Unable to connect to model. Trying Azure OpenAI.");
            AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
                .apiKey(AZURE_OPENAI_API_KEY)
                .deploymentName(AZURE_OPENAI_DEPLOYMENT_NAME)
                .endpoint(AZURE_OPENAI_ENDPOINT)
                .timeout(ofSeconds(TIMEOUT))
                .temperature(TEMPERATURE)
                .maxTokens(MAX_NEW_TOKEN)
                .build();
            assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(
                                    sessionId -> MessageWindowChatMemory.withMaxMessages(MAX_MESSAGES))
                .build();
            if (!isOnline()) {
                logger.severe("Unable to connect to model. Cannot operate.");
                assistant = null;
            }
            
        }
<<<<<<< HEAD
        if (!isOnline()) {
            logger.severe("Unable to connect to model. Trying Azure OpenAI.");
            AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
                .apiKey(AZURE_OPENAI_API_KEY)
                .deploymentName(AZURE_OPENAI_DEPLOYMENT_NAME)
                .endpoint(AZURE_OPENAI_ENDPOINT)
                .timeout(ofSeconds(TIMEOUT))
                .temperature(TEMPERATURE)
                .maxTokens(MAX_NEW_TOKEN)
                .build();
            assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(
                                    sessionId -> MessageWindowChatMemory.withMaxMessages(MAX_MESSAGES))
                .build();
            if (!isOnline()) {
                logger.severe("Unable to connect to model. Cannot operate.");
                assistant = null;
            }
            
        }
=======
>>>>>>> 1e0e63f (On branch edburns-msft-ibm-781 Re-add Hugging Face as primary choice.)
        
        return assistant;
    }

    private boolean isOnline() {
        boolean isOnline = true;
        try {
            String sessionId = "" + System.currentTimeMillis();
            assistant.chat(sessionId, "What is MicroProfile?");
        } catch (Exception e) {
            isOnline = false;
        }
        return isOnline;
    }

    public String chat(String sessionId, String message) {
        return getAssistant().chat(sessionId, message).trim();
    }

}
