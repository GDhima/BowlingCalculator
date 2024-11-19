/**
 * Frame class represents a single frame in a bowling game.
 * Keeps track of the number of pins knocked down in each roll while also identifying strikes and spares.
 * Also provides methods to display each roll in the frame.
 */
public class Frame {
    // Private fields containing number of pins knocked each roll.
    private Integer firstRoll;
    private Integer secondRoll;
    private Integer thirdRoll; // Used in 10th frame only

    // Flags indicating whether the frame is a strike or spare.
    private boolean isStrike;
    private boolean isSpare;

    // Frame number in the bowling game.
    private final int frameNumber;

    /**
     * Creates a Frame instance with the specified frame number.
     *
     * @param frameNumber The number of the frame in the game.
     */
    public Frame(int frameNumber) {
        this.frameNumber = frameNumber;
        this.firstRoll = null;
        this.secondRoll = null;
        this.thirdRoll = null;
        this.isStrike = false;
        this.isSpare = false;
    }

    /**
     * Sets the number of pins knocked down in the first roll and
     * updates the strike flag accordingly if all 10 pins are knocked down
     *
     * @param pins The number of pins knocked down.
     */
    public void setFirstRoll(int pins) {
        this.firstRoll = pins;
        if (pins == 10 && frameNumber < 10) {
            isStrike = true;
        }
    }

    /**
     * Sets the number of pins knocked down in the second roll and
     * updates the spare flag accordingly if all 10 pins are knocked down.
     *
     * @param pins The number of pins knocked down.
     */
    public void setSecondRoll(int pins) {
        this.secondRoll = pins;
        if (frameNumber < 10 && firstRoll != null) {
            if (firstRoll + pins == 10 && firstRoll != 10) {
                isSpare = true;
            }
        }
    }

    /**
     * Sets the number of pins knocked down in the third (bonus) roll
     * in 10th frame.
     *
     * @param pins The number of pins knocked down.
     */
    public void setThirdRoll(int pins) {
        this.thirdRoll = pins;
    }

    /**
     * Getters for each field.
     */

    public Integer getFirstRoll() {
        return firstRoll;
    }

    public Integer getSecondRoll() {
        return secondRoll;
    }

    public Integer getThirdRoll() {
        return thirdRoll;
    }

    public boolean isStrike() {
        return isStrike;
    }

    public boolean isSpare() {
        return isSpare;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    /**
     * Determines if the frame is complete taking into consideration bonus rolls as well.
     *  Frames 1-9: A strike completes a frame, or both rolls must be completed.
     *  Frame 10: 
     *      - If the first roll is a strike, two additional rolls needed.
     *      - If it is a spare in first two rolls, another roll is needed to be completed.
     *      - Otherwise, no bonus roll, and frame is completed.
     * @return True if the frame is complete, false otherwise.
     */
    public boolean isComplete() {
        if (frameNumber < 10) {
            if (isStrike) {
                return true;
            } else {
                return firstRoll != null && secondRoll != null;
            }
        } else {
            if (firstRoll != null && firstRoll == 10) {
                // If first roll is a strike, need two more rolls
                return secondRoll != null && thirdRoll != null;
            } else if (firstRoll != null && secondRoll != null) {
                if (firstRoll + secondRoll == 10) {
                    // If spare, need one more roll
                    return thirdRoll != null;
                } else {
                    // Open frame, no bonus roll
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * Returns display string for first roll.
     * Legend:
     *      - "X" for strikes
     *      - "-" not yet rolled.
     *      - "Number of pins knocked."
     * @return String displaying first roll result.
     */
    public String displayFirstRoll() {
        if (firstRoll == null) {
            return "-";
        } else if (firstRoll == 10 && frameNumber != 10) {
            return "X";
        } else {
            return String.valueOf(firstRoll);
        }
    }


    /**
     * Returns display string for second roll.
     * Legend:
     *      - "/" for spares.
     *      - "-" if not yet rolled or no roll needed.
     *      - "X" for strikes in 10th frame bonus rolls.
     *      - "Number of pins knocked".
     * @return String displaying second roll result.
     */
    public String displaySecondRoll() {
        if (secondRoll == null) {
            return "-";
        }
        if (frameNumber < 10) {
            if (isSpare()) {
                return "/";
            } else if (isStrike()) {
                return "-";
            } else {
                return String.valueOf(secondRoll);
            }
        } else {
            if (firstRoll == 10) {
                if (secondRoll == 10) {
                    return "X";
                } else if (firstRoll + secondRoll == 10) {
                    return "/";
                } else {
                    return String.valueOf(secondRoll);
                }
            } else {
                if (firstRoll + secondRoll == 10) {
                    return "/";
                } else {
                    return String.valueOf(secondRoll);
                }
            }
        }
    }

    /**
     * Returns display string for third roll.
     * Legend:
     *      - "/" for spares.
     *      - "-" if not yet rolled or no roll needed.
     *      - "X" for strikes in 10th frame bonus rolls.
     *      - "Number of pins knocked".
     * @return String displaying third roll result.
     */
    public String displayThirdRoll() {
        if (thirdRoll == null) {
            return "-";
        }else if (thirdRoll == 10) {
            return "X";
        }else if (secondRoll != null && secondRoll != 10 && secondRoll + thirdRoll == 10) {
            return "/";
        }else{
            return String.valueOf(thirdRoll);
        }
    }
}
