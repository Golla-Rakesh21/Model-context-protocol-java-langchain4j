package com.learning;

import dev.langchain4j.service.SystemMessage;

public interface FileAssistant {

    @SystemMessage("""
            
            You are a helpful file assistant
            use the available file system tools when the user ask about the local files
            never invent file content.
            """)
    String chat(String message);
}
