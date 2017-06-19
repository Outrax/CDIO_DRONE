package de.yadrone.apps.cdio.listeners;

import de.yadrone.apps.cdio.gui.Controller;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CameraSwitchListener extends MouseAdapter
{
//    private final Controller controller;

    public CameraSwitchListener(Controller controller) {
//        this.controller = controller;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
//        controller.getDrone().getCommandManager().setVideoChannel(VideoChannel.NEXT);
    }
}