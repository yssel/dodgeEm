package dodgeEm;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public abstract class PowerUp {
    protected float time;
    protected Image sprite;
    protected Shape bounds;
    protected float x, y;
    protected float width, height;

    public PowerUp(){ }

    public void render(){
        sprite.drawCentered(Play.mapX + this.x, Play.mapY + this.y);
        this.renderBounds(Play.mapX + this.x, Play.mapY + this.y);
    }

    public void renderBounds(float x, float y){
        this.bounds = new Rectangle(x, y, this.width, this.height);
        this.bounds.setCenterX(x);
        this.bounds.setCenterY(y);

        Graphics graphics = new Graphics();
        graphics.draw(this.bounds);

    }

}
