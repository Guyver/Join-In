
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
	this._player = new Player( "Player", mesh_loader.cloneLimbs() , this._spawnPosition );
	this._otherPlayers = [];
	this._otherPlayers.push( new Player( "Player2", mesh_loader.cloneLimbs() , this._spawnPosition2 ) );
	this._otherPlayers.push( new Player( "Player3", mesh_loader.cloneLimbs() , this._spawnPosition3 ) );
	this._otherPlayers.push( new Player( "Player4", mesh_loader.cloneLimbs() , this._spawnPosition4 ) );
	
	
	// The checkpoints that the Player has to move to.
	this._checkpoints = [];
	this._checkpoints.push( 	new THREE.Vector3( 14645, 2000, 5697) 	);
	this._checkpoints.push( 	new THREE.Vector3( 14627, 2000, 10197) 	);
	this._checkpoints.push( 	new THREE.Vector3( 14606, 2000, 15597) 	);	
	this._checkpoints.push( 	new THREE.Vector3( 14054, 2000, 19154) 	);
	this._checkpoints.push( 	new THREE.Vector3( 10455, 2000, 19080) 	);
	this._checkpoints.push( 	new THREE.Vector3( 5958, 2000, 18931) 	);	
	this._checkpoints.push( 	new THREE.Vector3( 4937, 2000, 15479) 	);
	this._checkpoints.push( 	new THREE.Vector3( 5161, 2000, 11886) 	);
	this._checkpoints.push( 	new THREE.Vector3( 5498, 2000, 6496) 	);	

	// The end goal for the Player.
	this._finishPosition = new THREE.Vector3( 0,0,0 );

	// Each checkpoint has to trigger this to move to the next.
	this._moveNext = false;

	// Score Multiplier. Can be changed on various logic.
	this._scoreMultiplier = 1;

	// Game timer
	this._lastTime = new Date();
	this._currentTime = new Date();	
	this._deltaTime = undefined;

	// Used to face towards the targets automatically.
	this._autoRotate = true;

};



/**	@Name: Update
	@Brief:Check all objects in the scene to see if any collisions occured.
	@Arguments:N/A
	@Returns:N/A
*/
Player_Manager.prototype.update = function(  ){

	// update the player...
	this._player.update();
	try{
		for ( var k = 0; k < this._otherPlayers.length; k++ ){
		
			if( this._otherPlayers[ k ]._kinectData != undefined ){ 
			
				this._otherPlayers[ k ]._rig.setPreTrannyPts( this._otherPlayers[ k ]._kinectData );
				this._otherPlayers[ k ]._rig.update( this._otherPlayers[ k ]._sightNode, this._otherPlayers[ k ]._kinectData );
			}
		}	
	}
	catch( error )
	{
		console.log( "The other players got broked... :(" );
	}
	
	if( g_level == 2 )
	{
		this._spawnPosition= new THREE.Vector3(  11829 , 100 ,4463 );
		var playerPos = this._player.getPosition();
		// The distance of the player to the next checkpoint.
		var dist = this.getDistance( this._player.getPosition(), this._checkpoints[ 0 ] )

		if( true )// Play game or something
		{	
			// If the player is within 50 units of the checkpoint.
			if ( dist < 50 )
			{
				// Is this the last position?
				if( this._checkpoints[ 1 ] != undefined )
				{
					// Check to see if the player has an object in his inventory.
					if( this._player.checkInventory() )
					{
						// Pop off the so the current goal is the next one.
						this._checkpoints.shift();
						// Remove the equipped item from the Player.
						this._player.removeInventory();

						// Take the current time
						this._currentTime = new Date();
						// Get the change in time from "last"
						this._deltaTime = this._currentTime.getTime() - this._lastTime.getTime();
						// Reset last time to now.
						this._lastTime = this._currentTime;	

						this._player._split = this._deltaTime;
						// Calculate the multiplier from the time taken.
						if( this._deltaTime < 3000)
							this._scoreMultiplier = 5;
						else if( this._deltaTime < 4000)
							this._scoreMultiplier = 4;
						else if( this._deltaTime < 5000)
							this._scoreMultiplier = 3;
						else if( this._deltaTime < 6000)
							this._scoreMultiplier = 2;
						else if( this._deltaTime < 7000)
							this._scoreMultiplier = 1.5;
						else
							this._scoreMultiplier = 1;

						// Increment the score of the player.
						this._player.addScore( 100 * this._scoreMultiplier );
					}
				}
				else
				{
					g_objectsCollected = true;
				}
			}
			else
			{
				var goalDir = new THREE.Vector3( this._checkpoints[0].x - playerPos.x, 
									0, 
									this._checkpoints[0].z - playerPos.z );

				var playerSight = new THREE.Vector3( this._player.getSightNode().x - playerPos.x, 
									0, 
									this._player.getSightNode().z - playerPos.z );

				// Get the angle needed to rotate so we're facing the target.
				if( this._autoRotate )
				{
					var angle = 50;
					while( angle >= 0.1 )
					{
						playerSight.x = this._player.getSightNode().x - playerPos.x; 
						playerSight.y = 0; 
						playerSight.z = this._player.getSightNode().z - playerPos.z; 

						var angle = Math.acos( goalDir.dot( playerSight ) / (goalDir.length() * playerSight.length() ) );
						angle = angle * ( 180 / Math.PI ) ;

						this._player.rotateModelLeft();
					}
				}
				
				// Move in the facing direction.
				this._player.moveInDirection( goalDir );
			}	
		}
	}
	
	if( g_level == 3 )
	{
		this._spawnPosition= new THREE.Vector3(  16000,100,10000 ) ;
		this._spawnPosition2= new THREE.Vector3( 20000,0,14000 ) ;
		this._spawnPosition3= new THREE.Vector3( 20000,0,20000 ) ;
		this._spawnPosition4= new THREE.Vector3( 16000,0,26000 ) ;
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