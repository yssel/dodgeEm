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

    public Car(String name, int color){
        this.playerName = name;
        this.totalHp = 100;
        this.hp = 50;
        this.carColor = color;
        this.angle = 0;
    }

    public void render(int posX, int posY, Graphics graphics) throws SlickException{
        //Render Player Name
        graphics.setColor(Color.white);
        graphics.drawString(this.playerName, posX-15, posY-100);

        //Render Car
        this.sprite = new Image("res/red-car.png").getScaledCopy(new Float(0.08));
        sprite.setRotation(this.angle);
        sprite.drawCentered(posX, posY);

        //Render health
        graphics.setColor(Color.red);
        graphics.fillRoundRect(posX-(this.totalHp/2),posY+70, this.totalHp, 15, 20);

        graphics.setColor(Color.green);
        graphics.fillRoundRect(posX-(this.totalHp/2),posY+70, this.hp, 15, 20);
    }

    public void trackMouse(float targetX, float targetY, float originX, float originY){
        float opposite = targetY - originY;
        float adjacent = targetX - originX;
        this.angle = (float) Math.toDegrees(Math.atan2(opposite, adjacent));
    }





}
