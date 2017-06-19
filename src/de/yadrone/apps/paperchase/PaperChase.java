package de.yadrone.apps.paperchase;

import de.yadrone.apps.controlcenter.plugins.altitude.AltitudeChart;
import de.yadrone.apps.controlcenter.plugins.configuration.ConfigurationPanel;
import de.yadrone.apps.controlcenter.plugins.statistics.StatisticsPanel;
import de.yadrone.apps.paperchase.controller.PaperChaseAbstractController;
import de.yadrone.apps.paperchase.controller.PaperChaseAutoController;
import de.yadrone.apps.paperchase.controller.PaperChaseKeyboardController;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;

public class PaperChase 
{
	public final static int IMAGE_WIDTH = 640; // 640 or 1280
	public final static int IMAGE_HEIGHT = 360; // 360 or 720
	
	public final static int TOLERANCE = 80;
	
	private IARDrone drone = null;
	private PaperChaseAbstractController autoController;
	private QRCodeScanner scanner = null;
	private ObjectDetection objectdetection = null;
	
	public PaperChase()
	{
		drone = new ARDrone();
		drone.start();
		
		drone.up();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
	
		new AltitudeChart();
		new ConfigurationPanel();
		new StatisticsPanel();
		PaperChaseGUI gui = new PaperChaseGUI(drone, this);
		
		// keyboard controller is always enabled and cannot be disabled (for safety reasons)
		PaperChaseKeyboardController keyboardController = new PaperChaseKeyboardController(drone);
		keyboardController.start();
		
		// auto controller is instantiated, but not started
		autoController = new PaperChaseAutoController(drone);
		
		scanner = new QRCodeScanner();
		scanner.addListener(gui);
		
		objectdetection = new ObjectDetection();
		
		drone.getVideoManager().addImageListener(objectdetection);
		
		drone.getVideoManager().addImageListener(gui);
		drone.getVideoManager().addImageListener(scanner);
		drone.setMinAltitude(2000);
		drone.setMaxAltitude(2500);
	}
	
	public void enableAutoControl(boolean enable)
	{
		if (enable)
		{
			System.out.println("AUTOCONTROL ER SLÅET TIL");
			scanner.addListener(autoController);
			autoController.start();
		}
		else
		{
			autoController.stopController();
			scanner.removeListener(autoController); // only auto autoController registers as TagListener
		}
	}
	
	
	
	public static void main(String[] args)
	{
		new PaperChase();
	}
	
}
