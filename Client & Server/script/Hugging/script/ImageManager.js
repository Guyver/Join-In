/**			
	@Author: James Browne
	
	@Brief: 
	A single object that handles all the images for the game.
	It loads all the images given and stores them for use.
			
	Example usage:
			
	var loader = new ImageManager();
	loader.queueDownload( '../../img/example.jpg' );
	loader.downloadAll( startGame );
	...
	this.img = loader[ '../../img/example.jpg' ];	

*/
function ImageManager() {

    this._successNum = 0;	// The number of load callbacks we get for the listener.
    this._errorNum = 0;		// The number of error callbacks we recieved on loading.
    this._cache = {};            // Store them here so we can get them for use. Pass url as a key...sounds gay so might change after test!
    this._downloadQueue = [];	// The queue of images to be processed.
    this._imagesLoaded = false;
};


/**		TODO:		QUEUE DOWNLOAD()

	@Brief: Adds a url of an image to a queue to be downloaded.
	@Arguments: path:- A string URL of the image to be loaded.
	

*/
ImageManager.prototype.queueDownload = function( path ) {

    this._downloadQueue.push( path );
};


/**				DOWNLOAD ALL() 
	
	@Brief: Processes all the image urls and provides callbacks for sucess and failure of loading.
	@Arguments: A string function name, called to start game.

*/
ImageManager.prototype.downloadAll = function( downloadCallback ) {

	// If there are no images pack it in.
    if (this._downloadQueue.length === 0 ) {
	
        downloadCallback();
    }
    
	
	// Process all the image urls in the _downloadQueue.
    for (var i = 0; i < this._downloadQueue.length; i++) {
	
        var path = this._downloadQueue[i];
        var img = new Image();
		
		/*  Can be tricky to understand, 'this' is a reference to the current object and 'self' is a class object reference.
			For example, inside the "load" listener callback, "this" is a reference to the image of the event and not the ImageManager.
			I use 'manager' to call the asset manager function in here as its safer than using this explicitly.		
		*/
        var manager = this; 
		
		// Add an event listener for a load image. Could be somewhere else maybe.
        img.addEventListener("load", function() {
			
			// Log that it was successfull for debugging.
            console.log(this.src + ' is loaded');
			
			// Increment the success count.
            manager._successNum += 1;
			
			// Check to see if we're done loading.
            if (manager.isDone()) {
			
				// Callback to begin the game!
				downloadCallback();
			}
        }, false);
		
		// For unsuccessfully loaded images!
        img.addEventListener("error", function() {
		
			// Increment the error counter.
            manager._errorNum += 1;
			
			// Check to see if that was the last one.
            if (manager.isDone()) {
			
				// Call this to start the game or add in your own init()
                downloadCallback();
            }
        }, false);
		
        img.src = path;
        this._cache[path] = img;
    }
};


/**				GET ASSET()
	@Brief:	Get an image for the manager using its url as a key.
	@Arguments: path:- A string URL key to retrieve an image from the manager.
	@Returns: An Image object.
*/
ImageManager.prototype.getAsset = function(path) {
	
    return this._cache['../'+path];
};


/**				IS DONE()

	@Brief:	Has the download queue been processed yet? 

*/
ImageManager.prototype.isDone = function() {

	// Have the amount of successes and failures so far equalled the total to be processed.
    return (this._downloadQueue.length  == this._successNum + this._errorNum);
};


/**				LOAD RESOURCES()
	@Brief: Called to initate the loading sequence of the Image Manager.
	

*/
function loadResources(){

	// Add images to be downloaded by the manager!
	imageManager.queueDownload( '../img/grassTile01.png' );
	imageManager.queueDownload( '../img/wallTexture.png' );
	imageManager.queueDownload( '../img/ground_plane.png' );
	imageManager.queueDownload( '../img/floor.png' );
	imageManager.queueDownload( '../img/Tree1.png' );
	imageManager.queueDownload( '../img/popcornCeiling.png' );
	imageManager.queueDownload( '../img/wallPaper.png' );
	imageManager.downloadAll( onImagesComplete );

};


/**				ON IMAGES COMPLETE()
	@Brief: This gets called when the images are cooked.


*/
function onImagesComplete(){

	_imagesLoaded = true;
	//var img = imageManager.getAsset( 'img/target_blue.png' );
};

// Implementation!

var imageManager = new ImageManager();

// Begin Loading
loadResources();