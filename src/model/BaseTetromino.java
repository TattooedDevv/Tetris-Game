package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


public abstract class BaseTetromino {
    protected final int[][][] rotations;
    protected final Color color;


    public abstract int getIndex();


    protected BaseTetromino(String[] seed, Color color) {
        this.rotations = buildRotations(parse(seed));
        this.color = color;
    }

    public int[][][] getRotations() { return rotations; }
    public Color getColor() {
        return color;
    }



    private static int[][] parse(String[] rows) {
        int h = rows.length, w = 0;
        for (String r : rows) w = Math.max(w, r.length());
        int[][] m = new int[h][w];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < rows[r].length(); c++) {
                m[r][c] = rows[r].charAt(c) == '1' ? 1 : 0;
            }
        }
        return trim(m);
    }

    private static int[][][] buildRotations(int[][] base) {
        int[][][] out = new int[4][][];
        int[][] cur = base;
        for (int i = 0; i < 4; i++) {
            out[i] = trim(cur);
            cur = rotCW(cur);
        }
        return out;
    }

    private static int[][] rotCW(int[][] m) {
        int h = m.length, w = m[0].length;
        int[][] r = new int[w][h];
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                r[j][h - 1 - i] = m[i][j];
        return r;
    }

    private static int[][] trim(int[][] m) {
        int h = m.length, w = m[0].length;
        List<Integer> rs = new ArrayList<>(), cs = new ArrayList<>();
        for (int r = 0; r < h; r++) {
            boolean any = false;
            for (int c = 0; c < w; c++) if (m[r][c] != 0) { any = true; break; }
            if (any) rs.add(r);
        }
        for (int c = 0; c < w; c++) {
            boolean any = false;
            for (int r = 0; r < h; r++) if (m[r][c] != 0) { any = true; break; }
            if (any) cs.add(c);
        }
        if (rs.isEmpty() || cs.isEmpty()) return new int[][]{{0}};
        int[][] out = new int[rs.size()][cs.size()];
        for (int i = 0; i < rs.size(); i++)
            for (int j = 0; j < cs.size(); j++)
                out[i][j] = m[rs.get(i)][cs.get(j)];
        return out;
    }
}

