/*
	For this to reconstruct a level at runtime.
		* An array, with multiple level designs.
		* Be edited to allow for player position.
		* Be edited to allow for object placement.
			- P = Player.
			- O = Object/s
			- D = Door
			- H = Help Zone

my_scene = {
				"map" : [
					"##########",
					"#........#",
					"#...#....#",
					"#...#....#",
					"#........#",
					"##########"
				] 
			};


*/
function Scene_Builder( data ){

	this._blockSize = 1000;
	this.createScene( data )

};

Scene_Builder.prototype.createScene = function( data ){
	
	var rows = 0, columns = 0, yPosition, color;

	// Create a texture from an image, image mush be a power of 2 in size. i.e 512*256
	var texture_blue = new THREE.Texture(imageManager.getAsset('img/wallTexture.png', {}, render()));
	// Oh yes, it does need this!
	texture_blue.needsUpdate = true;
	
	var geometry = new THREE.CubeGeometry(this._blockSize, this._blockSize, this._blockSize);
		
    for( var rows = 0; rows < data.map.length; rows++ ) {
	
      for(var columns = 0; columns < data.map[rows].length; columns++) {
	  
        if (data.map[rows][columns] == '#') {
		
          yPosition = 0;
          color = 0xff0000;
		  
        } 
		else {
		
          yPosition = -this._blockSize;
          color = 0x0000ff;
        }

		var texture = new THREE.Mesh(geometry, new THREE.MeshBasicMaterial({
				map: texture_blue
			}));
		texture.doubleSided = true;
		
        texture.position = new THREE.Vector3(this._blockSize*columns, yPosition, this._blockSize*rows);
		texture.name = "Wall";
		
        scene.add(  texture );
      }
    }
};