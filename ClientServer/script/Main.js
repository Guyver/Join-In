/**	@Name:	Main
	@Author: James Browne
	@Brief:	 Hugging Level
	Where the game logic is controlled.
*/

// Connect to the server.
var socket = io.connect( '127.0.0.1:7541' );

// Connect to the new Kinect SDK.
var kinect = new WebSocket("ws://localhost:7540/KinectHtml5");
var kinectData;
var status;
var jsonObject;
 // Connection established.
 
kinect.onopen = function () 
{
    status = "Connection successful.";
};

// Connection closed.
kinect.onclose = function () 
{
     status = "Connection closed.";
};

// Receive data FROM the server!
kinect.onmessage = function (evt) 
{	
    status = "Kinect data received.";
	if( !g_levelOnePlayed  )return;
	
	var data = {};
    // Get the data in JSON format.
    jsonObject = eval('(' + evt.data + ')'); 
 
    // Display the skeleton joints.
    for (var i = 0; i < jsonObject.skeletons.length; i++) 
	{
        for (var j = 0; j < jsonObject.skeletons[i].joints.length; j++) 
		{
            var joint = jsonObject.skeletons[i].joints[j];
			data[ joint.name] = joint;
		}
    }
	
	level_Manager.getPlayer()._kinectData = data;
	
    // Inform the server about the update.
    kinect.send("Skeleton updated on: " + (new Date()).toDateString() + ", " + (new Date()).toTimeString());
};

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
var numJoints, model, g_jointList;

// Game Physics vars TODO: Move to player manager.
var players, globalTeam;

// The time since last frame.
var deltaTime, last, current, countDown;
var sounds = [];

var waiting = false;
var paused = false;
var key = false;
// The level manager.
var level_Manager;

// Game objects.
var objects = [];

// The scene builder.
var scene_manager;


/**	@Name:	Init
	@Brief:	Initalise objects we need.
	@Arguments:N/A
	@Returns:N/A
*/
function init()
{		
	// Set up the renderer type.
	initRenderer();	
	
	// Set up the lights.
	setupLights();
	
	// Set up the camera.
	initCamera();
	
	// Audio
	initSound();
	
	window.setInterval( 'gameLoop()', 1/60 );
};


function initSound()
{	
	sounds[0] = document.getElementById( 'ambient' ) ;
	sounds[1] = document.getElementById( 'hug' ) ;
	sounds[2] = document.getElementById( 'error' );
};


/**	@Name:	Init Camera
	@Brief:	Initalise camera objects we need.
	@Arguments:N/A
	@Returns:N/A
*/
function initCamera()
{
	nearClip = 1;
	farClip = 1000000000;
	fov = 70;
	aspectRatio = window.innerWidth / window.innerHeight;
	camera = new THREE.PerspectiveCamera( fov, aspectRatio, nearClip, farClip );
	camera.position.y = 150;
	camera.position.z = 2000;
	// Will be used to rescale the view frustrum on window resize...
	tanFOV = Math.tan( ( ( Math.PI / 180 ) * camera.fov / 2 ) );
	scene.add( camera );
};


/**	@Name:	Init Render
	@Brief:	Initalise the renderer and add it to the Html page.
	@Arguments:N/A
	@Returns:N/A
*/
function initRenderer()
{
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
function setupLights()
{
	var ambient = new THREE.AmbientLight( 0xffffff );
	//ambient.castShadow = true;
	//ambient.shadowDarkness = 0.5;
	scene.add( ambient );

	directionalLight = new THREE.DirectionalLight( 0xffffff );
	directionalLight.position.y = -70;
	directionalLight.position.z = 100;
	directionalLight.position.normalize();
	//directionalLight.castShadow = true;
	//directionalLight.shadowDarkness = 0.5;
	scene.add( directionalLight );

	pointLight = new THREE.PointLight( 0xffaa00 );
	pointLight.position.x = 0;
	pointLight.position.y = 0;
	pointLight.position.z = 0;
	//pointLight.castShadow = true;
	//pointLight.shadowDarkness = 0.5;
	scene.add( pointLight );
				
	// Directional
	var directionalLight = new THREE.DirectionalLight( 0xffffff );
	directionalLight.position.x = 500;
	directionalLight.position.y = 500;
	directionalLight.position.z = 0;
	directionalLight.position.normalize();
	//directionalLight.castShadow = true;
	//directionalLight.shadowDarkness = 0.5;
	// Add to the scene.
	scene.add( directionalLight );

	// Another directional...
	var directionalLight = new THREE.DirectionalLight( 0xffffff );
	directionalLight.position.x = 3000;
	directionalLight.position.y = 500;
	directionalLight.position.z = -2000;
	directionalLight.position.normalize();
	//directionalLight.castShadow = true;
	//directionalLight.shadowDarkness = 0.5;
	// Add to the scene.
	scene.add( directionalLight );
	
};


/**	@Name:	Game Loop
	@Brief:	This is the loop we call per frame to update the game.
	@Arguments:N/A
	@Returns:N/A
*/
function gameLoop()
{	
	//connectToLocalSandraServer();
	
	resetAudio();
	
	// Take the current time
	g_currentTime = new Date();
	
	// delta time / fps
	g_deltaTime = g_currentTime.getTime() - g_lastTime.getTime();
	g_fps = g_deltaTime / 1000;
	
	// Reset last time to now.
	g_lastTime = g_currentTime;	
	
	if( !g_modelsLoaded )return;// Waiting for the models to load.
	
	if( !g_imagesLoaded ) return;// Waiting for the images to load.
	
	if( g_introPlayed ) // Intro video
	{		
		if( g_levelOnePlayed  )
		{	
			if( g_level == 1 )updateLevelOne( );
			
			if( g_levelTwoPlayed )
			{				
				// Can do level 2 updating logic in here...
				if( g_level == 2 )updateLevelTwo( );
				
				if( g_levelThreePlayed )
				{									
					// Can do level 3 updating logic in here...
					if( g_level == 3 )updateLevelThree( );
				}
				else
				{
					if( !g_levelThreePlaying && g_level == 3)
					{
						// Play level one tutorial...
						LEVEL_THREE._playTutorial();
					}
					else
					{
						// Can do some loading here
					}
				}
			}
			else
			{
				if( !g_levelTwoPlaying && g_level == 2)
				{
					// Play level one tutorial...
					LEVEL_TWO._playTutorial();
				}
				else
				{
					// Can do some loading here
				}
			}
		}
		else
		{
			if( !g_levelOnePlaying && g_level == 1 )
			{
				// Play level one tutorial...
				LEVEL_ONE._playTutorial();
			}
			else
			{
				// Can do some loading here
			}
		}
	}
	else
	{	
		if( !g_introPlaying )
		{
			// Play intro here...
			g_intro.width = window.innerWidth;
			g_intro.height = window.innerHeight;
			g_intro.play();
			g_introPlaying = true;
			
		}
		else
		{
			if( g_intro.ended )
			{		
				// Signal that the video is finished...
				g_introPlayed = true;
				// Clear the video canvas.
				g_canvasVideo.width = window.innerWidth;
				g_canvasVideo.height = window.innerHeight;
			}
			else
			{
				// Draw the video.
				g_ctxVideo.drawImage( g_intro, 0, 0, window.innerWidth, window.innerHeight);
			}
		}
	}	
	
	
	if( !g_playersLoaded )// Load the players
	{		
		// Initalise the level manager.
		level_Manager = new Level_Manager();
		// Get the player key from storage.
		level_Manager.getPlayer()._userKey = localStorage.userKey;
		level_Manager.getPlayer().sendData();
		g_playersLoaded = true;
		fadeOut();		
	}	
	
	// Load the next level and build the world while playing the level tutorial.	
	if( g_currentLevelFinished )
	{
		fadeIn();
		level_Manager.loadNextLevel();
		g_waiting = false;
		fadeOut();
	}
	
	checkPaused();
};


function updateLevelOne( fps )
{
	if( !g_beginTime )
	{
		g_beginTime = new Date();
	}
		

	// Update the level manager.
	level_Manager.update( scene, camera );	
	g_key.rotation.x += 1 * g_fps;		
		
	// Render the scene.
	render();
		
	if( g_gotKey && !g_waiting )
	{
		console.log( "Calling endLevel" );
		socket.emit( 'endLevel' );
		g_waiting = true;
	}
};


function updateLevelTwo( fps )
{
	level_Manager.update( scene, camera );
	render();
	
	if( g_objectsCollected && !g_waiting )
	{
		console.log( "Calling endLevel" );
		socket.emit( 'endLevel' );
		g_waiting = true;
	}
};


function updateLevelThree( fps )
{
	if( !g_endTime )
	{
		g_endTime = new Date();
		// Total time in milliseconds.
		var milliseconds = g_beginTime.getTime() - g_endTime.getTime();
		g_totalTime = parseInt(milliseconds / 1000)+' sec';
	}
	
	level_Manager.update( scene, camera );
	render();
	
	if(  g_timesUp && !g_waiting )
	{
		console.log( "Calling endLevel" );
		socket.emit( 'endLevel' );
		g_waiting = true;
	}
};


function createTeam()
{
	
	var team = globalTeam;
	var offset = 0;
	
	// Pair the team members to the empty players in other players.
	for ( i in team )
	{
		if( i != level_Manager._player_Manager.getPlayer()._ip )
		{
			for ( j in level_Manager._player_Manager._otherPlayers )
			{			
				var index =  ( parseInt( j ) + offset ).toString() ;
				level_Manager._player_Manager._otherPlayers[ index ]._name = team[ i ].name;
				level_Manager._player_Manager._otherPlayers[ index ]._image.src = team[ i ].connectedImageUrl;
				level_Manager._player_Manager._otherPlayers[ index ]._ip = team[ i ].ip;
				level_Manager._player_Manager._otherPlayers[ index ]._userKey = team[ i ].userKey;
				
				if( team[ i ].kinect != undefined && team[ i ].kinect != null )
				{
					level_Manager._player_Manager._otherPlayers[ index ]._kinectData = team[ i ].kinect;
				}
				else
				{
					console.log( "The kinect data was null or undefined." );
				}
				
				level_Manager._player_Manager._otherPlayers[ index ]._score = team[ i ].score;
				level_Manager._player_Manager._otherPlayers[ index ]._antiquesCollected = team[ i ].antiques;
				level_Manager._player_Manager._otherPlayers[ index ]._hugCount = team[ i ].hugs;
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
	}
	
	// Set the game to start after the players have been assigned.
	level_Manager._player_Manager._playGame = true;	
};


function connectToLocalSandraServer()
{
	// Try reconnecting.
	if( !sandra.socket.connected )
	{
		try
		{
			sandra = undefined;
			sandra = io.connect( '127.0.0.1:8080' );	
		}
		catch( error )
		{
			console.log( "Failed to initalise Sandra socket." );
		}
	}
};	


function resetAudio()
{
	// Reset Audio.
	for ( i in sounds )
	{
		if( sounds[ i ].ended )
		{
			// Reload if finished.
			sounds[ i ].load();
		}
	}
};


function checkPaused()
{
	//
	// Paused logic
	//
	if( !paused )
	{
		// Package your player into a map.
		var me = {
			name : level_Manager.getPlayer()._name,
			pos : level_Manager.getPlayer()._position,
			score : level_Manager.getPlayer()._score,
			antiques : level_Manager.getPlayer()._antiquesCollected,
			hugs : level_Manager.getPlayer()._hugCount,
			sight : level_Manager.getPlayer()._sightNode,
			kinect : level_Manager.getPlayer()._rig._translatedMap,	
			team : level_Manager.getPlayer()._team,
			userKey : level_Manager.getPlayer()._userKey
		};
			
		// Send it to the server to be stored for others.
		socket.emit( 'updateMe', me  );		
	}
	else
	{

	}	
};


/**	@Name:	Render 
	@Brief:	Draw the scene.
	@Arguments:N/A
	@Returns:N/A
*/
function render()
{
	
	renderer.render( scene, camera );
	
	// GUI Stuff
	guiCanvas.width = guiCanvas.width;
	// Set the font and size.
	guiCtx.font = 'italic 40px Calibri';
	guiCtx.shadowOffsetX = 5;
	guiCtx.shadowOffsetY = 5;
	guiCtx.shadowBlur    = 4;
	guiCtx.shadowColor   = 'rgba( 155, 155, 155, 0.5)';
	guiCtx.fillStyle     = 'rgba(255, 255, 255, 0.5)';
	guiCtx.fillRect( 5, 5, 255, 255 );
	guiCtx.fillRect( 270, 5, 255, 255 );
	guiCtx.fillRect( 535, 5, 255, 255 );
	guiCtx.fillRect( 800, 5, 255, 255 );
	guiCtx.fillRect( 1065, 5, 510, 255 );
	guiCtx.fillStyle     = '#000';
	
	if( g_playersLoaded )
	{
	
		// Player 1 data
		guiCtx.drawImage( level_Manager._player_Manager.getPlayer()._image, 10 ,10 , 128, 128 );
		guiCtx.fillText( level_Manager._player_Manager.getPlayer()._name, 10, 180 );
		guiCtx.fillText( 'Score : '+ level_Manager._player_Manager.getPlayer()._score, 10, 210 );
		if( g_level == 1 )guiCtx.fillText( 'Hugs : '+ level_Manager._player_Manager.getPlayer()._hugCount, 10, 240 );
		else if( g_level == 2 )guiCtx.fillText( 'Collected : '+ level_Manager._player_Manager.getPlayer()._antiquesCollected, 10, 240 );
		//else if( g_level == 3 )guiCtx.fillText( 'Total Time : '+ level_Manager._player_Manager.getPlayer()._hugCount, 10, 240 );
		
		// Player 2 data
		guiCtx.drawImage( level_Manager._player_Manager._otherPlayers[0]._image, 275 ,10 , 128, 128 );
		guiCtx.fillText( level_Manager._player_Manager._otherPlayers[0]._name, 275, 180 );
		guiCtx.fillText( 'Score : '+ level_Manager._player_Manager._otherPlayers[0]._score, 275, 210 );
		if( g_level == 1 )guiCtx.fillText( 'Hugs : '+ level_Manager._player_Manager._otherPlayers[0]._hugCount, 275, 240 );
		else if( g_level == 2 )guiCtx.fillText( 'Collected : '+ level_Manager._player_Manager._otherPlayers[0]._antiquesCollected, 275, 240 );
		//else if( g_level == 3 )guiCtx.fillText( 'Total Time : '+ level_Manager._player_Manager._otherPlayers[0]._hugCount, 275, 240 );
		
		// Player 3 data
		guiCtx.drawImage( level_Manager._player_Manager._otherPlayers[1]._image, 540 ,10 , 128, 128 );
		guiCtx.fillText( level_Manager._player_Manager._otherPlayers[1]._name,  540, 180 );
		guiCtx.fillText( 'Score : '+ level_Manager._player_Manager._otherPlayers[1]._score,  540, 210 );
		if( g_level == 1 )guiCtx.fillText( 'Hugs : '+ level_Manager._player_Manager._otherPlayers[1]._hugCount,540, 240 );
		else if( g_level == 2 )guiCtx.fillText( 'Collected : '+ level_Manager._player_Manager._otherPlayers[1]._antiquesCollected,540, 240 );
		//else if( g_level == 3 )guiCtx.fillText( 'Total Time : '+ level_Manager._player_Manager._otherPlayers[1]._hugCount,540, 240 );
		
		// Player 4 data
		guiCtx.drawImage( level_Manager._player_Manager._otherPlayers[2]._image, 805 ,10 , 128, 128 );
		guiCtx.fillText( level_Manager._player_Manager._otherPlayers[2]._name, 805, 180 );
		guiCtx.fillText( 'Score : '+ level_Manager._player_Manager._otherPlayers[2]._score, 805, 210 );		
		if( g_level == 1 )guiCtx.fillText( 'Hugs : '+ level_Manager._player_Manager._otherPlayers[2]._hugCount, 805, 240 );
		else if( g_level == 2 )guiCtx.fillText( 'Collected : '+ level_Manager._player_Manager._otherPlayers[2]._antiquesCollected, 805, 240 );
		//else if( g_level == 3 )guiCtx.fillText( 'Total Time : '+ level_Manager._player_Manager._otherPlayers[2]._hugCount, 805, 240 );
		
		// Level progression data.
				
		if( g_level == 1 )
		{
			guiCtx.drawImage( image_loader.getAsset( 'key.png' ), 1070 ,10 , 256, 128 );
			guiCtx.fillText( 'Level Goal : Collect the Key', 1070, 210 );
		}
		else if( g_level == 2 )
		{
			guiCtx.drawImage( image_loader.getAsset( 'ming.jpg' ), 1070 ,10 , 256, 128 );
			guiCtx.fillText( 'Level Goal : Collect Antiques', 1070, 180 );
			guiCtx.fillText( level_Manager._player_Manager.getPlayer()._antiquesCollected+' out of 9 collected.', 1070, 210 );
			
		}
		else if( g_level == 3 )
		{
			guiCtx.drawImage( image_loader.getAsset( 'congrats.png' ), 1070 ,10 , 256, 128 );
			guiCtx.fillText( 'Level Goal : No goal!', 1070, 210 );
			guiCtx.fillText( 'Time taken : '+ g_totalTime, 1070, 292 );
		}
	}
};
 




/**	@Name:	Handle Key Events.
	@Brief:	Called from the dom's mouse down event listener.
	@Arguments: event object.
	@Returns:N/A
*/
function handleKeyEvents( event ) 
{
	
	var key = event.keyCode;
	var update = true;
	
	switch( key ){
		
		case 38:// UP ARROW	
	  		fadeIn();
	  		break;
		case 40:// DOWN ARROW
			// Rotate Down
			fadeOut();
	  		break;
		case 37:// LEFT ARROW
			//sounds[0].loop = true;
			//sounds[0].play();
			level_Manager._scene_manager.destroyCurrentScene();
	  		// Strafe Left
	  		break;
		case 39:// RIGHT ARROW
			//sounds[1].play();
			//g_gotKey = true;
			g_currentLevelFinished = true;
			//window.open("http://www.w3schools.com");
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
socket.on( 'nextLevel', function( data )
{
	 g_currentLevelFinished = true;
});

		
			
/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
socket.on( 'teamMates', function( team )
{	
	// Works fine.
	globalTeam = team;
	
	createTeam();

});


/**	@Name:	
	@Brief:	
	@Arguments:N/A
	@Returns:N/A
*/
socket.on( 'youWereCreated', function( data )
{

	level_Manager.getPlayer()._name = data.name;
	level_Manager.getPlayer()._ip = data.ip;
	level_Manager.getPlayer()._team = data.team;
	level_Manager.getPlayer()._userKey = data.userKey;
	level_Manager.getPlayer()._image.src = data.connectedImageUrl;
	var pos = new THREE.Vector3();
	var x,z;

	switch( data.teamNumber )
	{
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
socket.on( 'deleteUser', function( data )
{

	var player = data.user;
	
	// Find the player with the same team number and delete his ass.
	for ( i in level_Manager._player_Manager._otherPlayers )
	{	
		if( String( level_Manager._player_Manager._otherPlayers[ i ]._ip ) == String ( player.ip ) )
		{		
			level_Manager._player_Manager._otherPlayers[ i ].remove();
			level_Manager._player_Manager._otherPlayers[ i ].setPosition( new THREE.Vector3( 0,0,0 ) );
		}	
	}
});


/**	@Name: Distance
	@Brief:	
	@Arguments: 2 3d Vectors
	@Returns:
*/
function getDistance3D( objA, objB )
{

		var dx,dy,dz,x2,y2,z2,dist;
		// Deltas.
		dx = objA.x - objB.x;
		dy = objA.y - objB.y;
		dz = objA.z - objB.z;
		// Squares.
		x2 = dx * dx;
		y2 = dy * dy;
		z2 = dz * dz;
		// Root
		return ( Math.sqrt( x2 + y2 + z2 ) );
};


/**	@Name:	Resize
	@Brief:	Called from the dom's resize listener.
	@Arguments:N/A
	@Returns:N/A
*/
function resize()
{
    
	camera.aspect = window.innerWidth / window.innerHeight;
    
    // adjust the FOV
    camera.fov = ( 360 / Math.PI ) * Math.atan( tanFOV * ( window.innerHeight / windowHeight ) );
    
    camera.updateProjectionMatrix();
    camera.lookAt( scene.position );

    renderer.setSize( window.innerWidth, window.innerHeight );
	guiCanvas.width = window.innerWidth;
	guiCanvas.height = window.innerHeight;
	g_canvasCurtain.width = window.innerWidth;
	g_canvasCurtain.height = window.innerHeight;
    // Redraw 
    render();
};

window.addEventListener('resize', resize, false);
window.addEventListener('orientationchange', resize, false);
//window.addEventListener( 'mousemove', handleMouseEvents, false );
window.addEventListener( 'keydown', handleKeyEvents, false );
// Tell me when the window loads!
window.onload = init;
