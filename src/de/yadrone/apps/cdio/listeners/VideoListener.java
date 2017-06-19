package de.yadrone.apps.cdio.listeners;

import de.yadrone.base.video.ImageListener;
import de.yadrone.apps.cdio.gui.Controller;
import java.awt.image.BufferedImage;

public class VideoListener implements ImageListener
{
    private final Controller controller;

    public VideoListener(Controller controller) {
        this.controller = controller;
    }
    
    @Override
    public void imageUpdated(BufferedImage bi) {
        controller.updateImage(bi);
    }
}