/**	@Name:	Main

	@Author: James Browne
	
	@Brief:
	Where the game logic is controlled.
	
	* Add to players.
	* Will collision detection be done client side or server side?
	* Will the timing events be server side or client side?
	* Will the game flow logic be client or server side?

*/

// Connect to the server.
var socket = io.connect('193.156.105.166:7541');

// Connect to Sandra.
//var local = io.connect('127.0.0.0.1:7540');

// Connect to the social media Api. 
//var socket = io.connect('127.0.0.0.1:7541');

// Variables for the sugary goodness!
var gui, param, varNum, interval;

// Three.js vars
var scene, renderer, mesh, geometry, material;

// Camera vars, initalised in "initCamera()"
var camera, nearClip, farClip, aspectRatio, fov;

// remember these initial values
var tanFOV ;
var windowHeight = window.innerHeight;

// Kinect data
var numJoints, model, jointList;

// Game Physics vars
var player, players;

// The time since last frame.
var deltaTime, last, current;

var level_Manager = new Level_Manager();

var objects = [];

// The fat model...
var jointModels = [];
var head, torso, upperArmL, upperArmR, lowerArmL, lowerArmR, handL, handR, upperLegL, upperLegR, lowerLegL, lowerLegR, footL, footR;

var imgContainer;

var architect;
// Debugging Variables.
var skin,skin2,model2;
var test;
var loop = 0;
var server =0;


/**	@Name:	Init
	@Brief:	Initalise objects we need.
	@Arguments:N/A
	@Returns:N/A
*/
function init(){
	
	// Loop until the image manager is finished loading.
	while( !imageManager.isDone() ){
		
		test = 0;
	}
	
	container = document.createElement( 'div' );
	document.body.appendChild( container );

	var info = document.createElement( 'div' );
	info.style.position = 'absolute';
	info.style.top = '10px';
	info.style.width = '100%';
	info.style.textAlign = 'center';
	info.innerHTML = 'Use A and D to rotate and Up and Down to move forward and backward.';
	container.appendChild( info );

	
	// Set up the three scene.
	initScene();
	// Set up the renderer type.
	initRenderer();	
	// Set up the lights.
	setupLights();
	// Set up the camera.
	initCamera();
	// Create the game objects.
	createObjects();
	// Gui stuff.
	setupGui();
	// Skybox...etc.
	setupEnviornment();
	// Request players from the server.
	getPlayers();
	// Send the server your data.
	sendData();
	// Test code is stuffed in here.
	ExampleCode();
	// Initalise the game loop to 60fps. Anim frame pffft
	interval = setInterval( 'gameLoop()', 1000 / 60 );

}




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
}




/**	@Name:	Init Scene
	@Brief:	Initalise the Three.js Scene
	@Arguments:N/A
	@Returns:N/A
*/
function initScene(){
	
	// the scene contains all the 3D object data
	scene = new THREE.Scene();	
}




/**	@Name:	Init Render
	@Brief:	Initalise the renderer and add it to the Html page.
	@Arguments:N/A
	@Returns:N/A
*/
function initRenderer(){
	
	/*renderer = new THREE.CanvasRenderer();*/
	
	renderer = new THREE.WebGLRenderer({
			antialias: true,
			canvas: document.createElement( 'canvas' ),
			clearColor: 0x000000,
			clearAlpha: 0,
			maxLights: 4,
			stencil: true,
			preserveDrawingBuffer: false
	});
	
	// Fit the render area into the window.
	renderer.setSize( window.innerWidth, window.innerHeight );
	
	// The renderer's canvas domElement is added to the body.
	document.body.appendChild( renderer.domElement );
}




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
	scene.add( directionalLight );/**/
}




/**	@Name:	Create Objects.
	@Brief:	Create the games objects like the Player.
	@Arguments:N/A
	@Returns:N/A
*/
function createObjects(){
	

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
				
	player = new Player( "Default", new THREE.Vector3( 9000 , 100 ,9000 ) );
	
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
			
	architect = new Scene_Builder( my_scene );
	
	for ( var i = 0; i < 5; i++ ){
	
		//objects.push( new Object( new THREE.Vector3( 1000*i, 1500, 5000 ), "Object" ) );	
	}		
	
	for ( var i = 0; i < 2; i++ ){
	
		//objects.push( new Object( new THREE.Vector3( 2000*i, 250, 2000 ), "Bin" ) );	
	}	
}

var attached = false;
var needsScale = true;
var scaleX,scaleY,scaleZ = 50;

/**	@Name:	Game Loop
	@Brief:	This is the loop we call per frame to update the game.
	@Arguments:N/A
	@Returns:N/A
*/
function gameLoop(){
	
	player.update();
	
	// Update all the players.
	for ( each_player in players ){
		
		players[ each_player ].update();	
	}
	
	// Update the objects.
	for( each_object in objects ){
	
		objects[ each_object ].update();
	}
	
	// Test player wall collisions...
	//level_Manager.testCollision( player._mesh, scene );
	
	level_Manager.update( player, objects, camera );
	
	// Set the camera Z to the gui for debugging!
	camera.position.x = param['camera_X'];
	camera.position.y = param['camera_Y'];
	camera.position.z = param['camera_Z'];
	
	// Look at the Player.
	this.camera.lookAt( player.getPosition()  );	
	
	//
	// Do the scaling before the positioning otherwise it will be off.
	//	
	if( needsScale ){
		
		for ( each in jointModels ){
	
			jointModels[ each ].scale.set( scaleX, scaleY, scaleZ );
			jointModels[ each ].updateMatrix();
		}
		needsScale = false;
	}
	
	// 
	// Set the positions of the models each frame.
	//	
	if ( player._kinectData != undefined && player._kinectData != null ){
	
		head.position 		= 	player._rig._joint["neck"].getPosition();
		torso.position 		= 	player._rig._joint["torso"].getPosition();
		upperArmL.position 	= 	player._rig._joint["leftShoulder"].getPosition();
		upperArmR.position 	= 	player._rig._joint["rightShoulder"].getPosition();
		lowerArmL.position 	= 	player._rig._joint["leftElbow"].getPosition();
		lowerArmR.position 	= 	player._rig._joint["rightElbow"].getPosition();
		handL.position 		= 	player._rig._joint["leftHand"].getPosition();
		handR.position     	= 	player._rig._joint["rightHand"].getPosition();
		upperLegL.position 	= 	player._rig._joint["leftHip"].getPosition();
		upperLegR.position 	= 	player._rig._joint["rightHip"].getPosition();
		lowerLegL.position 	= 	player._rig._joint["leftKnee"].getPosition();
		lowerLegR.position 	= 	player._rig._joint["rightKnee"].getPosition();
		footL.position 		= 	player._rig._joint["leftFoot"].getPosition();
		footR.position 		= 	player._rig._joint["rightFoot"].getPosition();
		
		
		//
		// Set the orientation of the models each frame.
		//
		head.lookAt(  player.getSightNode()  );
		torso.lookAt( player.getSightNode() );
		footL.lookAt( player.getSightNode() );
		footR.lookAt( player.getSightNode() );
		handL.lookAt( player.getSightNode() );
		handR.lookAt( player.getSightNode() );
		upperArmL.lookAt( player._rig._joint["leftElbow"].getPosition() );
		upperArmR.lookAt( player._rig._joint["rightElbow"].getPosition() );
		lowerArmL.lookAt( player._rig._joint["leftHand"].getPosition() );
		lowerArmR.lookAt( player._rig._joint["rightHand"].getPosition() );
		upperLegL.lookAt( player._rig._joint["leftKnee"].getPosition() );
		upperLegR.lookAt( player._rig._joint["rightKnee"].getPosition() );
		lowerLegL.lookAt( player._rig._joint["leftFoot"].getPosition() );
		lowerLegR.lookAt( player._rig._joint["rightFoot"].getPosition() );
	}
	
	// Initalise last for the 1st iteration.
	if(!last)last= new Date();
	
	// Find time now.
	current = new Date();
	
	// Get the change in time, dt.
	deltaTime = current.getTime() - last.getTime();
	
	// reset the last time to time this frame for the next.
	last = current;					
	// Render the scene.
	render();
	
	// 
	//player._sightNode.y = player._rig._joint["torso"].getPosition();
	
	// Get the kinect data for the next frame.
	socket.emit( 'updateKinect' );
}



/**	@Name:	Render 
	@Brief:	Draw the scene.
	@Arguments:N/A
	@Returns:N/A
*/
function render(){

	var x = document.getElementById('string');
	x.value =  1000 / deltaTime + " fps " ;
	//x.value =  camera.position.z;
	
	renderer.render( scene, camera );
}




/**	@Name:	Setup Gui
	@Brief:	Creates the GUI panel on screen and assigns variables to change at run time for debugging
	@Arguments:N/A
	@Returns:N/A
*/
function setupGui(){
	
	// The number of entries/spaces on the GUI
	varNum = 47
	
	// Create the GUI
	gui = new DAT.GUI( { varNum : 5 * 32 - 1} );
	 
		 // Store the list of changable parameters.
	param = {
		 fps:60,
		 parallaxSpeed:2,
		 camera_X:8000,		// 0
		 camera_Y:5000,
		 camera_Z:8000,
		 Head_X:8000,		// 1
		 Head_Y:500,
		 Head_Z:8000,
		 UpperArmL_X:8000,	// 2
		 UpperArmL_Y:1500,
		 UpperArmL_Z:8000,
		 UpperArmR_X:8000,	// 3
		 UpperArmR_Y:500,
		 UpperArmR_Z:8000,
		 ForeArmL_X:8000,	// 4
		 ForeArmL_Y:1500,
		 ForeArmL_Z:8000,
		 ForeArmR_X:8000,	// 5
		 ForeArmR_Y:500,
		 ForeArmR_Z:8000,
		 Torso_X:8000,		// 6
		 Torso_Y:500,
		 Torso_Z:8000,
		 HandL_X:8000,		// 7
		 HandL_Y:500,
		 HandL_Z:8000,
		 HandR_X:8000,		// 8
		 HandR_Y:500,
		 HandR_Z:8000,
		 UpperLegL_X:8000,	// 9
		 UpperLegL_Y:500,
		 UpperLegL_Z:8000,
		 UpperLegR_X:8000,	// 10
		 UpperLegR_Y:500,
		 UpperLegR_Z:8000,
		 LowerLegL_X:8000,	// 11
		 LowerLegL_Y:500,
		 LowerLegL_Z:8000,
		 LowerLegR_X:8000,	// 12
		 LowerLegR_Y:500,
		 LowerLegR_Z:8000,
		 FootL_X:8000,		// 13
		 FootL_Y:500,
		 FootL_Z:8000,
		 FootR_X:8000,		// 14
		 FootR_Y:500,
		 FootR_Z:8000,
	 
	 };
	 
	 // Add the paramater values to the GUI, give it a name, upon change specify the callback function.
	 gui.add( param, 'fps').name('Frame Rate').onFinishChange(function(){
	 
		// Clear the current framerate 
		clearInterval( interval );
		
		// Set it up again using the paramater!
		setInterval( "gameLoop()", 1000/ param['fps']);
	});
	
	 // Add the paramater values to the GUI, give it a name, upon change specify the callback function
	 gui.add( param, 'parallaxSpeed').name('Parallax Speed').min(-5).max(5).step(0.25).onFinishChange(function(){
	 
		// No need to change, the next loop will use the new scroll speed! 
	});
	
	
	// Camera GUI data, no need to call anything on change. It will update on the next tick.
	
	/* Add the paramater values to the GUI, give it a name, set the min and max values 
		inside the clip plane upon change specify the callback function*/
	gui.add( param, 'camera_X').name('Camera_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'camera_Y').name('Camera_Y').min(( 0 ) * -1).max( farClip ).step(100).onFinishChange(function(){
		
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'camera_Z').name('Camera_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		
		
	});
	
	//
	gui.add( param, 'Head_X').name('Head_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		head.position.x = param['Head_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'Head_Y').name('Head_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		head.position.y = param['Head_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'Head_Z').name('Head_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		head.position.z = param['Head_Z'];
		
	});
	
	
	//
	gui.add( param, 'Torso_X').name('Torso_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		torso.position.x = param['Torso_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'Torso_Y').name('Torso_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		torso.position.y = param['Torso_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'Torso_Z').name('Torso_Z').min( 5000 ).max( farClip ).step( 10 ).onFinishChange(function(){
		torso.position.z = param['Torso_Z'];
		
	});
	
	//
	gui.add( param, 'UpperArmL_X').name('UpperArmL_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		upperArmL.position.x = param['UpperArmL_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'UpperArmL_Y').name('UpperArmL_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		upperArmL.position.y = param['UpperArmL_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'UpperArmL_Z').name('UpperArmL_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		upperArmL.position.z = param['UpperArmL_Z'];
		
	});
	
	//
	gui.add( param, 'UpperArmR_X').name('UpperArmR_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		upperArmR.position.x = param['UpperArmR_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'UpperArmR_Y').name('UpperArmR_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		upperArmR.position.y = param['UpperArmR_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'UpperArmR_Z').name('UpperArmR_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		upperArmR.position.z = param['UpperArmR_Z'];
		
	});
	
	//
	gui.add( param, 'ForeArmL_X').name('ForeArmL_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		lowerArmL.position.x = param['ForeArmL_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'ForeArmL_Y').name('ForeArmL_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		lowerArmL.position.y = param['ForeArmL_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'ForeArmL_Z').name('ForeArmL_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		lowerArmL.position.z = param['ForeArmL_Z'];
		
	});
	
	//
	gui.add( param, 'ForeArmR_X').name('ForeArmR_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		lowerArmR.position.x = param['ForeArmR_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'ForeArmR_Y').name('ForeArmR_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		lowerArmR.position.y = param['ForeArmR_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'ForeArmR_Z').name('ForeArmR_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		lowerArmR.position.z = param['ForeArmR_Z'];
		
	});
	
	//
	gui.add( param, 'HandL_X').name('HandL_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		handL.position.x = param['HandL_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'HandL_Y').name('HandL_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		handL.position.y = param['HandL_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'HandL_Z').name('HandL_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		handL.position.z = param['HandL_Z'];
		
	});
	
	//
	gui.add( param, 'HandR_X').name('HandR_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		handR.position.x = param['HandR_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'HandR_Y').name('HandR_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		handR.position.y = param['HandR_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'HandR_Z').name('HandR_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		handR.position.z = param['HandR_Z'];
		
	});
	
	//
	gui.add( param, 'UpperLegL_X').name('UpperLegL_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		upperLegL.position.x = param['UpperLegL_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'UpperLegL_Y').name('UpperLegL_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		upperLegL.position.y = param['UpperLegL_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'UpperLegL_Z').name('UpperLegL_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		upperLegL.position.z = param['UpperLegL_Z'];
		
	});
	
	//
	gui.add( param, 'UpperLegR_X').name('UpperLegR_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		upperLegR.position.x = param['UpperLegR_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'UpperLegR_Y').name('UpperLegR_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		upperLegR.position.y = param['UpperLegR_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'UpperLegR_Z').name('UpperLegR_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		upperLegR.position.z = param['UpperLegR_Z'];
		
	});
	
	//
	gui.add( param, 'FootL_X').name('FootL_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		footL.position.x = param['FootL_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'FootL_Y').name('FootL_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		footL.position.y = param['FootL_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'FootL_Z').name('FootL_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		footL.position.z = param['FootL_Z'];
		
	});
	
	//
	gui.add( param, 'FootR_X').name('FootR_X').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		footR.position.x = param['FootR_X'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'FootR_Y').name('FootR_Y').min( 0 ).max( 5000 ).step( 10 ).onFinishChange(function(){
		footR.position.y = param['FootR_Y'];
		
	});
	
	// Add the paramater values to the GUI, give it a name, upon change specify the callback function
	gui.add( param, 'FootR_Z').name('FootR_Z').min( 5000 ).max( 10000 ).step( 10 ).onFinishChange(function(){
		footR.position.z = param['FootR_Z'];
		
	});	
}



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
	var planeTex = new THREE.Texture(imageManager.getAsset('img/grassTile01.png', {}, render()));
	
	planeTex.needsUpdate = true;
	planeTex.wrapT = THREE.RepeatWrapping;
	planeTex.wrapS = THREE.RepeatWrapping;
	planeTex.repeat.set( 1000, 1000 );// Higher for smaller tiles
	
	var planeGeo = new THREE.PlaneGeometry(100000, 100000, 1, 10);
	
	var ground = new THREE.Mesh( planeGeo, new THREE.MeshBasicMaterial({
		 map: planeTex
	}));
	
	//ground .rotation.x = -Math.PI / 2;
	ground .position.y = 0;
	ground .receiveShadow = true;
	ground.doubleSided = true;
	scene.add( ground );
	
	//
	addTrees();
	
}


/**	@Name:	Skybox
	@Brief:	Create the skybox
	@Arguments:N/A
	@Returns:N/A
*/
function Skybox(){
 	
	var urlPrefix	= "img/";
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
	
 }
 
 
 function addTrees(){
 
	var treeNum = 0;
	
	var treeBill = new THREE.Texture(imageManager.getAsset('img/Tree1.png' , {}, render()));	
		treeBill.needsUpdate = true;
	var treeGeo = new THREE.PlaneGeometry(500, 500, 1, 10);
	
	for( var i =0; i< treeNum;i++ ){
	
		var tree = new THREE.Mesh( treeGeo, new THREE.MeshBasicMaterial({
			 map: treeBill, transparent:true  
		}));
		
		//tree .rotation.x = -Math.PI / 2;
		tree .position.x = randomRange( 100, 15000);
		tree .position.y = 200;
		tree .position.z = randomRange( 100, 5000);
		tree .receiveShadow = true;
		tree .castShadow = true;
		tree.doubleSided = true;
		scene.add( tree );
	
	}
	
 
 };
 
 
 
 function addLionMale( collada ){
 
	var model = collada.scene;
	skin = collada.skins[0];
	model.updateMatrix();
	scene.add( model );
	
	model.name = "LionMale";
	model .castShadow = true;
	model.rotation.x = -Math.PI/2;
	model.position.x = 7500;
	model.position.y -= 0;
	model.position.z = 7500;
	model.scale.set(100,100,100);
 
  
 };
 
 
 function addHouse( collada ){
 
	var model = collada.scene;
	model.updateMatrix();
	scene.add( model );
	
	model.name = "House";
	model .castShadow = true;
	model.position.x = 8000;
	model.position.y -= 20;
	model.position.z = 8000;
	//model.scale.set(50,50,50);
	model.rotation.x = -Math.PI/2;
 
 };
 
 var offsetX =8000;
 var offsetY = 500;
 var offsetZ =8000;
 
 /*
 // The fat model...
var jointModels = [];
var head, torso, upperArmL, upperArmR, lowerArmL, lowerArmR, handL, handR, upperLegL, upperLegR, lowerLegL, lowerLegR, footL, footR;
 
*/

 function head( collada ){
 
	head = collada.scene;
	head.updateMatrix();
	scene.add( head );
	
	head.name = "HEAD";
	head.scale.set(50,50,50);
	head.position = player._rig._joint["HEAD"].getPosition();

	jointModels.push( head );
 
 }
 
 function torso( collada ){
 
	torso = collada.scene;
	torso.updateMatrix();
	scene.add( torso );
	
	torso.name = "TORSO";
	torso.scale.set(20,20,20);
	torso.position = player._rig._joint["TORSO"].getPosition();
	
	jointModels.push( torso );
 
 }
 
 function armUpperL( collada ){
 
	upperArmL = collada.scene;
	upperArmL.updateMatrix();
	scene.add( upperArmL );
	
	upperArmL.name = "ARM_UPPER_L";
	upperArmL.scale.set(50,50,50);
	upperArmL.position = player._rig._joint["LEFT_SHOULDER"].getPosition();
	
	jointModels.push( upperArmL );
 }
 
 function armUpperR ( collada ){
 
	upperArmR = collada.scene;
	upperArmR.updateMatrix();
	scene.add( upperArmR );
	
	upperArmR.name = "ARM_UPPER_R";
	upperArmR.scale.set(50,50,50);
	upperArmR.position = player._rig._joint["RIGHT_SHOULDER"].getPosition();
	
	jointModels.push( upperArmR );
 
 }

 function footL( collada ){
	
	footL = collada.scene;
	footL.updateMatrix();
	scene.add( footL );
	
	footL.name = "FOOT_L";
	footL.scale.set(50,50,50);
	footL.position = player._rig._joint["LEFT_FOOT"].getPosition();
	
	jointModels.push( footL );
 
 }
 
 function footR( collada ){
 
	footR = collada.scene;
	footR.updateMatrix();
	scene.add( footR );
	
	footR.name = "FOOT_R";
	footR.scale.set(50,50,50);
	footR.position = player._rig._joint["RIGHT_FOOT"].getPosition();
	
	jointModels.push( footR );
 
 }

 function foreArmL( collada ){
 
	lowerArmL = collada.scene;
	lowerArmL.updateMatrix();
	scene.add( lowerArmL );
	
	lowerArmL.name = "FOREARM_L";
	lowerArmL.scale.set(50,50,50);
	lowerArmL.position = player._rig._joint["LEFT_ELBOW"].getPosition();
	
	jointModels.push( lowerArmL );
 
 }
 
 function foreArmR( collada ){
 
	lowerArmR = collada.scene;
	lowerArmR.updateMatrix();
	scene.add( lowerArmR );
	
	lowerArmR.name = "FOREARM_R";
	lowerArmR.scale.set(50,50,50);
	lowerArmR.position = player._rig._joint["RIGHT_ELBOW"].getPosition();
	
	jointModels.push( lowerArmR );
 
 }
 
 function legUpperL( collada ){
 
	upperLegL = collada.scene;
	upperLegL.updateMatrix();
	scene.add( upperLegL );
	
	upperLegL.name = "LEG_UPPER_L";
	upperLegL.scale.set(50,50,50);
	upperLegL.position = player._rig._joint["LEFT_HIP"].getPosition();
	
	jointModels.push( upperLegL );
 
 }
 
 function legUpperR( collada ){
 
	upperLegR = collada.scene;
	upperLegR.updateMatrix();
	scene.add( upperLegR );
	
	upperLegR.name = "LEG_UPPER_R";
	upperLegR.scale.set(50,50,50);
	upperLegR.position = player._rig._joint["RIGHT_HIP"].getPosition();
	
	jointModels.push( upperLegR );
 
 }
 
 function legLowerR( collada ){
 
	lowerLegR = collada.scene;
	lowerLegR.updateMatrix();
	scene.add( lowerLegR );
	
	lowerLegR.name = "LEG_LOWER_R";
	lowerLegR.scale.set(50,50,50);
	lowerLegR.position = player._rig._joint["RIGHT_KNEE"].getPosition();
	
	jointModels.push( lowerLegR );
 }
 
 function legLowerL( collada ){
 
	lowerLegL = collada.scene;
	lowerLegL.updateMatrix();
	scene.add( lowerLegL );
	
	lowerLegL.name = "LEG_LOWER_L";
	lowerLegL.scale.set(50,50,50);
	lowerLegL.position = player._rig._joint["LEFT_KNEE"].getPosition();
	
	jointModels.push( lowerLegL );
 
 }
 
 function handR( collada ){
 
	handR = collada.scene;
	handR.updateMatrix();
	scene.add( handR );
	
	handR.name = "HAND_R";
	handR.scale.set(50,50,50);
	handR.position = player._rig._joint["RIGHT_HAND"].getPosition();
	
	jointModels.push( handR );
 }
 
 function handL( collada ){
 
    handL = collada.scene;
	handL.updateMatrix();
	scene.add( handL );
	
	handL.name = "HAND_L";
	handL.scale.set(50,50,50);
	handL.position = player._rig._joint["LEFT_HAND"].getPosition();
	
	jointModels.push( handL );
 
 }
  
 function addModels( collada ){
 
	model = collada.scene;
	model.updateMatrix();
	scene.add( model );
	
	model.scale.set(50,50,50);
	model.position.x = offsetX;
	model.position.y = offsetY;
	model.position.z = offsetZ;
	offsetZ += 1000;
 }
 
 
/**	@Name:	Example code
	@Brief:	Code snippets that I used for testing.
	@Arguments:N/A
	@Returns:N/A
*/
 function ExampleCode(){
 	
	//
	// Create Sky Cube.
	//
/**	
	// Create a texture from an image, image mush be a power of 2 in size. i.e 512*256
	var texture_blue = new THREE.Texture(imageManager.getAsset('img/target_blue.png', {}, render()));
	// Oh yes, it does need this!
	texture_blue.needsUpdate = true;
	
	var geometry = new THREE.CubeGeometry(10000, 10000, 10000);
	
	var texture = new THREE.Mesh(geometry, new THREE.MeshBasicMaterial({
		map: texture_blue
	}));
	texture.doubleSided = true;
	
	scene.add(texture);
*/	

	//
	// Load a model and add it to the scene.
	//
	var url = 'model/limbs/'
	var modelNames = [  "head.dae", "torso.dae", "armUpper_l.dae", "armUpper_r.dae",
						"foot_l.dae", "foot_r.dae", "hand_l.dae","hand_r.dae",
						"foreArm_l.dae", "foreArm_r.dae", "legLower_r.dae","legLower_l.dae",
						"legUpper_l.dae", "legUpper_r.dae"];
	for ( i in modelNames ){
		
		var x = new THREE.ColladaLoader();
		
		switch( modelNames[ i ] ){
			
			case "head.dae":
				x.load( url + modelNames[i] , head );
				break;
			case "torso.dae":
				x.load( url + modelNames[i] , torso );
				break;
			case "armUpper_l.dae":
				x.load( url + modelNames[i] , armUpperL );
				break;
			case "armUpper_r.dae":
				x.load( url + modelNames[i] , armUpperR);
				break;
			case "foot_l.dae":
				x.load( url + modelNames[i] , footL );
				break;
			case "foot_r.dae":
				x.load( url + modelNames[i] , footR );
				break;
			case "hand_l.dae":
				x.load( url + modelNames[i] , handL );
				break;
			case "hand_r.dae":
				x.load( url + modelNames[i] , handR );
				break;
			case "foreArm_l.dae":
				x.load( url + modelNames[i] , foreArmL );
				break;
			case "foreArm_r.dae":
				x.load( url + modelNames[i] , foreArmR );
				break;
			case "legLower_r.dae":
				x.load( url + modelNames[i] , legLowerR );
				break;
			case "legLower_l.dae":
				x.load( url + modelNames[i] , legLowerL );
				break;
			case "legUpper_l.dae":
				x.load( url + modelNames[i] , legUpperL );
				break;
			case "legUpper_r.dae":
				x.load( url + modelNames[i] , legUpperR );
				break;
			default:
				break;
			}
	}


	//
	// Create Plane
	//
/**	
	var planeTex = new THREE.Texture(imageManager.getAsset('img/ground_plane.png', {}, render()));
	
	planeTex.needsUpdate = true;
	
	var planeGeo = new THREE.PlaneGeometry(10000, 10000, 1, 10);
	
	var ground = new THREE.Mesh( planeGeo, new THREE.MeshBasicMaterial({
		 map: planeTex 
	}));
	
	ground .rotation.x = -Math.PI / 2;
	ground .position.y = 0;
	ground .receiveShadow = true;
	ground.doubleSided = true;
	scene.add( ground );
*/	
	
	
 }

 

function getPlayers() {  
	// Request all the players registered in the server.
	socket.emit( 'getPlayers' );
}  




function createPlayers( data ) {  

	players = {};
	// Dont Recreate yourself ffs, thats just stupid.
	
	for( index in data ){
	
		players[ data[ index ].ip ] = new Player(data[ index ].name, data[ index ].pos );
		players[ data[ index ].ip ]._ip = data[ index ].ip;
		players[ data[ index ].ip ]._kinectData = data[ index ].kinect;
	}
}  




function updatePlayers( data ) {  
	
	// Use the ip to match up the data. 
	
	for ( index in data ){
	
		try{
			players[ index ].setPosition( data[index].pos );
		}
		catch( err ){
			console.log("Couldn't find the player with ip address of  : %s", index );
			if( index !== undefined){
				// Create the player.
				players[ index ] = new Player( "", data[index].pos);
			}
		}		
	}// end for.	
} //end func. 




function sendData(  ) {  
		// Send a template to the server to store. This is fine.
	socket.emit('registerMeInServer', { 
		
		name : player._name,
		id	: player._userId,
		pos : player._position,
		kinect : player._kinectData,
		ip : player._ip,
		mesh : player._meshName,
		visible : player._visible
	});	
} //end func. 




function addUser( user ){

	// Create and add the new user.
	players[ user.ip ] = new Player( user.player.name, user.player.pos );
	players[ user.ip ]._ip = user.ip;
}




function removeUser( user ){

	// Delete the user...
	delete players[ user.ip ];

}




/**	@Name:	Load
	@Brief:	Called when the window loads
	@Arguments:N/A
	@Returns:N/A
*/
function load() {  
	init(); 
}  




/**	@Name:	Random Range
	@Brief:	A helper function to get a value between the arguments.
	@Arguments: int min, int max
	@Returns: int random value
*/
function randomRange(min, max) {
	return Math.random()*(max-min) + min;
}



/**	@Name:	Handle Key Events.
	@Brief:	Called from the dom's mouse down event listener.
	@Arguments: event object.
	@Returns:N/A
*/
function handleKeyEvents( event ) {
	
	var key = event.keyCode;
	var update = true;
	
	switch( key ){
		
		case 38:
	  		// Move Forward
			player.moveModel( +1 );
	  		break;
		case 40:
	  		// Move Back
			player.moveModel( -1 );
	  		break;
		case 37:
	  		// Move Left
			player.move( new THREE.Vector3( 0,0,100 ) );
	  		break;
		case 39:
	  		// Move Right
			player.move( new THREE.Vector3( 0,0,-100 ) );
	  		break;
		case 65:
	  		// Rotate Left
			player.rotateModelLeft();
			//testOne( player._sightNode, player._position );
	  		break;
		case 68:
	  		// Rotate Right
			player.rotateModelRight();
			//testTwo( player._sightNode, player._position  );
	  		break;
		case 87:
	  		// Rotate up
			player.rotateUp();
			//testThree( player._sightNode, player._position  );
	  		break;
		case 83:
	  		// Rotate Down
			player.rotateDown();
	  		break;
		case 88:
		// Fps
			player.removeInventory();
	  		break;
		case 90:
		// Top down.
	  		break;
		default:
			update = false;
	  		return;
			
	}
	
	var map = { 
			pos : player.getPosition(), 
			ip : player._ip,
		};
		
	if( update ){
		socket.emit('updateMe', map	);
	}	
}



socket.on( 'heresPlayersFromServer', function( data ) {

	createPlayers( data )
});



socket.on( 'playersDataFromServer',function( data ){

	updatePlayers( data )
});



socket.on( 'registerSelf', function( data ){
	
	/**	The format of a player on the server.
			"name": data.name,
			"pos":data.pos,
			"ip":client.handshake.address.address,
			"kinect":data.kinect,
			"id": data.id,
			"meshName":data.mesh,
			"visible": data.visible
	*/
	player._name = data.player.name;
	player._ip = data.player.ip;
	player._userId = data.id;
	
});



socket.on( 'RegisterNewUser', function( data ){

	addUser( data );
});



socket.on( 'updateHim', function( data ){

	if( data.ip != player._ip ){
		// A client has moved and needs to be updated.
		players[ data.ip ].setPosition( data.pos );
		players[ data.ip ]._kinectData = data.kinect;
	}

});



socket.on('syncKinect', function( users ){

	// for all the users in the server.
	for ( ip in users ){
		// If the current user is me...
		if( player._ip == users[ ip ].ip ){
			// Store my new kinect data.
			player._kinectData = users[ ip ].kinect;
		}
		else{// If the current user is another client.
			// Find him in the local client list and store his kinect data.
			players[ ip ]._kinectData = users[ ip ].kinect;
		}	
	}
});



socket.on( 'deleteHim', function( data ){

	if( data.ip == player._ip ){
		// Remove self.
		player.remove();
		
	}
	players[ data.ip ].remove();
	delete players[ data.ip ];
	console.log( " The user %s has left the game. ", data.ip );
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
}

window.addEventListener('resize', resize, false);
window.addEventListener('orientationchange', resize, false);
//window.addEventListener( 'mousemove', handleMouseEvents, false );
window.addEventListener( 'keydown', handleKeyEvents, false );
// Tell me when the window loads!
window.onload = load;

  


