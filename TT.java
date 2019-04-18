import java.util.TimerTask;
public class TT extends TimerTask{
    FrameTest ft;
    double arc;
    double arcStep;
    static int period=1000/30;
    public TT(FrameTest ft, double arc, double arcStep){
        super();
        this.ft=ft;
        this.arc=arc;
        this.arcStep=arcStep;
    }
    public TT(FrameTest ft){
        this(ft,Math.PI/2,Math.PI/100);
    }
    public void run(){
        float sf=(float) Math.sqrt(Math.pow(this.ft.frame.a.c.dir.x,2)+Math.pow(this.ft.frame.a.c.dir.z,2));
        arc=arc+arcStep;
        this.ft.frame.a.c.dir.x = (float) (Math.cos(arc)*sf);
        this.ft.frame.a.c.dir.z = (float) (Math.sin(arc)*sf);
        //this.ft.frame.a.c.dir=this.ft.frame.a.c.dir.multiply(0.8f);
        //this.ft.frame.a.c.pos.z=this.ft.frame.a.c.pos.z+0.5f;
        this.ft.cframe.update(this.ft.cframe.getGraphics());
        this.ft.frame.update(this.ft.frame.getGraphics());
    }
}