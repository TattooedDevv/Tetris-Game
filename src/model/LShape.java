package model;

import java.awt.Color;

public class LShape extends BaseTetromino {
    public LShape() {
        super(new String[]{
                "..1",
                "111",
                "..."
        }, new Color(255, 165, 0));
    }
    @Override public int getIndex() { return 2; }
}

