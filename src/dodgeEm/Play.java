package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Play extends BasicGameState {
    //Cursor location
    private int curX = 0;
    private int curY = 0;

    //Screen center
    private int centerX;
    private int centerY;

    private Car playerCar = null;


    public Play(int state) {
        this.playerCar = new Car("Rain", 1);
    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        this.centerX = gameContainer.getWidth()/2;
        this.centerY = gameContainer.getHeight()/2;
    }


    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        graphics.drawString("x: " + this.curX + " y: " + this.curY, 100, 10);
        this.playerCar.render(this.centerX, this.centerY, graphics);



    }


    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        this.curX = Mouse.getX();
        this.curY = gameContainer.getHeight() - Mouse.getY();
        this.playerCar.trackMouse(this.curX, this.curY, this.centerX, this.centerY);
    }


}
