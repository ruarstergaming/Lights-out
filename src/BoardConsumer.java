import javax.swing.*;

/**
 * Represents something that makes use of a board. Sets out some required basic methods that the
 * board requires to exist so it can inform the parent of certain events such as buttons being
 * activated
 */
public interface BoardConsumer {
    /**
     * Inform the parent of a board state change
     */
    void handleUpdate();

    JPanel getPanel();

    /**
     * Remove the board consumer and stop any internal state, like timers.
     */
    void cleanUp();
}
