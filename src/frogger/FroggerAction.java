package frogger;

public enum FroggerAction
{

    UP (38),
    DOWN (40),
    RIGHT (39),
    LEFT (37),
    STAY (0);

    private final int keyCode;

    FroggerAction(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return this.keyCode;
    }
}
