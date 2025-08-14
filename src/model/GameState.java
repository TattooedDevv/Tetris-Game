package model;

import java.awt.Color;
import java.util.Random;

public class GameState {
    public static final int ROWS = 20;
    public static final int COLUMNS = 10;

    public static final int DROP_MS_START = 550;
    public static final int DOP_MS_FAST = 50;

    public static final int WIN_LINES = 40;

    private final int[][] board = new int[ROWS][COLUMNS];
    private final Color[] palette = new Color[Tetromino.values().length];

    private final Random rng = new Random();
    private Piece cur;
    private Piece next;

    private boolean paused = false;
    private boolean gameOver = false;
    private boolean win = false;

    private int score = 0;
    private int linesCleared = 0;
    private int level = 1;
    private int dropDelay = DROP_MS_START;

    public GameState() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLUMNS; c++)
                board[r][c] = -1;
        Tetromino[] types = Tetromino.values();
        for (int i = 0; i < types.length; i++) palette[i] = types[i].color;
        reset();
    }


    public void reset() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLUMNS; c++)
                board[r][c] = -1;
        score = 0;
        linesCleared = 0;
        level = 1;
        dropDelay = DROP_MS_START;
        paused = false;
        gameOver = false;
        win = false;
        cur = randomPiece();
        next = randomPiece();
        if (collides(cur, cur.row, cur.col, cur.rot)) {
            gameOver = true;
        }
    }

    public boolean stepGravity() {
        if (paused || gameOver)
            return false;
        return tryMove(cur.row + 1, cur.col, cur.rot, true);
    }

    public void hardDrop() {
        if (paused || gameOver) return;
        while (!collides(cur, cur.row + 1, cur.col, cur.rot)) cur.row++;
        lockPiece();
    }

        public void moveLeft() {
        if (!paused && !gameOver)
            tryMove(cur.row, cur.col - 1, cur.rot, false);
    }
        public void moveRight() {
        if (!paused && !gameOver)
            tryMove(cur.row, cur.col + 1, cur.rot, false);
    }
        public void softDropOnce() {
        if (!paused && !gameOver)
            tryMove(cur.row + 1, cur.col, cur.rot, true);
    }

    public void rotateCW() {
        if (paused || gameOver) return;
        int nr = (cur.rot + 1) % 4;
        if (!collides(cur, cur.row, cur.col, nr))
            cur.rot = nr;
        else if (!collides(cur, cur.row, cur.col - 1, nr)){
            cur.col--; cur.rot = nr;
        }
        else if (!collides(cur, cur.row, cur.col + 1, nr)){
            cur.col++; cur.rot = nr;
        }
    }

    public void togglePause() {
        if (!gameOver)
            paused = !paused;
    }

    public int[][] getBoard() {
        return board;
    }

    public Color[] getPalette() {
        return palette;
    }

    public Piece getNext() {
        return next;
    }

    public Piece getCurrent() {
        return cur;
    }

    public boolean isPaused() {
        return paused;
    }
    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWin() {
        return win;
    }

    public int getScore() {
        return score;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public int getLevel() {
        return level;
    }
    public int getDropDelay() {
        return dropDelay;
    }

    private Piece randomPiece() {
        Tetromino t = Tetromino.values()[rng.nextInt(Tetromino.values().length)];
        int rot = 0;
        int spawnCol = (COLUMNS - t.rotations[0][0].length) / 2;
        return new Piece(t,rot,0,spawnCol);
    }

    private boolean tryMove(int nr, int nc, int rot, boolean lockIfBlocked) {
        if (!collides(cur, nr, nc, rot)) {
            cur.row = nr;
            cur.col = nc;
            cur.rot = rot;
            return  true;
        }     else if (!lockIfBlocked && nr == cr.row + 1 && nc == cur.col) {
            lockPiece();
        }
        return false;
    }

    private void lockPiece() {
        int idx = cur.type.ordinal();
        int[][] s = cur.shape();
        for (int r = 0; r < s.length; r++) {
            for (int c = 0; c < s[0].length; c++) {
                if (s[r][c] == 0) continue;
                int br = cur.row + r, bc = cur.col + c;
                if (br >= 0 && br < ROWS && bc >= 0 && bc < COLUMNS) {
                    board[br][bc] = idx;
                } else {
                    gameOver = true; // locked out of bounds
                }
            }
        }
        int cleared = clearLines();
        updateScore(cleared);
        if (!gameOver) spawnNext();
    }

    private void spawnNext() {
        cur = next;
        next = randomPiece();
        if (collides(cur, cur.row, cur.col,cur.rot))
            gameOver = true;
    }

    private int clearLines() {
        int cleared = 0;
        for (int r = ROWS -1 ; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < COLUMNS; c++)
                if (board[r][c] == -1) {
                    full = false;
                }
            if (full) {
                cleared++;
                for (int rr = r; rr > 0; rr--) System.arraycopy(board[rr - 1], 0, board[rr], 0, COLUMNS);
                for (int c = 0; c < COLUMNS; c++) board[0][c] = -1;
                r++;
            }
        }
        linesCleared += cleared;
        if (linesCleared >= WIN_LINES) { win = true; gameOver = true; }
        return cleared;
    }

    private void updateScore(int cleared) {
        int add = switch (cleared) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 500;
            case 4 -> 800;
            default -> 0;
        };
        score += add;
        int newLevel = 1 + linesCleared / 10;
        if (newLevel != level) {
            level = newLevel;
            dropDelay = Math.max(90,DROP_MS_START - (level - 1) * 50);
        }
    }
}
