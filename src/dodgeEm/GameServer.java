package dodgeEm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameServer implements Runnable, GameConfig{
    String playerData;
    int playerCount=0; //currently connected player
    DatagramSocket serverSocket = null;

    ArrayList<String> names = new ArrayList<>();
    GameState game;
    int gameStage=WAITING_FOR_PLAYERS;
    Thread t = new Thread(this);

    public GameServer(){
        try {
            serverSocket = new DatagramSocket(PORT);
            serverSocket.setSoTimeout(100);
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+PORT);
            System.exit(-1);
        }catch(Exception e){}
        //Create the game state
        game = new GameState();

        System.out.println("Game created...");

        //Start the game thread
        t.start();
    }

    /**
     * Helper method for broadcasting data to all players
     */
    public void broadcast(String msg){
        for(Iterator ite=game.getPlayers().keySet().iterator();ite.hasNext();){
            String name=(String)ite.next();
            Player player=(Player)game.getPlayers().get(name);
            send(player,msg);
        }
    }

    /**
     * Send a message to a player
     */
    public void send(Player player, String msg){
        DatagramPacket packet;
        byte buf[] = msg.getBytes();
        packet = new DatagramPacket(buf, buf.length, player.getAddress(),player.getPort());
        try{
            serverSocket.send(packet);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void run() {
        while(true){

            // Get the data from players
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try{
                serverSocket.receive(packet);
            }catch(Exception ioe){}

            /**
             * Convert the array of bytes to string
             */
            playerData=new String(buf);

            //remove excess bytes
            playerData = playerData.trim();
            //if (!playerData.equals("")){
            //	System.out.println("Player Data:"+playerData);
            //}

            // process
            switch(gameStage){
                case WAITING_FOR_PLAYERS:
                    //System.out.println("Game State: Waiting for players...");
                    if (playerData.startsWith("CONNECT")){
                        boolean isUnique= true;
                        String tokens[] = playerData.split(" ");
                        Player player=new Player(tokens[1],packet.getAddress(),packet.getPort());
                        System.out.println("Player connected: "+tokens[1]);
                        game.update(tokens[1].trim(),player);
                        broadcast("CONNECTED "+tokens[1]);
                        if(names.size()==0) names.add(tokens[1]);
                        else{
                            for(int i=0; i<names.size();i++){
                                if(names.get(i).equals(tokens[1])){
                                    isUnique=false;
                                    names.add(tokens[1]);
                                }
                            }
                        }

                        if(isUnique) playerCount++;
                        if (playerCount==playerNum){
                            gameStage=GAME_START;
                        }
                    }
                    break;
                case GAME_START:
                    System.out.println("Game State: START");
                    broadcast("START");
                    gameStage=IN_PROGRESS;
                    break;
                case IN_PROGRESS:
                    //System.out.println("Game State: IN_PROGRESS");

                    //Player data was received!
                    if (playerData.startsWith("PLAYER")){
                        //Tokenize:
                        //The format: PLAYER <player name> <x> <y>
                        String[] playerInfo = playerData.split(" ");
                        String pname =playerInfo[1];
                        //Get the player from the game state
                        Player player=(Player)game.getPlayers().get(pname);
                        player.update( Float.parseFloat(playerInfo[2].trim()),Float.parseFloat(playerInfo[3].trim()),Float.parseFloat(playerInfo[4].trim()), Float.parseFloat(playerInfo[5].trim()),Float.parseFloat(playerInfo[6].trim()),Float.parseFloat(playerInfo[7].trim()),Float.parseFloat(playerInfo[8].trim()),Float.parseFloat(playerInfo[9].trim()), Float.parseFloat(playerInfo[10].trim()), Float.parseFloat(playerInfo[11].trim()), playerInfo[1], Integer.parseInt(playerInfo[12].trim()));
                        //Update the game state
                        game.update(pname, player);
                        //Send to all the updated game state
                        broadcast(game.toString());
                    }
                    break;
            }
        }
    }

    public static void main(String args[]) {
        new GameServer();
    }
}
