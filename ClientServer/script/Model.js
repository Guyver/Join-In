/**	@Name:	Model Class
	@Author: James Browne	
	@Brief: A model that represents all the joint data from the kinect.
	Upon creation there will be 15 joints.
	After construction the individual joints data will be passed as a map.

*/
var that;

function Model( jointNames, models, playerPos ){

	this._joint 		= {};				        // Map of joint objects.
	this._jointNames 	= jointNames;				// Array of key values for the kinect data.
	this._jointModels 	= [];
	this._translatedMap = {};
	this._head 			= models['head'];
	this._torso 		= models[ 'torso'];
	this._hand_l 		= models['hand_l'];
	this._hand_r 		= models['hand_r'];
	this._forearm_l 	= models['forearm_l'];
	this._forearm_r 	= models['forearm_r'];
	this._upperArm_l 	= models['upperArm_l'];
	this._upperArm_r	= models['upperArm_r'];
	this._upperLeg_l 	= models['upperLeg_l'];
	this._upperLeg_r 	= models['upperLeg_r'];
	this._lowerLeg_l 	= models['lowerLeg_l'];
	this._lowerLeg_r 	= models['lowerLeg_r'];
	this._foot_l 		= models['foot_l'];
	this._foot_r 		= models['foot_r'];

	that = this;
	this._angle = 0;
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
Model.prototype.update = function( sightNode, kinectData ){

	// 
	// Set the positions of the models each frame. Surely a cleaner way but works for now. 
	//	
	if ( kinectData != undefined && kinectData != null )
	{
			
		this._head.position 		= 	this.getJointPosition( "shouldercenter" );
		this._torso.position 		= 	this.getJointPosition( "shouldercenter" );
		this._upperArm_l.position 	= 	this.getJointPosition( 'shoulderleft' );
		this._upperArm_r.position 	= 	this.getJointPosition( 'shoulderright' );
		this._forearm_l.position 	= 	this.getJointPosition( 'elbowleft' );
		this._forearm_r.position 	= 	this.getJointPosition( 'elbowright' );
		this._hand_l.position 		= 	this.getJointPosition( 'handleft' );
		this._hand_r.position     	= 	this.getJointPosition( 'handright' );
		this._upperLeg_l.position 	= 	this.getJointPosition( 'hipleft' );
		this._upperLeg_r.position 	= 	this.getJointPosition( 'hipright' );
		this._lowerLeg_l.position 	= 	this.getJointPosition( 'kneeleft' );
		this._lowerLeg_r.position 	= 	this.getJointPosition( 'kneeright' );
		this._foot_l.position 		= 	this.getJointPosition( 'footleft' );
		this._foot_r .position 		= 	this.getJointPosition( 'footright' );	
		
		// This is so the torso points in between the hips
		var h1 = this.getJointPosition( 'hipleft' );
		var h2 = this.getJointPosition( 'hipright' ); 
		var hh = new THREE.Vector3( h2.x - h1.x, h2.y - h1.y, h2.z - h1.z )
		hh.x = hh.x / 2;
		hh.y = hh.y / 2;
		hh.z = hh.z / 2;
		var x = new THREE.Vector3( hh.x + h2.x, hh.y + h2.y, hh.z + h2.z )
		
		//
		// Set the orientation of the models each frame.
		//
		this._head.lookAt(  sightNode  );
		this._torso.lookAt( x );
		this._foot_l.lookAt( sightNode );
		this._foot_r.lookAt( sightNode );
		this._hand_l.lookAt( sightNode );
		this._hand_r.lookAt( sightNode );
		this._upperArm_l.lookAt( this.getJointPosition( 'elbowleft') );
		this._upperArm_r.lookAt( this.getJointPosition( 'elbowright') );
		this._forearm_l.lookAt( this.getJointPosition( 'handleft') );
		this._forearm_r.lookAt( this.getJointPosition( 'handright') );
		this._upperLeg_l.lookAt( this.getJointPosition( 'kneeleft') );
		this._upperLeg_r.lookAt( this.getJointPosition( 'kneeright') );
		this._lowerLeg_l.lookAt( this.getJointPosition( 'footleft') );
		this._lowerLeg_r.lookAt( this.getJointPosition( 'footright') );
		
	}// If the kinect data isn't bogus.	
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
Model.prototype.getJoint = function( name ){
	
	return ( this._joint[ name ] );

};


/**	@Name:
	@Brief:	Orientation will have to be applied in here.
	The joints must be drawn to face the sight node.
	@Arguments:
	@Returns:

*/
Model.prototype.setAllJoints = function( playerPos, angle, kinectMap ){

	// The kinect pos of the joint to rotate and the Vector to that from the TORSO.
	var joint  = new THREE.Vector3( 0,0,0 );
	
	// The distance of the joint from the torso.
	var jointFromTorso = new THREE.Vector3( 0,0,0 );
	// Torso
	try{
		var torso = new THREE.Vector3( kinectMap[ "spine" ].x, kinectMap[ "spine" ].y, kinectMap[ "spine" ].z );		
	}catch(err){
		var torso = new THREE.Vector3( 0,0,0 );
	}
	// The position of the new joint after being translated from the player position.
	var translatedPos = new THREE.Vector3( 0,0,0 );
	
	// Cycle through the joints and set the according to the map.
	for ( i in this._jointNames )
	{	
		try
		{
			// Get a joint from the kinect map.
			joint = new THREE.Vector3( kinectMap[ this._jointNames[ i ] ].x , kinectMap[ this._jointNames[ i ] ].y , kinectMap[ this._jointNames[ i ] ].z );
		}
		catch( err )
		{
		
		}

		// Get the translation.
		jointFromTorso.sub( joint , torso);
		// To set the sight node set the angle initally.
		if( angle != 0.0 ){
	
			// The axis we want to rotate about.
			var axis = new THREE.Vector3( 0, 1, 0 );
			
			// Create the translation matrix with a scale of 1 using the axis and degree of rotation.
			var matrix = new THREE.Matrix4().makeRotationAxis( axis, angle );

			// Apply the rotation to the sight node.
			matrix.multiplyVector3( jointFromTorso );			
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
		
		// Store the new, translated, kinect joints.
		this._translatedMap[ this._jointNames[ i ] ] = translatedPos;
		
	}// End for
	
	// Return the player pos for the next iteration. 
	return playerPos;
	
};//End set all Joints


/**
	Loop through all the translated kinect joints and set the joint positions.
	
*/
Model.prototype.setPreTrannyPts= function( map ){

	
		for ( i in this._jointNames ){
		
			this._joint[ this._jointNames[ i ] ].setPosition(	new THREE.Vector3( 
			
					map[ this._jointNames[ i ] ].x , 
					map[ this._jointNames[ i ] ].y , 
					map[ this._jointNames[ i ] ].z 
			) );
		
		}
};


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

	 return ( this._joint[ 'spine' ].getPosition() );
			
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
