import javax.swing.WindowConstants;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.awt.Color;
public class FrameTest {
    Frame frame;
    CFrame cframe;
    Timer t;
    TT tsk;
    KL kl;
    public FrameTest() {
        System.out.println("\n=============\nnew FrameTest\n=============\n");
        ArrayList<GraphicObject> objs=new ArrayList<GraphicObject>();
        /*
        for (int z=-1;z<3;z=z+1){
            for (int i=-250;i<250;i=i+5){
                for (int j=-250;j<250;j=j+5){
                    Point[] ps=new Point[4];
                    ps[0]=new Point(i,z,j,new Point(0,0,0));
                    ps[1]=new Point(i+5,z,j,new Point(5,0,0));
                    ps[2]=new Point(i+5,z,j+5,new Point(5,5,0));
                    ps[3]=new Point(i,z,j+5,new Point(0,5,0));
                    PXPolygon pxpoly=new PXPolygon(ps,new TTexture(5,5,"r"));
                    objs.add(pxpoly);
                }
            }
        }*/
        for (int i=-250;i<250;i=i+5){
            for (int j=-250;j<250;j=j+5){
                Point[] ps=new Point[4];
                ps[0]=new Point(i,j,100,new Point(0,0,0));
                ps[1]=new Point(i+5,j,100,new Point(5,0,0));
                ps[2]=new Point(i+5,j+5,100,new Point(5,5,0));
                ps[3]=new Point(i,j+5,100,new Point(0,5,0));
                PXUPolygon pxpoly=new PXPolygon(ps,new TTexture(5,5,"ch"));
                objs.add(pxpoly);
            }
        }
        /*
        Point[] ps=new Point[3];
        ps[0]=new Point(0,0,2,new Point(0,0,0));
        ps[1]=new Point(0,5,2,new Point(0,5,0));
        ps[2]=new Point(5,0,2,new Point(5,0,0));
        
        PXPolygon pxpoly=new PXPolygon(ps,new TTexture(5,5,"ch"));
        objs.add(pxpoly);
        
        Point[] hps=new Point[6]; //hexagon
        hps[0]=new Point(-0.5f,(float)(-Math.sqrt(3)/2),2,new Point(1,0,0));
        hps[1]=new Point(0.5f,(float)(-Math.sqrt(3)/2),2,new Point(3,0,0));
        hps[2]=new Point(1,0,2,new Point(4,(float)Math.sqrt(3),0));
        hps[3]=new Point(0.5f,(float)(Math.sqrt(3)/2),2,new Point(3,(float)Math.sqrt(3)*2,0));
        hps[4]=new Point(-0.5f,(float)(Math.sqrt(3)/2),2,new Point(1,(float)Math.sqrt(3)*2,0));
        hps[5]=new Point(-1,0,2,new Point(0,(float)Math.sqrt(3),0));
        PXPolygon pxupoly=new PXPolygon(hps,new TTexture(5,5,"ch"));
        //objs.add(pxupoly);
        
        Point[] qps=new Point[8];
        qps[0]=new Point(-10,-10,10,new Point(0,0,0));
        qps[1]=new Point(-10,-10,-10,new Point(5,0,0));
        qps[2]=new Point(10,-10,-10,new Point(0,0,0));
        qps[3]=new Point(10,-10,10,new Point(5,0,0));
        qps[4]=new Point(-10,10,10,new Point(0,5,0));
        qps[5]=new Point(-10,10,-10,new Point(5,5,0));
        qps[6]=new Point(10,10,-10,new Point(0,5,0));
        qps[7]=new Point(10,10,10,new Point(5,5,0));
        
        Point[] qps1={qps[0],qps[1],qps[2],qps[3]};
        Point[] qps2={qps[4],qps[5],qps[6],qps[7]};
        Point[] qps3={qps[0],qps[1],qps[5],qps[4]};
        Point[] qps4={qps[2],qps[3],qps[7],qps[6]};
        Point[] qps5={qps[1],qps[2],qps[6],qps[5]};
        Point[] qps6={qps[3],qps[0],qps[4],qps[7]};
        PXUPolygon q1=new PXUPolygon(qps1,Color.BLACK.getRGB());
        PXUPolygon q2=new PXUPolygon(qps2,Color.WHITE.getRGB());
        PXUPolygon q3=new PXPolygon(qps3,new TTexture(5,5,"b"));
        PXUPolygon q4=new PXPolygon(qps4,new TTexture(5,5,"b"));
        PXUPolygon q5=new PXPolygon(qps5,new TTexture(5,5,"r"));
        PXUPolygon q6=new PXPolygon(qps6,new TTexture(5,5,"g"));
        
        for (int i=0;i<100;i++){
            objs.add(q1.copy());
            objs.add(q2.copy());
            objs.add(q3.copy());
            objs.add(q4.copy());
            objs.add(q5.copy());
            objs.add(q6.copy());
        }
        
        Point[] tps=new Point[3]; //triangle
        tps[0]=new Point(-1,-1,1);
        tps[1]=new Point(2,3,3);
        tps[2]=new Point(6,4,4);
        PXUPolygon tri=new PXUPolygon(tps,Color.RED.getRGB());
        objs.add(tri);*/
        /*
        for (int x=-1;x<=1;x=x+2){
            for (int y=-1;y<=1;y=y+2){
                Point p1 = new Point(x-1,y-1,5);
                Point p2 = new Point(x-1,y+1,5);
                Point p3 = new Point(x+1,y-1,5);
                Point p4 = new Point(x+1,y+1,5);
                Point p5 = new Point(x-1,y-1,3);
                Point p6 = new Point(x-1,y+1,3);
                Point p7 = new Point(x+1,y-1,3);
                Point p8 = new Point(x+1,y+1,3);
                Line l1 = new Line(p1,p2);
                Line l2 = new Line(p1,p3);
                Line l3 = new Line(p2,p4);
                Line l4 = new Line(p3,p4);
                Line l5 = new Line(p5,p6);
                Line l6 = new Line(p5,p7);
                Line l7 = new Line(p6,p8);
                Line l8 = new Line(p7,p8);
                Line l9 = new Line(p1,p5);
                Line l10 = new Line(p2,p6);
                Line l11 = new Line(p3,p7);
                Line l12 = new Line(p4,p8);
                objs.add(p1);
                objs.add(p2);
                objs.add(p3);
                objs.add(p4);
                objs.add(p5);
                objs.add(p6);
                objs.add(p7);
                objs.add(p8);
                
                objs.add(l1);
                objs.add(l2);
                objs.add(l3);
                objs.add(l4);
                objs.add(l5);
                objs.add(l6);
                objs.add(l7);
                objs.add(l8);
                objs.add(l9);
                objs.add(l10);
                objs.add(l11);
                objs.add(l12);
            }
        }*/
        
        Vector cpos=new Vector(-1,1,-4);
        Vector cdir=new Vector(0,-0.5f,4); //Beware the scale of this, if points are too near, it may look weird. //if this is too small, are there bugs, or are they expected behaviour?
        int pixelU=(int)(1366);
        int pixelV=(int)(768);
        this.t=new Timer();
        try{
            Camera c=new Camera(cpos,cdir,4,3,pixelU,pixelV,Float.POSITIVE_INFINITY);
            ArrayList<Lamp> ls=new ArrayList<Lamp>();
            ls.add(new DLamp(new Vector(0,0,0),new Vector(0,0,1),3,3));
            Area a=new Area(objs,c,true,ls);
            frame = new Frame(a);
            frame.setSize(pixelU,pixelV);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
            
            cframe=new CFrame(frame.a.c);
            cframe.setSize(300,400);
            cframe.setBounds(1066,0,300,400);
            cframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            cframe.setVisible(true);
            
            try{
                Thread.sleep(100); //Sometimes, NullPointerExceptions are thrown for some odd reason (Camera not fully initialised?). This is still the case with this, but much less likely?
            }
            catch(InterruptedException e){}
            
            this.tsk=new TT(this);
            t.schedule(tsk,TT.period,TT.period);
            
            this.kl=new KL(this);
            this.frame.addKeyListener(this.kl);
            this.cframe.addKeyListener(this.kl);
        }
        catch(ZeroVectorException e){
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args){
        new FrameTest();
    }
}