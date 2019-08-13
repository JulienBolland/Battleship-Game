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

// This class emits messages on the OutputStream and compress them.
class BattleshipEmitter{

  private Socket sock;
  //Chunk size is arbitrary FFF
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
      }
      // Flushing
      serverOut.flush();
      System.out.println("\n\nResponse:");
      System.out.println(new String(msg[0]));
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }

  // This methods receives the body in byte, and returns the chuncked
  // encoded body.
  private void chunckedEncoding(String charset, byte[] body){

    int i = 0;
    try{
      OutputStream sout = sock.getOutputStream();
      //While the body is greater than chunksize, we send full chunksize chunks
      while(body.length >= CHUNKSIZE * (i + 1)){
        sout.write(Integer.toHexString(CHUNKSIZE).getBytes(charset));
        sout.write("\r\n".getBytes(charset));
        sout.write(body, i * CHUNKSIZE, CHUNKSIZE);
        sout.write("\r\n".getBytes(charset));
        sout.flush();
        i++;
      }
      //When the body is shorter than the chunksize, we send the remaining data
      if(body.length - i * CHUNKSIZE > 0){
        sout.write(Integer.toHexString(body.length - i * CHUNKSIZE).getBytes(charset));
        sout.write("\r\n".getBytes(charset));
        sout.write(body, i * CHUNKSIZE, body.length - i *CHUNKSIZE);
        sout.flush();
        sout.write("\r\n".getBytes(charset));
      }
      //then we send the last empty chunk
      sout.write(Integer.toHexString(0).getBytes(charset));
      sout.write("\r\n\r\n".getBytes(charset));
    }
    catch(IOException e){
      System.err.print(e.getMessage());
    }
  }
}
