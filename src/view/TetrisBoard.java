package view;

import model.Piece;
import model.Tetromino;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.Random;

/*
@ creating the fields for the board
 */
public class TetrisBoard extends JPanel implements ActionListener, KeyListener {

    /**
     * @Board configurations
     */
    private static final int ROWS = 50;
    private static final int COLUMNS = 20;
    private static final int TITLE_SIZE = 40;
    private static final int INFO_HEIGHT = 40;

    /**
     * @ Gavitiy config
     */
    private static final int DROP_MS_START = 550;
    private static final int DROP_MS_END = 50;

    private final int[][] board = new int[ROWS][COLUMNS];
    private final Color[] palette = new Color[Tetromino.values().length];
    private Piece cur;
    private Piece next;
    private final Random rng = new Random();
    private Timer timer;
    private int dropDelay = DROP_MS_START;
    private boolean paused = false;
    private boolean gameOver = false;

    private int score = 0;
    private int linesCleared = 0;
    private int level = 1;
    private String playerName = "Jasmine";


    public TetrisBoard() {
        setPreferredSize(new Dimension(COLUMNS * TITLE_SIZE, ROWS * TITLE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);


        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLUMNS; c++)
                board[r][c] = -1;

        Tetromino[] types = Tetromino.values();
        for (int i = 0; i < types.length; i++) palette[i] = types[i].color;

        cur = randomPiece;
        next = randomPiece();

        timer = new Timer(dropDelay, this);
        timer.start();

    }

    private Piece randomPiece() {
        Tetromino[] types = Tetromino.values();
        Tetromino t = types[rng.nextInt(types.length)];
        int rot = 0;
        int spawnCol = (COLUMNS - t.rotations[0][0].length) / 2;
        Piece p = new Piece(t, rot, 0, spawnCol);
        return p;
    }

    private boolean collides(Piece p, int newRow, int newCol, int newRot) {
        int[][] shape = p.type.rotations[newRot];
        int h = shape.length, w = shape[0].length;

        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                int br = newRow + r;
                int bc = newCol + c;

                if (br < 0 || bc >= COLUMNS || br >= ROWS)
                    return true;
                if (br < 0)
                    continue;
                if (board[br][bc] != -1)
                    return true;
            }
        }
        return false;
    }

    private void lockPiece() {
        int idx = cur.type.ordinal();
        int[][] s = cur.shapes();
        for (int r = 0; r < s.length; r++) {
            for (int c = 0; c < s[0].length; c++) {
                if (s[r][c] == 0)
                    continue;
                int br = cur.row + r;
                int bc = cur.col + c;
                if (br >= 0 && br < ROWS && bc >= 0 && bc < COLUMNS) {
                    board[br][bc] = idx;
                } else {
                    gameOver = true;
                }
            }
        }
        int cleared = clearLines();
        updateScore(cleared);
        spawnNext();
    }

    private void spawnNext() {
        cur = next;
        next = randomPiece();
        if (collides(cur, cur.row, cur.col, cur.rot)) {
            gameOver = true;
            timer.stop();
        }
    }

    private int clearLines() {
        int cleared = 0;
        for (int r = ROWS - 1; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < COLUMNS; c++) {
                if (board[r][c] == -1) { full = false; break; }
            }
            if (full) {
                cleared++;
                for (int rr = r; rr > 0; rr--) {
                    System.arraycopy(board[rr - 1], 0, board[rr], 0, COLUMNS);
                }
                for (int c = 0; c < COLUMNS; c++) board[0][c] = -1;
                r++;
            }
        }
        linesCleared += cleared;
        return cleared;
    }

    private void updateScore(int cleared) {
        int add = switch(cleared) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 500;
            case 4 -> 800;
            default -> 0;
        };
        score += add;
        int newLevel = 1 + linesCleared / 10;
        if(newLevel != level) {
            level = newLevel;
            dropDelay = Math.max(90,DROP_MS_START - (level - 1) * 50);
            timer.setDelay(dropDelay);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if ( paused || gameOver) return;
        tryMove(cur.row + 1, cur.col, cur.rot, true);
    }

    private void tryMove(int newRow, int newCol, int newRot, boolean lockIfBlocked) {
        if(!collides(cur, newRow, newCol, newRot)) {
            cur.row = newRow;
            cur.col = newCol;
            cur.rot = newRot;
        } else if (lockIfBlocked && newRow == cur.row + 1 && newCol == cur.col) {
            lockPiece();
        }
        repaint();
    }

    private void hardDrop() {
        while (!collides(cur, cur.row + 1, cur.col, cur.rot)) {
            cur.row++;
        }
        lockPiece();
        repaint();
    }

    private void resetGame() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLUMNS; c++)
                board[r][c] = -1;

            score = 0;
            linesCleared = 0;
            level = 1;
            dropDelay = DROP_MS_START;
            timer.setDelay(dropDelay);
            paused = false;
            gameOver = false;
            cur = randomPiece();
            next = randomPiece();
            timer.start();
            repaint();
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
