package src.errors;

/**
 * Thrown in FileIO.fileToLines when file line count exceeds the {@code max} parameter
 */
public class MaxLines extends Exception{
    public MaxLines(){
        super();
    }

    public MaxLines(String message){
        super(message);
    }
}
