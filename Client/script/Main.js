/**=========================================================================

	@Author: James Browne
	
	@Brief:
	This is where the magic happens. The entry point, kind of.
	The game logic is carried out here after initalising the game.
	
=========================================================================*/


// Variables for the sugary goodness!
var gui, param, varNum, interval;

// Three.js vars
var scene, renderer, mesh, geometry, material;

// Camera vars, initalised in "initCamera()"
var camera, nearClip, farClip, aspectRatio, fov;

// Kinect data
var numJoints, model, jointList;

// Game Physics vars
var player;

// The time since last frame.
var deltaTime, last, current;

var imgContainer;


/*====================================INIT()==========================================

	Initalise some variables needed for start up.
//========================================================================*/
function init(){
	
	container = document.createElement( 'div' );
	document.body.appendChild( container );

	var info = document.createElement( 'div' );
	info.style.position = 'absolute';
	info.style.top = '10px';
	info.style.width = '100%';
	info.style.textAlign = 'center';
	info.innerHTML = 'Use the sliders in the top right corner to move the camera about.';
	container.appendChild( info );

	
	// Set up the three scene
	initScene();
	// Set up the renderer type
	initRenderer();	
	// Set up the lights
	setupLights();
	// Set up the camera
	initCamera();
	// create the game objects.
	createObjects();
	// Gui stuff.
	setupGui();
	// Skybox...etc
	setupEnviornment();
	
	
	
	var x = new THREE.ColladaLoader();
	x.load( 'models/warehouse_model.dae', function( collada ){
		var model = collada.scene;
		model.scale.set(100,100,100);
		model.rotation.x = -Math.PI/2;
		scene.add( model );	
	
	});
	// Initalise the game loop to 60fps. Anim frame pffft
	interval = setInterval( 'gameLoop()', 1000 / 60 );

}




/*================================INIT CAMERA()==========================

	Initalise our Three camera.

========================================================================*/
function initCamera(){
	
	nearClip = 1;
	farClip = 100000;
	fov = 70;
	aspectRatio = window.innerWidth / window.innerHeight;
	camera = new THREE.PerspectiveCamera( fov, aspectRatio, nearClip, farClip );
	camera.position.y = 150;
	camera.position.z = 1000;
	scene.add( camera );
}




/*================================INIT SCENE()========================================

	Initalise the scene that will contain all the game data.

========================================================================*/
function initScene(){
	
	// the scene contains all the 3D object data
	scene = new THREE.Scene();
}




/*===============================INIT RENDERER()======================================

	Set up the renderer that will decide render what is in the view frustrum.
	This will be a CANVAS renderer, not webgl. For the test at least.

//========================================================================*/
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




/*===============================SETUP LIGHTS()=======================================

	Let there be light!

//========================================================================*/
function setupLights(){
	
	// Ambient
	var ambientLight = new THREE.AmbientLight( Math.random() * 0x10 );
	// Add to the scene.
	scene.add( ambientLight );
	
	// Directional
	var directionalLight = new THREE.DirectionalLight( Math.random() * 0xffffff );
	directionalLight.position.x = Math.random() - 0.5;
	directionalLight.position.y = Math.random() - 0.5;
	directionalLight.position.z = Math.random() - 0.5;
	directionalLight.position.normalize();
	// Add to the scene.
	scene.add( directionalLight );

	// Another directional...
	var directionalLight = new THREE.DirectionalLight( Math.random() * 0xffffff );
	directionalLight.position.x = Math.random() - 0.5;
	directionalLight.position.y = Math.random() - 0.5;
	directionalLight.position.z = Math.random() - 0.5;
	directionalLight.position.normalize();
	// Add to the scene.
	scene.add( directionalLight );
}




/*===============================CREATE OBJECTS()=====================================

	Set up stuff! Args: Name, Position (vector3), Mesh (Three.Mesh)
========================================================================*/
function createObjects(){
	

		jointList = [ 'HEAD',
				'LEFT_ELBOW',
				'LEFT_FOOT',
				'LEFT_HAND',
				'LEFT_HIP',
				'LEFT_KNEE',
				'LEFT_SHOULDER',
				'NECK',
				'RIGHT_ELBOW',
				'RIGHT_FOOT',
				'RIGHT_HAND',
				'RIGHT_HIP',
				'RIGHT_KNEE',
				'RIGHT_SHOULDER',
				'TORSO'];
				
		player = new Player( 'James', 42, 'model/monster.dae', new THREE.Vector3( 100,100,100) );

		
}



/*===============================GAME LOOP()==========================================

	This is the main game loop. Where all the magic happens if you will.
	I'm calculating delta time here to use for Newtonian Mechanics. :D

//========================================================================*/
function gameLoop(){
	
	// Initalise last for the 1st iteration.
	if(!last)	{
		last= new Date();
	}
	
	// Set the camera Z to the gui for debugging!
	camera.position.x = param['cameraX'];
	camera.position.y = param['cameraY'];
	camera.position.z = param['cameraZ'];
	
	// Look at the custom object I made!
	this.camera.lookAt( this.player.getPosition() );
		
	// Find time now.
	current = new Date();
	// Get the change in time, dt.
	deltaTime = current.getTime() - last.getTime();
	// reset the last time to time this frame for the next.
	last = current;
	
	// Have a map of Kinnect maps with time for key.
	// Sort them each frame by time relevance and use the top.
	// This acts as a queue for a players kinect data.
	
	if( kinect ){
		//Get the kinect data.
		socket.emit('kinect');
		console.log("Getting the kinect data from main");
	}
	
	try{
	
		if( kinectMap['HEAD'] != undefined){
			syncKinect();
			// Look at the custom object I made!
			this.camera.lookAt( this.player.getPosition() );
		}
	}catch( err ){
		
		console.log("kinectMap is undefined or null");
		kinectMap.clear();
		return;
	}
	
	// Wait for assets to load and begin the game loop.
	if( imagesLoaded ){
		render();
	}

}




/*================================RENDER()============================================
	
	Render some stuff to the html page.
	
========================================================================*/
function render(){

	var x = document.getElementById('string');
	x.value =  1000 / deltaTime + " fps " ;
	//x.value =  camera.position.z;
	
	renderer.render( scene, camera );
}




/*================================SETUP GUI()=========================================

	Make the Gui do stuff, the callbacks for changable variables is in here. 
	I did it like this so I can change the time step and watch at run time.
	In effect you can edit at run time now using the GUI Paramaters :)
 	Like a boss!
 	
========================================================================*/
function setupGui(){
	
	// The number of entries/spaces on the GUI
	varNum = 5
	
	// Create the GUI
	 gui = new DAT.GUI( { varNum : 5 * 32 - 1} );
	 
	 // Store the list of changable parameters.
	 param = {
	 fps:60,
	 parallaxSpeed:2,
	 cameraX:0,
	 cameraY:0,
	 cameraZ:1000
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
	 gui.add( param, 'cameraX').name('Camera.X').min(( farClip / 20 ) * -1).max(farClip/20).step(100).onFinishChange(function(){
		
		
	});
	
	 // Add the paramater values to the GUI, give it a name, upon change specify the callback function
	 gui.add( param, 'cameraY').name('Camera.Y').min(( farClip / 20) * -1).max(farClip/20).step(100).onFinishChange(function(){
		
		
	});
	
	 // Add the paramater values to the GUI, give it a name, upon change specify the callback function
	 gui.add( param, 'cameraZ').name('Camera.Z').min(( farClip / 20) * -1).max(farClip/20).step(100).onFinishChange(function(){
		
		
	});

}




/*================================SETUP ENVIORNMENT()=================================

	Create a box around the origin for testing.
	Will be a nice sandbox to play with our model.
	
========================================================================*/
function setupEnviornment(){

	// Create a texture from an image, image mush be a power of 2 in size. i.e 512*256
	var texture_blue = new THREE.Texture( imageManager.getAsset( 'img/target_blue.png', {}, render() ));

	// Oh yes, it does need this!
	texture_blue.needsUpdate = true;
	var geometry = new THREE.CubeGeometry( 10000, 10000,  10000);
	
	var texture = new THREE.Mesh( geometry, new THREE.MeshBasicMaterial( { map: texture_blue } ) );
	texture.doubleSided = true;
	
	scene.add( texture );
}




/*=================================SYNC KINECT()=============================================

	Syncronise the model with the Kinect data we've got from openNi
========================================================================*/
function syncKinect() {  
	
	player.syncJoints( kinectMap );
}  




/*=================================LOAD()=============================================

	Fired whenis called when the window loads!
========================================================================*/
function load() {  
	init(); 
}  




/*=================================RANDOM RANGE()=====================================

	Helper function for random numbers
========================================================================*/
function randomRange(min, max) {
	return Math.random()*(max-min) + min;
}




/*==================================RESIZE()==========================================

	Helper function for resizing the display
//========================================================================*/
function resize(){
    
	// Fit the render area into the window.
	renderer.setSize( window.innerWidth, window.innerHeight );
    	// Redraw 
    	render();
}

window.addEventListener('resize', resize, false);
window.addEventListener('orientationchange', resize, false);
// Tell me when the window loads!
window.onload = load;

  


