import java.awt.Color;
public interface GraphicObject{
    public GraphicObject copy();
    public int getColor();
    public void setColor(int color);
    public Point[] getRelevantPoints();
    public void setRelevantPoints(Point[] relps);
    /**
     * This should actually be static, but this is not possible with an interface.
     * This expects their own relevant points projected to a canvas (the other parameters come from a camera)
     * The returned value is the number of writes to writeTo, for performance calculations
     */
    public int raster(Point[] psUV, PixelArray writeTo, int pixelU, int pixelV);
}