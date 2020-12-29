import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

  public Socket s; // creatin client using socket and giving the server address
  public DataOutputStream dout; // output stream variable
  public DataInputStream din; // input stream variable
  public String str1, str2, strTemp, option, fileName, dirName;
  public Scanner sc = new Scanner(System.in);

  public Client() {
    try {
      s = new Socket("localhost", 6666); // creatin client using socket and giving the server address
      dout = new DataOutputStream(s.getOutputStream()); // output stream variable
      din = new DataInputStream(s.getInputStream()); // input stream variable

      while (true) {

        System.out.print("client(me) : ");
        str1 = sc.nextLine();

        StringTokenizer st = new StringTokenizer(str1, " ");
        strTemp = st.nextToken();

        if (strTemp.equals("stop")) {
          break;
        }

        else if (strTemp.equals("ls")) {
          dout.writeUTF(strTemp);
          dout.flush();
          getListFiles();
        }

        else if (strTemp.equals("mkdir")) {
          dout.writeUTF(strTemp);
          dout.flush();
          dirName = st.nextToken();
          createDirectory(dirName);
        }

        else if (strTemp.equals("upload")) { // uploading file option
          dout.writeUTF(strTemp);
          fileName = st.nextToken();
          uploadFile(fileName);
        }

        else if (strTemp.equals("download")) {
          dout.writeUTF(strTemp);
          dout.flush();
          fileName = st.nextToken();
          downloadFile(fileName);
        }

        else {
          dout.writeUTF(strTemp);
          dout.flush();
          str2 =(String) din.readUTF();
          System.out.println("server : " + str2);
        }
      }
      System.out.println("Client : closing connction!!!!!");
      dout.writeUTF("stop");
      dout.flush();
      dout.close(); // closing the output steam
      s.close();// closing the socket

    } catch(Exception e) {
      System.out.println("Client could not connect to server!!!\n" + e);
    }
  }

  public void getListFiles() {
    try {
      System.out.println("                           SERVER");
      while (true){
        str2 = (String) din.readUTF();
        System.out.println(str2);
        if (str2.equals(" ")) {
          break;
        }
      }
      System.out.println("                             End");
    } catch(Exception e) {
      System.out.println("Something went wrong...\n" + e);
    }
  }

  public void createDirectory(String dirName) {
    try {
      dout.writeUTF(dirName);
      dout.flush();

      str2 = (String) din.readUTF();

      if (str2.equals("Ready")) {
        System.out.println("sever : " + (String) din.readUTF());
      }
      else {

        System.out.println("server : " + str2);
        option = sc.nextLine();
        dout.writeUTF(option);

        System.out.println("server : " + (String) din.readUTF());
      }
    } catch(Exception e) {
      System.out.println("Directory is not created.\nSomething went wrong.\n" + e);
    }
  }

  public void uploadFile(String fileName) {
    try {
      File file = new File(fileName);

      if (!file.exists()) {
        System.out.println("client: File is not found.");
        dout.writeUTF("File is not found.");
        dout.flush();
      }
      else {
        dout.writeUTF(fileName);

        str2 = (String) din.readUTF();

        if (str2.equals("File already exists. Do you want to replace it? (y/n)")) {
          System.out.println("server : " + str2);
          System.out.print("client(me) : ");
          option = sc.nextLine();
          dout.writeUTF(option);

          str2 = (String) din.readUTF();
          if (!str2.equals("Ready")) {
            System.out.println("server : " + str2);
            return;
          }
        }
        // when the option is 'y' or the server sends "Ready" for new file , the procedure

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
          //dout.write(bytes, 0, count);
          //dout.flush();
        //}
        //System.out.println("Count =   " + count);
        //dout.close();
        fin.close(); // closing FileInputStream

        //dout.writeUTF("File uploaded successfully!!!");
        //dout.flush();
        System.out.println("client : Uploading file was sucessful!!!"); // console shwoing
      }
    } catch(Exception e) {
      System.out.println("File is not uploaded.\nSomething went wrong.\n" + e);
    }
  }

  public void downloadFile(String fileName) {
    try {
      dout.writeUTF(fileName);
      dout.flush();

      str2 = (String) din.readUTF();
      if (str2.equals("File doesn't exists!!!")) {
        System.out.println("server : " + str2 + "\nFile could not be downloaded!!!!");
      }
      else {

        File file = new File(fileName);
        if (file.exists()) {
          System.out.println("client : This file already exists. Do you want to replace it? (y/n)");
          System.out.print("client(me) : ");
          option = sc.nextLine();
        }
        else {
          option = "y";
        }

        if (option.equals("y")) {
          dout.writeUTF("Ready");
          dout.flush();

          FileOutputStream fout = new FileOutputStream(file); // FileOutputStream instialization
          str2 = (String) din.readUTF(); // getting the size of the file in sring form
          int size = Integer.parseInt(str2); // parsing the size in the integer form
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

          System.out.println("client : Downloading file was successful!!!"); // console output for the server
        }
        else {
          System.out.println("client : File is not downloded!!!!");
          dout.writeUTF("File is not downloded!!!!");
          dout.flush();
        }
      }
    } catch(Exception e) {
      System.out.println("File is not downloaded.\nSomething went wrong.\n" + e);
    }
  }

  public static void main(String[] args) {
    Client User = new Client();
  }
}
