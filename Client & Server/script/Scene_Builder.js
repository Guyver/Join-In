
function Scene_Builder( data )
{
	this._blockSize = 5000;
	this._wallMeshes = [];
	this._staticObjects = [];
	this._ground;
	this._ceiling;
	this._skybox;
	
};

Scene_Builder.prototype.createHuggingScene = function( data )
{
	this.destroyCurrentScene ();
	this.createScene( data, image_loader.getAsset( 'wallTexture.png') );
	this.createHouse( 'house', new THREE.Vector3( 8000, -100, 20000 ), new THREE.Vector3( 250,250,250 ), 6000 );
	this.createFloor( image_loader.getAsset( 'grassTile01.png' ) ); 
	this.createKey();
	this.createSkybox( "../img/skybox/" );
};


Scene_Builder.prototype.createReachScene = function( data )
{
	this.destroyCurrentScene ();
	
	// Load the walls
	this.createScene( data, image_loader.getAsset( 'seamlessWall.png' ) );
	
	// Load the floor.
	this.createFloor( image_loader.getAsset( 'wallTexture.png' ) ); 
	
	// Create the ceiling.
	this.createCeiling( image_loader.getAsset( 'popcornCeiling.png' ) ); 
	
	// Create Antiques...
	this.createAntique( 'vase' , new THREE.Vector3( 14645, 2000, 5697), new THREE.Vector3( 100, 100, 100) );	
	this.createAntique( 'vase' , new THREE.Vector3( 14627, 2000, 10197), new THREE.Vector3( 100, 100, 100) );
	this.createAntique( 'vase' , new THREE.Vector3( 14606, 2000, 15597),new THREE.Vector3( 100, 100, 100) );
	this.createAntique( 'vase' , new THREE.Vector3( 14054, 2000, 19154),new THREE.Vector3( 100, 100, 100) );
	this.createAntique( 'vase' , new THREE.Vector3( 10455, 2000, 19080),new THREE.Vector3( 100, 100, 100) );
	this.createAntique( 'vase' , new THREE.Vector3( 5958, 2000, 18931), new THREE.Vector3( 100, 100, 100) );
	this.createAntique( 'vase' , new THREE.Vector3( 4937, 2000, 15479), new THREE.Vector3( 100, 100, 100) );
	this.createAntique( 'vase' , new THREE.Vector3( 5161, 2000, 11886), new THREE.Vector3( 100, 100, 100) );
	this.createAntique( 'vase' , new THREE.Vector3( 5498, 2000, 6496), new THREE.Vector3( 100, 100, 100) );

};


Scene_Builder.prototype.createLobbyScene = function( data )
{
	this.destroyCurrentScene ();
	this.createScene( data, image_loader.getAsset( 'wallTexture.png') );
	this.createHouse( 'house', new THREE.Vector3( 8000, -100, 20000 ), new THREE.Vector3( 250,250,250 ), 6000 );
	this.createFloor( image_loader.getAsset( 'grassTile01.png' ) ); 
	this.createSkybox( "../img/skybox2/" );
};


Scene_Builder.prototype.destroyCurrentScene = function()
{
	for ( i in this._wallMeshes )scene.remove( this._wallMeshes[i] );
	for ( i in this._staticObjects )scene.remove( this._staticObjects[i] );
	scene.remove( this._ground );
	scene.remove( this._ceiling );
	scene.remove( this._skybox );

};


Scene_Builder.prototype.createStaticObject = function( name, position, scale, radius, rotation )
{
	var model = mesh_loader.getModel( name );
	model.mesh.updateMatrix();
	scene.add( model.mesh );

	model.mesh.updateMatrix();
	scene.add( model.mesh );

	model.mesh.name = name;
	model.mesh .castShadow = true;
	model.mesh .boundRadius = radius;
	model.mesh.position.x = position.x;
	model.mesh.position.y = position.y;
	model.mesh.position.z = position.z;
	model.mesh.scale.set( scale.x, scale.y, scale.z );
	model.mesh.rotation.x = -Math.PI/2;
	model.mesh.rotation.z = -Math.PI/2;
	this._staticObjects.push( model.mesh );
};


Scene_Builder.prototype.createScene = function( data, image )
{
	var rows = 0, columns = 0, yPosition, color;

	// Create a texture from an image, image mush be a power of 2 in size. i.e 512*256
	var tex = new THREE.Texture( image, {}, render() );
	
	// Oh yes, it does need this!
	tex.needsUpdate = true;
	
	var geometry = new THREE.CubeGeometry( this._blockSize, this._blockSize, this._blockSize );
		
    for( var rows = 0; rows < data.map.length; rows++ ) 
	{
	
      for( var columns = 0; columns < data.map[ rows ].length; columns++) 
	  {	  
        if (data.map[ rows ][ columns ] == '#') 
		{		
          yPosition = 0;
          color = 0xff0000;		  
        } 
		else 
		{		
          yPosition = -this._blockSize;
          color = 0x0000ff;
        }
		
		var mesh= new THREE.Mesh( geometry, new THREE.MeshBasicMaterial({
				map: tex
			}));
		mesh.doubleSided = true;
		
        mesh.position = new THREE.Vector3( this._blockSize * columns , yPosition, this._blockSize * rows );
		mesh.name = "Wall";
		this._wallMeshes.push( mesh );
        scene.add(  mesh );
      }
    }
};


Scene_Builder.prototype.createHouse = function( name, position, scale, radius )
{
	var model = mesh_loader.getModel( name );
	model.mesh.updateMatrix();
	scene.add( model.mesh );

	model.mesh.updateMatrix();
	scene.add( model.mesh );

	model.mesh.name = "House";
	model.mesh .castShadow = true;
	model.mesh .boundRadius = radius;//500;
	model.mesh.position.x = position.x;
	model.mesh.position.y = position.y;
	model.mesh.position.z = position.z;
	model.mesh.scale.set( scale.x, scale.y, scale.z );
	model.mesh.rotation.x = -Math.PI/2;
	model.mesh.rotation.z = -Math.PI/2;
	this._staticObjects.push( model.mesh );

};


Scene_Builder.prototype.createAntique = function( name, position, scale )
{
	var model = mesh_loader.getModel( name );
	model.mesh.updateMatrix();
	scene.add( model.mesh );

	model.mesh.name = "Antique";
	model.mesh .castShadow = true;
	model.mesh .boundRadius = 500;
	model.mesh.position.x = position.x;
	model.mesh.position.y = position.y;
	model.mesh.position.z = position.z;
	model.mesh.scale.set( scale.x, scale.y, scale.z );
	this._staticObjects.push( model.mesh );
};


Scene_Builder.prototype.createTree = function( name, position )
{
	var model = mesh_loader.getModel( name );
	model.mesh.updateMatrix();
	scene.add( model.mesh );

	model.mesh.name = "Object";
	model.mesh .castShadow = true;
	model.mesh .boundRadius = 500;
	model.mesh.position.x = position.x;
	model.mesh.position.y = position.y;
	model.mesh.position.z = position.z;
	model.mesh.scale.set( 100, 100, 100 );
	this._staticObjects.push( model.mesh );
};


Scene_Builder.prototype.createKey = function( )
{
	var model = mesh_loader.getModel( 'key' );
	g_key = model.mesh;
	g_key .updateMatrix();	
	scene.add( g_key );

	g_key.name = "Key";
	g_key.boundRadius = 500;
	g_key .castShadow = true;
	g_key.position.x = 16000;
	g_key.position.y = 1000;
	g_key.position.z = 20000;
	g_key.scale.set( 100,100,100 );
	this._staticObjects.push( g_key );
};


Scene_Builder.prototype.createCeiling = function( image )
{
	//
	// Create Plane
	//
	var planeTex = new THREE.Texture( image , {}, render() );
	
	planeTex.needsUpdate = true;
	planeTex.wrapT = THREE.RepeatWrapping;
	planeTex.wrapS = THREE.RepeatWrapping;
	planeTex.repeat.set( 10, 10 );// Higher for smaller tiles
	
	var planeGeo = new THREE.PlaneGeometry( 100000, 100000, 1, 10);
	
	var ceiling = new THREE.Mesh( planeGeo, new THREE.MeshBasicMaterial({
		 map: planeTex
	}));
	
	ceiling .position.y = this._blockSize/2 - 50;
	ceiling.position.x = 7500;
	ceiling.position.z = 7500;
	ceiling .receiveShadow = true;
	ceiling.doubleSided = true;
	scene.add( ceiling );
	this._ceiling = ceiling;

};


Scene_Builder.prototype.createFloor = function( image )
{
	//
	// Create Plane
	//
	var planeTex = new THREE.Texture( image , {}, render() );
	
	planeTex.needsUpdate = true;
	planeTex.wrapT = THREE.RepeatWrapping;
	planeTex.wrapS = THREE.RepeatWrapping;
	planeTex.repeat.set( 10, 10 );// Higher for smaller tiles
	
	var planeGeo = new THREE.PlaneGeometry( 100000, 100000, 1, 10);
	
	var ground = new THREE.Mesh( planeGeo, new THREE.MeshBasicMaterial({
		 map: planeTex
	}));
	
	//ground .rotation.x = -Math.PI / 2;
	ground .position.y = 0;
	ground .receiveShadow = true;
	ground.doubleSided = true;
	scene.add( ground );
	this._ground = ground;
};


Scene_Builder.prototype.createSkybox = function( url )
{
	var urlPrefix = url; // "../img/skybox/";
		var urls = [ urlPrefix + "x.png", urlPrefix + "-x.png", urlPrefix + "y.png", urlPrefix + "-y.png", urlPrefix + "z.png", urlPrefix + "-z.png" ];
		var textureCube	= THREE.ImageUtils.loadTextureCube( urls );
		textureCube.needsUpdate = true;
		
		var shader	= THREE.ShaderUtils.lib["cube"];
		shader.uniforms["tCube"].texture = textureCube;
		
		var material = new THREE.ShaderMaterial({
			
			fragmentShader	: shader.fragmentShader,
			vertexShader	: shader.vertexShader,
			uniforms	: shader.uniforms
		});

		skyboxMesh	= new THREE.Mesh( new THREE.CubeGeometry( 100000000, 100000000, 100000000, 1, 1, 1, null, true ), material );
		skyboxMesh.doubleSided = true;
		
		scene.add( skyboxMesh );
		this._skybox = skyboxMesh;
};