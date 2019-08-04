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
import java.text.SimpleDateFormat;
import java.util.zip.GZIPOutputStream;


/*HttpHandler is a class that is meant to parse an http request or create one.
Once the class is instanciated, it either creates a dummy message, that is meant
to be modified via the following methods:
                       -

, or parses a string and collect all the information it contains
which can be accessed via its methods. These are the followings:
                       -HttpReader(InputStream): reads and parse a request
                       -getHeader(String): returns the string contained by a header
                       -getURL: returns the URL requested.
                       -getBody: returns the body of the requested.
                       -getMethod: returns the method of the request.*/
class HttpHandler{
  private URL _Url;
  private String[] method;
  private String body;
  private ArrayList<String[]> headers;

  //state is "false" if the Httpmessage is a request and "true" if it is a response.
  private  boolean state;


  public HttpHandler(String msg){

    headers = new ArrayList<String[]>();
    ParseHttp(msg);
    state = false;
    try{
        _Url = new URL(new URL("http://localhost:8018"), method[1].split(" ")[0]);
    }
    catch(MalformedURLException e){
      System.err.print("Error during the creation of the URL.");
    }
  }

  public HttpHandler(){
    headers = new ArrayList<String[]>(3);
    try{
      _Url = new URL(new URL("http://localhost:8018"), "/play.html");
    }
    catch(MalformedURLException e){
      System.err.print("Error during the creation of the URL.");
    }

    method = new String[] {"HTTP/1.1", "200 OK"};
    body = "";
    SimpleDateFormat dateForm = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
    Date date = new Date();
    headers.add(new String[] {"Date", "Blank"});
    headers.add(new String[] {"Connection", "close"});
    headers.add(new String[] {"Content-Type", "text/html; charset=utf-8"});

    state = true;
  }

  /*Displays the http request.*/
  public void displayHttp(){

    SimpleDateFormat dateForm = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
    Date date = new Date();
    headers.get(0)[1] = dateForm.format(date);

    System.out.println(method[0] + " " + method[1]);
    int i;
    for(i = 0; i < headers.size(); i++)
      System.out.println(headers.get(i)[0] + ": " + headers.get(i)[1]);
    System.out.println("\n" + body);
  }

  /*if [String] request is a valid HTTP REQUEST, ParseHttp(request) will treat
  the request retrieving the method, the header and the body in order to be
  able to access them separately using the adequate methods.*/
  private void ParseHttp(String request){
    try{
      //The received message is first split into head and body at "\r\n\r\n"
      //tag and then the head is split using "\r\n" tags
      String[] headbody = request.split("\r\n\r\n");
      String[] split_request = headbody[0].split("\r\n");

      //-----------First we extract the request line---------------
      method = new String[2];
      method[0] = split_request[0].split(" ")[0];
      method[1] = split_request[0].split(method[0]+" ")[1];

      //-----------Then we extract the headers line--------------------
      int i;
      for(i = 1; i < split_request.length; i++){
        String temp = "";
        if(split_request[i].split(": ").length > 1)
          temp = split_request[i].split(": ")[1];
        printHeader(split_request[i].split(":")[0], temp);
      }

      //-----------Finally the body is extracted------------------
      if(headbody.length == 2)
        body = headbody[1];
      }
      catch(Exception e){
        System.err.println("Error : " + e.getMessage());
      }
  }

  /*If header_Name is a header name that is contained by the request,
  getHeader(header_Name) returns the string contained by the matching header line
  otherwise, it returns the NULL pointer.*/
  public String getHeader(String header_Name){
    int i;
    for(i = 0; i < headers.size(); i++){
      if(headers.get(i)[0].equals(header_Name))
        return headers.get(i)[1];
    }
    return null;
  }

  //getURL returns the URL requested by the message.
  public URL getURL(){
    return _Url;
  }
  //getMethod returns the method of the HTTP request.
  public String getMethod(){
    return method[0];
  }
  //getBody returns the entire untreated body of the request.
  public String getBody(){
    return body;
  }

  public ArrayList<String[]> getQuery(){

    ArrayList<String[]> parsedquery = new ArrayList<String[]>();
    String query = null;
    String[] temp;

    if(method[0].equals("GET"))
      query = _Url.getQuery();
    if(method[0].equals("POST"))
      query = body;
    if(query == null)
      return null;
    else if(!query.contains("=")){
       parsedquery.add(new String[] {"query=", query});
      return parsedquery;
    }

    int i;
    for(i = 0; i < query.split("&").length; i++){
      temp = new String[] {query.split("&")[i].split("=")[0], query.split("&")[i].split("=")[1]};
      parsedquery.add(temp);
    }
    return parsedquery;
  }

  /*printHeader, creates a new header or modifies it if it already exists*/
  public void printHeader(String header_Name, String content){
    int i;
    for(i = 0; i < headers.size(); i++){
      if(headers.get(i)[0].equals(header_Name)){
        headers.get(i)[1] = content;
        i = headers.size() + 1;
      }
    }
    if(i == headers.size())
      headers.add(new String[] {header_Name, content});
  }
  /*removeHeader removes a specified header*/

  public void removeHeader(String header_Name){
    int i;
    for(i = 0; i < headers.size(); i++){
      if(headers.get(i)[0].equals(header_Name)){
        headers.remove(i);
        i = headers.size() + 1;
      }
    }
    if(i == headers.size())
      System.out.println("No such header found. Proceed..");
  }

  /*printBody, modifies the body of the Http request*/
  public void printBody(String content){
    body = content;
    if(method[0].contains("HTTP/1.0") || method[1].contains("HTTP/1.0"))
      printHeader("Content-Length", Integer.toString(content.length()));
  }

  /*printMethod modifies the method of the Httprequest*/
  public void printMethod(String content){
    method[0] = content;
    if(state)
      method[1] = "/ HTTP/1.1";
    state = false;
  }

  /*printURL modifies the _URL of the Http request*/
  public void printUrl(String content){
    try{
      _Url = new URL(new URL("http://localhost:8018"), content);
      method[1] = "/" + content + " HTTP/1.1";
    }
    catch(MalformedURLException e){
      System.err.print("Error during the creation of the URL.");
    }
    if(state)
      method[0] = "";
    state = false;
  }
  /*printStatus modifies the status of the http response*/
  public void printStatus(int status){
    switch(status){
      case 200:
        method = new String[] {"HTTP/1.1", "200 OK"};
        break;
      case 303:
        method = new String[] {"HTTP/1.1", "303 See Other"};
        break;
      case 400:
        method = new String[] {"HTTP/1.1", "400 Bad Request"};
        printBody("<html><body><h1>Bad Request</h1><h2>The server cannot or will not process the request due to an apparent client error.</h2></body></html>");
        break;
      case 404:
        method = new String[] {"HTTP/1.1", "404 Not Found"};
        printBody("<html><body><h1>Not Found</h1></body><h2>The requested URL was not found on this server.</h2></html>");
        break;
      case 405:
        method = new String[] {"HTTP/1.1", "405 Method Not Allowed"};
        printBody("<html><body><h1>Method Not Allowed</h1><h2>The method used is not supported for the requested resource.</h2></body></html>");
        break;
      case 411:
        method = new String[] {"HTTP/1.1", "411 Length Required"};
        printBody("<html><body><h1>Length Required</h1><h2>The request did not specify the length of its content.</h2></body></html>");
        break;
      case 501:
        method = new String[] {"HTTP/1.1", "501 Not Implemented"};
        printBody("<html><body><h1>Not Implemented</h1><h2>The server does not recognize the request method.</h2></body></html>");
        break;
      case 505:
        method = new String[] {"HTTP/1.1", "505 HTTP Version Not Supported"};
        printBody("<html><body><h1>HTTP Version Not Supported</h1><h2>The server does not support the HTTP protocol version used in the request.</h2></body></html>");
        break;
    }
    state = true;
  }
  /*getHttp returns the entire response in the format of a two dimensions
  array of bytes where */
  public byte[][] getHttp(){

    String Http = new String();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[][] httpmsg = new byte[2][];

    //The date is written into the "Date" header
    SimpleDateFormat dateForm = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
    Date date = new Date();
    printHeader("Date", dateForm.format(date));

    //Body is gzip encoded if and only if the client accepts it-----------------
    try{
      if(getHeader("Content-Encoding") != null && getHeader("Content-Encoding").contains("gzip"))
        encodeGzip(baos);
      else
        baos.write(body.getBytes());
    }
    catch(IOException e){
      System.err.println(e.getMessage());
    }
    httpmsg[1] = baos.toByteArray();
    //--------------------------------------------------------------------------

    //The content length is updated. If it is HTTP/1.0 else we'll chunk encode.
    if(method[1].contains("HTTP/1.0") || method[0].contains("HTTP/1.0"))
      printHeader("Content-Length", Integer.toString(httpmsg[1].length));
    else if(method[1].contains("HTTP/1.1") || method[0].contains("HTTP/1.1")){
      if(getHeader("Content-Length") != null){removeHeader("Content-Length");}
      if(getHeader("No Chunk") == null){printHeader("Transfer-Encoding", "chunked");}
      if(getHeader("No Chunk") != null){removeHeader("No Chunk");}
    }

    //Headers are prepared.
    Http = Http + method[0] + " " + method[1] + "\r\n";
    int i;
    for(i = 0; i < headers.size(); i++)
      Http = Http + headers.get(i)[0] + ": " + headers.get(i)[1] + "\r\n";
    Http = Http + "\r\n";

    httpmsg[0] = Http.getBytes();
    return httpmsg;
  }

  //encodeGzip compresses the body using GZIPOutputStream
  private void encodeGzip(ByteArrayOutputStream baos){
    if(body == null)
      return;
    try{
      GZIPOutputStream gzipos = new GZIPOutputStream(baos);
      gzipos.write(body.getBytes(), 0, body.length());
      gzipos.close();
    }
    catch(IOException e){
      System.err.println(e.getMessage());
    }
  }


}
