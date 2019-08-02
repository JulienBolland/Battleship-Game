import java.net.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.util.*;

class BattleshipReceiver{

  Socket sock;
  HttpHandler handl;

  //Constructor
  public BattleshipReceiver(Socket _sock){
    this.sock = _sock;
    handl = new HttpHandler();
  }

  public String get_Message(/*InputStream inputStream, String charset*/) throws IOException {
    InputStream inputStream = sock.getInputStream();
  	StringBuilder stringBuilder = new StringBuilder();
  	String line = null;
    boolean Guardian = true;

  	try{
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream/*, charset*/));
  		while (Guardian){
        line = bufferedReader.readLine();
  			stringBuilder.append(line + "\r\n");
        Guardian = !stringBuilder.toString().contains("\r\n\r\n");
  		}
  	}
    catch(Exception e){
      System.out.println("Error while reading buffer : " + e.getMessage());
    }
    System.out.println("Requête reçue:\n\n" + stringBuilder.toString() + "\n\n");
  	return stringBuilder.toString();
  }

}
