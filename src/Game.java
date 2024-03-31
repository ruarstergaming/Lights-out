import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a play of the game. Can have a time limit, but cannot support the Coloured variant.
 */
public class Game implements BoardConsumer {
    private static final Dimension GAME_SIZE = new Dimension(500, 700);
    private static final Dimension BOTTOM_SIZE = new Dimension(300, 190);
    /**
     * In milliseconds. 1000ms = 1s
     */
    private static final int UPDATE_DELAY = 1000;
    private final JLabel moveDisplay;
    private final JPanel gamePanel;
    private final JLabel levelLabel;
    private final JFrame parent;
    private final JPanel bottom;

    /**
     * Represents when time when the current level was started
     */
    private LocalDateTime start;
    private Timer textUpdater = null;
    private Board board;
    private int currentLevel;
    private int moveCount = 0;

    private int timeLimit = 0;
    private boolean timeLimitEnabled = false;

    /**
     * Creates a new game with the given parent and initial level
     *
     * @param parent The JFrame to add this game to
     * @param level  The initial level to start at
     * @throws Exception Where reading the level from file fails
     */
    public Game(JFrame parent, int level) throws Exception {
        this(parent, level, null);
    }

    /**
     * Creates a new game and adds it to the parent. Starts at the provided level, and uses the
     * passed board.
     *
     * @param parent The JFrame to add this game to
     * @param level  The level start the game at
     * @param board  The initial board to display
     * @throws Exception Where a level cannot be read from file.
     */
    public Game(JFrame parent, int level, Board board) throws Exception {
        // Create panel
        gamePanel = new JPanel();
        BoxLayout gamePanelLayout = new BoxLayout(gamePanel, BoxLayout.Y_AXIS);
        gamePanel.setLayout(gamePanelLayout);
        gamePanel.setSize(GAME_SIZE);
        gamePanel.setBackground(MainFrame.BACKGROUND_COLOR);

        this.currentLevel = level;
        this.parent = parent;

        // Use a pre-set board if one is provided.
        if (board != null) {
            this.board = board;
            // Ensure consumer is set
            board.setConsumer(this);
            // Disable auto progression
            this.currentLevel = -1;
        } else {
            // Load buttons
            this.setLevelFromFile();
        }


        this.currentLevel = level;
        // UI Components
        //create label to display level number
        levelLabel = new JLabel("Level: " + currentLevel);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelLabel.setFont(new Font("", Font.PLAIN, 18));

        // Bottom part
        bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        bottom.setSize(BOTTOM_SIZE);
        bottom.setPreferredSize(BOTTOM_SIZE);
        bottom.setMaximumSize(BOTTOM_SIZE);
        bottom.setOpaque(false);

        moveDisplay = new JLabel();
        moveDisplay.setText("Moves: 0");

        JLabel timeTaken = new JLabel();
        timeTaken.setText("Time taken: 00:00");

        // Timer will update the text every second - 1000ms.
        // A startTime is used to prevent any slipping from actual time.
        textUpdater = new Timer(UPDATE_DELAY, e -> {
            // Variables used for display and calculations
            LocalDateTime now = LocalDateTime.now();
            Duration elapsed = Duration.between(start, now);
            int seconds = elapsed.toSecondsPart();
            int minutes = elapsed.toMinutesPart();

            // Default time taken string
            String timeTakenStr = "Time taken: " + minutes + ":" + seconds;
            if (!timeLimitEnabled) {
                timeTaken.setText(timeTakenStr);
            } else {
                // Time limit is enabled - work out if they have time remaining and display time left
                LocalDateTime finishTime = start.plusSeconds(timeLimit);
                Duration durationTillEnd = Duration.between(now, finishTime);

                int secondsLeft = durationTillEnd.toSecondsPart();
                int minutesLeft = durationTillEnd.toMinutesPart();

                if (now.isAfter(finishTime)) {
                    // Time is up
                    timeTaken.setText("Times up!");
                    if (textUpdater != null) {
                        textUpdater.stop();
                    }

                    this.board.disable();
                    // todo: show popup to indicate failure
                } else {
                    timeTaken.setText(timeTakenStr + " - " + minutesLeft + ":" + secondsLeft + " left");

                    if (durationTillEnd.toSeconds() <= 10) {
                        timeTaken.setForeground(Color.red);
                    }
                }

            }

        });

        textUpdater.setRepeats(true);

        bottom.add(moveDisplay, BorderLayout.LINE_START);
        bottom.add(timeTaken, BorderLayout.LINE_END);

        // Strut for spacing
        parent.add(levelLabel);

        parent.add(gamePanel);

        parent.add(bottom);
    }

    /**
     * Start this game, reset applicable state and start the game timer.
     */
    public void start() {
        start = LocalDateTime.now();
        textUpdater.start();
        moveCount = 0;
    }

    /**
     * Display popup and move onto the next level if enabled
     */
    public void setWon() {
        this.start();

        // -1 means level progression is disabled
        if (this.currentLevel != -1) {

            //if current level is 15, display a win message with no options
            if (this.currentLevel == 15) {
                //create popup to display final win message
                JPopupMenu finalWinPopup = new JPopupMenu();
                finalWinPopup.setPreferredSize(new Dimension(300, 300));

                //create final win message
                JLabel winMessage = new JLabel("Level 15 complete!");
                winMessage.setFont(new Font("", Font.PLAIN, 24));
                winMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

                //adding final win message
                finalWinPopup.add(winMessage);

                //display popup
                finalWinPopup.show(parent, 250, 115);

            }
            else {

                //create a popup menu that displays a win message and gives the user the option to go to the next level
                JPopupMenu winPopup = new JPopupMenu();
                winPopup.setPreferredSize(new Dimension(300, 300));

                //create win message
                JLabel winMessage = new JLabel("Level " + this.currentLevel + " complete!");
                winMessage.setFont(new Font("", Font.PLAIN, 24));
                winMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

                //create button to go to next level
                JButton nextLevelButton = new JButton("Go to level " + (this.currentLevel + 1));
                nextLevelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                nextLevelButton.setPreferredSize(new Dimension(160, 50));
                nextLevelButton.setMaximumSize(new Dimension(160, 50));
                nextLevelButton.setFont(new Font("", Font.PLAIN, 18));

                //create button to stay at current level
                JButton stayButton = new JButton("Stay at level " + this.currentLevel);
                stayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                stayButton.setPreferredSize(new Dimension(160, 50));
                stayButton.setMaximumSize(new Dimension(160, 50));
                stayButton.setFont(new Font("", Font.PLAIN, 18));

                //add method to next level button to go to next level
                nextLevelButton.addActionListener(e -> {
                    try {
                        //go to next level
                        this.currentLevel++;
                        
                        this.updateLevelDisplay();
                        //remove previous board
                        gamePanel.remove(board.getBoard());

                        //remove popup
                        winPopup.setVisible(false);

                        try {
                            this.setLevelFromFile();

                        } catch (Exception err) {
                            System.out.println("Failed to find next board ");
                            err.printStackTrace();
                        }

                    } catch (Exception error) {
                        System.out.println("Failed to go to next level due to error:");
                        System.out.println(error.getMessage());
                        error.printStackTrace();
                    }
                });

                //add method to stay button to go to next level
                stayButton.addActionListener(e -> {
                    try {
                        this.updateLevelDisplay();
                        //remove previous board
                        gamePanel.remove(board.getBoard());

                        //remove popup
                        winPopup.setVisible(false);

                        try {
                            this.setLevelFromFile();

                        } catch (Exception err) {
                            System.out.println("Failed to find next board ");
                            err.printStackTrace();
                        }

                    } catch (Exception error) {
                        System.out.println("Failed to stay at current level due to error:");
                        System.out.println(error.getMessage());
                        error.printStackTrace();
                    }

                });

                //add components to popup
                winPopup.add(winMessage);
                winPopup.add(Box.createVerticalGlue());
                winPopup.add(nextLevelButton);
                winPopup.add(Box.createVerticalGlue());
                winPopup.add(stayButton);
                winPopup.add(Box.createVerticalGlue());

                //display popup
                winPopup.show(parent, 250, 115);
            
            }
        }
        //else for board editor display custom win message
        else {
            //create popup to display custom win message
            JPopupMenu customWinPopup = new JPopupMenu();
            customWinPopup.setPreferredSize(new Dimension(300, 300));

            //create custom win message
            JLabel winMessage = new JLabel("Custom level complete!");
            winMessage.setFont(new Font("", Font.PLAIN, 24));
            winMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

            //adding custom win message
            customWinPopup.add(winMessage);

            //display popup
            customWinPopup.show(parent, 250, 115);
        }
    }

    /**
     * Performs cleanup of timers etc.
     */
    public void cleanUp() {
        textUpdater.stop();
        this.parent.remove(this.getPanel());
        this.parent.remove(levelLabel);
        this.parent.remove(bottom);
    }

    /**
     * Retrieve the JPanel this game is using
     *
     * @return JPanel this game is using
     */
    public JPanel getPanel() {
        return gamePanel;
    }

    /**
     * Set the time limit for the current level. This will only be applied if the time limit is
     * also enabled
     *
     * @param timeRemaining int representing the time limit for this level, in seconds
     */
    public void setTimeLimit(int timeRemaining) {
        this.timeLimit = timeRemaining;
    }

    /**
     * Enable the time limit that is set for this level
     */
    public void enableTimeLimit() {
        this.timeLimitEnabled = true;
    }

    /**
     * Disable the time limit
     */
    public void disableTimeLimit() {
        this.timeLimitEnabled = false;
    }

    /**
     * Update move display and run setWon if the board has been solved
     * Called whenever a button in the board is clicked
     */
    @Override
    public void handleUpdate() {
        this.moveCount++;
        this.moveDisplay.setText("Moves: " + this.moveCount);
        if (this.board.isSolved()) {
            this.setWon();
        }
    }

    /**
     * Updates the level display on the UI
     * It is within a separate method to allow it to be overridden.
     */
    protected void updateLevelDisplay() {
        levelLabel.setText("Level: " + currentLevel);
    }


    /**
     * Retrieves the current level from file
     *
     * @throws Exception Where the level is missing or invalid
     */
    protected void setLevelFromFile() throws Exception {
        Level levelInfo = LevelReader.readLevelFile("level" + this.currentLevel);
        Button[][] buttons = levelInfo.getButtons();
        this.board = new Board(this, true, buttons);
        this.timeLimit = levelInfo.getTimeLimit();
    }

    /**
     * Retrieve the current level number
     *
     * @return int representing current level (1-16)
     */
    public int getCurrentLevel() {
        return currentLevel;
    }


    /**
     * Set the game board to the provided board with no additional effects
     *
     * @param board The board to set
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Retrieve the levelLabel which displays the currently active level
     *
     * @return JLabel which displays the current level
     */
    public JLabel getLevelLabel() {
        return levelLabel;
    }

}
