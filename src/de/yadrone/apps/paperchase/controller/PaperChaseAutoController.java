package de.yadrone.apps.paperchase.controller;

import java.util.ArrayList;
import java.util.Random;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import de.yadrone.apps.paperchase.PaperChase;
import de.yadrone.apps.paperchase.TagListener;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;

public class PaperChaseAutoController extends PaperChaseAbstractController implements TagListener
{
	private final static int SPEED = 5;
	private final static int SLEEP = 500;
	
	/* This list holds tag-IDs for all tags which have successfully been visited */
	private ArrayList<String> tagVisitedList = new ArrayList<String>();
	public enum STATE {NOT_CENTERED,CENTERED,STRAY_AROUND,FLY}
	private Result tag;
	private float tagOrientation;
	private STATE currentState = STATE.STRAY_AROUND;
	public PaperChaseAutoController(IARDrone drone)
	{
		super(drone);
		
	}
	
	
	public void ControlLoop(){
		
		while(!doStop){
		switch(currentState){
		case NOT_CENTERED:
			try {
				centerTag();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CENTERED:
			this.currentState = STATE.FLY;
			break;
			
		case STRAY_AROUND:
			try {
				strayAround();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case FLY:
			flyThroughTag();
			break;
		
		
		}
		}
	}
	
	
	
	
	public void run()
	{
		ControlLoop();
		while(!doStop) // control loop
		{
			try
			{
				if ((tag != null) && (System.currentTimeMillis() - tag.getTimestamp() > 500)) {// reset if too old (and not updated)
					tag = null;
			}
				if ((tag == null) || hasTagBeenVisited())
				{
					System.out.println("Stray around køres");
					//strayAround();
					currentState = STATE.STRAY_AROUND;
				}
				else if (!isTagCentered()) // tag visible, but not centered
				{
					System.out.println("Jeg centrerer mig i forhold til tag");
					//centerTag();
					currentState = STATE.NOT_CENTERED;
				}
				else
				{
					System.out.println("Jeg flyver nu igennem tagget");
					
					
				}
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}
		}
	}

	public void onTag(Result result, float orientation)
	{
		System.out.println("VI ER VED ET TAG");
		if (result == null) // ToDo: do not call if no tag is present
			return;
		
		System.out.println("PaperChaseAutoController: Tag found");

		tag = result;
		tagOrientation = orientation;
		if(!isTagCentered()){
			try {
				centerTag();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			flyThroughTag();
		}
	}
	
	
	private boolean isTagCentered()
	{
		System.out.println("TAGCENTERED");
		if (tag == null)
			return false;
		
		// a tag is centered if it is
		// 1. if "Point 1" (on the tag the upper left point) is near the center of the camera  
		// 2. orientation is between 350 and 10 degrees
		
		int imgCenterX = PaperChase.IMAGE_WIDTH / 2;
		int imgCenterY = PaperChase.IMAGE_HEIGHT / 2;
		
		ResultPoint[] points = tag.getResultPoints();
		boolean isCentered = ((points[1].getX() > (imgCenterX - PaperChase.TOLERANCE)) &&
			(points[1].getX() < (imgCenterX + PaperChase.TOLERANCE)) &&
			(points[1].getY() > (imgCenterY - PaperChase.TOLERANCE)) &&
			(points[1].getY() < (imgCenterY + PaperChase.TOLERANCE)));

		boolean isOriented = ((tagOrientation < 10) || (tagOrientation > 350));
			
		System.out.println("PaperChaseAutoController: Tag centered ? " + isCentered + " Tag oriented ? " + isOriented);
		
		return isCentered && isOriented;
	}
	
	private boolean hasTagBeenVisited()
	{
		synchronized(tag)
		{
			for (int i=0; i < tagVisitedList.size(); i++)
			{
				if (tag.getText().equals(tagVisitedList.get(i)))
					return true;
			}
		}
		
		return false;
	}
	
	private void strayAround() throws InterruptedException
	{
		System.out.println("STRAYAROUND ER IGANG");
		//int direction = new Random().nextInt() % 4;
		int direction = 6;
		
		switch(direction)
		{
			case 0 : drone.getCommandManager().forward(SPEED); System.out.println("PaperChaseAutoController: Stray Around: FORWARD"); break;
			case 1 : drone.getCommandManager().backward(SPEED); System.out.println("PaperChaseAutoController: Stray Around: BACKWARD");break;
			case 2 : drone.getCommandManager().goLeft(SPEED); System.out.println("PaperChaseAutoController: Stray Around: LEFT"); break;
			case 3 : drone.getCommandManager().goRight(SPEED); System.out.println("PaperChaseAutoController: Stray Around: RIGHT");break;
			case 4 : drone.getCommandManager().spinLeft(10).doFor(2500); System.out.println("PaperChaseAutocontroller: Stray Around: Spin Left"); break;
			case 5 : drone.getCommandManager().spinRight(20).doFor(2500); System.out.println("PaperChaseAutocontroller: Stray Around: Spin Right"); break;
			case 6 : drone.getCommandManager().hover().doFor(2500);System.out.println("HOVERING");break;
		}
		
		Thread.currentThread().sleep(SLEEP);
	}
	
	private void centerTag() throws InterruptedException
	{
		String tagText;
		ResultPoint[] points;
		
		synchronized(tag)
		{
			points = tag.getResultPoints();	
			tagText = tag.getText();
		}
		
		int imgCenterX = PaperChase.IMAGE_WIDTH / 2;
		int imgCenterY = PaperChase.IMAGE_HEIGHT / 2;
		
		float x = points[1].getX();
		float y = points[1].getY();
		
		if ((tagOrientation > 10) && (tagOrientation < 180))
		{
			System.out.println("PaperChaseAutoController: Spin left");
			drone.getCommandManager().spinLeft(SPEED * 2);
			Thread.currentThread().sleep(SLEEP);
		}
		else if ((tagOrientation < 350) && (tagOrientation > 180))
		{
			System.out.println("PaperChaseAutoController: Spin right");
			drone.getCommandManager().spinRight(SPEED * 2);
			Thread.currentThread().sleep(SLEEP);
		}
		else if (x < (imgCenterX - PaperChase.TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go left");
			drone.getCommandManager().goLeft(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else if (x > (imgCenterX + PaperChase.TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go right");
			drone.getCommandManager().goRight(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else if (y < (imgCenterY - PaperChase.TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go forward");
			drone.getCommandManager().forward(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else if (y > (imgCenterY + PaperChase.TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go backward");
			drone.getCommandManager().backward(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else
		{
			System.out.println("PaperChaseAutoController: Tag centered");
			drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN, 10, 5);
			
			tagVisitedList.add(tagText);
			flyThroughTag();
		}
	}

	@Override
	public void onTags(Result[] result, float orientation) {
		// TODO Auto-generated method stub
		
	}
	public void flyThroughTag(){
		drone.getCommandManager().forward(50);
	}
	
	
}
