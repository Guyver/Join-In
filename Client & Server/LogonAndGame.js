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
var rooms = [];		

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
//var connected = [];
var clients=[];
//var g_id;
var images=[];



socket.sockets.on( 'connection', function( client ){

	clients[client.handshake.address.address]=client;
	
	function registerUser(userKeyParam){
	

		var mongoose = require('mongoose/'),
			Schema = mongoose.Schema;
		
		mongoose.connect('mongodb://localhost/joinInDB');
		
		var userSchema = new Schema({
			name : String, 
			userKey: Number,
			score : Number,
			connectedImageUrl : String,
			disconnectedImageUrl: String,
			pos : String, 
			kinect : String,
			meshName :String
			
		});		

		var usersModel= mongoose.model('users',userSchema);
			
		var nameAux;
		var scoreAux;
		var connectedImageUrlAux;
		var disconnectedImageUrlAux;
		var posAux;
		var kinectAux;
		var meshNameAux;
	
		var clientIPAddress=client.handshake.address.address;
		
		usersModel.findOne({userKey:userKeyParam}, function (err, doc) {
			
			 nameAux=doc.name;
			 scoreAux=doc.score;
			 connectedImageUrlAux=doc.connectedImageUrl;
			 disconnectedImageUrlAux=doc.disconnectedImageUrl;
			 posAux=0;
			 kinectAux=null;
			 meshNameAux=doc.meshName;

			// Construct a map from the new player.
			var map = { 
		
				"ip":clientIPAddress,
				"userKey":userKeyParam,
				"name": nameAux,
				"score": scoreAux,
				"connectedImageUrl": connectedImageUrlAux,
				"disconnectedImageUrl": disconnectedImageUrlAux,
				"pos": posAux,
				"kinect": kinectAux,
				"visible": true,
				"meshName": meshNameAux
			};
			
			// Store me in the map format.
			users[ clientIPAddress ] =  map ;	
	
			for(index in users){
				
				socket.sockets.emit( 'updateNewUser',  JSON.stringify(users[index]) );	
			}	
			
		});
	}
	
	//
	//
	//
	client.on('giveMeConnectedUsers', function() {
		var aux = {};
		for ( index in users){
			
			aux[ index ] = users[ index ];
		
		}
		client.emit( 'hereYouAreTheConnectedUsers', aux);
	});
	
	//
	//
	//
	client.on( 'giveMeUserPictures', function(userKeyParam) {
		
		var mongoose = require('mongoose/'),
			Schema = mongoose.Schema;
		
		mongoose.connect('mongodb://localhost/joinInDB');
		
		var userSchema = new Schema({
			name : String, 
			userKey: Number,
			score : Number,
			connectedImageUrl : String,
			disconnectedImageUrl: String,
			pos : String, 
			kinect : String,
			meshName :String
			
		});		


		var usersModel= mongoose.model('users',userSchema);
		var imagesAux=[];

		
		
		usersModel.find( {}, function (err, team) {
			if(images.length==0){
				for(var index=1; index<=team.length; index++){
			
					var indexAjustado = index-1;
					
					var clientIPAddress=client.handshake.address.address;
					imagesAux.push ({userKey:team[indexAjustado].userKey, image:team[indexAjustado].disconnectedImageUrl});
				}
			}else{
				imagesAux=images;	
				for(index in team){
					imagesAux[index].image=team[index].disconnectedImageUrl;
				}		
			}
			var usersAux = {};
			for(index in users){
				
				for(index2 in imagesAux){
		
					if(users[index].userKey==imagesAux[index2].userKey){
						
						imagesAux[index2].image=team[index2].connectedImageUrl;
					}else{
						imagesAux[index2].image=team[index2].disconnectedImageUrl;
					}
				}

			usersAux[ index ] = users[ index ];
			}
			
			for (i in users){
				console.log("i vale "+i+" y users "+users[i]);
			}

	  		client.emit( 'sendingUserPictures', imagesAux, usersAux);
			images= imagesAux;
			
			registerUser(userKeyParam);
		});
	});

		
	//				(1)
	// GET PLAYERS, SEND TO JUST ME.
	// ****** Only happens upon connection to the server. ******
	client.on('getPlayers', function() {
		
		var test = {};
		var count=0;
		for ( index in users){
			count++;
			test[ count ] = users[ index ];
		
		}
		
		// Send the new client all the connected users.
		client.emit( 'heresPlayersFromServer', test );

	});
	
	//
	//
	//
	client.on('registerMeInServerFirstPage', function(userKeyParam){
		
		registerUser(userKeyParam);	
	});
	
	
	// 				(2)
	// STORE ME AS A USER.
	//	******* Only happens once when the player sends a template for the server to fill in and store him here********
	client.on('registerMeInServer', function( data ){
		console.log("Register Me In Server was called on the server.");

		// Construct a map from the new player.
		var map = { 
		
			"name": data.name,
			"pos":data.pos,
			"ip":client.handshake.address.address,
			"kinect":data.kinect,
			"meshName":data.mesh
		};
		
		// Store me in the map format.
		users[ client.handshake.address.address ] =  map ;	

		// Return my profile to me.
		client.emit( 'registerSelf', { player : users[ client.handshake.address.address ] } );
		
		// Tell previously connected users a player connected and pass his profile to them. Don't send to self.
		client.broadcast.emit( 'RegisterNewUser', { player:users[ client.handshake.address.address ], ip:client.handshake.address.address }  );
	});
	
	
	// 				(3)
	// UPDATE ME AND TELL EVERYONE BUT ME.
	//***** Called from the onclick function in the game *****		HMM I DUNNO DAVID!
	client.on( 'updateMe', function( me ) {
	
		console.log("Update me was called on the server.");
		
		// Find the user in the data structure.
		if( users[ me.ip ] !== undefined){
			
			// If he exists, store my position.
			users[ me.ip ].pos = me.pos;
			
			// Send my new position to everyone else connected, not me.
			client.broadcast.emit( 'updateHim', users[ me.ip] );
		}
		else
		{
			// If I dont exist somehow, fire a message.
			console.log( " Unregistered user "+ me.ip );return;		
		}

	});
	
	
	//			(3A)
	// GIVE ME MY KINECT DATA YOU SOB!
	//***** Called every game frame ******
	client.on( 'updateKinect', function( ) {
		console.log( "Update my kinect request on server");
		
		var temp = {};
		var count=0;
		for ( index in users){
			count++;
			temp[ count ] = users[ index ];
		
		}
		// Return the users kinect data.
		client.emit('syncKinect', temp );
			
	});
	
	
	// 				(4)
	// TELL EVERYONE I'M OFF AND DELETE ME.
	//
	client.on('disconnect', function(){		
		
		
		socket.sockets.emit( 'deleteHim', users[ client.handshake.address.address ]);		// Send to everyone, including me.
		// Tell the users that some one has quit so tey can remve from their scenes.
		delete users[ client.handshake.address.address ];
	});
	

	client.on( 'test', function(){
		
		var test = {};
		var count=0;
		for ( index in users){
			count++;
			test[ count ] = users[ index ];
		
		}
		socket.sockets.send( 'test', test );
		socket.sockets.emit( 'test', test );		
		socket.sockets.send( 'test', test );	  
		client.emit( 'test', test );		
	});

});// End of 'onConnection'

// Listen for connection
server.listen( clientPort );


var javaPort = 7540;
var dataBuffer = "";
var newlineIndex = 0
var javaServer = require('net').createServer();

javaServer.on('listening', function () {

    console.log('Server is listening on for kinect data on :' + javaPort);
});

javaServer.on('error', function ( e ) {

    console.log('Server error: ');// + e.code);
});

javaServer.on('close', function () {

    console.log('Server closed');
});

javaServer.on('connection', function ( javaSocket ) {

	var remote_address = javaSocket.remoteAddress;
	
	for( index in users ){
		
		if( users[ index ].ip == remote_address ){
		
			javaSocket.write( "{continue:true}\n" );
			break;
		}
		else
		{
			console.log(" We didn't find the corresponding client to the interface. Not storing data." );		
		}
	}
	
	//
	// DATA RECIEVED FROM THE KINECT.
	//
    javaSocket.on('data', function( data ){
		dataBuffer += data;
		
		newlineIndex = dataBuffer.indexOf( '\n' );
		
		clients[javaSocket.remoteAddress].emit( 'sandraHasConnected' );
		
		if( newlineIndex == -1){
			// Send next packet.
			javaSocket.write( "{continue:true}\n");
			
			return;// If there was no end of package in the data return.
		}
		
		// Store the kinect data locally on the server.
		if( users[javaSocket.remoteAddress ] !== undefined){

		
			users[ javaSocket.remoteAddress ].kinect = JSON.parse( dataBuffer.slice(0, newlineIndex) );
			users[ javaSocket.remoteAddress ].visible = true;
			
			var ipAddress= javaSocket.remoteAddress;

		
		
			var info= users[ javaSocket.remoteAddress ].kinect;
			var userKey=0;
			for(i in info){
			
				if(i=="accept" && info[i]=="true"){
					
					for(i2 in clients){	
						for(i3 in users){
							if(users[i3].ip==javaSocket.remoteAddress){
								userKey=users[i3].userKey;						
							}
						}
						for(i3 in clients){
							clients[i3].emit("userReady", userKey );	

						}
					}	
				}else if(i=="cancel" && info[i]=="true"){
					for(i2 in clients){
						for(i3 in users){
							if(users[i3].ip==javaSocket.remoteAddress){
								userKey=users[i3].userKey;						
							}
						}
						for(i3 in clients){
							clients[i3].emit("userNotReady", userKey );	

						}
					}	
				}		
			}

		}


        dataBuffer = dataBuffer.slice(newlineIndex + 1);	
		javaSocket.write(  "{continue:true}\n" );
	
		
	});// End of on.Data

	// User has disconnected...
    javaSocket.on('close', function() {
		
		//users[ javaSocket.address().address ].visible = false;
		//delete interfaces[ javaSocket.remoteAddress  ];
    });
});

// Listen for connections on the java port specified!
javaServer.listen( javaPort );
