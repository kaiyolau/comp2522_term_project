import java.util.Random;

/**
 * Abstract class that represents the base implementation of a Number Game.
 * It contains common game logic and methods for managing the grid and generating random numbers.
 * <p>
 * This class implements the {@link NumberGameStrategy} interface and provides the shared functionality for grid management,
 * generating the next number, and validating the placement of numbers in the grid. The actual game logic is expected to be 
 * implemented in subclasses.
 * </p>
 * Author: Kyle Lau
 * Version 1.0
 */
public abstract class AbstractNumberGame implements NumberGameStrategy {
    protected int[][] grid;
    protected Random random;
    protected int currentNumber;
    protected int totalGames;
    protected int gamesWon;
    protected int totalSuccessfulPlacements;

    /**
     * Constructor that initializes the game grid, the random number generator, 
     * and resets the grid to its initial state.
     */
    public AbstractNumberGame() {
        grid = new int[4][5];
        random = new Random();
        resetGrid();
    }

    /**
     * Resets the grid by setting all cells to 0, indicating they are empty.
     */
    public void resetGrid() {
        for (int[] row : grid) {
            java.util.Arrays.fill(row, 0);
        }
    }

    /**
     * Generates the next random number between 1 and 1000.
     *
     * @return A random integer between 1 and 1000.
     */
    @Override
    public int generateNextNumber() {
        return random.nextInt(1000) + 1;
    }

    /**
     * Checks if a given number can be placed in a specific row of the grid.
     * The number is only allowed if it maintains ascending order, checking against 
     * previous rows to ensure no conflicts with already placed numbers.
     *
     * @param grid The current game grid.
     * @param number The number to be placed.
     * @param row The row index where the number is to be placed.
     * @return true if the number can be placed in the specified row, false otherwise.
     */
    @Override
    public boolean canPlaceNumber(int[][] grid, int number, int row) {
        // Check if the number fits in ascending order in the specified row
        for (int col = 0; col < grid[row].length; col++) {
            if (grid[row][col] == 0) {
                // Check numbers in previous rows
                for (int prevRow = 0; prevRow < row; prevRow++) {
                    for (int prevCol = 0; prevCol < grid[prevRow].length; prevCol++) {
                        if (grid[prevRow][prevCol] > 0 && grid[prevRow][prevCol] > number) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Abstract method that starts the game. This method is expected to be implemented by subclasses.
     */
    public abstract void startGame();

    /**
     * Abstract method to update game statistics after each game. This method is expected to be implemented by subclasses.
     *
     * @param won A boolean indicating whether the game was won or not.
     */
    public abstract void updateGameStats(boolean won);
}
