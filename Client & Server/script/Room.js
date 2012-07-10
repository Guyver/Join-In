/**
	For the server to control connections there are seperate rooms.
	Each room contains a collections of players that are playing the same game at the same time.
	Each time a consortium joins the server spawns a new room and adds associated players to the room.
	The players Kinect data, database and other game logic will be stored in this room in the users list.
*/
function Room(){

	// A list of the players for this game.
	this._players = undefined;
	// The game that is being played.
	this._game = undefined;
	
};