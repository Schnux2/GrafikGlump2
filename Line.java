import java.awt.Color;
public class Line implements GraphicObject{
    public Point p1;
    public Point p2;
    public int color = Color.RED.getRGB();
    public Line(Point p1, Point p2){
        this.p1=p1;
        this.p2=p2;
    }
    public Line(Point p1, Point p2, int color){
        this.p1=p1;
        this.p2=p2;
        this.color=color;
    }
    public Line copy(){
        Line ret= new Line(this.p1,this.p2,this.color);
        return ret;
    }
    public String toString(){
        return "Line: ( p1 = "+p1.toString()+",\n\tp2 = "+p2.toString()+")";
    }
    public int getColor(){
        return this.color;
    }
    public void setColor(int c){
        this.color=c;
    }
    public Point[] getRelevantPoints(){
        Point[] ret={this.p1.copy(),this.p2.copy()};
        return ret;
    }
    public void setRelevantPoints(Point[] relps){
        this.p1=relps[0].copy();
        this.p2=relps[1].copy();
    }
    public int raster(Point[] psUV, PixelArray writeTo, int pixelU, int pixelV){
        int writes=0;
        Point p1=psUV[0];
        Point p2=psUV[1];
        if (p1!=null && p2!=null){
            float length=p2.subtract(p1).length();
            for (float r=0;r<length && r<Math.sqrt(Math.pow(pixelU,2)+Math.pow(pixelV,2));r++){
                //r<Math.sqrt(Math.pow(pixelU,2)+Math.pow(pixelV,2)) because if p1 or p2 is near
                //to the camera, the value for length can get very large
                Point p=new Point(p1.add(p2.subtract(p1).multiply(r/p2.subtract(p1).length())));
                p=Camera.correctDepth(p,p1,p2);
                if (p!=null){
                    int u=(int) Math.round(p.x);
                    int v=(int) Math.round(p.y);
                    if (u>=0 && u<pixelU && v>=0 && v<pixelV && (p.z>=1 && writeTo.depths[u][v]==0 || p.z<writeTo.depths[u][v])){
                        writeTo.colors[u][v]=p1.color;
                        writeTo.depths[u][v]=p.z;
                        writes++;
                    }
                }
            }
        }
        return writes;
    }
}