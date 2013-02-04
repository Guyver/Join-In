/**
	@AUTHOR: James Browne.
	@DESCRIPTION:
	This code is to take all the mesh urls of the models to be loaded
	and process them all sequentially.
	All the models in the scene will be loaded in here and our objects
	will request the model it needs at upon creation.
	
	@TODO:
	Considering there are multiple players in the game all using the limb
	models. I'll add code in here to duplicate models to save reading from
	file again.

*/

var mesh_loader = {	
	
	_meshUrls : 	[],								// Array of maps. key : name, value : url
	_loadedMesh : 	[],								// Array of maps. key : name, value : mesh
	_loaded : 		false,							// Bool to tell if the loading is finished.
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
			this._loaded = true;
		}
	},
	
	callback : function test( collada ){
		
		// Store a reference to ourselves, assuming the 'this' is a reference to the window.
		var _self = mesh_loader;
		// Store the model data with a name in the loaded mesh structure.
		_self._loadedMesh.push( { name : _self._currMeshName, mesh : collada.scene } );	
		
		// Pop off the loaded mesh and call the next load.
		_self._meshUrls.shift( );
		_self.loadItem( );
	}	
};