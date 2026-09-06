package io.sbtxx.mcdevassistant.ai;

import io.sbtxx.mcdevassistant.model.ProjectReport;

public final class PromptBuilder {
    private PromptBuilder() {}

    public static String build(String task) {
        return "You are a senior Minecraft Java developer.\n\n"
                + "Task:\n" + task + "\n\n"
                + "Context:\n"
                + "- Prefer clean, maintainable Java.\n"
                + "- Target modern Minecraft server/plugin ecosystems.\n"
                + "- Explain important API choices and compatibility risks.\n"
                + "- Include files to create/change and a verification plan.\n"
                + "- Do not invent APIs; clearly flag assumptions.\n";
    }

    public static String buildForReport(ProjectReport report) {
        return build("Review this project based on the following scan and propose the next three improvements, ordered by impact.\n"
                + "Health: " + report.score() + "/100\n"
                + "Java files: " + report.javaFiles() + "\n"
                + "Build: " + report.buildSystem() + "\n"
                + "Minecraft project: " + report.minecraftProject() + "\n"
                + "Findings: " + String.join(" | ", report.findings()));
    }
}
