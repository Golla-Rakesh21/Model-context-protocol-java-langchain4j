package com.learning;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;

import java.util.Map;

public class Main2 {
    public static void main(String args[]){

//        OpenAiChatModel model = OpenAiChatModel.builder()
//           //     .apiKey(System.getenv("OPENAI_API_KEY"))
//                .apiKey("")
//                .modelName("gpt-4o-mini")
//                .logRequests(false)
//                .logResponses(false)
//                .build();

        String googleApiKey = System.getenv("GOOGLE_API_KEY");
        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(googleApiKey)
                .modelName("gemini-3.5-flash-lite")
                .logRequestsAndResponses(false)
                .build();

        String githubToken = "SET_YOUR_GITHUB_PERSONAL_ASSESS_TOKEN";
        if (githubToken == null || githubToken.isBlank()) {
            throw new IllegalStateException(
                    "GITHUB_PAT environment variable is missing"
            );
        }
        StreamableHttpMcpTransport transport = StreamableHttpMcpTransport.builder()
                .url("https://api.githubcopilot.com/mcp/")
                .customHeaders(Map.of(
                        "Authorization",
                        "Bearer " + githubToken,

                        "X-MCP-Toolsets",
                        "context,repos,issues,pull_requests",

                        "X-MCP-Readonly",
                        "true"
                ))
                .logRequests(false)
                .logResponses(false)
                .build();

        DefaultMcpClient mcpClient = DefaultMcpClient.builder()
                .key("github-remote-client")
                .transport(transport)
                .build();

        try {

            // 5. Discover available GitHub tools

            System.out.println("Available GitHub MCP Tools:");

            mcpClient.listTools().forEach(tool ->
                    System.out.println(tool.name())
            );

            McpToolProvider mcpToolProvider = McpToolProvider.builder()
                    .mcpClients(mcpClient)
                    .build();

            GitHubAssistant assistant = AiServices.builder(GitHubAssistant.class)
                    .chatModel(model)
                    .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.sendExceptionMessageToLlm())
                    .toolExecutionErrorHandler(ToolExecutionErrorHandler.failInvocationUnlessVisibleToLlm())
                    .toolProvider(mcpToolProvider)
                    .build();
            String result = assistant.chat("Count all the public repositories available in the github and tell me which tool You used");

            System.out.println("\n===== AI RESPONSE =====");
            System.out.println("Result  : "+result);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            mcpClient.close();
        }



        }
}
