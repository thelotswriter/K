package miniMain;

import frogger.FroggerAction;
import frogger.Main;

public class PlayFrogger implements MiniMain
{

    public static void main(String args[])
    {
        Main froggerGame = new Main();
        froggerGame.run();

        while (froggerGame.getGameState() != Main.GAME_OVER) {
            switch (froggerGame.getGameState()) {
                case Main.GAME_INTRO:
                case Main.GAME_INSTRUCTIONS:
                case Main.GAME_FINISH_LEVEL:
                    break;
                case Main.GAME_PLAY:
                    froggerGame.froggerVirtualInputHandler(makeRandomMove());
            }
        }

    }

    public static FroggerAction makeRandomMove() {
        return FroggerAction.UP;
    }

    public int getBoardHeight() {
        return Main.WORLD_HEIGHT;
    }

    public int getBoardWidth() {
        return Main.WORLD_WIDTH;
    }


    @Override
    public void running()
    {

    }

    @Override
    public void paused()
    {

    }
}
