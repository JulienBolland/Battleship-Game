/************************************************
* Introduction to computer networking : Project 2
*
* Authors : Julien Bolland : s161622 @ULiège
*           Thomas Mazur : s162939 @ULiège
*
* All rights reserved.
************************************************/
import java.util.*;
import java.net.*;

// This class implements the ranking of the game, containing the
// 10 best players on the server.
public class Ranking{

  // List containing the best players
  // We ensure that the size does not exceed 10.
  private ArrayList<HallOfFame> hof = new ArrayList<HallOfFame>();

  // Constructor
  public Ranking(){

  }

  // This method allows to add a game to the ranking.
  // If the game is not better than the games in the list,
  // than we do not add it. A sorting of the list takes place
  // in order to classify the players according to their scores.
  // ARGUMENTS : currGame => the game we want to add in the ranking
  public void addGame(Game currGame){
    HallOfFame newGame = new HallOfFame(currGame);
    // If the list of players if lower than 10, we add it automatically
    if(hof.size() < 10){
      hof.add(newGame);
      sort();
    }
    else{
      // If newGame deserves its place in the ranking
      if(checkIfBetter(newGame)){
        // we remove the last player in the ranking
        hof.remove(hof.size()-1);
        // and we add the newGame
        hof.add(newGame);
        sort();
      }
    }
  }

  // This function sorts the lst of players, according to their scores.
  private void sort(){
    int indexOfLastAdded = hof.size()-1;
    HallOfFame lastAdded = hof.get(indexOfLastAdded);
    // We stop when the game is the best game or if it is at the right place
    // in the current ranking
    while(indexOfLastAdded > 0 && lastAdded.compareTo(hof.get(indexOfLastAdded - 1)) < 0){
      swap(indexOfLastAdded, --indexOfLastAdded);
    }
  }

  // This method swaps two elements of the ranking list.
  // ARGUMENTS : i => the index of the first element
  //             j => the index of the second element
  private void swap(int i, int j){
    ArrayList<HallOfFame> newArray = new ArrayList<HallOfFame>();
    for(int k = 0; k < hof.size(); k++){
      if(k == i){
        newArray.add(hof.get(j));
      }
      else if(k == j){
        newArray.add(hof.get(i));
      }
      else{
        newArray.add(hof.get(k));
      }
    }
    hof = newArray;
  }

  // This methods checks whether a game deserves its place in the ranking.
  // ARGUMENTS : newGame => the game we want to add in the ranking
  // RETURNS : true if newGame deserves its place, false otherwise.
  private boolean checkIfBetter(HallOfFame newGame){
    return (newGame.compareTo(hof.get(hof.size()-1)) < 0) ? true : false;
  }

  // Gives the score of the player located at index i of the ranking list.
  public int getScore(int i){
    return hof.get(i).getScore();
  }

  // Gives the cookie of the player located at index i of the ranking list.
  public HttpCookie getCookie(int i){
    return hof.get(i).getCookie();
  }
}
