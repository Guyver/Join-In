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
// User group final score.
var topScore = 0;
//var connected = [];
var clients=[];
//var g_id;
var images=[];
//
var totalClients = 0;
// Get mongoose and connect to the server.
var mongoose = require( 'mongoose' ).connect( 'mongodb://localhost/joinInDB' );



socket.sockets.on( 'connection', function( client ){

	clients[ client.handshake.address.address ]=client;
	
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
	client.on('giveMeConnectedUsers', function() {
		var aux = {};
		
		for ( index in users){
			
			aux[ index ] = users[ index ];
		
		}
		client.emit( 'hereYouAreTheConnectedUsers', aux);

	});
	
	client.on( 'giveMeUserPictures', function( userKeyParam ) {
		
		var Schema = mongoose.Schema;
		
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

		
		
		usersModel.find({}, function (err, team) {
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

	
	client.on( 'registerMeInServerFirstPage', function( userKeyParam ){
		
		registerUser(userKeyParam);	
	});
	// 				(2)
	// STORE ME AS A USER.
	//	******* Only happens once when the player sends a template for the server to fill in and store him here********
	client.on('registerMeInServer', function( data ){
	
		console.log("Register Me In Server was called on the server.");
	
		// Get stuff from the database using the user key.
		registerUser( data.userKey );		
	});
	
	//
	// Update my position in the user map.
	// 
	client.on( 'updateMe', function( me ) {
	
		// Find the user in the data structure.
		if( users[ client.handshake.address.address ] !== undefined){
			
			// If he exists, store my position.
			users[ client.handshake.address.address ].pos = me.pos;
		}
		else
		{
			// If I dont exist somehow, fire a message.
			console.log( " Unregistered user "+ client.handshake.address.address );	
		}

	});
	
	
	//	Called in the game loop to fetch the new Sandra data.	
	// 	It returns the kinect data in a map form.
	//
	client.on( 'updateKinect', function( ) {
		
		try{
		
			var kinectData = users[ client.handshake.address.address ].kinect
			// Return the users kinect data.
			client.emit('syncKinect', kinectData );
		}catch( err ){
			console.log("Do fuck all");		
		}
			
	});
	
	//			
	// 	PASS THE SERVER THE CLIENT SCORE
	//
	client.on( 'gameOver', function( userScore ) {
	
		console.log( "The user has completed the game and uploaded their score.");
		
		// Store the score of the client and check if its game over.
		users[ client.handshake.address.address ].score = userScore;	
		
		var userCount = users.length;
		var usersFinished = 0;
		var totalClients = 0;
		
		for ( i in users ){
			
			if( users[ i ].score != 0 ){
			
				// Increment the number of users finished.
				usersFinished++;
				// Increment the topScore.
				topScore += users[ i ].score;				
			}
			totalClients++;
		}
		
		//topScore = userScore;
		if( usersFinished == userCount || ( usersFinished == totalClients && usersFinished != 0 ) ){
			
			for ( i in users ){
			
				var ip = users[ i ].ip;
				// Game over, all the players are finished.
				clients[ ip ].emit( 'topScorePage' );
			}
		}
		else{
			// Reset the score
			topScore = 0;
		}	
	});
		

		
	//
	//
	//
	client.on( 'checkLoaded', function( ){	
	
		if ( users.length == totalClients )
			client.emit( 'hasFinished' );	
	
	});
	
	
	
	//
	//
	//
	client.on( 'getGroupScore', function( groupScore ){	
		
			//users[ client.handshake.address.address ].score = userScore;
			// Assign who the key holder was.
			var keyHolder = "Not implemented yet!";	

			var test = {};
			for ( i in users ){
				test[ i ] = users[ i ];
			}
			
			// Construct a map with the score and the keyholder in it.
			var data = {
				"score":topScore,
				"keyHolder":keyHolder,	
				"users":test
			};		
			
			// Send the score to the client.
			client.emit( 'groupScore', data );	
		
	});
	
	
	// 				(4)
	// TELL EVERYONE I'M OFF AND DELETE ME.
	//
	client.on('disconnect', function(){		
	
		// Tell the users that some one has quit so tey can remve from their scenes.
		delete users[ client.handshake.address.address ];
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

	
	// We're ready to stream. When the library gets a '\n' it begins to send the data...
		
	javaSocket.write( "{continue:true}\n");

	//
	// DATA RECIEVED FROM THE KINECT.
	//
    javaSocket.on('data', function( data ){
		dataBuffer += data;
		
		newlineIndex = dataBuffer.indexOf( '\n' );
		
		clients[ javaSocket.remoteAddress ].emit( 'sandraHasConnected' );
		
		if( newlineIndex == -1){
			// Send next packet.
			javaSocket.write( "{continue:true}\n");
			
			return;// If there was no end of package in the data return.
		}
		// Store the kinect data locally on the server.
		if( users[javaSocket.remoteAddress ] !== undefined){

		
			users[ javaSocket.remoteAddress ].kinect = JSON.parse( dataBuffer.slice(0, newlineIndex) );			
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