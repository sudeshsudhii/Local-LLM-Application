# Contributing to Local LLM Application

First off, thank you for considering contributing to this project! It's people like you that make the open source community such a fantastic place to learn, inspire, and create.

## 🚀 Quick Start for Contributors

1. Fork the repo and create your branch from `master`.
2. Ensure you have Java 21, Maven, and MongoDB installed.
3. Run `mvn clean install` to ensure the project builds correctly.
4. If you've added code that should be tested, add tests.
5. Ensure the test suite passes (`mvn test`).

## 🐞 Reporting Bugs

Please use the provided bug report template when creating an issue. Include as much detail as possible:
* Expected behavior
* Actual behavior
* Steps to reproduce
* Relevant logs (especially Spring Boot application logs or Ollama logs)

## ✨ Proposing Features

We love new features! Please open a Feature Request issue using the provided template to discuss your idea before submitting a Pull Request. This saves everyone time and ensures your PR aligns with the project roadmap.

## 📝 Pull Request Process

1. Update the `README.md` with details of changes to the interface, this includes new environment variables, exposed ports, useful file locations and container parameters.
2. Ensure your commit messages are descriptive. We prefer Conventional Commits format (e.g., `feat: added new endpoint for model parameters`).
3. You may merge the Pull Request in once you have the sign-off of the maintainers.

## 🛠 Coding Style

* **Java**: Follow standard Java coding conventions. We use Spring Boot formatting defaults.
* **Logging**: Use SLF4J `log.info()`, `log.debug()`, `log.error()`. Do not use `System.out.println` or `e.printStackTrace()`.
* **Testing**: Write JUnit 5 tests. Mock external services like Ollama and MongoDB.
