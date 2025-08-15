package model;

public class Piece {
    public BaseTetromino type;
    public int rot;
    public int row;
    public int col;

    public Piece(BaseTetromino type, int rot, int row, int col) {
        this.type = type;
        this.rot = rot;
        this.row = row;
        this.col = col;
    }

    public int[][] shape() {

        return type.rotations[rot];
    }

    public void rotateCW()  {
        rot = (rot + 1) % 4;
    }
    public void rotateCCW() {
        rot = (rot + 3) % 4;
    }
}

