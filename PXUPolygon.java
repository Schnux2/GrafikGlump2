import java.awt.Color;
import java.util.ArrayList;
/**
 * Planar, convex, untextured polygon
 */
public class PXUPolygon implements GraphicObject{ //Maybe it doesn't have to be planar. //maybe working, maybe not
    public Point[] points;
    public int color = Color.RED.getRGB();
    
    public PXUPolygon(Point[] points){
        this.points=points;
    }
    
    public PXUPolygon(Point[] points, int color){
        this.points=points;
        this.color=color;
    }
    
    public PXUPolygon copy(){
        PXUPolygon ret=new PXUPolygon(this.points,this.color);
        return ret;
    }
    
    public int getColor(){
        return this.color;
    }
    
    public void setColor(int c){
        this.color=c;
    }
    
    public String toString(){
        String ret="Polygon: (";
        for (int i=0;i<this.points.length;i++){
            ret=ret+this.points[i].toString()+",\n\t";
        }
        ret=ret.substring(0,ret.length()-3)+")";
        return ret;
    }
    
    public Point[] getRelevantPoints(){
        Point[] ret=new Point[this.points.length];
        for (int i=0;i<this.points.length;i++){
            ret[i]=this.points[i].copy();
        }
        return ret;
    }
    
    public void setRelevantPoints(Point[] relps){
        for (int i=0;i<relps.length;i++){
            this.points[i]=relps[i].copy();
        }
    }
    
    public int raster(Point[] psUV, PixelArray writeTo, int pixelU, int pixelV){
        //maybe something here is wrong
        //maybe depth calculations are wrong
        int writes=0;
        
        //psUV=PXUPolygon.orderClockwise(psUV); //get ordered anyways in getPathsLR => unneeded?
        Point[][] pathsLR=PXUPolygon.getPathsLR(psUV);
        Point[] pathLArr=pathsLR[0];
        Point[] pathRArr=pathsLR[1];
        
        int[] minMaxVIndices=PXUPolygon.getMinMaxVIndices(psUV);
        float minV=psUV[minMaxVIndices[0]].y;
        float maxV=psUV[minMaxVIndices[1]].y;
        minV=Math.max(minV,0);
        maxV=Math.min(maxV,pixelV); //those are already adjusted from [-pixelV/2,pixelV/2] as the viewing range to [0,pixelV], see Area
        
        int color=psUV[0].color;
        
        //now iterate over lines
        if (pathLArr.length>=2 && pathRArr.length>=2){
            for (int activeV=(int) Math.round(minV); activeV<(int) Math.round(maxV);activeV++){
                int lowerLeftIndex=-1;
                for (int i=0;i<pathLArr.length-1;i++){
                    if (pathLArr[i].y<activeV){ //pathLArr[i]!=null && 
                        lowerLeftIndex=i;
                        break;
                    }
                }
                if (lowerLeftIndex==-1){ //rounding error?
                    lowerLeftIndex=pathLArr.length-1;
                }
                int lowerRightIndex=-1;
                for (int i=0;i<pathRArr.length-1;i++){
                    if (pathRArr[i].y<activeV){ //pathRArr[i]!=null && 
                        lowerRightIndex=i;
                        break;
                    }
                }
                if (lowerRightIndex==-1){
                    lowerRightIndex=pathRArr.length-1;
                }
                int upperLeftIndex=lowerLeftIndex-1;
                int upperRightIndex=lowerRightIndex-1;
                Point ll=pathLArr[lowerLeftIndex];
                Point ul=pathLArr[upperLeftIndex];
                Point lr=pathRArr[lowerRightIndex];
                Point ur=pathRArr[upperRightIndex];
                
                //minU = lowerLeft+(upperLeft-lowerLeft)*cl with cl as some constant
                //minU.y=v
                //=> c = (v-lowerLeft.y)/(upperLeft.y-lowerLeft.y)
                //same with maxU (right side)
                if (ll!=null && ul!=null && lr!=null && ur!=null){
                    Vector oldMinU=new Point(ll.add(ul.subtract(ll).multiply((activeV-ll.y)/(ul.y-ll.y))));
                    Camera.correctDepthInt(oldMinU,ll,ul);
                    
                    Vector oldMaxU=new Point(lr.add(ur.subtract(lr).multiply((activeV-lr.y)/(ur.y-lr.y))));
                    Camera.correctDepthInt(oldMaxU,lr,ur);
                    //inconsistent that minV and maxV are floats, but minU and maxU are points
                    
                    Point minU=new Point(Math.max(oldMinU.x,0),oldMinU.y,oldMinU.z);
                    minU.z=Camera.correctDepthInt(Math.round(minU.x),(int) oldMinU.x,oldMinU.z,(int) oldMaxU.x,oldMaxU.z);
                    Point maxU=new Point(Math.min(oldMaxU.x,pixelU),oldMaxU.y,oldMaxU.z);
                    maxU.z=Camera.correctDepthInt(Math.round(maxU.x),(int) oldMinU.x,oldMinU.z,(int) oldMaxU.x,oldMaxU.z);
                    if (maxU.x>minU.x){
                        int minUX=(int)minU.x;
                        int maxUX=(int)maxU.x;
                        float minUDepth=minU.z;
                        float maxUDepth=maxU.z;
                        if (writeTo.pxpOrigins[minUX][activeV]==writeTo.pxpOrigins[maxUX<writeTo.pxpOrigins.length ? maxUX : maxUX-1][activeV] &&
                                writeTo.depths[minUX][activeV]<=minUDepth &&
                                writeTo.depths[maxUX<writeTo.pxpOrigins.length ? maxUX : maxUX-1][activeV]<=maxUDepth){
                                    //should improve performance
                                    //is this sensible for the use-case?
                        }
                        else{
                            for (int u=minUX;u<maxUX;u++){
                                float currentDepth=writeTo.depths[u][activeV];
                                if (currentDepth>minUDepth || currentDepth>maxUDepth){ //should improve performance, unsure if it does, is this even right?
                                    float depth=Camera.correctDepthInt(u,minUX,minUDepth,maxUX,maxUDepth);
                                    
                                    if (depth<currentDepth){ //&& depth>=1 ?
                                        writeTo.colors[u][activeV]=color;
                                        writeTo.depths[u][activeV]=depth;
                                        writeTo.pxpOrigins[u][activeV]=this;
                                        writes++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return writes;
    }
    
    public static int[] getMinMaxVIndices(Point[] psUV){
        float maxV=Float.NEGATIVE_INFINITY;
        int maxVIndex=-1;
        for (int j=0;j<psUV.length;j++){
            if (psUV[j]!=null && psUV[j].y>maxV){
                maxVIndex=j;
                maxV=psUV[maxVIndex].y;
            }
        }
        float minV=Float.POSITIVE_INFINITY;
        int minVIndex=-1;
        for (int j=0;j<psUV.length;j++){
            if (psUV[j]!=null && psUV[j].y<minV){
                minVIndex=j;
                minV=psUV[minVIndex].y;
            }
        }
        int[] ret={minVIndex,maxVIndex};
        return ret;
    }
    
    /**
     * This returns two "paths", from the maximum y-value to the minimum y-value on the left and on the right side of the polygon.
     */
    public static Point[][] getPathsLR(Point[] psUV){
        
        psUV=PXUPolygon.orderClockwise(psUV);
        int[] minMaxVIndices=PXUPolygon.getMinMaxVIndices(psUV);
        int minVIndex=minMaxVIndices[0];
        int maxVIndex=minMaxVIndices[1];
        
        ArrayList<Point> path1=new ArrayList<Point>(psUV.length); //from maxV to minV on one side of the polygon, by now, it is not known, on which one
        ArrayList<Point> path2=new ArrayList<Point>(psUV.length);
        if (minVIndex>maxVIndex){
            for (int i=maxVIndex;i>=0;i--){
                path1.add(psUV[i]);
            }
            for (int i=psUV.length-1;i>=minVIndex;i--){
                path1.add(psUV[i]);
            }
            
            for (int i=maxVIndex;i<=minVIndex;i++){
                path2.add(psUV[i]);
            }
        }
        else if (minVIndex<maxVIndex){ //system is similar to above
            for (int i=maxVIndex;i<=psUV.length-1;i++){
                path2.add(psUV[i]);
            }
            for (int i=0;i<=minVIndex;i++){
                path2.add(psUV[i]);
            }
            
            for (int i=maxVIndex;i>=minVIndex;i--){
                path1.add(psUV[i]);
            }
        }
        
        ArrayList<Point> pathL; //path on the left side of the polygon
        ArrayList<Point> pathR;
        Point pp1=null; //any point from the first path, to determine which one is left and which one is right
        Point pp2=null;
        for (int i=0;i<path1.size()-1;i++){
            if (path1.get(i)!=null){
                pp1=path1.get(i);
                break;
            }
        }
        for (int i=0;i<path2.size()-1;i++){
            if (path2.get(i)!=null){
                pp2=path2.get(i);
                break;
            }
        }
        if (pp1==null){ //wrong? //all points in path1 outside the viewing range or behind the canvas
            pathL=path2;
            pathR=path1;
        }
        else if (pp2==null){ //wrong?
            pathL=path2;
            pathR=path1;
        }
        else if (pp1.x>pp2.x){
            pathL=path2;
            pathR=path1;
        }
        else{
            pathL=path1;
            pathR=path2;
        }
        
        Point[] pathLArr=new Point[pathL.size()];
        Point[] pathRArr=new Point[pathR.size()]; //are those (much) faster?
        for (int i=0;i<pathL.size();i++){
            pathLArr[i]=pathL.get(i);
        }
        for (int i=0;i<pathR.size();i++){
            pathRArr[i]=pathR.get(i);
        }
        
        Point[][] ret={pathLArr,pathRArr};
        return ret;
    }
    
    public static boolean clockwise(Point[] psUV){ //order of points in the point array
        //expects its own relevant points translated and rotated by Camera.projectStraight
        int maxVIndex=PXUPolygon.getMinMaxVIndices(psUV)[0];
        Point p1;
        Point p2;
        if (maxVIndex==0){
            p1=psUV[psUV.length-1];
        }
        else{
            p1=psUV[maxVIndex-1];
        }
        if (maxVIndex==psUV.length-1){
            p2=psUV[0];
        }
        else{
            p2=psUV[maxVIndex+1];
        }
        if (p1.x<=p2.x){
            return true;
        }
        else{
            return false;
        }
    }
    
    public static Point[] orderClockwise(Point[] psUV){
        //not that much tested => maybe wrong
        Point[] ret=new Point[psUV.length];
        int[] mmvIndices=PXUPolygon.getMinMaxVIndices(psUV);
        int minVIndex=mmvIndices[0];
        int maxVIndex=mmvIndices[1];
        
        if (maxVIndex==-1 || minVIndex==-1 || minVIndex==maxVIndex){ //why is this even needed?
            return psUV;
        }
        Point pMaxV=psUV[maxVIndex];
        Point pMinV=psUV[minVIndex];
        Vector ldir=pMaxV.subtract(pMinV);
        ArrayList<Point> pathL=new ArrayList<Point>();
        ArrayList<Point> pathR=new ArrayList<Point>();
        for (int j=0;j<psUV.length;j++){
            if (j!=maxVIndex && j!=minVIndex){
                Point p=psUV[j];
                Vector ldir1=ldir.multiply(p.subtract(pMinV).y/ldir.y);
                ArrayList<Point> path;
                if (p.x>pMinV.add(ldir1).x){
                    path=pathR;
                }
                else{
                    path=pathL;
                }
                int k=0;
                if (path==pathR){ //different orders
                    while (k<path.size() && path.get(k).y>p.y){
                        k=k+1;
                    }
                }
                else{
                    while (k<path.size() && path.get(k).y<p.y){
                        k=k+1;
                    }
                }
                path.add(k,p);
            }
        }
        ArrayList<Point> retList=new ArrayList<Point>();
        retList.add(pMaxV);
        retList.addAll(pathR);
        retList.add(pMinV);
        retList.addAll(pathL);
        for (int i=0;i<retList.size();i++){
            ret[i]=retList.get(i);
        }
        return ret;
    }
    
    public static Vector getNormal(Point[] relps){ //normal points or pointUVss //untested
        Point p0=null;
        Point p1=null;
        Point p2=null;
        for (int i=0;i<relps.length;i++){
            if (relps[i]!=null){
                if (p0==null){
                    p0=relps[i];
                }
                else if (p1==null && relps[i]!=p0){
                    p1=relps[i];
                }
                else if (p2==null && relps[i]!=p0 && relps[i]!=p1){
                    Vector cmpVec1 = p1.subtract(p0);
                    Vector cmpVec2 = relps[i].subtract(p0).multiply(cmpVec1.length()/relps[i].subtract(p0).length()); 
                    //p2 should also not lie on the line from p0 t0 p1
                    if (Math.round(cmpVec1.x*1000)==Math.round(cmpVec2.x*1000) && Math.round(cmpVec1.y*1000)==Math.round(cmpVec2.y*1000) && Math.round(cmpVec1.z*1000)==Math.round(cmpVec2.z*1000)){
                        
                    }
                    else{
                        p2=relps[i];
                        break;
                    }
                }
            }
        }
        if (p0!=null && p1!=null && p2!=null){
            try{
                return p1.subtract(p0).cross(p2.subtract(p0)).normalize();
            }
            catch(ZeroVectorException e){
                return null;
            }
        }
        else{
            return null;
        }
    }
}