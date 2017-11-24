package dodgeEm;

import org.newdawn.slick.SlickException;

import java.util.Timer;

public class Gum extends PowerUp {
    protected float speedBonus;
    protected Timer timer = new Timer();

    public Gum(float x, float y) throws SlickException{
        super(x, y, "res/gum.png");
        this.duration = 2;
        this.speedBonus = 0.1f;
    }
}
