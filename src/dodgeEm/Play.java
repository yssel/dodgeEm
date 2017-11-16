package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.*;

public class Play extends BasicGameState {
    //Cursor location
    private int curX = 0;
    private int curY = 0;

    //Screen center
    private int centerX;
    private int centerY;

    private Car playerCar = null;

    private Image map = null;
    private Shape[] mapBounds;

    public Play(int state) {
        this.playerCar = new Car("Rain", 1);
    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        this.map = new Image("res/map.png").getScaledCopy(0.6f);
        this.centerX = gameContainer.getWidth()/2;
        this.centerY = gameContainer.getHeight()/2;
        this.curX = this.centerX;
        this.curY = this.centerY;
        this.playerCar.init();
        this.initMapBounds();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        this.map.draw(this.playerCar.getPositionX(), this.playerCar.getPositionY());
        graphics.drawString("x: " + this.curX + " y: " + this.curY, 100, 10);
        this.playerCar.render(this.centerX, this.centerY, graphics);
        this.drawBounds(graphics);
    }


    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        this.curX = Mouse.getX();
        this.curY = gameContainer.getHeight() - Mouse.getY();
        this.playerCar.trackMouse(this.curX, this.curY, this.centerX, this.centerY);

        if(curY < new Float(this.centerY)){
            float carY = this.playerCar.getPositionY();
            carY += 0.2 + delta * 0.1f;
            this.playerCar.setPositionY(carY);
        }
        else if(curY > new Float(this.centerY)){
            float carY = this.playerCar.getPositionY();
            carY -= 0.2 + delta * 0.1f;
            this.playerCar.setPositionY(carY);
        }

        if(curX < new Float(this.centerX)){
            float carX = this.playerCar.getPositionX();
            carX += 0.2 + delta * 0.1f;
            this.playerCar.setPositionX(carX);
        }
        else if(curX > new Float(this.centerX)){
            float carX = this.playerCar.getPositionX();
            carX -= 0.2 + delta * 0.1f;
            this.playerCar.setPositionX(carX);
        }
    }

    private void initMapBounds(){
        Shape top = new Rectangle(this.playerCar.getPositionX()+286, this.playerCar.getPositionY()+285, 4320, 20);
        Shape left = new Rectangle(this.playerCar.getPositionX()+286, this.playerCar.getPositionY()+305, 20,3180);
        Shape right = new Rectangle(this.playerCar.getPositionX()+286+4320, this.playerCar.getPositionY()+305, 20,3180);
        Shape bottom = new Rectangle(this.playerCar.getPositionX()+286, this.playerCar.getPositionY()+305+3180, 4320, 20);

        this.mapBounds = new Shape[]{top, left, right, bottom};
    }

    private void drawBounds(Graphics graphics){
        this.mapBounds[0].setLocation(this.playerCar.getPositionX()+286, this.playerCar.getPositionY()+285);
        this.mapBounds[1].setLocation(this.playerCar.getPositionX()+286, this.playerCar.getPositionY()+305);
        this.mapBounds[2].setLocation(this.playerCar.getPositionX()+286+4320, this.playerCar.getPositionY()+305);
        this.mapBounds[3].setLocation(this.playerCar.getPositionX()+286, this.playerCar.getPositionY()+305+3180);

        graphics.draw(this.mapBounds[0]);
        graphics.draw(this.mapBounds[1]);
        graphics.draw(this.mapBounds[2]);
        graphics.draw(this.mapBounds[3]);
    }


    private boolean checkCollision(int delta){
        float carX = this.playerCar.getPositionX();
        float carY = this.playerCar.getPositionY();

        for(int i=0; i<4; i++){
            if(this.mapBounds[i].intersects(this.playerCar.getBounds())){
//                this.mapBounds[i].
            }
        }
        return false;
    }


}
