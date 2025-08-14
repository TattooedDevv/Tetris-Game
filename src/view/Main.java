package view;
import javax.swing.*;


/*
@ Main class to make sure the board appears
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tetris");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            TetrisPanel board = new TetrisPanel();
            frame.setContentPane(board);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            board.requestFocusInWindow();

        });
    }

}
