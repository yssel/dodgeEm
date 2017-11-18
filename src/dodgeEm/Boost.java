package dodgeEm;

import org.newdawn.slick.SlickException;

import java.util.Timer;

public class Boost extends PowerUp{
    protected float speedBonus;
    protected Timer timer;

    public Boost(float x, float y) throws SlickException{
        super(x, y, "res/boost.png");
        this.duration = 5;
        this.speedBonus = 2f;
    }
}
