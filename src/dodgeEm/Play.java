package dodgeEm;

import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
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
    private ConcurrentHashMap<Integer,Car> cars;
    private Car myCar = null;

    /** COLLECTIONS OF GAME OBJECTS **/
    private ConcurrentHashMap<Integer, PowerUp> powerUps;
    private HashMap<String, Shape> bounds;

    /** CHAT BOX COMPONENTS **/
    private Boolean chatBoxHidden = true;
    private TextField chatBox;
    private TrueTypeFont chatFont;
    protected static ConcurrentLinkedDeque<Message> chatMessages;

    /** UDP **/
    private DatagramSocket socket= new DatagramSocket();
    private String serverdata;
    private boolean connected=false;
    private boolean started=false;


    public Play(int state) throws SocketException {
        socket.setSoTimeout(10);
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
        /*car = new Car("Yssel", 1, 0);
        car.init(650, 500);*/
        cars = new ConcurrentHashMap<>();

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
        for(int i=0; i<playerNum-1; i++){
            cars.put(i, new Car("", 1, 0));
            cars.get(i).init(650, 500);
        }
        System.out.println("CARS:"+cars);

        /** INITIALIZE CHAT CLIENT **/
        chatFont = Game.loadFont("res/zig.ttf", 20f);
        chatBox = initChatBox(gameContainer, chatFont);
        chatMessages = new ConcurrentLinkedDeque<>();
   }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        if(connected && started){
            /** RENDERING OF MAP **/
            map.draw(mapX, mapY);

            /** RENDERING OF POWER UPS **/
            for(Integer i: powerUps.keySet()){
                powerUps.get(i).render();
            }
            /**RENDERING OF BUMPER CARS */
            if (serverdata.startsWith("PLAYER")){
                String[] playersInfo = serverdata.split(":");
                boolean encountered = false;
                for (int i=0;i<playersInfo.length;i++){
                    String[] playerInfo = playersInfo[i].split(" ");
                    String pname =playerInfo[1];
                    if(pname.equals(myCar.getName())){
                        System.out.println("ENTERED HERE");
                        myCar.renderFixed();
                        /** CURRENT LOCATION OF MY CAR **/
                        graphics.drawString("x: " + (myCar.posX) + " y: " + (myCar.posY), 100, 10);
                        encountered=true;
                    }else{
                        System.out.println("RECEIVED: "+ playersInfo[i]);
                        if(!encountered){
                            cars.get(i).update( Float.parseFloat(playerInfo[2]),Float.parseFloat(playerInfo[3]),Float.parseFloat(playerInfo[4]), Float.parseFloat(playerInfo[5]),Float.parseFloat(playerInfo[6]),Float.parseFloat(playerInfo[7]),Float.parseFloat(playerInfo[8]),Float.parseFloat(playerInfo[9]), Float.parseFloat(playerInfo[10]), Float.parseFloat(playerInfo[11]), playerInfo[1], Integer.parseInt(playerInfo[12]));
                            cars.get(i).render();
                        } else{
                            cars.get(i-1).update( Float.parseFloat(playerInfo[2]),Float.parseFloat(playerInfo[3]),Float.parseFloat(playerInfo[4]), Float.parseFloat(playerInfo[5]),Float.parseFloat(playerInfo[6]),Float.parseFloat(playerInfo[7]),Float.parseFloat(playerInfo[8]),Float.parseFloat(playerInfo[9]), Float.parseFloat(playerInfo[10]), Float.parseFloat(playerInfo[11]), playerInfo[1], Integer.parseInt(playerInfo[12]));
                            cars.get(i-1).render();
                        }

                    }
                }
            }

            /** RENDERING OF BOUNDS **/
            for(String key : bounds.keySet()){
                graphics.setColor(Color.green);
                graphics.draw(bounds.get(key));
            }

            /** RENDERING CHAT **/
            renderChat(gameContainer, graphics);


            graphics.drawString("carX: " + myCar.posX + "carY: " + myCar.posY, 200, 40);
        }
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try{
            socket.receive(packet);
        }catch(Exception ioe){}
        serverdata=new String(buf);
        serverdata=serverdata.trim();

        if (!connected && serverdata.startsWith("CONNECTED")) {
            connected = true;
            System.out.println("Connected.");
        }else if (!connected){
            System.out.println("Connecting..");
            send("CONNECT "+myCar.getName());
        }
        if(serverdata.startsWith("START")){
            started=true;
        }
        if(connected && started){
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

            send("PLAYER "+myCar.getDetails());
        }
    }

    /**
     * Helper method for sending data to server
     */
    public void send(String msg){
        try{
            System.out.println("TOSEND: "+ msg);
            byte[] buf = msg.getBytes();
            InetAddress address= InetAddress.getByName(Game.HOST);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Game.PORT);
            socket.send(packet);
        }catch(Exception e){}

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
            if(bounds.get("TOP").intersects(myCar.bounds)){
                myCar.posY += 100;
                mapY = -(myCar.posY - Play.OFFSET_Y);
            }
            for(int i=0; i<playerNum-1;i++){
                if(cars.get(i).collidedWith(myCar)){
                    myCar.posY += 100;
                    mapY = -(myCar.posY - Play.OFFSET_Y);
                }
            }
        }
        if(this.cursorY > Game.CENTER_Y){
            myCar.posY += (delta * 0.1f * myCar.speed);
            mapY = -(myCar.posY - Play.OFFSET_Y);
            if(bounds.get("BOTTOM").intersects(myCar.bounds)){
                myCar.posY -= 100;
                mapY = -(myCar.posY - Play.OFFSET_Y);
            }
            for(int i=0; i<playerNum-1;i++){
                if(cars.get(i).collidedWith(myCar)){
                    myCar.posY -= 100;
                    mapY = -(myCar.posY - Play.OFFSET_Y);
                }
            }
        }

        //Point cursor left and right
        if(this.cursorX < Game.CENTER_X){
            myCar.posX -= (delta * 0.1f * myCar.speed);
            mapX = -(myCar.posX - Play.OFFSET_X);
            if(bounds.get("LEFT").intersects(myCar.bounds)){
                myCar.posX += 100;
                mapX = -(myCar.posX - Play.OFFSET_X);
            }
            for(int i=0; i<playerNum-1;i++){
                if(cars.get(i).collidedWith(myCar)){
                    myCar.posX += 100;
                    mapX = -(myCar.posX - Play.OFFSET_X);
                }
            }
        }
        if(this.cursorX > Game.CENTER_X){
            myCar.posX += (delta * 0.1f * myCar.speed);
            mapX = -(myCar.posX - Play.OFFSET_X);
            if(bounds.get("RIGHT").intersects(myCar.bounds)){
                myCar.posX -= 100;
                mapX = -(myCar.posX - Play.OFFSET_X);
            }
            for(int i=0; i<playerNum-1;i++){
                if(cars.get(i).collidedWith(myCar)){
                    myCar.posX -= 100;
                    mapX = -(myCar.posX - Play.OFFSET_X);
                }
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
