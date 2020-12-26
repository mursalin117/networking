import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
  public static void main(String[] args) {
    try {
      ServerSocket ss = new ServerSocket(6666); // socket for creating server
      Socket s = ss.accept(); // establish connection between server and client
      DataInputStream din = new DataInputStream(s.getInputStream()); // input stream declaration
      DataOutputStream dout = new DataOutputStream(s.getOutputStream()); // output stream declaration
      String str1, str2;
      Scanner sc = new Scanner(System.in);
      while (true) {
        str1 = (String) din.readUTF(); // input stream in unicode form
        System.out.println("client : " + str1); // showing the message
        System.out.print("me(server) : ");
        str2 = sc.nextLine();
        if (str2.equals("stop")) break;
        else {
          dout.writeUTF(str2); // output stream in unicode form
          dout.flush(); // for immediate storing the output stream for destination
        }
      }
      dout.writeUTF("Server is closing");
      dout.flush();
      dout.close(); // closing the oupput stream
      s.close(); // socket closing
      ss.close(); // closing the server Socket

    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
