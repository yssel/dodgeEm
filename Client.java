import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
   private static Socket clientSocket = null;
   private static PrintStream outputStream = null;
   private static DataInputStream inputStream = null;

   private static BufferedReader inputLine = null;
   private static boolean closed = false;

   public static void main(String[] args) {
      int portNumber = Integer.parseInt(args[1]);
      String host = args[0];

    //Client tries to join the server at host:portnumber
      try {
        System.out.println("Joining " + host + ":" + portNumber);
        clientSocket = new Socket(host, portNumber);
        inputLine = new BufferedReader(new InputStreamReader(System.in));
        outputStream = new PrintStream(clientSocket.getOutputStream());
        inputStream = new DataInputStream(clientSocket.getInputStream());
      }
      catch (UnknownHostException e){
         System.out.println("Host unknown.");
      }
      catch (IOException e){
         System.out.println("No I/O for host connection.");
      }

      //Allows user to enter message
      if (clientSocket != null && outputStream != null && inputStream != null) {
         try {
            new Thread(new Client()).start();
            while(!closed) {
                outputStream.println(inputLine.readLine().trim());
            }
            outputStream.close();
            inputStream.close();
            clientSocket.close();
         }
         catch (IOException e) {
            System.out.println("I/O Exception: " + e);
         }
      }
   }   

   public void run() {
      String responseLine;

      //Displays the sent message to the sender
      try {
         while((responseLine = inputStream.readLine()) != null) {
            System.out.println(responseLine);
         }
         closed = true;
      }
      catch (IOException e){
         System.out.println("I/O Exception: " + e);
      }
   }
}

