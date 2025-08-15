package controller;

import model.GameState;
import util.AudioManager;

import javax.swing.*;
import java.awt.event.*;
public class TetrisController implements ActionListener, KeyListener {

    private final GameState state;
    private final JComponent target;
    private final Timer timer;
    private final AudioManager audio;

    public TetrisController(GameState state, JComponent repaintTarget, AudioManager audio ) {
        this.state = state;
        this.target = repaintTarget;
        this.audio = audio;

        this.timer = new Timer(state.getDropDelay(), this);
        this.timer.start();

        repaintTarget.addKeyListener(this);
        repaintTarget.setFocusable(true);
        repaintTarget.requestFocusInWindow();

        audio.startGameplayLoop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        state.stepGravity();
        timer.setDelay(state.getDropDelay());
        target.repaint();
        checkEndAudio();
    }

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

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            timer.setDelay(state.getDropDelay());
    }
    @Override
    public void keyTyped(KeyEvent e) {}

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
