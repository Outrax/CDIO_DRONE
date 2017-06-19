package de.yadrone.apps.cdio.data;

import de.yadrone.apps.cdio.utilities.ScalarUtil;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import org.opencv.core.Scalar;

public class HSVHandler implements Serializable
{
    //Range 1
    private static final Scalar DEFAULT_R1_LOWER = new Scalar(0, 100, 110); //HSV = 0, 150, 100 (0, 150, 100)
    private static final Scalar DEFAULT_R1_UPPER = new Scalar(8, 255, 255); //HSV = 8, 255, 255 (10, 255, 255)
    
    //Range 2
    private static final Scalar DEFAULT_R2_LOWER = new Scalar(160, 59, 140); //HSV = 160, 59, 140 (160, 150, 100)
    private static final Scalar DEFAULT_R2_UPPER = new Scalar(179, 255, 255); //HSV = 179, 255, 255 (179, 255, 255)
    
    private final String filePath;
    private static HSVSetting hsvSetting;
    
    public HSVHandler(String filePath, boolean loadFile) {
        this.filePath = filePath;
        
        if(loadFile) {
            if(load(false) == null) {
                resetToDefault();
            }
        }
    }
    
    public HSVSetting load(boolean verbose) {
        HSVSetting setting;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            setting = (HSVSetting) ois.readObject();
            updateSettings(setting);
        } catch (Exception e) {
            e.printStackTrace();
            if(verbose){
            	
            }
        }
        
        return hsvSetting;
    }
    
    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(hsvSetting);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void resetToDefault() {
        updateSettings(new HSVSetting(DEFAULT_R1_LOWER, DEFAULT_R1_UPPER, DEFAULT_R2_LOWER, DEFAULT_R2_UPPER));
        save();
        load(true);
    }
    
    public HSVSetting getSettings() {
        return hsvSetting;
    }
    
    public void updateSettings(HSVSetting setting) {
        this.hsvSetting = setting;
    }
    
    public void updateRange1Lower(Scalar hsvValue) {
        this.hsvSetting.setR1Lower(hsvValue);
    }
    
    public Scalar getRange1Lower() {
        return this.hsvSetting.getR1Lower();
    }
    
    public void updateRange1Upper(Scalar hsvValue) {
        this.hsvSetting.setR1Upper(hsvValue);
    }
    
    public Scalar getRange1Upper() {
        return this.hsvSetting.getR1Upper();
    }
    
    public void updateRange2Lower(Scalar hsvValue) {
        this.hsvSetting.setR2Lower(hsvValue);
    }
    
    public Scalar getRange2Lower() {
        return this.hsvSetting.getR2Lower();
    }
    
    public void updateRange2Upper(Scalar hsvValue) {
        this.hsvSetting.setR2Upper(hsvValue);
    }
    
    public Scalar getRange2Upper() {
        return this.hsvSetting.getR2Upper();
    }
    
    public class HSVSetting implements Serializable {
        //Range 1
        private ScalarUtil r1Lower;
        private ScalarUtil r1Upper;
        
        //Range 2
        private ScalarUtil r2Lower;
        private ScalarUtil r2Upper;

        public HSVSetting(Scalar r1Lower, Scalar r1Upper, Scalar r2Lower, Scalar r2Upper) {
            this.r1Lower = new ScalarUtil(r1Lower);
            this.r1Upper = new ScalarUtil(r1Upper);
            this.r2Lower = new ScalarUtil(r2Lower);
            this.r2Upper = new ScalarUtil(r2Upper);
        }

        public Scalar getR1Lower() {
            return r1Lower.toScalar();
        }

        public void setR1Lower(Scalar r1Lower) {
            this.r1Lower = new ScalarUtil(r1Lower);
        }

        public Scalar getR1Upper() {
            return r1Upper.toScalar();
        }

        public void setR1Upper(Scalar r1Upper) {
            this.r1Upper = new ScalarUtil(r1Upper);
        }

        public Scalar getR2Lower() {
            return r2Lower.toScalar();
        }

        public void setR2Lower(Scalar r2Lower) {
            this.r2Lower = new ScalarUtil(r2Lower);
        }

        public Scalar getR2Upper() {
            return r2Upper.toScalar();
        }

        public void setR2Upper(Scalar r2Upper) {
            this.r2Upper = new ScalarUtil(r2Upper);
        }

        @Override
        public String toString() {
            return "HSVSetting{r1Lower=" + r1Lower + ", r1Upper=" + r1Upper + ", r2Lower=" + r2Lower + ", r2Upper=" + r2Upper + '}';
        }
    }
}