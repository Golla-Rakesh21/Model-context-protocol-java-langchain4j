package com.learning;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class Main4 {

    //This if for only sending same relpy to the telegram bot , but to make it continues runs use while loop(true)
    //YOu need to create a bot in telegram and copy its token to send message.
    //this code will send the hi message to the bot repeatdly.
    public static void main(String[] args) {

        String botToken = "GET_YOUR_TELEGRAM_BOT_TOKEN";
        String api = "https://api.telegram.org/bot" + botToken;
        String googleApiKey = System.getenv("GOOGLE_API_KEY");

        GoogleGenAiChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(googleApiKey)
                .logRequestsAndResponses(false)
                .modelName("gemini-3.5-flash-lite")
                .build();

        McpTransport telegramTransport = StdioMcpTransport.builder()
                .command(List.of("cmd", "/c", "npx", "-y",
                        "@deus-ai/telegram-mcp"))   // Windows: "npx.cmd"
                .environment(Map.of("TELEGRAM_BOT_TOKEN", botToken))
                .logEvents(true)   // helpful while testing
                .build();

        McpClient telegramClient = DefaultMcpClient.builder()
                .key("telegram")
                .transport(telegramTransport)
                .build();

        TelegramAssistant assistant = AiServices.builder(TelegramAssistant.class)
                .chatModel(model)
                .toolProvider(McpToolProvider.builder().mcpClients(telegramClient).build())
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.failInvocationUnlessVisibleToLlm())
                .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.sendExceptionMessageToLlm())
                .build();
        while(true) {
            System.out.println(assistant.chat("Send  a Telegram bot message telling hi in reply"));
        }

    }
}
