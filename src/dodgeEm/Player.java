package dodgeEm;
import java.net.InetAddress;

public class Player {

    private InetAddress address;
    private int port;

    /** Attributes **/
    /** HEALTH **/
    private float hp;
    private float maxHp;

    /** SPEED **/
    protected float speed;
    protected float maxSpeed;

    /** POSITIONING AND DIRECTION **/
    protected float posX;
    protected float posY;
    protected float angle;

    /** SIZE **/
    protected float width;
    protected float height;

    /** CAR DAMAGE TO OPPONENT **/
    protected float damage;

    /** VISUALIZATION **/
    private String name;
    private int color;

    public Player(String name,InetAddress address, int port){
        this.address = address;
        this.port = port;
        this.name = name;
    }

    public InetAddress getAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }

    protected void update(float hp,float maxHp,float speed, float maxSpeed,float posX,float posY,float angle,float width, float height, float damage, String name, int color){
        /** HEALTH **/
        this.hp=hp;
        this.maxHp=maxHp;

        /** SPEED **/
        this.speed=speed;
        this.maxSpeed=maxSpeed;

        /** POSITIONING AND DIRECTION **/
        this.posX=posX;
        this.posY=posY;
        this.angle=angle;

        /** SIZE **/
        this.width=width;
        this.height=height;

        /** CAR DAMAGE TO OPPONENT **/
        this.damage=damage;

        /** VISUALIZATION **/
        this.name=name;
        this.color=color;
    }

    public String toString(){
        String myDetails ="";

        myDetails+="PLAYER ";

        myDetails+= this.name+" ";
        /** HEALTH **/
        myDetails+=this.hp+" ";
        myDetails+=this.maxHp+" ";

        /** SPEED **/
        myDetails+= this.speed+" ";
        myDetails+= this.maxSpeed+" ";

        /** POSITIONING AND DIRECTION **/
        myDetails+= this.posX+" ";
        myDetails+= this.posY+" ";
        myDetails+= this.angle+" ";

        /** SIZE **/
        myDetails+= this.width+" ";
        myDetails+= this.height+" ";

        /** CAR DAMAGE TO OPPONENT **/
        myDetails+= this.damage+" ";

        /** VISUALIZATION **/
        myDetails+= this.color;

        return myDetails;
    }
}
