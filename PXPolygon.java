import java.awt.image.*;
/**
 * Planar, convex, textured polygon
 */
public class PXPolygon extends PXUPolygon implements GraphicObject{ //unfinished
    public BufferedImage texture;
    
    public PXPolygon(Point[] ps){
        super(ps);
    }
    
    public PXPolygon(Point[] ps, BufferedImage texture){
        super(ps);
        this.texture=texture;
    }
    
    public int getColor(){
        return 0;
    }
    
    public void setColor(){};
    
    public PXPolygon copy(){
        return new PXPolygon(this.points,this.texture);
    }
    
    public int raster(Point[] psUV, PixelArray writeTo, int pixelU, int pixelV){
        int writes=0;
        
        boolean allCtps=true;
        for (int i=0;i<psUV.length;i++){
            if (psUV[i]==null || psUV[i].ctp==null){
                allCtps=false;
                break;
            }
        }
        if (this.texture==null || !allCtps){
            return 0;
        }
        
        Raster raster=this.texture.getRaster();
        int rasterWidth=raster.getWidth();
        int rasterHeight=raster.getHeight();
        int[] textureData1D = ((DataBufferInt) raster.getDataBuffer()).getData();
        int[][] textureData = new int[rasterWidth][rasterHeight];
        for (int i=0;i<rasterHeight;i++){
            System.arraycopy(textureData1D,i*rasterWidth,textureData[i],0,rasterWidth);
        }
        
        int[] mmvi=PXUPolygon.getMinMaxVIndices(psUV);
        int minVIndex=mmvi[0];
        int maxVIndex=mmvi[1];
        float minV=psUV[minVIndex].y;
        float maxV=psUV[maxVIndex].y;
        minV=Math.max(minV,0);
        maxV=Math.min(maxV,pixelV);
        Point[][] plr=PXUPolygon.getPathsLR(psUV);
        Point[] pathL=plr[0];
        Point[] pathR=plr[1];
        Point[] pathLS=new Point[pathL.length]; //the whole path as pointStraights, to calculate ctps easier
        Point[] pathRS=new Point[pathR.length];
        for (int i=0;i<pathL.length;i++){
            pathLS[i]=new Point(pathL[i].x*pathL[i].z, pathL[i].y*pathL[i].z, pathL[i].z, pathL[i].ctp);
        }
        for (int i=0;i<pathR.length;i++){
            pathRS[i]=new Point(pathR[i].x*pathR[i].z, pathR[i].y*pathR[i].z, pathR[i].z, pathR[i].ctp);
        }
        
        //calculation of an effect, not sure whether this looks good
        Point[] psUVs=new Point[pathLS.length+pathRS.length-2]; //unordered
        System.arraycopy(pathLS,0,psUVs,0,pathLS.length);
        System.arraycopy(pathRS,1,psUVs,pathLS.length,pathRS.length-2);
        Vector normal=PXUPolygon.getNormal(psUVs);
        if (normal!=null){
            if (Math.sqrt(Math.pow(normal.x,2)+Math.pow(normal.z,2)) > Math.abs(normal.y)){
                for (int i=0;i<textureData.length;i++){
                    for (int j=0;j<textureData[i].length;j++){
                        int alpha=(textureData[i][j] & 0xFF000000) >> 24;
                        int r=(textureData[i][j] & 0x00FF0000) >> 16;
                        int g=(textureData[i][j] & 0x0000FF00) >> 8;
                        int b=textureData[i][j] & 0x000000FF;
                        r=Math.max(r-50,0);
                        g=Math.max(g-50,0);
                        b=Math.max(b-50,0);
                        textureData[i][j]=(alpha << 24) + (r << 16) + (g << 8) + b;
                    }
                }
            }
        }
        
        if (pathL.length>=2 && pathR.length>=2){
            for (int v= (int)(minV); v<maxV;v++){
                int lowerLeftIndex=-1;
                for (int i=0;i<pathL.length-1;i++){
                    if (pathL[i].y<v){
                        lowerLeftIndex=i;
                        break;
                    }
                }
                if (lowerLeftIndex==-1){ //rounding error?
                    lowerLeftIndex=pathL.length-1;
                }
                int lowerRightIndex=-1;
                for (int i=0;i<pathR.length-1;i++){
                    if (pathR[i].y<v){
                        lowerRightIndex=i;
                        break;
                    }
                }
                if (lowerRightIndex==-1){
                    lowerRightIndex=pathR.length-1;
                }
                int upperLeftIndex=lowerLeftIndex-1;
                int upperRightIndex=lowerRightIndex-1;
                Point ll=pathL[lowerLeftIndex];
                Point ul=pathL[upperLeftIndex];
                Point lr=pathR[lowerRightIndex];
                Point ur=pathR[upperRightIndex];
                
                Point llS=pathLS[lowerLeftIndex];
                Point ulS=pathLS[upperLeftIndex];
                Point lrS=pathRS[lowerRightIndex];
                Point urS=pathRS[upperRightIndex];
                
                //minU = lowerLeft+(upperLeft-lowerLeft)*cl with cl as some constant
                //minU.y=v
                //=> c = (v-lowerLeft.y)/(upperLeft.y-lowerLeft.y)
                //same with maxU (right side)
                if (ll!=null && ul!=null && lr!=null && ur!=null){
                    Vector oldMinU=ll.add(ul.subtract(ll).multiply((v-ll.y)/(ul.y-ll.y))); //vectors are (a bit) faster than points
                    Camera.correctDepthInt(oldMinU,ll,ul);
                    //oldMinU.ctp = new IntPoint(ll.ctp.add(ul.ctp.subtract(ll.ctp).multiply((oldMinU.y-ll.y)/(ul.y-ll.y))));
                    Vector oldMaxU=lr.add(ur.subtract(lr).multiply((v-lr.y)/(ur.y-lr.y)));
                    Camera.correctDepthInt(oldMaxU,lr,ur);
                    //oldMaxU.ctp = new IntPoint(lr.ctp.add(ur.ctp.subtract(lr.ctp).multiply((oldMaxU.y-lr.y)/(ur.y-lr.y))));
                    
                    Vector oldMinUS=new Vector(oldMinU.x*oldMinU.z,oldMinU.y*oldMinU.z,oldMinU.z); //oldMinU as pointStraight, to calculate the texture points
                    Vector oldMaxUS=new Vector(oldMaxU.x*oldMaxU.z,oldMaxU.y*oldMaxU.z,oldMaxU.z);
                    Vector oldMinUSCtp = llS.ctp.add(ulS.ctp.subtract(llS.ctp).multiply((oldMinUS.y-llS.y)/(ulS.y-llS.y)));
                    Vector oldMaxUSCtp = lrS.ctp.add(urS.ctp.subtract(lrS.ctp).multiply((oldMaxUS.y-lrS.y)/(urS.y-lrS.y)));
                    
                    Vector minU=new Vector(Math.max(oldMinU.x,0),oldMinU.y,oldMinU.z);
                    minU.z=Camera.correctDepthInt((minU.x),(oldMinU.x),oldMinU.z,(oldMaxU.x),oldMaxU.z);
                    Vector maxU=new Vector(Math.min(oldMaxU.x,pixelU),oldMaxU.y,oldMaxU.z);
                    maxU.z=Camera.correctDepthInt((maxU.x),(oldMinU.x),oldMinU.z,(oldMaxU.x),oldMaxU.z);
                    //those do not need ctps because the texture points are calculated directly from oldMinU and oldMaxU
                    
                    if (maxU.x>minU.x){
                        float minUX=minU.x;
                        float maxUX=maxU.x;
                        float minUDepth=minU.z;
                        float maxUDepth=maxU.z;
                        float oldMinUX=oldMinU.x;
                        float oldMaxUX=oldMaxU.x;
                        float oldMinUDepth=oldMinU.z;
                        float oldMaxUDepth=oldMaxU.z;
                        if (writeTo.pxpOrigins[(int) minUX][v]==writeTo.pxpOrigins[(int) maxUX<writeTo.pxpOrigins.length ? (int) maxUX : (int) (maxUX-1)][v] &&
                                writeTo.depths[(int) minUX][v]<=minUDepth &&
                                writeTo.depths[(int) maxUX<writeTo.pxpOrigins.length ? (int) maxUX : (int) (maxUX-1)][v]<=maxUDepth){
                                    //should improve performance
                                    //is this sensible for the use-case?
                        }
                        else{
                            //the following if-clause is just for different calculations of the texture coordinates
                            //there should not occur rounding errors, but this is not too sure
                            
                            if (Math.round(oldMaxUS.x*1000)!=Math.round(oldMinUS.x*1000)){
                                for (int u=(int) minUX;u<maxUX;u++){
                                    float currentDepth=writeTo.depths[u][v];
                                    if (currentDepth>minUDepth || currentDepth>maxUDepth){ //should improve performance, unsure if it does
                                        float depth=Camera.correctDepthInt(u,minUX,minUDepth,maxUX,maxUDepth);
                                        
                                        if (depth<currentDepth){ //&& depth>=1 ?
                                            int tu = (int)(oldMinUSCtp.x + (oldMaxUSCtp.x-oldMinUSCtp.x) * (u*depth-oldMinUS.x) / (oldMaxUS.x-oldMinUS.x));
                                            int tv = (int)(oldMinUSCtp.y + (oldMaxUSCtp.y-oldMinUSCtp.y) * (u*depth-oldMinUS.x) / (oldMaxUS.x-oldMinUS.x));
                                            //coordinates on the texture
                                            tu=tu>rasterWidth-1 ? rasterWidth-1 : tu;
                                            tu = tu<0 ? 0 : tu;
                                            tv=tv>rasterHeight-1 ? rasterHeight-1 : tv;
                                            tv = tv<0 ? 0 : tv;
                                            
                                            writeTo.colors[u][v]=textureData[tu][tv];
                                            writeTo.depths[u][v]=depth;
                                            writeTo.pxpOrigins[u][v]=this;
                                            writes++;
                                        }
                                    }
                                }
                            }
                            else if (Math.round(oldMaxUDepth*1000)!=Math.round(oldMinUDepth*1000)){
                                for (int u=(int) minUX;u<maxUX;u++){
                                    float currentDepth=writeTo.depths[u][v];
                                    if (currentDepth>minUDepth || currentDepth>maxUDepth){ //should improve performance, unsure if it does
                                        float depth=Camera.correctDepthInt(u,minUX,minUDepth,maxUX,maxUDepth);
                                        
                                        if (depth<currentDepth){ //&& depth>=1 ?
                                            int tu = Math.round(oldMinUSCtp.x + (oldMaxUSCtp.x-oldMinUSCtp.x) * (depth-oldMinUDepth) / (oldMaxUDepth-oldMinUDepth));
                                            int tv = Math.round(oldMinUSCtp.y + (oldMaxUSCtp.y-oldMinUSCtp.y) * (depth-oldMinUDepth) / (oldMaxUDepth-oldMinUDepth));
                                            //coordinates on the texture
                                            tu = tu>rasterWidth-1 ? rasterWidth-1 : tu;
                                            tu = tu<0 ? 0 : tu;
                                            tv = tv>rasterHeight-1 ? rasterHeight-1 : tv;
                                            tv = tv<0 ? 0 : tv;
                                            
                                            writeTo.colors[u][v]=textureData[tu][tv];
                                            writeTo.depths[u][v]=depth;
                                            writeTo.pxpOrigins[u][v]=this;
                                            writes++;
                                        }
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
}