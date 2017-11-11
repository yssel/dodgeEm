import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
//testing
import java.io.InputStreamReader;
import java.io.BufferedReader;

//ref: circlewars
//author: JACHermocilla

public class Client implements Runnable, Const{
  //Game timer, handler receives data from server to update game state
  Thread t=new Thread(this);
  //Flag to indicate whether this player has connected or not
  boolean connected=false;
  DatagramSocket socket = new DatagramSocket();
  String serverData;
  String server;
  String name;
  String pname;

  public Client(String server, String name) throws Exception{
    this.server=server;
    this.name=name;
    //set some timeout for the socket
    socket.setSoTimeout(100);

    t.start(); 

  }

  //send data to server
  public void send(String msg){
    try{
          byte[] buf = msg.getBytes();
          InetAddress address = InetAddress.getByName(server);
          DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT); //UDP port from Const
          socket.send(packet);
        }catch(Exception e){}
    
  }

  //thread to receive game info
  public void run(){
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    while(true){
      try{
        Thread.sleep(1);
      }catch(Exception ioe){}

      try{
        if(in.ready()){       
          send("CHAT\n"+name+"\n"+in.readLine());  
        }
      }
      catch(Exception e){}
            
      //Get the data from players
      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      try{
          socket.receive(packet);
      }catch(Exception ioe){ 
      }
      
      serverData=new String(buf);
      serverData=serverData.trim();
      
      //if (!serverData.equals("")){
      //  System.out.println("Server Data:" +serverData);
      //}

      String tokens[]= serverData.split(";");
      if (!connected && tokens[0].equals("CONNECTED")){
        //setting connected to true if successfull
        connected=true;
        System.out.println("Connected.");
      }else if (!connected){
        //reconnect if failed
        System.out.println("Connecting..");       
        send("CONNECT;"+name);
      }else if (connected){
        //if the server has other player's chat
        //not yet sure for batch chat
        String[] playerInfo = serverData.split("\n"); 
        if (playerInfo[0].equals("CHAT")){
            String pname =playerInfo[1];
            String chatMessage = playerInfo[2]; 
            if(!(chatMessage.equals("")))         
              System.out.println(pname+": "+chatMessage);
        }    
      }
    }
  }
  

   public static void main(String[] args) throws Exception{
     if (args.length != 2){
        System.out.println("Usage: java Client <server> <player name>");
        System.exit(1);
      }

      new Client(args[0],args[1]);
   }   
}

