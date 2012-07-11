
/**	@Name:	Player_Manager Class
	@Author: James Browne	
	@Brief:	A Player_Manager that controls all the players connected to the game.
	TODO

*/
function Player_Manager( checkpoints ){

	// A list of points to walk to. Starts at the start point
	this._walklist = [];
	
	// The Spawn Position
	this._spawnPosition= new THREE.Vector3( 6500,0,6500 ) ;
	
	// The checkpoints that the Player has to move to.
	this._checkpoints = checkpoints;
	
	// The end goal for the Player.
	this._finishPosition = new THREE.Vector3( 0,0,0 );
	
	// Each checkpoint has to trigger this to move to the next.
	this._moveNext = false;
	
	// The Player in question.
	this._player = new Player( "Default", this._spawnPosition );
	this._checkpoints.push( this._spawnPosition );
	
	// Debugging.
	this._autoRotate = true;//false;
	
};



/**	@Name: Update
	@Brief:Check all objects in the scene to see if any collisions occured.
	@Arguments:N/A
	@Returns:N/A
*/
Player_Manager.prototype.update = function(  ){

	// update the player...
	this._player.update();
	// The distance of the player to the next checkpoine.
	var dist = this.getDistance( this._player.getPosition(), this._checkpoints[0] )
	
	if( this._moveNext ){
	
		// If the player is within 50 units of the checkpoint.
		if ( dist < 50 ){
						
			if ( this._checkpoints[0] == this._spawnPosition){
				this._player.setPosition( this._spawnPosition );
				this._moveNext = false;
				return;
			}
			// Check to see if the player has an object in his inventory.
			if( this._player.checkInventory() ){
			
				// If at the position, push it into the walklist again.
				this._checkpoints.push( this._checkpoints[0] );
				// Pop off the so the current goal is the next one.
				this._checkpoints.shift();
				// Remove the equipped item from the Player.
				this._player.removeInventory();	
				// Move to the next position.
				this._moveNext = true;
			}
			else{
				// Stop at the chckpoint and grab an object.
				this._moveNext = false;
			}
		}
		else{
			
			// Get direction.
			var playerPos = this._player.getPosition();
			var goalDir = new THREE.Vector3( this._checkpoints[0].x - playerPos.x, 
								0, 
								this._checkpoints[0].z - playerPos.z );
			//goalDir.normalize();
			
			var playerSight = new THREE.Vector3( this._player.getSightNode().x - playerPos.x, 
								0, 
								this._player.getSightNode().z - playerPos.z );
					//playerSight.normalize();
					
			// Get the angle needed to rotate so we're facing the target.
			if( this._autoRotate ){
				
				var angle = 50;
				while( angle >= 0.1){
					
					playerSight.x = this._player.getSightNode().x - playerPos.x; 
					playerSight.y = 0; 
					playerSight.z = this._player.getSightNode().z - playerPos.z; 
					//playerSight.normalize();
					
					var angle = Math.acos( goalDir.dot( playerSight ) / (goalDir.length() * playerSight.length() ) );
					angle = angle * ( 180 / Math.PI ) ;
				
					this._player.rotateModelLeft();
				}
				//this._player.rotateSightByAngle( angle );
			}
			// Store the goal direction.
			//this._player._goalDirection = goalDir;
			// move in that direction.
			this._player.moveInDirection( goalDir );
			// Rotate joints towards the goal.
			//this._player.rotateToVector( goalDir );
		}
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
Player_Manager.prototype.moveNextPosition = function(  ){

	this._moveNext = true;
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