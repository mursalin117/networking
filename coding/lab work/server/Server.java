import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
  public static ServerSocket ss;
  public static Socket s;
  public static DataInputStream din;
  public static DataOutputStream dout;
  public static void main(String[] args) {
    try {
      ss = new ServerSocket(6666);
      s = ss.accept();
      System.out.println("Server is connected!");
      din = new DataInputStream(s.getInputStream());
      dout = new DataOutputStream(s.getOutputStream());
      String str;
      while (true) {
        str = (String) din.readUTF();
        System.out.println("Client: " + str);
        if (str.equals("stop")) {
          break;
        } else if (str.equals("ls")) {
          File currDir = new File(".");
          getListFiles(currDir);
          dout.writeUTF(" ");
          dout.flush();
        }
      }
      System.out.println("Client is disconnected...\nServer is closing...");
      dout.close();
      ss.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public static void getListFiles(File curDir) {
    try {
      File[] filesList = curDir.listFiles();
      for (File file : filesList) {
        if (file.isDirectory()) {
          //System.out.println(file.getPath() + "/");
          dout.writeUTF(file.getPath() + "/");
          dout.flush();
          getListFiles(file);
        }
        if (file.isFile()) {
          //System.out.println(file.getPath() + "----> " + file.getName());
          dout.writeUTF(file.getPath() + "--> " + file.getName());
          dout.flush();
        }
      }
    } catch(Exception e) {
      System.out.println("Somethng went wrong...\n" + e);
    }
  }
}