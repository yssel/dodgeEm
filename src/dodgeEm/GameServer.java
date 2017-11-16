package dodgeEm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer implements Runnable {
    public static final String APP_NAME = "Circle Wars 0.01";
    public static final int GAME_START = 0;
    public static final int IN_PROGRESS = 1;
    public final int GAME_END = 2;
    public final int WAITING_FOR_PLAYERS = 3;
    public static final int PORT = 4444;

    HashMap<Integer, Car> cars = new HashMap<>();

    String playerData;
    int playerCount = 0, maxPlayers;

    DatagramSocket serverSocket = null;

    int gameStage = WAITING_FOR_PLAYERS;

    Thread mainThread = new Thread(this);

    public GameServer(int maxPlayers){
        this.maxPlayers = maxPlayers;

        try {
            serverSocket = new DatagramSocket(PORT);
            serverSocket.setSoTimeout(100);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(-1);
        }catch(Exception e){}

        System.out.println("Game created.");

        mainThread.start();
    }

    public void broadcast(String msg){
        for(Integer i : cars.keySet()){
            send(cars.get(i));
        }
    }

    public void send(Car car){
//        DatagramPacket
    }



    @Override
    public void run() {
        while(true){
            byte[] buffer = new byte[256];
        }
    }
}
