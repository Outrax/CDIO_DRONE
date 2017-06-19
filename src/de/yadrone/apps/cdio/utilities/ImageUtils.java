package de.yadrone.apps.cdio.utilities;/*
 * Created by thomas on 29/03/2017.
 */

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageUtils
{
    public static BufferedImage fromMatrix(Mat matrix)
    {
        BufferedImage image;


        if(matrix.channels() > 1) {
            image = new BufferedImage(matrix.width(), matrix.height(), BufferedImage.TYPE_3BYTE_BGR);
        }
        else {
            image = new BufferedImage(matrix.width(), matrix.height(), BufferedImage.TYPE_BYTE_GRAY);
        }

        final byte[] source = new byte[matrix.width() * matrix.height() * matrix.channels()];
        final byte[] dest = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        matrix.get(0, 0, source);
        System.arraycopy(source, 0, dest, 0, source.length);

        return image;
    }

    public static Mat toMatrix(BufferedImage image)
    {
        Mat    mat  = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        mat.put(0, 0, data);

        return mat;
    }
}