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
  static int CHUNKSIZE = 4095;

  //Constructor
  public BattleshipEmitter(Socket _sock){
    this.sock = _sock;
  }

  //This methods sends the response in the OutputStream in chuncked
  //encoding (gzip compression if enabled has already been done)
  public void send(HttpHandler response){
    try{
      OutputStream serverOut = sock.getOutputStream();
      byte[][] msg = response.getHttp();
      // We retrieve the type of charset used
      String charset = "";
      String s = response.getHeader("Content-Type");
      int i = s.indexOf("charset=")+8; // 8 = (length of "charset=")
      for(; i < s.length(); i++){
        charset += s.charAt(i);
      }
      // Writing the headers
      serverOut.write(msg[0]);

      // Writing the body
      if(response.getHeader("Transfer-Encoding") == null)
        serverOut.write(msg[1]);
      else if(response.getHeader("Transfer-Encoding").contains("chunked")){
        chunckedEncoding(charset, msg[1]);
        serverOut.write(Integer.toHexString(0).getBytes(charset));
        serverOut.write("\r\n\r\n".getBytes(charset));
      }
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
  private void chunckedEncoding(String charset, byte[] body){

    int i = 0;
    try{
      OutputStream sout = sock.getOutputStream();
      while(body.length >= CHUNKSIZE * (i + 1)){
        sout.write(Integer.toHexString(CHUNKSIZE).getBytes(charset));
        sout.write("\r\n".getBytes(charset));
        sout.write(body, i * CHUNKSIZE, CHUNKSIZE);
        sout.write("\r\n".getBytes(charset));
        sout.flush();
        i++;
      }
      if(body.length - i * CHUNKSIZE > 0){
        sout.write(Integer.toHexString(body.length - i * CHUNKSIZE).getBytes(charset));
        sout.write("\r\n".getBytes(charset));
        sout.write(body, i * CHUNKSIZE, body.length - i *CHUNKSIZE);
        sout.flush();
        sout.write("\r\n".getBytes(charset));
      }
    }
    catch(IOException e){
      System.err.print(e.getMessage());
    }
  }
}
