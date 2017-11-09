import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
   private static Socket clientSocket = null;
   private static PrintStream os = null;
   private static DataInputStream is = null;

   private static BufferedReader inputLine = null;
   private static boolean closed = false;

   public static void main(String[] args) {
      int portNumber = 8080;
      String host = "localhost";

      try {
      clientSocket = new Socket(host, portNumber);
      inputLine = new BufferedReader(new InputStreamReader(System.in));
      os = new PrintStream(clientSocket.getOutputStream());
      is = new DataInputStream(clientSocket.getInputStream());
      }
      catch (UnknownHostException e){
         System.out.println("Error. Host unknown." + host);
      }
      catch (IOException e){
         System.out.println("No I/O for host connection." + host);
      }

      if (clientSocket != null && os != null && is != null) {
         try {
            new Thread(new Client()).start();
            while(!closed) {
               os.println(inputLine.readLine().trim());
            }
            os.close();
            is.close();
            clientSocket.close();
         }
         catch (IOException e) {
            System.out.println("I/O Exception: " + e);
         }
      }
   }   

   public void run() {
      String responseLine;
      try {
         while((responseLine = is.readLine()) != null) {
            System.out.println(responseLine);
            if (responseLine.indexOf("Bye") != -1) {
               break;
            }
         }
         closed = true;
      }
      catch (IOException e){
         System.out.println("I/O Exception: " + e);
      }
   }
}

