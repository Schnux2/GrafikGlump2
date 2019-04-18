import java.util.ArrayList;
public interface Lamp{
    //currently does nothing
    public Vector getPos();
    public void setPos(Vector v);
    public Point[] getLitPoints(ArrayList<GraphicObject> objs);
}