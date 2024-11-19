import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * This class represents a bowling game.
 * It handles user interactions, calculates real-time scores and displays them.
 */
public class BowlingGame {
    private final List<Frame> frames;
    private final Scanner scanner;
    private int totalScore;

    /**
     * Constructs an instance of a bowling game.
     * Initializes the  Scanner and list of Frames.
     */
    public BowlingGame() {
        frames = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    /**
     * Starts the bowling game.
     * Prompts the user for input for each roll, handles the input, updates the score in real time and displays it upon
     * request. At the end of the game calculates and displays total score.
     */
    public void startGame() {
        System.out.println("Happy Bowling!");
        System.out.println("Enter 'Q' at any time to quit the game or 'D' to display the score at any time.");
        // Iterates through each frame.
        for (int i = 1; i <= 10; i++) {
            Frame frame = new Frame(i);
            frames.add(frame);
            System.out.println("\nFrame " + i + ":");

            Integer firstRoll = getValidInput(10, "Enter pins knocked down in first roll: ");
            frame.setFirstRoll(firstRoll);

            // Specifically handle the case where we are in the last frame due to unique rules.
            if (frame.getFrameNumber() == 10) {
                handleTenthFrame(frame, firstRoll);
                continue;
            }

            // In case of a strike, alert user and skip to next frame.
            if (firstRoll == 10) {
                System.out.println("Strike!");
                continue;
            }

            // Prompts for second roll of frame,
            Integer secondRoll = getValidInput(10 - firstRoll, "Enter pins knocked down in second roll: ");
            frame.setSecondRoll(secondRoll);

            // In case of a spare, alerts the user.
            if (firstRoll + secondRoll == 10) {
                System.out.println("Spare!");
            }
        }

        // Print the score summary after game has finished.
        displayScore();

        // Display the final total score from the 10th frame's running total
        System.out.println("\nGame Over! Your total score is: " + totalScore);
    }


    /**
     * Specifically handles the 10th frame actions
     * Identifies strikes/spares and applies the rules accordingly.
     * @param frame The 10th frame passed as an argument.
     * @param firstRoll The number of pins rolled in the first roll.
     */
    private void handleTenthFrame(Frame frame, int firstRoll) {


        if (firstRoll == 10) { // In case of a strike on first roll.
            // First bonus roll
            Integer secondRoll = getValidInput(10, "Enter pins knocked down in second roll: ");
            frame.setSecondRoll(secondRoll);

            // In case of another strike or spare, get second bonus roll
            if (secondRoll == 10) {
                frame.setThirdRoll(getValidInput(10, "Enter pins knocked down in third roll: "));
            } else if (firstRoll + secondRoll == 10) {
                frame.setThirdRoll(getValidInput(10, "Enter pins knocked down in third roll: "));
            }
        } else {
            Integer secondRoll = getValidInput(10 - firstRoll, "Enter pins knocked down in second roll: ");
            frame.setSecondRoll(secondRoll);

            // In case of spare.
            if (firstRoll + secondRoll == 10) {
                // Bonus roll
                frame.setThirdRoll(getValidInput(10, "Enter pins knocked down in third roll: "));
            }
        }
    }

    /**
     * Prompts the user for input and validates it.
     * For invalid inputs, re-prompts the user.
     * If the user enters 'D' or 'd', the current score is displayed.
     *
     * @param max    The maximum valid number of pins in frame.
     * @param prompt The appropriate prompt message to display according to situation.
     * @return The number of pins knocked down.
     */
    private Integer getValidInput(int max, String prompt) {
        Integer pins = null;

        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            // Handle score display command
            if (input.toUpperCase(Locale.ROOT).equals("D")) {
                displayScore();
                continue;
            }else if (input.toUpperCase(Locale.ROOT).equals("Q")) {
                System.out.println("Game Terminated.");
                System.exit(0);
            }

            // Validate the input.
            try {
                pins = Integer.parseInt(input);
                if (pins < 0 || pins > max) {
                    System.out.println("Invalid input. Please enter a number between " + 0 + " and " + max + ", 'D' to display the score, or 'Q' to quit the game.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer, 'D' to display the score, or 'Q' to quit the game.");
            }
        }
        return pins;
    }

    /**
     * Display the rolls' information and running total for each frame played.
     * Running total is displayed only when all necessary rolls (including bonuses) are completed.
     * Also updates the total score as frames are processed.
     */
    public void displayScore() {
        System.out.println("\n=== Current Score ===\n");
        int runningTotal = 0;
        boolean canUpdate = true; // Flag if the running total of current frame can be updated based on previous frames

        // Iterates over frames.
        for (int i = 0; i < frames.size(); i++) {
            Frame frame = frames.get(i);

            // Display rolls' information for specific frame.
            System.out.println("Frame " + frame.getFrameNumber() + ":");
            System.out.println("  First Roll: " + frame.displayFirstRoll());
            System.out.println("  Second Roll: " + frame.displaySecondRoll());

            // Third Roll (only for 10th frame)
            if (frame.getFrameNumber() == 10 && frame.getThirdRoll() != null) {
                System.out.println("  Third Roll: " + frame.displayThirdRoll());
            }

            // Calculate the score for the current frame
            Integer frameScore = getFrameScore(i);
            if (frameScore != null && canUpdate) {
                runningTotal += frameScore;
                totalScore = runningTotal; // Update the totalScore field
                System.out.println("  Running Total: " + runningTotal);
            } else {
                if (frame.isSpare() || frame.isStrike()) {
                    System.out.println("  Running Total: - (Waiting Bonus)");
                } else {
                    System.out.println("  Running Total: -");
                }
                canUpdate = false; // Following frames may depend on this frame's bonuses.
            }

            System.out.println(); // Empty line for easier readability.
        }

        System.out.println("======================\n");
    }

    /**
     * Calculates the score for a frame, including bonuses for strikes and spares
     * according to the bowling rules provided.
     *
     * @param index The index of the frame.
     * @return The score for the frame or null if bonuses are not yet available.
     */
    private Integer getFrameScore(int index) {
        Frame currentFrame = frames.get(index);

        // Handle frames 1 to 9
        if (currentFrame.getFrameNumber() < 10) {
            if (currentFrame.isStrike()) { // Check if it is a strike
                // Score is 10 + the next two rolls
                Integer bonus = getNextTwoRolls(index);
                return (bonus != null) ? 10 + bonus : null;
            } else if (currentFrame.isSpare()) { // Checks if the frame is a spare
                // Score is 10 + the next one roll
                Integer bonus = getNextRoll(index);
                return (bonus != null) ? 10 + bonus : null;
            } else { // If not either
                // Score = First Roll + Second Roll
                if (currentFrame.getSecondRoll() != null) {
                    return currentFrame.getFirstRoll() + currentFrame.getSecondRoll();
                } else {
                    // Second roll not yet registered.
                    return null;
                }
            }
        } else {
            // Handle the 10th frame
            if (currentFrame.isComplete()) { // Checks if last frame is complete.
                // Score = First Roll + Second Roll + Third Roll (If applicable).
                int score = currentFrame.getFirstRoll();
                if (currentFrame.getSecondRoll() != null) {
                    score += currentFrame.getSecondRoll();
                }
                if (currentFrame.getThirdRoll() != null) {
                    score += currentFrame.getThirdRoll();
                }
                return score;
            } else {
                // 10th frame not complete, return null.
                return null;
            }
        }
    }

    /**
     * Retrieves the sum of the next two rolls after the current frame for strike bonus calculation.
     *
     * @param index The index of the current frame.
     * @return Bonus points for a strike (sum of next two rolls)
     */
    private Integer getNextTwoRolls(int index) {
        if (index + 1 >= frames.size()) {
            // No next frame available
            return null;
        }

        Frame nextFrame = frames.get(index + 1); // Goes over to next frame.

        if (nextFrame.isStrike()) {  // If the next frame is a strike

            if (index + 2 >= frames.size()) { // No second next frame available for second bonus roll
                return null;
            }

            Frame frameAfterNext = frames.get(index + 2); // Second next frame.
            if (frameAfterNext.getFirstRoll() != null) { // Checks if second bonus roll is registered.
                return 10 + frameAfterNext.getFirstRoll(); // Bonus = 10 (strike) + second bonus roll.
            } else {
                // Second bonus roll not yet recorded
                return null;
            }
        } else {
            // Next frame is not a strike
            if (nextFrame.getFirstRoll() != null && nextFrame.getSecondRoll() != null) { // If two rolls have been recorded.
                return nextFrame.getFirstRoll() + nextFrame.getSecondRoll(); // Bonus = Roll1 + Roll2
            } else { // One or both bonus rolls not recorded
                return null;
            }
        }
    }

    /**
     * Retrieves the next roll after the current frame for spare bonus calculation.
     *
     * @param index The index of the current frame.
     * @return Bonus points for a spare (next roll).
     */
    private Integer getNextRoll(int index) {
        if (index + 1 >= frames.size()) {
            // No next frame available
            return null;
        }

        Frame nextFrame = frames.get(index + 1);
        return nextFrame.getFirstRoll();
    }
}