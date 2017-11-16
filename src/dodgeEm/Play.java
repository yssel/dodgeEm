package dodgeEm;

import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.*;

public class Play extends BasicGameState {

    /** GAME WINDOW SIZE **/
    public static final float CENTER_X = 400;
    public static final float CENTER_Y = 300;

    /** OFFSET FOR MY CAR **/
    public static final float OFFSET_X = 400;
    public static final float OFFSET_Y = 300;

    /** MAP POSITIONING **/
    public static float mapX = 0;
    public static float mapY = 0;

    /** MAP IMAGE **/
    private Image map = null;

    /** CURSOR LOCATION **/
    private int cursorX = 0;
    private int cursorY = 0;

    /** BUMPER CARS **/
    private Car car = null;
    private Car myCar = null;

    public Play(int state) {
        this.car = new Car("Rain", 1);
        this.myCar = new Car("My Car", 1);
    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        /**INITIALIZE MAP **/
        this.map = new Image("res/map.png").getScaledCopy(0.6f);

        /** INITIALIZE OTHER BUMPER CARS **/
        car.init(650, 500);

        /** INITIALIZE MY CAR **/
        myCar.init(0, 0);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        /** RENDERING OF MAP **/
        map.draw(mapX, mapY);

        /**RENDERING OF BUMPER CARS */
        car.render();
        myCar.renderFixed();

        /** CURRENT LOCATION OF MY CAR **/
        graphics.drawString("x: " + (myCar.posX) + " y: " + (myCar.posY), 100, 10);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        this.cursorX = Mouse.getX();
        this.cursorY = gameContainer.getHeight() - Mouse.getY();

        /** PLAY USING ARROW (DEBUGGING) **/
        playArrow(gameContainer);

        /** PLAY USING MOUSE **/
        playCursor(delta);
    }


    public void trackCursor(float targetX, float targetY){
        float opposite = targetY - Play.CENTER_Y;
        float adjacent = targetX - Play.CENTER_X;

        /** ROTATION OF SPRITE AND BOUNDING POLYGON **/
        myCar.angle = (float) Math.toDegrees(Math.atan2(opposite, adjacent));
        myCar.initBounds(Play.OFFSET_X, Play.OFFSET_Y); //Re-initialize polygon to reset its angle
        myCar.bounds = myCar.bounds.transform(Transform.createRotateTransform((float) Math.toRadians(myCar.angle)));
    }

    private void playCursor(int delta){
        trackCursor(this.cursorX, this.cursorY);

        //Point cursor up and down
        if(this.cursorY < CENTER_Y){
            myCar.posY -= (delta * 0.1f + 1);
            mapY = -(myCar.posY - Play.OFFSET_Y);
        }
        if(this.cursorY > CENTER_Y){
            myCar.posY += (delta * 0.1f + 1);
            mapY = -(myCar.posY - Play.OFFSET_Y);
        }

        //Point cursor left and right
        if(this.cursorX < CENTER_X){
            myCar.posX -= (delta * 0.1f + 1);
            mapX = -(myCar.posX - Play.OFFSET_X);
        }
        if(this.cursorX > CENTER_X){
            myCar.posX += (delta * 0.1f + 1);
            mapX = -(myCar.posX - Play.OFFSET_X);
        }
    }

    private void playArrow(GameContainer gameContainer){
        Input input = gameContainer.getInput();

        if(input.isKeyDown(Input.KEY_LEFT)){
            myCar.posX -=1;
            mapX = -(myCar.posX - Play.OFFSET_X);

            System.out.println("CAR x: " + myCar.posX + " y: " + myCar.posY);
            System.out.println("MAP x: " + mapX + " y: " + mapY);

            System.out.println();
        }
        if(input.isKeyDown(Input.KEY_RIGHT)){
            myCar.posX +=1;
            mapX = -(myCar.posX - Play.OFFSET_X);

            System.out.println("CAR x: " + myCar.posX + " y: " + myCar.posY);
            System.out.println("MAP x: " + mapX + " y: " + mapY);

            System.out.println();
        }

        if(input.isKeyDown(Input.KEY_UP)){
            myCar.posY -=1;
            mapY = -(myCar.posY - Play.OFFSET_Y);

            System.out.println("CAR x: " + myCar.posX + " y: " + myCar.posY);
            System.out.println("MAP x: " + mapX + " y: " + mapY);

            System.out.println();
        }

        if(input.isKeyDown(Input.KEY_DOWN)){
            myCar.posY +=1;
            mapY = -(myCar.posY - Play.OFFSET_Y);

            System.out.println("CAR x: " + myCar.posX + " y: " + myCar.posY);
            System.out.println("MAP x: " + mapX + " y: " + mapY);

            System.out.println();
        }
    }
}
