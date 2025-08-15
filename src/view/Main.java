package view;
import controller.TetrisController;
import model.GameState;
import util.AudioManager;

import javax.swing.*;


/**
 * Entry point of the Tetris game.
 * Creates the game state model, panel view, and controller
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameState state = new GameState();
            TetrisPanel panel = new TetrisPanel(state, "Jasmine");

            JFrame frame = new JFrame("Tetris");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            AudioManager audio = new AudioManager();
            new TetrisController(state, panel, audio);
            panel.requestFocusInWindow();
        });
    }
}




