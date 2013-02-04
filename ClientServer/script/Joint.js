/**	@Author:	James Browne. TODO: Comment, tidy and optamise.
	@Brief:		This is a container for the joint information.
	@Arguments:	name : The name of the joint.
	@Returns:
*/
function Joint( name ){

	this._name = name;
	this._position = new THREE.Vector3( 0,0,0 );
};

/**	@Name:	
	@Brief:	
	@Arguments:	
	@Returns:
*/
Joint.prototype.getPosition = function(  ){

	return ( new THREE.Vector3( this._position.x, this._position.y, this._position.z ) );	
};


/**	@Name:	
	@Brief:		
	@Arguments:		
	@Returns:	
*/
Joint.prototype.setPosition = function( position ){

	this._position = position;
};


/**	@Name:	
	@Brief:
	@Arguments:
	@Returns:
*/
Joint.prototype.remove = function(){
	
};