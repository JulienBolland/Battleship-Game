import java.net.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.util.*;

class BattleshipReceiver{

  private Socket sock;
  private HttpHandler handl;

  //Constructor
  public BattleshipReceiver(Socket _sock){
    this.sock = _sock;
    handl = new HttpHandler();
  }

  public String get_Message(){


  	StringBuilder stringBuilder = new StringBuilder();
  	String line = null;
    boolean Guardian = true;


  	try{
      InputStream sin = this.sock.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sin/*, charset*/));
  		while (Guardian){
        line = bufferedReader.readLine();
  			stringBuilder.append(line + "\r\n");
        Guardian = !stringBuilder.toString().contains("\r\n\r\n");
  		}
    }
    catch(IOException e){
      System.err.println(e.getMessage());

	   }
    System.out.println("Requête reçue:\n\n" + stringBuilder.toString() + "\n\n");
  	return stringBuilder.toString();
  }

}
