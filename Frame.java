import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JFrame;
public class Frame extends JFrame {
    Area a;
    int sizeU;
    int sizeV;
    boolean start=true;
    public Frame(Area a){
        super();
        this.a=a;
    }
    @Override
    public void paint(Graphics g) {
        if (start){
            super.paint(g);
            start=false;
        }
        g.setColor(Color.RED);
        a.paint(g);
    }
    public void setSize(int u, int v){
        super.setSize(u,v);
        this.sizeU=u;
        this.sizeV=v;
    }
    public void setBounds(int u, int v, int w, int h){
        super.setBounds(u,v,w,h);
        this.sizeU=w;
        this.sizeV=h;
    }
}