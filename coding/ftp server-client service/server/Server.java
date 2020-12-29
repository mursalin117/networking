import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

  public ServerSocket ss; // socket for creating server
  public Socket s; // establish connection between server and client
  public DataInputStream din;// input stream declaration
  public DataOutputStream dout;; // output stream declaration
  public String str1, str2, strTemp, option, fileName, dirName;
  public Scanner sc = new Scanner(System.in);

  public Server() {
    try {
      ss = new ServerSocket(6666); // socket for creating server
      System.out.println("Server is waiting...");
      s = ss.accept(); // establish connection between server and client
      System.out.println("Sever is connected!!!");

      din = new DataInputStream(s.getInputStream()); // input stream declaration
      dout = new DataOutputStream(s.getOutputStream()); // output stream declaration

      while (true) {
        str1 = (String) din.readUTF(); // input stream in unicode form
        System.out.println("client : " + str1); // showing the message

        if (str1.equals("stop")) {
          break;
        }

        else if (str1.equals("ls")) {
          File curDir = new File(".");
          getListFiles(curDir);
          dout.writeUTF(" ");
          dout.flush();
        }

        else if (str1.equals("stop")) {
          break;
        }

        else if (str1.equals("mkdir")) {
          dirName = (String) din.readUTF();
          createDirectory(dirName);
        }

        else if (str1.equals("upload")) { // upload option
          receiveFile();
        }

        else if (str1.equals("download")) {
          fileName = (String) din.readUTF();
          sendFile(fileName);
        }

        else {
          dout.writeUTF("You have entered wrong keyword");
          dout.flush();
        }
      }

      System.out.println("client : Client is disconnected!!\nserver(me) : Server is closing!!!");
      dout.close(); // closing the oupput stream
      s.close(); // socket closing
      ss.close(); // closing the server Socket

    } catch (Exception ex) {
      System.out.println("unexpected error occurs!!!\nServer is disconnected!!!\n" + ex);
    }
  }

  public void getListFiles(File curDir) {
    try {
      File[] filesList = curDir.listFiles();
      for (File file : filesList) {
        if (file.isDirectory()) {
          //System.out.println(file.getPath() + "/");
          dout.writeUTF(file.getPath() + "/");
          dout.flush();
          getListFiles(file);
        }
        if(file.isFile()) {
          //System.out.println(file.getPath() + "----> " + file.getName());
          dout.writeUTF(file.getPath() + "----> " + file.getName());
          dout.flush();
        }
      }
    } catch(Exception e) {
      System.out.println("Somethng went wrong...\n" + e);
    }
  }

  public void receiveFile() {

    try {
      strTemp = (String) din.readUTF();
      if (strTemp.equals("File is not found.")) {
        System.out.println("client : " + strTemp + "\nFile is not uploaded...");
      }
      else {
        System.out.println("Uploading File name = " + strTemp);
        fileName = strTemp;
        File file = new File(fileName);

        if (file.exists()) {
          dout.writeUTF("File already exists. Do you want to replace it? (y/n)");
          dout.flush();

          option = (String) din.readUTF();
          //System.out.println("client : " + option);
        }
        else{
          //dout.writeUTF("Ready");
          option = "y";
          // file input nibe....
        }

        if (option.equals("y")) {
          dout.writeUTF("Ready");
          // file nibe....
          FileOutputStream fout = new FileOutputStream(file); // FileOutputStream instialization
          strTemp = (String) din.readUTF(); // getting the size of the file in sring form
          int size = Integer.parseInt(strTemp); // parsing the size in the integer form
          byte[] bytes = new byte[size]; // byte argument for the file

          // file receiving procedure
          din.read(bytes, 0, size); // reading the file using the DataInputStream
          fout.write(bytes, 0, size); // writing the file data of byte form using the FileOutputStream

          // another way of file receiving for large size file specially
          //int count;
          //while ((count = din.read(bytes)) != -1) {
            //fout.write(bytes, 0, count);
            //System.out.println("Count = " + count);
          //}

          fout.close(); // closing the FileOutputStream
          //din.close();

          System.out.println("server(me) : File received successfully"); // console output for the server
        }
        else {
          // nibe na....
          System.out.println("server(me) : file is not received.");
          dout.writeUTF("File is not uploaded.");
          dout.flush();
        }
      }
    } catch(Exception e) {
      System.out.println("Something went wrong!!!\n" + e);
    }
  }

  public void sendFile(String fileName) {
    try {
      File file = new File(fileName);

      if (!file.exists()) {
        System.out.println("server(me) : File doesn't exists!!!");
        dout.writeUTF("File doesn't exists!!!");
        dout.flush();
        //return;
      }
      else {
        dout.writeUTF("Ready");
        dout.flush();

        strTemp = (String) din.readUTF();
        if (strTemp.equals("Ready")) {
          // when the option is 'y', the procedure

          int size = (int) file.length(); // file size
          dout.writeUTF(Integer.toString(size)); // file size sending

          byte[] bytes = new byte[size]; // byte stream for the file
          FileInputStream fin = new FileInputStream(file); // FileInputStream initialization

          // sending files
          fin.read(bytes, 0, size); // file reading using FileInputStream
          dout.write(bytes, 0, size); // sending the file using socket
          dout.flush(); // instantly sending

          // another way of sending file, specially for the large size
          // try both the way....
          //int count;
          //while ((count = fin.read(bytes)) != -1) {
            //dout.write(bytes, 0, size);
          //}

          //dout.close();
          fin.close(); // closing FileInputStream

          //dout.writeUTF("File uploaded successfully!!!");
          //dout.flush();
          System.out.println("server(me) : File was sent successfully!!!"); // console shwoing
        }
        else {
          System.out.println("client : " + strTemp);
          //return;
        }
      }
      //System.out.println("Download kora jabe na");
    } catch(Exception e) {
      System.out.println("Something went wrong!!!\n" + e);
    }
  }

  public void createDirectory(String dirName) {

    try {
      System.out.println("Creating Directory name = " + dirName);
      File dir = new File(dirName);

      if (dir.isDirectory()) {
        dout.writeUTF("Directory already exists. Do you want to replace it? (y/n)");
        dout.flush();

        option = (String) din.readUTF();
        if (option.equals("y")) { // if directory already exists then first delet it and recreate it for replace option
          new File(dirName).delete();
        }
      }
      else {
        option = "y";
        dout.writeUTF("Ready");
        dout.flush();
      }

      if (option.equals("y") && dir.mkdirs()) {
        System.out.println("server(me) : Directory is created!!!");
        dout.writeUTF("Directory is created!!!");
        dout.flush();
      }
      else {
        System.out.println("server(me) : Directory is not created!!!");
        dout.writeUTF("Directory is not created!!!");
        dout.flush();
      }
      //System.out.println("mkdir hobe na");

    } catch(Exception e) {
      System.out.println("Directory is not created.\nSomething went wrong...\n" + e);
    }
  }

  public static void main(String[] args) {
    Server Myserver = new Server();
  }
}
