import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
public class KL implements KeyListener{
    public FrameTest ft;
    boolean ttRunning;
    double arc=Math.PI/2;
    double arcStep=Math.PI/100;
    public KL(FrameTest ft){
        this.ft=ft;
        ttRunning=true;
    }
    public void keyPressed(KeyEvent e){
        int c=e.getKeyCode();
        if (c==KeyEvent.VK_SPACE){
            if (ttRunning){
                this.arc=this.ft.tsk.arc;
                this.ft.tsk.cancel();
                this.ft.t.purge();
                ttRunning=false;
            }
            else{
                this.ft.tsk=new TT(this.ft,this.arc,this.arcStep);
                this.ft.t.schedule(this.ft.tsk,TT.period,TT.period);
                ttRunning=true;
            }
        }
        else if (c==KeyEvent.VK_LEFT){
            this.ft.tsk.arcStep=Math.min(this.ft.tsk.arcStep+Math.PI/400,Math.PI);
            this.arcStep=this.ft.tsk.arcStep;
        }
        else if (c==KeyEvent.VK_RIGHT){
            this.ft.tsk.arcStep=Math.max(this.ft.tsk.arcStep-Math.PI/400,-Math.PI);
            this.arcStep=this.ft.tsk.arcStep;
        }
    }
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
}