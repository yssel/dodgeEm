package dodgeEm;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Health extends PowerUp{
    protected int potency;


    public Health(float x, float y) throws SlickException{

        /** POSITIONING **/
        this.x = x;
        this.y = y;

        /** VISUALIZATION AND SIZING **/
        this.sprite = new Image("res/health.png").getScaledCopy(new Float(0.08));
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();

        /** ADDITIONAL ATTRIBUTES **/
        this.potency = 25;

    }



}
