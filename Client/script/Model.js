/**	@Name:	Model Class

	@Author: James Browne
	
	@Brief:
	A model that represents all the joint data from the kinect.
	Upon creation there will be 15 joints.
	After construction the individual joints data will be passed as a map.

*/
function Model( jointNames, playerPos ){

	this._joint = {};				        // Map of joint objects.
	this._jointNames = jointNames;			// Array of key values for the kinect data.
	
	// Construct all the joints.
	for( var i = 0; i < this._jointNames.length; i++){
	
		this._joint[ this._jointNames[ i ] ] = new Joint( this._jointNames[ i ] );
		
		// Give it a random position.
		this._joint[ this._jointNames[ i ] ].setPosition(  new THREE.Vector3( 
			playerPos.x, playerPos.y, playerPos.z
		));		
	}
};



/**	@Name:

	@Brief:
	
	@Arguments:
	
	@Returns:

*/
Model.prototype.setJointPosition = function( name, pos ){

	this._joint[ name ].setPosition( pos );
};

/**	@Name:
	@Brief:	
	@Arguments:	
	@Returns:
*/
Model.prototype.getJointPosition = function( name ){
	
	return ( this._joint[ name ].getPosition( ) );

};

/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:

*/
Model.prototype.translateJoints = function( playerPos, sightNode, kinectMap ){
	// Translate the joints from kinect space into 3d space.
	
	if( kinectMap["head"] == undefined ){
		return;
	}
	
	// Get the vector starting from the torso to each of the joints.
	
};


/**	@Name:
	@Brief:	
	Orientation will have to be applied in here.
	The joints must be drawn to face the sight node.
	@Arguments:
	@Returns:

*/
Model.prototype.setAllJoints = function( playerPos, angle, kinectMap ){

	// The kinect pos of the joint to rotate and the Vector to that from the TORSO.
	var joint  = new THREE.Vector3(0,0,0);
	var jointFromTorso = new THREE.Vector3(0,0,0);	
	var torso;
	
	try{
		// Initalise the TORSO to the kinect position, we will use this to get the vector from the TORSO to the JOINT.	
		torso = new THREE.Vector3( kinectMap[ "torso" ].x, kinectMap[ "torso" ].y, kinectMap[ "torso" ].z );		
	}catch( err ){
		torso = new THREE.Vector3(0,0,0);
	}
	
	// The position after we add it to the PLAYERPOS in game space.
	var translatedPos = new THREE.Vector3(0,0,0);
	
	// Cycle through the joints and set the according to the map.
	for ( i in this._jointNames ){
	
		try{
			joint = new THREE.Vector3( kinectMap[ this._jointNames[ i ] ].x , kinectMap[ this._jointNames[ i ] ].y , kinectMap[ this._jointNames[ i ] ].z );
		}catch( err ){
			console.log("There was a problem creating a new vector from the kinect map, it's probably null.");
			joint = new THREE.Vector3(0,0,0);
		}
		
		// When its the TORSO's turn as the joint, the jointFromTorso will be Zero and will be the same position as the player when translated.
		try{
			jointFromTorso.x = joint.x - torso.x;
			jointFromTorso.y = joint.y - torso.y;
			jointFromTorso.z = joint.z - torso.z;
			
		}catch( err ){
		
			console.log(" Error subtracting the torso from the joint and assigning it ot the jointFromTorso");
		}
		
		if( angle != 0 ){
	
			// The axis we want to rotate about.
			var axis = new THREE.Vector3( 0, 1, 0 );
			
			// Create the translation matrix with a scale of 1 using the axis and degree of rotation.
			var matrix = new THREE.Matrix4().makeRotationAxis( axis, angle );

			// Apply the rotation to the sight node.
			matrix.multiplyVector3( jointFromTorso );

			console.log("Rotating the joint while is still in Kinect space at the origin");
		}
		
		// Translate the new joint vector from the PLAYERPOS / TORSO.
		translatedPos = this.translatePos( playerPos, jointFromTorso );
		
		// This is to move the center of the player up so their feet are above ground. 50 is the feet.
		while( translatedPos.y - 50 < 0 ){
			// Raise so the feet are on the ground.
			playerPos.y+=1;
			// Recalculate.
			translatedPos = this.translatePos( playerPos, jointFromTorso );
		}
		
		// Set the translated joint position to the position of the corresponding joint object's position.
		this._joint[ this._jointNames[ i ] ].setPosition( new THREE.Vector3( translatedPos.x, translatedPos.y, translatedPos.z ) );
		
	}// End for
	
	//Reset the angle for next rotation.
	angle = 0;
	
	// Return the player pos for the next iteration. 
	return playerPos;
	
};//End set all Joints


/**	@Name: Move
	@Brief:	
	@Arguments:
	@Returns:

*/
Model.prototype.move = function( newPos ){

	var pos = new THREE.Vector3( newPos.x, newPos.y, newPos.z );
	pos.y+= 150;
	for ( index in this._joint ){
	
		this._joint[ index ].setPosition( pos );
	}
};


/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:

*/
Model.prototype.translatePos = function( playerPos, kinectPos ){

	var newPos = new THREE.Vector3( 0,0,0 );
	
	return ( newPos.add(  playerPos, kinectPos ) );
			
};


/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:

*/
Model.prototype.getPosition = function(  ){

	 return ( this._joint[ 'torso' ].getPosition() );
			
};


/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:

*/
Model.prototype.remove = function(){
	
	//Remove all joints from the scene.
	for ( index in this._joint ){
		this._joint[ index ].remove();
	}
};