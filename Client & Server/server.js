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
// I dunno, its Santis
var clientsConnected = [];

var server = http.createServer( function ( request , response ) {
 
    //console.log('request starting...'+ request.url);

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
			//console.log( "Serving JS "+request.url ); 
            break;
        case '.css':// Serving some style
            contentType = 'text/css';
			filePath = '.'+request.url;
			//console.log( "Serving CSS "+request.url ); 
            break;
		case '.png':// We're serving an image.
            contentType = 'image/png';
			filePath = '.'+request.url;
			//console.log( "Serving PNG "+request.url ); 
            break;
		case '.jpg':// We're serving an image.
            contentType = 'image/jpg';
			filePath = '.'+request.url;
			//console.log( "Serving JPG "+request.url ); 
            break;
		case '.dae':// We're serving an image.
            contentType = 'text/plain';
			filePath = '.'+request.url;
			//console.log( "Serving DAE "+request.url ); 
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

// Listen for connections 
var socket = io.listen( server,{
        'log level': 0                // socket.io spams like whore, silence it
});
	
// The users data.
var users = [];	
// The number of users.						
var userCount = 0;						
// User group final score.
var topScore = 0;
// A store of the sockets.
var clients=[];
// The images of the users? Santis
var images=[];
// Total clients connected.
var totalClients = 0;
// Get mongoose and connect to the server.
var mongoose = require( 'mongoose' );
mongoose.connect( 'mongodb://localhost/test' );
var Schema = mongoose.Schema;


var userSchema = new Schema({
			userKey: Number,
			password: String,
			name : String, 		
			score : Number,
			connectedImageUrl : String,
			pos : String, 
			ip : String,
			kinect : String,
			meshName :String,
			teamPosition: Number,
			teamId: Number
	});				
		
var usersModel = mongoose.model( 'users' , userSchema );

socket.sockets.on( 'connection', function( client ){

	// Store the connected socket for future use.
	clients[ client.handshake.address.address ] = client;


	//
	//
	//
	client.on( 'giveMeConnectedUsers', function( team ) {
	
		var aux = {};
	
		for ( index in users ){			
			if( users[ index ].team == team ){
			
				aux[ index ] = users[ index ];
			}
		}
		
		// Send them back to the client.
		client.emit( 'hereYouAreTheConnectedUsers', aux );
	});

	
	//
	//
	//
	client.on( 'giveMeUserPictures', function( userKeyParam ) {

		var imagesAux=[];

		usersModel.findOne( { userKey:userKeyParam } , function( err, currentUser ){

			var currentTeam = currentUser.team;

			usersModel.find( { team:currentTeam }, function ( err, team ) {

				for(var index = 1; index<=team.length; index++){

					var adjustedIndex = index-1;
					var clientIPAddress=client.handshake.address.address;
					imagesAux.push ({ userKey:team[ adjustedIndex ].userKey, image:team[ adjustedIndex ].disconnectedImageUrl });
				}

				var usersAux = {};
				for( index in users ){

					for( index2 in imagesAux ){

						if( users[ index ].userKey == imagesAux[ index2 ].userKey){

							imagesAux[ index2 ].image = team[ index2 ].connectedImageUrl;
						}else{
							imagesAux[ index2 ].image = team[ index2 ].disconnectedImageUrl;
						}
					}

					usersAux[ index ] = users[ index ];
				}
				
				client.emit( 'sendingUserPictures', imagesAux, usersAux);
				images= imagesAux;

				registerUser( client, userKeyParam );

			});
		});
	});


	// 				
	// 
	//	
	client.on( 'registerMeInServer' , function( data ){

		// Get stuff from the database using the user key.
		registerUser( client, data.userKey );	

	});

	
	//
	// 
	// 
	client.on( 'updateMe', function( me ) {
		
		// Find the user in the data structure.
		if( users[ client.handshake.address.address ] != undefined ){
			
			try{
				// If he exists, store my position.
				users[ client.handshake.address.address ].kinect = me.kinect;
				users[ client.handshake.address.address ].sight = me.sight;
				users[ client.handshake.address.address ].pos = me.pos;
				
				var team = {};
				var teamCount = 0;
				for ( i in users ){

					if( users[ i ].team == me.team ){
					
						team[ i ] = users[ i ];
						teamCount++;
					}				
				}
				
				// If its multiplayer, send the data.
				if( teamCount > 0 ){
					// Give back the users team mates.
					client.emit( 'teamMates' , team ); 
				}
			}
			catch( error ){
				
				console.log( "Assignment error in updateMe : "+ error );
			}
		}
	});


	//	
	// 
	//
	client.on( 'updateKinect' , function( ) {

		try{		
			var kinectData = users[ client.handshake.address.address ].kinect
			// Return the users kinect data.
			client.emit('syncKinect', kinectData );
		}catch( err ){
			console.log("There's no kinect data or that user doesnt exist.");		
		}
	});

	
	//			
	// 	
	//
	client.on( 'gameOver', function( userScore ) {

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
	client.on( 'endHugging', function( ) {

		// Find the user who called.
		var keyHolder = users[ client.handshake.address.address ];
		// Get his team mates' clients.
		var team = findTeamMates( keyHolder.team );
		var sockets = findTeamMatesSockets( team )
		// Send to them that its game over time.
		sendToTeam( sockets, 'nextLevel', './reach.html' ); 

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


	// 				
	// 
	//
	client.on( 'disconnect', function( ){		
	
		try{
		
			var user = users[ client.handshake.address.address ];
			var team = findTeamMates( user.team );
			var sockets = findTeamMatesSockets( team )
			sendToTeam( sockets, 'deleteUser', { user : user } ); 
			delete users[ client.handshake.address.address ];
			delete clients[ client.handshake.address.address ];
		}
		catch( error ){
		
			console.log( "Failed deleting the user" );
		}
	});

});// End of 'onConnection'


// Listen for connection
server.listen( clientPort );

/**
	Get all team mates in the users array.
*/
function findTeamMates( teamId ){

	var team = {};
	
	for( i in users ){
	
		if( String ( users[ i ].team ) == String ( teamId ) ){
		
			team[ i ] = users[ i ];
		}
	}
	
	return team;
};

/**
	Get all the sockets for the team arg.

*/
function findTeamMatesSockets( teamArray ){
	
	var teamSockets = {};
	
	for( i in teamArray ){
	
		teamSockets[ i ] = clients[ i ];
	}
	
	return teamSockets;
};

/**
	Sent to all the sockets to name and data

*/
function sendToTeam( team, name, data ){

	for( i in team ){
	
		team[ i ].emit( name, data );
	}
};

/*
// The listening port for Sandra
var javaPort = 7540;
// A buffer for incoming data.
var dataBuffer = "";
// The index of the beffer string where a /n was found.
var newlineIndex = 0;
// Create a TCP/UDP server 
var javaServer = require('net').createServer();
// A store of the Sandra sockets.
var javaSockets = [];


//
//
//
javaServer.on('listening', function () {

    console.log('Server is listening on for kinect data on :' + javaPort);
});


//
//
//
javaServer.on('error', function ( e ) {

    console.log('Server error: ');// + e.code);
});


//
//
//
javaServer.on('close', function () {

    console.log('Server closed');
});


//
//
//
javaServer.on( 'connection', function ( javaSocket ) {

	javaSocket.write( "{continue:true}\n");
	javaSockets[ javaSocket.remoteAddress ] = javaSocket;
	
	//
	// 
	//
    javaSocket.on('data', function( data ){
	
		dataBuffer += data;
		newlineIndex = dataBuffer.indexOf( '\n' );
	
		if( !( clientsConnected [ javaSocket.remoteAddress ] == undefined || 
			clientsConnected [ javaSocket.remoteAddress ] == undefined ||  
			clientsConnected [ javaSocket.remoteAddress ] == null ) ){
			
			clientsConnected [ javaSocket.remoteAddress ]= true;
			if( clients[ javaSocket.remoteAddress ] != undefined && clients[ javaSocket.remoteAddress ] != null){
			
				clients[ javaSocket.remoteAddress ].emit( 'sandraHasConnected' );
			}
		}
	
		if( newlineIndex == -1 ){
			// Send next packet.
			javaSocket.write( "{continue:true}\n");
			console.log( "The data sent was never ending :"+ newlineIndex );
			return;// If there was no end of package in the data return.
		}
		
		// Store the kinect data locally on the server.
		if( users[ javaSocket.remoteAddress ] !== undefined ){

			var info = JSON.parse( dataBuffer.slice( 0, newlineIndex ) );
			
			if( info != null && info != undefined ){
			
				console.log( "The SANDRA address is :"+ javaSocket.remoteAddress );
				console.log( "The SANDRA is storing data in user :"+ users[ javaSocket.remoteAddress ] );
				users[ javaSocket.remoteAddress ].kinect = info;
			}	
			else{
				console.log( "The SANDRA info is bogus:"+ info );
			}

			for( i in info ){

				if( i == "pause" && info[ i ] == "true" ){
				
					clients[ javaSocket.remoteAddress ].emit( "pause", true );
					console.log("-----------------------------------------------------PAUSED-----------------------------------------------------");
				}
				else if( i == "resume" && info[ i ]== "true" ){
				
					clients[ javaSocket.remoteAddress ].emit( "pause", false );
					console.log("+++++++++++++++++++++++++++++++++++++++++++++++++++++RESUMED++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				}	
				
				if( i == "accept" && info[ i ] == "true" ){
					var myTeam={};
					var team;
					var givenUser={};
					
					// For each of the users...
					for( i3 in users ){
						
						// If this user has the same ip of sandra...
						if( users[ i3 ].ip == javaSocket.remoteAddress ){
							
							// Assign a user team id to team...
							team = users[ i3 ].team;	
							// Assign him as the given user...
							givenUser = users[ i3 ];
							// That user is now ready...
							users[ i3 ].ready = true;					
						}
					}
						
					// For each of the users again...
					for( i3 in users ){
						
						// If the users team is the team id...
						if( users[ i3 ].team == team ){
								
							// My team
							myTeam[ i3 ] = users[ i3 ];		
						}
					}
						
					socket.sockets.emit( 'updateNewUser',  givenUser, myTeam );
				
				}else if( i=="cancel" && info[ i ] == "true" ){
					var myTeam={};
					var team;
					var givenUser={};
	
					for( i3 in users ){
						if( users[ i3 ].ip == javaSocket.remoteAddress ){

							team = users[ i3 ].team;	
							givenUser=users[ i3 ];
							users[ i3 ].ready = false;					
						}
					}
					for( i3 in users ){
						if( users[ i3 ].team == team ){

							myTeam[ i3 ] = users[ i3 ];		
						}
					}
					socket.sockets.emit( 'updateNewUser',  givenUser, myTeam );
				}	
			}
		}
		else
		{
			console.log( "The SANDRA address is :"+ javaSocket.remoteAddress );
			console.log( "The users are :"+ users ); 
		}

        dataBuffer = dataBuffer.slice( newlineIndex + 1 );	
		javaSocket.write(  "{continue:true}\n" );

	});// End of on.Data

	// User has disconnected...
    javaSocket.on('close', function() {

	//users[ javaSocket.address().address ].visible = false;

    });
});

// Listen for connections on the java port specified!
javaServer.listen( javaPort );

*/

/**
	@name:
	@brief:
	@args:
	@returns:
*/
function registerUser( client, userKeyParam ){

	var clientIPAddress=client.handshake.address.address;

	usersModel.findOne( { userKey : userKeyParam } , function ( err, doc ) {

		console.log( doc );
		
		// Construct a map from the new player.
		var map = { 

			"ip":clientIPAddress,
			"userKey":userKeyParam,
			"name": doc.name,
			"score": doc.score,
			"connectedImageUrl": doc.connectedImageUrl,
			"disconnectedImageUrl": "http://1.bp.blogspot.com/_NBiNZNE1Qn0/TSHNPsWH0fI/AAAAAAAAALw/rlChMDZ_2OE/s1600/offline_Jan_03_Main.png",
			"pos": 0,
			"sight" : 0,
			"kinect": null,
			"visible": true,
			"meshName": doc.meshName,
			"team": doc.teamId,
			"teamNumber" : doc.teamPosition, 
			"ready": false
		};
		
		console.log( map );
		// Store me in the map format.
		users[ clientIPAddress ] =  map ;	

		var myTeam={};
		// Update the green bar of the others about me
		for( index in users ){

			if( ( users[ index ].team ) - ( users[ clientIPAddress ].team ) == 0){
				myTeam[ index ] = users[ index ];	
			}
		}	
			
		// Return the user data for the main player.
		client.emit( 'youWereCreated', users[ clientIPAddress ] );			
			
		// Send to all clients the new user and the team of players. 
		socket.sockets.emit( 'updateNewUser',  users[ clientIPAddress ], myTeam );
				
	});
};
