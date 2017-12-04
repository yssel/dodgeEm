package dodgeEm;

import org.newdawn.slick.*;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;

public class InstructionDialog {
    private Image dialogBox;
    protected Boolean isVisible;
    private Image closeButton;

    public InstructionDialog(GameContainer gameContainer) throws SlickException{
        this.isVisible = false;
        this.dialogBox = new Image("res/instruction.png");
        this.closeButton = new Image("res/close.png");
    }

    public void render(GameContainer gameContainer, Graphics graphics){
        if(isVisible){
            dialogBox.drawCentered(Game.CENTER_X, Game.CENTER_Y);
            closeButton.drawCentered(Game.CENTER_X+220, Game.CENTER_Y-85);
        }
    }

    public void show(){
        this.isVisible = true;
    }

    public void hide(){
        this.isVisible = false;
    }


}
