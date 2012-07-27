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
Collision_Manager.prototype.testCollision = function( scene ){
	
	var sceneMeshes = scene.children;
	var wallMeshes = [];
	var playerMeshes = [];
	var moveableObjects = [];
	var leftHand, rightHand;
	var collision = false;
	
	// Cycle through all the meshes in the scene.
	for ( index in sceneMeshes ){
		
		// Pick out the walls.
		if( sceneMeshes[ index ].name === "Wall" ){
		
			wallMeshes.push( sceneMeshes[ index ] );
		}
		// Pick out the players, objects, left and right hand.
		else if( sceneMeshes[ index ].name === "Player" ){
			
			playerMeshes.push( sceneMeshes[ index ] );
		}
		else if( sceneMeshes[ index ].name === "Object" ){
			
			moveableObjects.push( sceneMeshes[ index ] );
		}
		else if( sceneMeshes[ index ].name === "leftHand" ){
			
			leftHand = sceneMeshes[ index ] 
		}
		else if( sceneMeshes[ index ].name === "rightHand" ){
			
			rightHand = sceneMeshes[ index ] 
		}
	}
	// For all the players, test against the wall radius.
	for ( index in playerMeshes ){
		
		for( i in wallMeshes )
		{
			var collision = this.ballWall( playerMeshes[ index ] , wallMeshes[ i ] );
			
			if( collision ){
				console.log( "Player has collided with wall %i", wallMeshes[ i ].id );
			}
		}
	}
	// For all the moveable objects, test against the hands.
	for (index in moveableObjects){
	
		var coll1 = this.sphereSphereCollision( leftHand, moveableObjects[ index ] );
		var coll2 = this.sphereSphereCollision( rightHand, moveableObjects[ index ] );
		if( coll1 && coll2 ){
		
			var collision = true;
			console.log( "Someones hands have collided with object %i", moveableObjects[ index ].id );
		}
	}	
};//End sphere collision.


/**	@Name: Sphere - Sphere Collision
	@Brief:	
	@Arguments:	
	@Returns:
*/
Collision_Manager.prototype.sphereSphereCollision = function( objA, objB ){

	var dist,rad1,rad2;
	dist = this.getDistance( objA.position, objB.position );
	rad1 = objA.boundRadius;
	rad2 = objB.boundRadius;
	
	if( dist < (rad1 + rad2) ){
	
		return true;
	}
	
	return false;	
};//End sphere collision.


/**	@Name:	Cube - Cube Collision
	@Brief:	Axis Aligned Bounding Boxes
	@Arguments:
	@Returns:
*/
Collision_Manager.prototype.cubeCubeCollision = function( objA, objB ){


};//End cube collision


/**	@Name:	Plane - Sphere Collision
	@Brief:	Point - line collision detection.
	@Arguments:
	@Returns:
*/
Collision_Manager.prototype.planeSphereCollision = function( objA, plane ){

};


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


/**	@Name: S.A.T 
	@Brief:	Seperating axis Therom. Convex polygons
	@Arguments:
	@Returns:
*/
Collision_Manager.prototype.SAT = function( objA, objB ){
	
	// Test number of edges.
	
	// Store edges.
	
	// Remove parallel edges and normalise.
	
	// For each edge project all points of both objects.
	
	// Store the min and max values for each object.
	
	// Check the min max values to determin a seperation.
	
	// Break on seperation.
	
	// Continue for all edges.
	 return false;
		
};


Collision_Manager.prototype.ballWall = function( ball, wall ){
	
	var insideBound = this.checkBounding( ball, wall );
	
	if ( insideBound ){
	
		return insideBound;
		// Inside bound but not colliding for definate yet.
		/*
			// Cast a ray from position in the direction.
			var leftRay = new THREE.Ray( ball.position, wallvector );
			// Find out what it intersected.
			var c = ray.intersectObject( meshes );
		*/
		var colliding = this.SAT( ball, wall );
		
		if( colliding ){
			// objects are colliding, seperate and apply impulse.
			
		}// end if colliding
		
	}// end if in bound

};// end ball wall collision


/**	@Name: Check Boundng
	@Brief:	Check if an objects bounding sphere is inside anothers.
	@Arguments: objA - Three.mesh, objB - Three.mesh
	@Returns:
*/
Collision_Manager.prototype.checkBounding = function( objA, objB ){
	
	var dist,rad1,rad2;
	dist = this.getDistance( objA, objB );
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
Collision_Manager.prototype.update = function(  ){

	// Get all objects in the scene.
	
	// Test for collisions between all...expensive bt hey, its a prototype.
	
	// if the left hand, right hand and object are in collision pick up the object.
	
	// As soon as they are not in contact anymore update the object so it adheres to gravity.	

};