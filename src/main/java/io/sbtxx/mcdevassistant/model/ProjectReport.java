package io.sbtxx.mcdevassistant.model;

import java.nio.file.Path;
import java.util.List;

public record ProjectReport(
        Path root,
        int totalFiles,
        int javaFiles,
        String buildSystem,
        boolean minecraftProject,
        boolean pluginDescriptor,
        int score,
        List<String> findings) {

    public ProjectReport {
        findings = List.copyOf(findings);
    }
}
