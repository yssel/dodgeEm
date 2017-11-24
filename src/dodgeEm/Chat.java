package dodgeEm;
import dodgeEm.GameConfig;

import java.io.*;
import java.net.*;

public class Chat implements GameConfig, Runnable {
  private static Socket clientSocket = null;
  private static PrintStream os = null;
  private static DataInputStream is = null;

  private static BufferedReader reader = null;
  private static boolean closed = false;
  public static String name = null;

  public static void main(String[] args) {
    int portNumber = PORT;
    String host = args[0];
    name = args[1];

    try {
      clientSocket = new Socket(host, portNumber);
      reader = new BufferedReader(new InputStreamReader(System.in));
      os = new PrintStream(clientSocket.getOutputStream());
      is = new DataInputStream(clientSocket.getInputStream());
      sendName(); //send name to server
    } catch (UnknownHostException e){
      e.printStackTrace();
    } catch (IOException e){
      e.printStackTrace();
    }

    try {
      new Thread(new Chat()).start();
      while(!closed) {
        os.println(reader.readLine().trim());   //printing
      }
      os.close();
      is.close();
      clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }  

  public static void sendName() {
    try {
      String name1 = name;
      os.print(name1);
    } catch (Exception e){
      e.printStackTrace();
    }
  }

 public void run() {
    String responseLine;
    try {
      while((responseLine = is.readLine()) != null) {
        System.out.println(responseLine + "yo");
        if (responseLine.indexOf("quit") != -1) {
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

