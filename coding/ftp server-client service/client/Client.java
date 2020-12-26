import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
  public static void main(String[] args) {
    try {
      Socket s = new Socket("localhost", 6666); // creatin client using socket and giving the server address
      DataOutputStream dout = new DataOutputStream(s.getOutputStream()); // output stream variable
      DataInputStream din = new DataInputStream(s.getInputStream()); // input stream variable
      String str1, str2, strTemp, option, fileName, dirName;
      Scanner sc = new Scanner(System.in);

      while (true) {

        System.out.print("me(client) : ");
        str1 = sc.nextLine();

        StringTokenizer st = new StringTokenizer(str1, " ");
        strTemp = st.nextToken();

        if (strTemp.equals("stop")) break;

        else if (strTemp.equals("ls")) {
          dout.writeUTF(strTemp);
          dout.flush();
        }

        else if (strTemp.equals("mkdir")) {
          dout.writeUTF(strTemp);
          dout.flush();

          dirName = st.nextToken();
          dout.writeUTF(dirName);
          dout.flush();

          str2 = (String) din.readUTF();

          if (str2.equals("Ready")) {
            System.out.println((String) din.readUTF());
          }
          else {

            System.out.println(str2);
            option = sc.nextLine();
            dout.writeUTF(option);

            System.out.println((String) din.readUTF());
          }
        }

        else if (strTemp.equals("upload")) { // uploading file option

          dout.writeUTF(strTemp);// sending the "upload" option to server

          fileName = st.nextToken(); // getting file name
          File file = new File(fileName); // new file create

          if (!file.exists()) { // if file doesn't exists
            dout.writeUTF("File is not found!!!"); // sending to the server
            dout.flush(); // instantly
            System.out.println("File is not found!!!"); // console output showing
            continue; // nothing to do
          }
          else { // if the file exists

            dout.writeUTF("Ready"); // for the passing the waiting stream in server

            dout.writeUTF(fileName); // file name sending to server
            dout.flush(); // instantly
            // file name is sent to server and now server checks whether the file already exists in there
            // or not and sends the argument accordingly

            str2 = (String) din.readUTF(); // argument from the server
            System.out.println(str2); // showing "what server says"

            if (str2.equals("File already exits. Do you want to rewrite it? (y/n)")) { // if it matches with it

              // taking answer from the client
              option = sc.nextLine(); // taking user answer

              dout.writeUTF(option); // sending this answer to server
              dout.flush(); // instantly

              // if the answer of the user is "no" or any other things
              strTemp = (String) din.readUTF();
              if (!strTemp.equals("Ready")) {
                System.out.println(strTemp);
                continue;
              }
              //if (option.equals("n") || !option.equals("y")) {

                //strTemp = (String) din.readUTF(); // server's saying for negative or other answer of user
                //System.out.println(strTemp); // showing it
                //continue; // do nothing else
              //}
              else {
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
                System.out.println("Uploading file was sucessful!!!"); // console shwoing
              }
            }
            else {
              System.out.println("File is not uploaded");
              continue;
            }
          }
        }

        else if (strTemp.equals("download")) {
          dout.writeUTF(strTemp);
          dout.flush();

          fileName = st.nextToken();
          dout.writeUTF(fileName);
          dout.flush();

          str2 = (String) din.readUTF();
          if (str2.equals("File doesn't exists!!!")) {
            System.out.println(str2 + "\nFile could not be downloaded!!!!");
            continue;
          }
          else {

            File file = new File(fileName);
            if (file.exists()) {
              System.out.println("This file already exists. Do you want to replace it? (y/n)");
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

              System.out.println("Downloading file successfully"); // console output for the server
              continue;
            }
            else {
              System.out.println("File is not downloded!!!!");
              dout.writeUTF("File is not downloded!!!!");
              dout.flush();
              continue;
            }
          }
        }
        else {
          dout.writeUTF(strTemp);
          dout.flush();
          str2 =(String) din.readUTF();
          System.out.println("server : " + str2);
        }
      }
      System.out.println("closing connction!!!!!");
      dout.writeUTF("stop");
      dout.flush();
      dout.close(); // closing the output steam
      s.close();// closing the socket

    } catch(Exception e) {
      System.out.println("Client could not connect to server!!!\n" + e);
    }
  }
}
