import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  public static Socket s;
  public static DataInputStream din;
  public static DataOutputStream dout;

  public static void main(String[] args) {
    try {
      s = new Socket("localhost", 6666);
      din = new DataInputStream(s.getInputStream());
      dout = new DataOutputStream(s.getOutputStream());
      String str;
      Scanner sc = new Scanner(System.in);
      while (true) {
        System.out.print("message: ");
        str = sc.nextLine();
        dout.writeUTF(str);
        dout.flush();
        if (str.equals("stop")) {
          break;
        } else if (str.equals("ls")) {
          getListFiles();
        }
      }
      dout.close();
      s.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public static void getListFiles() {
    try {
      System.out.println("                           SERVER");
      while (true){
        String temp = (String) din.readUTF();
        System.out.println(temp);
        if (temp.equals(" ")) {
          break;
        }
      }
      System.out.println("                             End");
    } catch(Exception e) {
      System.out.println("Something went wrong...\n" + e);
    }
  }
}