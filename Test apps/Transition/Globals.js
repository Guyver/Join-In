

var CANVAS_GUI, CANVAS_GAME, CANVAS_TRANS, CTX_GUI, CTX_GAME, CTX_TRANS;
CANVAS_GUI = document.getElementById( 'gui' );
CANVAS_GAME = document.getElementById( 'game' );
CANVAS_TRANS = document.getElementById( 'trans' );
CTX_GUI = CANVAS_GUI.getContext( '2d' ); 
CTX_GAME = CANVAS_GAME.getContext( '2d' ); 
CTX_TRANS = CANVAS_TRANS.getContext( '2d' );

var MESH_LOADED, MESH_LOADING, OBJECTS_LOADED, OBJECTS_LOADING, LEVEL_LOADED, LEVEL_LOADING , PLAYERS_LOADED, PLAYERS_LOADING;

var CURRENT_LEVEL, PLAY_GAME, GAME_INTERVAL;

var TUTORIAL_VIDEOS;
var INTRO_VIDEO = document.getElementById( 'introVid' );
var INTRO_PLAYED = false;
var INTRO_PLAYING = false;

var LEVEL_ONE = {

	_tutorialVideo : document.getElementById( 'tutVid1' ),
	_imageURL : './Img/loading.png',
	_image : new Image(),
	_loading : false,
	_loaded : false,
	_complete : false,
	_playingVid : false,
	_videoFinished : false,
	
	
	_init : function(){
	
		this._image.src = this._imageURL;
	},
	
	_playTutorial : function()
	{	
		if( !this._videoFinished )
		{
			if( !this._playingVid )
			{
				this._tutorialVideo.play();
				this._playingVid = true;
			}
			else
			{
				this._draw();
				this._checkVideoStatus();
			}
		}
		else
		{
			this._loaded = true; // TODO: ADD LEVEL INIT		
		}
		
	},
	
	_checkVideoStatus : function()
	{	
		if( this._tutorialVideo.ended )
		{
			this._videoFinished = true;
		}
	},
	
	_draw : function()
	{		
		CTX_TRANS.drawImage(this._tutorialVideo, 0, 0 ,window.innerWidth, window.innerHeight );
	}
	
};LEVEL_ONE._init();

var LEVEL_TWO = {

	tutorialVideo : document.getElementById( 'tutVid2' ),
	_image : './Img/level_two.png',
	_loading : false,
	_loaded : false,
	_playingVid : false,
	_videoFinished : false,
	
	_playTutorial : function()
	{	
		this._tutorialVideo.width = window.innerWidth;
		this._tutorialVideo.height = window.innerHeight;
		this._tutorialVideo.play();
		this._playingVid = true;	
	},
	
	_checkVideoStatus : function()
	{	
		if( this._tutorialVideo.ended ) this._videoFinished = true;
	}
};

var getDistance3D = function( objA, objB ){

		var dx,dy,dz,x2,y2,z2,dist;
		// Deltas.
		dx = objA.x - objB.x;
		dy = objA.y - objB.y;
		dz = objA.z - objB.z;
		// Squares.
		x2 = dx * dx;
		y2 = dy * dy;
		z2 = dz * dz;
		// Root
		return ( Math.sqrt( x2 + y2 + z2 ) );
};

var resize = function(){
    
	var currentAlpha = CTX_GUI.globalAlpha;
	CANVAS_GUI.width = window.innerWidth;
	CANVAS_GUI.height = window.innerHeight;	
	CTX_GUI.globalAlpha = currentAlpha;
	
	currentAlpha = CTX_GAME.globalAlpha;
	CANVAS_GAME.width = window.innerWidth;
	CANVAS_GAME.height = window.innerHeight;	
	CTX_GAME.globalAlpha = currentAlpha;
	
	currentAlpha = CTX_TRANS.globalAlpha;
	CANVAS_TRANS.width = window.innerWidth;
	CANVAS_TRANS.height = window.innerHeight;
	CTX_TRANS.globalAlpha = currentAlpha;
	
};resize();

var onLoad = function(){
	
	GAME_INTERVAL = setInterval( 'game()', 1/60 );
};

var fadeIn = function(){

	if( CTX_TRANS.globalAlpha > 0.01 )
	{
		// Fade out the video canvas' transparency.
		CTX_TRANS.globalAlpha += 0.005;
		resize();
		setTimeout( 'fadeIn()', 50 );
		GAME_INTERVAL = clearInterval( GAME_INTERVAL );
	}
	else
	{	
		onLoad();
	}
};

var fadeOut = function(){

	// Fade out the video canvas' transparency.
	CTX_TRANS.globalAlpha -= 0.005;
};

var MESH_LOADER = {	
	
	_meshUrls : 	[],								// Array of maps. key : name, value : url
	_loadedMesh : 	[],								// Array of maps. key : name, value : mesh
	_loader :		new THREE.ColladaLoader(),		// The Three.js loader for collada files.
		
	addMesh : function does( name , url ){	
	
		// Add url to _meshUrls. Increment _numMeshes.
		this._meshUrls.push( { url : url, name : name, loaded : false } );
	},
	
	beginLoading : function it( ){	
	
		// Set off the loading cycle.	
		this.loadItem( );		
	},

	loadItem : function matter( ){
		
		if( this._meshUrls[ 0 ] != undefined || this._meshUrls[ 0 ] != null ){
		
			// Set the name of the loading object..
			this._currMeshName = this._meshUrls[ 0 ].name;
			// Get the url of the loading object..
			var url = this._meshUrls[ 0 ].url;
			// Begin the loading of the object..
			this._loader.load( url, this.callback );
		}
		else{
		
			console.log( "The mesh urls structure is empty. Loading should be finished." );
			MESH_LOADED = true;
		}
	},
	
	callback : function test( collada ){
		
		// Store a reference to ourselves, assuming the 'this' is a reference to the window.
		var _self = MESH_LOADER;
		// Store the model data with a name in the loaded mesh structure.
		_self._loadedMesh.push( { name : _self._currMeshName, mesh : collada.scene } );	
		
		// Pop off the loaded mesh and call the next load.
		_self._meshUrls.shift( );
		_self.loadItem( );
	}	
};

