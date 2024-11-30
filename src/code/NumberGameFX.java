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

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Game grid
        GridPane gridPane = new GridPane();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                final int currentRow = row; // Declare new final variable
                final int currentCol = col; // Declare new final variable
                Button button = new Button();
                button.setPrefSize(60, 60);
                button.setOnAction(e -> handleCellClick(currentRow, currentCol, button));
                buttons[row][col] = button;
                gridPane.add(button, col, row);
            }
        }


        // Start Game Button
        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> startNewGame());

        // Reset Button
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> resetGame());

        // Add to root
        root.getChildren().addAll(currentNumberLabel, gridPane, startGameButton, resetButton, statsLabel);

        // Scene and stage setup
        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Number Game");
        primaryStage.show();
    }

    private void startNewGame() {
        resetGrid();
        generateRandomNumbers();
        displayNextNumber();
        updateStats();
    }

    private void resetGame() {
        resetGrid();
        numberQueue.clear();
        currentNumberLabel.setText("Game Reset");
    }

    private void resetGrid() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = 0;
                buttons[row][col].setText("");
                buttons[row][col].setDisable(false);
            }
        }
    }

    private void generateRandomNumbers() {
        Random random = new Random();
        for (int i = 0; i < ROWS * COLS; i++) {
            numberQueue.add(random.nextInt(1000) + 1);
        }
    }

    private void displayNextNumber() {
        if (numberQueue.isEmpty()) {
            return;
        }
        currentNumber = numberQueue.poll();
        currentNumberLabel.setText("Current Number: " + currentNumber);
    }

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

    private boolean isPlacementValid(int row, int col) {
        for (int r = 0; r <= row; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] > 0 && grid[r][c] > currentNumber) {
                    return false;
                }
            }
        }
        return true;
    }

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateStats() {
        statsLabel.setText(String.format("Games: %d Played, %d Won | Average Placements: %.2f",
                totalGames, gamesWon,
                totalGames > 0 ? (double) successfulPlacements / totalGames : 0));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
