package dodgeEm;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.Font;
import java.io.InputStream;

public class Game extends StateBasedGame {

    public static final String GAME_NAME = "Dodge 'Em";
    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int LOBBY = 2;

    /** GAME WINDOW SIZE **/
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    public static final float CENTER_X = 400;
    public static final float CENTER_Y = 300;

    public Game(String title){
        super(title);
        this.addState(new MainMenu(MENU));
        this.addState(new Play(PLAY));
        this.addState(new Lobby(LOBBY));
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        this.getState(MENU).init(gameContainer, this);
        this.enterState(MENU);
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

    public static TrueTypeFont loadFont(String res, float size){
        try {
            InputStream inputStream	= ResourceLoader.getResourceAsStream(res);
            Font font = java.awt.Font.createFont(Font.TRUETYPE_FONT, inputStream);
            font = font.deriveFont(size); // set font size
            return new TrueTypeFont(font, false);

        } catch (Exception e) {}
        return null;
    }
}
