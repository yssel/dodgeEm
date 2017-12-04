package dodgeEm;

import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameServer implements Runnable{
    // SERVER States
    public static final int INITIALIZING_SERVER = 0;
    public static final int WAITING_FOR_PLAYERS = 1;
    public static final int CONNECTED_PLAYER = 2;
    public static final int GAME_START = 3;
    public static final int GAME_ONGOING = 4;
    public static final int GAME_ENDED = 5;

    /**
     SERVER SEND DATA FORMAT:
        A player successfully connected:
        2,id-carName-carColor-initialX-initialY

        A player moves:
        4,id-carAngle-posX-posY-hP

     **/

    // CLIENT States
    public static final int CONNECTING_PLAYER = 0;
    public static final int WAITING_FOR_START = 1;
    public static final int PLAYING_GAME = 2;
    public static final int PLAYER_DIED = 3;

    /**
     CLIENT SEND DATA FORMAT:
        Player wants to connect:
        0,name-carColor

        Player waits for game to start:
        1

        Player is playing:
        2,PlayerID-cursorX-cursorY-bumped-bumpType-bumpedCarPlayerID

        Player died:
        3
     **/

    int serverState;
    String carData;
    String playerData;
    int connectedPlayers;
    int totalPlayers;
    DatagramSocket serverSocket = null;
    int winnerId = -1;

    // Initial X and Y positions of Cars
    ArrayList<Point> initialPositions = new ArrayList<Point>();

    // Main Game Thread
    Thread gameThread = new Thread(this);

    // Player Database < PlayerID, Player >
    ArrayList<Player> players = new ArrayList<Player>();

    public GameServer(int numPlayers, int port){
        this.totalPlayers = numPlayers;

        // INITIALIZE SERVER
        this.serverState = INITIALIZING_SERVER;
        System.out.println("Server state: " + serverState);

        // Establish connection
        try{
            serverSocket = new DatagramSocket(port);
            serverSocket.setSoTimeout(100);
        }catch(IOException e){
            System.err.println("Could not listen on port: "+ port);
            System.exit(-1);
        }catch(Exception e){}

        // WAIT FOR PLAYERS TO COMPLETE
        this.serverState = WAITING_FOR_PLAYERS;
        System.out.println("Server state: " + serverState);
        this.connectedPlayers = 0;

        // Start main game thread
        gameThread.start();

    }

    public static void main(String args[]){
        if (args.length != 2){
            System.out.println("Usage: java GameServer <number of players> <port>");
            System.exit(1);
        }

        new GameServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    public Point getInitialXY(){
        Random rand = new Random();
        int x,y;
        // Generate safe initial X and Y position for car
        boolean coordDoesNotCollide;
        while (true) {
            x = rand.nextInt(3500) + 700;
            y = rand.nextInt(2300) + 700;
            coordDoesNotCollide = true;
            for (Point point : initialPositions) {
                if (x + 300 > point.getX() - 300 || x - 300 < point.getX() + 300 || y + 300 > point.getY() || y - 300 < point.getY()) {
                    coordDoesNotCollide = false;
                    break;
                }
            }

            if(coordDoesNotCollide) break;
        }

        Point feasiblePoint = new Point(x,y);
        return  feasiblePoint;
    }

    public void broadcastMessage(int state, String message){
        for(Player player : players){
            // Format: "state,message"
            message = Integer.toString(state) + "," + message;

            // send message to each player
            byte buf[] = message.getBytes();
            DatagramPacket packet;
            packet = new DatagramPacket(buf, buf.length, player.getAddress(),player.getPort());
            try{
                this.serverSocket.send(packet);
            }catch(IOException ioe){
                ioe.printStackTrace();
            }

        }

    }

    public void run(){
        byte[] buf;
        DatagramPacket packet;
        String playerData;
        String tokens[];
        String tokens2[];
        int playerState;

        while(true){
            switch (this.serverState){
                case WAITING_FOR_PLAYERS:
                    // Get the data from players
                    buf = new byte[256];
                    packet = new DatagramPacket(buf, buf.length);
                    try{
                        this.serverSocket.receive(packet);
                    }catch(Exception ioe){}

                    //Convert the array of bytes to string
                    playerData = new String(buf);

                    //remove excess bytes
                    playerData = playerData.trim();

                    tokens = playerData.split(",");
                    if(tokens[0].length() > 0){
                        playerState = Integer.parseInt(tokens[0]);
                    }else{
                        playerState = -1;
                    }

                    if(playerState == CONNECTING_PLAYER && tokens.length > 1){
                        // Tokenize
                        // Fomat: name-carColor
                        tokens2 = tokens[1].split("-");
                        String carName = tokens2[0];
                        int carColor = Integer.parseInt(tokens2[1]);

                        // Initial angle
                        float carAngle = 180;

                        // Randomize initial X and Y position available for client car
                        Point initialPoint = getInitialXY();
                        int initialX = (int)initialPoint.getX();
                        int initialY = (int)initialPoint.getY();

                        // Initialize Player
                        Player player = new Player(this.connectedPlayers, packet.getPort(), packet.getAddress(), carName, carColor, carAngle, initialX, initialY);
                        players.add(player);

                        // Update playerCount
                        connectedPlayers++;
                        if(connectedPlayers == totalPlayers) {
                            serverState = GAME_START;
                        }

                        // Inform players a new player has connected
                        // Format: 2,connectedPlayers-totalPlayers
                        String message = Integer.toString(connectedPlayers) + "-" + Integer.toString(totalPlayers);

                        broadcastMessage(CONNECTED_PLAYER, message);
                    }

                    break;

                case GAME_START:
                    // All players are now registered
                    System.out.println("Server state: " + serverState);

                    // Broadcast player database to all
                    // Format: 4,id-carName-carColor-initialX-initialY/
                    String message = "";
                    for(Player player: players){
                        message = message + player.getId() + "-" + player.getPlayerName() + "-" + player.getColor() + "-" + player.getPosX() + "-" + player.getPosY() + "/";
                    }
                    broadcastMessage(GAME_START, message);

                    // Countdown 3, 2, 1... start
                    try {
                        Thread.sleep(3000);
                    }catch (Exception e){}

                    serverState = GAME_ONGOING;
                    break;

                case GAME_ONGOING:
                    // Get the data from players
                    buf = new byte[256];
                    packet = new DatagramPacket(buf, buf.length);
                    try{
                        this.serverSocket.receive(packet);
                    }catch(Exception ioe){}

                    //Convert the array of bytes to string
                    playerData = new String(buf);

                    //remove excess bytes
                    playerData = playerData.trim();

                    tokens = playerData.split(",");
                    if(tokens[0].length() > 0){
                        playerState = Integer.parseInt(tokens[0]);
                    }else{
                        playerState = -1;
                    }

                    if(playerState == PLAYING_GAME && tokens.length > 1){
                        // Tokenize
                        // Format: PlayerID-posX-posY-angle-bumped-bumpType-opponentId
                        tokens2 = tokens[1].split("-");
                        int playerId = Integer.parseInt(tokens2[0]);
                        float posX = Float.parseFloat(tokens2[1]);
                        float posY = Float.parseFloat(tokens2[2]);
                        float angle = Float.parseFloat(tokens[3]);
                        int bumped = Integer.parseInt(tokens2[4]);

                        Player client = this.players.get(playerId);

                        // Car bumped
                        if(bumped == 1){
                            int bumpType = Integer.parseInt(tokens2[5]);
                            int opponentId = Integer.parseInt(tokens2[6]);

                            Player opponent = this.players.get(opponentId);

                            // Update hp of client and opponent
                            if(bumpType == 0){ // head-to-head
                                client.attack(opponent);
                                opponent.attack(client);
                            }else{ //head-to-side
                                client.attack(opponent);
                            }
                        }

                        // Set new posX posY angle
                        client.setPosX(posX);
                        client.setPosY(posY);
                        client.setAngle(angle);

                        // Broadcast client move
                        // Format: 4,id-carAngle-posX-posY-hP/id-carAngle-posX-posY-hP/.../...
                        message = "";
                        for(Player player: players){
                            message += player.toString();
                        }
                        broadcastMessage(GAME_ONGOING, message);
                    }

                    // Check if there's already a winner
                    int playerCount = 0;
                    Player alivePlayer = null;
                    for(Player player : this.players){
                        if(player.isAlive()) {
                            playerCount++;
                            alivePlayer = player;
                        }
                        if(playerCount > 2) break;
                    }

                    // One player left
                    if(playerCount == 1) {
                        serverState = GAME_ENDED;
                        winnerId = alivePlayer.getId();
                    }

                    break;

                case GAME_ENDED:
                    break;

            }
        }
    }

}
