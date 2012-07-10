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

// Variables for the sugary goodness!
var gui, param, varNum, interval;

// Three.js vars
var scene, renderer, mesh, geometry, material;

// Camera vars, initalised in "initCamera()"
var camera, nearClip, farClip, aspectRatio, fov;

// remember these initial values
var tanFOV;
var windowHeight = window.innerHeight;

// Kinect data
var numJoints, model, jointList;

// Game Physics vars TODO: Move to player manager.
var players;

// The time since last frame.
var deltaTime, last, current;

// The level manager.
var level_Manager;

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
	
	container = document.createElement( 'div' );
	document.body.appendChild( container );

	var info = document.createElement( 'div' );
	info.style.position = 'absolute';
	info.style.top = '10px';
	info.style.width = '100%';
	info.style.textAlign = 'center';
	info.innerHTML = 'Use W,A,S,D to move around';
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
	//ExampleCode();
	
	gameLoop();
	/*
	// Initalise the game loop to 60fps. Anim frame pffft
	interval = setInterval( 'gameLoop()', 1000 / 60 );
	*/

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
	scene.add( directionalLight );/**/
};


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
	
	level_Manager = new Level_Manager();
	
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
	
		objects.push( new Object( new THREE.Vector3( 1000*i, 1500, 5000 ), "Object" ) );	
	}		
	
	for ( var i = 0; i < 2; i++ ){
	
		objects.push( new Object( new THREE.Vector3( 2000*i, 250, 2000 ), "Bin" ) );	
	}	
};


/**	@Name:	Game Loop
	@Brief:	This is the loop we call per frame to update the game.
	@Arguments:N/A
	@Returns:N/A
*/
function gameLoop(){
	
	requestAnimationFrame( gameLoop, renderer.domElement );
	
	// Update all the players. TODO: Move to player manager.
	for ( each_player in players ){
		
		players[ each_player ].update();	
	}
	
	// Update the objects.
	for( each_object in objects ){
	
		objects[ each_object ].update();
	}
	
	// Test player wall collisions...
	level_Manager.testCollision( scene );
	
	// Update the level manager.
	level_Manager.update( objects, camera );
	
	 /*//Debugging camera
	// Set the camera Z to the gui for debugging!
	camera.position.x = 6000;//param['camera_X'];
	camera.position.y = 1500;//param['camera_Y'];
	camera.position.z = 6000;//param['camera_Z'];
	camera.lookAt( level_Manager.getPlayer().getPosition() );
*/
	// Initalise last for the 1st iteration.
	if(!last)last= new Date();
	
	// Find time now.
	current = new Date();
	
	// Get the change in time, dt.
	deltaTime = current.getTime() - last.getTime();
	var x = document.getElementById('string');
	x.value =  Math.floor(1000 / deltaTime) + " fps " ;
	var posX = document.getElementById('posX');
	posX.value =  Math.floor(level_Manager.getPlayer().getPosition().x);
	var posY = document.getElementById('posY');
	posY.value =  Math.floor(level_Manager.getPlayer().getPosition().y);
	var posZ = document.getElementById('posZ');
	posZ.value =  Math.floor(level_Manager.getPlayer().getPosition().z);
	// reset the last time to time this frame for the next.
	last = current;	
	
	// Render the scene.
	render();
	
	// Get the kinect data for the next frame.
	socket.emit( 'updateKinect' );
};


/**	@Name:	Render 
	@Brief:	Draw the scene.
	@Arguments:N/A
	@Returns:N/A
*/
function render(){
	renderer.render( scene, camera );
};


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
	var planeTex = new THREE.Texture(imageManager.getAsset('img/grassTile01.png', {}, render()));
	
	planeTex.needsUpdate = true;
	planeTex.wrapT = THREE.RepeatWrapping;
	planeTex.wrapS = THREE.RepeatWrapping;
	planeTex.repeat.set( 100, 100 );// Higher for smaller tiles
	
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
	
};
 

/**	@Name:	Example code
	@Brief:	Code snippets that I used for testing.
	@Arguments:N/A
	@Returns:N/A
*/
 function ExampleCode(){
 	
	
 };


function getPlayers() {  
	// Request all the players registered in the server.
	socket.emit( 'getPlayers' );
}; 


function createPlayers( data ) {  
	// TODO: Move this to the player manager.
	players = {};
	// Dont Recreate yourself ffs, thats just stupid.
	
	for( index in data ){
	
		players[ data[ index ].ip ] = new Player(data[ index ].name, data[ index ].pos );
		players[ data[ index ].ip ]._ip = data[ index ].ip;
		players[ data[ index ].ip ]._kinectData = data[ index ].kinect;
	}
};


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
}; //end func. 


function sendData(  ) {  
		// Send a template to the server to store. This is fine.
	socket.emit('registerMeInServer', { 
		
		name : level_Manager.getPlayer()._name,
		id	: level_Manager.getPlayer()._userId,
		pos : level_Manager.getPlayer()._position,
		kinect : level_Manager.getPlayer()._kinectData,
		ip : level_Manager.getPlayer()._ip,
		mesh : level_Manager.getPlayer()._meshName,
		visible : level_Manager.getPlayer()._visible
	});	
}; //end func. 


function addUser( user ){

	// Create and add the new user.TODO: Move to player manager
	players[ user.ip ] = new Player( user.player.name, user.player.pos );
	players[ user.ip ]._ip = user.ip;
};


function removeUser( user ){

	// Delete the user...
	delete players[ user.ip ];
};


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
			level_Manager.setCameraType( cameraType );
	  		break;
		case 99:// Num pad 3. Third Person.
			var cameraType = 3;
			level_Manager.setCameraType( cameraType );
	  		break;
		default:
			update = false;
	  		return;
			
	}
	
	var map = { 
			pos : level_Manager.getPlayer().getPosition(), 
			ip : level_Manager.getPlayer()._ip,
		};
		
	if( update ){
		socket.emit('updateMe', map	);
	}	
};


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
	level_Manager.getPlayer()._name = data.player.name;
	level_Manager.getPlayer()._ip = data.player.ip;
	level_Manager.getPlayer()._userId = data.id;
	
});


socket.on( 'RegisterNewUser', function( data ){

	addUser( data );
});


socket.on( 'updateHim', function( data ){

	if( data.ip != level_Manager.getPlayer()._ip ){
		// A client has moved and needs to be updated.
		players[ data.ip ].setPosition( data.pos );
		players[ data.ip ]._kinectData = data.kinect;
	}

});


socket.on('syncKinect', function( users ){

	// for all the users in the server.
	for ( ip in users ){
		// If the current user is me...
		if( level_Manager.getPlayer()._ip == users[ ip ].ip ){
			// Store my new kinect data.
			level_Manager.getPlayer()._kinectData = users[ ip ].kinect;
		}
		else{// If the current user is another client.
			// Find him in the local client list and store his kinect data.
			players[ ip ]._kinectData = users[ ip ].kinect;
		}	
	}
});


socket.on( 'deleteHim', function( data ){

	if( data.ip == level_Manager.getPlayer()._ip ){
		// Remove self.
		level_Manager.getPlayer().remove();
		
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
};


window.addEventListener('resize', resize, false);
window.addEventListener('orientationchange', resize, false);
//window.addEventListener( 'mousemove', handleMouseEvents, false );
window.addEventListener( 'keydown', handleKeyEvents, false );
// Tell me when the window loads!
window.onload = load;

  


