package dodgeEm;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.state.*;

import java.util.Timer;
import java.util.TimerTask;


public class Car {

    /** HEALTH **/
    private int hp;
    private int maxHp;

    /** SPEED **/
    protected float speed;
    protected float maxSpeed;

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

    protected PowerUp item;

    public Car(String name, int color){ }

    public void init(float x, float y) throws SlickException{
        /** HEALTH **/
        this.hp = 50;
        this.maxHp = 100;

        /** BUMPER CAR SPEED **/
        this.speed = 5f;
        this.maxSpeed = this.speed;

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
        this.renderHealth(Play.OFFSET_X, Play.OFFSET_Y + (this.height/2));
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

    public void renderHealth(float centerX, float centerY){
        Graphics graphics = new Graphics();

        /** EXPAND MAX HP HEALTH BAR WIDTH **/
        float barWidth = this.maxHp * 1.5f;

        /** MAX HEALTH BAR **/
        graphics.setColor(Color.red);
        graphics.fillRoundRect(centerX-(barWidth/2), centerY, barWidth, 10,20);

        /** CURRENT HEALTH BAR **/
        graphics.setColor(Color.green);
        graphics.fillRoundRect(centerX-(barWidth/2), centerY, this.hp * 1.5f, 10,20);
    }


    public void move(float destX, float destY){
        this.posX = destX;
        this.posY = destY;
    }

    public void usePowerUp(PowerUp powerUp){

        /** OVERRIDE THE EFFECTIVITY OF PREVIOUS ITEM **/
        if(this.item != null){
            if(this.item instanceof Gum){
                Gum gum = (Gum) this.item;
                gum.timer.cancel();
                System.out.println("CANCELLEDT GUM");
            }
            else if(this.item instanceof Boost){
                Boost boost = (Boost) this.item;
                boost.timer.cancel();
                System.out.println("CANCELLEDT BOOST");
            }
        }

        /** STORE POWER UP AS AN ITEM OF CAR **/
        this.item = powerUp;

        /** INVOKES DIFFERENT METHODS FOR EACH ITEM **/
        if(powerUp instanceof Health){
            this.use((Health) this.item);
        }
        else if(powerUp instanceof Gum){
            this.use((Gum) this.item);
        }
        else if(powerUp instanceof Boost){
            this.use((Boost) this.item);
        }
    }

    public void use(Health health){
        if(this.hp + health.potency > this.maxHp){
            this.hp = this.maxHp;
        }
        else{
            this.hp += health.potency;
        }
    }


    public void use(Gum gum){
        gum.timer = new Timer();
        gum.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println(gum.duration);
                gum.duration -= 1;

                Car.this.speed = Car.this.maxSpeed * gum.speedBonus; //Slows down the car (speedBonus < 1)

                if (gum.duration < 0) {
                    gum.timer.cancel();
                    Car.this.speed = Car.this.maxSpeed;
                    Car.this.item = null;
                }
            }
        }, 0, 1000);
    }

    public void use(Boost boost){
        boost.timer = new Timer();
        boost.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println(boost.duration);
                boost.duration -= 1;

                Car.this.speed = Car.this.maxSpeed * boost.speedBonus; //Speeds up the car (speedBonus > 1)

                if (boost.duration < 0) {
                    boost.timer.cancel();
                    Car.this.speed = Car.this.maxSpeed;
                    Car.this.item = null;
                }
        }
        }, 0, 1000);
    }


}
