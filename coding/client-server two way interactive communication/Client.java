import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
  public static void main(String[] args) {
    try {
      Socket s = new Socket("localhost", 6666); // creatin client using socket and giving the server address
      DataOutputStream dout = new DataOutputStream(s.getOutputStream()); // output stream variable
      DataInputStream din = new DataInputStream(s.getInputStream()); // input stream variable
      String str1, str2;
      Scanner sc = new Scanner(System.in);
      while (true) {
        System.out.print("me(client) : ");
        str1 = sc.nextLine();
        if(str1.equals("stop")) break;
        else {
          dout.writeUTF(str1); // output the stream in unicode form
          dout.flush(); // for immediate storing the output data in destination
          str2 = (String) din.readUTF(); // input stream in unicode form
          System.out.println("server : " + str2);
        }
      }
      dout.writeUTF("Client is closing");
      dout.flush();
      dout.close(); // closing the output steam
      s.close();// closing the socket

    } catch(Exception e) {
      System.out.println(e);
    }
  }
}
