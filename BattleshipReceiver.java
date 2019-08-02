import java.net.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.util.*;

class BattleshipReceiver{

  InputStream serverIn;
  HttpHandler handl;

  //Constructor
  public BattleshipReceiver(InputStream serverIn){
    this.serverIn = serverIn;
    handl = new HttpHandler();
  }

  public String get_Message(InputStream inputStream/*, String charset*/) throws IOException {

  	StringBuilder stringBuilder = new StringBuilder();
  	String line = null;
    boolean Guardian = true;


  	try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream/*, charset*/))){
  		while (Guardian){
        line = bufferedReader.readLine();
  			stringBuilder.append(line + "\r\n");
        Guardian = !stringBuilder.toString().contains("\r\n\r\n");
  		}
  	}

  	return stringBuilder.toString();
  }

}
