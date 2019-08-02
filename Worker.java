/************************************************
* Introduction to computer networking : Project 2
*
* Authors : Julien Bolland : s161622 @ULiège
*           Thomas Mazur : s162939 @ULiège
*
* All rights reserved.
************************************************/

import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class Worker extends Thread{

  private static ArrayList<Game> listOfGames;
  private Socket sock;
  private InputStream sin;
  private HttpHandler httpresponse;
  private HttpHandler httpreq;

  public Worker(Socket _sock, ArrayList<Game> list){
    try{
      this.sock = _sock;
      listOfGames = list;
      this.sin = _sock.getInputStream();

    }
    catch(IOException e){
      System.out.println("Error during the creation of the worker: "+e.getMessage());
    }
  }
  @Override
  public void run(){
    try{
      // The socket expires after 2 minutes if the client doesn't respond
      sock.setSoTimeout(120000);
      //Initialization of parameters
      HttpCookie cookie = null;
      BattleshipHTML htmlGenerator = new BattleshipHTML();
      Game currentGame = null;
      ArrayList<String[]> query = null;
      //Request reception
      BattleshipReceiver msgrecept = new BattleshipReceiver(sin);
      BattleshipEmitter emitter = new BattleshipEmitter(sock);

      //Parsing of the received message and initialization of response.
      httpreq = new HttpHandler(msgrecept.get_Message(sin));
      httpresponse = new HttpHandler();

      URL myURL = httpreq.getURL();
      query = httpreq.getQuery();

      //Retrieving the game associated with the cookie if it exists
      String tmp = httpreq.getHeader("Cookie");
      if(tmp  != null){
        cookie = new HttpCookie("SESSID", tmp);
        currentGame = getGame(cookie);
      }



      //First part, get method
      if(httpreq.getMethod().equals("GET")){
        //Redirection to play.html
        if(myURL.getPath().equals("/")){
          httpresponse.printStatus(303);
          httpresponse.printHeader("Location", "/play.html");
          emitter.send(httpresponse);
        }
        else if(myURL.getPath().equals("/play.html")){

          if(currentGame != null && query != null && queryCheck(query)){
            String hit = "false", win = "false", gameOver = "false";

            // We send a JSON file with 3 fields :
            // - 'win' => say if the game is won or not
            // - 'gameOver' => say if the player lost the game
            // - 'hit' => say if a ship has been hit or not with the attempt
            if(currentGame.checkAttempt(Integer.parseInt(query.get(0)[1]), Integer.parseInt(query.get(1)[1])))
              hit = "true";

            //If the player wins
            if(currentGame.isWin()){
              listOfGames.remove(currentGame);
              httpresponse.printHeader("Set-Cookie", "SESSID=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
              win = "true";
              cookie = null;
            }
            // If the player looses
            if(currentGame.gameOver()){
              listOfGames.remove(currentGame);
              httpresponse.printHeader("Set-Cookie", "SESSID=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
              gameOver = "true";
              cookie = null;
            }
            httpresponse.printHeader("Content-Type", "JSON");
            httpresponse.printBody("{ \"win\":\""+win+"\", \"gameOver\":\""+gameOver+"\", \"hit\":"+hit+"}");
            emitter.send(httpresponse);
          }
          //Page actualisation
          else if(currentGame != null && query == null){

          }
          //New Game creation
          else if(currentGame == null && query == null){
            cookie = newCookie();
            currentGame = getGame(cookie);

            //Construction of the response
            cookie.getValue();
            httpresponse.printHeader("Set-Cookie", /*cookie.getValue()+ */"; path=/");
            if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
              httpresponse.printHeader("Content-Encoding", "gzip");
            httpresponse.printBody(htmlGenerator.generateHtml("GET", currentGame));
            emitter.send(httpresponse);
          }
          else
            throw new BattleshipException("400");
        }
        else if(myURL.getPath().equals("/halloffame.html")){
          httpresponse.printStatus(303);
          httpresponse.printHeader("Location", "/play.html");
          emitter.send(httpresponse);
        }
        //Here should be added a new page
        else
          throw new BattleshipException("404");
      }




      //Second part: Post method
      else if(httpreq.getMethod().equals("POST")){

        //Redirection to play.html
        if(myURL.getPath().equals("/")){
          httpresponse.printStatus(303);
          httpresponse.printHeader("Location", "/play.html");
          emitter.send(httpresponse);
        }
        else if(myURL.getPath().equals("/play.html")){
          if(currentGame != null && query != null && queryCheck(query)){
            //Collecting the attempt
            int[] attempt = new int[2] ;
            attempt[0] = Integer.parseInt(query.get(0)[1]);
            attempt[1] = Integer.parseInt(query.get(1)[1]);

            //Checking whether a ship has been touched
            currentGame.checkAttempt(attempt[0], attempt[1]);

            //If the player won the game
            if(currentGame.isWin()){
              httpresponse.printHeader("Set-Cookie", "SESSID=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
              httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
              if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
                httpresponse.printHeader("Content-Encoding", "gzip");
              cookie = null;
              emitter.send(httpresponse);
            }
            //If the player lost
            else if(currentGame.gameOver()){
              httpresponse.printHeader("Set-Cookie", "SESSID=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
              httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
              if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
                httpresponse.printHeader("Content-Encoding", "gzip");
              cookie = null;
              emitter.send(httpresponse);
            }
            //Page actualisation
            else{
              if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
                httpresponse.printHeader("Content-Encoding", "gzip");
              httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
              httpresponse.printBody(htmlGenerator.generateHtml("POST", currentGame));
              emitter.send(httpresponse);
            }
          }
          //Page actualisation
          else if(currentGame != null && query == null){
            if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
              httpresponse.printHeader("Content-Encoding", "gzip");
            httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
            httpresponse.printBody(htmlGenerator.generateHtml("POST", currentGame));
            emitter.send(httpresponse);
          }
          //New game creation
          else if(currentGame == null && query == null){
            cookie = newCookie();
            currentGame = getGame(cookie);
            if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
              httpresponse.printHeader("Content-Encoding", "gzip");
            httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
            httpresponse.printBody(htmlGenerator.generateHtml("POST", currentGame));
            emitter.send(httpresponse);
          }
          else
            throw new BattleshipException("400");
        }
        else if(myURL.getPath().equals("/halloffame.html")){
          httpresponse.printStatus(303);
          httpresponse.printHeader("Location", "/play.html");
          emitter.send(httpresponse);
        }
        //Here should be added a new page
        else
          throw new BattleshipException("404");
      }

      //If the method is neither GET nor POST
      else
        throw new BattleshipException("501");




      //Close the socket and the server.
      System.out.println("\nFinished treating request, closing connection");
      sock.close();
    }
    catch(BattleshipException e){
      //Jesaispasencore
    }
    catch(SocketException e){
			System.err.println(e.getMessage());
		}
		catch(SocketTimeoutException e){
      System.err.println(e.getMessage());
    }
		catch(IOException e){
      System.err.println(e.getMessage());
    }
  }

  private HttpCookie newCookie(){
    HttpCookie cookie = null;
    Game currentGame = null;
    synchronized(listOfGames){
      listOfGames.remove(currentGame);
      cookie = new HttpCookie("SESSID", Integer.toString(listOfGames.size()));
      // We set the timelife of the cookie
      cookie.setMaxAge(600);
      listOfGames.add(new Game(cookie));
    }
    return cookie;

  }

  private Game getGame(HttpCookie cookie){
    Game currentGame;
    for(int i = 0; i < listOfGames.size(); i++){
      if(listOfGames.get(i).getCookie().getValue().equals(cookie.getValue())){
        currentGame = listOfGames.get(i);
        currentGame.justDisplay();
        return currentGame;
      }
    }
    return null;
  }

  private boolean queryCheck(ArrayList<String[]> query){
    return query.size() == 2 && query.get(0)[0].equals("x") && query.get(1)[0].equals("y");
  }

}
