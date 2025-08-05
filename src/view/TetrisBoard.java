package view;

import javax.swing.*;
import java.awt.*;

/*
@ creating the fields for the board
 */
public class TetrisBoard extends JPanel {
    private static final int ROWS = 20;
    private static final int COLUMNS = 10;
    private static final int TITLE_SIZE = 40;

    /*
    @coloring the board and creating the lines for the rows and column
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GRAY);
        for (int row =0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                g.drawRect(column*TITLE_SIZE, row*TITLE_SIZE, TITLE_SIZE, TITLE_SIZE);
            }
        }
    }

}
