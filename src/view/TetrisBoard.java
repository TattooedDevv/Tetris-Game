package view;

import javax.swing.*;
import java.awt.*;

/*
@ creating the fields for the board
 */
public class TetrisBoard extends JPanel {
    private static final int ROWS = 50;
    private static final int COLUMNS = 20;
    private static final int TITLE_SIZE = 40;
    private static final int INFO_HEIGHT = 40;

    public TetrisBoard() {
        setPreferredSize(new Dimension(COLUMNS * TITLE_SIZE, ROWS * TITLE_SIZE));
    }

    /*
    @coloring the board and creating the lines for the rows and column
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);



        // Draw user info, this is a placeholder
        g.setColor(Color.RED);
        g.drawString("Player: Jasmine", 10, 20);
        g.drawString("High Score: 5000", 150, 20);

        g.setColor(Color.GRAY);
        for (int row =0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                int x = column * TITLE_SIZE;
                int y = row * TITLE_SIZE + INFO_HEIGHT;
                g.drawRect(x, y, TITLE_SIZE, TITLE_SIZE);
            }
        }
    }

}
