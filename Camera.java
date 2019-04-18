import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Camera{
    public Vector pos;
    public Vector dir; //The length of this is the distance from the camera to the canvas.
    public float canvasSizeU;
    public float canvasSizeV;
    public int pixelU;
    public int pixelV;
    public float viewingRange; //currently not really supported
    
    public Camera(Vector pos, Vector dir, float canvasSizeU, float canvasSizeV, int pixelU, int pixelV, float viewingRange) throws ZeroVectorException{
        this.pos=pos;
        if (dir.length()==0){
            throw(new ZeroVectorException());
        }
        this.dir=dir;
        this.canvasSizeU=canvasSizeU;
        this.canvasSizeV=canvasSizeV;
        this.pixelU=pixelU;
        this.pixelV=pixelV;
        this.viewingRange=viewingRange;
        //distance to canvas is determined by the length of dir
    }
    
    public Vector getUVect(){//"bug" if n1==0 and n3==0
        //A plane has the equation n1*x + n2*y + n3*z = d with n1,n2,n3 as the components of its normal vector and d as some constant.
        //u-vector describes how far to go in x, y, and z-direction dependent on u (which is the first pixel coordinate on the canvas)
        //=> u-vector = (n2,-n1,0) (or (-n2,n1,0), but this goes to the left instead of the right) //length has to be adjusted
        //=> v-vector = (-n1,-n2,(d+n1^2+n2^2)/n3) (could also be negative, but then again the wrong direction)
        float n1=-this.dir.x;
        //float n2=-this.dir.y;
        float n3=-this.dir.z;
        //float d = - Math.pow(n1,2) - Math.pow(n2,2) - Math.pow(n3,2);
        Vector uvect=new Vector(-n3,0,n1);//(n2,-n1,0); //seems to be true
        if (n1==0 && n3==0){
            uvect.x=1;
            uvect.y=0;
            uvect.z=0;
        }
        uvect=uvect.multiply(this.canvasSizeU/(uvect.length()*this.pixelU));
        return uvect;
    }
    
    public Vector getVVect(){ //"bug" if n1==0 and n3==0
        float n1=-this.dir.x;
        float n2=-this.dir.y;
        float n3=-this.dir.z;
        //float d = - Math.pow(n1,2) - Math.pow(n2,2) - Math.pow(n3,2);
        Vector vvect=new Vector(-n1,(float) ((Math.pow(n1,2)+Math.pow(n3,2))/n2),-n3);//(-n1,-(Math.pow(n1,2)+Math.pow(n3,2))/n2,n3); //maybe wrong
        if (n2<0){ //maybe true, maybe not
            vvect.x=-vvect.x;
            vvect.y=-vvect.y;
            vvect.z=-vvect.z;
        }
        if (n2==0){
            vvect.x=0;
            vvect.y=1;
            vvect.z=0;
        }
        else if (n1==0 && n3==0){
            vvect.x=0;
            vvect.y=0;
            vvect.z=1;
        }
        vvect=vvect.multiply(this.canvasSizeV/(vvect.length()*this.pixelV));
        return vvect;
    }
    
    /**
     * This is just a translation and a rotation of the coordinate system (center at the camera position, z-axis has the direction of this.dir).
     */
    public Point projectStraight(Point p, Vector uv, Vector vv, float d, float normalSqNeg){ //with parallel rays, not with diverging ones (which go all through the camera)
        //uses a given uvect, vvect and d for performance reasons
        //normalSqNeg = + n1*dir.x + n2*dir.y + n3*dir.z => can be calculated only once instead of in every projectStraight call
        float n1=-this.dir.x;
        float n2=-this.dir.y;
        float n3=-this.dir.z;
        //n1*(pd.x-dir.x*c) + n2*(pd.y-dir.y*c) + n3*(pd.y-dir.y*c) = d, pd=posDiff
        float c = - (d - n1*(p.x-this.pos.x) - n2*(p.y-this.pos.y) - n3*(p.z-this.pos.z)) / (normalSqNeg);
        float u;
        float v;
        if (Math.round(vv.y*1000)!=0){
            v=(p.y-this.pos.y-dir.y*(c+1))/vv.y;  //The +1 because the canvas does not go through (0|0|0) (the camera position)
            if (Math.abs(uv.x)>=Math.abs(uv.z)){
                u=(p.x-this.pos.x-dir.x*(c+1)-vv.x*v)/uv.x;
            }
            else{
                u=(p.z-this.pos.z-dir.z*(c+1)-vv.z*v)/uv.z;
            }
        }
        else{ //n1=0 and n3=0 => uv=(1,0,0) and vv=(0,0,1) (see getUVect and getVVect), but those should maybe be changed
            v=(p.z-this.pos.z-dir.z*(c+1))/vv.z;
            u=(p.x-this.pos.x-dir.x*(c+1)-vv.x*v)/uv.x;
        }
        return new Point(u,v,c+1,p.color,p.ctp); //a point on the canvas has a depth of 1
        
        /*
        float n1=-this.dir.x;
        float n2=-this.dir.y;
        float n3=-this.dir.z;
        float d = (float) (- Math.pow(n1,2) - Math.pow(n2,2) - Math.pow(n3,2));
        Vector pd=p.subtract(this.pos);
        //n1*(pd.x-dir.x*c) + n2*(pd.y-dir.y*c) + n3*(pd.y-dir.y*c) = d
        float c = - (d - n1*pd.x - n2*pd.y - n3*pd.z) / (+ n1*dir.x + n2*dir.y + n3*dir.z);
        float u;
        float v;
        if (Math.round(vv.y*1000)!=0){
            Vector dirmul=dir.multiply(c+1); //The +1 because the canvas does not go through (0|0|0) (the camera position), is this right?
            v=pd.subtract(dirmul).y/vv.y;
            Vector vmul=vv.multiply(v);
            Vector umul=pd.subtract(dirmul).subtract(vmul);
            if (Math.abs(uv.x)>=Math.abs(uv.z)){
                u=umul.x/uv.x;
            }
            else{
                u=umul.z/uv.z;
            }
        }
        else{ //n1=0 and n3=0 => uv=(1,0,0) and vv=(0,0,1) (see getUVect and getVVect), but those should maybe be changed
            Vector dirmul=dir.multiply(c+1);
            v=pd.subtract(dirmul).z/vv.z;
            Vector vmul=vv.multiply(v);
            Vector umul=pd.subtract(dirmul).subtract(vmul);
            u=umul.x/uv.x;
        }
        return new Point(u,v,c+1,p.color,p.ctp); //a point on the canvas has a depth of 1
        //*/
    }
    
    public Point projectStraight(Point p){
        Vector uv=this.getUVect();
        Vector vv=this.getVVect();
        float d = (float) (- Math.pow(this.dir.x,2) - Math.pow(this.dir.y,2) - Math.pow(this.dir.z,2));
        float normalSqNeg = - (this.dir.x*this.dir.x + this.dir.y*this.dir.y + this.dir.z*this.dir.z);
        return this.projectStraight(p,uv,vv,d,normalSqNeg);
    }
    
    /**
     * Returned coordinates are u,v and depth.
     * This expects points in front of the canvas (getPotentiallyVisibles deals with this).
     */
    public Point projectToCanvas(Point p, Vector uv, Vector vv, float d, float normalSqNeg){
        //imagine "reverse raytracing": A ray which crosses this point has posDiff * (some constant) as direction
        //and since the canvas is by definition at z=1, the calculations get very easy.
        Point ps=this.projectStraight(p,uv,vv,d,normalSqNeg); //is already a posDiff
        if (Math.round(ps.z*1000)/1000>=1){
            return new Point(ps.x/ps.z,ps.y/ps.z,ps.z,p.color,p.ctp);
        }
        else{
            return null;
        }
    }
    
    public Point projectToCanvas(Point p){
        Vector uvect=this.getUVect();
        Vector vvect=this.getVVect();
        float d = (float) (- Math.pow(this.dir.x,2) - Math.pow(this.dir.y,2) - Math.pow(this.dir.z,2));
        float normalSqNeg = - (this.dir.x*this.dir.x + this.dir.y*this.dir.y + this.dir.z*this.dir.z);
        return this.projectToCanvas(p,uvect,vvect,d,normalSqNeg);
    }
    
    public static Point projectAStraightToCanvas(Point ps){
        if (Math.round(ps.z*1000)/1000>=1){
            return new Point(ps.x/ps.z,ps.y/ps.z,ps.z,ps.color,ps.ctp);
        }
        else{
            return null;
        }
    }
    
    /**
     * Potentially means in front of the canvas. This also cuts lines and polygons which are only partly in front of the canvas down to their potentially visible part.
     */
    public ArrayList<GraphicObject> getPotentiallyVisiblesAndProject(ArrayList<GraphicObject> objs){
        //maybe true, probably wrong
        ArrayList<GraphicObject> objs2 = new ArrayList<GraphicObject>();
        Vector uvect=this.getUVect();
        Vector vvect=this.getVVect();
        float d = (float) (- Math.pow(this.dir.x,2) - Math.pow(this.dir.y,2) - Math.pow(this.dir.z,2));
        float normalSqNeg = - (this.dir.x*this.dir.x + this.dir.y*this.dir.y + this.dir.z*this.dir.z);
        for (int i=0;i<objs.size();i++){
            GraphicObject obj = objs.get(i);
            Point[] relps=obj.getRelevantPoints();
            Point[] relpsUVs=new Point[relps.length];
            for (int j=0;j<relps.length;j++){
                relpsUVs[j]=this.projectStraight(relps[j],uvect,vvect,d,normalSqNeg);
            }
            ArrayList<Integer> indicesBehind=new ArrayList<Integer>(); //indices of all points behind the canvas
            ArrayList<Integer> indicesInFront=new ArrayList<Integer>();
            for (int j=0;j<relpsUVs.length;j++){
                if (relpsUVs[j].z<1){
                    indicesBehind.add(j);
                }
                else{
                    indicesInFront.add(j);
                }
            }
            //those should at maximum contain two indices (since the polygons should be convex)
            ArrayList<Integer> ncbIndices=new ArrayList<Integer>(); //near the canvas, but behind (one of their neighbours is in front of the canvas)
            ArrayList<Integer> ncfIndices=new ArrayList<Integer>(); //near to and in front of the canvas
            ArrayList<Integer> currentIndicesList;
            if (indicesBehind.contains(0)){
                currentIndicesList=indicesBehind;
            }
            else{
                currentIndicesList=indicesInFront;
            }
            for (int j=1;j<relpsUVs.length;j++){
                if (!currentIndicesList.contains(j)){
                    if (currentIndicesList==indicesBehind){
                        ncbIndices.add(j-1);
                        ncfIndices.add(j);
                        currentIndicesList=indicesInFront;
                    }
                    else{
                        ncfIndices.add(j-1);
                        ncbIndices.add(j);
                        currentIndicesList=indicesBehind;
                    }
                }
            }
            if (!currentIndicesList.contains(0) && !(obj instanceof Line)){ //because else, if it is a line and its p1 (here: index 0) is behind the canvas,
                //it is inserted twice => bug. Point is similar, but here not necessary because it would be removed anyway.
                if (currentIndicesList==indicesBehind){
                    ncbIndices.add(relpsUVs.length-1);
                    ncfIndices.add(0);
                    currentIndicesList=indicesInFront;
                }
                else{
                    ncfIndices.add(relpsUVs.length-1);
                    ncbIndices.add(0);
                    currentIndicesList=indicesBehind;
                }
            }
            
            ArrayList<Point> nrelps=new ArrayList<Point>(); //sometimes seem to have the wrong orders => get ordered later in Area (PXPolygon.orderClockwise)
            for(int j=0;j<relpsUVs.length;j++){
                if (ncbIndices.contains(j)){
                    int ncbIndex=j;
                    int ncfIndex=ncfIndices.get(ncbIndices.indexOf(j));
                    Point ncb=relpsUVs[ncbIndex];
                    Point ncf=relpsUVs[ncfIndex]; //those two are neighbours
                    Vector pd=ncf.subtract(ncb);
                    //ncb + pd*c = pInter
                    //pInter.z=1 (on the canvas)
                    float c = (1-ncb.z)/pd.z;
                    if (ncb.ctp==null || ncf.ctp==null){
                        Point pInter=new Point(ncb.add(pd.multiply(c)),obj.getColor());
                        nrelps.add(pInter);
                    }
                    else{
                        //ncb.ctp + ctpDiff*c = pInter.ctp
                        Vector ctpDiff=ncf.ctp.subtract(ncb.ctp);
                        Point pInter=new Point(ncb.add(pd.multiply(c)), obj.getColor(), new Point(ncb.ctp.add(ctpDiff.multiply(c))));
                        nrelps.add(pInter);
                    }
                    
                    if (ncbIndices.lastIndexOf(j)!=ncbIndices.indexOf(j)){ //another occurence of this ncbIndex in the list besides the first one
                        //(one ncb, two corresponding ncfs)
                        int ncbIndex1=j;
                        int ncfIndex1=ncfIndices.get(ncbIndices.lastIndexOf(j));
                        Point ncb1=relpsUVs[ncbIndex];
                        Point ncf1=relpsUVs[ncfIndex1];
                        Vector pd1=ncf1.subtract(ncb1);
                        float c1 = (1-ncb1.z)/pd1.z;
                        if (ncb1.ctp==null || ncf1.ctp==null){
                            Point pInter1=new Point(ncb1.add(pd1.multiply(c1)),obj.getColor());
                            nrelps.add(pInter1);
                        }
                        else{
                            Vector ctpDiff1=ncf1.ctp.subtract(ncb1.ctp);
                            Point pInter1=new Point(ncb1.add(pd1.multiply(c1)), obj.getColor(), new Point(ncb1.ctp.add(ctpDiff1.multiply(c1))));
                            nrelps.add(pInter1);
                        }
                    }
                }
                else if (indicesBehind.contains(j)){
                    
                }
                else if (indicesInFront.contains(j)){
                    nrelps.add(relpsUVs[j]);
                }
            }
            Point[] nrelpsArr=new Point[nrelps.size()];
            for (int j=0;j<nrelps.size();j++){
                nrelpsArr[j]=nrelps.get(j);
            }
            for (int j=0;j<nrelpsArr.length;j++){
                nrelpsArr[j]=Camera.projectAStraightToCanvas(nrelpsArr[j]);
            }
            
            if (indicesInFront.size()>0){
                if (obj instanceof Point){
                    Point p=nrelpsArr[0].copy();
                    p.color=obj.getColor();
                    objs2.add(p); //not actually necessary?
                }
                else if (obj instanceof Line){
                    Line li=new Line(nrelpsArr[0],nrelpsArr[1],obj.getColor());
                    objs2.add(li);
                }
                else if (obj instanceof PXPolygon){
                    PXPolygon pxp=new PXPolygon(nrelpsArr,((PXPolygon) obj).texture);
                    objs2.add(pxp);
                }
                else if (obj instanceof PXUPolygon){
                    PXUPolygon pxp=new PXUPolygon(nrelpsArr,obj.getColor());
                    objs2.add(pxp);
                }
            }
            else{
                
            }
        }
        
        int i=0; //should improve performance, unsure if it does
        while(i<objs2.size()){
            Point[] relps=objs2.get(i).getRelevantPoints();
            boolean allLeft=true; //all points left of the canvas => does not need to be drawn
            for (int j=0;j<relps.length;j++){
                if (relps[j]!=null && relps[j].x>=-this.pixelU/2){
                    allLeft=false;
                    break;
                }
            }
            if (!allLeft){
                boolean allRight=true;
                for (int j=0;j<relps.length;j++){
                    if (relps[j]!=null && relps[j].x<=this.pixelU/2){
                        allRight=false;
                        break;
                    }
                }
                if (!allRight){
                    boolean allUp=true;
                    for (int j=0;j<relps.length;j++){
                        if (relps[j]!=null && relps[j].y<=this.pixelV/2){
                            allUp=false;
                            break;
                        }
                    }
                    if (!allUp){
                        boolean allDown=true;
                        for (int j=0;j<relps.length;j++){
                            if (relps[j]!=null && relps[j].y>=-this.pixelV/2){
                                allDown=false;
                                break;
                            }
                        }
                        if (allDown){
                            objs2.remove(i);
                        }
                        else{
                            boolean allBehind=true;
                            for (int j=0;j<relps.length;j++){
                                if (relps[j]!=null && relps[j].z<this.viewingRange){
                                    allBehind=false;
                                    break;
                                }
                            }
                            if (allBehind){
                                objs2.remove(i);
                            }
                            else{
                                i=i+1;
                            }
                        }
                    }
                    else{
                        objs2.remove(i);
                    }
                }
                else{
                    objs2.remove(i);
                }
            }
            else{
                objs2.remove(i);
            }
        }
        return objs2;
    }
    
    public ArrayList<GraphicObject> orderRoughly(ArrayList<GraphicObject> objs){ 
        //should increase performance but is probably so inefficient (projection at every comparision) that it doesn't
        Vector uvect=this.getUVect();
        Vector vvect=this.getVVect();
        float d = (float) (- Math.pow(this.dir.x,2) - Math.pow(this.dir.y,2) - Math.pow(this.dir.z,2));
        float normalSqNeg = - (this.dir.x*this.dir.x + this.dir.y*this.dir.y + this.dir.z*this.dir.z);
        ArrayList<GraphicObject> objs2=new ArrayList<GraphicObject>(objs);
        Collections.sort(objs2,
            new Comparator<GraphicObject>(){
                public int compare(GraphicObject g1, GraphicObject g2){
                    Point[] relps1=g1.getRelevantPoints();
                    Point[] relps2=g2.getRelevantPoints();
                    for (int i=0;i<relps1.length;i++){
                        relps1[i]=projectStraight(relps1[i],uvect,vvect,d,normalSqNeg);
                    }
                    for (int i=0;i<relps2.length;i++){
                        relps2[i]=projectStraight(relps2[i],uvect,vvect,d,normalSqNeg);
                    }
                    Point nps1=relps1[0];
                    Point nps2=relps2[0];
                    for (int i=0;i<relps1.length;i++){
                        if (nps1==null || (relps1[i]!=null && relps1[i].z<nps1.z)){
                            nps1=relps1[i];
                        }
                    }
                    for (int i=0;i<relps2.length;i++){
                        if (nps2==null || (relps2[i]!=null && relps2[i].z<nps2.z)){
                            nps2=relps2[i];
                        }
                    }
                    if (nps1==null || nps2==null){
                        throw new NullPointerException();
                    }
                    if (nps1.z<nps2.z){
                        return -1;
                    }
                    else if (nps1.z>nps2.z){
                        return 1;
                    }
                    else{
                        return 0;
                    }
                }
            });
        return objs2;
    }
    
    /**
     * The two "reference points" p1UVref and p2UVref are assumed to have the right depth-values.
     * pUV is assumed to be on the line which also contains p1UVref and p2UVref.
     */
    public static Point correctDepth(Point pUV, Point p1UVref, Point p2UVref){
        Point p1UVsRef=new Point(p1UVref.x*p1UVref.z,p1UVref.y*p1UVref.z,p1UVref.z);
        Point p2UVsRef=new Point(p2UVref.x*p2UVref.z,p2UVref.y*p2UVref.z,p2UVref.z);
        //ldir.x or ldir.y should not be zero
        Vector rdir=new Vector(pUV.x,pUV.y,1);
        Vector ldir=p2UVsRef.subtract(p1UVsRef);
        //rdir*r1 = p1UVsRef + ldir*r2
        //for every coordinate
        //r1 = (p1UVsRef.z + ldir.z*r2) / rdir.z
        //rdir.x * (p1UVsRef.z + ldir.z*r2) / rdir.z = p1UVsRef.x + ldir.x*r2
        //rdir.x*p1UVsRef.z/rdir.z + rdir.x*ldir.z*r2/ rdir.z = p1UVsRef.x + ldir.x*r2
        //rdir.x*p1UVsRef.z/rdir.z - p1UVsRef.x = ldir.x*r2 - rdir.x*ldir.z*r2/rdir.z
        //r2 = (rdir.x*p1UVsRef.z/rdir.z - p1UVsRef.x) / (ldir.x - rdir.x*ldir.z/rdir.z)
        
        float r2;
        if (ldir.x - rdir.x*ldir.z/rdir.z!=0){
            r2=(rdir.x*p1UVsRef.z/rdir.z - p1UVsRef.x) / (ldir.x - rdir.x*ldir.z/rdir.z);
        }
        else if (ldir.y - rdir.y*ldir.z/rdir.z!=0){ //is this right?
            r2=(rdir.y*p1UVsRef.z/rdir.z - p1UVsRef.y) / (ldir.y - rdir.y*ldir.z/rdir.z);
        }
        else{
            return pUV; //not nice
        }
        float r1;
        r1=(p1UVsRef.z + ldir.z*r2)/rdir.z;
        Point pUVs=new Point(rdir.multiply(r1));
        return new Point(pUV.x,pUV.y,pUVs.z);
    }
    
    public static void correctDepthInt(Vector pUV, Point p1UVref, Point p2UVref){ //the "int" has nothing to say anymore
        //in this case, p1UVref and p2UVref can not be directly above each other => bug?
        float p1UVsRefX=p1UVref.x*p1UVref.z;
        float p1UVsRefY=p1UVref.y*p1UVref.z;
        float p1UVsRefZ=p1UVref.z;
        float p2UVsRefX=p2UVref.x*p2UVref.z;
        float p2UVsRefY=p2UVref.y*p2UVref.z;
        float p2UVsRefZ=p2UVref.z;
        float rdirX=pUV.x;
        float rdirY=pUV.y;
        Vector ldir=new Vector(p2UVsRefX-p1UVsRefX,p2UVsRefY-p1UVsRefY,p2UVsRefZ-p1UVsRefZ);
        float r2;
        
        if (Math.abs(ldir.y - rdirY*ldir.z) >= Math.abs(ldir.x - rdirX*ldir.z)){//Math.round(ldir.y*1000)!=Math.round(rdirY*ldir.z*1000)){
            r2=(rdirY*p1UVsRefZ - p1UVsRefY) / (ldir.y - rdirY*ldir.z);
        }
        else if (Math.round(ldir.x*1000)!=Math.round(rdirX*ldir.z*1000)){
            r2=(rdirX*p1UVsRefZ - p1UVsRefX) / (ldir.x - rdirX*ldir.z);
        }
        else{
            return; //not nice
        }
        float r1;
        r1=p1UVsRefZ + ldir.z*r2;
        pUV.z=r1;
    }
    
    /**
     * This is mainly for PX(U)Polygon.raster, the points are assumed to all have the same v-value.
     */
    public static float correctDepthInt(int u, int p1UVrefU, float p1UVrefDepth, int p2UVrefU, float p2UVrefDepth){
        float p1UVsRefU = p1UVrefU * p1UVrefDepth;
        float p2UVsRefU = p2UVrefU * p2UVrefDepth;
        
        float r2;
        float denominator = p2UVsRefU-p1UVsRefU - u*(p2UVrefDepth-p1UVrefDepth);
        if (denominator!=0){
            r2=(u*p1UVrefDepth - p1UVsRefU) / denominator;
        }
        else{
            return 1; //not nice
        }
        
        return p1UVrefDepth + (p2UVrefDepth-p1UVrefDepth)*r2;
    }
    
    /**
     * This is mainly for PX(U)Polygon.raster, the points are assumed to all have the same v-value.
     */
    public static float correctDepthInt(float u, float p1UVrefU, float p1UVrefDepth, float p2UVrefU, float p2UVrefDepth){
        float p1UVsRefU = p1UVrefU * p1UVrefDepth;
        float p2UVsRefU = p2UVrefU * p2UVrefDepth;
        
        float r2;
        float denominator = p2UVsRefU-p1UVsRefU - u*(p2UVrefDepth-p1UVrefDepth);
        if (denominator!=0){
            r2=(u*p1UVrefDepth - p1UVsRefU) / denominator;
        }
        else{
            return 1; //not nice
        }
        
        return p1UVrefDepth + (p2UVrefDepth-p1UVrefDepth)*r2;
    }
    
    public boolean isVisible(Point pUV){ //untested
        return (pUV!=null &&
                pUV.x>=-this.pixelU/2 && pUV.x<=this.pixelU/2 &&
                pUV.y>=-this.pixelU/2 && pUV.y<=this.pixelU/2 &&
                pUV.z>=-Math.pow(this.dir.x,2)-Math.pow(this.dir.y,2)-Math.pow(this.dir.z,2) &&
                pUV.z<=this.viewingRange);
    }
}