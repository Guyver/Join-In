/*			
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

    this.successNum = 0;	// The number of load callbacks we get for the listener.
    this.errorNum = 0;		// The number of error callbacks we recieved on loading.
    this.cache = {};            // Store them here so we can get them for use. Pass url as a key...sounds gay so might change after test!
    this.downloadQueue = [];	// The queue of images to be processed.
}




/*			QUEUE DOWNLOAD()

	@Brief: Adds a url of an image to a queue to be downloaded.
	@Arguments: path:- A string URL of the image to be loaded.
	

*/
ImageManager.prototype.queueDownload = function( path ) {

    this.downloadQueue.push( path );
};




/*			DOWNLOAD ALL() 
	
	@Brief: Processes all the image urls and provides callbacks for sucess and failure of loading.
	@Arguments: A string function name, called to start game.

*/
ImageManager.prototype.downloadAll = function( downloadCallback ) {

	// If there are no images pack it in.
    if (this.downloadQueue.length === 0 ) {
	
        downloadCallback();
    }
    
	
	// Process all the image urls in the downloadQueue.
    for (var i = 0; i < this.downloadQueue.length; i++) {
	
        var path = this.downloadQueue[i];
        var img = new Image();
		
		/*  Can be tricky to understand, 'this' is a reference to the current object and 'self' is a class object reference.
			For example, in the "load" callback this is a reference to the image of the event and not the ImageManager.
			I use 'manager' to call the asset manager function in here as its safer than using this explicitly.		
		*/
        var manager = this; 
		
		// Add an event listener for a load image. Could be somewhere else maybe.
        img.addEventListener("load", function() {
			
			// Log that it was successfull for debugging.
            console.log(this.src + ' is loaded');
			
			// Increment the success count.
            manager.successNum += 1;
			
			// Check to see if we're done loading.
            if (manager.isDone()) {
			
				// Callback to begin the game!
				downloadCallback();
			}
        }, false);
		
		// For unsuccessfully loaded images!
        img.addEventListener("error", function() {
		
			// Increment the error counter.
            manager.errorNum += 1;
			
			// Check to see if that was the last one.
            if (manager.isDone()) {
			
				// Call this to start the game or add in your own init()
                downloadCallback();
            }
        }, false);
		
        img.src = path;
        this.cache[path] = img;
    }
}




/*			GET ASSET()
	@Brief:	Get an image for the manager using its url as a key.
	@Arguments: path:- A string URL key to retrieve an image from the manager.

*/
ImageManager.prototype.getAsset = function(path) {
    return this.cache[path];
};




/*			IS DONE()

	@Brief:	Has the download queue been processed yet? 

*/
ImageManager.prototype.isDone = function() {

	// Have the amount of successes and failures so far equalled the total to be processed.
    return (this.downloadQueue.length  == this.successNum + this.errorNum);
};




/*			LOAD RESOURCES()
	@Brief: Called to initate the loading sequence of the Image Manager.
	

*/
function loadResources(){

	// Add images to be downloaded by the manager!
	imageManager.queueDownload( 'img/target_blue.png' );
	imageManager.queueDownload( 'img/target_red.png' );
	imageManager.queueDownload( 'img/target_green.png' );	
	imageManager.queueDownload( 'img/Kinect.png' );
	imageManager.queueDownload( 'img/Left_Arm_Raised.png' );
	imageManager.queueDownload( 'img/Pause.png' );
	imageManager.queueDownload( 'img/Pick_up.png' );
	imageManager.queueDownload( 'img/Reaching.png' );
	imageManager.queueDownload( 'img/Right_Arm_Raised.png' );
	
	imageManager.downloadAll( onImagesComplete );

}




/*			ON IMAGES COMPLETE()
	@Brief: This gets called when the images are cooked.


*/
function onImagesComplete(){

	imagesLoaded = true;
	var img = imageManager.getAsset( 'img/target_blue.png' );
}

// Implementation!

var imageManager = new ImageManager();
var imagesLoaded = false;

// Begin Loading
loadResources();