import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents a board button. This class is generic and can represent either a coloured or normal
 * button with required methods implemented.
 */
public abstract class Button extends JButton implements ActionListener {
    protected static final Color OFF_COLOR = Color.darkGray;
    private static final int BUTTON_SIZE = 50;
    private final int row;
    private final int column;
    private Board board;

    /**
     * Create a new button with the specified
     *
     * @param board  The initial board to use
     * @param row    The row position of this button. Only used to pass it to the board when clicked.
     * @param column position of this button. Only used to pass it to the board when clicked.
     */
    public Button(Board board, int row, int column) {
        this(row, column);
        this.setBoard(board);
    }

    /**
     * Create a button with no initial board
     *
     * @param row    position of this button
     * @param column position of this button.
     */
    public Button(int row, int column) {
        this.row = row;
        this.column = column;

        // Button setup that is common to all types
        // Assigning these here assists with consistency between button/game variants.
        this.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        this.setBackground(OFF_COLOR);
        this.addActionListener(this);
    }


    /**
     * Alter the state of this button and of those around it - used when a button is clicked.
     * Causes update to be run on this button and those above, left, right and below by directing
     * the board to carry these updates out
     */
    public void activate() {
        this.update();
        board.handleActivation(this.row, this.column);
    }

    /**
     * Update this button alone without altering the surrounding buttons
     */
    public abstract void update();

    /**
     * Determines if this button is lit
     *
     * @return boolean indicating whether this button is lit - that is, whether it's state is not
     * equal to off.
     */
    public abstract boolean isOn();

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.activate();
    }

}
