package dodgeEm;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
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

    private Image map;

    public Car(String name, int color){
        this.playerName = name;
        this.totalHp = 100;
        this.hp = 50;
        this.carColor = color;
        this.angle = 270;
        this.posX = 0;
        this.posY = 0;
    }

    public void init() throws SlickException{
        this.map = new Image("res/map.png").getScaledCopy(0.6f);
        this.sprite = new Image("res/red-car.png").getScaledCopy(new Float(0.13));
    }

    public void render(int centerX, int centerY, Graphics graphics) throws SlickException{
        this.map.draw(this.posX, this.posY);
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

        //Render health
        graphics.setColor(Color.red);
        graphics.fillRoundRect(centerX-(this.totalHp/2),centerY+70, this.totalHp, 15, 20);

        graphics.setColor(Color.green);
        graphics.fillRoundRect(centerX-(this.totalHp/2),centerY+70, this.hp, 15, 20);
    }

    public void trackMouse(float targetX, float targetY, float originX, float originY){
        float opposite = targetY - originY;
        float adjacent = targetX - originX;

        if(targetX != 0 && targetY !=600){ //Avoid changing the initial direction after the game has started
            this.angle = (float) Math.toDegrees(Math.atan2(opposite, adjacent));
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

}
