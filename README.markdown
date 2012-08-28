## Join-In
http://www.helmholtz-muenchen.de/en/join-in/home

#The open source device library for interfacing the Kinect.

http://75.98.78.94/default.aspx

#Node.js and all its trimmings.

http://nodejs.org/

#We used Three.js for rendering the enviornment.

https://github.com/mrdoob/three.js/

# MongoDB for the remote database.

http://www.mongodb.org/

Dev Branch, May not be stable.

#How to use

## Remote

On the server machine you need to have node.js and mongoDB installed. Open the port needed to run Nodejs. In this version I used port 7541.
Before running the server.js you must run the mongo server and use the db provided called 'test'. Once the Mongo server is running you may start the Game server ( server.js ).

1) Run MongoD
2) Run server.js


## Client side

Locally, before a user connects to the game server, they must run the sandra server. This will store the data sent from Sandra and will be accessed by the client javascript.
You may then run the Sandra batch file to start using sandra. 

Finally you can connect to the game server and begin the game.

1) Run sandra.js
2) Run sandra.bat
3) Connect to the game server's ip:port

Note:

The most stable browser is Google's Chrome, latest version. Firefox hasnt been used for development so browser specific bugs aren't ironed out yet. 

