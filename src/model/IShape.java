package model;

import java.awt.Color;

public class IShape extends BaseTetromino {
    public IShape() {
        super(new String[]{
                "....",
                "1111",
                "....",
                "...."
        }, new Color(0, 240, 240));
    }
    @Override public int getIndex() { return 0; }
}

