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

  public static void main(String args[]){

    try{
      System.out.print("Openning server on port 8018...\n");
      int maxThread = DEFAULT_MAX_THREAD;
      if(args.length == 1)
        maxThread = Integer.parseInt(args[0]);
      else if(args.length == 0);
      else
        throw new IllegalArgumentException("Too much argument entered..");

      pool = Executors.newFixedThreadPool(maxThread);
      ServerSocket ss = new ServerSocket(8018);

      //Server waits for a Connection
      while(true){
        pool.execute(new Worker(ss.accept(), listOfGames));
        System.out.println("Worker created...");
      }

    }
    catch(IllegalArgumentException e){System.err.println("Enter a positive number of thread");}
    catch(IOException ex){pool.shutdown();}
  }
}
