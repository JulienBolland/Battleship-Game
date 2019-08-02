import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.*;

// This class converts the images used in this project in base64,
// which allows us to send them in a HTTP data field.
class BattleshipImages{
  private static String[] imagesBase64 = null;

  //Constructor
  public BattleshipImages(){
    //Creating the base64 conversion for the images in the
    //constructor allows us to do this operation only once.
    imagesBase64 = new String[4];
    imagesBase64[0] = toBase64("images/water.png");
    imagesBase64[1] = toBase64("images/bomb.png");
    imagesBase64[2] = toBase64("images/boom.jpg");
    imagesBase64[3] = toBase64("images/plouf.png");
  }

  //Converts the images located in 'pathToImage' into a string
  //(i.e. the base64 form).
  private static String toBase64(String pathToImage){
    try{
      //creating new BufferedImage
      BufferedImage im = ImageIO.read(new File(pathToImage));
      //creating the output stream
      final ByteArrayOutputStream os = new ByteArrayOutputStream();
      //retrieving the format of the picture (png or jpg)
      String formatOfImage = pathToImage.split("\\.")[1];
      ImageIO.write(im, formatOfImage, os);
      //encoding the buffered image to the base64 form
      return Base64.getEncoder().encodeToString(os.toByteArray());
    }
    catch(IllegalArgumentException e){
      System.err.println(e);
    }
    catch(IOException e){
      System.err.println(e);
    }
    return null;
  }

  //This method allows the worker to display the needed picture
  //on the game set, by giving its name.
  public String getImage(String name){
    if(name == "water")
      return imagesBase64[0];
    if(name == "bomb")
      return imagesBase64[1];
    if(name == "boom")
      return imagesBase64[2];
    if(name == "plouf")
      return imagesBase64[3];
    return null;
  }
}
