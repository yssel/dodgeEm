package dodgeEm;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.*;


public class Car {
    private String playerName;
    private int hp;
    private int totalHp;

    private int carColor;
    private float angle;

    private Image sprite;
    private float posX;
    private float posY;
    private float width;
    private float length;

    private Shape bounds;


    public Car(String name, int color){
        this.playerName = name;
        this.totalHp = 100;
        this.hp = 50;
        this.carColor = color;
//        this.angle = 270;
        this.posX = 0;
        this.posY = 0;
    }

    public void init() throws SlickException{
        this.sprite = new Image("res/red-car.png").getScaledCopy(new Float(0.13));
        this.width = this.sprite.getWidth();
        this.length = this.sprite.getHeight();
        this.initBounds();
    }

    public void render(int centerX, int centerY, Graphics graphics) throws SlickException{
        graphics.drawString("x: " + this.posX + " y: " + this.posY, 400, 10);
        graphics.drawString("sX: " + centerX + " sY: " + centerY, 400, 30);


        //Render Car
        sprite.setRotation(this.angle);
        sprite.drawCentered(centerX, centerY);


        graphics.setColor(Color.blue);
        graphics.drawRect(centerX, centerY, 100, 100);

        //Render Player Name
        graphics.setColor(Color.white);
        graphics.drawString(this.playerName, centerX-20, centerY-100);

        this.drawBounds(Color.red, graphics);
        //Render health
        graphics.setColor(Color.red);
        graphics.fillRoundRect(centerX-(this.totalHp/2),centerY+70, this.totalHp, 15, 20);

        graphics.setColor(Color.green);
        graphics.fillRoundRect(centerX-(this.totalHp/2),centerY+70, this.hp, 15, 20);
    }

    public void initBounds(){
        float centerX = Game.SCREEN_WIDTH/2;
        float centerY = Game.SCREEN_HEIGHT/2;

        float[] points = {
                centerX-(this.sprite.getWidth()/2), centerY,
                centerX-130, centerY+40,
                centerX-110, centerY+65,
                centerX-75, centerY+(this.sprite.getHeight()/2),
                centerX, centerY+(this.sprite.getHeight()/2),
                centerX+75, centerY+(this.sprite.getHeight()/2),
                centerX+110, centerY+65,
                centerX+130, centerY+40,
                centerX+(this.sprite.getWidth()/2), centerY,
                centerX+130, centerY-40,
                centerX+110, centerY-65,
                centerX+75, centerY-(this.sprite.getHeight()/2),
                centerX, centerY-(this.sprite.getHeight()/2),
                centerX-75, centerY-(this.sprite.getHeight()/2),
                centerX-110, centerY-65,
                centerX-130, centerY-40};

        this.bounds = new Polygon(points);
    }


    public void trackMouse(float targetX, float targetY, float originX, float originY){
        float opposite = targetY - originY;
        float adjacent = targetX - originX;

        if(targetX != 0 && targetY !=600){ //Avoid changing the initial direction after the game has started
            this.angle = (float) Math.toDegrees(Math.atan2(opposite, adjacent));
            float radian = (float) Math.toRadians(this.angle);

            this.initBounds();
            this.bounds  = this.bounds.transform(Transform.createRotateTransform(radian));
        }
    }

    public int getCarHeight(){
       return this.sprite.getHeight();
    }

    public int getCarWidth(){
        return this.sprite.getHeight();
    }

    public float getPositionX(){
        return this.posX;
    }

    public float getPositionY(){
        return this.posY;
    }
    public void setPositionX(float newPosX){
        this.posX = newPosX;
    }

    public void setPositionY(float newPosY){
        this.posY = newPosY;
    }

    private void drawBounds(Color color, Graphics graphics){
        this.bounds.setCenterX(Game.SCREEN_WIDTH/2);
        this.bounds.setCenterY(Game.SCREEN_HEIGHT/2);

        graphics.setColor(color);
        graphics.draw(this.bounds);
    }

    public Shape getBounds(){
        return this.bounds;
    }
}
