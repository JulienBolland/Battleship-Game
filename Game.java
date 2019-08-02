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

public class Game{

	private Battlefield secretBF;
	private int nbAttempt = 0, ships = 17;
	private HttpCookie cookie;
	private boolean win = false;

  // Constructor
	public Game(HttpCookie cookie){
		this.cookie = cookie;
    secretBF = new Battlefield(true);
	}

  // This methods checks whether a ship has been touched or not,
  // and updates its state if it has.
  // ARGUMENTS : x => horizontal coordinate
  //             y => vertical coordinate
  // RETURNS : true if the tile is an unvisited tile
  //           false if the tile has already been visited
	public synchronized boolean checkAttempt(int x, int y){
    Battleship ship = secretBF.getShip(x, y);
    if(ship.isTouched()){
      return false;
    }
    nbAttempt++;
    ship.setTouched();
    if(ship.getType() != "water"){
			--ships;
    // If there are no more ships, the game is won
    	if(ships <= 0)
				win = true;
			return true;
		}
    return false;
	}

  public String getTile(int x, int y){
    Battleship ship = secretBF.getShip(x, y);
    return ship.getType();
  }

  public Battlefield getBattlefield(){
    return secretBF;
  }

	public HttpCookie getCookie(){
		return cookie;
	}

	public boolean gameOver(){
		return nbAttempt == 70;
	}

	public boolean isWin(){
		return win;
	}

	public int getNbAttempt(){
		return nbAttempt;
	}

	public void justDisplay(){
		secretBF.display();
	}

}
