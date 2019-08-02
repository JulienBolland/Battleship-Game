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


class BattleshipHTML{
  private String htmlCode;

  public BattleshipHTML(){
    htmlCode = "";
  }

  public String generateHtml(String method, Game game){
      htmlCode = "<html>" + generateHeadHtml() + generateBodyHtml(method, game) + "</html>";
      return htmlCode;
  }

  private String generateHeadHtml(){
    String head = "<head>\n";
    BattleshipImages im = new BattleshipImages();

    head = head.concat("<meta charset=\"utf-8\" http-equiv=\"content-type\" content=\"text/html\"/> <title>Battleship Game</title><style>"+ getFile("style.css") +"</style> \n");
    head = head.concat("<link rel=\"icon\" type=\"image/png\" href=\"http://example.com/myicon.png\">\n");
    return head.concat("</head>\n");
  }

  private String generateBodyHtml(String method, Game game){
    String body = "\n<body background=\"\">\n";
    int i = 0, j;
    //The title of the page is appended
    body = body.concat("<h1 class=\"h1\"> BATTLESHIP </h1>\n");
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
    body = body.concat("<center> <form action=\"/halloffame.html\" method=\"GET\"><button type=\"inputButton\" value=\"Hall Of Fame\"/> </form> </center>\n");
    body = body.concat(generateScript(method));

    return body.concat("</body>\n");
  }

  private String getImage(int j, int i, Game game){
<<<<<<< HEAD
    return "images/water.png";
=======
    BattleshipImages temp = new BattleshipImages();

    if(game == null)
      return temp.getImage("water");

    String temptype = game.getBattlefield().getShip(i, j).getType();
    if(temptype == "water"){
      if(game.getBattlefield().getShip(i, j).isTouched())
        return temp.getImage("plouf");
      else
        return temp.getImage("water");
    }
    else{
      if(game.getBattlefield().getShip(i, j).isTouched())
        return temp.getImage("boom");
      else
        return temp.getImage("water");
    }
>>>>>>> 2d246c1b7765baab738ab8d9edacbf8ea35ffc4b
  }

  private String generateScript(String method){
    BattleshipImages tempo = new BattleshipImages();
    if(method.equals("GET")){
      return "<script>\n var plouf =\"" + tempo.getImage("plouf")+ "\"; var boom =\"" + tempo.getImage("boom") + "\"; var water =\"" + tempo.getImage("water")+ "\";\n\n\n" + getFile("actions.js") + "\n</script>\n";
    }

    if(method.equals("POST")){
      String temp = "<noscript><form action=\"play.html\" method=\"POST\"> <center>Column:<select name=\"x\">\n";
      int i;
      for(i = 0; i < 10; i++)
        temp = temp.concat("<option value=\"" + Integer.toString(i)+ "\">" + Integer.toString(i) + "</option>\n");
      temp = temp.concat("</select> Line: <select name=\"y\">\n");
      for(i = 0; i < 10; i++)
        temp = temp.concat("<option value=\"" + Integer.toString(i)+ "\">" + String.valueOf((char) (i + 65)) + "</option>\n");
      return temp.concat("</select> </center> <center><input type=\"submit\" value=\"Shoot\"></input></center></form></noscript>\n");
    }
    return null;
  }


  public String getEndPage(String conclusion){
    return "<!DOCTYPE/ html> <html>"+ generateHeadHtml() + "<body> <h1>"+ conclusion + "</h1> </body> </html>";
  }

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
