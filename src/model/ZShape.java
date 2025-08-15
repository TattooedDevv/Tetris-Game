package model;

import java.awt.Color;

public class ZShape extends BaseTetromino {
    public ZShape() {
        super(new String[]{
                "11.",
                ".11",
                "..."
        }, new Color(255, 0, 0));
    }
    @Override public int getIndex() { return 6; }
}

