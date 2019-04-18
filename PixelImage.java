import java.awt.Color;
import java.awt.Point;
import java.awt.image.*;
import java.awt.Graphics;
public class PixelImage{
    public BufferedImage content;
    public PixelImage(PixelArray pixels, int bgcolor){ //is bgcolor the right name here?
        int width=pixels.colors.length;
        int height=pixels.colors[0].length;
        int[] data=new int[width*height];
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                int index=i+j*width;
                if (pixels.depths[i][j]!=Double.POSITIVE_INFINITY){
                    data[index]=pixels.colors[i][j];
                }
                else{
                    data[index]=bgcolor;
                }
            }
        }
        //from https://stackoverflow.com/questions/6319465/fast-loading-and-drawing-of-rgb-data-in-bufferedimage, slightly adjusted
        //http://www.jhlabs.com/ip/managed_images.html => makes the program slow?
        this.content = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        int[] oldData = ((DataBufferInt)this.content.getRaster().getDataBuffer()).getData();
        System.arraycopy(data,0,oldData,0,data.length);
    }
}