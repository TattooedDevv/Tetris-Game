package model;

import java.awt.Color;

public class OShape extends BaseTetromino {
    public OShape() {
        super(new String[]{
                "11",
                "11"
        }, new Color(255, 255, 0));
    }
    @Override public int getIndex() { return 3; }
}

