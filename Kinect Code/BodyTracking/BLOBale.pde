// Diewalk cv kit: untouched apart from disabling what was not required
//new BLOBable class, that implements the BLOBable-interface.
public final class BLOBable_Colorizer implements BLOBable {
  int width_, height_;
  private float hsb_[] = new float[3];
  private float mousex_val_, mousey_val_;
  private String name_; 
  private PApplet papplet_;
  private PImage img_;
  int pixels[];

  public BLOBable_Colorizer(PApplet papplet, PImage img) {
    papplet_ = papplet;
    img_ = img;
  }

  //@Override
  public final void init() {
    name_ = this.getClass().getSimpleName();
  }

  //@Override
  public final void updateOnFrame(int width, int height) {
    width_ = width;
    height_ = height;
    pixels = img_.pixels;
  }
  //@Override
  public final boolean isBLOBable(int pixel_index, int x, int y) {
    if ( (PixelColor.brighntess(pixels[pixel_index]) > 50) )
      return true;
    else
      return false;
  }
}

