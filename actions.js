/************************************************
* Introduction to computer networking : Project 2
*
* Authors : Julien Bolland : s161622 @ULiège
*           Thomas Mazur : s162939 @ULiège
*
* All rights reserved.
************************************************/
//Every tile is enumerated to append an event listener containing the ajax script
for(var i = 0; i < 10; i++){
  for(var j = 0; j < 10; j++){
    var num =(i * 10) + j;
    var str;
    if(num < 10)
      str = "0" + num.toString(10);
    else
      str = num.toString(10);

    var fun = funx(Number(str));
    if(document.getElementById(str) != null)
      document.getElementById(str).addEventListener("click", fun);
  }
}

//Anonymous fonction allowing to remember id of the tile
function funx(id){
  return function t(){tileReact(id);};
}

//When the player clicks on a tile, this is the reaction
function tileReact(tile){
  if(tile != null){
    var col, line;
    var id;
    //First we extract the id
    if(tile < 10)
      id = "0"+tile.toString(10);
    else
      id = tile.toString(10);
    //Then we get the column/line index
    col = tile%10;
    line = (tile - col)/10;
    //A new request is created and set
    var xhttp = new XMLHttpRequest();
    xhttp.open('GET', '/play.html?x='+col.toString(10)+'&y='+line.toString(10),true);
    xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    //When a request is received, execute this
    xhttp.onreadystatechange = function(){
  		if (xhttp.readyState === XMLHttpRequest.DONE && xhttp.status === 200) {
        //Parse the json file
  			var result = JSON.parse(xhttp.responseText);
        //Then check its content and update the page according to it
        if("true".localeCompare(result.hit) == 0){
          document.getElementById(id).innerHTML = "<img src=\"data:image/png;base64, "+ boom +"\" width=\"100%\" />";
        }
        if("false".localeCompare(result.hit) == 0){
          document.getElementById(id).innerHTML = "<img src=\"data:image/png;base64, "+ plouf + "\" width=\"100%\" />";
        }
  			if("true".localeCompare(result.win) == 0){
  				alert('You won');
          document.cookie = xhttp.getResponseHeader("Set-Cookie");
  			}
  			if("true".localeCompare(result.gameOver) == 0){
  				alert('Game over');
          document.cookie = xhttp.getResponseHeader("Set-Cookie");
  			}
  		}
  	};
    //The request is set only if the tile isn't a boom nor a plouf
    //(i.e. it should be a water tile --> see report)
    if(!document.getElementById(id).innerHTML.includes(plouf) &&  !document.getElementById(id).innerHTML.includes(boom))
      xhttp.send();
  }
}
