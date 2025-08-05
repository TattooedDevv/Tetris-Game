package view;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tetris");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400,800);
            frame.setResizable(false);

            TetrisBoard board = new  TetrisBoard();
            frame.add(board);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}
