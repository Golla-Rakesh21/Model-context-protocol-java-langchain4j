package com.learning;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        String openaiApiKey = System.getenv("OPENAI_API_KEY");
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(openaiApiKey)
                .modelName("gpt-5.4-mini")
                .build();

        //starting (launching) the MCP filesystem server yourself from your Java application.
        StdioMcpTransport transport = StdioMcpTransport.builder()
                .command(List.of(
                                "cmd", "/c",
                                "npx", "-y",
                                "@modelcontextprotocol/server-filesystem",
                                "--read-only",
                                "E:\\Mpc-text-files"
                        )
                ).logEvents(true)
                .build();

        //creating the client for the file system server.
        McpClient mcpClient = DefaultMcpClient.builder()
                .key("filesystem-server")
                .transport(transport)
                .build();
        //user to register the available tools to the llm ,  so that llm can make a request.
        McpToolProvider provider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();


        //list and printing all the tools available in the server through the client
        List<ToolSpecification> tools = mcpClient.listTools();
        tools.forEach((tool1)->{
            System.out.println(tool1.name());
             System.out.println(tool1.description());
            System.out.println(tool1.parameters());
            System.out.println("--------------------------------------------------------------");
        });

        //Aiservice
        FileAssistant assistant = AiServices.builder(FileAssistant.class)
                .chatModel(model)
                .toolProvider(provider)
                .toolArgumentsErrorHandler(
                        ToolArgumentsErrorHandler.sendExceptionMessageToLlm()
                )
                .toolExecutionErrorHandler(
                        ToolExecutionErrorHandler.failInvocationUnlessVisibleToLlm()
                )
                .build();

        System.out.println("======================AI Response =====================");
        String answer = assistant.chat("what text present in the file1.txt?");

        System.out.println("Result : "+answer);



    }
}