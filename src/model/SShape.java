package model;

import java.awt.Color;

public class SShape extends BaseTetromino {
    public SShape() {
        super(new String[]{
                ".11",
                "11.",
                "..."
        }, new Color(0, 255, 0));
    }
    @Override public int getIndex() { return 4; }
}

