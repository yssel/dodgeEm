package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Game extends StateBasedGame {

    public static final String GAME_NAME = "Dodge 'Em";
    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;

    public Game(String title){
        super(title);
        this.addState(new MainMenu(MENU));
        this.addState(new Play(PLAY, "192.18.1.1"));
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        this.getState(MENU).init(gameContainer, this);
        this.getState(PLAY).init(gameContainer, this);
        this.enterState(PLAY);

    }

    public static void main(String[] args){
        AppGameContainer app;
        try{
            app = new AppGameContainer(new Game(GAME_NAME));
            app.setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, false);
            app.start();

        }catch (SlickException e){
            e.printStackTrace();
        }

    }
}
