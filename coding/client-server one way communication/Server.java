import java.io.*;
import java.net.*;

public class Server {
  public static void main(String[] args) {
    try {
      ServerSocket ss = new ServerSocket(6666); // socket for creating server
      Socket s = ss.accept(); // establish connection between server and client
      DataInputStream dis = new DataInputStream(s.getInputStream()); // input stream declaration
      String str = (String) dis.readUTF(); // input stream in unicode form
      System.out.println("message : " + str); // showing the message
      ss.close(); // closing the server Socket

    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
