import java.net.*;
import java.io.*;
import java.util.*;

public class ChatThread extends Thread{

  private final Socket clientSocket;
  private final ArrayList<ChatThread> threadList;
  public String name = null;
  private PrintStream os;
  private DataInputStream is;

  public ChatThread (Socket clientSocket, ArrayList<ChatThread> threadList) {
    this.clientSocket = clientSocket;
    this.threadList = threadList;
  }

  @Override
  public void run() {
    try {
      handleClientSocket();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void handleClientSocket () throws IOException, InterruptedException {
    this.is = new DataInputStream (clientSocket.getInputStream());
    this.os = new PrintStream (clientSocket.getOutputStream());

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    String nameReader = reader.readLine();
    String nameToken[] = nameReader.split("\n");    
    this.name  = nameToken[0];

    while(true) {
      String line = is.readLine();
      if (line.equalsIgnoreCase("quit")) {
        String msg = name + " is leaving the chat ... ";
        handleQuit(msg, name);
        break;
      }
      else {
        String[] msg = line.split("\n");
        handleMessage(msg, name);
      }
    }

    is.close();
    os.close();
    clientSocket.close();
  }

  private void send(String msg, String messenger) throws IOException {
    os.println(messenger + "> " + msg);
    // if (!(msg.equals("\n")) || !(msg.equals(""))) {
    //   os.println(messenger + "> " + msg);
    // }
  }

  private void handleMessage(String[] msgTokens, String messenger) throws IOException{
    String msg = msgTokens[0];
    // if (!(msg.equals("\n")) || !(msg.equals(""))) {
      for (ChatThread i : threadList) {
        i.send(msg, messenger);
      }
    // }
  }

  private void handleQuit(String msg, String messenger) throws IOException {
    for (int i=0; i<threadList.size(); i++) {
      if (threadList.get(i) != this) {
        threadList.get(i).send(msg, messenger);
      }
    }
  }
} 