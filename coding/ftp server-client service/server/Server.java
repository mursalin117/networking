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
        //StringTokenizer st = new StringTokenizer(str1, " ");
        //System.out.print("me(server) : ");
        //str2 = sc.nextLine();
        //strTemp = st.nextToken();
        if (str1.equals("stop")) {
          break;
        }

        else if (str1.equals("ls")) {

          File curDir = new File(".");
          getAllFiles(curDir);

          //System.out.println("ls nai");
        }

        else if (str1.equals("stop")) {
          break;
        }

        else if (str1.equals("mkdir")) {

          dirName = (String) din.readUTF();

          File dir = new File(dirName);

          if (dir.isDirectory()) {
            dout.writeUTF("Directory already exists. Do you want to replace it? (y/n)");
            dout.flush();

            option = (String) din.readUTF();
          }
          else {
            option = "y";
            dout.writeUTF("Ready");
            dout.flush();
          }

          if (option.equals("y") && dir.mkdirs()) {
            System.out.println("Directory is created!!!");
            dout.writeUTF("Directory is created!!!");
            dout.flush();
          }
          else {
            System.out.println("Directory is not created!!!");
            dout.writeUTF("Directory is not created!!!");
            dout.flush();
          }
          //System.out.println("mkdir hobe na");
        }

        else if (str1.equals("upload")) { // upload option

          // getting the argument from client
          strTemp = (String) din.readUTF();

          // checking the client's argument
          if (strTemp.equals("File is not found!!!")) { // if the file doesn't exists
            System.out.println(strTemp + "\nFile could not be uploaded..."); // console output for server
            continue; // nothing to do
          }
          else { // if the file exists
            // getting the file name which will be uploaded to the server
            fileName = (String) din.readUTF(); // file name
            File file = new File(fileName); // creating new file

            if (file.exists()) { // if the file already exists in the server
              dout.writeUTF("File already exits. Do you want to rewrite it? (y/n)"); // asking for the opinion of the client
              dout.flush(); // instantly

              option = din.readUTF(); // taking the opinion of the client
            }
            else {
              option = "y";
            }

            if (option.equals("y")) { // if the opinion is 'y', the procedure of further working
              dout.writeUTF("Ready"); // sending ok flag to client
              dout.flush(); // instantly

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
                //fout.write(bytes, 0, size);
              //}

              fout.close(); // closing the FileOutputStream
              //din.close();

              System.out.println("File uploaded successfully"); // console output for the server
              continue;
            }
            else { // if the option is = 'n' or anything else
              // file will not be uploaded
              System.out.println("File is not uploaded!!!"); // server console output
              dout.writeUTF("File is not uploaded!!!"); // client console oupput sending
              dout.flush(); // instantly
              continue; // nothing to do any more
            }
          }
        }

        else if (str1.equals("download")) {

          fileName = (String) din.readUTF();
          File file = new File(fileName);

          if (!file.exists()) {
            System.out.println("File doesn't exists!!!");
            dout.writeUTF("File doesn't exists!!!");
            dout.flush();
            continue;
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
              System.out.println("File was downloaded sucessful!!!"); // console shwoing
            }
            else {
              System.out.println(strTemp);
              continue;
            }
          }
          //System.out.println("Download kora jabe na");
        }

        else {
          dout.writeUTF("You have entered wrong keyword");
          dout.flush();
        }
      }

      System.out.println("Client is disconnected!!\n\nServer is closing!!!");
      dout.close(); // closing the oupput stream
      s.close(); // socket closing
      ss.close(); // closing the server Socket

    } catch (Exception ex) {
      System.out.println("unexpected error occurs!!!\nServer is disconnected!!!\n" + ex);
    }
  }

  public static void getAllFiles(File curDir) {

    File[] filesList = curDir.listFiles();
    for (File f : filesList) {
      if (f.isDirectory()) {
        System.out.println(f.getPath() + "  ___  " + f.getName() + "/");
        getAllFiles(f);
      }
      if(f.isFile()) {
        System.out.println(f.getPath() + "----> " + f.getName());
      }
    }
  }

  public static void receiveFile() {

  }

  public static void sendFile() {

  }

  public static void createDirectory() {

  }

  public static void closeServer(){

  }

  public static void main(String[] args) {
    Server Myserver = new Server();
  }
}
