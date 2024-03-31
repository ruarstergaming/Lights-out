// Imports
import java.io.File;
import java.util.Scanner;

/**
 * Thrown when a level is invalid due to a bad number of rows or columns, or invalid characters
 */
class InvalidLevelException extends Exception {
    private final String fileName;

    public InvalidLevelException(String message, String fileName) {
        super(message);
        this.fileName = fileName;
    }
}

/**
 * Represents a level that has been read from file, including it's buttons and the time limit
 */
class Level {
    private final Button[][] buttons;
    private final int timeLimit;

    /**
     * Create a new level with the
     *
     * @param buttons   2D array of buttons read from the file
     * @param timeLimit int representing the time limit in seconds
     */
    public Level(Button[][] buttons, int timeLimit) {
        this.timeLimit = timeLimit;
        this.buttons = buttons;
    }

    /**
     * Retrieve the buttons from this level
     *
     * @return Buttons 2D array, which can be passed to a board.
     */
    public Button[][] getButtons() {
        return buttons;
    }

    /**
     * Retrieve the time limit applied to this level
     *
     * @return int representing time limit, in seconds
     */
    public int getTimeLimit() {
        return timeLimit;
    }
}

public class LevelReader {
    public static Level readLevelFile(String fileName) throws Exception {

        NormalButton[][] levelButtons = new NormalButton[Board.BOARD_SIZE][Board.BOARD_SIZE];

        // Open file from https://www.javatpoint.com/how-to-read-csv-file-in-java, accessed 20 March 2021.
        // parsing a CSV file into Scanner class constructor
        // Start copied code
        Scanner levelScanner = new Scanner(new File("src/levels/" + fileName + ".csv"));
        // End copied code

        int i = 0;
        while (i < Board.BOARD_SIZE && levelScanner.hasNext()) {  //returns a boolean value
            String[] currentRow = levelScanner.next().split(",");
            // Invalid file - incorrect number of columns
            if (currentRow.length != Board.BOARD_SIZE) {
                throw new InvalidLevelException("Invalid level: Incorrect number of columns", fileName);
            }

            for (int j = 0; j < currentRow.length; j++) {
                boolean currentButtonState = false;

                //following line removes all non digits from the string as there is a hidden character in the first row and column of every level
                currentRow[j] = currentRow[j].replaceAll("[^0-9]", "");

                // Use each value to set the state of the buttons
                if (currentRow[j].equals("0")) {
                    currentButtonState = false;
                } else if (currentRow[j].equals("1")) {
                    currentButtonState = true;
                } else {
                    // This will throw for coloured levels too
                    throw new InvalidLevelException("Normal level contains invalid characters", fileName);
                }

                levelButtons[i][j] = new NormalButton(i, j, currentButtonState);
            }
            i++;
        }

        // Get time limit
        int timeLimit = 0;
        if (levelScanner.hasNext()) {
            timeLimit = levelScanner.nextInt();
            // More after timeLimit indicates it's invalid.
            if (levelScanner.hasNext()) {
                throw new InvalidLevelException("Level has invalid number of rows", fileName);
            }
        }

        levelScanner.close();  //closes the scanner  
        return new Level(levelButtons, timeLimit);

    }


    public static Level readColouredLevelFile(String fileName) throws Exception {

        ColoredButton[][] levelButtons = new ColoredButton[Board.BOARD_SIZE][Board.BOARD_SIZE];

        // Open file from https://www.javatpoint.com/how-to-read-csv-file-in-java, accessed 20 March 2021.
        // parsing a CSV file into Scanner class constructor
        // Start copied code
        Scanner levelScanner = new Scanner(new File("src/levels/coloured" + fileName + ".csv"));
        // End copied code

        int i = 0;
        while (i < Board.BOARD_SIZE && levelScanner.hasNext()) {  //returns a boolean value
            String[] currentRow = levelScanner.next().split(",");
            // Invalid file - incorrect number of columns
            if (currentRow.length != Board.BOARD_SIZE) {
                throw new InvalidLevelException("Invalid level: Incorrect number of columns", fileName);
            }

            for (int j = 0; j < currentRow.length; j++) {
                int currentButtonState = 0;

                // Following line removes all non digits from the string as there is a hidden
                // character in the first row and column of every level
                currentRow[j] = currentRow[j].replaceAll("[^0-9]", "");
                if (currentRow[j].equals("0")) {
                    currentButtonState = 0;
                } else if (currentRow[j].equals("1")) {
                    currentButtonState = 1;
                } else if (currentRow[j].equals("2")) {
                    currentButtonState = 2;
                } else if (currentRow[j].equals("3")) {
                    currentButtonState = 3;
                } else {
                    // This will throw for coloured levels too
                    throw new InvalidLevelException("Coloured level contains invalid characters", fileName);
                }

                levelButtons[i][j] = new ColoredButton(i, j, currentButtonState);
            }
            i++;
        }

        // Get time limit
        int timeLimit = 0;
        if (levelScanner.hasNext()) {
            timeLimit = levelScanner.nextInt();
            // More after timeLimit indicates it's invalid.
            if (levelScanner.hasNext()) {
                throw new InvalidLevelException("Level has invalid number of rows", fileName);
            }
        }

        levelScanner.close();  //closes the scanner  
        return new Level(levelButtons, timeLimit);
    }
}
