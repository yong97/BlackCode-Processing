
import wblut.processing.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.core.*;

HE_Mesh mesh;
WB_Render render;

void setup() {
  size(800, 800, P3D);
  HEC_Sphere creator=new HEC_Sphere();
  creator.setRadius(200); 
  creator.setUFacets(16);
  creator.setVFacets(16);
  mesh=new HE_Mesh(creator); 

  render=new WB_Render(this);
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 100);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  stroke(0);
  render.drawEdges(mesh);
  noStroke();
  render.drawFaces(mesh);
}

