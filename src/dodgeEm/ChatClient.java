package dodgeEm;

import java.io.*;
import java.net.*;

public class ChatClient implements GameConfig, Runnable {
  private static Socket clientSocket = null;
  private static PrintStream os = null;
  private static DataInputStream is = null;
  private static boolean closed = false;

  public ChatClient(){}

  public void startChatSession(String playerName){
    try {
      clientSocket = new Socket(HOST, PORT);
      os = new PrintStream(clientSocket.getOutputStream());
      is = new DataInputStream(clientSocket.getInputStream());
      sendName(playerName); //send name to server
    } catch (UnknownHostException e){
      e.printStackTrace();
    } catch (IOException e){
      e.printStackTrace();
    }

    try {
      new Thread(this).start();
      if(closed){
          os.close();
          is.close();
          clientSocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void send(String message){
    os.println(message);
  }

  public static void sendName(String name) {
    try {
      os.print(name);
    } catch (Exception e){
      e.printStackTrace();
    }
  }

 public void run() {
    String response;
    try {
      while((response = is.readLine()) != null) {
          Play.emitMessage(response);
          if (response.indexOf("quit") != -1) {
              break;
          }
      }
      closed = true;
    }
    catch (IOException e){
       e.printStackTrace();
    }
 }
}

