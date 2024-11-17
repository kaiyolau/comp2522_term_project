import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Main class for the Word Game implementation.
 */
public class WordGame {
    private World world;
    private List<Score> scores;
    private Scanner scanner;
    private Random random;

    /**
     * Constructs a new WordGame object.
     */
    public WordGame() {
        world = new World();
        scores = new ArrayList<>();
        scanner = new Scanner(System.in);
        random = new Random();
        loadCountryData();
        loadScores();
    }

    /**
     * Loads country data from files.
     */
    private void loadCountryData() {
        // Implementation to read from a.txt through z.txt files
        // This would need to be implemented based on the file format
    }

    /**
     * Loads previous scores from score.txt.
     */
    private void loadScores() {
        try {
            File file = new File("score.txt");
            if (!file.exists()) {
                return;
            }
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                // Parse the score file format and create Score objects
                // Implementation needed based on the specific file format
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error loading scores: " + e.getMessage());
        }
    }

    /**
     * Plays one round of the game.
     * @return true if the player wants to play again, false otherwise
     */
    public boolean playGame() {
        int firstAttempts = 0;
        int secondAttempts = 0;
        int incorrect = 0;

        for (int i = 0; i < 10; i++) {
            int questionType = random.nextInt(3);
            boolean result = askQuestion(questionType);
            if (result) {
                firstAttempts++;
            } else {
                result = askQuestion(questionType); // Second attempt
                if (result) {
                    secondAttempts++;
                } else {
                    incorrect++;
                }
            }
        }

        // Save score and check for high score
        Score currentScore = new Score(
                LocalDateTime.now(),
                1,
                firstAttempts,
                secondAttempts,
                incorrect
        );
        scores.add(currentScore);
        saveScore(currentScore);
        checkHighScore(currentScore);

        return askToPlayAgain();
    }

    /**
     * Asks a question based on the question type.
     * @param questionType The type of question (0-2)
     * @return true if answered correctly, false otherwise
     */
    private boolean askQuestion(int questionType) {
        // Implementation for different question types
        // Returns true if answer is correct, false otherwise
        return false; // Placeholder
    }

    /**
     * Saves the score to the score.txt file.
     * @param score The score to save
     */
    private void saveScore(Score score) {
        try {
            FileWriter writer = new FileWriter("score.txt", true);
            writer.write(String.format("%s,%d,%d,%d,%d\n",
                    score.getFormattedDateTime(),
                    score.getNumGamesPlayed(),
                    score.getNumCorrectFirstAttempt(),
                    score.getNumCorrectSecondAttempt(),
                    score.getNumIncorrectTwoAttempts()));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving score: " + e.getMessage());
        }
    }

    /**
     * Checks if the current score is a new high score.
     * @param currentScore The current score to check
     */
    private void checkHighScore(Score currentScore) {
        double currentAverage = currentScore.calculateAverageScore();
        Score highScore = getHighScore();

        if (highScore == null || currentAverage > highScore.calculateAverageScore()) {
            System.out.printf("CONGRATULATIONS! You are the new high score with an average of %.2f points per game",
                    currentAverage);
            if (highScore != null) {
                System.out.printf("; the previous record was %.2f points per game on %s",
                        highScore.calculateAverageScore(),
                        highScore.getFormattedDateTime());
            }
            System.out.println();
        } else {
            System.out.printf("You did not beat the high score of %.2f points per game from %s\n",
                    highScore.calculateAverageScore(),
                    highScore.getFormattedDateTime());
        }
    }

    /**
     * Gets the highest score from the scores list.
     * @return The highest Score object, or null if no scores exist
     */
    private Score getHighScore() {
        if (scores.isEmpty()) {
            return null;
        }
        return Collections.max(scores,
                (a, b) -> Double.compare(a.calculateAverageScore(), b.calculateAverageScore()));
    }

    /**
     * Asks the user if they want to play again.
     * @return true if the user wants to play again, false otherwise
     */
    private boolean askToPlayAgain() {
        while (true) {
            System.out.println("Would you like to play again? (Yes/No)");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                return true;
            } else if (response.equals("no")) {
                return false;
            }
            System.out.println("Please enter either 'Yes' or 'No'");
        }
    }
}
