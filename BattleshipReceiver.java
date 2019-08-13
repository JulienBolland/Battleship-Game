/************************************************
* Introduction to computer networking : Project 2
*
* Authors : Julien Bolland : s161622 @ULiège
*           Thomas Mazur : s162939 @ULiège
*
* All rights reserved.
************************************************/
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
  // This method receive the message on the input stream and
  // read it (in bytes) and converts it into a String
  public String get_Message() throws IOException {
    InputStream inputStream = sock.getInputStream();
  	StringBuilder stringBuilder = new StringBuilder();
  	String line = null;
    //headers
    String headers = "";
    //body
    String body = "";
    boolean Guardian = true;

  	try{
      //Reading headers
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
  		while (Guardian){
        line = bufferedReader.readLine();
  			stringBuilder.append(line + "\r\n");
        Guardian = !stringBuilder.toString().contains("\r\n\r\n");
  		}
      headers = stringBuilder.toString();
      int value;
      // Reading body
      while(bufferedReader.ready() && (value = bufferedReader.read()) != -1){
        body += Character.toString((char)value);
      }
  	}
    catch(Exception e){
      e.printStackTrace();
    }
    System.out.println("\n\nRequest:\n" + headers + body);
  	return headers + body;
  }

}
