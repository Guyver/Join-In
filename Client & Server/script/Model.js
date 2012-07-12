/**	@Name:	Model Class
	@Author: James Browne	
	@Brief: A model that represents all the joint data from the kinect.
	Upon creation there will be 15 joints.
	After construction the individual joints data will be passed as a map.

*/
var that;

function Model( jointNames, playerPos ){

	this._joint = {};				        // Map of joint objects.
	this._jointNames = jointNames;			// Array of key values for the kinect data.
	this._jointModels = [];
	this._head = undefined;
	this._torso = undefined;
	this._upperArmL = undefined; 
	this._upperArmR = undefined; 
	this._lowerArmL = undefined; 
	this._lowerArmR = undefined; 
	this._handL = undefined;
	this._handR = undefined; 
	this._upperLegL = undefined;
	this._upperLegR = undefined;
	this._lowerLegL = undefined;
	this._lowerLegR = undefined;
	this._footL = undefined;
	this._footR = undefined;
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
	
	// Create the limbs.
	this.createLimbs();
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
	if ( kinectData != undefined && kinectData != null && this._jointModels.length == 14 ){
	
		this._head.position 		= 	this.getJointPosition( "neck" );
		this._torso.position 		= 	this.getJointPosition( "torso" );
		this._upperArmL.position 	= 	this.getJointPosition( "leftShoulder" );
		this._upperArmR.position 	= 	this.getJointPosition( "rightShoulder" );
		this._lowerArmL.position 	= 	this.getJointPosition( "leftElbow" );
		this._lowerArmR.position 	= 	this.getJointPosition( "rightElbow" );
		this._handL.position 		= 	this.getJointPosition( "leftHand" );
		this._handR.position     	= 	this.getJointPosition( "rightHand" );
		this._upperLegL.position 	= 	this.getJointPosition( "leftHip" );
		this._upperLegR.position 	= 	this.getJointPosition( "rightHip" );
		this._lowerLegL.position 	= 	this.getJointPosition( "leftKnee" );
		this._lowerLegR.position 	= 	this.getJointPosition( "rightKnee" );
		this._footL.position 		= 	this.getJointPosition( "leftFoot" );
		this._footR.position 		= 	this.getJointPosition( "rightFoot" );	
		//
		// Set the orientation of the models each frame.
		//
		this._head.lookAt(  sightNode  );
		this._torso.lookAt( sightNode );
		this._footL.lookAt( sightNode );
		this._footR.lookAt( sightNode );
		this._handL.lookAt( sightNode );
		this._handR.lookAt( sightNode );
		this._upperArmL.lookAt( this.getJointPosition( "leftElbow") );
		this._upperArmR.lookAt( this.getJointPosition( "rightElbow") );
		this._lowerArmL.lookAt( this.getJointPosition( "leftHand") );
		this._lowerArmR.lookAt( this.getJointPosition( "rightHand") );
		this._upperLegL.lookAt( this.getJointPosition( "leftKnee") );
		this._upperLegR.lookAt( this.getJointPosition( "rightKnee") );
		this._lowerLegL.lookAt( this.getJointPosition( "leftFoot") );
		this._lowerLegR.lookAt( this.getJointPosition( "rightFoot") );
		
	}// If the kinect data isn't bogus.	
	//
	// Do the scaling before the positioning otherwise it will be off.
	//	
	var needsScale = false;
	if( needsScale ){
		
		for ( each in this._jointModels ){
	
			this._jointModels[ each ].scale.set( 10, 10, 10 );
			this._jointModels[ each ].updateMatrix();
		}
		needsScale = false;
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
	var joint  = new THREE.Vector3(0,0,0);
	
	// The distance of the joint from the torso.
	var jointFromTorso = new THREE.Vector3(0,0,0);
	// Torso
	try{
		var torso = new THREE.Vector3( kinectMap[ "torso" ].x, kinectMap[ "torso" ].y, kinectMap[ "torso" ].z );		
	}catch(err){
		var torso = new THREE.Vector3(0,0,0);
	}
	// The position of the new joint after being translated from the player position.
	var translatedPos = new THREE.Vector3(0,0,0);
	
	// Cycle through the joints and set the according to the map.
	for ( i in this._jointNames ){
		try{
			// Get a joint from the kinect map.
			joint = new THREE.Vector3( kinectMap[ this._jointNames[ i ] ].x , kinectMap[ this._jointNames[ i ] ].y , kinectMap[ this._jointNames[ i ] ].z );
		}catch(err){
		
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
		
	}// End for
	
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
Model.prototype.createLimbs = function( ){

	// Load all of the models that represent the model.

	var url = 'model/limbs/'
	var modelNames = [  "head.dae", "torso.dae", "armUpper_l.dae", "armUpper_r.dae",
						"foot_l.dae", "foot_r.dae", "hand_l.dae","hand_r.dae",
						"foreArm_l.dae", "foreArm_r.dae", "legLower_r.dae","legLower_l.dae",
						"legUpper_l.dae", "legUpper_r.dae" ];
						
	var x = new THREE.ColladaLoader();	
	
	for ( i in modelNames ){
		
		switch( modelNames[ i ] ){
			
			case "head.dae":
				x.load( url + modelNames[i] , this.head );
				break;
			case "torso.dae":
				x.load( url + modelNames[i] , this.torso );
				break;
			case "armUpper_l.dae":
				x.load( url + modelNames[i] , this.armUpperL );
				break;
			case "armUpper_r.dae":
				x.load( url + modelNames[i] , this.armUpperR);
				break;
			case "foot_l.dae":
				x.load( url + modelNames[i] , this.footL );
				break;
			case "foot_r.dae":
				x.load( url + modelNames[i] , this.footR );
				break;
			case "hand_l.dae":
				x.load( url + modelNames[i] , this.handL );
				break;
			case "hand_r.dae":
				x.load( url + modelNames[i] , this.handR );
				break;
			case "foreArm_l.dae":
				x.load( url + modelNames[i] , this.foreArmL );
				break;
			case "foreArm_r.dae":
				x.load( url + modelNames[i] , this.foreArmR );
				break;
			case "legLower_r.dae":
				x.load( url + modelNames[i] , this.legLowerR );
				break;
			case "legLower_l.dae":
				x.load( url + modelNames[i] , this.legLowerL );
				break;
			case "legUpper_l.dae":
				x.load( url + modelNames[i] , this.legUpperL );
				break;
			case "legUpper_r.dae":
				x.load( url + modelNames[i] , this.legUpperR );
				break;
			default:
				break;
			}
	}
};


/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/
Model.prototype.head= function( collada ){

	that._head = collada.scene;
	that._head.updateMatrix();
	scene.add( that._head );
	
	that._head.name = "HEAD";
	that._head.scale.set(30,30,30);
	that._head.position = that.getJointPosition( "head");

	that._jointModels.push( that._head );
};
 
 
 /**@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/
Model.prototype.torso= function( collada ){
 
	that._torso = collada.scene;
	that._torso.updateMatrix();
	scene.add( that._torso );
	
	that._torso.name = "TORSO";
	that._torso.scale.set(20,20,20);
	that._torso.position = that.getJointPosition( "torso");
	
	that._jointModels.push( that._torso ); 
 };
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.armUpperL= function( collada ){
 
	that._upperArmL = collada.scene;
	that._upperArmL.updateMatrix();
	scene.add( that._upperArmL );
	
	that._upperArmL.name = "ARM_UPPER_L";
	that._upperArmL.scale.set(50,50,50);
	that._upperArmL.position = that.getJointPosition( "leftShoulder");
	
	that._jointModels.push( that._upperArmL );
 };
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.armUpperR = function( collada ){
 
	that._upperArmR = collada.scene;
	that._upperArmR.updateMatrix();
	scene.add( that._upperArmR );
	
	that._upperArmR.name = "ARM_UPPER_R";
	that._upperArmR.scale.set(50,50,50);
	that._upperArmR.position = that.getJointPosition( "rightShoulder");
	
	that._jointModels.push( that._upperArmR );
 };

 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.footL= function( collada ){
	
	that._footL = collada.scene;
	that._footL.updateMatrix();
	scene.add( that._footL );
	
	that._footL.name = "FOOT_L";
	that._footL.scale.set(50,50,50);
	that._footL.position = that.getJointPosition( "leftFoot");
	
	that._jointModels.push( that._footL );
 };
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.footR= function ( collada ){
 
	that._footR = collada.scene;
	that._footR.updateMatrix();
	scene.add( that._footR );
	
	that._footR.name = "FOOT_R";
	that._footR.scale.set(50,50,50);
	that._footR.position = that.getJointPosition( "rightFoot");
	
	that._jointModels.push( that._footR );
 };

 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.foreArmL= function( collada ){
 
	that._lowerArmL = collada.scene;
	that._lowerArmL.updateMatrix();
	scene.add( that._lowerArmL );
	
	that._lowerArmL.name = "FOREARM_L";
	that._lowerArmL.scale.set(50,50,50);
	that._lowerArmL.position = that.getJointPosition( "leftElbow");
	
	that._jointModels.push( that._lowerArmL );
 
 };
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.foreArmR= function( collada ){
 
	that._lowerArmR = collada.scene;
	that._lowerArmR.updateMatrix();
	scene.add( that._lowerArmR );
	
	that._lowerArmR.name = "FOREARM_R";
	that._lowerArmR.scale.set(50,50,50);
	that._lowerArmR.position = that.getJointPosition( "rightElbow");
	
	that._jointModels.push( that._lowerArmR );
 };
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
 Model.prototype.legUpperL= function( collada ){
 
	that._upperLegL = collada.scene;
	that._upperLegL.updateMatrix();
	scene.add( that._upperLegL );
	
	that._upperLegL.name = "LEG_UPPER_L";
	that._upperLegL.scale.set(50,50,50);
	that._upperLegL.position = that.getJointPosition( "leftHip");
	
	that._jointModels.push( that._upperLegL );
 
 };
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.legUpperR= function( collada ){
 
	that._upperLegR = collada.scene;
	that._upperLegR.updateMatrix();
	scene.add( that._upperLegR );
	
	that._upperLegR.name = "LEG_UPPER_R";
	that._upperLegR.scale.set(50,50,50);
	that._upperLegR.position = that.getJointPosition( "rightHip");
	
	that._jointModels.push( that._upperLegR );
 };
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.legLowerR= function( collada ){
 
	that._lowerLegR = collada.scene;
	that._lowerLegR.updateMatrix();
	scene.add( that._lowerLegR );
	
	that._lowerLegR.name = "LEG_LOWER_R";
	that._lowerLegR.scale.set(50,50,50);
	that._lowerLegR.position = that.getJointPosition( "rightKnee");
	
	that._jointModels.push( that._lowerLegR );
};
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.legLowerL= function( collada ){
 
	that._lowerLegL = collada.scene;
	that._lowerLegL.updateMatrix();
	scene.add( that._lowerLegL );
	
	that._lowerLegL.name = "LEG_LOWER_L";
	that._lowerLegL.scale.set(50,50,50);
	that._lowerLegL.position = that.getJointPosition( "leftKnee");
	
	that._jointModels.push( that._lowerLegL );
 
 };
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.handR= function( collada ){
 
	that._handR = collada.scene;
	that._handR.updateMatrix();
	scene.add( that._handR );
	
	that._handR.name = "HAND_R";
	that._handR.scale.set(50,50,50);
	that._handR.position = that.getJointPosition( "rightHand");
	
	that._jointModels.push( that._handR );
};
 
 
/**	@Name:
	@Brief:	
	@Arguments:
	@Returns:
*/ 
Model.prototype.handL= function( collada ){
 
    that._handL = collada.scene;
	that._handL.updateMatrix();
	scene.add( that._handL );
	
	that._handL.name = "HAND_L";
	that._handL.scale.set(50,50,50);
	that._handL.position = that.getJointPosition( "leftHand");
	
	that._jointModels.push( that._handL ); 
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