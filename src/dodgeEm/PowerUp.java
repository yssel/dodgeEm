package dodgeEm;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public abstract class PowerUp  {
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

        /** BOUNDING POLYGON **/
        this.bounds = new Rectangle(x, y, this.width, this.height);
    }

    public void render(){
        sprite.drawCentered(Play.mapX + this.x, Play.mapY + this.y);
        this.renderBounds(Play.mapX + this.x, Play.mapY + this.y);
    }

    public void renderBounds(float x, float y){
        this.bounds.setCenterX(x);
        this.bounds.setCenterY(y);

        Graphics graphics = new Graphics();
        graphics.draw(this.bounds);
    }

}
