import java.awt.Color;
public class Point extends Vector implements GraphicObject{
    public int color = Color.RED.getRGB();
    public Point ctp=null; //corresponding texture point
    //z, color and ctp of this point are irrelevant, this is only needed if it is used in a textured polygon.
    public Point(float x, float y, float z){
        super(x,y,z);
    }
    public Point(float x, float y, float z, int color, Point ctp){
        this(x,y,z);
        this.color=color;
        this.ctp=ctp;
    }
    public Point(float x, float y, float z, int color){
        this(x,y,z);
        this.color=color;
    }
    public Point(float x, float y, float z, Point ctp){
        this(x,y,z);
        this.ctp=ctp;
    }
    public Point(Vector v){
        this(v.x,v.y,v.z);
    }
    public Point(Vector v, int color, Point ctp){
        this(v.x,v.y,v.z);
        this.color=color;
        this.ctp=ctp;
    }
    public Point(Vector v, int color){
        this(v.x,v.y,v.z,color);
    }
    
    public Point copy(){
        Point ret=new Point(this.x,this.y,this.z,this.color,this.ctp);
        return ret;
    }
    public int getColor(){
        return this.color;
    }
    public void setColor(int c){
        this.color=c;
    }
    public Point[] getRelevantPoints(){
        Point[] ret={this.copy()};
        return ret;
    }
    public void setRelevantPoints(Point[] relps){
        this.x=relps[0].x;
        this.y=relps[0].y;
        this.z=relps[0].z;
    }
    public int raster(Point[] psUV, PixelArray writeTo, int pixelU, int pixelV){
        int writes=0;
        Point p=psUV[0];
        if (p!=null){
            int u=(int) Math.round(p.x);
            int v=(int) Math.round(p.y);
            if (u>=0 && u<pixelU && v>=0 && v<pixelV && (p.z>=1 && writeTo.depths[u][v]==0 || p.z<writeTo.depths[u][v])){
                writeTo.colors[u][v]=p.color;
                writeTo.depths[u][v]=p.z;
                writes++;
            }
        }
        return writes;
    }
}