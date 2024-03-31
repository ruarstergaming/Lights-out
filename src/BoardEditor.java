import javax.swing.*;
import java.awt.*;

/**
 * Allows a board to be edited and it's board to be extracted so it can be played.
 * Uses a lambda as a callback of sorts.
 */
public class BoardEditor implements BoardConsumer {
    private final Board board;
    private final JFrame frame;
    private final JLabel validLabel;
    private final JButton startGame;
    private final JPanel gamePanel;
    private final JPanel bottom;

    /**
     * Create a new Board editor and add it to Parent frame, and call lambda when editing is complete.
     *
     * @param parent     The JFrame to add this BoardEditor to
     * @param informMain A lambda to call when editing is complete.
     */
    public BoardEditor(JFrame parent, EditDone informMain) {
        frame = parent;
        gamePanel = new JPanel();
        BoxLayout gamePanelLayout = new BoxLayout(gamePanel, BoxLayout.Y_AXIS);
        gamePanel.setLayout(gamePanelLayout);
        gamePanel.setSize(500, 300);

        board = new Board(this, false);

        // Bottom part
        bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        bottom.setSize(300, 190);
        bottom.setPreferredSize(new Dimension(300, 50));
        bottom.setMaximumSize(new Dimension(300, 50));
        bottom.setOpaque(false);

        validLabel = new JLabel();

        BoardEditor editor = this;

        startGame = new JButton();
        startGame.setText("Start");
        startGame.addActionListener(e -> {
            if (editor.isSolvable()) {
                // Switch to a game
                parent.remove(bottom);
                informMain.editDone(board);
            }
        });
        startGame.setEnabled(false);

        bottom.add(validLabel, BorderLayout.LINE_START);
        bottom.add(startGame, BorderLayout.LINE_END);

        parent.add(gamePanel);
        parent.add(bottom);
    }

    /**
     * Returns a boolean if this board is solvable, by attempting to 'chase the lights' and then
     * checking the bottom row to see if it is a solvable combination
     *
     * @return Boolean indicating if the current board can be solved.
     * @see <a href="https://www.logicgamesonline.com/lightsout/tutorial.html">LogicGamesOnline</a>
     */
    public boolean isSolvable() {
        // Clone board
        Board testBoard = new Board(null, true);
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int column = 0; column < Board.BOARD_SIZE; column++) {
                Button button = this.board.getButton(row, column);
                if (button.isOn()) {
                    testBoard.getButton(row, column).update();
                }
            }
        }

        // Iterate top row and turn those below them on
        for (int row = 0; row < Board.BOARD_SIZE - 1; row++) {
            for (int column = 0; column < Board.BOARD_SIZE; column++) {
                Button button = testBoard.getButton(row, column);
                if (button.isOn()) {
                    // hit the button below it
                    testBoard.getButton(row + 1, column).activate();
                }
            }
        }

        // Check for bottom shapes
        int finalRow = Board.BOARD_SIZE - 1;
        boolean buttonOne = testBoard.getButton(finalRow, 0).isOn();
        boolean buttonTwo = testBoard.getButton(finalRow, 1).isOn();
        boolean buttonThree = testBoard.getButton(finalRow, 2).isOn();
        boolean buttonFour = testBoard.getButton(finalRow, 3).isOn();
        boolean buttonFive = testBoard.getButton(finalRow, 4).isOn();

        // Check if all are out:
        if (!buttonOne && !buttonTwo && !buttonThree && !buttonFour && !buttonFive) {
            return true;
        }

        // __+++ means it is solvable
        if (!buttonOne && !buttonTwo && buttonThree && buttonFour && buttonFive) {
            return true;
        }

        // _ + _ + _ is also solvable
        if (!buttonOne && buttonTwo && !buttonThree && buttonFour && !buttonFive) {
            return true;
        }

        // _++_+ is solvable
        if (!buttonOne && buttonTwo && buttonThree && !buttonFour && buttonFive) {
            return true;
        }

        // +___+ is solvable
        if (buttonOne && !buttonTwo && !buttonThree && !buttonFour && buttonFive) {
            return true;
        }

        // +_++_ is solvable
        if (buttonOne && !buttonTwo && buttonThree && buttonFour && !buttonFive) {
            return true;
        }

        // ++_++
        if (buttonOne && buttonTwo && !buttonThree && buttonFour && buttonFive) {
            return true;
        }

        // Final condition - if it is false then no solution was found.
        // +++__
        return buttonOne && buttonTwo && buttonThree && !buttonFour && !buttonFive;
    }

    /**
     * Set the solvable label based on whether the current board can be solved, or not.
     */
    public void handleUpdate() {
        if (this.isSolvable()) {
            validLabel.setText("Solvable!");
            validLabel.setForeground(Color.green);
            startGame.setEnabled(true);
        } else {
            validLabel.setForeground(Color.red);
            validLabel.setText("Not solvable");
            startGame.setEnabled(false);
        }
    }

    /**
     * Remove the BoardEditor from UI
     */
    public void cleanUp() {
        JPanel parent = this.getPanel();
        parent.removeAll();

        frame.remove(bottom);
        frame.remove(parent);
    }

    /**
     * Retrieve the current JPanel
     *
     * @return JPanel which the board editor is parented to
     */
    public JPanel getPanel() {
        return gamePanel;
    }

    /**
     * Lambda interface for when editing is complete
     */
    interface EditDone {
        void editDone(Board board);
    }
}
