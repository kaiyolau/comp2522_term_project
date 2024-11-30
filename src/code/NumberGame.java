import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NumberGame extends AbstractNumberGame {
    private JFrame frame;
    private JButton[][] buttons;
    private JLabel currentNumberLabel;
    private JLabel statsLabel;
    private boolean guiMode;

    // Console mode constructor
    public NumberGame(boolean isGuiMode) {
        super();
        this.guiMode = isGuiMode;

        if (isGuiMode) {
            initializeGUI();
        }
    }

    // Default constructor (GUI mode)
    public NumberGame() {
        this(true);
    }

    // Console-based play method
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

    private void resetGame() {
        resetGrid();
        currentNumber = generateNextNumber();
    }

    private void printGrid() {
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell == 0 ? "[ ]" : "[" + cell + "]");
            }
            System.out.println();
        }
    }

    private void placeNumberInRow(int row) {
        for (int col = 0; col < grid[row].length; col++) {
            if (grid[row][col] == 0) {
                grid[row][col] = currentNumber;
                break;
            }
        }
    }

    private void displayGameStats() {
        System.out.printf("Game Stats:\n");
        System.out.printf("Total Games: %d\n", totalGames);
        System.out.printf("Games Won: %d\n", gamesWon);
        System.out.printf("Total Successful Placements: %d\n", totalSuccessfulPlacements);
        System.out.printf("Average Placements per Game: %.2f\n",
                totalGames > 0 ? (double)totalSuccessfulPlacements / totalGames : 0);
    }

    // Rest of the previous GUI implementation remains the same...

    // Override the GUI-specific methods to do nothing in console mode
    private void initializeGUI() {
        // Only initialize if in GUI mode
        if (!guiMode) return;

        // Previous GUI initialization code remains the same
    }

    // Main method for standalone testing
    public static void main(String[] args) {
        // For testing console mode
        NumberGame game = new NumberGame(false);
        game.playGame();
    }
}