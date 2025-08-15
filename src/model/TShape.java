package model;

import java.awt.Color;

public class TShape extends BaseTetromino {
    public TShape() {
        super(new String[]{
                ".1.",
                "111",
                "..."
        }, new Color(160, 0, 240));
    }
    @Override public int getIndex() { return 5; }
}
