
/**	@Name:	Player_Manager Class
	@Author: James Browne	
	@Brief:	A Player_Manager that controls all the players connected to the game.
	TODO

*/
function Player_Manager(  ){

	// A list of points to walk to. Starts at the start point
	this._walklist = [];
	// A list of starting locations.
	this._spawnPositions = [];
	this._spawnPositions.push( new THREE.Vector3( 6500,0,6500 ) );
	
	/*this._spawnPositions.push( new THREE.Vector3( 6000,0,3000 ) );
	this._spawnPositions.push( new THREE.Vector3( 9000,0,3000 ) );
	this._spawnPositions.push( new THREE.Vector3( 12000,0,3000 ) );
	*/
	this._checkpoint1 = new THREE.Vector3( 1450,0,1360 );
	this._checkpoint2 =new THREE.Vector3( 13710,0,1360 );
	this._checkpoint3 =new THREE.Vector3( 13710,0,12890 );
	this._checkpoint4 =new THREE.Vector3( 2280,0,13600 );
	/*this._checkpoint5 =new THREE.Vector3( 6000,0,12000 );
	this._checkpoint6 =new THREE.Vector3( 9000,0,12000 );
	this._checkpoint7 =new THREE.Vector3( 12000,0,12000 );
	this._checkpoint8 =new THREE.Vector3( 12000,0,9000 );
	this._checkpoint9 =new THREE.Vector3( 12000,0,6000 );
	this._checkpoint10 =new THREE.Vector3( 12000,0,3000 );
	this._finishPosition = new THREE.Vector3( 0,0,0 );
	*/
	this._walklist.push( this._checkpoint1 );
	this._walklist.push( this._checkpoint2 );
	this._walklist.push( this._checkpoint3 );
	this._walklist.push( this._checkpoint4 );	
	
	this._player = new Player( "Default", this._spawnPositions.pop() );
	this._test = true;
};



/**	@Name: Update
	@Brief:Check all objects in the scene to see if any collisions occured.
	@Arguments:N/A
	@Returns:N/A
*/
Player_Manager.prototype.update = function(  ){

	// update the player...
	this._player.update();
	
	var dist = this.getDistance( this._player.getPosition(), this._walklist[0] )
	
	// If the player is within 200 units of the checkpoint.
	if ( dist < 50 ){
		
		// Add move condition, such as touched object.
		if( true){
			// If at the position, push it into the walklist again.
			this._walklist.push( this._walklist[0] );
		
			this._walklist.shift();
			// Pop off the so the current goal is the next one.
			//this._player.setPosition( this._walklist.shift() );	
			this._test = true;
		}
	}
	else{
		
		// Get direction.
		var playerPos = this._player.getPosition();
		var goalDir = new THREE.Vector3( this._walklist[0].x - playerPos.x, 
							this._walklist[0].y - playerPos.y, 
							this._walklist[0].z - playerPos.z );
		goalDir.normalize();
		
		// Get the angle needed to rotate so we're facing the target.
		if( this._test ){
		
			var playerSight = this._player.getSightNode().subSelf( playerPos );
			playerSight.normalize();
			
			var angle = Math.acos( goalDir.dot( playerSight ) / ( goalDir.length() * playerSight.length() ) );
			//if( (goalDir.x < 0 && goalDir.z > 0) || (goalDir.x > 0 && goalDir.z < 0) ){
				angle *= -1;
			//}
			this._player.rotateSightByAngle( angle );
			this._test = false;
		}
		// Store the goal direction.
		//this._player._goalDirection = goalDir;
		// move in that direction.
		this._player.moveInDirection( goalDir );
		// Rotate joints towards the goal.
		//this._player.rotateToVector( goalDir );
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