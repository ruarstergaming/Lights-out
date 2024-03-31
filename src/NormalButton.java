import java.awt.*;

public class NormalButton extends Button {
    private static final Color ON_COLOR = Color.green;
    private boolean state;

    public NormalButton(int row, int column, boolean state) {
        this(null, row, column, state);
    }

    public NormalButton(Board board, int row, int column) {
        this(board, row, column, false);
    }

    public NormalButton(Board board, int row, int column, boolean initalState) {
        super(board, row, column);

        this.state = initalState;

        if (initalState) {
            this.setBackground(ON_COLOR);
        }
    }


    /**
     * Toggle the state of this button.
     */
    public void update() {
        this.state = !this.state;
        if (this.state) {
            this.setBackground(ON_COLOR);
        } else {
            this.setBackground(OFF_COLOR);
        }
    }

    @Override
    public boolean isOn() {
        return state;
    }
}
