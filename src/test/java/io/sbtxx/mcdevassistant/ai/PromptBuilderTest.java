package io.sbtxx.mcdevassistant.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptBuilderTest {
    @Test
    void promptContainsDeveloperGuardrails() {
        String prompt = PromptBuilder.build("Add a /spawn command");

        assertTrue(prompt.contains("senior Minecraft Java developer"));
        assertTrue(prompt.contains("Do not invent APIs"));
        assertTrue(prompt.contains("verification plan"));
    }
}
