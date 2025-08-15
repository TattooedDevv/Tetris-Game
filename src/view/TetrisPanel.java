package view;

import model.GameState;
import model.Piece;

import javax.swing.*;
import java.awt.*;

/**
 * for rendering the entire Tetris game screen
 */
public class TetrisPanel extends JPanel {

    private static final int TILE_SIZE = 60;
    private static final int INFO_HEIGHT = 70;

    private final GameState state;
    private final String playerName;


    /**
     * Construct a panel with a given game state and player name and sets background color and preferred size
     * @param state
     * @param playerName
     */
    public TetrisPanel(GameState state, String playerName) {
        this.state = state;
        this.playerName = playerName;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(
                GameState.COLUMNS * TILE_SIZE,
                INFO_HEIGHT + GameState.ROWS * TILE_SIZE
        ));
    }


    /**
     * draws the entire board
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (g instanceof Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }


        g.setColor(Color.WHITE);
        g.drawString("Player: " + playerName, 10, 18);
        g.drawString("Score: " + state.getScore(), 160, 18);
        g.drawString("Lines: " + state.getLinesCleared(), 260, 18);
        g.drawString("Level: " + state.getLevel(), 350, 18);
        if (state.isPaused()) g.drawString("[PAUSED]", 430, 18);
        if (state.isGameOver())
            g.drawString(state.isWin() ? "[YOU WIN]" : "[GAME OVER] Press R", 430, 18);


        g.setColor(new Color(20, 20, 20));
        g.fillRect(0, INFO_HEIGHT, GameState.COLUMNS * TILE_SIZE, GameState.ROWS * TILE_SIZE);


        g.setColor(new Color(45, 45, 45));
        for (int r = 0; r <= GameState.ROWS; r++)
            g.drawLine(0, INFO_HEIGHT + r * TILE_SIZE,
                    GameState.COLUMNS * TILE_SIZE, INFO_HEIGHT + r * TILE_SIZE);
        for (int c = 0; c <= GameState.COLUMNS; c++)
            g.drawLine(c * TILE_SIZE, INFO_HEIGHT,
                    c * TILE_SIZE, INFO_HEIGHT + GameState.ROWS * TILE_SIZE);


        int[][] board = state.getBoard();
        var palette = state.getPalette();
        for (int r = 0; r < GameState.ROWS; r++) {
            for (int c = 0; c < GameState.COLUMNS; c++) {
                int idx = board[r][c];
                if (idx != -1) drawTile(g, c, r, palette[idx]);
            }
        }


        model.Piece cur = state.getCurrent();
        if (cur != null && !state.isGameOver()) {
            int[][] s = cur.shape();
            for (int r = 0; r < s.length; r++) {
                for (int c = 0; c < s[0].length; c++) {
                    if (s[r][c] == 0) continue;
                    int br = cur.row + r, bc = cur.col + c;
                    if (br >= 0) drawTile(g, bc, br, cur.type.getColor());
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
        g.drawString("Next:", GameState.COLUMNS * TILE_SIZE - 70, 18);

        Piece next = state.getNext();
        if (next == null) return; // guard

        int[][] s = next.shape();
        int baseX = (GameState.COLUMNS * TILE_SIZE) - 70;
        int baseY = 28;
        for (int r = 0; r < s.length; r++) {
            for (int c = 0; c < s[0].length; c++) {
                if (s[r][c] == 0) continue;
                int x = baseX + c * (TILE_SIZE / 2);
                int y = baseY + r * (TILE_SIZE / 2);
                g.setColor(next.type.getColor());
                g.fillRect(x, y, TILE_SIZE / 2 - 2, TILE_SIZE / 2 - 2);
            }
        }
    }
}


