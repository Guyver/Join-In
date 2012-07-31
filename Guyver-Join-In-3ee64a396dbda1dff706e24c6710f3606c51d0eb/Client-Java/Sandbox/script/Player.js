/**	@Name:	Player Class

	@Author: James Browne
	
	@Brief:

*/



/** @name CONSTRUCTOR( )

	@brief
	Construct an instance of the player.
	
	@args
	name = A String name of the user.
	id = An intiger unique user identifer.
	meshUrl = The String path to the model to be loaded.
	position = The Vector3 position to create the entity.
	
	@Returns
	N/A
*/
function Player( name, position ){

	// The name of the user.
	this._name = name;
	// The global position.
	this._position = position;
	// THe velocity of the player.
	this._velocity = new THREE.Vector3(0,0,0);
	// The acceleration...
	this._accel = new THREE.Vector3(0,-9.81,0);
	// The walkspeed, could replace velocity.
	this._walkSpeed = 100;
	// The direction of the player.
	this._direction = new THREE.Vector3(0,0,1);
	// Move 100 units in the z direction, this is the players orientation.
	this._sightNode = new THREE.Vector3( 5000 , position.y , 0);
	// Define the up axis on the cartesian plane.
	this._upAxis = new THREE.Vector3( 0,1,0 );
	// The unique id, i.p address for Example.
	this._ip  = undefined;
	// The local id from the kinect.
	this._userId = undefined;			
	// The Kinect data.
	this._kinectData =undefined/* {
     "LEFT_ELBOW": {
         "x": -274.7901123046875,
         "y": 280.0702239990234,
         "z": 2308.780859375
     },
     "HEAD": {
         "x": 241.60778045654297,
         "y": 523.9069396972657,
         "z": 2158.64599609375
     },
     "RIGHT_ELBOW": {
         "x": 653.9172607421875,
         "y": 216.89061584472657,
         "z": 2262.339697265625
     },
     "RIGHT_KNEE": {
         "x": 281.5267761230469,
         "y": -634.7611511230468,
         "z": 2234.8218017578124
     },
     "device": "kinect",
     "LEFT_SHOULDER": {
         "x": 8.228845655918121,
         "y": 304.7044708251953,
         "z": 2242.640625
     },
     "LEFT_FOOT": {
         "x": -78.25286865234375,
         "y": -1038.9497192382812,
         "z": 2347.7722412109374
     },
     "RIGHT_HIP": {
         "x": 255.50746459960936,
         "y": -175.0600601196289,
         "z": 2202.8742431640626
     },
     "RIGHT_FOOT": {
         "x": 280.81434020996096,
         "y": -1053.9205322265625,
         "z": 2347.6310791015626
     },
     "RIGHT_SHOULDER": {
         "x": 388.03627624511716,
         "y": 263.89619903564454,
         "z": 2194.360546875
     },
     "RIGHT_HAND": {
         "x": 997.0059692382813,
         "y": 280.8474090576172,
         "z": 2333.0134765625
     },
     "NECK": {
         "x": 198.1325653076172,
         "y": 284.3003356933594,
         "z": 2218.5005615234377
     },
     "TORSO": {
         "x": 174.02362518310548,
         "y": 60.23494758605957,
         "z": 2217.4669189453125
     },
     "LEFT_KNEE": {
         "x": -30.573639106750488,
         "y": -610.0912719726563,
         "z": 2241.30546875
     },
     "LEFT_HAND": {
         "x": -653.9635803222657,
         "y": 304.9511779785156,
         "z": 2292.358349609375
     },
     "LEFT_HIP": {
         "x": 44.321907424926756,
         "y": -152.60081939697267,
         "z": 2229.9922607421877
     }
 };// Is the player to be drawn to screen.*/
	this._visible = undefined;
	// Angle of Rotation.
	this._angle = 0;
	// The avatar url.
	this._model = undefined;
	// The url of the Avatar.
	this._meshName = undefined;

	//this._model = this.loadModelMesh( 'model/monster.dae' );

	
	// The data for the joints
	this._rig = new Model( jointList, this._position );
	// The items the player has.
	this._inventory = [];
	
	// Set up the sphere vars
	var radius = 10, segments = 10, rings = 10;
	var Material = new THREE.MeshLambertMaterial( {color: 0xfffffffff });
	var Geometry = new THREE.SphereGeometry( radius, segments, rings );
	
	// The mesh of the Joint. Contains physical properties.
	this._mesh = new THREE.Mesh( Geometry , Material );	
	this._mesh.name	= "Player";
	// Add ourself to the scene.
	scene.add( this._mesh );	
	
	this._mesh.position = this._position;
};


/**	@name REMOVE( )

	@brief
	Remove the meshes associated with the Player.
	
	@args
	
	
	@Returns
	N/A
*/
Player.prototype.remove = function(  ){

	//Remove all meshes from the scene associated with the player.
	//Joint data.
	this._rig.remove();
	// Player Mesh.
	scene.remove( this._mesh );
	renderer.deallocateObject( this._mesh );
};



/**	@name SYNC JOINTS(  ) 

	@brief
	Updates the joint positions of the player using the kinect data.
	
	@args
	jointMap = A map object containing the names and positions of the player's joints.
	
	@Returns
	N/A

*/
Player.prototype.syncJoints = function( ){

	// Update if there is something to update...
	if ( this._kinectData != undefined && this._kinectData != null ){
	
		// The new position represents the updated height, keeps feet from going throuh floor.
		var newPosition = this._rig.setAllJoints( this._position , this._angle, this._kinectData );		
		this._position = newPosition;
	}
};



/**	@name Update(  ) 

	@brief
	Updates the joint positions of the player using the kinect data.
	
	@args
	jointMap = A map object containing the names and positions of the player's joints.
	
	@Returns
	N/A

*/
Player.prototype.update = function( ){

	// Apply the movements from the Kinect.
	this.handleMovement();
	// Put the kinect joints in game space.
	this.syncJoints();
	
	if( this._kinectData == undefined ){
	
		this._rig.move( this._position );
	}
};//End update



/**	@name SET POSITION( )

	@brief
	Sets the position of the player.
	
	@args
	pos = A vector3 position to position the player.
	
	@Returns
	N/A
*/
Player.prototype.setPosition = function( pos ){

	this._position = pos;
	this._mesh.position = pos;
};



/**	@name GET POSITION(  )

	@brief
	Get the Vector3 position of the player.
	
	@args
	N/A
	
	@Returns
	A Vector3 representing the players position.

*/
Player.prototype.getPosition = function(  ){

	return ( this._position );
};


/**	@Name:	Get Sight Node
	
	@Brief:
	A model that represents all the joint data from the kinect.
	Upon creation there will be 15 joints.
	After construction the individual joints data will be passed as a map.
	
	@Arguments: N/A
	
	@Returns: Vector3 sight node position

*/
Player.prototype.getSightNode = function( ) {

	return ( this._sightNode );
};


/**	@Name:	Set Sight Node
	
	@Brief:
	Set the position of the sight node.
	
	@Arguments: angle
	
	@Returns: N/A

*/
Player.prototype.setSightNode = function( theta ) {
	
	// Translate...sight - player pos
	this._sightNode.subSelf( this._position );
	
	// Rotate up and down
	this._sightNode.x = this._sightNode.x * Math.cos( theta ) + Math.sin( theta ) * this._sightNode.z; 
	this._sightNode.z = this._sightNode.z * Math.cos( theta ) - Math.sin( theta ) * this._sightNode.x 
	this._sightNode.y = 500;
	// Translate...sight + playerPos
	this._sightNode.addSelf( this._position );
	

};


/**	@Name:	Add Inventory
	
	@Brief:
	Add an item picked up to the players inventory.
	
	@Arguments: Item - an object to carry.
	
	@Returns: N/A

*/
Player.prototype.addInventory = function( item ) {
	
	this._inventory.push( item );	

};


/** @Name: Remove Inventory 


*/
Player.prototype.removeInventory = function( ) {
	
	this._inventory[0].removeFromMesh();
	this._inventory.pop();

};


/**	@Name: Move

	@Brief:When the player moves it should move the model.
	The joint data and its own position also.
	Move the 
	
	@Arguments: Vector3 pos
	A vector to translate the current position to.
	
	@Returns:
	N/A
*/
Player.prototype.move = function( direction ){

	// Move in the direction of the sight node.
	var dir = new THREE.Vector3( this._sightNode.x - this._position.x, this._position.y, this._sightNode.z- this._position.z );
	dir.normalize();
	var dist = direction * this._walkSpeed;
	var x = dist * dir.x;
	var y = 0;// Move on the x z plane.
	var z = dist * dir.z;
	var newVec =  new THREE.Vector3( x,y,z )
	
	this._position.addSelf( newVec  );
	this._sightNode.addSelf( newVec  );
	
	// Set the position of the mesh for the player.
	this._mesh.position = this._position;
	/*
	// Update player position.
	this._position.addSelf( pos );
	// Keep the sight node in sync.
	this._sightNode.addSelf( pos );
	*/
};



/**	@Name:	Handle Movement
	
	@Brief:
	Process the commands that were sent from the users Kinect.
	
	@Arguments: N/A
	
	@Returns: N/A

*/
Player.prototype.handleMovement = function(  ) {
	
	// Process all the states to be applied to the Player.
	var state = undefined;
	var needsUpdate = true;
	
	for ( index in this._kinectData ){
		
		if (  this._kinectData[ index ] == "true" ){
			
			state = index;		
		}
		
		switch( state ){
			case "walk":
				this._walkSpeed = 1;
				//this.move( 1 );
				this.moveModel( 1 );
				console.log( state );
				break;
			case "run":
				this._walkSpeed = 1;
				this.moveModel( 1 );
				console.log( state );
				break;
			case "AADSD":
				this.rotateModelLeft();
				console.log( state );
				break;
			case "rotateRight":
				this.rotateModelRight();
				console.log( state );
				break;
			case "standStill":
				console.log( state );
				// Do nowt!
				break;
			case "backwards":
				this._walkSpeed = 1;
				//this.move( -1 );
				this.moveModel( -1 );
				console.log( state );
				break;
			default:
				needsUpdate = false;
				break;
		}//End Switch
		
	}// End for 
	
	var map = { 
			pos : this._position, 
			ip : 	this._ip,
		};
		
	if( needsUpdate ){
		socket.emit('updateMe', map	);
	}	
};



Player.prototype.rotateModelLeft = function( ){


	var theta = 0.1;
	// Move the sight node around the torso.
	var pos = new THREE.Vector3( 0,0,0 );
	var torso = this._rig._joint[ "torso"].getPosition();
	pos = new THREE.Vector3( torso.x , torso.y , torso.z );
	
	// Translate...sight - player pos
	this._sightNode.subSelf( pos );
	
	// Rotate up and down
	this._sightNode.x = this._sightNode.x * Math.cos( theta ) + Math.sin( theta ) * this._sightNode.z; 
	this._sightNode.z = this._sightNode.z * Math.cos( theta ) - Math.sin( theta ) * this._sightNode.x 
	
	// Translate...sight + playerPos
	this._sightNode.addSelf( pos );
	// Store the rotation.
	this._angle += theta;
	// After you rotate the sight node, rotate all the joints to face it.
	//this._rig.rotateJoints( this._sightNode );
};



Player.prototype.rotateModelRight = function( ){

	var theta = -0.1;
	// Move the sight node around the torso.
	var pos = new THREE.Vector3( 0,0,0 );
	var torso = this._rig._joint[ "torso"].getPosition();
	pos = new THREE.Vector3( torso.x , torso.y , torso.z );
	
	// Translate...sight - player pos
	this._sightNode.subSelf( pos );
	
	// Rotate up and down
	this._sightNode.x = this._sightNode.x * Math.cos( theta ) + Math.sin( theta ) * this._sightNode.z; 
	this._sightNode.z = this._sightNode.z * Math.cos( theta ) - Math.sin( theta ) * this._sightNode.x 

	// Translate...sight + playerPos
	this._sightNode.addSelf( pos );
	
	// Store the rotation.
	this._angle += theta;
};



Player.prototype.moveModel = function( direction ){
	
	var torso = this._rig._joint[ "torso"].getPosition();
	// Move in the direction of the sight node.
	var dir = new THREE.Vector3( this._sightNode.x - torso.x, torso.y, this._sightNode.z- torso.z );
	dir.normalize();
	var dist = direction * this._walkSpeed;
	var x = dist * dir.x;
	var y = 0;// Move on the x z plane.
	var z = dist * dir.z;
	
	var newVec =  new THREE.Vector3( x,y,z )
	
	this._position.addSelf( newVec  );
	this._sightNode.addSelf( newVec  );
	
	// Set the position of the mesh for the player.
	this._mesh.position = this._position;

};



/**	@Name:	Rotate Left
	
	@Brief:

	
	@Arguments: N/A
	
	@Returns: 

*/
Player.prototype.rotateLeft = function( ) {

	var pos = new THREE.Vector3( 0,0,0 );
	
	if( this._kinectData != undefined ){
	
		pos = new THREE.Vector3( this._kinectData[ "head"].x , this._kinectData[ "head"].y , this._kinectData[ "head"].z );
	}
	else{
		pos = this._position;
	}
	theta = -0.1;
	
	//console.log( "Before translation and rotation, the sight node is %d,%d,%d.",this._sightNode.x,this._sightNode.y,this._sightNode.z );
	// Translate...sight - player pos
	this._sightNode.subSelf( pos );
	
	// Rotate up and down
	this._sightNode.x = this._sightNode.x * Math.cos( theta ) + Math.sin( theta ) * this._sightNode.z; 
	this._sightNode.z = this._sightNode.z * Math.cos( theta ) - Math.sin( theta ) * this._sightNode.x 
	//this._sightNode.y = 250;
	// Translate...sight + playerPos
	this._sightNode.addSelf( pos );
	
	//console.log( "After translation and rotation, the sight node is %d,%d,%d.",this._sightNode.x,this._sightNode.y,this._sightNode.z );
};


/**	@Name:	Rotate Right
	
	@Brief:

	
	@Arguments: N/A
	
	@Returns: 

*/
Player.prototype.rotateRight = function( ) {

	var pos = new THREE.Vector3( 0,0,0 );
	if( this._kinectData != undefined ){
		pos = new THREE.Vector3( this._kinectData[ "head"].x , this._kinectData[ "head"].y , this._kinectData[ "head"].z );
	}
	else{
		pos = this._position;
	}
	theta = 0.1;
	// Translate...sight - player pos
	this._sightNode.subSelf( pos );
	
	// Rotate up and down
	this._sightNode.x = this._sightNode.x * Math.cos( theta ) + Math.sin( theta ) * this._sightNode.z; 
	this._sightNode.z = this._sightNode.z * Math.cos( theta ) - Math.sin( theta ) * this._sightNode.x 
	//this._sightNode.y = 250;
	// Translate...sight + playerPos
	this._sightNode.addSelf( pos );
};


/**	@Name:	Rotate Up
	
	@Brief:

	
	@Arguments: N/A
	
	@Returns: 

*/
Player.prototype.rotateUp = function( ) {

	theta = 0.01;
	// Translate...sight - player pos
	this._sightNode.subSelf( this._position );
	
	// Rotate up and down
	this._sightNode.x = this._sightNode.x * Math.cos( theta ) + Math.sin( theta ) * this._sightNode.y; 
	this._sightNode.y = this._sightNode.y * Math.cos( theta ) - Math.sin( theta ) * this._sightNode.x 
	
	// Translate...sight + playerPos
	this._sightNode.addSelf( this._position );
};


/**	@Name:	Rotate Down
	
	@Brief:

	
	@Arguments: N/A
	
	@Returns: 

*/
Player.prototype.rotateDown = function( ) {

	theta = -0.1;
	// Translate...sight - player pos
	this._sightNode.subSelf( this._position );
	
	// Rotate up and down
	this._sightNode.x = this._sightNode.x * Math.cos( theta ) + Math.sin( theta ) * this._sightNode.y; 
	this._sightNode.y = this._sightNode.y * Math.cos( theta ) - Math.sin( theta ) * this._sightNode.x  
	
	// Translate...sight + playerPos
	this._sightNode.addSelf( this._position );
};


/**	@name LOAD MODEL MESH( )

	@brief
	Load a model from file specified.
	
	@args
	url = The string location of the model on disk
	
	@Returns
	N/A
*/
Player.prototype.loadModelMesh = function( url ){
	
	var that = this;
	var what = new THREE.ColladaLoader();
	
	what.load( url ,function( collada ){
		
		that._model = collada.scene;
		that._model.scale.set(0.01,0.01,0.01);
		that._model.position = that._position;
		that._model.rotation.x = -Math.PI/2;
		
		scene.add( that._model );
	});
};