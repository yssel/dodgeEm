package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tests.xml.Inventory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Play extends BasicGameState {

    /** OFFSET FOR MY CAR **/
    public static final float OFFSET_X = 400;
    public static final float OFFSET_Y = 300;

    /** MAP POSITIONING **/
    public static float mapX = 0;
    public static float mapY = 0;

    /** MAP IMAGE **/
    private Image map = null;

    /** CURSOR LOCATION **/
    private int cursorX = 0;
    private int cursorY = 0;

    /** BUMPER CARS **/
    private Car car = null;
    private Car myCar = null;

    private ConcurrentHashMap<Integer, PowerUp> powerUps;
    private String server;
    private DatagramSocket socket;
    
    public Play(int state, String server) {
        car = new Car("Rain", 1);
        myCar = new Car("My Car", 1);
        powerUps = new ConcurrentHashMap<>();
        this.server = server;

        try{
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(100);
        }catch(Exception e){}


    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException  {
        /**INITIALIZE MAP **/
        map = new Image("res/map.png").getScaledCopy(0.6f);

        /** INITIALIZE POWER UP **/
        powerUps = new ConcurrentHashMap<>();

        /** INITIALIZE OTHER BUMPER CARS **/
        car = new Car("Yssel", 1, 0);
        car.init(650, 500);

        /** INITIALIZE MY CAR **/
        myCar = new Car(MainMenu.name, MainMenu.carColor, 270);
        myCar.init(0, 0);

        /** RANDOMIZE POWER UPS **/
        for(int i=0; i<20; i++){
            Random rand = new Random();
            switch(rand.nextInt(3)){
                case 0:
                    powerUps.put(i, new Health(5 + rand.nextFloat() * (3000 - 5), 5 + rand.nextFloat() * (3000 - 5)));
                    break;
                case 1:
                    powerUps.put(i, new Gum(5 + rand.nextFloat() * (3000 - 5), 5 + rand.nextFloat() * (3000 - 5)));
                    break;
                case 2:
                    powerUps.put(i, new Boost(5 + rand.nextFloat() * (3000 - 5), 5 + rand.nextFloat() * (3000 - 5)));
                    break;
            }

        }
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


    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        this.cursorX = Mouse.getX();
        this.cursorY = gameContainer.getHeight() - Mouse.getY();

        /** PLAY USING ARROW (DEBUGGING) **/
        playArrow(gameContainer);

        /** PLAY USING MOUSE **/
        playCursor(delta);

        for(Integer i: powerUps.keySet()){
            if(myCar.bounds.intersects(powerUps.get(i).bounds)){
                myCar.usePowerUp(powerUps.get(i));
                powerUps.remove(i);
            }
        }
        this.send(myCar);
    }

<<<<<<< HEAD
    public void send(Car car){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try{
            out = new ObjectOutputStream(bos);
            out.writeObject(car);
            out.flush();
            byte[] buffer = bos.toByteArray();

            InetAddress serverAddress = InetAddress.getByName(server);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, GameServer.PORT);
            System.out.println("BUFFERRRR " + buffer.length);


        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                bos.close();
            }catch (IOException ex){}
        }
    }


=======
    }
>>>>>>> slick2d

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
        }
        if(this.cursorY > Game.CENTER_Y){
            myCar.posY += (delta * 0.1f * myCar.speed);
            mapY = -(myCar.posY - Play.OFFSET_Y);
        }

        //Point cursor left and right
        if(this.cursorX < Game.CENTER_X){
            myCar.posX -= (delta * 0.1f * myCar.speed);
            mapX = -(myCar.posX - Play.OFFSET_X);
        }
        if(this.cursorX > Game.CENTER_X){
            myCar.posX += (delta * 0.1f * myCar.speed);
            mapX = -(myCar.posX - Play.OFFSET_X);
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
}
