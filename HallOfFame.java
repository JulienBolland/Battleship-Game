/************************************************
* Introduction to computer networking : Project 2
*
* Authors : Julien Bolland : s161622 @ULiège
*           Thomas Mazur : s162939 @ULiège
*
* All rights reserved.
************************************************/

import java.net.*;

// This class implements a HallOfFame object, which corresponds
// to a place in the ranking of the 10 best players of Battleship
public class HallOfFame implements Comparable<HallOfFame>{

  private HttpCookie cookie;
  private int score;

  // Constructor
  public HallOfFame(Game currGame){
    cookie = currGame.getCookie();
    score = currGame.getNbAttempt();
  }

  // Give the score of the object
  public int getScore(){
    return score;
  }

  // Give the cookie of the object
  public HttpCookie getCookie(){
    return cookie;
  }

  // This method allows us to compare two HallOfFame objects, by comparing
  // their scores.
  // ARGUMENTS : hof => a HallOfFame object we want to compare with
  // RETURNS : an integer, taking several values :
  //          - if scores are equals, the current object should be placed
  //            just below the compared object (which already exists)
  //          - if the returned number if positive, then the current object
  //            should be placed below the compared object
  //          - if it is negative, then the object is placed above the compared
  //            one.
  @Override
  public int compareTo(HallOfFame hof){
    return (this.score - hof.getScore());
  }

}
