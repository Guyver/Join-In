/**

	TODO: Document, tidy, optamise.


*/

function Object( position, type, meshUrl ){

	this._id = undefined;
	this._mesh = meshUrl;
	this._position = position ;
	this._equipped = false;
	this._owner = undefined;
	this._alive = true;
	this._loaded = false;
	this._type = type;		// Is is the bin or and object.
	that = this;
	this.createMesh();
};


/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
Object.prototype.setPosition = function( position ){
	
	this._position = position;
	this._mesh.position = this._position;
};


/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
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


/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
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


/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
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


/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
Object.prototype.getPosition = function(){

	return( this._position );

};


/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
Object.prototype.equipToMesh = function( owner ){

	this._owner = owner;
	this._equipped = true;
};


/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
Object.prototype.removeFromMesh = function(){

	this._owner = undefined;
	this._equipped = false;
	this._alive = true;
	this._position = new THREE.Vector3( 0,0,0 );
	this._mesh.position = this._position;
};


/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
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


/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
Object.prototype.isOwned = function(){
	
	if( this._equipped )
	return true;
	return false;
};