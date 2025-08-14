package view;
import model.Piece;
import model.Tetromino;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TetrisPanel extends JPanel implements ActionListener, KeyListener {

    // --- Board config ---
    private static final int ROWS = 20;
    private static final int COLUMNS = 10;
    private static final int TILE_SIZE = 32;
    private static final int INFO_HEIGHT = 40;

    // --- Timing ---
    private static final int DROP_MS_START = 550;
    private static final int DROP_MS_FAST = 50;

    // --- State ---
    private final int[][] board = new int[ROWS][COLUMNS]; // -1 empty, else tetromino index
    private final Color[] palette = new Color[Tetromino.values().length];
    private final Random rng = new Random();

    private Piece cur;
    private Piece next;
    private Timer timer;
    private int dropDelay = DROP_MS_START;
    private boolean paused = false;
    private boolean gameOver = false;

    // --- Score ---
    private int score = 0;
    private int linesCleared = 0;
    private int level = 1;
    private String playerName = "Jasmine";

    public TetrisPanel() {
        setPreferredSize(new Dimension(COLUMNS * TILE_SIZE, INFO_HEIGHT + ROWS * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // init board
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLUMNS; c++)
                board[r][c] = -1;

        // palette
        Tetromino[] types = Tetromino.values();
        for (int i = 0; i < types.length; i++) palette[i] = types[i].color;

        cur = randomPiece();
        next = randomPiece();

        timer = new Timer(dropDelay, this);
        timer.start();
    }

    private Piece randomPiece() {
        Tetromino[] types = Tetromino.values();
        Tetromino t = types[rng.nextInt(types.length)];
        int rot = 0;
        int spawnCol = (COLUMNS - t.rotations[0][0].length) / 2;
        return new Piece(t, rot, 0, spawnCol);
    }

    private boolean collides(Piece p, int newRow, int newCol, int newRot) {
        int[][] shape = p.type.rotations[newRot];
        int h = shape.length, w = shape[0].length;

        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (shape[r][c] == 0) continue;
                int br = newRow + r;
                int bc = newCol + c;

                if (bc < 0 || bc >= COLUMNS || br >= ROWS) return true; // walls/floor
                if (br < 0) continue;                                  // above top is ok
                if (board[br][bc] != -1) return true;                  // block collision
            }
        }
        return false;
    }

    private void lockPiece() {
        int idx = cur.type.ordinal();
        int[][] s = cur.shape();
        for (int r = 0; r < s.length; r++) {
            for (int c = 0; c < s[0].length; c++) {
                if (s[r][c] == 0) continue;
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
                r++; // re-check same row
            }
        }
        linesCleared += cleared;
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
            dropDelay = Math.max(90, DROP_MS_START - (level - 1) * 50);
            timer.setDelay(dropDelay);
        }
    }

    // --- Game tick (gravity) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        if (paused || gameOver) return;
        tryMove(cur.row + 1, cur.col, cur.rot, true);
    }

    private void tryMove(int newRow, int newCol, int newRot, boolean lockIfBlocked) {
        if (!collides(cur, newRow, newCol, newRot)) {
            cur.row = newRow;
            cur.col = newCol;
            cur.rot = newRot;
        } else if (lockIfBlocked && newRow == cur.row + 1 && newCol == cur.col) {
            lockPiece();
        }
        repaint();
    }

    private void hardDrop() {
        while (!collides(cur, cur.row + 1, cur.col, cur.rot)) cur.row++;
        lockPiece();
        repaint();
    }

    private void resetGame() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLUMNS; c++)
                board[r][c] = -1;

        score = 0; linesCleared = 0; level = 1;
        dropDelay = DROP_MS_START;
        timer.setDelay(dropDelay);
        paused = false; gameOver = false;
        cur = randomPiece(); next = randomPiece();
        timer.start();
        repaint();
    }

    // --- Input ---
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) { if (e.getKeyCode() == KeyEvent.VK_R) resetGame(); return; }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> tryMove(cur.row, cur.col - 1, cur.rot, false);
            case KeyEvent.VK_RIGHT -> tryMove(cur.row, cur.col + 1, cur.rot, false);
            case KeyEvent.VK_DOWN -> { timer.setDelay(DROP_MS_FAST); tryMove(cur.row + 1, cur.col, cur.rot, true); }
            case KeyEvent.VK_UP -> {
                int nextRot = (cur.rot + 1) % 4;
                if (!collides(cur, cur.row, cur.col, nextRot)) {
                    cur.rot = nextRot;
                } else if (!collides(cur, cur.row, cur.col - 1, nextRot)) {
                    cur.col -= 1; cur.rot = nextRot;
                } else if (!collides(cur, cur.row, cur.col + 1, nextRot)) {
                    cur.col += 1; cur.rot = nextRot;
                }
                repaint();
            }
            case KeyEvent.VK_SPACE -> hardDrop();
            case KeyEvent.VK_P -> { paused = !paused; if (paused) timer.stop(); else timer.start(); repaint(); }
            case KeyEvent.VK_R -> resetGame();
        }
    }
    @Override public void keyReleased(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_DOWN) timer.setDelay(dropDelay); }
    @Override public void keyTyped(KeyEvent e) {}

    // --- Rendering ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (g instanceof Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        g.setColor(Color.WHITE);
        g.drawString("Player: " + playerName, 10, 18);
        g.drawString("Score: " + score, 160, 18);
        g.drawString("Lines: " + linesCleared, 260, 18);
        g.drawString("Level: " + level, 350, 18);
        if (paused) g.drawString("[PAUSED]", 430, 18);
        if (gameOver) g.drawString("[GAME OVER] Press R", 430, 18);

        g.setColor(new Color(20, 20, 20));
        g.fillRect(0, INFO_HEIGHT, COLUMNS * TILE_SIZE, ROWS * TILE_SIZE);

        g.setColor(new Color(45, 45, 45));
        for (int r = 0; r <= ROWS; r++)
            g.drawLine(0, INFO_HEIGHT + r * TILE_SIZE, COLUMNS * TILE_SIZE, INFO_HEIGHT + r * TILE_SIZE);
        for (int c = 0; c <= COLUMNS; c++)
            g.drawLine(c * TILE_SIZE, INFO_HEIGHT, c * TILE_SIZE, INFO_HEIGHT + ROWS * TILE_SIZE);

        // locked tiles
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                int idx = board[r][c];
                if (idx != -1) drawTile(g, c, r, palette[idx]);
            }
        }

        // current piece
        if (!gameOver) {
            int[][] s = cur.shape();
            for (int r = 0; r < s.length; r++) {
                for (int c = 0; c < s[0].length; c++) {
                    if (s[r][c] == 0) continue;
                    int br = cur.row + r;
                    int bc = cur.col + c;
                    if (br >= 0) drawTile(g, bc, br, cur.type.color);
                }
            }
        }

        drawNextPreview(g);
    }

    private void drawTile(Graphics g, int col, int row, Color color) {
        int x = col * TILE_SIZE;
        int y = INFO_HEIGHT + row * TILE_SIZE;

        g.setColor(color);
        g.fillRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2);

        g.setColor(color.brighter());
        g.drawLine(x + 1, y + 1, x + TILE_SIZE - 2, y + 1);
        g.drawLine(x + 1, y + 1, x + 1, y + TILE_SIZE - 2);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + TILE_SIZE - 2, x + TILE_SIZE - 2, y + TILE_SIZE - 2);
        g.drawLine(x + TILE_SIZE - 2, y + 1, x + TILE_SIZE - 2, y + TILE_SIZE - 2);
    }

    private void drawNextPreview(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Next:", COLUMNS * TILE_SIZE - 70, 18);

        int[][] s = next.shape();
        int baseX = (COLUMNS * TILE_SIZE) - 70;
        int baseY = 28;
        for (int r = 0; r < s.length; r++) {
            for (int c = 0; c < s[0].length; c++) {
                if (s[r][c] == 0) continue;
                int x = baseX + c * (TILE_SIZE / 2);
                int y = baseY + r * (TILE_SIZE / 2);
                g.setColor(next.type.color);
                g.fillRect(x, y, TILE_SIZE / 2 - 2, TILE_SIZE / 2 - 2);
            }
        }
    }
}




