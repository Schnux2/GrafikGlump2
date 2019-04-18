import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
public class Area{
    public ArrayList<GraphicObject> objs;
    public Camera c;
    public boolean hasShadow; //by now not implemented
    public ArrayList<Lamp> lamps;
    int index=0;
    
    public Area(GraphicObject[] objs, Camera c, boolean hasShadow, Lamp[] lamps){
        this.objs=new ArrayList<GraphicObject>();
        for (int i=0;i<objs.length;i++){
            this.objs.add(objs[i]);
        }
        this.c=c;
        this.hasShadow=hasShadow;
        this.lamps=new ArrayList<Lamp>();
        for (int i=0;i<lamps.length;i++){
            this.lamps.add(lamps[i]);
        }
    }
    
    public Area(ArrayList<GraphicObject> objs, Camera c, boolean hasShadow, ArrayList<Lamp> lamps){
        this.objs=objs; //reference
        this.c=c;
        this.hasShadow=hasShadow;
        this.lamps=lamps; //reference
    }
    
    public Area(GraphicObject[] objs, Camera c){
        this(objs,c,false,new Lamp[0]);
    }
    
    public Area(ArrayList<GraphicObject> objs, Camera c){
        this(objs,c,false,new ArrayList<Lamp>());
    }
    
    public void paint(Graphics g){
        index++;
        if (c!=null){
            long t0=System.nanoTime();
            PixelArray pixels=new PixelArray(c.pixelU,c.pixelV);
            for (int u=0;u<c.pixelU;u++){
                for (int v=0;v<c.pixelV;v++){
                    pixels.depths[u][v]=Float.POSITIVE_INFINITY;
                }
            }
            ArrayList<GraphicObject> objs2=c.getPotentiallyVisiblesAndProject(objs);
            //objs2=c.orderRoughly(objs2); //from the front to the back
            long t1=System.nanoTime();
            
            //rastering and overlaying of the rasters
            int writes=0;
            long t_polygons=0;
            long t_others=0;
            for (int i=0;i<objs2.size();i++){
                long t0_loop=System.nanoTime();
                GraphicObject obj=objs2.get(i);
                Point[] relps=obj.getRelevantPoints();
                for (int j=0;j<relps.length;j++){
                    if (relps[j]!=null){
                        relps[j].x=Math.round(relps[j].x+c.pixelU/2); //because the array goes from 0 to pixelU, but the returned coordinates from -pixelU/2 to pixelU/2
                        relps[j].y=Math.round(-relps[j].y+c.pixelV/2); //inverted coordinate system
                        relps[j].color=obj.getColor();
                    }
                }
                long t1_loop=System.nanoTime();
                
                writes = writes +
                obj.raster(relps,pixels,c.pixelU,c.pixelV);
                
                long t2_loop=System.nanoTime();
                if (obj instanceof PXUPolygon){
                    t_polygons=t_polygons+t2_loop-t0_loop;
                }
                else{
                    t_others=t_others+t2_loop-t0_loop;
                }
            }
            System.out.println("Writes: "+writes);
            
            if (false){//this.hasShadow){
                for (int u=0;u<c.pixelU;u++){
                    for (int v=0;v<c.pixelV;v++){
                        if (pixels.depths[u][v]!=0){
                            for (int dist=0;dist<c.viewingRange;dist=dist+5){ //just a nice effect, no real shadow
                                if (pixels.depths[u][v]>dist){
                                    pixels.colors[u][v]=new Color(pixels.colors[u][v]).darker().getRGB();
                                }
                                else{
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            //*
            if (t_others!=0){
                System.out.println("t_polygons = "+t_polygons+",\tt_others = "+t_others+",\tt_polygons/t_others = "+(t_polygons/t_others));
            }
            else{
                System.out.println("t_polygons = "+t_polygons+",\tt_others = "+t_others);
            }//*/
            long t2=System.nanoTime();
            
            PixelImage pi=new PixelImage(pixels,Color.LIGHT_GRAY.getRGB());
            g.drawImage(pi.content,0,0,null);
            long t3=System.nanoTime();
            
            ///*
            if (t2-t1!=0){
                System.out.println((t1-t0)+"\t"+(t2-t1)+"\t"+(t3-t2));
            }
            else{
                System.out.println((t1-t0)+"\t"+(t2-t1));
            }
            if (t2-t0!=0){
                System.out.println("("+(1000000000/(t3-t0))+" (FPS))");
            }//*/
            System.out.println("index: "+index+"\n");
        }
    }
}