package dodgeEm;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.*;
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
    private ConcurrentHashMap<Integer, PowerUp> healthPowerUps;
    private ConcurrentHashMap<Integer, PowerUp> boostPowerUps;
    private ConcurrentHashMap<Integer, PowerUp> gumPowerUps;
    private HashMap<String, Shape> bounds;

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
        healthPowerUps = new ConcurrentHashMap<>();
        boostPowerUps = new ConcurrentHashMap<>();
        gumPowerUps = new ConcurrentHashMap<>();

        /** INITIALIZE OTHER BUMPER CARS **/
        car = new Car("Yssel", 1, 0);
        car.init(650, 500);

        /** INITIALIZE MY CAR **/
        myCar = new Car(MainMenu.name, MainMenu.carColor, 180);
        myCar.init(1000, 1000);

        /** INITIALIZE MAP ARENA BOUNDS **/
        initArenaBounds();

        /** RANDOMIZE POWER UPS **/
       for(int i=0; i<6; i++){
           Random rand = new Random();
           healthPowerUps.put(i, new Health(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT), ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
           healthPowerUps.get(i).spawn(1);
       }
        for(int i=0; i<12; i++){
            Random rand = new Random();
            gumPowerUps.put(i, new Gum(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT), ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
            gumPowerUps.get(i).spawn(2);
       }
        for(int i=0; i<12; i++){
            Random rand = new Random();
            boostPowerUps.put(i, new Boost(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT), ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
            boostPowerUps.get(i).spawn(3);
        }
   }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        /** RENDERING OF MAP **/
        map.draw(mapX, mapY);

        /** RENDERING OF POWER UPS **/
        for(Integer i: healthPowerUps.keySet()){
            healthPowerUps.get(i).render(1);
        }
        for(Integer i: gumPowerUps.keySet()){
            gumPowerUps.get(i).render(2);
        }
        for(Integer i: boostPowerUps.keySet()){
            boostPowerUps.get(i).render(3);
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

        for(Integer i: healthPowerUps.keySet()){
            if(myCar.bounds.intersects(healthPowerUps.get(i).bounds)){
                myCar.usePowerUp(healthPowerUps.get(i));
                healthPowerUps.remove(i);
                Random rand= new Random();
                healthPowerUps.put(i, new Health(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT), ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
                healthPowerUps.get(i).spawn(1); //DOUBLE CHECK
            }
            else if(healthPowerUps.get(i).toRemove==true){
                healthPowerUps.remove(i);
                Random rand= new Random();
                healthPowerUps.put(i, new Health(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT), ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
                healthPowerUps.get(i).spawn(1);
            }
        }
        for(Integer i: gumPowerUps.keySet()) {
            if (myCar.bounds.intersects(gumPowerUps.get(i).bounds)) {
                myCar.usePowerUp(gumPowerUps.get(i));
                gumPowerUps.remove(i);
                Random rand= new Random();
                gumPowerUps.put(i, new Health(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT), ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
                gumPowerUps.get(i).spawn(2); //DOUBLE CHECK
            }
            else if(gumPowerUps.get(i).toRemove==true){
                gumPowerUps.remove(i);
                Random rand= new Random();
                gumPowerUps.put(i, new Health(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT), ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
                gumPowerUps.get(i).spawn(2);
            }
        }
        for(Integer i: boostPowerUps.keySet()) {
            if (myCar.bounds.intersects(boostPowerUps.get(i).bounds)) {
                myCar.usePowerUp(boostPowerUps.get(i));
                boostPowerUps.remove(i);
                Random rand= new Random();
                boostPowerUps.put(i, new Health(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT), ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
                boostPowerUps.get(i).spawn(3); //DOUBLE CHECK
            }
            else if(boostPowerUps.get(i).toRemove==true){
                boostPowerUps.remove(i);
                Random rand= new Random();
                boostPowerUps.put(i, new Health(ARENA_LEFT + rand.nextFloat() * (ARENA_RIGHT - ARENA_LEFT), ARENA_TOP + rand.nextFloat() * (ARENA_BOTTOM - ARENA_TOP)));
                boostPowerUps.get(i).spawn(3); //DOUBLE CHECK
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
}
