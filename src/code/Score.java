import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game score with date/time and statistics.
 */
class Score {
    private LocalDateTime dateTimePlayed;
    private int numGamesPlayed;
    private int numCorrectFirstAttempt;
    private int numCorrectSecondAttempt;
    private int numIncorrectTwoAttempts;

    /**
     * Constructs a new Score object.
     *
     * @param dateTimePlayed          The date and time the game was played
     * @param numGamesPlayed          Number of games played
     * @param numCorrectFirstAttempt  Number of correct answers on first attempt
     * @param numCorrectSecondAttempt Number of correct answers on second attempt
     * @param numIncorrectTwoAttempts Number of incorrect answers after two attempts
     */
    public Score(LocalDateTime dateTimePlayed, int numGamesPlayed,
                 int numCorrectFirstAttempt, int numCorrectSecondAttempt,
                 int numIncorrectTwoAttempts) {
        this.dateTimePlayed = dateTimePlayed;
        this.numGamesPlayed = numGamesPlayed;
        this.numCorrectFirstAttempt = numCorrectFirstAttempt;
        this.numCorrectSecondAttempt = numCorrectSecondAttempt;
        this.numIncorrectTwoAttempts = numIncorrectTwoAttempts;
    }

    /**
     * Calculates the total score for this record.
     *
     * @return The total score
     */
    public int getScore() {
        return (numCorrectFirstAttempt * 2) + numCorrectSecondAttempt;
    }

    /**
     * Gets the formatted date/time string.
     *
     * @return The formatted date/time string
     */
    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTimePlayed.format(formatter);
    }

    @Override
    public String toString() {
        return String.format(
                "Date and Time: %s\nGames Played: %d\nCorrect First Attempts: %d\nCorrect Second Attempts: %d\nIncorrect Attempts: %d\nScore: %d points",
                getFormattedDateTime(),
                numGamesPlayed,
                numCorrectFirstAttempt,
                numCorrectSecondAttempt,
                numIncorrectTwoAttempts,
                getScore()
        );
    }


    /**
     * Appends a score to the specified file.
     *
     * @param score     The score to append
     * @param fileName  The name of the file
     * @throws IOException If an I/O error occurs
     */
    public static void appendScoreToFile(Score score, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(score.toFileFormat() + System.lineSeparator());
        }
    }

    /**
     * Reads scores from the specified file.
     *
     * @param fileName The name of the file
     * @return A list of scores
     * @throws IOException If an I/O error occurs
     */
    public static List<Score> readScoresFromFile(String fileName) throws IOException {
        List<Score> scores = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists() || file.length() == 0) {
            return scores; // Return empty list if the file doesn't exist or is empty
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Score score = fromFileFormat(line);
                if (score != null) {
                    scores.add(score);
                }
            }
        }
        return scores;
    }

    /**
     * Converts the score to a file-friendly format.
     *
     * @return A string representing the score in file format
     */
    private String toFileFormat() {
        return String.format("%s,%d,%d,%d,%d",
                getFormattedDateTime(), numGamesPlayed, numCorrectFirstAttempt, numCorrectSecondAttempt, numIncorrectTwoAttempts);
    }

    /**
     * Parses a score from a file-friendly format.
     *
     * @param line The line to parse
     * @return The parsed score or null if parsing fails
     */
    private static Score fromFileFormat(String line) {
        try {
            String[] parts = line.split(",");
            LocalDateTime dateTime = LocalDateTime.parse(parts[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            int gamesPlayed = Integer.parseInt(parts[1]);
            int correctFirst = Integer.parseInt(parts[2]);
            int correctSecond = Integer.parseInt(parts[3]);
            int incorrect = Integer.parseInt(parts[4]);
            return new Score(dateTime, gamesPlayed, correctFirst, correctSecond, incorrect);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Getters
    public LocalDateTime getDateTimePlayed() {
        return dateTimePlayed;
    }

    public int getNumGamesPlayed() {
        return numGamesPlayed;
    }

    public int getNumCorrectFirstAttempt() {
        return numCorrectFirstAttempt;
    }

    public int getNumCorrectSecondAttempt() {
        return numCorrectSecondAttempt;
    }

    public int getNumIncorrectTwoAttempts() {
        return numIncorrectTwoAttempts;
    }
}
