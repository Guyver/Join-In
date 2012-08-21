
/**	@Name:	Player_Manager Class
	@Author: James Browne	
	@Brief:	A Player_Manager that controls all the players connected to the game.
	TODO

*/
function Player_Manager(  ){

	// A list of points to walk to. Starts at the start point
	this._walklist = [];
	
	// The Spawn Position
	this._spawnPosition= new THREE.Vector3(  16000 , 100 ,10000 ) ;
	this._spawnPosition2= new THREE.Vector3( 20000,0,14000 ) ;
	this._spawnPosition3= new THREE.Vector3( 20000,0,20000 ) ;
	this._spawnPosition4= new THREE.Vector3( 16000,0,26000 ) ;

	// The Player in question.
	this._player = new Player( "Player", undefined, this._spawnPosition );
	this._otherPlayers = [];
	// Begin the game.
	this._playGame = false;
	
	// The game is over. Dont start initally.
	this._gameOver = false;
	
	// Score Multiplier. Can be changed on various logic.
	this._scoreMultiplier = 1;
	
	// Game timer
	this._lastTime = new Date();
	this._currentTime = new Date();	
	this._deltaTime = undefined;

};



/**	@Name: Update
	@Brief:Check all objects in the scene to see if any collisions occured.
	@Arguments:N/A
	@Returns:N/A
*/
Player_Manager.prototype.update = function(  ){

	// update the player...
	this._player.update();
	for ( i in this._otherPlayers ){
		this._otherPlayers[ i ].update();
	}
};


/**	@Name: 
	@Brief:
	@Arguments:N/A
	@Returns:N/A
*/
Player_Manager.prototype.getPlayer = function(  ){

	return ( this._player );
};


/**	@Name: 
	@Brief:
	@Arguments:N/A
	@Returns:N/A
*/
Player_Manager.prototype.getPlayerScore = function(  ){

	return ( this._player.getScore() );
};


/**	@Name: 
	@Brief:
	@Arguments:N/A
	@Returns:N/A
*/
Player_Manager.prototype.playGame = function(  ){

	this._playGame = true;
};


/**	@Name: 
	@Brief:
	@Arguments:N/A
	@Returns:N/A
*/
Player_Manager.prototype.isGameReady = function(  ){

	return this._playGame;
};


/**	@Name: Distance
	@Brief:	
	@Arguments:
	@Returns:
*/
Player_Manager.prototype.getDistance = function( objA, objB ){

	var dx,dz,x2,z2,dist;
	// Deltas.
	dx = objA.x - objB.x;
	dz = objA.z - objB.z;
	// Squares.
	x2 = dx * dx;
	z2 = dz * dz;
	// Root
	return ( Math.sqrt( x2 + z2 ) );
};