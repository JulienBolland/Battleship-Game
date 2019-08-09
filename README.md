# Battleship Game
This project is made as part of the course INFO0010-4 at the Uliège. The server uses the HTTP protocol to provide an interactive Battleship game to its clients. In association with Thomas Mazur (https://github.com/ThMazur).

# Run
To run the project, type on a terminal opened at the location of the files : 
```bash
javac -Xlint:all *.java
```
This will compile the java files into binary files. Then type : 
```bash
java WebServer [number of thread]
```
Where [number of thread] is the number of thread you want to open on the socket.
As the server is running, open your browser and type the URL : http://localhost:8018 (this version is made to play in local)

From there you can play at the Battleship Game!

NB: it is not a complete game, several option could be added in order to make the game experience more complete. This implementation has been done in order to comply with the assignment of the project. 
