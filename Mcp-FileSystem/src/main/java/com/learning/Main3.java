package com.learning;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;

import java.util.List;
import java.util.Map;

public class Main3 {

    public static void main(String args[]){
        String googleApiKey = System.getenv("GOOGLE_API_KEY");
        GoogleGenAiChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(googleApiKey)
                .logRequestsAndResponses(false)
                .modelName("gemini-3.5-flash-lite")
                .build();

        String githubToken = "SET_YOUR_GITHUB_PERSONAL_ASSESS_TOKEN";
        if (githubToken == null || githubToken.isBlank()) {
            throw new IllegalStateException(
                    "GITHUB_PAT environment variable is missing"
            );
        }

        StreamableHttpMcpTransport githubTransport = StreamableHttpMcpTransport.builder()
                .customHeaders(Map.of(
                        "Authorization",
                        "Bearer " + githubToken,

                        "X-MCP-Toolsets",
                        "context,repos,issues,pull_requests",

                        "X-MCP-Readonly",
                        "true"
                ))
                .url("https://api.githubcopilot.com/mcp/")
                .logRequests(false)
                .logResponses(false)
                .build();
        DefaultMcpClient githubclient = DefaultMcpClient.builder()
                .key("gtihub-Mcp")
                .transport(githubTransport)
                .build();

        //duckDuckGO
        StdioMcpTransport duckduckgotransport = StdioMcpTransport.builder()
                .command(List.of("uvx", "duckduckgo-mcp-server"))   // Windows: "uvx.exe" or "cmd","/c","uvx",...
                .logEvents(false)
                .build();
        DefaultMcpClient duckduckGoClient = DefaultMcpClient.builder()
                .transport(duckduckgotransport)
                .key("web-search")
                .build();




        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .toolProvider(McpToolProvider.builder().mcpClients(githubclient, duckduckGoClient).build())
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.failInvocationUnlessVisibleToLlm())
                .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.sendExceptionMessageToLlm())
                .build();

//        System.out.println(assistant.chat("List all my repositories"));
        System.out.println(assistant.chat("what is current gold price"));
//        System.out.println(assistant.chat(
//                "Find my most recently updated repo, then search the web for how to improve its README"));

    }
}
