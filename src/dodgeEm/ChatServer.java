package dodgeEm;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ChatServer implements GameConfig {
  public String name;
  public static ArrayList<ChatThread> threadList = new ArrayList<>();

  public static void main(String [] args){
    try {
      ServerSocket serverSocket = new ServerSocket(PORT);
        while(true){
          Socket clientSocket = serverSocket.accept();
          System.out.println("Connection accepted from ... " + clientSocket);
          ChatThread thread = new ChatThread(clientSocket, threadList);
          threadList.add(thread);
          thread.start();
        } 
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public static void removeThread(ChatThread thread) {
      threadList.remove(thread);
    }
}