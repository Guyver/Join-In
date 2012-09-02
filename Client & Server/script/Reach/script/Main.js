/**	@Name:	Main
	@Author: James Browne
	@Brief:
	Where the game logic is controlled.
	
	* Add to players.
	* Will collision detection be done client side or server side?
	* Will the timing events be server side or client side?
	* Will the game flow logic be client or server side?
*/


var currentLevel = "reach";

// Connect to the server.
var socket = io.connect('193.156.105.158:7541');

// Connect to Sandra.
var sandra = io.connect( '127.0.0.1:8080' );
var kinectData;

// Variables for the sugary goodness!
var gui, param, varNum, interval;

// Three.js vars
var scene, renderer, mesh, geometry, material;

// Camera vars, initalised in "initCamera()"
var camera, nearClip, farClip, aspectRatio, fov;

// remember these initial values
var tanFOV;
var windowHeight = window.innerHeight;
var guiCanvas,guiCtx;
// Kinect data
var numJoints, model, jointList;
var sounds = [];
// Game Physics vars TODO: Move to player manager.
var players;

// The time since last frame.
var deltaTime, last, current;

var waiting = false;
var paused = false;
// The level manager.
var level_Manager;


var playerLoaded, objectsLoaded, startGame = false;
var groupScore, winner;

// Game objects.
var objects = [];

// The scene builder.
var architect;

/**	@Name:	Init
	@Brief:	Initalise objects we need.
	@Arguments:N/A
	@Returns:N/A
*/
function init(){
	
	// Set up the three scene.
	initScene();
	// Set up the renderer type.
	initRenderer();	
	// Set up the lights.
	setupLights();
	// Set up the camera.
	initCamera();
	// Audio
	initSound();
	// Create the game objects.
	createObjects();
	// Skybox...etc.
	setupEnviornment();
	// Send the server your data.
	sendData();	
	// The first call to the game loop.
	gameLoop();
	
};


/**	@Name:	Init Camera
	@Brief:	Initalise camera objects we need.
	@Arguments:N/A
	@Returns:N/A
*/
function initCamera(){
	nearClip = 1;
	farClip = 100000;
	fov = 70;
	aspectRatio = window.innerWidth / window.innerHeight;
	camera = new THREE.PerspectiveCamera( fov, aspectRatio, nearClip, farClip );
	camera.position.y = 150;
	camera.position.z = 1000;
	// Will be used to rescale the view frustrum on window resize...
	tanFOV = Math.tan( ( ( Math.PI / 180 ) * camera.fov / 2 ) );
	scene.add( camera );
};


/**	@Name:	Init Scene
	@Brief:	Initalise the Three.js Scene
	@Arguments:N/A
	@Returns:N/A
*/
function initScene(){
	// the scene contains all the 3D object data
	scene = new THREE.Scene();	
};


/**	@Name:	Init Render
	@Brief:	Initalise the renderer and add it to the Html page.
	@Arguments:N/A
	@Returns:N/A
*/
function initRenderer(){

	// Create the WebGl renderer using the canvas 3d.
	renderer = new THREE.WebGLRenderer({
			antialias: true,
			canvas: document.getElementById( "canvas3D" ),
			clearColor: 0x000000,
			clearAlpha: 0,
			maxLights: 4,
			stencil: true,
			preserveDrawingBuffer: false
	});
	
	// Fit the render area into the window.
	renderer.setSize( window.innerWidth, window.innerHeight );
	// Initalise the Gui canvas.
	guiCanvas = document.getElementById( "canvas2D" );
	guiCanvas.width = window.innerWidth;
	guiCanvas.height = window.innerHeight;
	// Get the context for drawing.
	guiCtx = guiCanvas.getContext( "2d" );
};


/**	@Name:	Setup Lights
	@Brief:	Initalise lights we need.
	@Arguments:N/A
	@Returns:N/A
*/
function setupLights(){
	var ambient = new THREE.AmbientLight( 0xffffff );
	scene.add( ambient );

	directionalLight = new THREE.DirectionalLight( 0xffffff );
	directionalLight.position.y = -70;
	directionalLight.position.z = 100;
	directionalLight.position.normalize();
	scene.add( directionalLight );

	pointLight = new THREE.PointLight( 0xffaa00 );
	pointLight.position.x = 0;
	pointLight.position.y = 0;
	pointLight.position.z = 0;
	scene.add( pointLight );
				
	// Directional
	var directionalLight = new THREE.DirectionalLight( 0xffffff );
	directionalLight.position.x = 500;
	directionalLight.position.y = 500;
	directionalLight.position.z = 0;
	directionalLight.position.normalize();
	// Add to the scene.
	scene.add( directionalLight );

	// Another directional...
	var directionalLight = new THREE.DirectionalLight( 0xffffff );
	directionalLight.position.x = 3000;
	directionalLight.position.y = 500;
	directionalLight.position.z = -2000;
	directionalLight.position.normalize();
	// Add to the scene.
	scene.add( directionalLight );
	

};


function initSound(){
	
	sounds[0] = document.getElementById( 'ambient' ) ;
	sounds[1] = document.getElementById( 'hug' ) ;
	sounds[2] = document.getElementById( 'error' );
};


/**	@Name:	Create Objects.
	@Brief:	Create the games objects like the Player.
	@Arguments:N/A
	@Returns:N/A
*/
function createObjects(){		
	
	// The list of joint names for the kinect.
	jointList = [ 'head',
				'leftElbow',
				'leftFoot',
				'leftHand',
				'leftHip',
				'leftKnee',
				'leftShoulder',
				'neck',
				'rightElbow',
				'rightFoot',
				'rightHand',
				'rightHip',
				'rightKnee',
				'rightShoulder',
				'torso'];
	// Initalise the level manager.
	level_Manager = new Level_Manager();
	
	
	level_Manager.getPlayer()._userKey = localStorage.userKey;
	// Define the map with hash tags.
	var my_scene = {
				"map" : [
					"################",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"#..............#",
					"################"
				] 
	};
	
	// Initalise the scene builder with the hash map.
	architect = new Scene_Builder( my_scene );	
	
};


/**	@Name:	Game Loop
	@Brief:	This is the loop we call per frame to update the game.
	@Arguments:N/A
	@Returns:N/A
*/
function gameLoop(){
	
	requestAnimationFrame( gameLoop, renderer.domElement );
	
	//
	// GUI Stuff
	//
	guiCanvas.width = guiCanvas.width;
	// Set the font and size.
	guiCtx.font = 'italic 40px Calibri';
	guiCtx.shadowOffsetX = 5;
	guiCtx.shadowOffsetY = 5;
	guiCtx.shadowBlur    = 4;
	guiCtx.shadowColor   = 'rgba( 155, 155, 155, 0.5)';
	guiCtx.fillStyle     = 'rgba(255, 255, 255, 0.5)';
	guiCtx.fillRect( 5, 5, 255, 255 );
	guiCtx.fillStyle     = '#000';
	guiCtx.drawImage( level_Manager._player_Manager.getPlayer()._image, 10 ,10 , 128, 128 );
	guiCtx.fillText( level_Manager._player_Manager.getPlayer()._name, 10, 180 );
	guiCtx.fillText( 'Score : '+ level_Manager._player_Manager.getPlayer()._score, 10, 210 );
	guiCtx.fillText( 'Split : '+ level_Manager._player_Manager.getPlayer()._split, 10, 240 );
		
		
	//
	// Audio Stuff
	//
	for ( i in sounds ){
		if( sounds[ i ].ended ){
			// Reload if finished.
			sounds[ i ].load();
		}
	}
	
	// Get the Sandra Data.
	sandra.emit( 'getData' );
	
	/*
		Wait while the player models load, the objects, the other players and finally a call from the server to syncronise start!	
		if( !gameOver && PlayersLoaded && ObjectsLoaded && startGame )
	*/
	
	if(  playerLoaded && objectsLoaded && startGame && !level_Manager._player_Manager._gameOver && !paused ){
		
		// Update the level manager.
		level_Manager.update( scene, camera );
		
		// Render the scene.
		render();		
		
	}
	else{
	
		/*
			Check to see if the players models are loaded. Then load the Objects.
		*/
		if( !playerLoaded ){
			
			// Count the models to see if they are loaded yet.
			var modelCount = level_Manager._player_Manager.getPlayer().getModels().length;

			if( modelCount == 14 ){
			
				playerLoaded = true;

				// Begin loading the models. 			
			}

		}
		
		
		/*
			Check to see if the objects are loaded. 
		*/
		if( !objectsLoaded && playerLoaded ){
			
			objectsLoaded = true;
			startGame = true;
		}
	

		if( !waiting && !paused && level_Manager._player_Manager._gameOver && level_Manager._player_Manager._playGame ){
		
			// If we're not waiting and the game isn't paused and its game over go in here.
			socket.emit( 'getGroupScore', { score : level_Manager.getPlayer()._score } );
			waiting = true;	
		}
		
		if( paused ){
			// Do nothing 
			console.log( "The game is paused." );
			guiCtx.fillText( "PAUSED", guiCanvas.width/2 , guiCanvas.height/2 );
		}
		
		if( waiting ){
	
			guiCtx.fillText( "Group Score : "+groupScore , guiCanvas.width/2 , 30 );
			guiCtx.fillText( "First finished : "+winner , guiCanvas.width/2 , 70 );
		
		}
		
	}// End Else.
	
	if( !paused ){

			// Package your player into a map.
			var me = {
				name : level_Manager.getPlayer()._name,
				pos : level_Manager.getPlayer()._position,
				sight : level_Manager.getPlayer()._sightNode,
				kinect : level_Manager.getPlayer()._rig._translatedMap,	
				team : level_Manager.getPlayer()._team,
				userKey : level_Manager.getPlayer()._userKey,
				score : level_Manager.getPlayer()._score
			};
			
			// Send it to the server to be stored for others.
			socket.emit( 'updateMe', me  );	

		level_Manager.getPlayer().update( );
		
	}// End of Not Paused
	
};// End of Game Loop


/**



*/
function createTeam(){
	
	var team = globalTeam;
	var offset = 0;
	// Pair the team members to the empty players in other players.
	for ( i in team ){

		if( i != level_Manager._player_Manager.getPlayer()._ip )
		{
			for ( j in level_Manager._player_Manager._otherPlayers ){
			
				var index =  ( parseInt( j ) + offset ).toString() ;
				level_Manager._player_Manager._otherPlayers[ index ]._name = team[ i ].name;
				level_Manager._player_Manager._otherPlayers[ index ]._ip = team[ i ].ip;
				level_Manager._player_Manager._otherPlayers[ index ]._userKey = team[ i ].userKey;
				level_Manager._player_Manager._otherPlayers[ index ]._kinectData = team[ i ].kinect;
				level_Manager._player_Manager._otherPlayers[ index ]._score = team[ i ].score;
				level_Manager._player_Manager._otherPlayers[ index ]._position.x = team[ i ].pos.x;
				level_Manager._player_Manager._otherPlayers[ index ]._position.y = team[ i ].pos.y;
				level_Manager._player_Manager._otherPlayers[ index ]._position.z = team[ i ].pos.z;
				level_Manager._player_Manager._otherPlayers[ index ]._sightNode.x = team[ i ].sight.x;
				level_Manager._player_Manager._otherPlayers[ index ]._sightNode.y = team[ i ].sight.y;
				level_Manager._player_Manager._otherPlayers[ index ]._sightNode.z = team[ i ].sight.z;
				level_Manager._player_Manager._otherPlayers[ index ]._team = team[ i ].team;
				delete team[ i ];
				break;
			}	
			offset+=1;
		}
		else
		{
			//level_Manager._player_Manager.getPlayer()._kinectData = team[ i ].kinect;		
		}
	}
	// Set the game to start after the players have been assigned.
	level_Manager._player_Manager._playGame = true;	
};
 

/**	@Name:	Render 
	@Brief:	Draw the scene.
	@Arguments:N/A
	@Returns:N/A
*/
function render(){
	renderer.render( scene, camera );
};



/**	@Name:	Setup Enviornment 
	@Brief:	Initalise terrain and game art.
	@Arguments:N/A
	@Returns:N/A
*/
function setupEnviornment(){

	//
	// Create Sky Box.
	//
	Skybox();
	
	//
	// Create Plane
	//
	/*
	
	imageManager.queueDownload( 'img/Proxy_Grass.png' );
	imageManager.queueDownload( 'img/grassTile01.png' );
	
	*/
	var planeTex = new THREE.Texture(imageManager.getAsset('img/seamlessFloor.jpg', {}, render()));
	
	planeTex.needsUpdate = true;
	planeTex.wrapT = THREE.RepeatWrapping;
	planeTex.wrapS = THREE.RepeatWrapping;
	planeTex.repeat.set( 50 , 50 );// Higher for smaller tiles
	
	var planeGeo = new THREE.PlaneGeometry(100000, 100000, 1, 10);
	
	var ground = new THREE.Mesh( planeGeo, new THREE.MeshBasicMaterial({
		 map: planeTex
	}));
	
	//ground .rotation.x = -Math.PI / 2;
	ground .position.y = 0;
	ground .receiveShadow = true;
	ground.doubleSided = true;
	scene.add( ground );
	
};


/**	@Name:	Skybox
	@Brief:	Create the skybox
	@Arguments:N/A
	@Returns:N/A
*/
function Skybox(){
 	
	var urlPrefix	= "../img/";
	var urls = [ urlPrefix + "x.png", urlPrefix + "-x.png",
			urlPrefix + "y.png", urlPrefix + "-y.png",
			urlPrefix + "z.png", urlPrefix + "-z.png" ];
	var textureCube	= THREE.ImageUtils.loadTextureCube( urls );
	textureCube.needsUpdate = true;
	
	var shader	= THREE.ShaderUtils.lib["cube"];
	shader.uniforms["tCube"].texture = textureCube;
	var material = new THREE.ShaderMaterial({
		
		fragmentShader	: shader.fragmentShader,
		vertexShader	: shader.vertexShader,
		uniforms	: shader.uniforms
	});

	skyboxMesh	= new THREE.Mesh( new THREE.CubeGeometry( 100000, 100000, 100000, 1, 1, 1, null, true ), material );
	skyboxMesh.doubleSided = true;
	
	scene.add( skyboxMesh );	
	
};
 

/**	@Name:	Example code
	@Brief:	Code snippets that I used for testing.
	@Arguments:N/A
	@Returns:N/A
*/
 function ExampleCode(){
 
	
 };

 
/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
function sendData(  ) {  
	
	console.log( "The key from the cookie is : "+level_Manager.getPlayer()._userKey );
	// Send a template to the server to store. This is fine.
	socket.emit('registerMeInServer', { 

		userKey	: level_Manager.getPlayer()._userKey,
		pos : level_Manager.getPlayer()._position,
		kinect : level_Manager.getPlayer()._kinectData
	});	
}; //end func. 


/**	@Name:	Load
	@Brief:	Called when the window loads
	@Arguments:N/A
	@Returns:N/A
*/
function load() {  
	init(); 
};


/**	@Name:	Random Range
	@Brief:	A helper function to get a value between the arguments.
	@Arguments: int min, int max
	@Returns: int random value
*/
function randomRange(min, max) {
	return Math.random()*(max-min) + min;
};


/**	@Name:	Handle Key Events.
	@Brief:	Called from the dom's mouse down event listener.
	@Arguments: event object.
	@Returns:N/A
*/
function handleKeyEvents( event ) {
	
	var key = event.keyCode;
	var update = true;
	
	switch( key ){
		
		case 38:// UP ARROW	
	  		level_Manager.getPlayer().rotateUp();
	  		break;
		case 40:// DOWN ARROW
			// Rotate Down
			level_Manager.getPlayer().rotateDown();
	  		break;
		case 37:// LEFT ARROW
	  		// Strafe Left
	  		break;
		case 39:// RIGHT ARROW
	  		// Strafe Right
	  		break;
		case 65:// A
	  		// Rotate Left
			level_Manager.getPlayer().rotateModelLeft();
	  		break;
		case 68:// D
	  		// Rotate Right
			level_Manager.getPlayer().rotateModelRight();
	  		break;
		case 87:// W
	  		// Rotate up
			// Move Forward
			level_Manager.getPlayer().moveModel( +1 );
	  		break;
		case 83:// S
	  		// Move Back
			level_Manager.getPlayer().moveModel( -1 );
	  		break;
		case 88:
			level_Manager.getPlayer().removeInventory();
	  		break;
		case 97:// Num pad 1. First Person.
			var cameraType = 1;
			paused = true;
			level_Manager.setCameraType( cameraType );
	  		break;
		case 98:// Num pad 2. Test.
			level_Manager._player_Manager.playGame();
	  		break;
		case 99:// Num pad 3. Third Person.
			var cameraType = 3;
			paused = false;
			level_Manager.setCameraType( cameraType );
	  		break;
		default:
			update = false;
	  		return;		
	}
};


/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
socket.on( 'nextLevel', function( data ){

	window.parent.location.href = data;
});


/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
sandra.on( 'returnData', function( dataR ){
	
	var data = JSON.parse( dataR );	
	if( data[ 'head' ] != undefined && data[ 'head' ] != null ){
		level_Manager.getPlayer()._kinectData = data;		
	}	
});
			
			
/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
socket.on( 'teamMates', function( team ){
	
	// Works fine.
	globalTeam = team;
	
	//Need to figure out when to delete unused players from other players.
	// ...and when to start the game.
	if ( level_Manager._player_Manager._playGame ){
		createTeam();
	}
});


/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
socket.on( 'error', function( data ){

	console.log( data.error );

});


/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
socket.on( 'youWereCreated', function( data ){

	level_Manager.getPlayer()._name = data.name;
	level_Manager.getPlayer()._ip = data.ip;
	level_Manager.getPlayer()._team = data.team;
	level_Manager.getPlayer()._userKey = data.userKey;
	level_Manager.getPlayer()._image.src = data.connectedImageUrl;
	var pos = new THREE.Vector3();

	switch( data.teamNumber ){
		case 1:
			pos.copy( level_Manager._player_Manager._spawnPosition );
			level_Manager.getPlayer().rotateSightByAngle( 180 );
			level_Manager.getPlayer()._startingPos.copy( level_Manager._player_Manager._spawnPosition );
			break;
		case 2:
			pos.copy( level_Manager._player_Manager._spawnPosition2 );
			level_Manager.getPlayer().rotateSightByAngle( 90 );
			level_Manager.getPlayer()._startingPos.copy( level_Manager._player_Manager._spawnPosition2 );
			break;
		case 3:
			pos.copy( level_Manager._player_Manager._spawnPosition3 );
			level_Manager.getPlayer().rotateSightByAngle( 90 );
			level_Manager.getPlayer()._startingPos.copy( level_Manager._player_Manager._spawnPosition3 );
			break;
		case 4:
			pos.copy( level_Manager._player_Manager._spawnPosition4 );
			level_Manager.getPlayer()._startingPos.copy( level_Manager._player_Manager._spawnPosition4 );
			break;
	};
	
	level_Manager.getPlayer().setPosition( pos ) 
});


/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
socket.on( 'deleteUser', function( data ){

	var player = data.user;
	
	// Find the player with the same team number and delete his ass.
	for ( i in level_Manager._player_Manager._otherPlayers ){
	
		if( String( level_Manager._player_Manager._otherPlayers[ i ]._ip ) == String ( player.ip ) ){
		
			level_Manager._player_Manager._otherPlayers[ i ].remove();
			level_Manager._player_Manager._otherPlayers[ i ].setPosition( new THREE.Vector3( 0,0,0 ) );
		}	
	}
});


socket.on( 'pause' , function( value ) {
	
	paused = value;
});


/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
socket.on( 'groupScore', function( data ){
	
	waiting = true;
	level_Manager._player_Manager._gameOver = true;
	groupScore = data.score;
	winner = data.winner;
	setTimeout( function(){ socket.emit( 'endLevel', { level : currentLevel } ) } , 5000 );
});


/**	@Name:	Resize
	@Brief:	Called from the dom's resize listener.
	@Arguments:N/A
	@Returns:N/A
*/
function resize(){
    
	camera.aspect = window.innerWidth / window.innerHeight;
    
    // adjust the FOV
    camera.fov = ( 360 / Math.PI ) * Math.atan( tanFOV * ( window.innerHeight / windowHeight ) );
    
    camera.updateProjectionMatrix();
    camera.lookAt( scene.position );

    renderer.setSize( window.innerWidth, window.innerHeight );
	
    // Redraw 
    render();
};


window.addEventListener('resize', resize, false);
window.addEventListener('orientationchange', resize, false);
//window.addEventListener( 'mousemove', handleMouseEvents, false );
window.addEventListener( 'keydown', handleKeyEvents, false );
// Tell me when the window loads!
window.onload = load;

  


