# Minecraft Developer Assistant

A Java-first developer toolkit for Minecraft projects.

The goal is simple: help Minecraft developers understand a project faster, catch common setup problems, generate safe boilerplate, and prepare high-quality prompts for AI-assisted development.

## Current features

### Project Doctor
Scan a local Java/Minecraft project and get:

- project health score
- Java/build-system detection
- Minecraft API detection
- plugin descriptor checks
- TODO/FIXME hints
- structural warnings

### Plugin Generator
Generate a clean starter project structure for a Paper-style plugin without manually creating every folder and file.

### AI Prompt Engine
Turn a development request or project scan into a structured prompt designed for an AI coding assistant. The prompt emphasizes maintainability, compatibility, explicit assumptions, and verification.

## Example

```text
mcdev analyze ./MyPlugin

=== Minecraft Developer Assistant ===
Project: /.../MyPlugin
Health score: 85/100

Files: 24
Java files: 12
Build system: Maven
Minecraft project: yes
Plugin descriptor: found
```

Generate a starter plugin:

```text
mcdev generate-plugin SkyTools io.example.skytools ./SkyTools
```

Create an AI-ready request:

```text
mcdev prompt "Add a /spawn command with a configurable cooldown"
```

## Project goals

The first release intentionally uses a small, dependency-light Java core. Future milestones will add deeper Minecraft API diagnostics, GitHub integration, optional LLM providers, richer project graphs, and automated fixes.

## Development

Requires Java 21+ and Maven.

```bash
mvn test
mvn package
java -jar target/minecraft-dev-assistant-0.1.0-SNAPSHOT.jar help
```

## License

MIT
