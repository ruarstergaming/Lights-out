import javax.swing.*;
import java.awt.*;

/**
 * Represents a game board. It is used to represent boards that are being edited, boards in play and
 * boards across all variants. Whether or not it is a coloured board is determined by the type of
 * buttons which are passed in.
 */
public class Board {
    /**
     * The number of buttons wide/high the board is.
     */
    protected final static int BOARD_SIZE = 5;
    /**
     * Pixels between each button
     */
    private final static int BOARD_GAP = 5;

    private final static int BOARD_HEIGHT = 300;
    private final static int BOARD_WIDTH = 300;

    private final Button[][] buttons;
    private final JPanel board;
    private boolean shouldUpdateSiblings;
    private BoardConsumer consumer;

    /**
     * Creates a new board with a standard arrangement of unlit normal buttons.
     *
     * @param consumer       The Board consumer which has created this board. Either Game or BoardEditor.
     * @param updateSiblings Whether or not adjacent lights should also be altered on click.
     *                       For actual games, this is always true. When editing the board, it is not.
     */
    public Board(BoardConsumer consumer, boolean updateSiblings) {
        this(consumer, updateSiblings, null);
    }

    /**
     * Create a new board with the given buttons. This allows pre-set configurations (such as levels)
     * to be passed. If initialButtons is null, a grid of NormalButtons is generated.
     *
     * @param consumer       - The Board consumer which has created this board. Either Game or BoardEditor.
     * @param updateSiblings - Whether or not adjacent lights should also be altered on click.
     *                       For actual games, this is always true. When editing the board, it is not.
     * @param initialButtons - The Buttons to initially populate the board with.
     */
    public Board(BoardConsumer consumer, boolean updateSiblings, Button[][] initialButtons) {
        this.shouldUpdateSiblings = updateSiblings;
        this.consumer = consumer;

        // Call JPanel & set initial properties
        this.board = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, BOARD_GAP, BOARD_GAP));
        // https://docs.oracle.com/javase/tutorial/uiswing/layout/grid.html
        this.board.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        this.board.setMaximumSize(this.board.getSize());
        this.board.setBackground(MainFrame.SECONDARY_BACKGROUND);

        // Add to parent UI
        // Consumer is not always present in some cases, like where this board is being used to
        // Test solvability
        if (consumer != null) {
            consumer.getPanel().add(this.board);
        }

        if (initialButtons != null) {
            buttons = initialButtons;
        } else {
            // Generate some buttons - no initial was provided
            buttons = new NormalButton[BOARD_SIZE][BOARD_SIZE];
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int column = 0; column < BOARD_SIZE; column++) {
                    buttons[row][column] = new NormalButton(this, row, column);
                }
            }
        }

        // Construct display all buttons and ensure they hold the correct board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                this.board.add(buttons[row][column]);
                buttons[row][column].setBoard(this);
            }
        }
    }


    /**
     * Retrieve the Button at the given location.
     *
     * @param row    - Row position of the button
     * @param column - Column position of the button
     * @return Button at the given location. This is either a NormalButton or a ColouredButton.
     */
    public Button getButton(int row, int column) {
        return this.buttons[row][column];
    }


    /**
     * Updates the buttons adjacent to a given button which has been activated.
     * Does not update the button at the position itself. This method will not update adjacent buttons
     * or 'siblings', if 'updateSiblings' is set to false. It will always inform the consumer - the
     * Game or board editor - that an update has occurred.
     *
     * @param row    - The row position of the button which has been clicked.
     * @param column - The column number of the button which has been clicked.
     */
    public void handleActivation(int row, int column) {
        BoardConsumer consumer = this.getConsumer();
        // If a consumer is set and siblings should not be updated, stop early and inform the parent.
        if (!shouldUpdateSiblings && consumer != null) {
            consumer.handleUpdate();
            return;
        }
        // Button above
        if (row - 1 >= 0) {
            buttons[row - 1][column].update();
        }
        // Button below
        if (row + 1 < BOARD_SIZE) {
            buttons[row + 1][column].update();
        }
        // Button to the left
        if (column - 1 >= 0) {
            buttons[row][column - 1].update();
        }

        // Button to the right
        if (column + 1 < BOARD_SIZE) {
            buttons[row][column + 1].update();
        }
        if (consumer != null) {
            this.getConsumer().handleUpdate();
        }

    }

    /**
     * Determines whether this board is 'solved' - it is solved when all lights are out.
     *
     * @return boolean indicating if all buttons are 'off', using Button.isOn.
     */
    public boolean isSolved() {
        // If any buttons are lit, it is not solved.
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (this.getButton(row, col).isOn()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Set whether adjacent buttons should be altered on click.
     * Generally true for a normal game, and false for an editing board.
     *
     * @param shouldUpdateSiblings Boolean whether sibling buttons should be affected by an activation
     */
    public void setUpdateSiblings(boolean shouldUpdateSiblings) {
        this.shouldUpdateSiblings = shouldUpdateSiblings;
    }

    /**
     * Get the consumer (Game or BoardEditor for example) that is using this board
     *
     * @return BoardEditor which using this object.
     */
    public BoardConsumer getConsumer() {
        return consumer;
    }

    /**
     * set the consumer of this board and add the board to it's Panel.
     *
     * @param consumer to add this board to. Such as a game or board editor.
     */
    public void setConsumer(BoardConsumer consumer) {
        consumer.getPanel().add(this.getBoard());
        this.consumer = consumer;
    }

    /**
     * Retrieve the Board JPanel which contains the grid
     *
     * @return The current board panel
     */
    public JPanel getBoard() {
        return board;
    }

    /**
     * Disables the buttons within this board so that they can no longer be altered by clicking on
     * them. Uses JButton.setEnabled.
     */
    public void disable() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                this.getButton(row, col).setEnabled(false);
            }
        }
    }

    /**
     * Print the board grid to System.out
     * Useful for debugging - does not represent colours.
     */
    public void print() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                if (getButton(row, column).isOn()) {
                    System.out.print("X");
                } else {
                    System.out.print("O");
                }
            }
            System.out.println();
        }
    }

}