import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import javax.sound.midi.SysexMessage;

public class Server {
  private static ServerSocket serverSocket = null;
  private static Socket clientSocket = null;
  private static final int maxClientsCount = 10;
  private static final clientThread[] threads = new clientThread[maxClientsCount];

  
  //Usage: java Server [portNumber]
  public static void main(String args[]) {
    if(args.length == 0){
      System.out.println("Usage: java Server [portNumber]");
      System.exit(1);
    }
    
    //Creates server at specific port number
    try { 
      int portNumber = Integer.parseInt(args[0]);    
      serverSocket = new ServerSocket(portNumber);
      System.out.println("Listening at port " + portNumber);
    }
    catch(IOException e) {
      System.out.println(e);
    }

    while(true) {
      try {
        clientSocket = serverSocket.accept();

        int activeClientCount = 0;

        //The server creates a new client thread when there is an available slot
        for(int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null) {
            (threads[i] = new clientThread(clientSocket, threads)).start();
            activeClientCount = i;
            break;
          }
        }

        //If the active client count is beyond the server capacity, it will not allow the client to join the server.
        if(activeClientCount == maxClientsCount) {
          PrintStream outputStream = new PrintStream(clientSocket.getOutputStream());
          outputStream.println("Server too busy. Try later.");
          outputStream.close();
          clientSocket.close();
        }
      }
      catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}

class clientThread extends Thread {
  private String clientName = null;
  private DataInputStream inputStream = null;
  private PrintStream outputStream = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

    try {
      inputStream = new DataInputStream(clientSocket.getInputStream());
      outputStream = new PrintStream(clientSocket.getOutputStream());
      String name;

      // Prompts client to enter display name
      outputStream.println("Enter your name: ");
      name = inputStream.readLine().trim();

      outputStream.println("Welcome to Dodge 'Em chat room. Enter /quit to leave");
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] == this) {
            clientName = name;
            break;
          }
        }

        // Notifies other users that a new user has entered the chat room
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            threads[i].outputStream.println(name + " entered the chat room");
          }
        }
      }
      
      while (true) {
        String line = inputStream.readLine();
        if (line.startsWith("/quit")) {
          break;
        }
        // Sends message to all users
        else {
          synchronized (this) {
            for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i].clientName != null) {
                threads[i].outputStream.println("<" + name + "> " + line);
              }
            }
          }
        }
      }

      // Notifies other users when a user left the room
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this
              && threads[i].clientName != null) {
            threads[i].outputStream.println(name + " left the chat room.");
          }
        }
      }
      outputStream.println("You left the chat room");

      // Free up slots in server
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }

      inputStream.close();
      outputStream.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }
}