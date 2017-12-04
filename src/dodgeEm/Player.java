package dodgeEm;

import java.net.InetAddress;

public class Player {
    // Player Attributes
    private int id;
    private InetAddress address;
    private int port;
    private String playerName;
    private int playerAlive;

    // Car Attributes
    private int color;
    private float hp;
    private float maxHp;
    private float damage;

    // Movement Attributes
    private float posX;
    private float posY;
    private float angle;
    private float speed;
    private float maxSpeed;

    // Power Up
    //protected PowerUp item = null;

    public Player(int id, int port, InetAddress address, String name, int carColor, float carAngle, int initialX, int initialY){
        /** CLIENT **/
        this.id = id;
        this.port = port;
        this.address = address;
        this.playerName = name;

        /** CAR **/
        this.color = carColor;
        this.angle = carAngle;
        this.posX = initialX;
        this.posY = initialY;
        this.damage = 0.5f;

        /** HEALTH **/
        this.hp = 100f;
        this.maxHp = 100f;
        this.playerAlive = 1;

        /** BUMPER CAR SPEED **/
        this.speed = 5f;
        this.maxSpeed = this.speed;

        /** Initial Position **/
        // Randomize X and Y
    }

    public String getPlayerName() {
        return playerName;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public float getHp() {
        return hp;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public void attack(Player opponent){
        opponent.setHp(opponent.getHp() - this.damage);
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public String toString() {
        // Format: id-carAngle-posX-posY-hP
        return Integer.toString(this.id) + "-" + this.angle  + "-" + this.posX  + "-" + this.posY  + "-" + this.hp;
    }

    public boolean isAlive(){
        return this.playerAlive == 1;
    }
}
