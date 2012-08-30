/**	@Name:	Player Object
	@Author: James Browne	
	@Brief:
*/



/** @name CONSTRUCTOR
	@brief Construct an instance of the player.	
	@args name = A String name of the user.
	id = An intiger unique user identifer.
	meshUrl = The String path to the model to be loaded.
	position = The Vector3 position to create the entity.	
	@Returns N/A
*/
function Player( meshName, models, position ){

	// The name of the user.
	this._name = "";
	// An image of the player.
	this._image = new Image();
	// Src
	this._image.src = "http://4.bp.blogspot.com/_SJsqZeAk064/SxLz6pn9_aI/AAAAAAAACd8/lozmwiAthnk/s1600/offline.bmp";
	// The global position.
	this._position = position;
	if( this._position == undefined )this._position = new THREE.Vector3( 0,0,0 );
	// THe velocity of the player.
	this._velocity = new THREE.Vector3(0,0,0);
	// The starting position.
	this._startingPos = new THREE.Vector3(0,0,0);
	// The acceleration...
	this._accel = new THREE.Vector3( 0,-9.81,0 );
	// The walkspeed, could replace velocity.
	this._walkSpeed = 500;
	// Define the up axis on the cartesian plane.
	this._upAxis = new THREE.Vector3( 0,1,0 );
	// The unique id, i.p address for Example.
	this._ip  = undefined;
	// The score of the player.
	this._score = 0;
	// Hug counter
	this._hugCount = 0;
	// The local id from the kinect.
	this._userKey = undefined;
	// The team the user is on.
	this._team = undefined;	
	// The Kinect data. Hard coded as Lars for debugging.
	this._kinectData ={
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
 };
	// Is the player to be drawn to screen.
	this._visible = undefined;
	// Angle of Rotation.
	this._angle = 0;
	// Hugging boolian
	this._hugged = false;
	// The avatar url.
	this._model = undefined;
	// The url of the Avatar.
	this._meshName = undefined;
	// The data for the joints
	this._rig = new Model( jointList, models, this._position );
	// Move 100 units in the z direction, this is the players orientation.
	this._sightNode = new THREE.Vector3( this._position.x , this._kinectData["head"].y + 1000 , this._position.z - 5000 );
	// The direction of the player.
	this._direction = new THREE.Vector3( this._sightNode.x - this._position.x, this._position.y, this._sightNode.z- this._position.z );
	this._direction.normalize();

	// The central position of the player. Represented by a sphere for debugging.
	var radius = 10, segments = 10, rings = 10;
	var Material = new THREE.MeshLambertMaterial( {color: 0xfffffffff });
	var Geometry = new THREE.SphereGeometry( radius, segments, rings );
	
	// The mesh of the Joint. Contains physical properties.
	this._mesh = new THREE.Mesh( Geometry , Material );	
	this._mesh.name	= meshName;
	// Add ourself to the scene.
	scene.add( this._mesh );	
	
	this._mesh.position = this._position;
		
};


/**	@name REMOVE
	@brief:	Remove the meshes associated with the Player.	
	@args:N/A	
	@Returns:N/A
*/
Player.prototype.remove = function(  ){

	//Remove all meshes from the scene associated with the player.
	//Joint data.
	this._rig.remove();
	// Player Mesh.
	scene.remove( this._mesh );
	// Free up the mesh from being rendered.
	renderer.deallocateObject( this._mesh );
};


/**	@name SYNC JOINTS
	@brief Updates the joint positions of the player using the kinect data.
	@args jointMap = A map object containing the names and positions of the player's joints.	
	@Returns N/A

*/
Player.prototype.syncJoints = function( ){

	// Update if there is something to update...
	if ( this._kinectData != undefined && this._kinectData != null ){
	
		// The new position represents the updated height, this keeps feet from going throuh floor.
		var newPosition = this._rig.setAllJoints( this._position , this._angle, this._kinectData );		
		this._position = newPosition;
	}
};


/**	@name: Update
	@brief: Updates the joint positions of the player using the kinect data.	
	@args: jointMap = A map object containing the names and positions of the player's joints.	
	@Returns: N/A
*/
Player.prototype.update = function( ){

	// Apply the movement enumerations from the Kinect.
	this.processCommands();
	// Put the kinect joints in game space.
	this.syncJoints();
	// Update the joints.
	this._rig.update( this._sightNode, this._kinectData );
	
	// For debugging using the keyboard. 
	if( this._kinectData == undefined ){
	
		this._rig.move( this._position );
	}
	
	//if( this._angle < 0 || this._angle > 360 ) this._angle = 0;
};//End update


/**	@name: SET POSITION
	@brief:Sets the position of the player.	
	@args:pos = A vector3 position to position the player.	
	@Returns:N/A
*/
Player.prototype.setPosition = function( pos ){

	var oldPos = new THREE.Vector3( this._position.x , this._position.y , this._position.z  ); 
	var newPos = new THREE.Vector3( pos.x, 0 , pos.z );
	var vect = new THREE.Vector3( 0,0,0 );
	
	vect.x = newPos.x - oldPos.x;
	vect.y = 0;
	vect.z = newPos.z - oldPos.z;
	
	this._position.addSelf( vect );
	this._sightNode.addSelf( vect );
	
	this._mesh.position = pos;
};


/**	@name:GET POSITION
	@brief:Get the Vector3 position of the player.	
	@args:N/A	
	@Returns:A Vector3 representing the players position.
*/
Player.prototype.getPosition = function(  ){

	return ( new THREE.Vector3( this._position.x, this._position.y, this._position.z ) );
};


/**	@Name:	Get Sight Node	
	@Brief:A model that represents all the joint data from the kinect.
	Upon creation there will be 15 joints.
	After construction the individual joints data will be passed as a map.
	@Arguments: N/A	
	@Returns: Vector3 sight node position
*/
Player.prototype.getSightNode = function( ) {

	return ( new THREE.Vector3( this._sightNode.x,this._sightNode.y,this._sightNode.z ) );
};


/**	@Name:	Process commands.	
	@Brief:
	Process the commands that were sent from the users Kinect.	
	@Arguments: N/A	
	@Returns: N/A

*/
Player.prototype.processCommands = function(  ) {
	
	// Process all the states to be applied to the Player.
	var state = undefined;
	var needsUpdate = true;
	
	try{
		// If the left hand is above the head.
		if(  (this._kinectData[ "leftHand" ].y > this._kinectData[ "head" ].y )  
			&& ( this._kinectData[ "rightHand" ].y < this._kinectData[ "torso" ].y ) ){
		
			state = "rotateLeft";
		}
		else if( ( this._kinectData[ "rightHand" ].y > this._kinectData[ "head" ].y )  
					&& ( this._kinectData[ "leftHand" ].y < this._kinectData[ "torso" ].y  )){
		
			state = "rotateRight";
		}		
		else if( ( this._kinectData[ "rightKnee" ].y > this._kinectData[ "leftKnee" ].y + 50 )  
					||( this._kinectData[ "leftKnee" ].y > this._kinectData[ "rightKnee" ].y + 50 ) ){
		
			state = "walk";
		}		
	}
	catch( error ){
		// Something bad happened.
		console.log( "Player.processCommands catch "+ error );
	}
	
	var lHand = this._kinectData[ "leftHand" ];
	var rHand = this._kinectData[ "rightHand" ];
	var torso = this._kinectData[ "torso" ];	
	var dist = getDistance3D( lHand, rHand );
	var distFromTorso = getDistance3D( lHand, torso );
	var madeHug = ( dist < 300 ) && ( distFromTorso > 300 ) && ( ( lHand.y < ( torso.y + 200 ) ) && ( lHand.y > ( torso.y - 200 ) ) );
	
	if( madeHug && !this._hugged ){
		
		sounds[ 1 ].play();
		this._score += 100;
		this._hugCount++;
		console.log( "I've performed a hug!" );
		this._hugged = true;
	}else if( !madeHug ){
		this._hugged = false;	
	}
			
	switch( state ){
	
		case "walk":
			this._walkSpeed = 10;
			this.moveModel( 1 );
			console.log( state );
			break;
		case "run":
			this._walkSpeed = 1;
			this.moveModel( 1 );
			console.log( state );
			break;
		case "rotateLeft":
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
			this.moveModel( -1 );
			console.log( state );
			break;
		default:
			needsUpdate = false;
			break;			
	}
};


/**	@Name:	Rotate Left	
	@Brief:	
	@Arguments: N/A	
	@Returns: 

*/
Player.prototype.rotateModelLeft = function( ){
	
	var theta = 0.05;
	// Move the sight node around the torso.
	var torso = this._rig._joint[ "torso"].getPosition();

	// Translate...to the origin to get the sight node vector!
	this._sightNode.subSelf( this._position );

	// The axis we want to rotate about.
	var axis = new THREE.Vector3( 0, 1, 0 );
	
	// Create the translation matrix with a scale of 1 using the axis and degree of rotation.
	var matrix = new THREE.Matrix4().makeRotationAxis( axis, theta );

	// Apply the rotation to the sight node.
	matrix.multiplyVector3( this._sightNode );
	
	// Translate...back again.
	this._sightNode.addSelf( this._position );

	this._angle += theta;	
};


/**	@Name:	Rotate Sight By Angle	
	@Brief:	
	@Arguments: N/A	
	@Returns: 
*/
Player.prototype.rotateSightByAngle = function( angle ){
	
	// Move the sight node around the torso.
	var torso = this._rig._joint[ "torso"].getPosition();

	// Translate...to the origin to get the sight node vector!
	this._sightNode.subSelf( torso );

	// The axis we want to rotate about.
	var axis = new THREE.Vector3( 0, 1, 0 );
	
	// Create the translation matrix with a scale of 1 using the axis and degree of rotation.
	var matrix = new THREE.Matrix4().makeRotationAxis( axis, angle );

	// Apply the rotation to the sight node.
	matrix.multiplyVector3( this._sightNode );
	
	// Translate...back again.
	this._sightNode.addSelf( torso );

	this._angle += angle;	
};


/**	@Name:	Rotate Right	
	@Brief:	
	@Arguments: N/A	
	@Returns: 

*/
Player.prototype.rotateModelRight = function( ){

	var theta = -0.05;
	// Move the sight node around the torso.
	var torso = this._rig._joint[ "torso"].getPosition();

	// Translate...to the origin to get the sight node vector!
	this._sightNode.subSelf( torso );

	// The axis we want to rotate about.
	var axis = new THREE.Vector3( 0, 1, 0 );
	
	// Create the translation matrix with a scale of 1 using the axis and degree of rotation.
	var matrix = new THREE.Matrix4().makeRotationAxis( axis, theta );

	// Apply the rotation to the sight node.
	matrix.multiplyVector3( this._sightNode );
	
	// Translate...back again.
	this._sightNode.addSelf( torso );
	
	this._angle += theta;
};


/**	@Name:	Move In Direction
	@Brief:	
	@Arguments: Direction normal vector	
	@Returns: 
*/
Player.prototype.moveInDirection = function( direction ){
	
	direction.normalize();
	// Offset the player position in the direction of the sight node.
	this._position.x += this._walkSpeed * direction.x;
	this._position.z += this._walkSpeed * direction.z;
	
	// Offset the position of the sight node in the same direction by the same amount.
	this._sightNode.x += this._walkSpeed * direction.x;
	this._sightNode.z += this._walkSpeed * direction.z;
	
	// Set the position of the mesh for the player.
	this._mesh.position = this._position;
};


/**	@Name:	Move Model	
	@Brief:	
	@Arguments: N/A	
	@Returns: 

*/
Player.prototype.moveModel = function( direction ){
	
	// Move in the direction of the sight node.
	this._direction.x = this._sightNode.x - this._position.x;
	this._direction.z = this._sightNode.z - this._position.z;
	this._direction.normalize();
	
	var dist = direction * this._walkSpeed;
	
	// Offset the player position in the direction of the sight node.
	this._position.x += dist * this._direction.x;
	this._position.z += dist * this._direction.z;
	
	// Offset the position of the sight node in the same direction by the same amount.
	this._sightNode.x += dist * this._direction.x;
	this._sightNode.z += dist * this._direction.z;
	
	// Set the position of the mesh for the player.
	this._mesh.position = this._position;

};


/**	@Name:	Rotate Up	
	@Brief:	
	@Arguments: N/A	
	@Returns: 

*/
Player.prototype.rotateUp = function( ) {

	theta = 0.01;
	// Move the sight node around the torso.
	var torso = this._rig._joint[ "torso"].getPosition();

	// Translate...to the origin to get the sight node vector!
	this._sightNode.subSelf( torso );

	// The axis we want to rotate about.
	var axis = new THREE.Vector3( 0, 0, 1 );
	
	// Create the translation matrix with a scale of 1 using the axis and degree of rotation.
	var matrix = new THREE.Matrix4().makeRotationAxis( axis, theta );

	// Apply the rotation to the sight node.
	matrix.multiplyVector3( this._sightNode );
	
	// Translate...back again.
	this._sightNode.addSelf( torso );

};


/**	@Name:	Rotate Down	
	@Brief:	
	@Arguments: N/A	
	@Returns: 
*/
Player.prototype.rotateDown = function( ) {

	theta = -0.01;
	// Move the sight node around the torso.
	var torso = this._rig._joint[ "torso"].getPosition();

	// Translate...to the origin to get the sight node vector!
	this._sightNode.subSelf( torso );

	// The axis we want to rotate about.
	var axis = new THREE.Vector3( 0, 0, 1 );
	
	// Create the translation matrix with a scale of 1 using the axis and degree of rotation.
	var matrix = new THREE.Matrix4().makeRotationAxis( axis, theta );

	// Apply the rotation to the sight node.
	matrix.multiplyVector3( this._sightNode );
	
	// Translate...back again.
	this._sightNode.addSelf( torso );

};


/**	@Name: 	
	@Brief:	
	@Arguments: N/A	
	@Returns: 

*/
Player.prototype.getJointPosition = function( jointName ) {

	return ( this._rig.getJointPosition( jointName ) );	
};


/**	@Name:  	
	@Brief:	
	@Arguments: N/A	
	@Returns: 
*/
Player.prototype.addScore = function( score ) {

	this._score += score;
};


/**	@Name:  	
	@Brief:	
	@Arguments: N/A	
	@Returns: 
*/
Player.prototype.getScore = function(  ) {

	return this._score;
};


/**	@Name:  	
	@Brief:	
	@Arguments: N/A	
	@Returns: 
*/
Player.prototype.resetPosition = function(  ) {

	this.setPosition( this._startingPos );
};

/**	@Name:  	
	@Brief:	
	@Arguments: N/A	
	@Returns: 
*/
Player.prototype.getJoint = function( jointName ) {

	return ( this._rig.getJoint( jointName ) );	
};


/**	@Name:	
	@Brief:	
	@Arguments: N/A	
	@Returns: 
*/
Player.prototype.getDirection = function( ) {

	return ( this._direction );	
};


/**	@name LOAD MODEL MESH( )
	@brief Load a model from file specified.	
	@args url = The string location of the model on disk	
	@Returns N/A
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


Player.prototype.getModels = function(  ){
	return this._rig.getModelMeshes();
};