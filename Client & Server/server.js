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
// I dunno, its Santi's
var clientsConnected = [];
// Dummy kinect.
var kinectData ={
     "leftElbow": {
         "x": -274.7901123046875,
         "y": 280.0702239990234,
         "z": 2308.780859375
     },
     "head": {
         "x": 241.60778045654297,
         "y": 523.9069396972657,
         "z": 2158.64599609375
     },
     "rightElbow": {
         "x": 653.9172607421875,
         "y": 216.89061584472657,
         "z": 2262.339697265625
     },
     "rightKnee": {
         "x": 281.5267761230469,
         "y": -634.7611511230468,
         "z": 2234.8218017578124
     },
     "device": "kinect",
     "leftShoulder": {
         "x": 8.228845655918121,
         "y": 304.7044708251953,
         "z": 2242.640625
     },
     "leftFoot": {
         "x": -78.25286865234375,
         "y": -1038.9497192382812,
         "z": 2347.7722412109374
     },
     "rightHip": {
         "x": 255.50746459960936,
         "y": -175.0600601196289,
         "z": 2202.8742431640626
     },
     "rightFoot": {
         "x": 280.81434020996096,
         "y": -1053.9205322265625,
         "z": 2347.6310791015626
     },
     "rightShoulder": {
         "x": 388.03627624511716,
         "y": 263.89619903564454,
         "z": 2194.360546875
     },
     "rightHand": {
         "x": 997.0059692382813,
         "y": 280.8474090576172,
         "z": 2333.0134765625
     },
     "neck": {
         "x": 198.1325653076172,
         "y": 284.3003356933594,
         "z": 2218.5005615234377
     },
     "torso": {
         "x": 174.02362518310548,
         "y": 60.23494758605957,
         "z": 2217.4669189453125
     },
     "leftKnee": {
         "x": -30.573639106750488,
         "y": -610.0912719726563,
         "z": 2241.30546875
     },
     "leftHand": {
         "x": -653.9635803222657,
         "y": 304.9511779785156,
         "z": 2292.358349609375
     },
     "leftHip": {
         "x": 44.321907424926756,
         "y": -152.60081939697267,
         "z": 2229.9922607421877
     }
 };
 
 
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
		case '.wav':// We're serving an image.
            contentType = 'text/wav';
			filePath = '.'+request.url;
			//console.log( "Serving DAE "+request.url ); 
		case '.mp3':// We're serving an image.
            contentType = 'text/mp3';
			filePath = '.'+request.url;
			//console.log( "Serving DAE "+request.url ); 
            break;
    }
     
    path.exists( filePath, function( exists ) {// Check to see if the file exists
     
        if ( exists ) {// Returned from the callback, checking to see if valid.
			// Read file from disk and trigger callback.
            fs.readFile( filePath, function( error, content ) {
                if ( error ) {
					// If there's and error throw 500
                    response.writeHead( 500 );
                    response.end();
                }
                else {
					// Otherwise return the file.
                    response.writeHead( 200, { 'Content-Type': contentType } );
                    response.end( content, 'utf-8' );
                }
            });
        }
        else {
			// Throw 404 if the file isn't there.
            response.writeHead( 404 );
            response.end();
        }
	});

});// End of Http create server.

// Listen for connections 
var socket = io.listen( server,{
        'log level': 0         // Set the log level.
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
	@name:
	@brief:
	@args:
	@returns:
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
	@name:
	@brief:
	@args:
	@returns:
*/
function findTeamMatesSockets( teamArray ){
	
	var teamSockets = {};
	
	for( i in teamArray ){
	
		teamSockets[ i ] = clients[ i ];
	}
	
	return teamSockets;
};

/**
	@name:
	@brief:
	@args:
	@returns:
*/
function sendToTeam( team, name, data ){

	for( i in team ){
	
		team[ i ].emit( name, data );
	}
};


/**
	@name:
	@brief:
	@args:
	@returns:
*/
function registerUser( client, userKeyParam ){

	var clientIPAddress = client.handshake.address.address;

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
			"kinect": kinectData,
			"visible": true,
			"meshName": doc.meshName,
			"team": doc.teamId,
			"teamNumber" : doc.teamPosition, 
			"ready": false
		};
		
		console.log( map );
		// Store me in the map format.
		users[ clientIPAddress ] =  map ;	

		var myTeam = {};
		// Update the green bar of the others about me
		for( index in users ){
		
			if( ( users[ index ].team ) - ( users[ clientIPAddress ].team ) == 0 ){
				myTeam[ index ] = users[ index ];	
			}
		}	
			
		// Return the user data for the main player.
		client.emit( 'youWereCreated', users[ clientIPAddress ] );			
			
		// Send to all clients the new user and the team of players. 
		socket.sockets.emit( 'updateNewUser',  users[ clientIPAddress ], myTeam );
				
	});
};
