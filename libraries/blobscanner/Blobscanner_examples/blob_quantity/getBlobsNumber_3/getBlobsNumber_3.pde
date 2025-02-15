/* 
 * Blobscanner Processing library 
 * by Antonio Molinaro - 01/01/2011.
 * Finds the number of blobs in the frame.
 * In this example the method findBlob(int[], int, int)
 * is used instead of imageFindBlobs(Capture).
 */
import Blobscanner.*;
import processing.video.*;

Detector bd;
Capture frame;
 

void setup(){
  size(320, 240);
  frame = new Capture(this, width, height);
  bd = new Detector( this, 0, 0, frame.width, frame.height, 255 );
   
}

void draw(){
  if(frame.available()){
    frame.read();
    image(frame, 0, 0);
  }
 
   
 
  frame.filter(THRESHOLD);
  frame.loadPixels();
  
  bd.findBlobs(frame.pixels, frame.width, frame.height);
  
  println(bd.getBlobsNumber() + " BLOBS FOUND");
  
   
}
 
