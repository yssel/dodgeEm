package dodgeEm;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public abstract class PowerUp  {
    protected boolean spawn;
    protected boolean timerOn;
    protected boolean toRemove;
    protected boolean blink;
    protected Timer durationTimer;
    protected Timer spawnTimer;
    protected float mapDuration;

    protected float duration;
    protected Image sprite;
    protected Shape bounds;
    protected float x, y;
    protected float width, height;

    public PowerUp(float x, float y, String res) throws SlickException{
        /** POSITIONING **/
        this.x = x;
        this.y = y;

        /** VISUALIZATION AND SIZING **/
        this.sprite = new Image(res).getScaledCopy(new Float(0.08));
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        this.blink = false;
        this.spawn = false;
        this.toRemove = false;
        this.timerOn = false;

        /** BOUNDING POLYGON **/
        this.bounds = new Rectangle(x, y, this.width, this.height);

        this.mapDuration=0;
        this.spawnTimer= new Timer();
        this.durationTimer= new Timer();
    }

    public void spawn(int type){
        Random rand = new Random();
        int  n = 0;
        if(type==1)
            n= rand.nextInt(120000) + 60000;
        if(type==2|| type==3)
            n= rand.nextInt(120000) + 10000;

        this.spawnTimer.schedule(new TimerTask() {
            public void run() {
                PowerUp.this.spawn=true;
                PowerUp.this.timerOn=true;
                spawnTimer.cancel();
            }
        }, n);

    }

    public void render(int type){
        if(this.timerOn){
            this.startTimer(type);
        }
        if(!this.blink && this.spawn){
            sprite.drawCentered(Play.mapX + this.x, Play.mapY + this.y);
            this.renderBounds(Play.mapX + this.x, Play.mapY + this.y);
        }
    }

    public void startTimer(int type){
        int period = 0;
        if(type == 1) {
            period = 600;
            this.mapDuration=60;
        }
        if(type == 2 || type == 3){
            period = 300;
            this.mapDuration=30;
        }
        int finalPeriod = period;

        this.timerOn=false;
        this.durationTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                PowerUp.this.mapDuration -= 0.5;

               if(type==1 && PowerUp.this.mapDuration <= 3){
                   PowerUp.this.blinking();
               }
               if((type==2 || type==3) && PowerUp.this.mapDuration <= 3){
                   PowerUp.this.blinking();
               }
               if(PowerUp.this.mapDuration== 0){
                   PowerUp.this.toRemove=true;
                   durationTimer.cancel();
               }
            }
        }, 0, finalPeriod);
    }

    public void blinking(){
        this.blink=!this.blink;
    }

    public void renderBounds(float x, float y){
        this.bounds.setCenterX(x);
        this.bounds.setCenterY(y);

        Graphics graphics = new Graphics();
        graphics.draw(this.bounds);
    }

}
