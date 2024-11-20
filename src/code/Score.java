import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
     * @param dateTimePlayed The date and time the game was played
     * @param numGamesPlayed Number of games played
     * @param numCorrectFirstAttempt Number of correct answers on first attempt
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
     * Calculates the average points per game.
     * @return The average points per game
     */
    public double calculateAverageScore() {
        int totalPoints = (numCorrectFirstAttempt * 2) + numCorrectSecondAttempt;
        return (double) totalPoints / numGamesPlayed;
    }

    /**
     * Gets the formatted date/time string.
     * @return The formatted date/time string
     */
    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTimePlayed.format(formatter);
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