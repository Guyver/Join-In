function Scene_Builder( data ){

	this._blockSize = 1000;
	this._blockSizeHeight= 8000;
	this._blockSizeWidth = 1000;
	this._blockSizeDepth = 1000;
	
	this.createScene( data )
};


Scene_Builder.prototype.createScene = function( data ){
	
	var rows = 0, columns = 0, yPosition, color, rotation = 0;

	// Create a texture from an image, image mush be a power of 2 in size. i.e 512*256
	var texture_blue = new THREE.Texture( imageManager.getAsset('img/seamlessWall.jpg', {}, render()));
	// Oh yes, it does need this!
	texture_blue.needsUpdate = true;
	//texture_blue.wrapT = THREE.RepeatWrapping;
	//texture_blue.wrapS = THREE.RepeatWrapping;
	texture_blue.repeat.set( 1 , 2 );// Higher for smaller tiles
	
	var geometry = new THREE.CubeGeometry( this._blockSize, this._blockSizeHeight, this._blockSizeDepth );
		
    for( var rows = 0; rows < data.map.length; rows++ ) {
	
      for( var columns = 0; columns < data.map[rows].length; columns++) {
	  
        if ( data.map[rows][columns] == '#' ) {

			if( rows == 0 || rows == 15 )
				rotation = 0;
			else
				rotation = Math.PI /2;
			yPosition = 0;
			color = 0xff0000;
		  
        } 
		else {
		
			yPosition = this._blockSizeHeight;
			color = 0x0000ff;
			rotation = 0;
        }

		var texture = new THREE.Mesh( geometry, new THREE.MeshBasicMaterial({
				map: texture_blue
			}));
			
		texture.doubleSided = true;
		// Start at 0,0,0 and finishes at (15000, 0, 15000)
        texture.position = new THREE.Vector3( this._blockSize*columns, yPosition, this._blockSize*rows );
		texture.name = "Wall";
		texture.rotation.y = rotation;
		
        scene.add(  texture );
      }
    }
	
	this.createRoof();
};


Scene_Builder.prototype.createRoof = function( ){

	var planeTex = new THREE.Texture(imageManager.getAsset('img/popcornCeiling.png', {}, render()));
	
	planeTex.needsUpdate = true;
	planeTex.wrapT = THREE.RepeatWrapping;
	planeTex.wrapS = THREE.RepeatWrapping;
	planeTex.repeat.set( 5,5 );// Higher for smaller tiles
	
	var planeGeo = new THREE.PlaneGeometry(15000, 15000, 1, 10);

	var ceiling = new THREE.Mesh( planeGeo, new THREE.MeshBasicMaterial({
		 map: planeTex
	}));
	
	ceiling .position.y = this._blockSizeHeight/2 - 50;
	ceiling.position.x = 7500;
	ceiling.position.z = 7500;
	ceiling .receiveShadow = true;
	ceiling.doubleSided = true;
	scene.add( ceiling );

};


Scene_Builder.prototype.placeObjects = function( ){



};