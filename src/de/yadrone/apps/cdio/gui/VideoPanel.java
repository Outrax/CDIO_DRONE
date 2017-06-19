package de.yadrone.apps.cdio.gui;

import de.yadrone.apps.cdio.utilities.OpenCVUtils;
import de.yadrone.base.video.ImageListener;
import static de.yadrone.apps.cdio.utilities.OpenCVUtils.findAndDrawEllipse;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import whiteBalance.tools.Calibrator;
import whiteBalance.tools.WhiteBalance;

public class VideoPanel extends JPanel {
    private final Controller controller;
    protected BufferedImage imageRaw = null;
    
    public VideoPanel(Controller controller) {
        this.controller = controller;
    }
    
    protected void addVideoListener(ImageListener videoListener) {
        controller.drone.getVideoManager().addImageListener(videoListener);
    }
    
    protected void addCameraSwitchListener(MouseAdapter cameraSwitchListener) {
        addMouseListener(cameraSwitchListener);
    }
    
    protected void updateImage(BufferedImage newImage) {
        imageRaw = newImage;
        
        SwingUtilities.invokeLater(() -> {
            repaint();
        });
    }
    
    @Override
    synchronized public void paint(Graphics g) {
        if (imageRaw != null) {
            //Correct colors
            if (MenuPanel.colorOffset != null) {
                WhiteBalance wb = new WhiteBalance(MenuPanel.colorOffset[0], MenuPanel.colorOffset[1], MenuPanel.colorOffset[2]);
                Calibrator calib = new Calibrator(imageRaw, true);
                wb.colorImage(calib.getImage());
            }
            
            //Analyse imageRaw
            OpenCVUtils.ImageAnalyticsModel imageAnalytics = findAndDrawEllipse(OpenCVUtils.bufferedImageToMat(imageRaw), controller.hsvHandler.getSettings());
            
            //compute flight
            if(imageAnalytics != null) {
                controller.computeFlight(imageAnalytics);
                
                //Draw analysed image
                BufferedImage imgAnalysed = (BufferedImage) OpenCVUtils.toBufferedImage(imageAnalytics.sourceImg);
                g.drawImage(imgAnalysed, 0, 0, getWidth(), getHeight(), null);
            }
            else
                g.drawImage(imageRaw, 0, 0, getWidth(), getHeight(), null);
        }
        else
            g.drawString("WAITING FOR INPUT..", this.getWidth()/2, this.getHeight()/2);
    }
}