import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Entry point for LightsOut. Contains the start menu and manages the creation of various sub-classes
 * dependent on which buttons the user uses.
 */
public class MainFrame {
    public final static Color BACKGROUND_COLOR = Color.white;
    public final static Color SECONDARY_BACKGROUND = BACKGROUND_COLOR.darker();
    private final static String ICON_PATH = "icon.png";
    private final static Dimension MAIN_SIZE = new Dimension(800, 600);
    private final static Dimension BUTTON_SIZE = new Dimension(160, 80);
    private Game currentGame;
    private BoardEditor editor;
    // Variant options
    private boolean colouredMode = false;
    private boolean timeLimit = false;

    public MainFrame() {
        JFrame frame = new JFrame();
        frame.setTitle("Lights Out");
        frame.setSize(MAIN_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        try{
            // Icon set code from https://stackoverflow.com/questions/7194734/how-to-set-icon-image-for-swing-application
            // Accessed 05/04/21
            // START COPIED CODE
            frame.setIconImage(ImageIO.read(new File("icon.png")));
            // END COPIED CODE
        } catch (IOException error) {
            // not reported to user via. popup as it is not a serious issue
            System.out.println("Failed to set icon due to error");
            error.printStackTrace();
        }

        // Create a Panel to contain main menu buttons for ease of showing/hiding
        JPanel menuPanel = new JPanel();
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        // Set the layout to a vertical box layout
        BoxLayout layout = new BoxLayout(menuPanel, BoxLayout.Y_AXIS);
        menuPanel.setLayout(layout);
        //menuPanel.setSize(mainSize);
        menuPanel.setOpaque(false);

        //create header of main menu
        JLabel header = new JLabel();
        header.setText("Lights Out");
        header.setFont(new Font("", Font.PLAIN, 48));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        //create button to start a game
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(BUTTON_SIZE);
        startButton.setMaximumSize(startButton.getPreferredSize());
        startButton.setFont(new Font("", Font.PLAIN, 22));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFocusPainted(false);

        //create button to take user to the board editor
        JButton boardEditorButton = new JButton("Board Editor");
        boardEditorButton.setPreferredSize(BUTTON_SIZE);
        boardEditorButton.setMaximumSize(startButton.getPreferredSize());
        boardEditorButton.setFont(new Font("", Font.PLAIN, 18));
        boardEditorButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //create label to ask user to select a level
        JLabel selectLevelLabel = new JLabel("Select Level:");
        selectLevelLabel.setFont(new Font("", Font.PLAIN, 22));
        selectLevelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //create drop down menu where the level can be selected
        JComboBox<Integer> levelSelector = new JComboBox<>();
        levelSelector.setPreferredSize(new Dimension(120, 25));
        levelSelector.setFont(new Font("", Font.PLAIN, 14));
        levelSelector.setMaximumSize(levelSelector.getPreferredSize());
        for (int i = 1; i < 16; i++) {
            levelSelector.addItem(i);
        }

        JPanel checkboxes = new JPanel();
        JCheckBox timeLimit = new JCheckBox("Apply time limit", this.getTimeLimitEnabled());
        JCheckBox colouredBoard = new JCheckBox("Use multi-coloured variant", this.getColouredEnabled());
        timeLimit.addActionListener(e -> this.setTimeLimitEnabled(!this.timeLimit));
        colouredBoard.addActionListener(e -> this.setColoured(!this.colouredMode));
        checkboxes.setOpaque(false);
        timeLimit.setOpaque(false);
        colouredBoard.setOpaque(false);
        checkboxes.add(timeLimit);
        checkboxes.add(colouredBoard);

        //create back button to be used to go back to the main menu
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(80, 40));
        backButton.setMaximumSize(startButton.getPreferredSize());
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setVisible(false);


        //add method to start button to start a game
        // currentGame has to be an attribute because it is accessed in other methods, and
        // lambda functions cannot access local variables due to where they are stored in memory.
        startButton.addActionListener(e -> {
            try {
                // Create a new game and update the UI accordingly
                int selectedLevel = levelSelector.getItemAt(levelSelector.getSelectedIndex());
                if (colouredMode) {
                    currentGame = new ColoredGame(frame, selectedLevel);
                } else {
                    currentGame = new Game(frame, selectedLevel);
                }
                if (getTimeLimitEnabled()) {
                    currentGame.enableTimeLimit();
                }

                menuPanel.setVisible(false);
                backButton.setVisible(true);
                currentGame.start();
            } catch (Exception error) {
                System.out.println("Failed to start game to error: ");
                JOptionPane.showMessageDialog(frame, error.getMessage(),
                        "Error",JOptionPane.ERROR_MESSAGE);
                error.printStackTrace();
            }

        });

        //add method to board editor button to take user to the board editor
        boardEditorButton.addActionListener(e -> {
            editor = new BoardEditor(frame, board -> {
                if (board != null) {
                    board.setUpdateSiblings(true);
                    try {
                        board.getConsumer().cleanUp();
                        // create game with level advancement disabled
                        Game game = new Game(frame, -1, board);

                        game.start();
                        currentGame = game;
                    } catch (Exception error) {
                        System.out.println("Failed to switch to playable game! ");
                        error.printStackTrace();
                        JOptionPane.showMessageDialog(frame, error.getMessage(),
                                "Error",JOptionPane.ERROR_MESSAGE);

                    }

                } else {
                    // It was cancelled
                    menuPanel.setVisible(true);
                }
            });
            menuPanel.setVisible(false);
            backButton.setVisible(true);
        });

        // Add method to take user back to the main menu
        backButton.addActionListener(e -> {
            try {
                //set all main menu components to visible
                menuPanel.setVisible(true);

                //set back button to invisible
                backButton.setVisible(false);

                if (currentGame != null) {
                    currentGame.cleanUp();
                    currentGame = null;
                }

                //if board editor is open, set all board editor components to invisible
                if (editor != null) {
                    editor.cleanUp();
                    editor = null;
                }

            } catch (Exception error) {
                System.out.println("Failed to go back to main menu due to error: ");
                error.printStackTrace();
                JOptionPane.showMessageDialog(frame, error.getMessage(),
                        "Error",JOptionPane.ERROR_MESSAGE);
            }

        });

        // Add elements to the panel, with variable gaps in-between them
        frame.add(backButton);
        menuPanel.add(header);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(startButton);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(boardEditorButton);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(selectLevelLabel);
        menuPanel.add(levelSelector);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(checkboxes);
        menuPanel.add(Box.createVerticalGlue());

        // Make frame visible
        frame.add(menuPanel);
        frame.setVisible(true);
    }

    /**
     * Starts the program by instantiating a new main frame
     * @param args Program arguments
     */
    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
    }

    /**
     * Enables or disables the coloured variant
     * @param newState boolean representing whether to use the coloured variant or not
     */
    public void setColoured(boolean newState) {
        this.colouredMode = newState;
    }

    /**
     * Retrieve boolean indicating whether time limit should be applied
     * @return boolean
     */
    public boolean getTimeLimitEnabled() {
        return this.timeLimit;
    }

    /**
     * Enables or disables the time limit applied to the current level.
     * @param newState Boolean indicating if the time limit should be applied
     */
    public void setTimeLimitEnabled(boolean newState) {
        this.timeLimit = newState;
        if (currentGame != null) {
            if (newState) {
                currentGame.enableTimeLimit();
            } else {
                currentGame.disableTimeLimit();
            }
        }
    }

    /**
     * Retrieves whether coloured mode is enabled
     * @return boolean indicating if a ColouredGame should be used
     */
    public boolean getColouredEnabled() {
        return this.colouredMode;
    }
}
