/**
	@Author: James Browne
	
	@Brief:
	A model has 15 joints and this is the template class for them all.

*/
function Joint(  ){

	this._accel = new THREE.Vector3();		// Usually Gravity if ignoring external force.
	this._velocity = new THREE.Vector3();		// Velocity vector.
	this._direction = new THREE.Vector3();		// Direction vector, maybe use a quaternion.
	
	// Set up the sphere vars
	var radius = 10, segments = 10, rings = 10;
	this._Material = new THREE.MeshLambertMaterial( {color: 0x000000 });
	this._Geometry = new THREE.SphereGeometry( radius, segments, rings );
	
	// The mesh of the Joint. Contains physical properties.
	this._mesh = new THREE.Mesh( this._Geometry , this._Material );		
	
	// Add ourself to the scene.
	scene.add( this._mesh );						
}



/*
	@Brief:
	NOT USED
	
	@Arguments:

*/
Joint.prototype.move = function( dt ){

	// New position. S = U + T + 1/2 x A x (TxT)
	this._pos = this._pos + this._velocity + dt + 0.5 * this._accel * ( dt * dt );
	
	// New Veloctiy. V = U + A x T
	this._velocity = this._velocity + this._accel * dt;
	
	// Calculate the accleration of the Joint.
	CalcualteAccel();
	
	// Draw yourself.
	Render();
};



/*
	@Brief:
	NOT USED
	
	@Arguments:

*/
Joint.prototype.calcualteAccel = function( extForce, gravity){

	this._accel = gravity + extForce;
};



/*
	@Brief:
	NOT USED
	
	@Arguments:

*/
Joint.prototype.render = function(  ){


};



/*
	@Brief:
	NOT USED
	
	@Arguments:

*/
Joint.prototype.setMaterial = function( ){

};



/*
	@Brief:
	NOT USED
	
	@Arguments:

*/
Joint.prototype.setMesh = function(  ){
	// Probably redundant!

};


/*
	@Brief:
	Get the position of the Joint.
	
	@Arguments:
	N/A

*/
Joint.prototype.getPosition = function(  ){

	return ( this._mesh.position );
	
};

/*
	@Brief:
	Set the position of the Joint using the arguments.
	
	@Arguments:
	position: The position to set the joint...

*/
Joint.prototype.setPosition = function( position ){

	// The mesh hold the properties, not the joint class.
	this._mesh.position = position;
	
};