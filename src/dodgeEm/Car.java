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

    /** HEALTH **/
    private int hp;
    private int totalHp;

    /** POSITIONING AND DIRECTION **/
    protected float posX;
    protected float posY;
    protected float angle;

    /** SIZE **/
    private float width;
    private float height;

    /** VISUALIZATION **/
    private Image sprite;
    protected Shape bounds;


    public Car(String name, int color){ }

    public void init(float x, float y) throws SlickException{
        /** HEALTH **/
        this.hp = 50;
        this.totalHp = 100;

        /** CAR POSITIONING AND DIRECTION **/
        this.posX = x;
        this.posY = y;
        this.angle = 270;

        /** MAP POSITIONING **/
        Play.mapX = this.posX + Play.OFFSET_X;
        Play.mapY = this.posY + Play.OFFSET_Y;

        /** VISUALIZATION AND SIZING **/
        this.sprite = new Image("res/red-car.png").getScaledCopy(new Float(0.13));
        this.width = this.sprite.getWidth();
        this.height = this.sprite.getHeight();
        this.initBounds(x, y);

    }

    /** INITIALIZE BOUNDING POLYGON **/
    public void initBounds(float x, float y){
        float[] points = {
                Play.CENTER_X - (this.sprite.getWidth()/2), Play.CENTER_Y,
                Play.CENTER_X - 130, Play.CENTER_Y + 40,
                Play.CENTER_X - 110, Play.CENTER_Y + 65,
                Play.CENTER_X - 75, Play.CENTER_Y + (this.height/2),
                Play.CENTER_X, Play.CENTER_Y + (this.height/2),
                Play.CENTER_X + 75, Play.CENTER_Y + (this.height/2),
                Play.CENTER_X + 110, Play.CENTER_Y + 65,
                Play.CENTER_X + 130, Play.CENTER_Y + 40,
                Play.CENTER_X +(this.sprite.getWidth()/2), Play.CENTER_Y,
                Play.CENTER_X + 130, Play.CENTER_Y -40,
                Play.CENTER_X + 110, Play.CENTER_Y -65,
                Play.CENTER_X + 75, Play.CENTER_Y -(this.height/2),
                Play.CENTER_X, Play.CENTER_Y -(this.height/2),
                Play.CENTER_X - 75, Play.CENTER_Y -(this.height/2),
                Play.CENTER_X - 110, Play.CENTER_Y - 65,
                Play.CENTER_X - 130, Play.CENTER_Y - 40
        };

        this.bounds = new Polygon(points);
        this.bounds.setCenterX(x);
        this.bounds.setCenterY(y);
    }


    /** RENDERING TO MAP **/
    public void render(){
        sprite.setRotation(this.angle);
        sprite.drawCentered(Play.mapX+this.posX, Play.mapY+this.posY);
        this.renderBounds(Play.mapX+this.posX, Play.mapY+this.posY, Color.red);
    }

    public void renderFixed(){
        sprite.setRotation(this.angle);
        sprite.drawCentered(Play.OFFSET_X, Play.OFFSET_Y);
        this.renderBounds(Play.OFFSET_X, Play.OFFSET_Y, Color.red);
    }

    public void renderBounds(float centerX, float centerY, Color color){
        this.initBounds(centerX, centerY); //Re-initialize polygon to reset its angle
        this.bounds = this.bounds.transform(Transform.createRotateTransform((float) Math.toRadians(this.angle)));

        //Set center for bounding polygon
        this.bounds.setCenterX(centerX);
        this.bounds.setCenterY(centerY);

        //Render bounding polygon
        Graphics graphics = new Graphics();
        graphics.setColor(color);
        graphics.draw(this.bounds);
    }
}
