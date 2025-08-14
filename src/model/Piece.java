package model;

public class Piece {
    public Tetromino type;
    public int rot;   // 0..3
    public int row;   // top-left row
    public int col;   // top-left col

    public Piece(Tetromino type, int rot, int row, int col) {
        this.type = type;
        this.rot = rot;
        this.row = row;
        this.col = col;
    }

    public int[][] shape() {
        return type.rotations[rot];
    }

    public void rotateCW()  { rot = (rot + 1) % 4; }
    public void rotateCCW() { rot = (rot + 3) % 4; }
}

