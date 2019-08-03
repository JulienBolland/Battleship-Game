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
      System.out.println(charset);
      response.removeHeader("Transfert-Encoding");
      // Writing the headers
      serverOut.write(msg[0]);
      // Writing the body
      if(response.getHeader("Transfert-Encoding") == null)
        serverOut.write(msg[1]);
      else if(response.getHeader("Transfert-Encoding").contains("chunked"))
        chunckedEncoding(charset, msg[1]);
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
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try{
      OutputStream sout = sock.getOutputStream();
      while(body.length > CHUNKSIZE * (i + 1)){
        sout.write(Integer.toHexString(CHUNKSIZE).getBytes(charset));
        sout.write("\r\n".getBytes());
        sout.write(body, i * CHUNKSIZE, CHUNKSIZE);
        System.out.println("\n\n\n\n\nChunk launch:\n" + Integer.toHexString(CHUNKSIZE));
        sout.flush();
        sout.write("\r\n".getBytes());
        i++;
      }
      if(body.length - i * CHUNKSIZE > 0){
        sout.write(Integer.toHexString(body.length - i * CHUNKSIZE).getBytes(charset));
        sout.write(body, i * CHUNKSIZE, body.length - i *CHUNKSIZE);
        System.out.println("\n\n\n\n\nChunk launch:\n" + Integer.toHexString(body.length - i * CHUNKSIZE));
        sout.flush();
        sout.write("\r\n".getBytes());
      }
      sout.write(Integer.toHexString(0).getBytes(charset));
      System.out.println("\n\n\n\n\nChunk launch:\n 0");
    }
    catch(IOException e){
      System.err.print(e.getMessage());
    }
  }
}
