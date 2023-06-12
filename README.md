# Text Editor
Extendable text editor demo project written in Java for a university assignment.

**Author:** Rohan Khayech

## Features
- Basic text editor functionality with GUI built with JavaFX.
- Support for loading/saving files in UTF-8, UTF-16 and UTF-32 encoding.
- Multi-module configuration using Gradle.
- Extendable via a plugin API with example plugins included.
- Runtime loading of plugins using reflection.
- Extendable via Python scripting intepreted with Jython, with an example script included.
- Custom DSL and parser for configuring keyboard shortcuts built with JavaCC.
- Localisation support for language, date and number format, with example Dutch translation included.

## How to Run
Run the following terminal command in the root directory: 
```
> ./gradlew run
```

## Example Plugins/Scripts
- **Find Plugin** ( *texteditor.FindPlugin* ): Plugin for finding a specified phrase.
- **Date Plugin** ( *texteditor.DatePlugin* ): Inserts a localised string representing the current date at the cursor position.
- **Emoji Script** ( */scripts/emoji.py* ): Replaces ':-)' with a smiley face emoji. 
