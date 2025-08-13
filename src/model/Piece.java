package model;

public class Piece {
    public Tetromino type;
    public int rot;
    public int row;
    public int col;


    public Piece(Tetromino type, int row, int col, int rot) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.rot = rot;
    }

    public int[][] shape() {
        return type.rotations[rot];
    }

    public void rotateCW() {
        rot = (rot + 1) % 4;
    }

    public void rotateCCW() {
        rot = (rot + 3) % 4;
    }

}
