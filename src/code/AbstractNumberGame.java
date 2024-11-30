import java.util.Random;

public abstract class AbstractNumberGame implements NumberGameStrategy {
    protected int[][] grid;
    protected Random random;
    protected int currentNumber;
    protected int totalGames;
    protected int gamesWon;
    protected int totalSuccessfulPlacements;

    public AbstractNumberGame() {
        grid = new int[4][5];
        random = new Random();
        resetGrid();
    }

    public void resetGrid() {
        for (int[] row : grid) {
            java.util.Arrays.fill(row, 0);
        }
    }

    @Override
    public int generateNextNumber() {
        return random.nextInt(1000) + 1;
    }

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

    public abstract void startGame();
    public abstract void updateGameStats(boolean won);
}