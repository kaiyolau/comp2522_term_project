import java.util.*;
import java.util.logging.FileHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;

/**
 * The {@code ChordProgressionHandler} class handles the transposition of chord progressions
 * from one musical key to another. It ensures that all chord progressions in a given file
 * are correctly transposed while preserving their structure and alignment.
 *
 * <p>This class implements the Singleton pattern, ensuring only one instance of the class
 * exists throughout the application. The transposition logic supports various musical keys
 * and ensures compatibility with slash chords and other variations.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Singleton design pattern for a single point of access.</li>
 *   <li>Support for multiple keys, including sharps, flats, and minor chords.</li>
 *   <li>Automatic detection of the original key in chord files.</li>
 *   <li>Concurrent processing of chord lines using parallel streams.</li>
 * </ul>
 *
 * @author Kyle Lau
 * @version 1.0
 */
public class ChordProgressionHandler {
    private static ChordProgressionHandler instance;
    private final Map<String, String[]> keyMap;
    private final FileHandler fileHandler;

    /**
     * Nested class {@code ChordLine} represents a single line in a chord file.
     * Each line is associated with a line number and can be processed to include
     * transposed chords.
     */
    public class ChordLine implements Comparable<ChordLine> {
        private final String originalLine;
        private final int lineNumber;
        private StringBuilder processedLine;

        /**
         * Constructs a {@code ChordLine} instance.
         *
         * @param line The original line content.
         * @param num  The line number in the file.
         */
        public ChordLine(String line, int num) {
            this.originalLine = line;
            this.lineNumber = num;
            this.processedLine = new StringBuilder(line);
        }

        /**
         * Retrieves the original line content.
         *
         * @return The original line content.
         */
        public String getOriginalLine() {
            return originalLine;
        }

        /**
         * Retrieves the processed version of the line.
         *
         * @return A {@code StringBuilder} containing the processed line.
         */
        public StringBuilder getProcessedLine() {
            return processedLine;
        }

        @Override
        public int compareTo(ChordLine other) {
            return Integer.compare(this.lineNumber, other.lineNumber);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ChordLine other = (ChordLine) obj;
            return lineNumber == other.lineNumber &&
                    originalLine.equals(other.originalLine);
        }

        @Override
        public int hashCode() {
            return Objects.hash(originalLine, lineNumber);
        }
    }

    /**
     * Private constructor to initialize the {@code ChordProgressionHandler}.
     * Loads the mapping of musical keys and initializes the file handler.
     *
     * @throws IOException If an error occurs while initializing the file handler.
     */
    private ChordProgressionHandler() throws IOException {
        keyMap = new HashMap<>();
        fileHandler = new FileHandler();
        initializeKeys();
    }

    /**
     * Retrieves the singleton instance of the {@code ChordProgressionHandler}.
     *
     * @return The singleton instance.
     * @throws IOException If an error occurs during initialization.
     */
    public static ChordProgressionHandler getInstance() throws IOException {
        if (instance == null) {
            instance = new ChordProgressionHandler();
        }
        return instance;
    }

    /**
     * Initializes the mapping of musical keys and their respective scales.
     */
    private void initializeKeys() {
        keyMap.put("C", new String[]{"C", "Dm", "Em", "F", "G", "Am", "Bdim"});
        keyMap.put("D", new String[]{"D", "Em", "F#m", "G", "A", "Bm", "C#dim"});
        keyMap.put("E", new String[]{"E", "F#m", "G#m", "A", "B", "C#m", "D#dim"});
        keyMap.put("F", new String[]{"F", "Gm", "Am", "Bb", "C", "Dm", "Edim"});
        keyMap.put("G", new String[]{"G", "Am", "Bm", "C", "D", "Em", "F#dim"});
        keyMap.put("A", new String[]{"A", "Bm", "C#m", "D", "E", "F#m", "G#dim"});
        keyMap.put("B", new String[]{"B", "C#m", "D#m", "E", "F#", "G#m", "A#dim"});
    }

    /**
     * Starts the chord transposition process by reading an input file,
     * detecting the original key, and transposing the chords to a target key.
     */
    public void startTransposition() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter input file path: ");
            String inputPath = scanner.nextLine();

            System.out.print("Enter target key (e.g., D): ");
            String targetKey = scanner.nextLine().toUpperCase();

            if (!keyMap.containsKey(targetKey)) {
                throw new IllegalArgumentException("Invalid target key. Please use C, D, E, F, G, A, or B");
            }

            List<String> content = Files.readAllLines(Paths.get(inputPath));
            List<String> transposedContent = processContent(content, targetKey);

            String outputPath = inputPath.replace(".txt", "_transposed_" + targetKey + ".txt");
            Files.write(Paths.get(outputPath), transposedContent);

            System.out.println("Transposition completed! Output saved to: " + outputPath);

        } catch (Exception e) {
            System.out.println("Error during transposition: " + e.getMessage());
        }
    }

    /**
     * Determines if a given line contains chord information.
     *
     * @param line The line to analyze.
     * @return {@code true} if the line contains chords, {@code false} otherwise.
     */
    public boolean isChordLine(String line) {
        if (line == null || line.trim().isEmpty()) return false;

        return line.contains("/") || line.contains("#") ||
                (line.chars().filter(Character::isWhitespace).count() > 5 &&
                        line.chars().anyMatch(ch -> ch >= 'A' && ch <= 'G'));
    }

    /**
     * Detects the key of a given line if it contains a key marker.
     *
     * @param line The line to analyze.
     * @return The detected key or {@code null} if no key is found.
     */
    public String detectKey(String line) {
        if (line == null || !line.toLowerCase().contains("key")) return null;

        return Arrays.stream(new String[]{"C", "D", "E", "F", "G", "A", "B"})
                .filter(key -> line.contains(key))
                .findFirst()
                .orElse(null);
    }

    /**
     * Validates whether a given string is a valid chord.
     *
     * @param chord The chord string to validate.
     * @return {@code true} if the chord is valid, {@code false} otherwise.
     */
    public boolean isValidChord(String chord) {
        if (chord == null || chord.trim().isEmpty()) return false;

        String chordPattern = "^[A-G][#b]?(m|maj|dim)?[0-9]?(/[A-G][#b]?)?$";
        return chord.matches(chordPattern);
    }

    /**
     * Transposes a chord from one key to another.
     *
     * @param originalChord The chord to transpose.
     * @param fromKey       The original key.
     * @param toKey         The target key.
     * @return The transposed chord.
     */
    public String transposeChord(String originalChord, String fromKey, String toKey) {
        if (!keyMap.containsKey(fromKey) || !keyMap.containsKey(toKey)) {
            throw new IllegalArgumentException("Invalid key provided");
        }

        if (originalChord.contains("/")) {
            String[] parts = originalChord.split("/");
            return transposeChord(parts[0], fromKey, toKey) + "/" +
                    transposeChord(parts[1], fromKey, toKey);
        }

        String[] fromScale = keyMap.get(fromKey);
        String[] toScale = keyMap.get(toKey);

        int index = Arrays.asList(fromScale).indexOf(originalChord);
        if (index == -1) {
            throw new IllegalArgumentException("Invalid chord: " + originalChord);
        }

        return toScale[index];
    }

    /**
     * Processes a line of chords, transposing each chord while maintaining alignment.
     *
     * @param line    The line to process.
     * @param fromKey The original key.
     * @param toKey   The target key.
     * @return The transposed line.
     */
    public String processChordLine(ChordLine line, String fromKey, String toKey) {
        String originalLine = line.getOriginalLine();
        StringBuilder result = new StringBuilder();

        String regex = "(\\s+)|(\\S+)";
        Matcher matcher = Pattern.compile(regex).matcher(originalLine);

        while (matcher.find()) {
            String part = matcher.group();
            if (isValidChord(part.trim())) {
                result.append(transposeChord(part.trim(), fromKey, toKey));
            } else {
                result.append(part);
            }
        }

        return result.toString();
    }

    /**
     * Processes the content of a file, transposing each chord line while
     * maintaining other content unchanged.
     *
     * @param content   The content to process.
     * @param targetKey The target key for transposition.
     * @return The processed content.
     */
    public List<String> processContent(List<String> content, String targetKey) {
        String originalKey = content.stream()
                .map(this::detectKey)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No key specified in content"));

        return content.parallelStream()  // Concurrent processing
                .map(line -> {
                    if (line.toLowerCase().contains("key")) {
                        return targetKey + " key";
                    } else if (isChordLine(line)) {
                        ChordLine chordLine = new ChordLine(line, content.indexOf(line));
                        return processChordLine(chordLine, originalKey, targetKey);
                    }
                    return line;
                })
                .collect(Collectors.toList());
    }
}
