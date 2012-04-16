/*

	@Author:
	@Brief: 
	This the client side bridge to the server.
	Its the point where data is recieved from the server.
*/


// Connect to the ip of the server. 
// This will change according to the network.
var socket = io.connect('193.156.105.166:7541');
// A map of the kinect joints as vector3's
var kinectMap = {};
// Has the server recieved any kinect data.
var kinect = false;
	
/*	
	This is where we recieve the kinect data from the java app.
*/
socket.on('passClientData', function ( data ){
	 
	if( data != null){
		kinectMap = data ;
		console.log('We have the kinect data in the game now.');
	}	
	else{
		console.log("We've dropped a packet, it was null.");
	}
		
	});
	
	/*
		Tell the client to start syncing the kinect data as its ready.
	*/
	socket.on('kinectSynced', function ( bool ){
		kinect = bool;
		console.log(" The kinect synced = :"+bool);
	});
	
	/*
		On connection to server, ask for user's name with an anonymous callback
	*/
	socket.on('connect', function()	{
	
	});