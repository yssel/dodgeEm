package dodgeEm;

import org.newdawn.slick.SlickException;

public class Gum extends PowerUp {
    protected float speedBonus;

    public Gum(float x, float y) throws SlickException{
        super(x, y, "res/gum.png");
        this.duration = 5;
        this.speedBonus = 0.1f;
    }
}
