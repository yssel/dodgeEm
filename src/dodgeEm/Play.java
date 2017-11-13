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
        this.curX = this.centerX;
        this.curY = this.centerY;
        this.playerCar.init();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        graphics.drawString("x: " + this.curX + " y: " + this.curY, 100, 10);
        this.playerCar.render(this.centerX, this.centerY, graphics);
    }


    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        this.curX = Mouse.getX();
        this.curY = gameContainer.getHeight() - Mouse.getY();
        this.playerCar.trackMouse(this.curX, this.curY, this.centerX, this.centerY);

        if(curY < new Float(this.centerY)){
            float carY = this.playerCar.getPositionY();
            carY += delta * 0.1f;
            this.playerCar.setPositionY(carY);

            if(this.playerCar.getPositionY() > -144){
                carY -= 5 + delta * 10f;
                this.playerCar.setPositionY(carY);
            }
        }
        else if(curY > new Float(this.centerY)){
            float carY = this.playerCar.getPositionY();
            carY -= delta * 0.1f;
            this.playerCar.setPositionY(carY);

            if(this.playerCar.getPositionY() < -3046){
                carY += 5 + delta * 10f;
                this.playerCar.setPositionY(carY);
            }
        }

        if(curX < new Float(this.centerX)){
            float carX = this.playerCar.getPositionX();
            carX += delta * 0.1f;
            this.playerCar.setPositionX(carX);
            if(this.playerCar.getPositionX() > -47){
                carX -= 5 + delta * 10f;
                this.playerCar.setPositionX(carX);
            }
        }
        else if(curX > new Float(this.centerX)){
            float carX = this.playerCar.getPositionX();
            carX -= delta * 0.1f;
            this.playerCar.setPositionX(carX);
            if(this.playerCar.getPositionX() < -4068){
                carX += 5 + delta * 10f;
                this.playerCar.setPositionX(carX);
            }
        }
    }


}
