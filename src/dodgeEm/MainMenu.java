package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class MainMenu extends BasicGameState {
    private String mouse = "No input yet!";
    private int xPos = 0;
    private int yPos = 0;
    private SpriteSheet spriteSheet = null;
    private Image colorpicker = null;
    Image car = null;
    int carColor = 0;

    public MainMenu(int state){

    }


    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        spriteSheet = new SpriteSheet("res/car1-sprites.png", 190, 190, 10);
        colorpicker = new Image("res/color-picker.png").getScaledCopy(250, 42);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        graphics.drawString(this.mouse, gameContainer.getWidth()/2, 20);

        //Render title
        graphics.drawString(Game.GAME_NAME, (gameContainer.getWidth()/2) - new Float(42), 100);

        //Render bumper car
        car = spriteSheet.getSprite(6,carColor);
        car.draw((gameContainer.getWidth()/2)-(car.getWidth()/2), 100);
        System.out.println("x: " + this.xPos + " y: " + this.yPos);


        colorpicker.draw((gameContainer.getWidth()/2)-(colorpicker.getWidth()/2), 350);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        int xPos = Mouse.getX();
        int yPos = Mouse.getY();

        if(yPos>207 && yPos<253){
            if(xPos > 270 && xPos < 316){
                changeCarColor(0);
            }
            else if(xPos > 326 && xPos < 368){
                changeCarColor(1);
            }
            else if(xPos > 376 && xPos < 426){
                changeCarColor(2);
            }
            else if(xPos > 430 && xPos < 475){
                changeCarColor(3);
            }
            else if(xPos > 480 && xPos < 520){
                changeCarColor(4);
            }
        }
//        this.xPos = Mouse.getX();
//        this.yPos = Mouse.getY();

//        mouse = "Mouse position x: " + xpos  + " y: " + ypos;
    }

    public void changeCarColor(int color){
        if(Mouse.isButtonDown(0)){
            this.carColor = color;
        }
    }
}
