import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
//import

/**
 * Implementation of the Word Game that tests geography knowledge
 */
public class WordGame {
    private HashMap<String, Country> countries;
    private List<Score> scores;
    private Scanner scanner;
    private Random random;
    private int gamesPlayed;
    private int totalCorrectFirstAttempt;
    private int totalCorrectSecondAttempt;
    private int totalIncorrectAttempts;
    private static final String SCORE_FILE = "score.txt";

    /**
     * Constructs a new WordGame instance
     */
    public WordGame() {
        countries = new HashMap<>();
        scores = new ArrayList<>();
        scanner = new Scanner(System.in);
        random = new Random();
        if (!loadCountryData()) {
            System.out.println("Error: Unable to start game due to missing country data.");
            System.exit(1);  // Exit if no data is loaded
        }
        loadScores();
    }


    /**
     * Starts and manages the game session
     */
    public void playGame() {
        boolean playAgain = true;
        while (playAgain) {
            playSingleGame();
            playAgain = askToPlayAgain();
        }
    }

    /**
     * Plays a single game of 10 questions
     */
    private void playSingleGame() {
        if (countries.isEmpty()) {
            System.out.println("Error: Cannot play game without country data.");
            return;
        }

        int correctFirstAttempt = 0;
        int correctSecondAttempt = 0;
        int incorrectAttempts = 0;

        System.out.println("\nStarting new game - 10 questions about world geography!");

        for (int i = 1; i <= 10; i++) {
            System.out.println("\nQuestion " + i + ":");
            int questionType = random.nextInt(3);
            boolean firstTry = askQuestion(questionType);

            if (firstTry) {
                System.out.println("CORRECT on first attempt!");
                correctFirstAttempt++;
            } else {
                System.out.println("Incorrect. Try one more time!");
                boolean secondTry = askQuestion(questionType);
                if (secondTry) {
                    System.out.println("CORRECT on second attempt!");
                    correctSecondAttempt++;
                } else {
                    System.out.println("Incorrect again. Better luck next time!");
                    incorrectAttempts++;
                }
            }
        }

        // Update totals
        gamesPlayed++;
        totalCorrectFirstAttempt += correctFirstAttempt;
        totalCorrectSecondAttempt += correctSecondAttempt;
        totalIncorrectAttempts += incorrectAttempts;

        // Display results
        displayGameResults();

        // Save score
        Score currentScore = new Score(
                LocalDateTime.now(),
                gamesPlayed,
                totalCorrectFirstAttempt,
                totalCorrectSecondAttempt,
                totalIncorrectAttempts
        );
        saveScore(currentScore);
        checkHighScore(currentScore);
    }

    /**
     * Loads country data from provided text files
     * @return true if data was loaded successfully, false otherwise
     */
    private boolean loadCountryData() {
        boolean dataLoaded = false;

        // Print current working directory for debugging
//        System.out.println("Current working directory: " + System.getProperty("user.dir"));

        // Iterate through a.txt to z.txt files`
        for (char letter = 'a'; letter <= 'z'; letter++) {
            String filename = letter + ".txt";
            File file = new File(filename);

            // Debug output for each file
//            System.out.println("Checking for file: " + file.getAbsolutePath());

            if (!file.exists()) {
//                System.out.println("File not found: " + filename);
                continue; // Skip if file doesn't exist
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//                System.out.println("Reading from file: " + filename);
                String line;
                String currentCountry = null;
                List<String> factsList = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        // Process previous country if we have one
                        if (currentCountry != null && !factsList.isEmpty()) {
                            String[] parts = currentCountry.split(":");
                            if (parts.length == 2) {
                                String countryName = parts[0].trim();
                                String capitalCity = parts[1].trim();
                                String[] facts = factsList.toArray(new String[0]);
                                countries.put(countryName, new Country(countryName, capitalCity, facts));
                                dataLoaded = true;
//                                System.out.println("Added country: " + countryName);
                            }
                        }
                        // Reset for next country`
                        currentCountry = null;
                        factsList.clear();
                        continue;
                    }

                    if (currentCountry == null) {
                        // This should be a country:capital line
                        if (line.contains(":")) {
                            currentCountry = line;
                        }
                    } else {
                        // This is a fact
                        factsList.add(line);
                    }
                }

                // Process the last country in the file
                if (currentCountry != null && !factsList.isEmpty()) {
                    String[] parts = currentCountry.split(":");
                    if (parts.length == 2) {
                        String countryName = parts[0].trim();
                        String capitalCity = parts[1].trim();
                        String[] facts = factsList.toArray(new String[0]);
                        countries.put(countryName, new Country(countryName, capitalCity, facts));
                        dataLoaded = true;
//                        System.out.println("Added country: " + countryName);
                    }
                }

            } catch (IOException e) {
                System.out.println("Error reading file " + filename + ": " + e.getMessage());
            }
        }

        if (!dataLoaded) {
            System.out.println("ERROR: No country data could be loaded. Please ensure at least one valid input file is present.");
            System.out.println("Make sure your text files are in: " + System.getProperty("user.dir"));
            return false;
        }

        System.out.println("Successfully loaded " + countries.size() + " countries.");
        return true;
    }


    /**
     * Asks a single question based on the question type
     * @param questionType Type of question (0-2)
     * @return true if answered correctly, false otherwise
     */
    private boolean askQuestion(int questionType) {
        if (countries.isEmpty()) {
            throw new IllegalStateException("No country data available for questions");
        }

        List<Country> countryList = new ArrayList<>(countries.values());
        Country randomCountry = countryList.get(random.nextInt(countryList.size()));
        String correctAnswer;

        switch (questionType) {
            case 0: // Capital city question
                System.out.println("What country has " + randomCountry.getCapitalCityName() + " as its capital?");
                correctAnswer = randomCountry.getName();
                break;

            case 1: // Country question
                System.out.println("What is the capital city of " + randomCountry.getName() + "?");
                correctAnswer = randomCountry.getCapitalCityName();
                break;

            default: // Fact question
                String[] facts = randomCountry.getFacts();
                String randomFact = facts[random.nextInt(facts.length)];
                System.out.println("Which country is this fact about? " + randomFact);
                correctAnswer = randomCountry.getName();
                break;
        }

        System.out.print("Your answer: ");
        String userAnswer = scanner.nextLine().trim();

        return userAnswer.equalsIgnoreCase(correctAnswer);
    }

    /**
     * Asks if the player wants to play again
     * @return true if player wants to play again, false otherwise
     */
    private boolean askToPlayAgain() {
        while (true) {
            System.out.print("\nWould you like to play again? (Yes/No): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                return true;
            } else if (response.equals("no")) {
                return false;
            }
            System.out.println("Please enter either 'Yes' or 'No'");
        }
    }

    /**
     * Displays the results of the current game session
     */
    private void displayGameResults() {
        System.out.println("\nGame Results:");
        System.out.println("- " + gamesPlayed + " word game" + (gamesPlayed != 1 ? "s" : "") + " played");
        System.out.println("- " + totalCorrectFirstAttempt + " correct answers on the first attempt");
        System.out.println("- " + totalCorrectSecondAttempt + " correct answers on the second attempt");
        System.out.println("- " + totalIncorrectAttempts + " incorrect answers on two attempts each");
    }

    /**
     * Loads previous scores from the score.txt file
     */
    private void loadScores() {
        File file = new File(SCORE_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    LocalDateTime dateTime = LocalDateTime.parse(parts[0],
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    Score score = new Score(
                            dateTime,
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4])
                    );
                    scores.add(score);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading scores: " + e.getMessage());
        }
    }

    /**
     * Saves the current score to the score.txt file
     * @param score The score to save
     */
    private void saveScore(Score score) {
        try (FileWriter writer = new FileWriter(SCORE_FILE, true)) {
            // Format: datetime,gamesPlayed,correctFirst,correctSecond,incorrect
            writer.write(String.format("%s,%d,%d,%d,%d\n",
                    score.getFormattedDateTime(),
                    score.getNumGamesPlayed(),
                    score.getNumCorrectFirstAttempt(),
                    score.getNumCorrectSecondAttempt(),
                    score.getNumIncorrectTwoAttempts()));
        } catch (IOException e) {
            System.out.println("Error saving score: " + e.getMessage());
        }
    }

    /**
     * Checks if the current score is a new high score
     * @param currentScore The current score to check
     */
    private void checkHighScore(Score currentScore) {
        Score highScore = getHighScore();
        double currentAverage = currentScore.calculateAverageScore();

        if (highScore == null || currentAverage > highScore.calculateAverageScore()) {
            System.out.printf("\nCONGRATULATIONS! You are the new high score with an average of %.2f points per game",
                    currentAverage);
            if (highScore != null) {
                System.out.printf("; the previous record was %.2f points per game on %s",
                        highScore.calculateAverageScore(),
                        highScore.getFormattedDateTime());
            }
            System.out.println();
        } else {
            System.out.printf("\nYou did not beat the high score of %.2f points per game from %s\n",
                    highScore.calculateAverageScore(),
                    highScore.getFormattedDateTime());
        }
    }

    /**
     * Gets the highest score from the scores list
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
     * Inner class to represent a game score
     */
    private static class Score {
        private LocalDateTime dateTimePlayed;
        private int numGamesPlayed;
        private int numCorrectFirstAttempt;
        private int numCorrectSecondAttempt;
        private int numIncorrectTwoAttempts;

        public Score(LocalDateTime dateTimePlayed, int numGamesPlayed,
                     int numCorrectFirstAttempt, int numCorrectSecondAttempt,
                     int numIncorrectTwoAttempts) {
            this.dateTimePlayed = dateTimePlayed;
            this.numGamesPlayed = numGamesPlayed;
            this.numCorrectFirstAttempt = numCorrectFirstAttempt;
            this.numCorrectSecondAttempt = numCorrectSecondAttempt;
            this.numIncorrectTwoAttempts = numIncorrectTwoAttempts;
        }

        public double calculateAverageScore() {
            int totalPoints = (numCorrectFirstAttempt * 2) + numCorrectSecondAttempt;
            return (double) totalPoints / numGamesPlayed;
        }

        public String getFormattedDateTime() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTimePlayed.format(formatter);
        }

        // Getters
        public LocalDateTime getDateTimePlayed() { return dateTimePlayed; }
        public int getNumGamesPlayed() { return numGamesPlayed; }
        public int getNumCorrectFirstAttempt() { return numCorrectFirstAttempt; }
        public int getNumCorrectSecondAttempt() { return numCorrectSecondAttempt; }
        public int getNumIncorrectTwoAttempts() { return numIncorrectTwoAttempts; }
    }
}