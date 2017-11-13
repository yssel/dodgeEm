package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Game extends StateBasedGame {

    public static final String GAME_NAME = "Dodge 'Em";
    public static final int MENU = 0;
    public static final int PLAY = 1;

    public Game(String title){
        super(title);
        this.addState(new MainMenu(MENU));
        this.addState(new Play(PLAY));
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
            app.setDisplayMode(800, 600, false);
            app.start();


        }catch (SlickException e){
            e.printStackTrace();
        }

    }
}
