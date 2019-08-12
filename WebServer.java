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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WebServer{
  private static final int DEFAULT_MAX_THREAD = 10;
  private static ExecutorService pool;
  private static ArrayList<Game> listOfGames = new ArrayList<Game>();
  private static Ranking rank = new Ranking(listOfGames);

  public static void main(String args[]){

    try{
      //Check whether the user gave the correct amount of argument
      int maxThread = DEFAULT_MAX_THREAD;
      if(args.length == 1)
        maxThread = Integer.parseInt(args[0]);
      else if(args.length == 0);
      else
        throw new IllegalArgumentException("Too many argument entered..");

      pool = Executors.newFixedThreadPool(maxThread);
      ServerSocket ss = new ServerSocket(8018);

      //Server waits for a Connection
      while(true){
        pool.execute(new Worker(ss.accept(), listOfGames, rank));
      }

    }
    catch(IllegalArgumentException e){
      e.printStackTrace();
    }
    catch(IOException ex){
      pool.shutdown();
    }
  }
}
