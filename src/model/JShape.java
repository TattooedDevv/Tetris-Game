package model;

import java.awt.Color;

public class JShape extends BaseTetromino {
    public JShape() {
        super(new String[]{
                "1..",
                "111",
                "..."
        }, new Color(0, 0, 255));
    }
    @Override public int getIndex() { return 1; }
}
