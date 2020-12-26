import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
  public static void main(String[] args) {
    try {
      Socket s = new Socket("localhost", 6666); // creatin client using socket and giving the server address
      DataOutputStream dout = new DataOutputStream(s.getOutputStream()); // output stream variable
      String str;
      Scanner sc = new Scanner(System.in);
      while (true) {
        str = sc.nextLine();
        if(str.equals("stop")) break;
        else {
          dout.writeUTF(str); // output the stream in unicode form
          dout.flush(); // for immediate storing the output data in destination
        }
      }
      dout.writeUTF(str);
      dout.flush();
      dout.close(); // closing the output steam
      s.close();// closing the socket

    } catch(Exception e) {
      System.out.println(e);
    }
  }
}
