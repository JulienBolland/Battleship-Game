/************************************************
* Introduction to computer networking : Project 2
*
* Authors : Julien Bolland : s161622 @ULiège
*           Thomas Mazur : s162939 @ULiège
*
* All rights reserved.
************************************************/

/*
* This class implements the ships of our game.
*/
public class Battleship{

  private String type; // The type of the ship
  private int length; // The length of the ship
  private int nbType; // This number identifies the type
  private boolean touched = false;

  // Constructor
  public Battleship(String type){
    this.type = type;
    this.length = this.getLength(type);
    this.nbType = getNb(type);

  }

  // RETURNS : private attributes 'touched'.
  public boolean isTouched(){
    return touched;
  }

  // This methods set 'touched' to true.
  public void setTouched(){
    this.touched = true;
  }

  // ARGUMENTS : type => the type of the ship
  // RETURNS : the length of the corresponding ship
  public int getLength(String type){
    switch(type){
      case "carrier":
        return 5;
      case "battleship":
        return 4;
      case "cruiser":
        return 3;
      case "submarine":
        return 3;
      case "destroyer":
        return 2;
      default:
        return 1;
    }
  }

  // RETURNS : the ID number of the ship.
  public int getNb(){
    return nbType;
  }

  // ARGUMENTS : type => the type of the ship
  // RETURNS : the corresponding ID number of the ship.
  public static int getNb(String type){
    switch(type){
      case "carrier":
        return 1;
      case "battleship":
        return 2;
      case "cruiser":
        return 3;
      case "submarine":
        return 4;
      case "destroyer":
        return 5;
      default:
        return 0;
    }
  }

  // RETURNS : the type of the ship.
  public String getType(){
    return type;
  }

  // ARGUMENTS : the ID number of the ship
  // RETURNS : the corresponding type.
  public static String getType(int nbType){
    switch(nbType){
      case 0:
        return "water";
      case 1:
        return "carrier";
      case 2:
        return "battleship";
      case 3:
        return "cruiser";
      case 4:
        return "submarine";
      case 5:
        return "destroyer";
    }
    return "water";
  }


}
