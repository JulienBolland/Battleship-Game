import java.net.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.util.*;

class BattleshipReceiver{

<<<<<<< HEAD
  Socket sock;
  HttpHandler handl;
=======
  private Socket sock;
  private HttpHandler handl;
>>>>>>> 9496d01a3dead55a068d17438b52d0001b63f1fc

  //Constructor
  public BattleshipReceiver(Socket _sock){
    this.sock = _sock;
    handl = new HttpHandler();
  }

<<<<<<< HEAD
  public String get_Message(/*InputStream inputStream, String charset*/) throws IOException {
    InputStream inputStream = sock.getInputStream();
=======
  public String get_Message(){


>>>>>>> 9496d01a3dead55a068d17438b52d0001b63f1fc
  	StringBuilder stringBuilder = new StringBuilder();
  	String line = null;
    boolean Guardian = true;

<<<<<<< HEAD
  	try{
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream/*, charset*/));
=======

  	try{
      InputStream sin = this.sock.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sin/*, charset*/));
>>>>>>> 9496d01a3dead55a068d17438b52d0001b63f1fc
  		while (Guardian){
        line = bufferedReader.readLine();
  			stringBuilder.append(line + "\r\n");
        Guardian = !stringBuilder.toString().contains("\r\n\r\n");
  		}
<<<<<<< HEAD
  	}
    catch(Exception e){
      System.out.println("Error while reading buffer : " + e.getMessage());
    }
=======
    }
    catch(IOException e){
      System.err.println(e.getMessage());

	   }
>>>>>>> 9496d01a3dead55a068d17438b52d0001b63f1fc
    System.out.println("Requête reçue:\n\n" + stringBuilder.toString() + "\n\n");
  	return stringBuilder.toString();
  }

}
