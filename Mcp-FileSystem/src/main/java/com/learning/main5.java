

package com.learning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class main5 {
    //This code connects with the telegram and DuckDuckGo browser and answers the questions asked in the telegram bot through telegram bot.

    private static final ObjectMapper JSON = new ObjectMapper();

    // One message at a time, and never blocks the MCP thread
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor();

    interface BotAssistant {
        @SystemMessage("""
            You are a helpful Telegram assistant.
            For any question about current or factual information (prices, news, scores, weather, etc.),
            ALWAYS use the DuckDuckGo search tool first, then answer from the results.
            When you have the final answer, you MUST send it to the user with the Telegram
            send-message tool, using the chat_id exactly as given (including any prefix like "tg:").
            Keep the answer short and clear, and mention the currency/unit for prices.
            """)
        @UserMessage("Telegram chat_id (use EXACTLY as given): {{chatId}}\nUser question: {{text}}")
        String handle(@V("chatId") String chatId, @V("text") String text);
    }

    public static void main(String[] args) throws Exception {

        // 1. Credentials from environment variables (never hardcode them)
        String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
        String googleApiKey = System.getenv("GOOGLE_API_KEY");

        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException(
                    "TELEGRAM_BOT_TOKEN environment variable is missing"
            );
        }

        if (googleApiKey == null || googleApiKey.isBlank()) {
            throw new IllegalStateException(
                    "GOOGLE_API_KEY environment variable is missing"
            );
        }

        // 2. Gemini model
        // NOTE: enableGoogleSearch(true) removed. Gemini often can't combine built-in
        // Google Search with function calling (MCP tools), and DuckDuckGo handles search here.
        GoogleGenAiChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(googleApiKey)
                .logRequestsAndResponses(false)
                .modelName("gemini-3.5-flash-lite")
                .build();

        // The assistant is created after the Telegram client, but the handler needs it,
        // so we hold it in an array.
        final BotAssistant[] holder = new BotAssistant[1];

        // 3. MCP 1: Telegram (receives messages via log events, sends replies via tools)
        McpClient telegramClient = DefaultMcpClient.builder()
                .key("telegram")
                .transport(StdioMcpTransport.builder()
                        .command(List.of("cmd", "/c", "npx", "-y", "@deus-ai/telegram-mcp"))
                        .environment(Map.of("TELEGRAM_BOT_TOKEN", botToken))
                        .logEvents(true)
                        .build())
                .logHandler(msg -> {
                    if (!"incoming_message".equals(msg.logger())) return;
                    try {
                        JsonNode m = JSON.readTree(msg.dataAsJson());
                        if (m.isTextual()) m = JSON.readTree(m.asText()); // in case it's double-encoded

                        if (m.path("is_from_me").asBoolean(false)) return;

                        String chatId = m.path("chat_id").asText();   // e.g. "tg:7925450736"
                        String text = m.path("content").asText();
                        if (chatId.isBlank() || text.isBlank()) return;

                        System.out.println("User (" + chatId + "): " + text);

                        WORKER.submit(() -> {
                            try {
                                String result = holder[0].handle(chatId, text);
                                System.out.println("Done: " + result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .build();

        // 4. MCP 2: DuckDuckGo web search (needs `uv` installed)
        McpClient duckDuckGoClient = DefaultMcpClient.builder()
                .key("web-search")
                .transport(StdioMcpTransport.builder()
                        .command(List.of("cmd", "/c", "uvx", "duckduckgo-mcp-server"))
                        .logEvents(false)
                        .build())
                .build();

        // Print real tool names once, so you can verify the prompt matches them
        telegramClient.listTools().forEach(t -> System.out.println("TG tool: " + t.name()));
        duckDuckGoClient.listTools().forEach(t -> System.out.println("DDG tool: " + t.name()));

        // 5. Assistant with both MCPs as tools
        holder[0] = AiServices.builder(BotAssistant.class)
                .chatModel(model)
                .toolProvider(McpToolProvider.builder()
                        .mcpClients(telegramClient, duckDuckGoClient)
                        .build())
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.failInvocationUnlessVisibleToLlm())
                .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.sendExceptionMessageToLlm())
                .build();

        System.out.println("Bot is listening...");

        // 6. Keep the app alive; messages arrive through the log handler
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            WORKER.shutdownNow();
            try {
                telegramClient.close();
                duckDuckGoClient.close();
            } catch (Exception ignored) {
            }
        }));
        Thread.currentThread().join();
    }
}
