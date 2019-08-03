/* This class emits messages on the OutputStream and compress them.

  - CONSTRUCTOR : BattleshipEmitter(PrintWriter d_out);
  - METHODS : void sendMessage(HttpHandler handl);
              private void compress(); à refaire totalement
              private void chunkedEncode(); à refaire totalement
*/
import java.net.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.util.*;

class BattleshipEmitter{

  private Socket sock;

  //Constructor
  public BattleshipEmitter(Socket _sock){
    this.sock = _sock;
  }

  //This methods sends the response in the OutputStream in chuncked
  //encoding (gzip compression if enabled has already been done)
  public void send(HttpHandler request){
    try{
      OutputStream serverOut = sock.getOutputStream();
      byte[][] msg = request.getHttp();
      // We retrieve the type of charset used
      String charset = "";
      String s = request.getHeader("Content-Type");
      int i = s.indexOf("charset=")+8; // 8 = (length of "charset=")
      for(; i < s.length(); i++){
        charset += s.charAt(i);
      }
      // Writing the headers
      serverOut.write(msg[0]);
      // Writing the body with chuncked encoding
      serverOut.write(msg[1]);
      // Flushing
      serverOut.flush();
      System.out.println("\n\nResponse:");
      System.out.println(new String(msg[0]));
    }
    catch(IOException e){
      System.out.println(e.getMessage());
    }
  }

  // This methods receives the body in byte, and returns the chuncked
  // encoded body.
  private byte[] chunckedEncoding(String charset, byte[] body){
    ByteArrayOutputStream concat = new ByteArrayOutputStream();
    byte[] msg = null;
    try{
      concat.write((Integer.toHexString(body.length) + "\r\n").getBytes(charset));
      concat.write((body));
      concat.write(("\r\n" + "0").getBytes(charset));
    }
    catch(IOException e){
      System.out.println(e.getMessage());
    }
    msg = concat.toByteArray();
    return msg;
  }
}
