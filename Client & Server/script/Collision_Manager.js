/**	@Name:	Collision_Manager Class
	@Author: James Browne	
	@Brief:
	A Collision_Manager that controls all collisions regarding the 3d objects.
	Only look after your own players collision and then all the objects/Walls.
	DO NOT change to do all the player mesh collisions as its not nesisary.

*/
function Collision_Manager(  ){



};


/**	@Name: Test Collision
	@Brief:	
	@Arguments:
	@Returns:
*/
Collision_Manager.prototype.testCollision = function( player_manager, scene )
{
	var player = player_manager.getPlayer();
	var leftHand =  player._rig.getJoint( "leftHand" ).getPosition();
	var rightHand = player._rig.getJoint( "rightHand" ).getPosition();
	
	var sceneMeshes = scene.children;
	var wallMeshes = [];
	var playerMeshes = [];
	var moveableObjects = [];
	var imoveableObjects = [];
	var collision = false;
	
	// Cycle through all the meshes in the scene.
	for ( index in sceneMeshes )
	{		
		// Pick out the walls.
		if( sceneMeshes[ index ].name === "Wall" )
		{		
			wallMeshes.push( sceneMeshes[ index ] );
		}
		// Pick out the players, objects, left and right hand.
		else if( sceneMeshes[ index ].name === "Player" ||  sceneMeshes[ index ].name === "Player2" || 
					sceneMeshes[ index ].name === "Player3" || sceneMeshes[ index ].name === "Player4")
		{
			
			playerMeshes.push( sceneMeshes[ index ] );
		}
		else if( sceneMeshes[ index ].name === "Antique" || sceneMeshes[ index ].name === "Key")
		{			
			moveableObjects.push( sceneMeshes[ index ] );
		}
		else if( sceneMeshes[ index ].name === "House" )
		{
			imoveableObjects.push( sceneMeshes[ index ] );
		}
	}
	
	// For all the moveable objects, test against the hands.
	for ( index in moveableObjects )
	{	
		var coll1 = this.sphereSphereCollision( leftHand, moveableObjects[ index ].position );
		var coll2 = this.sphereSphereCollision( rightHand, moveableObjects[ index ].position );
		
		if( coll1 && coll2 )
		{		
			var collision = true;
			console.log( "Someones hands have collided with object %i", moveableObjects[ index ].id );
			if( moveableObjects[ index ].name == "Key" ) g_gotKey = true;
			else if ( moveableObjects[ index ].name == "Antique" )player.addInventory( moveableObjects[ index ] );
		}
	}	
	
	// For all the players, test against the wall radius.
	for ( index in imoveableObjects )
	{		
		for( i in playerMeshes )
		{
			var collision = this.ballWall( player._mesh , imoveableObjects[ index ] );
			
			if( collision )
			{
				player.resetPosition();
			}
		}
	}
	
	// Collision with the house.
	for ( index in playerMeshes )
	{		
		for( i in wallMeshes )
		{
			var collision = this.ballWall( player._mesh , wallMeshes[ i ] );
			
			if( collision )
			{
				player.resetPosition();
			}
		}
	}
	
	return { mesh : "", flag : "" };
};//End sphere collision.


/**	@Name: Sphere - Sphere Collision
	@Brief:	Do 2 spheres intersect?
	@Arguments:	Obj1, obj2
	@Returns: true if a collision
*/
Collision_Manager.prototype.sphereSphereCollision = function( objA, objB ){

	var dist,rad1,rad2;
	dist = this.getDistance( objA, objB );
	rad1 = 300;
	rad2 = 300;
	
	if( dist < ( rad1 + rad2 ) ){
	
		return true;
	}
	
	return false;	
};//End sphere collision.


/**	@Name: Distance
	@Brief:	
	@Arguments:
	@Returns:
*/
Collision_Manager.prototype.getDistance = function( objA, objB ){

		var dx,dy,dz,x2,y2,z2,dist;
		// Deltas.
		dx = objA.x - objB .x;
		dy = objA.y - objB .y;
		dz = objA.z - objB .z;
		// Squares.
		x2 = dx * dx;
		y2 = dy * dy;
		z2 = dz * dz;
		// Root
		return ( Math.sqrt( x2 + y2 + z2 ) );
};


/** @Name: ballWall


*/
Collision_Manager.prototype.ballWall = function( ball, wall ){
	
	var insideBound = this.checkBounding( ball, wall );
	
	if ( insideBound ){	
		return insideBound;
	}// end if in bound

};// end ball wall collision


/**	@Name: Check Boundng
	@Brief:	Check if an objects bounding sphere is inside anothers.
	@Arguments: objA - Three.mesh, objB - Three.mesh
	@Returns:
*/
Collision_Manager.prototype.checkBounding = function( objA, objB ){
	
	var dist,rad1,rad2;
	dist = this.getDistance( objA.position, objB.position );
	rad1 = objA.boundRadius;
	rad2 = objB.boundRadius;
	
	if( dist < (rad1 + rad2) ){
	
		return true;
	}	
	return false;
};


/**	@Name: Update
	@Brief:	Check all objects in the scene to see if any collisions occured.
	@Arguments:	N/A
	@Returns:N/A
*/
Collision_Manager.prototype.update = function( player_manager, scene )
{	
	var info = this.testCollision( player_manager , scene );
	
	if( info.flag == "Reset Player" )
	{	
		return ( info );
	}
	
	if( info.flag == "Got Key" )
	{	
		return ( info );
	}
	
	if( info.flag == "Got Antique" )
	{	
		return ( info );
	}
	
};