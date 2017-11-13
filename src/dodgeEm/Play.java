package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Play extends BasicGameState {
    private Image car = null;
    private int xPos = 0;
    private int yPos = 0;
    private float distX = 0;
    private float distY = 0;
    private int centerX;
    private int centerY;
    private double angle = 90;


    public Play(int state) {

    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        car = new Image("res/red-car.png").getScaledCopy(new Float(0.08));
        this.centerX = gameContainer.getWidth()/2;
        this.centerY = gameContainer.getHeight()/2;
    }


    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        float x1 = new Float(xPos);
        float y1 = new Float(yPos);
        float x2 = x1 + 100;
        float y2 = y1 + 100;


        graphics.drawString("x: " + this.xPos + " y: " + this.yPos, 100, 10);

        graphics.setColor(Color.white);
        graphics.drawString("Rain", this.centerX, this.centerY-100);

        car.drawCentered(this.centerX, this.centerY);

        int healthWidth = 200;


        graphics.setColor(Color.green);
        graphics.fillRoundRect(this.centerX-(healthWidth/2), this.centerY+70, healthWidth, 15, 10);

    }


    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        this.xPos = Mouse.getX();
        this.yPos = gameContainer.getHeight() - Mouse.getY();

//        if(Mouse.isButtonDown(0)){
            float opposite = this.yPos - this.centerY;
            float adjacent = this.xPos - this.centerX;
            angle = Math.toDegrees(Math.atan2(opposite, adjacent));

            System.out.println(angle);

            car.setRotation((float) angle);

//        }
//        distX = posX - (gameContainer.getWidth()/2);
//        distY = posY - (gameContainer.getHeight()/2);

    }
}
