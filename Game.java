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

	// Returns the tile with the coordinates (x,y)
  public String getTile(int x, int y){
    Battleship ship = secretBF.getShip(x, y);
    return ship.getType();
  }

	// Returns the entire battlefield.
  public Battlefield getBattlefield(){
    return secretBF;
  }

	// Returns the cookie used for this game
	public HttpCookie getCookie(){
		return cookie;
	}

	// Returns a boolean that states if we have lost
	public boolean gameOver(){
		return nbAttempt >= 70;
	}

	// Returns a boolean that states if we have won
	public boolean isWin(){
		return win;
	}

	// Returns a boolean that states if the game is finished
	// (lost or won)
	public boolean isFinished(){
		return this.isWin() || this.gameOver();
	}

	// Returns the number of attempts during this game
	public int getNbAttempt(){
		return nbAttempt;
	}

	// Allows us to display the game (for debug only)
	public void justDisplay(){
		secretBF.display();
	}

}
