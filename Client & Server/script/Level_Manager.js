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
function Level_Manager()
{
	// Handle the Player.
	this._player_Manager = new Player_Manager();
	// The scene manager.
	this._scene_manager = new Scene_Builder();
	// The collision manager.
	this._collisionHandler = new Collision_Manager();
	// The position of the camera.
	this._cameraPosition = undefined;
	
	// Camera Type, 1 = 1st, 3 = 3rd.
	this._cameraType = 3;
};


/**	@Name: Update
	@Brief:	Check all objects in the scene to see if any collisions occured.
	@Arguments:	N/A
	@Returns:	N/A
*/
Level_Manager.prototype.update = function( scene , camera )
{
	// Get the Player Manager to update the players.
	this._player_Manager.update();	
	
	this._collisionHandler.update( this._player_Manager , scene );
	
	switch ( this._cameraType )
	{
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
	
	return false;
};

/**	@Name:
	@Brief:
	@Arguments:
	@Returns:
*/
Level_Manager.prototype.firstPersonCamera = function( player, camera)
{

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
Level_Manager.prototype.thirdPersonCamera = function( player, camera )
{

	
	camera.position = this._player_Manager.getPlayer().getJointPosition( "head" );
	camera.position.y += 500;
	
	var dir = new THREE.Vector3(this._player_Manager.getPlayer()._sightNode.x - this._player_Manager.getPlayer().getJointPosition( "head" ).x,
								this._player_Manager.getPlayer()._sightNode.y - this._player_Manager.getPlayer().getJointPosition( "head" ).y,
								this._player_Manager.getPlayer()._sightNode.z - this._player_Manager.getPlayer().getJointPosition( "head" ).z);	
	dir.normalize();
	
	var dist = 2000;
	
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
Level_Manager.prototype.setCameraType = function( cameraCode )
{

	
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
Level_Manager.prototype.getPlayer = function()
{

	return ( this._player_Manager.getPlayer() );
};
	
Level_Manager.prototype.loadNextLevel = function()
{
	if( g_level == 0 )this.loadLevelOne();
	else if ( g_level == 1 )this.loadLevelTwo();
	else if ( g_level == 2 )this.loadLevelThree();
	else if( g_level == 3 ) window.location.href = '../html/index.html';
	else{}
	g_currentLevelFinished = false;
	g_level++;
};

Level_Manager.prototype.loadLevelOne = function()
{
	// Build the hugging level.
	g_waiting = false;
	// Create the walls, ceiling, floors if needed.
	// Define the map with hash tags.
	var level1 = {
				"map" : [
					"###########",
					"#.........#",
					"#....h....#",
					"#.........#",
					"#.p..k..p.#",
					"#...p.p...#",
					"#.ttttttt.#",
					"#.ttttttt.#",
					"#.........#",
					"###########"
				] 
	};

	// Initalise the scene builder with the hash map.
	this._scene_manager.createHuggingScene( level1 );
	// Set the player locations.
	
	// Place the game objects.
};

Level_Manager.prototype.loadLevelTwo = function()
{
	// Build the reach level.
	g_waiting = false;
	// Create the walls, ceiling, floors if needed.
	// Define the map with hash tags.
	var level2 = {
				"map" : [
					"#####",
					"#...#",
					"#...#",
					"#...#",
					"#...#",
					"#####"
				] 
	};

	// Initalise the scene builder with the hash map.
	this._scene_manager.createReachScene( level2 );
	// Set the player locations.
	
	// Place the game objects.
};

Level_Manager.prototype.loadLevelThree = function()
{
	// Build the lobby level.
	g_waiting = false;
	// Create the walls, ceiling, floors if needed.
	// Define the map with hash tags.
	var level3 = {
				"map" : [
					"##########",
					"#........#",
					"#........#",
					"#........#",
					"#........#",
					"#........#",
					"#...,....#",
					"#........#",
					"#........#",
					"##########"
				] 
	};

	// Initalise the scene builder with the hash map.
	this._scene_manager.createLobbyScene( level3 );
	// Set the player locations.
	
	// Place the game objects.
};
