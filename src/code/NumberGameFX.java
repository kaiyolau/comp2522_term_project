import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


/**
 * Represents the GUI-based Number Game using JavaFX.
 * The goal of the game is to place randomly generated numbers in a 4x5 grid in ascending order.
 * If a number is placed out of order or there are no valid positions for the current number, the player loses.
 * <p>
 * This class uses JavaFX to provide an interactive graphical interface for the game.
 * It includes a grid of buttons, score tracking, and options to restart or quit the game.
 * </p>
 * Author: Kyle Lau
 * Version 1.0
 */
public class NumberGameFX extends Application {
    private static final int ROWS = 4;
    private static final int COLS = 5;

    private int[][] grid = new int[ROWS][COLS];
    private Button[][] buttons = new Button[ROWS][COLS];
    private Queue<Integer> numberQueue = new LinkedList<>();
    private Label currentNumberLabel = new Label();
    private Label statsLabel = new Label();
    private int totalGames = 0;
    private int gamesWon = 0;
    private int successfulPlacements = 0;
    private int currentNumber = 0;

    /**
     * The entry point for the JavaFX application. It sets up the game window with buttons, labels, and grid.
     *
     * @param primaryStage The primary stage for the application.
     */
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Game grid
        GridPane gridPane = new GridPane();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                final int currentRow = row;
                final int currentCol = col;
                Button button = new Button();
                button.setPrefSize(60, 60);
                button.setOnAction(e -> handleCellClick(currentRow, currentCol, button));
                buttons[row][col] = button;
                gridPane.add(button, col, row);
            }
        }

        // Control buttons
        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> startNewGame());

        Button resetButton = new Button("Try Again");
        resetButton.setOnAction(e -> startNewGame());

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> {
            showAlert("Final Score", getFinalScore());
            primaryStage.close(); // Close the game window
        });

        // Add to root
        root.getChildren().addAll(currentNumberLabel, gridPane, startGameButton, resetButton, quitButton, statsLabel);

        // Scene and stage setup
        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Number Game");
        primaryStage.show();
    }

    /**
     * Prints the solution of the game by displaying the grid in ascending order.
     * The solution is generated by sorting the randomly generated numbers.
     */
    private void printSolution() {
        // Flatten the grid into a 1D array and sort the random numbers
        int[] solution = numberQueue.stream().sorted().mapToInt(Integer::intValue).toArray();

        // Display the solution in the console
        System.out.println("Solution:");
        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                System.out.printf("%4d ", solution[index++]);
            }
            System.out.println(); // Move to the next row
        }
        System.out.println("Follow this solution to win the game!");
    }

    /**
     * Starts a new game by resetting the grid, generating random numbers, and displaying the first number.
     */
    private void startNewGame() {
        resetGrid();
        generateRandomNumbers();
        printSolution();
        displayNextNumber();
        updateStats();
    }

    /**
     * Resets the grid, clearing all numbers and disabling buttons.
     */
    private void resetGrid() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = 0;
                buttons[row][col].setText("");
                buttons[row][col].setDisable(false);
            }
        }
        successfulPlacements = 0;
    }

    /**
     * Generates random numbers between 1 and 1000 and adds them to the queue.
     */
    private void generateRandomNumbers() {
        Random random = new Random();
        numberQueue.clear();
        for (int i = 0; i < ROWS * COLS; i++) {
            numberQueue.add(random.nextInt(1000) + 1);
        }
    }

    /**
     * Displays the next number to be placed on the grid.
     */
    private void displayNextNumber() {
        if (numberQueue.isEmpty()) {
            return;
        }
        currentNumber = numberQueue.poll();
        currentNumberLabel.setText("Current Number: " + currentNumber);
    }

    /**
     * Handles the click event on a cell in the grid. Places the current number in the clicked cell
     * and checks if the placement is valid. If the grid is full and valid, the player wins.
     *
     * @param row The row index of the clicked cell.
     * @param col The column index of the clicked cell.
     * @param button The button representing the clicked cell.
     */
    private void handleCellClick(int row, int col, Button button) {
        if (grid[row][col] != 0 || currentNumber == 0) {
            return; // Cell already occupied or no number to place
        }

        grid[row][col] = currentNumber;
        button.setText(String.valueOf(currentNumber));
        button.setDisable(true);

        if (!isPlacementValid(row, col)) {
            showAlert("Game Over", "Invalid placement! You lost.");
            totalGames++;
            updateStats();
            return;
        }

        successfulPlacements++;
        if (isGridFull()) {
            showAlert("Congratulations", "You won the game!");
            gamesWon++;
        } else {
            displayNextNumber();
        }

        updateStats();
    }

    /**
     * Checks whether the placement of the current number is valid according to the rules.
     * The numbers must be in ascending order across the grid.
     *
     * @param row The row index of the placed number.
     * @param col The column index of the placed number.
     * @return true if the placement is valid, false otherwise.
     */
    private boolean isPlacementValid(int row, int col) {
        // Ensure the numbers are in ascending order across the grid
        int previousNumber = 0;
        for (int r = 0; r <= row; r++) {
            for (int c = 0; c < COLS; c++) {
                int current = grid[r][c];
                if (current == 0) break; // Skip unfilled cells
                if (current < previousNumber) {
                    return false;
                }
                previousNumber = current;
            }
        }
        return true;
    }

    /**
     * Checks if the grid is completely filled with numbers.
     *
     * @return true if the grid is full, false otherwise.
     */
    private boolean isGridFull() {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Displays an alert dialog with the given title and message.
     *
     * @param title The title of the alert.
     * @param message The message to be displayed in the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Updates the game statistics, including the number of games played, won, and the average number of placements.
     */
    private void updateStats() {
        statsLabel.setText(String.format("Games: %d Played, %d Won | Average Placements: %.2f",
                totalGames, gamesWon,
                totalGames > 0 ? (double) successfulPlacements / totalGames : 0));
    }

    /**
     * Retrieves the final score, including the total number of games played, won, lost, and the average number of placements.
     *
     * @return A string representing the final score.
     */
    private String getFinalScore() {
        return String.format("Total Games: %d\nGames Won: %d\nGames Lost: %d\nAverage Placements per Game: %.2f",
                totalGames, gamesWon, totalGames - gamesWon,
                totalGames > 0 ? (double) successfulPlacements / totalGames : 0);
    }

    /**
     * The main entry point of the application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
