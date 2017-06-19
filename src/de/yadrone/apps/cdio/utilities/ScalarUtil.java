package de.yadrone.apps.cdio.utilities;
import java.io.Serializable;
import org.opencv.core.Scalar;

public class ScalarUtil implements Serializable
{
    public double[] val = new double[3];
    
    public ScalarUtil(double v0, double v1, double v2) {
        val = new double[]{v0,v1,v2};
    }
    
    public ScalarUtil(Scalar scalar) {
        this(scalar.val[0], scalar.val[1], scalar.val[2]);
    }
    
    public Scalar toScalar() {
        return new Scalar(val[0], val[1], val[2]);
    }
    
    public void toDFMScalar(Scalar scalar) {
        this.val = scalar.val;
    }
}