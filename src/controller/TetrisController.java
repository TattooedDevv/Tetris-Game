package controller;

import model.GameState;
import util.AudioManager;

import javax.swing.*;
import java.awt.event.*;

/**
 * @ Overall handles keyboard input, game timing, repainting and audio
 */
public class TetrisController implements ActionListener, KeyListener {

    private final GameState state;
    private final JComponent target;
    private final Timer timer;
    private final AudioManager audio;

    public TetrisController(GameState state, JComponent repaintTarget, AudioManager audio ) {
        this.state = state;
        this.target = repaintTarget;
        this.audio = audio;

        /**
         * Starts gravity at the models dop delay
         */
        this.timer = new Timer(state.getDropDelay(), this);
        this.timer.start();

        repaintTarget.addKeyListener(this);
        repaintTarget.setFocusable(true);
        repaintTarget.requestFocusInWindow();

        audio.startGameplayLoop();
    }

    /**
     *
     * @param e the event to be processed.
     * advance gravity, resync delay, repaint, and check end audio.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        state.stepGravity();
        timer.setDelay(state.getDropDelay());
        target.repaint();
        checkEndAudio();
    }

    /**
     *
     * @param e the event to be processed. Maps the key action example left, right, pause restart etc
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT  -> state.moveLeft();
            case KeyEvent.VK_RIGHT -> state.moveRight();
            case KeyEvent.VK_DOWN  -> {
                timer.setDelay(GameState.DROP_MS_FAST);
                state.softDropOnce();
            }
            case KeyEvent.VK_UP    -> state.rotateCW();
            case KeyEvent.VK_SPACE -> state.hardDrop();
            case KeyEvent.VK_P     -> {
                state.togglePause();
                if (state.isPaused()) { timer.stop(); audio.pauseGameplay(); }
                else { timer.start(); audio.resumeGameplay(); }
            }
            case KeyEvent.VK_R     -> {
                state.reset();
                timer.setDelay(state.getDropDelay());
                if (!state.isPaused()) timer.start();
                audio.startGameplayLoop();
            }
        }
        target.repaint();
        checkEndAudio();
    }

    /**
     * Handles when DOWN is release it'll do back to normal speed
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            timer.setDelay(state.getDropDelay());
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * If the game is over the timer will stop and play the win or lose audio
     */
    private void checkEndAudio() {
        if (state.isGameOver()) {
            timer.stop();
            if (state.isWin())
                audio.playWin();
            else
                audio.playLose();
        }
    }

}
