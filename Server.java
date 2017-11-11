import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

//sends data sent by a player to other players

public class Server implements Runnable, Const{
  // data received from the player
    
  //main game thread
  Thread t = new Thread(this);

  String playerData;
  //currently connected count
  int playerCount=0;
  DatagramSocket serverSocket = null;
  //game state (map of all players and player's status)
  State game;
  //current game stage
  int gameStage=WAITING_FOR_PLAYERS;
  int numPlayers;

  public Server(int numPlayers){
    this.numPlayers= numPlayers;
    try {
      serverSocket = new DatagramSocket(PORT);
      serverSocket.setSoTimeout(100);
    } catch (IOException e) {
            System.err.println("Could not listen on port: "+PORT);
            System.exit(-1);
    }catch(Exception e){}

    //Create the game state
    game = new State();
    
    System.out.println("Game created...");
    
    //Start the game thread
    t.start();
  }
  
  public void broadcast(String msg){
    System.out.println("Entered broadcast");
    for(Iterator ite=game.getPlayers().keySet().iterator();ite.hasNext();){
      String name=(String)ite.next();
      System.out.println("WHEREE ARE THE PLAYERSSS "+ name);
      Player player=(Player)game.getPlayers().get(name);      
      send(player,msg); 
    }
  }


  //send message to a player
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
  
  public void run(){
    while(true){
            
      // Get the data from players
      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      try{
          serverSocket.receive(packet);
      }catch(Exception ioe){}
      
      //bytes[] to string
      playerData=new String(buf);
      
      //remove excess bytes
      playerData = playerData.trim();
      
      if (!playerData.equals("")){
        System.out.println("Player Data:"+playerData);
      }
    
      // process
      switch(gameStage){
          case WAITING_FOR_PLAYERS:
            //System.out.println("Game State: Waiting for players...");
            String tokens[] = playerData.split(";");
            if (tokens[0].equals("CONNECT")){
              String address = packet.getAddress().toString();
              //address= address.replace("/","");
              //System.out.println(tokens[1]+"\n"+address+"\n"+packet.getPort());
              //InetAddress newAddress= null;
              //try{
              //  newAddress= InetAddress.getByName(address);
              //}catch(UnknownHostException e){System.out.println(e);}

              Player player=new Player(tokens[1],packet.getAddress(),packet.getPort());
              System.out.println("Player connected: "+tokens[1]);
              game.update(tokens[1].trim(),player);
              broadcast("CONNECTED;"+tokens[1]);
              playerCount++;
              if (playerCount==numPlayers){
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
             //Player data was received!
            String[] playerInfo = playerData.split("\n"); 
            if (playerInfo[0].equals("CHAT")){
              //Tokenize:
              //The format: CHAT;<player name>;<message>           
              String pname =playerInfo[1];
              String message= playerInfo[2];
              broadcast("CHAT\n"+pname+"\n"+message);
            }
            break;
      }         
    }
  } 

  public static void main(String args[]) throws IOException{
     if (args.length != 1){
      System.out.println("Usage: Server <number of players>");
      System.exit(1);
    }
    
    new Server(Integer.parseInt(args[0]));
    //new ServerThread().start(); //starting instance of server thread
  }
}