package dodgeEm;

import org.newdawn.slick.SlickException;

public class Health extends PowerUp{
    protected int potency;


    public Health(float x, float y) throws SlickException{
        super(x, y, "res/health.png");

        /** ADDITIONAL ATTRIBUTES **/
        this.potency = 25;
    }



}
