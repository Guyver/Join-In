/**

	TODO
	The object class...duh


*/


function Object( position, type ){

	this._id = undefined;
	this._mesh = undefined;
	this._position = position ;
	this._equipped = false;
	this._owner = undefined;
	this._alive = true;
	this._velocity = new THREE.Vector3( 0,0,0);
	this._acceleration = new THREE.Vector3( 0,0,0);
	this._direction = new THREE.Vector3( 0,0,0);
	this._type = type;		// Is is the bin or and object.
	this.createMesh();
};

Object.prototype.createMesh = function(){

	switch( this._type ){
		case "Object":
			this.createMoveable();
			break;
		case "Bin":
			this.createDropzone();
			break;
		case "Stairs":
			this.createStairs();
			break;
		default:
			break;
	
	}
};


/*
	Eventually add in the objects also.

*/
Object.prototype.createMoveable = function(){

	var radius = 300, segments = 10, rings = 10;
	var Material = new THREE.MeshLambertMaterial( {color: 0xff00ff });
	var Geometry = new THREE.SphereGeometry( radius, segments, rings );
	
	// The mesh of the Joint. Contains physical properties.
	this._mesh = new THREE.Mesh( Geometry , Material );	
	this._mesh.name	= this._type;
	// Add ourself to the scene.
	scene.add( this._mesh );	
	
	this._mesh.position = this._position;

};

Object.prototype.createDropzone = function( ){
	
	var radius = 500, segments = 10, rings = 10;
	var Material = new THREE.MeshLambertMaterial( {color: 0x00ff00 });
	var Geometry = new THREE.CubeGeometry( 500,500,500 );
	
	// The mesh of the Joint. Contains physical properties.
	this._mesh = new THREE.Mesh( Geometry , Material );	
	this._mesh.name	= this._type;
	// Add ourself to the scene.
	scene.add( this._mesh );	
	
	this._mesh.position = this._position;


};

Object.prototype.getPosition = function(){

	return( this._position );

};

Object.prototype.equipToMesh = function( owner ){

	this._owner = owner;
	this._equipped = true;
};

Object.prototype.removeFromMesh = function(){

	this._owner = undefined;
	this._equipped = false;
	//this._alive = false;
};

Object.prototype.update = function(){

	// If you run with it, you wil drop it.
	
	if( this._equipped ){
		
		this._position.x = this._owner.getPosition().x;
		this._position.y = this._owner.getPosition().y;
		this._position.z = this._owner.getPosition().z;
		// Offset from the center of the owner so we can see it.
		this._position.y += 100;
		this._position.z += 100;
		this._mesh.position = this._position;
	}
	
};