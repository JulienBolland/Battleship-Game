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
import java.io.ByteArrayOutputStream;

class Test{
  public static void main(String args[]) throws Exception{

    HttpHandler httpreq = new HttpHandler();
    
    HttpHandler httpres = new HttpHandler();


    httpreq.printStatus(200);
    httpreq.printBody("x=1&y=2&msg=Julien est un mec super cool.");
    httpreq.printHeader("Content-Type", "text/html; charset=utf-8");
    httpreq.removeHeader("Connection");

    System.out.println("\nRequête:\n" + new String(httpreq.getHttp()[0]) + new String(httpreq.getHttp()[1]));
    System.out.println("\nRéponse:\n" + new String(httpres.getHttp()[0]) + new String(httpres.getHttp()[1]));

  }


}
