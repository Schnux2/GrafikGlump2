public class ZeroVectorException extends Exception {
    public ZeroVectorException(){
        super();
    }
    public String getMessage(){
        return "Some Vector has the length zero but should not have.";
    }
}