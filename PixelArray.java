public class PixelArray{
    int[][] colors;
    float[][] depths;
    PXUPolygon[][] pxpOrigins; //used to increase performance, contains the PXPolygons 
    //from which those pixels originate. See also: PXPolygon.raster
    public PixelArray(int[][] colors, float[][] depths, PXUPolygon[][] pxpOrigins){
        this.colors=colors;
        this.depths=depths;
        this.pxpOrigins=pxpOrigins;
    }
    public PixelArray(int width, int height){
        this.colors=new int[width][height];
        this.depths=new float[width][height];
        this.pxpOrigins=new PXUPolygon[width][height];
    }
}