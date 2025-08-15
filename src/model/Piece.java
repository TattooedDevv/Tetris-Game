package model;



/**
 * Represents a single active tetromino instance on the board
 */
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

    /**
     * Get the 2D block grid for the piece in its current rotation
     * @return
     */
    public int[][] shape() {

        return type.rotations[rot];
    }

    /**
     * rotates peice 90 degree clockwise
     */
    public void rotateCW()  {
        rot = (rot + 1) % 4;
    }

    /**
     * rotates piece 90 degree counter clockwise
     */
    public void rotateCCW() {
        rot = (rot + 3) % 4;
    }
}

