import ddf.minim.*;
import ddf.minim.analysis.*;

Minim minim;
AudioInput in;
BeatDetect beat;

void setup() {
  size(displayWidth, displayHeight);
  minim = new Minim(this);
  in = minim.getLineIn();
  beat = new BeatDetect();
}

int r =200;
float rad = 50;
void draw() {
  fill(0, 50);
  noStroke();
  rect(0, 0, width, height);
  translate(width>>1, height>>1);
  fill(-1, 100); 
  beat.detect(in.mix);
  if (beat.isOnset()) rad = rad*0.9;
  else rad = 70;
  ellipse(0, 0, 2*rad, 2*rad);
  stroke(-1, 50);
  int bsize = in.bufferSize();
  for (int i = 0; i < bsize - 1; i+=5) {
    float x = (r)*cos(i*2*PI/bsize);
    float y = (r)*sin(i*2*PI/bsize);
    float x2 = (r + in.left.get(i)*50)*cos(i*2*PI/bsize);
    float y2 = (r + in.left.get(i)*50)*sin(i*2*PI/bsize);
    line(x, y, x2, y2);
  }

  beginShape();
  noFill();
  stroke(-1, 50);
  for (int i = 0; i < bsize; i+=20) {
    float x2 = (r + in.right.get(i)*50)*cos(i*2*PI/bsize);
    float y2 = (r + in.right.get(i)*50)*sin(i*2*PI/bsize);
    vertex(x2, y2);
    pushStyle();
    stroke(-1);
    strokeWeight(2);
    point(x2, y2);
    popStyle();
  }
  endShape(CLOSE);
}


boolean sketchFullScreen() {
  return true;
}

