import java.io.IOException;
import java.util.Scanner;

/**
 * Main class that provides the game selection menu.
 * Runs in an infinite loop until user chooses to quit.
 */
public class Main {
    /**
     * Main method that runs the game selection menu.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            displayMenu();
            try {
                String input = scanner.nextLine();
                System.out.println("You entered: " + input); // Debug line

                if (input == null || input.trim().isEmpty()) {
                    System.out.println("Please enter a valid choice.");
                    continue;
                }

                char choice = input.trim().toUpperCase().charAt(0);

                switch (choice) {
                    case 'W':
                        System.out.println("\nStarting Word Game...");
                        WordGame wordGame = new WordGame();
                        wordGame.playGame();
                        System.out.println("Word Game finished.\n");
                        break;

                    case 'N':
                        System.out.println("\nStarting Number Game...");
                        NumberGameFX.main(new String[]{}); // Launch the JavaFX-based Number Game
                        System.out.println("Number Game finished.\n");
                        break;

                    case 'M':
                        System.out.println("\nStarting Chord Progression Transposition...");
                        try {
                            ChordProgressionHandler.getInstance().startTransposition();
                        } catch (IOException e) {
                            System.out.println("An error occurred during chord progression initialization: " + e.getMessage());
                        }

                        System.out.println("Transposition finished.\n");
                        break;

                    case 'Q':
                        System.out.println("\nThank you for playing! Goodbye!");
                        running = false;
                        break;

                    default:
                        System.out.println("\nError: Invalid input! Please enter W, N, M, or Q.\n");
                        break;
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }

        scanner.close();
    }

    /**
     * Displays the main menu options.
     */
    private static void displayMenu() {
        System.out.println("\n=== Game Selection Menu ===");
        System.out.println("Press W to play the Word game.");
        System.out.println("Press N to play the Number game.");
        System.out.println("Press M to play the Custom game.");
        System.out.println("Press Q to quit.");
        System.out.print("Enter your choice: ");
        System.out.flush(); // Ensure output is displayed
    }
}
