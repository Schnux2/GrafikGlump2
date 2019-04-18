import java.util.ArrayList;
public class DLamp extends Camera implements Lamp{ //directed lamp
    //This extends Camera because those two are actually quite similar: 
    //All visibles from this DLamp are lit, all others aren't.
    //CameraSizeU and -V are the size of the opening of this lamp.
    //pixelU and pixelV are not too important, but the stuff gets still rastered.
    
    public DLamp(Vector pos, Vector dir, float openingU, float openingV, int pixelU, int pixelV, float lightingRange) throws ZeroVectorException{
        super(pos,dir,openingU,openingV,pixelU,pixelV,lightingRange);
    }
    public DLamp(Vector pos, Vector dir, float openingU, float openingV) throws ZeroVectorException{
        super(pos,dir,openingU,openingV,300,300,Float.POSITIVE_INFINITY);
    }
    public Vector getPos(){
        return super.pos;
    }
    public void setPos(Vector pos){
        super.pos=pos;
    }
    public Point[] getLitPoints(ArrayList<GraphicObject> objs){ //too many objects (or there is a mistake)
        objs=super.getPotentiallyVisiblesAndProject(objs); //does not expect them to be projected => error!
        PixelArray pixels=new PixelArray(super.pixelU,super.pixelV);
        for (int i=0;i<objs.size();i++){ //raster, mostly copied from Area
            GraphicObject obj=objs.get(i);
            Point[] relps=obj.getRelevantPoints();
            for (int j=0;j<relps.length;j++){
                relps[j]=super.projectToCanvas(relps[j]);
                if (relps[j]!=null){
                    relps[j].x=Math.round(relps[j].x+super.pixelU/2); //because the array goes from 0 to pixelU, but the returned coordinates from -pixelU/2 to pixelU/2
                    relps[j].y=Math.round(-relps[j].y+super.pixelV/2); //inverted coordinate system
                    relps[j].color=obj.getColor();
                }
            }
            PixelArray raster=new PixelArray(super.pixelU,super.pixelV);
            obj.raster(relps,raster,super.pixelU,super.pixelV);
            for (int u=0;u<super.pixelU;u++){
                for (int v=0;v<super.pixelV;v++){
                    if (raster.depths[u][v]>=1 && (pixels.depths[u][v]==0 || raster.depths[u][v]<pixels.depths[u][v])){
                        pixels.colors[u][v]=raster.colors[u][v];
                        pixels.depths[u][v]=raster.depths[u][v];
                    }
                }
            }
        }
        ArrayList<Point> retList=new ArrayList<Point>();
        for (int u=0;u<super.pixelU;u++){
            for (int v=0;v<super.pixelV;v++){
                //rdir = the direction of the ray which goes through the point at the canvas with u and v and (0|0) (the lamp's position here)
                Vector rdir = super.getUVect().multiply(u) .add(super.getVVect().multiply(v));
                //rdir.z*c = pixel.depth
                if (pixels.depths[u][v]!=0){
                    float c=pixels.depths[u][v]/rdir.z;
                    Point point=new Point(rdir.multiply(c));
                    //point.origin=pixel.origin; //currently not implemented
                    retList.add(point);
                }
            }
        }
        Point[] ret=new Point[retList.size()];
        for (int i=0;i<retList.size();i++){
            ret[i]=retList.get(i);
        }
        return ret;
    }
}