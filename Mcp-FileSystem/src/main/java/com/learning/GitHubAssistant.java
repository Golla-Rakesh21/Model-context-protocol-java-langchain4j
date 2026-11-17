package com.learning;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface GitHubAssistant {

    @SystemMessage("""
        You are a GitHub AI assistant.

        You can access GitHub using MCP tools.

        Use the available tools to retrieve real GitHub data.

        Never invent repository information.

        Explain the results in a structured manner.

        You have read-only GitHub access.
        You can identify the current user by calling get_me.
        Use it whenever the user refers to 'my' or 'me'.
        """)

    String chat(@UserMessage String message);
}
