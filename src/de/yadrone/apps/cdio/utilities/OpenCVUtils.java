package de.yadrone.apps.cdio.utilities;
import boofcv.alg.color.ColorHsv;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import de.yadrone.apps.cdio.data.Data;
import de.yadrone.apps.cdio.data.HSVHandler;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class OpenCVUtils
{
    public static Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }
    
    public static Image toBufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1)
            type = BufferedImage.TYPE_3BYTE_BGR;
        
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
    
    public static Result QRScanner(BufferedImage image) {
        Result result = null;
        
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        try {
            result = new MultiFormatReader().decode(bitmap);
        } catch (NotFoundException e) { }
        
        return result;
    }
    
    public synchronized static ImageAnalyticsModel findAndDrawEllipse(Mat sourceImg, HSVHandler.HSVSetting hsvSettings) {
        Rect rect = null;
        Mat hsvImg = new Mat();
        Imgproc.cvtColor(sourceImg, hsvImg, Imgproc.COLOR_BGR2HSV);
        Mat lower_hue_range = new Mat();
        Mat upper_hue_range = new Mat();
//        Core.inRange(hsvImg, new Scalar(0, 100, 45), new Scalar(15, 255, 255), lower_hue_range);
//        Core.inRange(hsvImg, new Scalar(160, 100, 45), new Scalar(180, 255, 255), upper_hue_range);
        Core.inRange(hsvImg, hsvSettings.getR1Lower(), hsvSettings.getR1Upper(), lower_hue_range);
        Core.inRange(hsvImg, hsvSettings.getR2Lower(), hsvSettings.getR2Upper(), upper_hue_range);
        Mat red_hue_image = new Mat();
        Core.addWeighted(lower_hue_range, 1.0, upper_hue_range, 1.0, 0, red_hue_image);
       Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10));
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 6));
        
//        Imgproc.blur(red_hue_image, red_hue_image, new Size(9, 9));
        Imgproc.blur(red_hue_image, red_hue_image, new Size(8, 8), new Point(2, 2));

         Imgproc.erode(red_hue_image, red_hue_image, erodeElement);
         Imgproc.dilate(red_hue_image, red_hue_image, dilateElement);
        // init
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        
        // find contours
        Imgproc.findContours(red_hue_image, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        // if any contour exist...
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        
        // For each contour found
        MatOfPoint2f contour2fbest = null;
        RotatedRect rotatedrect;
        RotatedRect rotatedrectbest = null;
        double aspect;
        for (MatOfPoint contour : contours) {
            // Convert contours(i) from MatOfPoint to MatOfPoint2f
            if (contour.toArray().length > 5) {
                rotatedrect = Imgproc.fitEllipse(new MatOfPoint2f(contour.toArray()));
                aspect = rotatedrect.boundingRect().height / rotatedrect.boundingRect().width;
                if (aspect > 0.8 && aspect < 1.9 && rotatedrect.boundingRect().area() > 15000 &&rotatedrect.boundingRect().area()<500000) {
                    if (rotatedrectbest == null) {
                        rotatedrectbest = rotatedrect;
                        contour2fbest = new MatOfPoint2f(contour.toArray());
                    } else if (rotatedrectbest.boundingRect().area() < rotatedrect.boundingRect().area()) {
                        rotatedrectbest = rotatedrect;
                        contour2fbest = new MatOfPoint2f(contour.toArray());
                    }
                }
            }
        }
        try {
            if (contour2fbest != null && rotatedrectbest != null) {
                double approxDistance = Imgproc.arcLength(contour2fbest, true) * 0.02;
                Imgproc.approxPolyDP(contour2fbest, approxCurve, approxDistance, true);
                
                // Convert back to MatOfPoint
                MatOfPoint points = new MatOfPoint(approxCurve.toArray());
                
                // Get bounding rect of contour
                rect = Imgproc.boundingRect(points);
                
                // draw enclosing rectangle (all same color, but you could use
                // variable i to make them unique)
                Imgproc.rectangle(sourceImg, rect.tl(), rect.br(), new Scalar(255, 0, 0), 1, 8, 0);
                Imgproc.ellipse(sourceImg, rotatedrectbest, new Scalar(255, 192, 203), 4, 8);
            }
        }
        catch (CvException e) {
            System.out.println("Ingen ellipse fundet: " + e);
        }
        if(Data.SHOW_BINARY)
            return new ImageAnalyticsModel(red_hue_image, rect, rotatedrectbest);
        else
            return new ImageAnalyticsModel(sourceImg, rect, rotatedrectbest);
    }
    
    public static class ImageAnalyticsModel {
        public Mat sourceImg;
        public Rect rect;
        public RotatedRect rotatedrectbest;

        public ImageAnalyticsModel(Mat sourceImg, Rect rect, RotatedRect rotatedrectbest) {
            this.sourceImg = sourceImg;
            this.rect = rect;
            this.rotatedrectbest = rotatedrectbest;
        }
    }
    
    public static double[] hsvToRGB(int hue, int saturation, int value) {
        double[] rgb = new double[3];
        
        double s = 0, v = 0;
        if(saturation != 0)
            s = saturation/255.0;
        if(value != 0)
            v = value/255.0;
        
        ColorHsv.hsvToRgb(Math.toRadians(hue * 2), s, v, rgb);
        rgb[0] *= 255;
        rgb[1] *= 255;
        rgb[2] *= 255;
        
        return rgb;
    }
}