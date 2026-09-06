package io.sbtxx.mcdevassistant.analyzer;

import io.sbtxx.mcdevassistant.model.ProjectReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class ProjectAnalyzer {
    private static final int MAX_SCAN_BYTES = 1_000_000;

    public ProjectReport analyze(Path root) throws IOException {
        Path project = root.toAbsolutePath().normalize();
        if (!Files.isDirectory(project)) throw new IllegalArgumentException("Not a directory: " + project);

        int total = 0;
        int java = 0;
        int todos = 0;
        boolean pom = Files.exists(project.resolve("pom.xml"));
        boolean gradle = Files.exists(project.resolve("build.gradle")) || Files.exists(project.resolve("build.gradle.kts"));
        boolean descriptor = Files.exists(project.resolve("src/main/resources/plugin.yml"))
                || Files.exists(project.resolve("src/main/resources/paper-plugin.yml"))
                || Files.exists(project.resolve("plugin.yml"))
                || Files.exists(project.resolve("paper-plugin.yml"));
        boolean minecraft = descriptor;
        List<String> findings = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(project)) {
            for (Path file : (Iterable<Path>) stream.filter(Files::isRegularFile)::iterator) {
                String relative = project.relativize(file).toString();
                if (relative.startsWith("target") || relative.startsWith(".git") || relative.startsWith(".idea")) continue;
                total++;
                String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
                if (name.endsWith(".java")) java++;
                if (!isTextLike(file, name)) continue;
                try {
                    long size = Files.size(file);
                    if (size > MAX_SCAN_BYTES) continue;
                    String text = Files.readString(file);
                    String lower = text.toLowerCase(Locale.ROOT);
                    if (lower.contains("org.bukkit") || lower.contains("io.papermc.paper") || lower.contains("net.fabricmc")) minecraft = true;
                    todos += count(lower, "todo") + count(lower, "fixme");
                } catch (IOException ignored) {
                    findings.add("⚠ Could not read " + relative);
                }
            }
        }

        String build = pom && gradle ? "Maven + Gradle" : pom ? "Maven" : gradle ? "Gradle" : "Not detected";
        if (!pom && !gradle) findings.add("⚠ No Maven or Gradle build file detected.");
        if (minecraft && !descriptor) findings.add("⚠ Minecraft code detected, but plugin.yml/paper-plugin.yml was not found.");
        if (java == 0) findings.add("⚠ No Java source files found.");
        if (todos > 0) findings.add("ℹ Found " + todos + " TODO/FIXME marker(s).");
        if (java > 0 && !Files.exists(project.resolve("src/main/java"))) findings.add("⚠ Java files exist outside the conventional src/main/java directory.");
        if (descriptor && !Files.exists(project.resolve("src/main/java"))) findings.add("⚠ Plugin descriptor exists, but src/main/java is missing.");

        int score = 100;
        if (!pom && !gradle) score -= 20;
        if (java == 0) score -= 25;
        if (minecraft && !descriptor) score -= 15;
        if (todos >= 10) score -= 10;
        else if (todos >= 3) score -= 5;
        score = Math.max(0, score);

        return new ProjectReport(project, total, java, build, minecraft, descriptor, score, findings);
    }

    private boolean isTextLike(Path file, String name) {
        return name.endsWith(".java") || name.endsWith(".yml") || name.endsWith(".yaml") || name.endsWith(".xml")
                || name.endsWith(".gradle") || name.endsWith(".kts") || name.endsWith(".properties") || name.endsWith(".md") || name.endsWith(".json");
    }

    private int count(String text, String token) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(token, index)) >= 0) {
            count++;
            index += token.length();
        }
        return count;
    }
}
