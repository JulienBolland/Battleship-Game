/************************************************
* Introduction to computer networking : Project 2
*
* Authors : Julien Bolland : s161622 @ULiège
*           Thomas Mazur : s162939 @ULiège
*
* All rights reserved.
************************************************/
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

function funx(id){
  return function t(){tileReact(id);};
}

function tileReact(tile){
  if(tile != null){
    var col, line;
    var id;
    if(tile < 10)
      id = "0"+tile.toString(10);
    else
      id = tile.toString(10);
    col = tile%10;
    line = (tile - col)/10;
    var xhttp = new XMLHttpRequest();
    xhttp.open('GET', '/play.html?x='+col.toString(10)+'&y='+line.toString(10),true);
    xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    xhttp.onreadystatechange = function(){
  		if (xhttp.readyState === XMLHttpRequest.DONE && xhttp.status === 200) {
  			var result = JSON.parse(xhttp.responseText);
        var trua = "true";
        var falsa = "false";
        if(trua.localeCompare(result.hit) == 0){
          document.getElementById(id).innerHTML = "<img src=\"data:image/png;base64, "+ boom +"\" width=\"100%\" />";
        }
        if(falsa.localeCompare(result.hit) == 0){
          document.getElementById(id).innerHTML = "<img src=\"data:image/png;base64, "+ plouf + "\" width=\"100%\" />";
        }
  			if(trua.localeCompare(result.win) == 0){
  				alert('You won');
          document.cookie = xhttp.getResponseHeader("Set-Cookie");
  			}
  			if(trua.localeCompare(result.gameOver) == 0){
  				alert('Game over');
          document.cookie = xhttp.getResponseHeader("Set-Cookie");
  			}
  		}
  	};
    if(!document.getElementById(id).innerHTML.includes(plouf) &&  !document.getElementById(id).innerHTML.includes(boom))
      xhttp.send();
  }
}
