/**	@Author:	TODO: Comment, tidy and optamise.
	@Brief:	
	@Arguments:	
	@Returns:
*/
function Joint( name ){

	this._accel = new THREE.Vector3();		    // Usually Gravity if ignoring external force.
	this._velocity = new THREE.Vector3();		// Velocity vector.
	this._direction = new THREE.Vector3();		// Direction vector, maybe use a quaternion.

	var colour;
	switch( name ){
		case "head":
			colour = {color: 0x000000000 };
			break;
		case "neck":
			colour = {color: 0x000000000 };
			break;
		case "leftShoulder":
			colour = {color: 0x000000000 };
			break;
		case "rightShoulder":
			colour ={color:  0x000000000} ;
			break;
		case "leftElbow":
			colour ={color:  0xfff000000} ;
			break;
		case "rightElbow":
			colour = {color: 0xfff000000};
			break;
		case "leftHand":
			colour = {color: 0xfff000000};
			break;
		case "rightHand":
			colour = {color: 0xffffff000};
			break;
		case "torso":
			colour = {color: 0xffffff000};
			break;
		case "leftHip":
			colour = {color: 0x000ffffff};
			break;
		case "rightHip":
			colour = {color: 0x000ffffff};
			break;
		case "leftKnee":
			colour = {color: 0x000fff000};
			break;
		case "rightKnee":
			colour = {color: 0x000fff000};
			break;
		case "leftFoot":
			colour = {color: 0xfff000000};
			break;
		case "rightFoot":
			colour = {color: 0xfff000000};
			break;
		default:
			//colour = {color: 0x000000000};
			break;
			
	}
	
	// Set up the sphere vars
	var radius = 10, segments = 10, rings = 10;
	this._Material = new THREE.MeshLambertMaterial( {color: 0xff00ff } );
	this._Geometry = new THREE.SphereGeometry( radius, segments, rings );
	
	// The mesh of the Joint. Contains physical properties.
	this._mesh = new THREE.Mesh( this._Geometry , this._Material );		
	this._mesh.name = name;
	// Add ourself to the scene.
	scene.add( this._mesh );	
	
};

/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
Joint.prototype.getPosition = function(  ){

	return ( this._mesh.position );	
};


/**	@Name:	
	@Brief:		
	@Arguments:		
	@Returns:	
*/
Joint.prototype.setPosition = function( position ){

	this._mesh.position =  position;
};


/**	@Name:	
	@Brief:
	@Arguments:
	@Returns:
*/
Joint.prototype.remove = function(){
	
	scene.remove( this._mesh );
	renderer.deallocateObject( this._mesh );
};