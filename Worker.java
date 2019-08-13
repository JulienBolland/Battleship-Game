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
  private static ArrayList<HttpCookie> listOfCookies;
  private Socket sock;
  private InputStream sin;
  private HttpHandler httpresponse;
  private HttpHandler httpreq;
  private BattleshipEmitter emitter;
  private Ranking gameRank;

  public Worker(Socket _sock, ArrayList<Game> list, Ranking gameRank){
    try{
      this.gameRank = gameRank;
      if(listOfCookies == null)
        listOfCookies = new ArrayList<HttpCookie>();
      this.sock = _sock;
      listOfGames = list;
      this.sin = _sock.getInputStream();
      httpresponse = new HttpHandler();
      emitter = new BattleshipEmitter(sock);
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
  @Override
  public void run(){
    try{
      purgeCookie();
      // The socket expires after 10 miliseconds if the client doesn't respond
      sock.setSoTimeout(100);
      //Initialization of parameters
      HttpCookie cookie = null;
      BattleshipHTML htmlGenerator = new BattleshipHTML();
      Game currentGame = null;
      ArrayList<String[]> query = null;
      //Request reception
      BattleshipReceiver msgrecept = new BattleshipReceiver(sock);

      //Parsing of the received message and initialization of response.
      httpreq = new HttpHandler(msgrecept.get_Message());

      if(!httpreq.getVersion().equals("HTTP/1.0") && !httpreq.getVersion().equals("HTTP/1.1"))
        throw new BattleshipException("505");

      URL myURL = httpreq.getURL();
      query = httpreq.getQuery();


      //Retrieving the game associated with the cookie if it exists
      String tmp = httpreq.getCookie("SESSID");
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
              removeCookie(cookie);
              gameRank.addGame(currentGame);

              httpresponse.printHeader("Set-Cookie", "SESSID=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
              win = "true";
              cookie = null;
            }
            // If the player looses
            if(currentGame.gameOver()){
              removeCookie(cookie);
              gameRank.addGame(currentGame);

              httpresponse.printHeader("Set-Cookie", "SESSID=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
              gameOver = "true";
              cookie = null;
            }
            httpresponse.printHeader("Content-Type", "application/json");
            httpresponse.printHeader("No Chunk", "Full");
            httpresponse.printBody("{\"win\":\""+win+"\", \"gameOver\":\""+gameOver+"\", \"hit\":\""+hit+"\"}\n");
            emitter.send(httpresponse);
          }
          //Page actualisation
          else if(currentGame != null && query == null){
            if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
              httpresponse.printHeader("Content-Encoding", "gzip");
            httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
            httpresponse.printBody(htmlGenerator.generateHtml("GET", currentGame, "main", gameRank));
            emitter.send(httpresponse);
          }
          //New Game creation
          else if(currentGame == null && query == null){
            cookie = newCookie();
            currentGame = getGame(cookie);

            //Construction of the response
            cookie.getValue();
            httpresponse.printHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; path=/");
            if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
              httpresponse.printHeader("Content-Encoding", "gzip");
            httpresponse.printBody(htmlGenerator.generateHtml("GET", currentGame, "main", gameRank));
            emitter.send(httpresponse);
          }
          else
            throw new BattleshipException("400");
        }
        else if(myURL.getPath().equals("/halloffame.html")){
          if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
            httpresponse.printHeader("Content-Encoding", "gzip");
          httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
          httpresponse.printBody(htmlGenerator.generateHtml("GET", currentGame, "hof", gameRank));
          emitter.send(httpresponse);
          gameRank = null;
        }
        else
          throw new BattleshipException("404");
      }


      //Second part: Post method
      //Only used when javascript is unenabled in play.html
      else if(httpreq.getMethod().equals("POST")){
        if(currentGame != null && query != null && queryCheck(query)){
          //Check if the header Content-Length is used
          if(httpreq.getHeader("Content-Length") == null){
            throw new BattleshipException("411");
          }
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
            httpresponse.printBody(htmlGenerator.getEndPage("You won!"));
            removeCookie(cookie);
          gameRank.addGame(currentGame);
            cookie = null;
            emitter.send(httpresponse);
          }
          //If the player lost
          else if(currentGame.gameOver()){
            httpresponse.printHeader("Set-Cookie", "SESSID=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
            httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
            if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
              httpresponse.printHeader("Content-Encoding", "gzip");
            httpresponse.printBody(htmlGenerator.getEndPage("You lost!"));
            removeCookie(cookie);
          gameRank.addGame(currentGame);
            cookie = null;
            emitter.send(httpresponse);
          }
          //Page actualisation
          else{
            if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
              httpresponse.printHeader("Content-Encoding", "gzip");
            httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
            httpresponse.printBody(htmlGenerator.generateHtml("POST", currentGame, "main", gameRank));
            emitter.send(httpresponse);
          }
        }
        //Page actualisation
        else if(currentGame != null && query == null){
          if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
            httpresponse.printHeader("Content-Encoding", "gzip");
          httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
          httpresponse.printBody(htmlGenerator.generateHtml("POST", currentGame, "main", gameRank));
          emitter.send(httpresponse);
        }
        //New game creation
        else if(currentGame == null && query == null){
          cookie = newCookie();
          currentGame = getGame(cookie);
          if(httpreq.getHeader("Accept-Encoding") != null && httpreq.getHeader("Accept-Encoding").contains("gzip"))
            httpresponse.printHeader("Content-Encoding", "gzip");
          httpresponse.printHeader("Content-Type", "text/html; charset=utf-8");
          httpresponse.printBody(htmlGenerator.generateHtml("POST", currentGame, "main", gameRank));
          emitter.send(httpresponse);
        }
        else{
          throw new BattleshipException("400");
        }
      }
      //If the method is not implented
      else if(httpreq.getMethod().equals("PUT") || httpreq.getMethod().equals("DELETE") || httpreq.getMethod().equals("PATCH"))
        throw new BattleshipException("405");
      //If the method is unrecognized
      else
        throw new BattleshipException("501");

    }
    catch(BattleshipException e){
      emitter.send(e.getErrorMessage());
    }
    catch(SocketException e){
			e.printStackTrace();
		}
		catch(SocketTimeoutException e){
      e.printStackTrace();
    }
		catch(IOException e){
      e.printStackTrace();
    }
    finally{
      //Close the socket and the server.
      try{
        sock.close();
      }
      catch(IOException e){
        e.printStackTrace();
      }
    }
  }

  //Method that creates a nex cookie and associates it to a new game on the game list
  private HttpCookie newCookie(){
    HttpCookie cookie = null;
    Game currentGame = null;
    synchronized(listOfGames){
      cookie = new HttpCookie("SESSID", Integer.toString(listOfGames.size()));
      // We set the timelife of the cookie
      cookie.setMaxAge(600);
      listOfGames.add(new Game(cookie));
      listOfCookies.add(cookie);
    }
    return cookie;

  }

  //Given a cookie, this method will return the game in the list of game associated
  //with this cookie (if it exists).
  private Game getGame(HttpCookie cookie){
    Game currentGame;
    for(int i = 0; i < listOfGames.size(); i++){
      if(listOfGames.get(i).getCookie().getValue().equals(cookie.getValue())){
        currentGame = listOfGames.get(i);
        return currentGame;
      }
    }
    return null;
  }

  //Method that checks if the query matches the battleship game format
  private boolean queryCheck(ArrayList<String[]> query){
    return query.size() == 2 && query.get(0)[0].equals("x") && query.get(1)[0].equals("y");
  }

  //MAthod that reviews the whole list of cookie to remove expired ones and their associated game
  private void purgeCookie(){
    int i;

    for(i = 0; i < listOfCookies.size(); i++){
      if(listOfCookies.get(i).hasExpired()){
        listOfGames.remove(getGame(listOfCookies.get(i)));
        listOfCookies.remove(i);
      }
    }
  }

  //Method that delete a cookie from the list
  private void removeCookie(HttpCookie cookie){
    int i;
    if(cookie != null){
      synchronized(listOfCookies){
        for(i = 0; i < listOfCookies.size(); i++){
          if(listOfCookies.get(i).getValue() == cookie.getValue()){
            listOfCookies.get(i).setMaxAge(0);}
        }
      }
    }
  }

}
