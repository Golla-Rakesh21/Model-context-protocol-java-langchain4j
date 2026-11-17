package com.learning;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;

import java.util.List;

public class Main6CustomMcp {
    public static void main(String[] args){


       String googleApiKey = System.getenv("GOOGLE_API_KEY");

        GoogleGenAiChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(googleApiKey)
                .logRequestsAndResponses(false)
                .modelName("gemini-3.5-flash-lite")
                .build();

        System.out.println("chat model created");

        //building the mcp Tranposrt

        StdioMcpTransport transport = StdioMcpTransport.builder()
                .command(List.of("java",
                        "-jar",
                        "E:\\idea pojects\\Custom-Mcp\\target\\Custom-Mcp-1.0-SNAPSHOT.jar"
                ))
                .logEvents(true)
                .build();

        DefaultMcpClient mcpclient = DefaultMcpClient.builder()
                .key("Custom-mcp-server-checks")
                .transport(transport)
                .build();

        List<ToolSpecification> tools = mcpclient.listTools();
        for(ToolSpecification tool : tools)
        {
            System.out.println("Tool: " + tool.name());
            System.out.println("Description: " + tool.description());
        }

        //creating tool provides
        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpclient)
                .build();

        CourseAssistant assistant = AiServices.builder(CourseAssistant.class)
                .toolProvider(toolProvider)
                .chatModel(model)
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.failInvocationUnlessVisibleToLlm())
                .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.sendExceptionMessageToLlm())
                .build();

        System.out.println("======================AI Response =====================");
        System.out.println("Result : "+assistant.chat("how many courses are currently available?"));


    }
}
