package io.sbtxx.mcdevassistant;

import io.sbtxx.mcdevassistant.ai.PromptBuilder;
import io.sbtxx.mcdevassistant.generator.PluginGenerator;
import io.sbtxx.mcdevassistant.model.ProjectReport;
import io.sbtxx.mcdevassistant.analyzer.ProjectAnalyzer;

import java.nio.file.Path;
import java.util.Arrays;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        try {
            switch (args[0].toLowerCase()) {
                case "analyze", "doctor" -> requirePath(args, "analyze <project-path>");
                case "prompt" -> requireArgs(args, 2, "prompt <task>");
                case "generate-plugin" -> requireArgs(args, 3, "generate-plugin <plugin-name> <package>");
                case "help", "--help", "-h" -> printHelp();
                default -> {
                    System.out.println("Unknown command: " + args[0]);
                    printHelp();
                    return;
                }
            }

            switch (args[0].toLowerCase()) {
                case "analyze", "doctor" -> printReport(new ProjectAnalyzer().analyze(Path.of(args[1])));
                case "prompt" -> System.out.println(PromptBuilder.build(String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                case "generate-plugin" -> {
                    Path output = args.length >= 4 ? Path.of(args[3]) : Path.of(args[1].toLowerCase().replaceAll("[^a-z0-9-]", "-"));
                    new PluginGenerator().generate(args[1], args[2], output);
                    System.out.println("Generated plugin project at: " + output.toAbsolutePath());
                }
                default -> { }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    private static void printReport(ProjectReport report) {
        System.out.println("\n=== Minecraft Developer Assistant ===");
        System.out.println("Project: " + report.root());
        System.out.println("Health score: " + report.score() + "/100");
        System.out.println();
        System.out.println("Files: " + report.totalFiles());
        System.out.println("Java files: " + report.javaFiles());
        System.out.println("Build system: " + report.buildSystem());
        System.out.println("Minecraft project: " + (report.minecraftProject() ? "yes" : "not detected"));
        System.out.println("Plugin descriptor: " + (report.pluginDescriptor() ? "found" : "missing"));
        System.out.println();
        System.out.println("Findings:");
        if (report.findings().isEmpty()) {
            System.out.println("  ✓ No obvious problems found by the built-in checks.");
        } else {
            report.findings().forEach(f -> System.out.println("  " + f));
        }
        System.out.println();
        System.out.println("AI prompt hint:");
        System.out.println(PromptBuilder.buildForReport(report));
    }

    private static void requirePath(String[] args, String usage) {
        requireArgs(args, 2, usage);
        if (!Path.of(args[1]).toFile().exists()) throw new IllegalArgumentException("Path does not exist: " + args[1]);
    }

    private static void requireArgs(String[] args, int count, String usage) {
        if (args.length < count) throw new IllegalArgumentException("Usage: mcdev " + usage);
    }

    private static void printHelp() {
        System.out.println("Minecraft Developer Assistant");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  analyze <path>                         Analyze a Minecraft/Java project");
        System.out.println("  doctor <path>                          Alias for analyze");
        System.out.println("  prompt <task>                          Build an AI-ready developer prompt");
        System.out.println("  generate-plugin <name> <package>      Scaffold a Paper-style plugin");
        System.out.println("  generate-plugin <name> <package> <dir> Write scaffold to a specific directory");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  java -jar minecraft-dev-assistant.jar analyze ./MyPlugin");
    }
}
