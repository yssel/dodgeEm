package dodgeEm;

import org.newdawn.slick.*;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;

public class ConfigDialog {
    private TextField hostField;
    private TextField portField;
    private Image dialogBox;
    protected Boolean isVisible;
    private Image closeButton;

    public ConfigDialog(GameContainer gameContainer) throws SlickException{
        this.isVisible = false;
        this.dialogBox = new Image("res/menu-dialog.png");
        this.closeButton = new Image("res/close.png");
        this.initHostTextField(gameContainer);
        this.initPortTextField(gameContainer);
        hostField.setFocus(true);
    }

    public void render(GameContainer gameContainer, Graphics graphics){
        if(isVisible){
            dialogBox.drawCentered(Game.CENTER_X, Game.CENTER_Y);
            closeButton.drawCentered(Game.CENTER_X+220, Game.CENTER_Y-85);
            graphics.setColor(Color.black);
            graphics.fillRect(Math.round(Game.CENTER_X)-95, Math.round(Game.CENTER_Y)-60, 290, 53);
            graphics.setColor(Color.white);
            hostField.render(gameContainer, graphics);
            hostField.setCursorVisible(true);

            graphics.setColor(Color.black);
            graphics.fillRect(Math.round(Game.CENTER_X)-95, Math.round(Game.CENTER_Y)+5, 290, 53);
            graphics.setColor(Color.white);
            portField.render(gameContainer, graphics);
        }
    }

    public void show(){
        this.isVisible = true;
    }

    public void hide(){
        this.isVisible = false;
        Game.HOST = hostField.getText();
        Game.PORT = Integer.parseInt(portField.getText());
    }

    private void initHostTextField(GameContainer gameContainer){
        this.hostField = new TextField(gameContainer, Game.deriveFont(35f),
                Math.round(Game.CENTER_X)-88, Math.round(Game.CENTER_Y)-50,
                Game.deriveFont(35f).getWidth("192.168.255.255"), 38);
        this.hostField.setText(Game.HOST);
        this.hostField.setCursorPos(Game.HOST.length());
        this.hostField.setBackgroundColor(Color.transparent);
        this.hostField.setTextColor(Color.white);
        this.hostField.setBorderColor(Color.transparent);
    }

    private void initPortTextField(GameContainer gameContainer){
        this.portField = new TextField(gameContainer, Game.deriveFont(35f),
                Math.round(Game.CENTER_X) - 88, Math.round(Game.CENTER_Y) + 15,
                Game.deriveFont(35f).getWidth("192.168.255.255"), 38);

        this.portField.setText(Integer.toString(Game.PORT));
        this.portField.setCursorPos(Integer.toString(Game.PORT).length());
        this.portField.setBackgroundColor(Color.transparent);
        this.portField.setTextColor(Color.white);
        this.portField.setBorderColor(Color.transparent);
    }


}
