package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.*;
import static dodgeEm.Game.loadFont;

public class MainMenu extends BasicGameState {

    /** CURSOR LOCATION **/
    private int xPos;
    private int yPos;

    /** MAIN MENU GRAPHICS **/
    private Image car;
    private Image map;
    private Image arrow;
    private SpriteSheet spriteSheet;


    /** PLAYER INFO **/
    public static int carColor;
    public static String name;

    /** NAME FIELD AND CUSTOM FONT **/
    private TextField nameField;
    private TrueTypeFont nameFieldFont;

    public MainMenu(int state){ }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        this.spriteSheet = new SpriteSheet("res/bumper-car1.png", 2129, 1250, 0);
        this.map = new Image("res/map.png").getScaledCopy(0.6f);
        this.arrow = new Image("res/arrow.png").getScaledCopy(0.15f);

        /** PLAYER NAME AND CAR COLOR **/
        this.carColor = 0;
        this.name = "";
        this.nameFieldFont = loadFont("res/zig.ttf", 35f);
        nameField = initNameField(gameContainer, nameFieldFont);

    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {

        /** MAIN MENU BACKGROUND **/
        map.draw(0, 0);

        /** CHANGE COLOR BUTTONS **/
        arrow.setRotation(0);
        arrow.drawCentered(Game.CENTER_X - 180, Game.CENTER_Y);
        arrow.setRotation(180);
        arrow.drawCentered(Game.CENTER_X + 180, Game.CENTER_Y);

        /** BUMPER CAR **/
        car = spriteSheet.getSprite(this.carColor, 0).getScaledCopy(new Float(0.13f));
        car.drawCentered(Game.CENTER_X, Game.CENTER_Y);

        graphics.drawString("x: " + this.xPos + " y: " + this.yPos, Game.CENTER_X, 20);
        graphics.drawString("Start game", Game.CENTER_X, 500);

        /** DISPLAY NAME TEXT FIELD **/
        nameField.render(gameContainer, graphics);
        nameField.setFocus(true);
        nameField.setLocation(Math.round(Game.CENTER_X - (this.nameFieldFont.getWidth(this.nameField.getText())/2)),
                Math.round(Game.CENTER_Y+100));
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        Input input = gameContainer.getInput();

        /** HIDE TEXT CURSOR WHEN LIMIT IS REACHED **/
        Boolean isVisible = nameField.getText().length() != 5;
        nameField.setCursorVisible(isVisible);

        /** CHANGE COLOR LEFT BUTTON **/
        if(this.xPos > 200 && this.xPos <237 && this.yPos > 270 && this.yPos < 333){
            if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
                this.carColor = (this.carColor) - 1 < 0 ? 4 : (this.carColor - 1);
            }
        }

        /** CHANGE COLOR RIGHT BUTTON **/
        if(this.xPos > 560 && this.xPos <597 && this.yPos > 270 && this.yPos < 333){
            if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
                this.carColor = (this.carColor) + 1 > 4 ? 0 : (this.carColor + 1);
            }
        }

        /** START GAME BUTTON **/
        if(this.xPos > 397 && this.xPos < 493 && this.yPos > 80 && this.yPos <100){
            if(Mouse.isButtonDown(0)){
                this.name = this.nameField.getText();
                stateBasedGame.getState(Game.PLAY).init(gameContainer, stateBasedGame);
                stateBasedGame.enterState(Game.PLAY);
            }
        }

        /** GET CURSOR LOCATION **/
        this.xPos = Mouse.getX();
        this.yPos = Mouse.getY();
    }



    public TextField initNameField(GameContainer gameContainer, TrueTypeFont font){
        TextField field = new TextField(gameContainer, font,
                Math.round(Game.CENTER_X - (font.getWidth("HELLO")/2)),
                Math.round(Game.CENTER_Y+100), 200, 100);

        field.setBackgroundColor(Color.transparent);
        field.setBorderColor(Color.transparent);
        field.setTextColor(Color.white);
        field.setMaxLength(5);
        return field;
    }



}
