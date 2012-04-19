/**	@name Player.js
	@Author: James Browne
	
	@Description:
	The user's class. This will hold all the data relevant to the player.
	

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
function Player( name, id, meshUrl, position){

	// The name of the user.
	this._name = name;
	// The unique id, i.p address for Example.
	this._id = id;
	// The avatar url.
	this._model = this.loadModelMesh( meshUrl );
	// The data for the joints
	this._rig = new Model( jointList );
	// The global position.
	this._position = position;
	// THe velocity of the player.
	this._velocity = new THREE.Vector3(0,0,0);
	// The acceleration...
	this._accel = new THREE.Vector3(0,-9.81,0);
	// The walkspeed, could replace velocity.
	this._walkSpeed = 5;
	// The direction of the player.
	this._direction = new THREE.Vector3(0,0,0);
};



/**	@name UPDATE()

	@brief
	Updates an instance of the player.
	
	@args
	dt = A decimal value that represents the time since last frame.
	
	@Returns
	N/A
*/
Player.prototype.update = function( dt ){
	
	// Calculate new position and set in model.
	
	// Calculate new velocity.
	
	// Calculate new Direction and set in model.

};



/**	@name SYNC JOINTS(  ) 

	@brief
	Updates the joint positions of the player using the kinect data.
	
	@args
	jointMap = A map object containing the names and positions of the player's joints.
	
	@Returns
	N/A

*/
Player.prototype.syncJoints = function( jointMap ){

	this._rig.setAllJoints( jointMap );
};



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



/**	@name ROTATE(  )
	
	@brief
	Rotate an face in the direction of the position specified.
	
	@args
	pos = the vector3 position to orient towards. 
	
	@Returns
	N/A
*/
Player.prototype.rotate = function( pos ){

	this._model.rotation.x = pos;
};



/**	@name LOAD MODEL MESH( path to model file )

	@brief
	Load a model from file specified.
	
	@args
	url = The string location of the model on disk
	
	@Returns
	N/A
*/
Player.prototype.loadModelMesh = function( url ){
	var that = this;
	new THREE.ColladaLoader().load( url ,function( collada ){
		that._model = collada;
		//model.scale.set(0.1,0.1,0.1);
		that._model.scene.rotation.x = -Math.PI/2;
		scene.add( that._model.scene );
	});
};