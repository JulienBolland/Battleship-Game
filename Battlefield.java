/************************************************
* Introduction to computer networking : Project 2
*
* Authors : Julien Bolland : s161622 @ULiège
*           Thomas Mazur : s162939 @ULiège
*
* All rights reserved.
************************************************/

import java.util.*;

/*
* This class implements the battlefield of our game.
*/
public class Battlefield {

  private int nbTries = 0;
  private Battleship[][] battlefield;
  private String type[] = {"water", "carrier", "battleship", "submarine",
                          "cruiser", "destroyer"};
  private int ships = 17;

  // Constructor
  public Battlefield(boolean isNewGame){
    battlefield = new Battleship[10][10];

    for(int i = 0; i < 10; i++){
      for(int j = 0; j < 10; j++){
        battlefield[i][j] = new Battleship(type[0]);
      }
    }

    if(isNewGame){
      for(int i = 0; i < 5; i++)
        while(!posAcceptation(battlefield, type[i+1]));
    }
  }

  // This method creates the battlefield by placing the ships on the map
  // randomly.
  // ARGUMENTS : bf => 2D array of Battleships
  //             type => the type of ship we want to add
  // RETURNS : false if the placement is not correct
  //           true if the ship has been correctly added
  private static boolean posAcceptation(Battleship[][] bf, String type){

    Random pos = new Random();
    int x = pos.nextInt(10);
    int y = pos.nextInt(10);
    int dir = pos.nextInt(2);
    int shipLength = bf[x][y].getLength(type);
    if(dir == 0){ //vertically
      y -= shipLength - 1;
    }
    else{ //horizontally
      x -= shipLength - 1;
    }
    if(y < 0 || x < 0)
      return false;

    if(dir == 0){ //Vertically
      int i = x;
      while((i < shipLength + x) && (i < 10)){
        if(bf[i][y].getType() != "water")
          return false;
        i++;
      }
      if(i < shipLength + x || i == 10)
        return false;
      for(i = x; i < shipLength + x; i++)
        bf[i][y] = new Battleship(type);

      return true;
    }
    else{ //Horizontally
      int i = y;
      while((i < shipLength + y) && (i < 10)){
        if(bf[x][i].getType() != "water")
          return false;
        i++;
      }
      if(i == 10 || i < shipLength + y)
        return false;
      for(i = y; i < shipLength + y; i++)
        bf[x][i] = new Battleship(type);

      return true;
    }
  }

  // This method returns a ship located in [x,y].
  // ARGUMENTS : x => horizontal coordinate
  //             y => vertical coordinate
  // RETURNS : the wanted ship.
  public Battleship getShip(int x, int y){
    return battlefield[x][y];
  }

  // This method indicates that a ship has been touched.
  // ARGUMENTS : x => horizontal coordinate
  //             y => vertical coordinate
  // RETURNS : void
  public void isTouched(int x, int y){

    if(battlefield[x][y].isTouched() != true){
      battlefield[x][y].setTouched();
      if(battlefield[x][y].getType() != "water")
        ships--;
    }
  }

  public void display(){
    int i, j;
    for(i = 0; i < 10; i++){
      for(j = 0; j < 10; j++){
        if(battlefield[j][i].getType().equals("water"))
          System.out.print(" o ");
        else
          System.out.print(" x ");
      }
        System.out.println("");
    }
  }

}
