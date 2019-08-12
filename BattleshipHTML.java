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
import java.util.zip.GZIPOutputStream;
import java.nio.charset.StandardCharsets;

// This class is used to write the HTML code that will be displayed
// on the browser.
class BattleshipHTML{
  private String htmlCode;
  private BattleshipImages im;

  // Constructor
  public BattleshipHTML(){
    htmlCode = "";
    im = new BattleshipImages();
  }

  // This method generates the HTML code of the corresponding page we want
  // to access. 'currGame' is a string that specifies if we are on play.html
  // (when it equals "main") or on halloffame.html (whent it eqauls "hof").
  public String generateHtml(String method, Game game, String currGame, Ranking gameRank)throws BattleshipException{
    htmlCode = "<html>" + generateHeadHtml();
    // if we are on play.html
    if(currGame.equals("main"))
      htmlCode += generatePlayHtml(method, game);
    // if we are on halloffame.html
    else if(currGame.equals("hof"))
      htmlCode += generateHofHtml(gameRank);
    // if the requested url is uncorrect
    else
      throw new BattleshipException("400");

    htmlCode += "</html>";
    return htmlCode;
  }

  // This method generates the head of our html code and defines
  // the css file we use.
  private String generateHeadHtml(){
    String head = "<head>\n";
    head = head.concat("<meta charset=\"utf-8\" http-equiv=\"content-type\" content=\"text/html\"/> <title>Battleship Game</title><style>"+ getFile("style.css") +"</style> \n");
    return head.concat("</head>\n");
  }

  // This method generates the body of the play.html page.
  private String generatePlayHtml(String method, Game game){
    String body = "\n<body background=\"\">\n";
    int i = 0, j;
    //The title of the page is appended
    body = body.concat("<h1 class=\"h1\"> BATTLESHIP </h1>\n");
    //The grid in which the tiles will be displayed
    body = body.concat("<div class=\"focus\"> <table id=\"gameTable\"> <div>\n");
    body = body.concat("<tr><td class=\"td\"></td>\n");
    for(i =0; i < 10; i++)
      body = body.concat("<td class=\"td\">" + Integer.toString(i) + "</td>\n");
    body = body.concat("</tr>\n");

    for(i = 0; i < 10; i++){
      body = body.concat("<tr> <td class=\"td\">" + String.valueOf((char) (i + 65)) + "</td>\n");
      for(j = 0; j < 10; j++)
        body = body.concat("<td class=\"td\" id=\"" + Integer.toString(i) + Integer.toString(j) + "\"> <img src=\"data:image/png;base64, " + getImage(i, j, game) + "\" width=\"100%\"> </td>\n");
      body = body.concat("</tr>\n");
    }
    body = body.concat("</div> </table> </div>\n");
    body = body.concat(generateScript(method));
    // The button to access halloffame.html
    body = body.concat("<center> <form action=\"/halloffame.html\" method=\"GET\"><button type=\"inputButton\"> Hall Of Fame </button></form>\n");
    return body.concat("</body>\n");
  }

  // This method generates the body of the halloffame.html page.
  private String generateHofHtml(Ranking rank){
    String body = "\n<body background=\"\">\n";
    body = body.concat("<h1 class=\"h1\"> BATTLESHIP </h1>\n");
    // The table in which will be displayed the ranking of the 10 best players
    body = body.concat("<div class=\"focus\">");
    body = body.concat("<table style=\"width:100%\"><tr><th> </th><th>Cookie Name</th><th>Score</th></tr>");
    for(int i = 0; i < 10; i++){
      if(i < rank.size())
        body = body.concat("<tr><td>"+ (i+1) +".</td><td>"+ rank.getCookie(i).getValue() +"</td><td>"+ rank.getScore(i) +"</td></tr>");
      else
        body = body.concat("<tr><td>"+ (i+1) +".</td><td>\\</td><td>\\</td></tr>");
    }
    body = body.concat("</table></div>");
    body = body.concat("<center> <form action=\"/play.html\" method=\"GET\"><button type=\"inputButton\"> Return to the game </button></form> </center>\n");
    return body;
  }

  // This methods allows us to put on the html code a string
  // corresponding to an image in base64, previously done by
  // BattleshipImages.
  private String getImage(int j, int i, Game game){
    if(game == null)
      return im.getImage("water");
    // Different images corresponding to a tile if it is touched or not.
    String temptype = game.getBattlefield().getShip(i, j).getType();
    if(temptype == "water"){
      if(game.getBattlefield().getShip(i, j).isTouched())
        return im.getImage("plouf");
      else
        return im.getImage("water");
    }
    else{
      if(game.getBattlefield().getShip(i, j).isTouched())
        return im.getImage("boom");
      else
        return im.getImage("water");
    }
  }

  // This method appends on the html code the script that will be used
  // to interact with the game set.
  private String generateScript(String method){
    BattleshipImages tempo = new BattleshipImages();
    // For javascript
    String temp = "<script>\n var plouf =\"" + tempo.getImage("plouf")+ "\"; var boom =\"" + tempo.getImage("boom") + "\"; var water =\"" + tempo.getImage("water")+ "\";\n\n\n" + getFile("actions.js") + "\n</script>\n";
    // If javascript is desactivated
    temp = temp.concat("<noscript><form action=\"play.html\" method=\"POST\"> <center>Column:<select name=\"x\">\n");
    int i;
    for(i = 0; i < 10; i++)
      temp = temp.concat("<option value=\"" + Integer.toString(i)+ "\">" + Integer.toString(i) + "</option>\n");
    temp = temp.concat("</select> Line: <select name=\"y\">\n");
    for(i = 0; i < 10; i++)
      temp = temp.concat("<option value=\"" + Integer.toString(i)+ "\">" + String.valueOf((char) (i + 65)) + "</option>\n");
    return temp.concat("</select> </center> <center><input type=\"submit\" value=\"Shoot\"></input></center></form></noscript>\n");

  }

  // This method writes the code of an HTML page that display a final message
  // when the game ends.
  public String getEndPage(String conclusion){
    return "<!DOCTYPE/ html> <html>"+ generateHeadHtml() + "<body> <h1>"+ conclusion + "</h1> </body> </html>";
  }

  // This method allows us to read a file and to write it as a string.
  private String getFile(String path){
    String filecontent= "";
    try{
      File file = new File(path);
      char[] temp = new char[(int) file.length()-2];
      FileReader fr = new FileReader(file);
      fr.read(temp);
      filecontent = new String(temp);
    }
    catch(IOException e){
      System.err.print(e.getMessage());
    }
    return filecontent;
  }
}
