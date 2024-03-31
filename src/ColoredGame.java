import javax.swing.*;

/**
 * Represents a game using the Coloured variant
 */
public class ColoredGame extends Game {
    public ColoredGame(JFrame parent, int level) throws Exception {
        this(parent, level, null);
    }

    /**
     * Creates a new ColoredGame with the given parent, level and initial board
     *
     * @param parent JFrame to add this game to
     * @param level  Selected level integer
     * @param board  Initial board to use
     * @throws Exception where the level cannot be read from file
     */
    public ColoredGame(JFrame parent, int level, Board board) throws Exception {
        super(parent, level, board);
    }

    /**
     * Updates the level display on the UI
     * It is within a separate method to allow it to be overridden.
     */
    @Override
    protected void updateLevelDisplay() {
        this.getLevelLabel().setText("Coloured Level: " + this.getCurrentLevel());
    }

    /**
     * Retrieves the current level from the levels folder
     *
     * @throws Exception Where an IO Exception occurs or a file is invalid
     */
    @Override
    protected void setLevelFromFile() throws Exception {
        // Read the file
        Level levelInfo = LevelReader.readColouredLevelFile("level" + this.getCurrentLevel());
        // Set new board and time limit
        Button[][] buttons = levelInfo.getButtons();
        this.setBoard(new Board(this, true, buttons));
        this.setTimeLimit(levelInfo.getTimeLimit());
    }

}
