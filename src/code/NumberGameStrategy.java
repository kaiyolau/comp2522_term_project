public interface NumberGameStrategy {
    /**
     * Generates the next number to be placed in the grid
     * @return a randomly selected number between 1 and 1000
     */
    int generateNextNumber();

    /**
     * Checks if a number can be placed in the given position
     * @param grid the current game grid
     * @param number the number to be placed
     * @param row the row where the number is to be placed
     * @return true if placement is valid, false otherwise
     */
    boolean canPlaceNumber(int[][] grid, int number, int row);
}
