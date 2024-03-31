import java.awt.*;


/**
 * Represents a button with multiple colours, used within the ColouredGame variant
 * Uses american spelling of color for consistentency with Java APIs
 *
 * @see ColoredGame
 */
public class ColoredButton extends Button {
    private static final Color OFF_COLOR = Color.darkGray;
    private static final Color RED_COLOR = Color.red;
    private static final Color GREEN_COLOR = Color.green;
    private static final Color BlUE_COLOR = Color.blue;
    private static final Color[] COLORS = {GREEN_COLOR, OFF_COLOR, RED_COLOR, BlUE_COLOR};
    private int state = 0;

    /**
     * Create a new button with the provided initial state
     *
     * @param row          Position of this button
     * @param column       Position of this button
     * @param initialState int representing which colour to use - a int from 0 to 3. Invalid values
     *                     will result in an off state.
     */
    public ColoredButton(int row, int column, int initialState) {
        this(null, row, column, initialState);
    }

    /**
     * Create a new button with the provided initial state, and an initial board
     *
     * @param board        - The board this button is a part of
     * @param row          Position of this button
     * @param column       Position of this button
     * @param initialState - int representing which colour to use - a int from 0 to 3. Invalid values
     *                     will result in an off state.
     */
    public ColoredButton(Board board, int row, int column, int initialState) {
        super(board, row, column);
        this.setState(initialState);
        this.setBackground(COLORS[this.state]);
    }

    /**
     * Update the colour state of this button. Discards invalid values
     *
     * @param newState int representing new state - from 0 to 3.
     */
    public void setState(int newState) {
        if (newState >= 0 && newState < COLORS.length) {
            this.state = newState;
        }
    }


    /**
     * Toggle the state of this button alone
     */
    public void update() {
        if (this.state + 1 >= COLORS.length) {
            this.setState(1);
        } else {
            this.setState(this.state + 1);
        }
        this.setBackground(COLORS[this.state]);
    }

    /**
     * When state is 0, the colour is dark gray (and thus it is off)
     *
     * @return boolean representing whether or not this button is on/lit up.
     */
    @Override
    public boolean isOn() {
        if(this.state == 2 || this.state == 3){
            return true;
        }
        return false;
    }
}