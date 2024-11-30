import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Represents the Number Game, which can be played either in GUI mode or console mode.
 * The goal of the game is to place randomly generated numbers in a 4x5 grid in ascending order.
 * If a number is placed out of order or there are no valid positions for the current number, the player loses.
 * <p>
 * This class extends {@link AbstractNumberGame} and implements the game logic for both GUI and console modes.
 * </p>
 * Author: Kyle Lau
 * Version 1.0
 */
public class NumberGame extends AbstractNumberGame {
    private JFrame frame;
    private JButton[][] buttons;
    private JLabel currentNumberLabel;
    private JLabel statsLabel;
    private boolean guiMode;

    /**
     * Console mode constructor.
     * Initializes the game in either GUI or console mode based on the provided parameter.
     *
     * @param isGuiMode A boolean indicating whether the game should be played in GUI mode or not.
     */
    public NumberGame(boolean isGuiMode) {
        super();
        this.guiMode = isGuiMode;

        // Call the abstract startGame method
        startGame();
    }

    /**
     * Default constructor, initializes the game in GUI mode by default.
     */
    public NumberGame() {
        this(true);
    }

    /**
     * Starts the game. If the game is in GUI mode, it initializes the graphical interface;
     * otherwise, it prepares for console-based gameplay.
     */
    @Override
    public void startGame() {
        if (guiMode) {
            // If in GUI mode, initialize the GUI
            initializeGUI();
        } else {
            // If in console mode, prepare for console play
            resetGrid();
            currentNumber = generateNextNumber();
        }
    }

    /**
     * Checks if the game is complete. The game is considered complete if all cells are filled
     * and the numbers are placed in ascending order across the grid.
     *
     * @return true if the game is complete, false otherwise.
     */
    private boolean isGameComplete() {
        // Check if all cells are filled and in ascending order
        for (int row = 0; row < grid.length; row++) {
            int lastNumber = 0;
            for (int col = 0; col < grid[row].length; col++) {
                // Check if current cell is empty
                if (grid[row][col] == 0) {
                    return false;
                }

                // Check if numbers are in ascending order
                if (grid[row][col] < lastNumber) {
                    return false;
                }

                lastNumber = grid[row][col];
            }
        }

        // Additional check to ensure numbers are truly in ascending order across rows
        int previousRowLastNumber = 0;
        for (int row = 0; row < grid.length; row++) {
            if (grid[row][0] < previousRowLastNumber) {
                return false;
            }

            // Find the last non-zero number in this row
            int lastNonZeroInRow = 0;
            for (int col = grid[row].length - 1; col >= 0; col--) {
                if (grid[row][col] != 0) {
                    lastNonZeroInRow = grid[row][col];
                    break;
                }
            }

            previousRowLastNumber = lastNonZeroInRow;
        }

        return true;
    }

    /**
     * Console-based play method. This method handles gameplay when the game is played in console mode.
     * It asks the player to enter a row to place the current number and checks if the placement is valid.
     * If the game is won, a message is displayed, and the player can choose to play again.
     */
    public void playGame() {
        if (guiMode) {
            // If in GUI mode, just bring the frame to front
            if (frame != null) {
                frame.toFront();
                frame.requestFocus();
            }
            return;
        }

        // Console mode implementation
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        resetGame();

        while (true) {
            System.out.println("\n--- Number Game ---");
            System.out.println("Current Number: " + currentNumber);

            // Print current grid state
            printGrid();

            System.out.println("Enter row (0-3) to place the number:");
            int row;
            try {
                row = scanner.nextInt();
                if (row < 0 || row > 3) {
                    System.out.println("Invalid row. Please enter a row between 0 and 3.");
                    continue;
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
                continue;
            }

            // Check if placement is valid
            if (canPlaceNumber(grid, currentNumber, row)) {
                // Place the number
                placeNumberInRow(row);

                // Check if game is complete
                if (isGameComplete()) {
                    updateGameStats(true);
                    System.out.println("Congratulations! You won the game!");
                    break;
                }

                // Generate next number
                currentNumber = generateNextNumber();
            } else {
                System.out.println("Invalid placement! Game Over.");
                updateGameStats(false);
                break;
            }
        }

        // Display game stats
        displayGameStats();

        // Ask if they want to play again
        System.out.println("\nDo you want to play again? (Y/N)");
        scanner.nextLine(); // Consume any leftover newline
        String playAgain = scanner.nextLine().trim().toUpperCase();

        if (playAgain.equals("Y")) {
            playGame();
        }
    }

    /**
     * Updates the game statistics, including the total number of games played, won,
     * and the average number of placements per game. This method updates stats in both
     * GUI mode (if enabled) and console mode.
     *
     * @param won A boolean indicating if the current game was won.
     */
    @Override
    public void updateGameStats(boolean won) {
        totalGames++;
        if (won) gamesWon++;

        // Count successful placements
        int successfulPlacements = 0;
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell != 0) successfulPlacements++;
            }
        }
        totalSuccessfulPlacements += successfulPlacements;

        // Update UI or console stats based on mode
        if (guiMode && statsLabel != null) {
            statsLabel.setText(String.format("Games: %d Won, %d Lost | Average Placements: %.2f",
                    gamesWon, totalGames,
                    totalGames > 0 ? (double)totalSuccessfulPlacements / totalGames : 0));
        } else if (!guiMode) {
            // Console mode stats display can be handled in the playGame method
            System.out.printf("Game Over - Won: %s\n", won);
            System.out.printf("Total Games: %d\n", totalGames);
            System.out.printf("Games Won: %d\n", gamesWon);
            System.out.printf("Total Successful Placements: %d\n", totalSuccessfulPlacements);
            System.out.printf("Average Placements per Game: %.2f\n",
                    totalGames > 0 ? (double)totalSuccessfulPlacements / totalGames : 0);
        }
    }

    /**
     * Resets the game state, clearing the grid and generating the first number.
     */
    private void resetGame() {
        resetGrid();
        currentNumber = generateNextNumber();
    }

    /**
     * Prints the current state of the grid to the console.
     */
    private void printGrid() {
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell == 0 ? "[ ]" : "[" + cell + "]");
            }
            System.out.println();
        }
    }

    /**
     * Places the current number in the first empty spot of the specified row.
     *
     * @param row The row in which the number is placed.
     */
    private void placeNumberInRow(int row) {
        for (int col = 0; col < grid[row].length; col++) {
            if (grid[row][col] == 0) {
                grid[row][col] = currentNumber;
                break;
            }
        }
    }

    /**
     * Displays the game statistics to the console, including total games, games won,
     * total successful placements, and average placements per game.
     */
    private void displayGameStats() {
        System.out.printf("Game Stats:\n");
        System.out.printf("Total Games: %d\n", totalGames);
        System.out.printf("Games Won: %d\n", gamesWon);
        System.out.printf("Total Successful Placements: %d\n", totalSuccessfulPlacements);
        System.out.printf("Average Placements per Game: %.2f\n",
                totalGames > 0 ? (double)totalSuccessfulPlacements / totalGames : 0);
    }

    /**
     * Initializes the GUI components if the game is being played in GUI mode.
     * This method does nothing in console mode.
     */
    private void initializeGUI() {
        // Only initialize if in GUI mode
        if (!guiMode) return;

        // Previous GUI initialization code remains the same
    }

    /**
     * The main entry point of the program. It initializes the game in console mode for testing purposes.
     *
     * @param args Command-line arguments (not used in this case).
     */
    public static void main(String[] args) {
        // For testing console mode
        NumberGame game = new NumberGame(false);
        game.playGame();
    }
}
