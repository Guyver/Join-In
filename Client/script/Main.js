/**	@Name:	Main

	@Author: James Browne
	
	@Brief:
	Where the game logic is controlled.

*/


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
var player;

// The time since last frame.
var deltaTime, last, current;

var imgContainer;

// Debugging Variable.
var test;


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




/**	@Name:	Create Objects.
	@Brief:	Create the games objects like the Player.
	@Arguments:N/A
	@Returns:N/A
*/
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
				
		player = new Player( 'James', 42, "", new THREE.Vector3( 100,100,100) );//'model/monster.dae'
		
}



/**	@Name:	Game Loop
	@Brief:	This is the loop we call per frame to update the game.
	@Arguments:N/A
	@Returns:N/A
*/
function gameLoop(){
	
	// Initalise last for the 1st iteration.
	if(!last)	{
		last= new Date();
	}	
	// Set the camera Z to the gui for debugging!
	/*camera.position.x = param['cameraX'];
	camera.position.y = param['cameraY'];
	camera.position.z = param['cameraZ'];
	*/
	
	// Camera Follows the player.
	camera.position.x = player.getPosition().x;
	camera.position.y = player.getPosition().y + 500;
	camera.position.z = player.getPosition().z - 500; 

	// Find time now.
	current = new Date();
	// Get the change in time, dt.
	deltaTime = current.getTime() - last.getTime();
	// reset the last time to time this frame for the next.
	last = current;
	
	if( kinect ){
		//Get the kinect data.
		socket.emit('kinect');
		console.log("Getting the kinect data from main");
	}
	
	try{
	
		if( kinect ){
		
			syncKinect();
		}
		else{
		
		}
	}catch( err ){
		
		console.log("kinectMap is undefined or null");
		kinectMap.clear();
		return;
	}
	
	// Look at the Player.
	this.camera.lookAt( player.getSightNode() );
	render();
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
	varNum = 5
	
	// Create the GUI
	 gui = new DAT.GUI( { varNum : 5 * 32 - 1} );
	 
	 // Store the list of changable parameters.
	 param = {
	 fps:60,
	 parallaxSpeed:2,
	 cameraX:0,
	 cameraY:500,
	 cameraZ:-500
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
	 gui.add( param, 'cameraX').name('Camera.X').min(( farClip  ) * -1).max(farClip).step(1000).onFinishChange(function(){
		
		
	});
	
	 // Add the paramater values to the GUI, give it a name, upon change specify the callback function
	 gui.add( param, 'cameraY').name('Camera.Y').min(( farClip ) * -1).max(farClip).step(1000).onFinishChange(function(){
		
		
	});
	
	 // Add the paramater values to the GUI, give it a name, upon change specify the callback function
	 gui.add( param, 'cameraZ').name('Camera.Z').min(( farClip ) * -1).max(farClip).step(1000).onFinishChange(function(){
		
		
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
	
	var planeTex = new THREE.Texture(imageManager.getAsset('img/floor.png', {}, render()));
	
	planeTex.needsUpdate = true;
	planeTex.wrapT = THREE.RepeatWrapping;
	planeTex.wrapS = THREE.RepeatWrapping;
	planeTex.repeat.set( 100, 100 );
	
	var planeGeo = new THREE.PlaneGeometry(100000, 100000, 1, 10);
	
	var ground = new THREE.Mesh( planeGeo, new THREE.MeshBasicMaterial({
		 map: planeTex 
	}));
	
	ground .rotation.x = -Math.PI / 2;
	ground .position.y = 0;
	ground .receiveShadow = true;
	ground.doubleSided = true;
	scene.add( ground );

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
/**
	var x = new THREE.ColladaLoader();
	x.load( 'models/warehouse_model.dae', function( collada ){
		var model = collada.scene;
		model.scale.set(100,100,100);
		model.rotation.x = -Math.PI/2;
		scene.add( model );	
	
	});
*/


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


 
 
/**	@Name:	Sync Kinect
	@Brief:	Syncronise the users kinect data with the player object in game.
	@Arguments:N/A
	@Returns:N/A
*/
function syncKinect() {  
	
	player.syncJoints( kinectMap );
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
	
	switch( key ){
		
		case 38:
	  		// Move up
			player.move( new THREE.Vector3( 100,0,0 ) );
	  		break;
		case 40:
	  		// Move Down
			player.move( new THREE.Vector3( -100,0,0 ) );
	  		break;
		case 37:
	  		// Move Left
			player.move( new THREE.Vector3( 0,0,100 ) );
	  		break;
		case 39:
	  		// Move Right
			player.move( new THREE.Vector3( 0,0,-100 ) );
	  		break;
		default:
	  		return;
	}
	
}




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

  


