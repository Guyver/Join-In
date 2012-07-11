/**	@Name:	Level_Manager Class

	@Author: James Browne
	
	@Brief:
	A Level_Manager that controls the flow of the game.
	Tell the scene builder how many levels there are and it will generate them for us.
	Give the scene builder the current level and it will only display that level.
	When the current level changes, tell the scene manager to change scene to the current one.
	Every frame tell the collision manger to handle the collisions.
	Every frame let the player manager handle all the players.

*/
function Level_Manager(  ){

	// The number of different levels.
	this._maxLevels = 5;
	// The current level...
	this._currentLevel = 1;
	// Handle Collisions.
	this._collision_Manager = new Collision_Manager();
	// The objects.
	var objects = [];
	// Handle the Players.
	this._player_Manager = undefined;
	// Handle the Scene.
	//this._scene_Manager = new Scene_Builder( this._maxLevels, this._currentLevel );
	this._cameraPosition = undefined;
	// Camera Type, 1 = 1st, 3 = 3rd.
	this._cameraType = 1;
	
	this.createObjects();

};


/**	@Name: Update
	@Brief:	Check all objects in the scene to see if any collisions occured.
	@Arguments:	N/A
	@Returns:	N/A
*/
Level_Manager.prototype.update = function( objects, camera ){

	// Test to see if the players hands are in an object.
	this.testPickups( this._player_Manager.getPlayer(), objects );
	
	// Test to see if the players equipped items are colliding with the bin.
	this.testDrops( this._player_Manager.getPlayer() , objects );
	
	// Get the collision manager to do its job.
	this._collision_Manager.update( );

	// Get the Player Manager to update the players.
	this._player_Manager.update( );
	
	switch ( this._cameraType ){
		case 1:
			this.firstPersonCamera( this._player_Manager.getPlayer(), camera );
			break;
		case 3:
			this.thirdPersonCamera( this._player_Manager.getPlayer(), camera );
			break;
		default:
			this.firstPersonCamera( this._player_Manager.getPlayer(), camera );
			break;
	};	

	// Update the height of the catchable objects.
	var head = this._player_Manager.getPlayer().getJointPosition( "head" ).y;
	var height = head + 500;
	for ( i in objects ){
	
		var currentPos = objects[ i ].getPosition();
		objects[i].setPosition( new THREE.Vector3( currentPos.x, height, currentPos.z ) );	
	}
	
};


/**	@Name:	
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.testPickups = function( player, objects ){
	
	if( player._inventory.length > 0 ){
		// If the player has an item already then forget it.
		return;
	}
	var lHand = player.getJoint( "leftHand" );
	var rHand = player.getJoint( "rightHand");
	var obj;
	for ( index in objects){
	
		if( objects[ index ]._type != "Bin" ){		
		
			//Test if the meshes collide.
			obj = objects[ index ];
			var coll1 = this._collision_Manager.sphereSphereCollision( lHand._mesh, obj._mesh );
			var coll2 = this._collision_Manager.sphereSphereCollision( rHand._mesh, obj._mesh );	
			
			if( coll1 || coll2 && objects[ index ]._alive ){
				// Ok the hands are on/in an object, is it already equipped?
				if( !objects[ index ]._equipped ){

					player.addInventory( objects[ index ] );	// Add it to the players inventory.
					objects[ index ].equipToMesh( rHand );		// Equip it to the right hands position.
					
				}//end if object equipped
			}// end if coll1&2
			else{//Either the left hand let go or trying to pick up a dead object or you're just not near it.
			
				// Hands dont touch this particular object, if he has it equipped drop it.
				// Make sure you only drop the item you know is not in his inventory.
				
				for ( index in player._inventory ){
				
					var equippedItem = player._inventory[ index ]._mesh;
					
					if( equippedItem.id === objects[ index ]._mesh.id )
					{
						//this.removeItemFromPlayer( player , objects[ index ] );					
					}//End if id's are the same.
					
				}// End each equipped item.
					
			}//End if/else no collision.
		}
	}// End for objects.
	
};//End function.


/**	@Name:	
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.testDrops = function( player, objects ){

	var bin;
	var equippedItem;
	// Test to see if the sphere attached to the player collided with the bin object.
	// Why not check for them all? Shhh you, be quiet. fuuu
	for ( index in player._inventory ){
			
		equippedItem = player._inventory[ index ];
		
		for ( item in objects ){
			
			if( objects[ item ]._type == "Bin" ){
				// Test collision between all the moveables.
				bin = objects[ item ];
				// Does the moveable radius intersect the box radius?
				var coll = this._collision_Manager.sphereSphereCollision( bin._mesh, equippedItem._mesh );
				if( coll == true ){
					this.removeItemFromPlayer( player , objects[ index ] );	
				}// An object needs to be dropped.
			}//Is the object a bin?
		}//Each item.			
	}// End each equipped item.
};// End test drops.


/**	@Name:
	@Brief:	
	Check all objects in the scene to see if any collisions occured.
	@Arguments:
	N/A
	@Returns:
	N/A

*/
Level_Manager.prototype.testCollision = function( scene ){
	
	var walls = [];
	
	this._collision_Manager.testCollision( scene  );
};


/**	@Name:	Remove Item From Player
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.removeItemFromPlayer = function( player, object ){

	object.removeFromMesh();
	this._player_Manager.getPlayer().removeInventory();	
};


/**	@Name:
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.firstPersonCamera = function( player, camera){

	camera.position = this._player_Manager.getPlayer().getJointPosition( "head" );
	
	var dir = new THREE.Vector3(this._player_Manager.getPlayer()._sightNode.x - this._player_Manager.getPlayer().getJointPosition( "head" ).x,
								this._player_Manager.getPlayer()._sightNode.y - this._player_Manager.getPlayer().getJointPosition( "head" ).y,
								this._player_Manager.getPlayer()._sightNode.z - this._player_Manager.getPlayer().getJointPosition( "head" ).z);	
	dir.normalize();
	
	var dist = 150;	
	// Offset the player position in the direction of the sight node.
	camera.position.x += dist * dir.x;
	camera.position.z += dist * dir.z;
	
	camera.lookAt( this._player_Manager.getPlayer().getSightNode() );
};


/**	@Name:
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.thirdPersonCamera = function( player, camera ){

	
	camera.position = this._player_Manager.getPlayer().getJointPosition( "head" );
	camera.position.y += 500;
	
	var dir = new THREE.Vector3(this._player_Manager.getPlayer()._sightNode.x - this._player_Manager.getPlayer().getJointPosition( "head" ).x,
								this._player_Manager.getPlayer()._sightNode.y - this._player_Manager.getPlayer().getJointPosition( "head" ).y,
								this._player_Manager.getPlayer()._sightNode.z - this._player_Manager.getPlayer().getJointPosition( "head" ).z);	
	dir.normalize();
	
	var dist = 1000
	// Offset the player position in the direction of the sight node.
	camera.position.x -= dist * dir.x;
	camera.position.z -= dist * dir.z;
	
	camera.lookAt( this._player_Manager.getPlayer().getSightNode() );
	
	//
	// Set the transparancy of the model to half so we can see in front of ourselves.
	//
	
};


/**	@Name:
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.setCameraType = function( cameraCode ){

	
	switch ( cameraCode ){
		case 1:
			this._cameraType = 1;
			break;
		case 3:
			this._cameraType = 3;
			break;
		default:
			this._cameraType = 1;
			break;
	};
};


/**	@Name:
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.getPlayer = function(  ){

	return ( this._player_Manager.getPlayer() );
};


/**	@Name:
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.createObjects = function(  ){
	
	/* The 4 corners of the map.
	this._checkpoint1 =new THREE.Vector3( 1450,0,1360 );
	this._checkpoint2 =new THREE.Vector3( 13710,0,1360 );
	this._checkpoint3 =new THREE.Vector3( 13710,0,12890 );
	this._checkpoint4 =new THREE.Vector3( 2280,0,13600 );
	*/
	var checkpoints = [];
	checkpoints.push( new THREE.Vector3( 3000, 0, 1360 ) );
	checkpoints.push( new THREE.Vector3( 6000, 0, 1360 ) );
	checkpoints.push( new THREE.Vector3( 9000, 0, 1360 ) );	
	checkpoints.push( new THREE.Vector3( 13800, 0, 3000 ) );
	checkpoints.push( new THREE.Vector3( 13800, 0, 6000 ) );
	checkpoints.push( new THREE.Vector3( 13800, 0, 9000 ) );	
	checkpoints.push( new THREE.Vector3( 9000, 0, 13800 ) );
	checkpoints.push( new THREE.Vector3( 6000, 0, 13800 ) );
	checkpoints.push( new THREE.Vector3( 3000, 0, 13800 ) );	
	checkpoints.push( new THREE.Vector3( 1360, 0, 9000 ) );
	checkpoints.push( new THREE.Vector3( 1360, 0, 3000 ) );
	checkpoints.push( new THREE.Vector3( 1360, 0, 6000 ) );
	
	for ( i in checkpoints ){
		objects.push( new Object( checkpoints[i], "Object" ) );
	}
	this._player_Manager = new Player_Manager( checkpoints );
};

	