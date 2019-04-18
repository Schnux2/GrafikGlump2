import java.awt.image.*;
import java.awt.Color;
import static java.awt.Color.*;
public class TTexture extends BufferedImage{ //testing texture
    static int r=RED.getRGB();
    static int g=GREEN.getRGB();
    static int b=BLUE.getRGB();
    static int rd=RED.darker().getRGB();
    static int gd=GREEN.darker().getRGB();
    static int bd=BLUE.darker().getRGB();
    static int rdd=RED.darker().darker().getRGB();
    static int gdd=GREEN.darker().darker().getRGB();
    static int bdd=BLUE.darker().darker().getRGB();
    static int rb=RED.brighter().getRGB();
    static int gb=GREEN.brighter().getRGB();
    static int bb=BLUE.brighter().getRGB();
    static int rbb=RED.brighter().brighter().getRGB();
    static int gbb=GREEN.brighter().brighter().getRGB();
    static int bbb=BLUE.brighter().brighter().getRGB();
    static int wh=WHITE.getRGB();
    static int bl=BLACK.getRGB();
    
    public TTexture(int width, int height, String t){
        super(width,height,BufferedImage.TYPE_INT_ARGB);
        int[] oldData = ((DataBufferInt) this.getRaster().getDataBuffer()).getData();
        int[] data=null;
        if (t=="r"){
            int[] nData={rdd,rd, r,  rd, rdd,
                  rd, r,  rb, r,  rd,
                  r,  rb, rbb,rb, r,
                  rd, r,  rb, r,  rd,
                  rdd,rd, r,  rd, rdd
            };
            data=nData;
        }
        else if (t=="g"){
            int[] nData={gdd,gd, g,  gd, gdd,
                  gd, g,  gb, g,  gd,
                  g,  gb, gbb,gb, g,
                  gd, g,  gb, g,  gd,
                  gdd,gd, g,  gd, gdd
            };
            data=nData;
        }
        else if (t=="b"){
            int[] nData={bdd,bd, b,  bd, bdd,
                  bd, b,  bb, b,  bd,
                  b,  bb, bbb,bb, b,
                  bd, b,  bb, b,  bd,
                  bdd,bd, b,  bd, bdd
            };
            data=nData;
        }
        else if (t=="ch"){
            int[] nData={bl,wh,bl,wh,bl,
                         wh,bl,wh,bl,wh,
                         bl,wh,bl,wh,bl,
                         wh,bl,wh,bl,wh,
                         bl,wh,bl,wh,bl
            };
            data=nData;
        }
        if (data!=null){
            System.arraycopy(data,0,oldData,0,Math.min(data.length,this.getRaster().getWidth()*this.getRaster().getHeight()));
        }
    }
}