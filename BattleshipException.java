/************************************************
* Introduction to computer networking : Project 2
*
* Authors : Julien Bolland : s161622 @ULiège
*           Thomas Mazur : s162939 @ULiège
*
* All rights reserved.
************************************************/

import java.lang.*;

// This class implements a BattleshipException, which occurs
// when we have to throw HTTP error codes
@SuppressWarnings("serial")
public class BattleshipException extends Exception{

  private String error;

  // Simple constructor
  public BattleshipException(){
    super();
  }

  // String constructor
  public BattleshipException(String s){
    super(s);
    error = s;
  }

  // Returns an HttpHandler object that will contain the error
  // message to display on the browser.
  public HttpHandler getErrorMessage(){
    HttpHandler resp = new HttpHandler();
    switch(error){
      // Error 400 Bad request
      // i.e. the client made a request that can't be treated because
      // of an error in this request.
      case "400" :
        resp.printStatus(400);
        break;
      // Error 404 Not found
      // i.e. the server could not find the requested page on the server
      case "404" :
        resp.printStatus(404);
        break;
      // Error 405 Method not allowed
      // i.e. the client made a request with a wrong method.
      case "405" :
        resp.printStatus(405);
        break;
      // Error 411 Length required
      // i.e. the client did not specify the length of the body
      case "411" :
        resp.printStatus(411);
        break;
      // Error 501 Not implemented
      // i.e. the method used is not implemented on the server
      case "501" :
        resp.printStatus(501);
        break;
      // Error 505 Version not supported
      // i.e. the server cannot support the HTTP version specified in the request.
      case "505" :
        resp.printStatus(505);
        break;
    }
    return resp;
  }

}
