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
var socket = io.connect('193.156.105.162:7541');

function getCookie(c_name)
{
	var i,x,y,ARRcookies=document.cookie.split(";");
	for (i=0;i<ARRcookies.length;i++){
		
		x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
		y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
		x=x.replace(/^\s+|\s+$/g,"");
		if (x==c_name)
		{
			return unescape(y);
		}
	}
}
function setCookie(c_name,value,exdays)
{
	var exdate=new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
	document.cookie=c_name + "=" + c_value;
}

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

var waiting = false;
var paused = false;
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
	//setupGui();
	// Skybox...etc.
	setupEnviornment();
	// Send the server your data.
	sendData();
	// Test code is stuffed in here.
	//ExampleCode();
	
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
	scene.add( directionalLight );
	

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
	
	
	level_Manager.getPlayer()._userKey = getCookie("userKey");
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
	
	if( !level_Manager._player_Manager._gameOver && !paused ){
		
		// Update the level manager.
		level_Manager.update( scene, camera );
		
		// Render the scene.
		render();
		
		socket.emit( 'updateKinect' );
	}
	else{
	
		if( !waiting && !paused && level_Manager._player_Manager._gameOver ){
			// If we're not waiting and the game isn't paused and its game over go in here.
			setCookie( "score" , level_Manager.getPlayer()._score , 1 );
			socket.emit( 'gameOver', level_Manager.getPlayer()._score  );
			waiting = true;	
			console.log( "Waiting for the call from the server that the team is finished." );
		}
		
		if( paused ){
			// Do nothing 
			console.log( "The game is paused." );
		
		}
	}
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

socket.on( 'topScorePage', function( ){

	window.parent.location.href="./highScore.html";
});


socket.on('syncKinect', function( data ){

	level_Manager.getPlayer()._kinectData = data;

});


socket.on('updateNewUser',function(user) {
		console.log( "User is :"+ JSON.parse( user ).name );	
		console.log( "User's ip :"+ JSON.parse( user ).ip);		
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

  

