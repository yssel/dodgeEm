package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.*;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Play extends BasicGameState implements GameConfig {

    /** OFFSET FOR MY CAR **/
    public static final float OFFSET_X = 400;
    public static final float OFFSET_Y = 300;

    /** MAP POSITIONING **/
    public static float mapX = 0;
    public static float mapY = 0;

    /** MAP IMAGE **/
    private Image map = null;

    /** MAP ARENA BOUNDS **/
    private final int ARENA_TOP = 304;
    private final int ARENA_LEFT = 307;
    private final int ARENA_BOTTOM = 3485;
    private final int ARENA_RIGHT = 4604;

    /** CURSOR LOCATION **/
    private int cursorX = 0;
    private int cursorY = 0;

    /** BUMPER CARS **/
    private Car car = null;
    private Car myCar = null;

    /** COLLECTIONS OF GAME OBJECTS **/
    private ConcurrentHashMap<Integer, PowerUp> powerUps;
    private HashMap<String, Shape> bounds;

    /** CHAT BOX COMPONENTS **/
    private Boolean chatBoxHidden = true;
    private TextField chatBox;
    private TrueTypeFont chatFont;
    protected static ConcurrentLinkedDeque<Message> chatMessages;


    public Play(int state) {
    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        /**INITIALIZE MAP **/
        map = new Image("res/map.png").getScaledCopy(0.6f);

        /** INITIALIZE POWER UP **/
        powerUps = new ConcurrentHashMap<>();

        /** INITIALIZE OTHER BUMPER CARS **/
        car = new Car("Yssel", 1, 0);
        car.init(650, 500);

        /** INITIALIZE MY CAR **/
        myCar = new Car(MainMenu.name, MainMenu.carColor, 180);
        myCar.init(1000, 1000);

        /** INITIALIZE MAP ARENA BOUNDS **/
        initArenaBounds();

        /** RANDOMIZE POWER UPS **/
        for(int i=0; i<30; i++){
            Random rand = new Random();
            switch(rand.nextInt(3)){
                case 0:
                    powerUps.put(i, new Health( ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT),
                                                ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
                    break;
                case 1:
                    powerUps.put(i, new Gum(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT),
                                            ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
                    break;
                case 2:
                    powerUps.put(i, new Boost(  ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT),
                                                ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
                    break;
            }
        }

        /** INITIALIZE CHAT CLIENT **/
        chatFont = Game.loadFont("res/zig.ttf", 20f);
        chatBox = initChatBox(gameContainer, chatFont);
        chatMessages = new ConcurrentLinkedDeque<>();
   }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        /** RENDERING OF MAP **/
        map.draw(mapX, mapY);

        /** RENDERING OF POWER UPS **/
        for(Integer i: powerUps.keySet()){
            powerUps.get(i).render();
        }

        /**RENDERING OF BUMPER CARS */
        car.render();
        myCar.renderFixed();

        /** CURRENT LOCATION OF MY CAR **/
        graphics.drawString("x: " + (myCar.posX) + " y: " + (myCar.posY), 100, 10);

        /** RENDERING OF BOUNDS **/
        for(String key : bounds.keySet()){
            graphics.setColor(Color.green);
            graphics.draw(bounds.get(key));
        }

        /** RENDERING CHAT **/
        renderChat(gameContainer, graphics);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        this.cursorX = Mouse.getX();
        this.cursorY = gameContainer.getHeight() - Mouse.getY();

        arenaRelativeToMap();

        /** PLAY USING ARROW (DEBUGGING) **/
        playArrow(gameContainer);

        /** PLAY USING MOUSE **/
        playCursor(delta);

        /** TOGGLE CHAT BOX **/
        toggleChatListener(gameContainer.getInput());

        for(Integer i: powerUps.keySet()){
            if(myCar.bounds.intersects(powerUps.get(i).bounds)){
                myCar.usePowerUp(powerUps.get(i));
                powerUps.remove(i);
            }
        }
    }

    public void trackCursor(float targetX, float targetY){
        float opposite = targetY - Game.CENTER_Y;
        float adjacent = targetX - Game.CENTER_X;

        /** ROTATION OF MY BUMPER CAR **/
        myCar.angle = (float) Math.toDegrees(Math.atan2(opposite, adjacent));
    }

    private void playCursor(int delta){
        trackCursor(this.cursorX, this.cursorY);

        //Point cursor up and down
        if(this.cursorY < Game.CENTER_Y){
            myCar.posY -= (delta * 0.1f * myCar.speed);
            mapY = -(myCar.posY - Play.OFFSET_Y);
            if(bounds.get("TOP").intersects(myCar.bounds) || car.collidedWith(myCar)){
                myCar.posY += 100;
                mapY = -(myCar.posY - Play.OFFSET_Y);
            }
        }
        if(this.cursorY > Game.CENTER_Y){
            myCar.posY += (delta * 0.1f * myCar.speed);
            mapY = -(myCar.posY - Play.OFFSET_Y);
            if(bounds.get("BOTTOM").intersects(myCar.bounds) || car.collidedWith(myCar)){
                myCar.posY -= 100;
                mapY = -(myCar.posY - Play.OFFSET_Y);
            }
        }

        //Point cursor left and right
        if(this.cursorX < Game.CENTER_X){
            myCar.posX -= (delta * 0.1f * myCar.speed);
            mapX = -(myCar.posX - Play.OFFSET_X);
            if(bounds.get("LEFT").intersects(myCar.bounds) || car.collidedWith(myCar)){
                myCar.posX += 100;
                mapX = -(myCar.posX - Play.OFFSET_X);
            }
        }
        if(this.cursorX > Game.CENTER_X){
            myCar.posX += (delta * 0.1f * myCar.speed);
            mapX = -(myCar.posX - Play.OFFSET_X);
            if(bounds.get("RIGHT").intersects(myCar.bounds) || car.collidedWith(myCar)){
                myCar.posX -= 100;
                mapX = -(myCar.posX - Play.OFFSET_X);
            }
        }
    }

    private void playArrow(GameContainer gameContainer){
        Input input = gameContainer.getInput();

        if(input.isKeyDown(Input.KEY_LEFT)){
            myCar.posX -=1;
            mapX = -(myCar.posX - Play.OFFSET_X);
        }
        if(input.isKeyDown(Input.KEY_RIGHT)){
            myCar.posX +=1;
            mapX = -(myCar.posX - Play.OFFSET_X);
        }
        if(input.isKeyDown(Input.KEY_UP)){
            myCar.posY -=1;
            mapY = -(myCar.posY - Play.OFFSET_Y);
        }
        if(input.isKeyDown(Input.KEY_DOWN)){
            myCar.posY +=1;
            mapY = -(myCar.posY - Play.OFFSET_Y);
            System.out.println("CAR x: " + myCar.posX + " y: " + myCar.posY);
            System.out.println("MAP x: " + mapX + " y: " + mapY);
            System.out.println();
        }
    }

    private void initArenaBounds(){
        bounds = new HashMap<>();
        bounds.put("LEFT", new Rectangle(mapX+(ARENA_LEFT-60), mapY+ARENA_TOP, 60, ARENA_BOTTOM-ARENA_TOP));
        bounds.put("TOP", new Rectangle(mapX+ARENA_LEFT, mapY+(ARENA_TOP-60), ARENA_RIGHT-ARENA_LEFT, 60));
        bounds.put("RIGHT", new Rectangle(mapX+ARENA_RIGHT, mapY+ARENA_TOP, 60, ARENA_BOTTOM-ARENA_TOP));
        bounds.put("BOTTOM", new Rectangle(mapX+ARENA_LEFT, mapY+ARENA_BOTTOM, ARENA_RIGHT-ARENA_LEFT, 60));
    }

    private void arenaRelativeToMap(){
        bounds.replace("LEFT", new Rectangle(mapX+(ARENA_LEFT-60), mapY+ARENA_TOP, 60, ARENA_BOTTOM-ARENA_TOP));
        bounds.replace("TOP", new Rectangle(mapX+ARENA_LEFT, mapY+(ARENA_TOP-60), ARENA_RIGHT-ARENA_LEFT, 60));
        bounds.replace("RIGHT", new Rectangle(mapX+ARENA_RIGHT, mapY+ARENA_TOP, 60, ARENA_BOTTOM-ARENA_TOP));
        bounds.replace("BOTTOM", new Rectangle(mapX+ARENA_LEFT, mapY+ARENA_BOTTOM, ARENA_RIGHT-ARENA_LEFT, 60));
    }

    /** CHATBOX-RELATED METHODS **/
    private TextField initChatBox(GameContainer gameContainer, TrueTypeFont font){
        TextField field = new TextField(gameContainer, font,
                font.getWidth("(ALL): ")+30, 550,
                (font.getWidth("123")*10)+10, 25);
        field.setBackgroundColor(Color.black);
        field.setBorderColor(Color.transparent);
        field.setTextColor(Color.white);
        field.setMaxLength(30);
        return field;
    }

    private void renderChat(GameContainer gameContainer, Graphics graphics){
        graphics.setFont(this.chatFont);

        /** DISPLAY ALL MESSAGES FROM PLAYERS **/
        renderMessages(graphics);

        /** DISPLAY CHAT BOX **/
        if(!chatBoxHidden){
            //Set "(ALL):" label color
            graphics.setColor(Color.white);
            graphics.drawString("(ALL): ", 30, 550);

            chatBox.render(gameContainer, graphics);
            chatBox.setFocus(true);
        }
    }

    private void renderMessages(Graphics graphics){
        int y = 440; //Y-position of topmost message
        for(Message message : chatMessages){
            message.render(30, y, graphics);
            y+=20;
        }
    }

    private void toggleChatListener(Input input){
        if(input.isKeyPressed(Input.KEY_ENTER)){
            if(!chatBoxHidden && chatBox.getText().length() > 0){
                MainMenu.chat.send(chatBox.getText());
            }
            chatBox.setText(""); //Clear chat box text
            chatBoxHidden = !chatBoxHidden; //Toggle chat box visibility
        }
    }

    protected static void emitMessage(String message){

        /** LAST FIVE MESSAGES **/
        if(Play.chatMessages.size() == 5){
            Play.chatMessages.removeFirst();
        }

        /** START TIMER AFTER ADDING MESSAGE TO QUEUE **/
        Play.chatMessages.addLast(new Message(message));
        Play.chatMessages.getLast().startTimer();
    }
}
