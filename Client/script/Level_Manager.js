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
	// Handle the Players.
	this._player_Manager = new Player_Manager();
	// Handle the Scene.
	//this._scene_Manager = new Scene_Builder( this._maxLevels, this._currentLevel );
	this._cameraPosition = undefined;
	// Camera Type, 1 = 1st, 3 = 3rd.
	this._cameraType = 1;

};


/**	@Name: Update
	@Brief:	
	Check all objects in the scene to see if any collisions occured.
	@Arguments:
	N/A
	@Returns:
	N/A

*/
Level_Manager.prototype.update = function( player, objects, camera ){

	// Test to see if the players hands are in an object.
	this.testPickups( player, objects );
	
	// Test to see if the players equipped items are colliding with the bin.
	this.testDrops( player, objects );
	
	// Get the collision manager to do its job.
	this._collision_Manager.update( );

	// Get the Player Manager to update the players.
	this._player_Manager.update( );
	
	switch ( this._cameraType ){
		case 1:
			this.firstPersonCamera( player, camera );
			break;
		case 3:
			this.thirdPersonCamera( player, camera );
			break;
		default:
			this.firstPersonCamera( player, camera );
			break;
	};
	
	
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
	var lHand = player._rig._joint["leftHand"];
	var rHand = player._rig._joint["rightHand"];
	var obj;
	for ( index in objects){
	
		if( objects[ index ]._type != "Bin" ){		
		
			//Test if the meshes collide.
			obj = objects[ index ];
			var coll1 = this._collision_Manager.sphereSphereCollision( lHand._mesh, obj._mesh );
			var coll2 = this._collision_Manager.sphereSphereCollision( rHand._mesh, obj._mesh );	
			
			if( coll1 && coll2 && objects[ index ]._alive ){
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


/**	@Name: Update
	@Brief:	
	Check all objects in the scene to see if any collisions occured.
	@Arguments:
	N/A
	@Returns:
	N/A

*/
Level_Manager.prototype.testCollision = function( objA, scene ){
	
	var walls = [];
	
	this._collision_Manager.testCollision( objA, scene  );
};


/**	@Name:	Remove Item From Player
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.removeItemFromPlayer = function( player, object ){

	object.removeFromMesh();
	player.removeInventory();	

};


/**	@Name:
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.firstPersonCamera = function( player, camera){

	camera.position = player.getJointPosition( "head" );
	
	var dir = new THREE.Vector3(player._sightNode.x - player.getJointPosition( "head" ).x,
								player._sightNode.y - player.getJointPosition( "head" ).y,
								player._sightNode.z - player.getJointPosition( "head" ).z);	
	dir.normalize();
	
	var dist = 150;	
	// Offset the player position in the direction of the sight node.
	camera.position.x += dist * dir.x;
	camera.position.z += dist * dir.z;
	
	camera.lookAt( player.getSightNode() );
};


/**	@Name:
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.thirdPersonCamera = function( player, camera ){

	
	camera.position = player.getJointPosition( "head" );
	camera.position.y += 500;
	
	var dir = new THREE.Vector3(player._sightNode.x - player.getJointPosition( "head" ).x,
								player._sightNode.y - player.getJointPosition( "head" ).y,
								player._sightNode.z - player.getJointPosition( "head" ).z);	
	dir.normalize();
	
	var dist = 1000
	// Offset the player position in the direction of the sight node.
	camera.position.x -= dist * dir.x;
	camera.position.z -= dist * dir.z;
	
	camera.lookAt( player.getSightNode() );
	
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