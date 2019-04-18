public class Vector{
    public float x;
    public float y;
    public float z;
    public Vector(float x, float y, float z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    public Vector copy(){
        return new Vector(x,y,z);
    }
    public Vector add(Vector v){
        return new Vector(x+v.x,y+v.y,z+v.z);
    }
    public Vector subtract(Vector v){
        return new Vector(x-v.x,y-v.y,z-v.z);
    }
    public Vector multiply(float s){
        return new Vector(x*s,y*s,z*s);
    }
    public float dot(Vector v){ //is this right?
        return x*v.x+y*v.y+z*v.z;
    }
    public Vector cross(Vector v){
        return new Vector(y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x);
    }
    public float length(){
        return (float) Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));
    }
    public Vector normalize() throws ZeroVectorException{
        if (this.length()!=0){
            return this.multiply(1/this.length());
        }
        else{
            throw(new ZeroVectorException());
        }
    }
    public float angle(Vector v){ //untested
        if (this.length()!=0 && v.length()!=0){
            return (float) Math.acos(this.dot(v)/(this.length()*v.length()));
        }
        else {
            return 0;
        }
    }
    public String toString(){
        return "(x = "+x+", y = "+y+", z = "+z+", length = "+this.length()+")";
    }
}