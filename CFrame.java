import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JFrame;
public class CFrame extends JFrame {
    //helper frame which describes the current state of a camera
    Camera c;
    public CFrame(Camera c){
        super();
        this.c=c;
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int sf = 100;
        int cx = 150 + (int) (c.dir.x*sf);
        int cz = 150 - (int) (c.dir.z*sf);
        int cy = 150 - (int) (c.dir.y*sf);
        
        g.fillOval(cx-5,cz-5,10,10);
        g.drawLine(cx,cz,150,150);
        g.fillOval(20,cy-5,10,10);
        g.drawLine(25,cy,25,150);
        
        int vectsf=5000;
        Vector uvect=c.getUVect();
        Vector vvect=c.getVVect();
        g.drawString("dir = "+c.dir.toString(),20,340);
        g.setColor(Color.BLUE);
        g.drawLine(cx,cz,cx+(int) (uvect.x*vectsf),cz-(int) (uvect.z*vectsf));
        g.drawLine(25,cy,25,cy-(int) (uvect.y*vectsf));
        g.drawString("uvect = "+uvect.toString(),20,360);
        g.setColor(Color.RED);
        g.drawLine(cx,cz,cx+(int) (vvect.x*vectsf),cz-(int) (vvect.z*vectsf));
        g.drawLine(25,cy,25,cy-(int) (vvect.y*vectsf));
        //g.fillOval(20,cy-(int) (vvect.y*vectsf)-5,10,10);
        g.drawString("vvect = "+vvect.toString(),20,380);
        g.setColor(Color.BLACK);
    }
}