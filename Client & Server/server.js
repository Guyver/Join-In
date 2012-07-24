// The port to listen for client connctions.
var clientPort = 7541;
// Http protocol 
var http = require('http');
// File serving
var fs = require('fs');
// Validates the existance of files.
var path = require('path');
// Sockets
var io = require('./lib/socket.io');

// 4 players per room. Each room has no knowledge of other rooms.
// Each room has their own game logic and win conditions.
var rooms = []; // Must be implemented to seperate players.
// User group final score.
var topScore = 0;
//var connected = [];
var clients = [];
//var g_id;
var imagesByTeam = [];
// Get mongoose and connect to the server.
var mongoose = require('mongoose').connect('mongodb://localhost/joinInDB');

var server = http.createServer( function ( request , response ) {
 
    console.log('request starting...'+ request.url);

    var filePath = '.' + request.url;

    if ( filePath == './' ){// Just the root, localtion of server.js. Will only enter here initally.
        filePath = './html/index.htm';// Serve html page.
	}

    var extname = path.extname( filePath );

    var contentType = 'text/html';

    switch ( extname ) {
        case '.js':// Serving some script.
            contentType = 'text/javascript';
			filePath = './script'+request.url;
			console.log( "Serving JS "+request.url ); 
            break;
        case '.css':// Serving some style
            contentType = 'text/css';
			filePath = '.'+request.url;
			console.log( "Serving CSS "+request.url ); 
            break;
		case '.png':// We're serving an image.
            contentType = 'image/png';
			filePath = '.'+request.url;
			console.log( "Serving PNG "+request.url ); 
            break;
		case '.jpg':// We're serving an image.
            contentType = 'image/jpg';
			filePath = '.'+request.url;
			console.log( "Serving JPG "+request.url ); 
            break;
		case '.dae':// We're serving an image.
            contentType = 'text/plain';
			filePath = '.'+request.url;
			console.log( "Serving DAE "+request.url ); 
            break;
    }
     
    path.exists( filePath, function(exists) {// Check to see if the file exists
     
        if (exists) {// Returned from the callback, checking to see if valid.
			// Read file from disk and trigger callback.
            fs.readFile( filePath, function(error, content) {
                if (error) {
					// If there's and error throw 500
                    response.writeHead(500);
                    response.end();
                }
                else {
					// Otherwise return the file.
                    response.writeHead(200, { 'Content-Type': contentType });
                    response.end(content, 'utf-8');
                }
            });
        }
        else {
			// Throw 404 if the file isn't there.
            response.writeHead(404);
            response.end();
        }
	});

});// End of Http create server.

var socket = io.listen( server ); 		// Socket IO server instance.
var users = [];							// List of connected players.
var userCount = 0;						// Number of users connected.
//var map = [];							// Container for the player data.
var rooms = [];							// Must be implemented to seperate players.
// User group final score.
var topScore = 0;
//var connected = [];
var clients=[];
//var g_id;
var imagesByTeam=[];
// Get mongoose and connect to the server.
var mongoose = require( 'mongoose' ).connect( 'mongodb://localhost/joinInDB' );

socket.sockets.on('connection', function (client) {
	//Store the client object in the clients array so that we can retreive it later to send messages to the browser given its IP 
	clients[client.handshake.address.address] = client;
	//This function connects to the database and stores in users[IP] the information related to the user with the given userKey. It also sends the message to make green the ready-bar if necessary.
	function registerUser(userKeyParam) {
		//Mongoose database connection
		var mongoose = require('mongoose/'),
			Schema = mongoose.Schema;
		mongoose.connect('mongodb://localhost/joinInDB');
		//Define the structure of the document
		var userSchema = new Schema({
			name: String,
			userKey: Number,
			score: Number,
			connectedImageUrl: String,
			disconnectedImageUrl: String,
			pos: String,
			kinect: String,
			meshName: String,
			team: Number
		});
		var usersModel = mongoose.model('users', userSchema);
		var nameAux;
		var scoreAux;
		var connectedImageUrlAux;
		var disconnectedImageUrlAux;
		var posAux;
		var kinectAux;
		var meshNameAux;
		var team;
		var clientIPAddress = client.handshake.address.address;
		//Get the user info related to the given userKey
		usersModel.findOne({
			userKey: userKeyParam
		}, function (err, doc) {
			//If there is no info related to the given userKey			
			if (doc == null) {
				//tell the browser to reload and ask again for the userKey
				clients[clientIPAddress].emit('noSuchUserKey');
			} else {
				//tell the browser to go to the psiPage
				clients[clientIPAddress].emit('goToPsiPage');
				nameAux = doc.name;
				scoreAux = doc.score;
				connectedImageUrlAux = doc.connectedImageUrl;
				disconnectedImageUrlAux = doc.disconnectedImageUrl;
				posAux = 0;
				kinectAux = null;
				meshNameAux = doc.meshName;
				team = doc.team;
				// Construct a map from the new player.
				var map = {
					"ip": clientIPAddress,
					"userKey": userKeyParam,
					"name": nameAux,
					"score": scoreAux,
					"connectedImageUrl": connectedImageUrlAux,
					"disconnectedImageUrl": disconnectedImageUrlAux,
					"pos": posAux,
					"kinect": kinectAux,
					"visible": true,
					"meshName": meshNameAux,
					"team": team
				};
				// Store me in the map format.
				users[clientIPAddress] = map;
				for (index in users) {
					//Make green the ready-bar if necessary
					socket.sockets.emit('updateNewUser', users[index]);
				}
			}
		});
	}
	//This function gets the connected users of the team of the user whose userKey is userKeyParam	
	client.on('giveMeConnectedUsers', function (userKeyParam) {
		var myTeam;
		usersModel.find({}, function (err, doc) {
			for (index in doc) {
				if (doc[index].userKey == userKeyParam) {
					myTeam = doc[index].team;
				}
			}
		});
		//Work with my team mates only
		var teamMates = [];
		for (var index = 1; index <= users.length; index++) {
			var adjustedIndex = index - 1;
			var aux = users[adjustedIndex].team;
			if (JSON.stringify(aux) == JSON.stringify(myTeam)) {
				teamMates.push(users[adjustedIndex]);
			}
		}
		var aux = {};
		for (index in teamMates) {
			aux[index] = teamMates[index];
		}
		client.emit('hereYouAreTheConnectedUsers', aux);
	});
	client.on('giveMeUserPictures', function (userKeyParam) {
		var Schema = mongoose.Schema;
		mongoose.connect('mongodb://localhost/joinInDB');
		var userSchema = new Schema({
			name: String,
			userKey: Number,
			score: Number,
			connectedImageUrl: String,
			disconnectedImageUrl: String,
			pos: String,
			kinect: String,
			meshName: String,
			team: Number
		});
		var usersModel = mongoose.model('users', userSchema);
		var myTeam;
		usersModel.find({}, function (err, doc) {
			for (index in doc) {
				if (doc[index].userKey == userKeyParam) {
					myTeam = doc[index].team;
				}
			}
			usersModel.find({}, function (err, usersInDB) {
				var imagesAux = [];
				var teamMates = [];
				//Work with my team mates only
				for (var index = 1; index <= usersInDB.length; index++) {
					var adjustedIndex = index - 1;
					var aux = usersInDB[adjustedIndex].team;
					if (JSON.stringify(aux) == JSON.stringify(myTeam)) {
						teamMates.push(usersInDB[adjustedIndex]);
					}
				}
				//if(imagesByTeam[myTeam]==undefined){
				//imagesByTeam[myTeam]=[];
				//}
				//if(imagesByTeam[myTeam].length==0){
				for (var index = 1; index <= teamMates.length; index++) {
					var adjustedIndex = index - 1;
					imagesAux.push({
						userKey: teamMates[adjustedIndex].userKey,
						image: teamMates[adjustedIndex].disconnectedImageUrl
					});
				}
				/*}
			else{
				imagesAux=imagesByTeam[myTeam];	
				for(index in teamMates){
					imagesAux[index].image=teamMates[index].disconnectedImageUrl;
				}		
			}*/
				var usersAux = {};
				for (index in teamMates) {
					for (index2 in imagesAux) {
						if (teamMates[index].userKey == imagesAux[index2].userKey) {
							imagesAux[index2].image = usersInDB[index2].connectedImageUrl;
						} else {
							imagesAux[index2].image = usersInDB[index2].disconnectedImageUrl;
						}
					}
					usersAux[index] = teamMates[index];
				}
				client.emit('sendingUserPictures', imagesAux, usersAux);
				imagesByTeam[myTeam] = imagesAux;
				registerUser(userKeyParam);
			});
		});
	});
	//				(1)
	// GET PLAYERS, SEND TO JUST ME.
	// ****** Only happens upon connection to the server. ******
	client.on('getPlayers', function () {
		var test = {};
		var count = 0;
		for (index in users) {
			count++;
			test[count] = users[index];
		}
		// Send the new client all the connected users.
		client.emit('heresPlayersFromServer', test);
	});
	client.on('registerMeInServerFirstPage', function (userKeyParam) {
		registerUser(userKeyParam);
	});
	// 				(2)
	// STORE ME AS A USER.
	//	******* Only happens once when the player sends a template for the server to fill in and store him here********
	client.on('registerMeInServer', function (data) {
		console.log("Register Me In Server was called on the server.");
		// Construct a map from the new player.
		var map = {
			"name": data.name,
			"pos": data.pos,
			"ip": client.handshake.address.address,
			"kinect": data.kinect,
			"meshName": data.mesh
		};
		// Store me in the map format.
		users[client.handshake.address.address] = map;
		// Return my profile to me.
		client.emit('registerSelf', {
			player: users[client.handshake.address.address]
		});
		// Tell previously connected users a player connected and pass his profile to them. Don't send to self.
		client.broadcast.emit('RegisterNewUser', {
			player: users[client.handshake.address.address],
			ip: client.handshake.address.address
		});
	});
	// 				(3)
	// UPDATE ME AND TELL EVERYONE BUT ME.
	//***** Called from the onclick function in the game *****		HMM I DUNNO DAVID!
	client.on('updateMe', function (me) {
		console.log("Update me was called on the server.");
		// Find the user in the data structure.
		if (users[me.ip] !== undefined) {
			// If he exists, store my position.
			users[me.ip].pos = me.pos;
			// Send my new position to everyone else connected, not me.
			client.broadcast.emit('updateHim', users[me.ip]);
		} else {
			// If I dont exist somehow, fire a message.
			console.log(" Unregistered user " + me.ip);
			return;
		}
	});
//			(3A)
	// GIVE ME MY KINECT DATA YOU SOB!
	//***** Called every game frame ******
	client.on('updateKinect', function () {
		console.log("Update my kinect request on server");
		var temp = {};
		var count = 0;
		for (index in users) {
			count++;
			temp[count] = users[index];
		}
		// Return the users kinect data.
		client.emit('syncKinect', temp);
	});
	//			
	// 	PASS THE SERVER THE CLIENT SCORE
	//
	client.on('gameOver', function (userScore) {
		console.log("The user has completed the game and uploaded their score.");
		// Store the score of the client and check if its game over.
		users[client.handshake.address.address].score = userScore;
		var userCount = users.length;
		var usersFinished = 0;
		for (i in users) {
			if (users[i].score != 0) {
				// Increment the number of users finished.
				usersFinished++;
				// Increment the topScore.
				topScore += users[i].score;
			}
		}
		//topScore = userScore;
		if (usersFinished == userCount + 1) {
			// Game over, all the players are finished.
			client.emit('topScorePage');
		} else {
			// Reset the score
			topScore = 0;
		}
	});
	//
	//
	//
	client.on('getGroupScore', function () {
		// Assign who the key holder was.
		var keyHolder = "James";
		// Construct a map with the score and the keyholder in it.
		var data = {
			"score": topScore,
			"keyHolder": keyHolder
		};
		// Send the score to the client.
		client.emit('groupScore', data);
	});
	// 				(4)
	// TELL EVERYONE I'M OFF AND DELETE ME.
	//
	client.on('disconnect', function () {
		socket.sockets.emit('deleteHim', users[client.handshake.address.address]); // Send to everyone, including me.
		// Tell the users that some one has quit so tey can remve from their scenes.
		delete users[client.handshake.address.address];
	});
	client.on('test', function () {
		var test = {};
		var count = 0;
		for (index in users) {
			count++;
			test[count] = users[index];
		}
		socket.sockets.send('test', test);
		socket.sockets.emit('test', test);
		socket.sockets.send('test', test);
		client.emit('test', test);
	});
}); // End of 'onConnection'
// Listen for connection
server.listen(clientPort);
var javaPort = 7540;
var dataBuffer = "";
var newlineIndex = 0
var javaServer = require('net').createServer();
javaServer.on('listening', function () {
	console.log('Server is listening on for kinect data on :' + javaPort);
});
javaServer.on('error', function (e) {
	console.log('Server error: '); // + e.code);
});
javaServer.on('close', function () {
	console.log('Server closed');
});
javaServer.on('connection', function (javaSocket) {
	// We're ready to stream. When the library gets a '\n' it begins to send the data...
	javaSocket.write("{continue:true}\n");
	//
	// DATA RECIEVED FROM THE KINECT.
	//
	javaSocket.on('data', function (data) {
		dataBuffer += data;
		newlineIndex = dataBuffer.indexOf('\n');
		clients[javaSocket.remoteAddress].emit('sandraHasConnected');
		if (newlineIndex == -1) {
			// Send next packet.
			javaSocket.write("{continue:true}\n");
			return; // If there was no end of package in the data return.
		}
		// Store the kinect data locally on the server.
		if (users[javaSocket.remoteAddress] !== undefined) {
			users[javaSocket.remoteAddress].kinect = JSON.parse(dataBuffer.slice(0, newlineIndex));
			users[javaSocket.remoteAddress].visible = true;
			var ipAddress = javaSocket.remoteAddress;
			var info = users[javaSocket.remoteAddress].kinect;
			var userKey = 0;
			for (i in info) {
				if (i == "accept" && info[i] == "true") {
					for (i2 in clients) {
						for (i3 in users) {
							if (users[i3].ip == javaSocket.remoteAddress) {
								userKey = users[i3].userKey;
							}
						}
						for (i3 in clients) {
							clients[i3].emit("userReady", userKey);
						}
					}
				} else if (i == "cancel" && info[i] == "true") {
					for (i2 in clients) {
						for (i3 in users) {
							if (users[i3].ip == javaSocket.remoteAddress) {
								userKey = users[i3].userKey;
							}
						}
						for (i3 in clients) {
							clients[i3].emit("userNotReady", userKey);
						}
					}
				}
			}
		}
		dataBuffer = dataBuffer.slice(newlineIndex + 1);
		javaSocket.write("{continue:true}\n");
	}); // End of on.Data
	// User has disconnected...
	javaSocket.on('close', function () {
		//users[ javaSocket.address().address ].visible = false;
		//delete interfaces[ javaSocket.remoteAddress  ];
	});
});
// Listen for connections on the java port specified!
javaServer.listen(javaPort);
