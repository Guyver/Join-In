// The listening port for sandraSocket
var javaPort = 7540;
// A buffer for incoming data.
var dataBuffer = "";
// The index of the beffer string where a /n was found.
var newlineIndex = 0;
// Create a TCP/UDP server 
var sandraSocket = require('net').createServer(),
	io = require('./lib/socket.io'),
	kinectData ={
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
 },
	g_sandra;


sandraSocket.on('listening', function () {

    console.log('Server is listening on for kinect data on :' + javaPort);
});


sandraSocket.on('error', function ( e ) {

    console.log('Server error: ');// + e.code);
});


sandraSocket.on('close', function () {

    console.log('Server closed');
});


sandraSocket.on( 'connection', function ( sandra ) {

	console.log( 'Sandra connected.' );
	g_sandra = sandra;
	sandra.write( "{continue:true}\n" );
	
    sandra.on( 'data', function( data ){
		
		console.log('Sandra data recieved.');
		gotData( sandra, data );	
	});


    sandra.on('close', function() {


    });
});

sandraSocket.listen( 7540 );


var socket = io.listen( 8080 );

socket.on( 'connection' , function( client ){

	console.log( "A connection has been established" );
	
	client.on( 'getData', function(){

		console.log( 'Client requesting Sandra data.' );
		client.emit( 'returnData', kinectData );
	});

	
	client.on( 'giveMeHugs', function( flag ) {

		ActivateHugs( flag );
	});
	
	
	client.on( 'disconnect', function(){
	
	
	});
});


function ActivateHugs( flag ){

	var giveMeHugsMap;
		
	if( flag ){
		giveMeHugsMap = "{device:kinect,action:start,type:hug}\n" ;
	}else{
		giveMeHugsMap = "{device:kinect,action:stop,type:hug}\n" ;
	}

	g_sandra.write( giveMeHugsMap );
};


function gotData( sandra, data ){
		
	dataBuffer += data;
	newlineIndex = dataBuffer.indexOf( '\n' );

	if( newlineIndex == -1 ){
		// Send next packet.
		sandra.write( "\n");
		return;// If there was no end of package in the data return.
	}
	
	// Assign to the Kinect data store. 	
	kinectData = dataBuffer.slice( 0, newlineIndex );
	console.log( 'Got data.'+ kinectData );
	// Reset the buffer and request next message.
    dataBuffer = dataBuffer.slice( newlineIndex + 1 );	
	sandra.write( "{continue:true}\n" );

};