package texteditor.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * The File I/O class handles loading and saving files with a specific encoding.
 * 
 * @author Rohan Khayech
 */
public class FileIO {

    /** Constructs a new FileIO object. */
    public FileIO() {}

    /**
     * Loads the contents of the specified file as a string.
     * @param file The file to load.
     * @param encoding The encoding to use.
     * @return A String containing the file contents.
     * @throws IOException If there is an issue loading the file or an incorrect encoding is specified.
     */
    public String load(File file, String encoding) throws IOException {
        return new String(Files.readAllBytes(file.toPath()),encoding);
    }

    /**
     * Saves the specified text to the specified file.
     * @param file The file to write to.
     * @param contents The text to write into the file.
     * @param encoding The encoding to use.
     * @throws IOException If there is an issue saving to the file or an incorrect encoding is specified.
     */
    public void save(File file, String contents, String encoding) throws IOException {
        Files.write(file.toPath(),contents.getBytes(encoding));
    }
}
