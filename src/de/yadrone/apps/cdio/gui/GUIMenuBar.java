package de.yadrone.apps.cdio.gui;

import de.yadrone.apps.cdio.listeners.MenuItemListener;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class GUIMenuBar extends JMenuBar
{
    public static final String AC_WB = "miacWB";
    public static final String AC_HSV = "miacHSV";
    public static final String AC_SETTINGS = "miacSET";
    public static final String AC_MANUAL = "miacMAN";
    
    public static final String AC_BCAM = "miacBCAM";
    
    public static final String AC_HELP = "miacHELP";
    public static final String AC_ABOUT = "miacABOUT";
    
    public GUIMenuBar(Controller controller) {
        setBackground(Color.LIGHT_GRAY);
        MenuItemListener mil = new MenuItemListener(controller);
        int defaultShortcutKey = Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask();
        
        //Tools menu
        JMenu tools = new JMenu("Tools");
        
        JMenuItem miHSV = new JMenuItem("Adjust HSV filter");
        JMenuItem miWB = new JMenuItem("White Balance");
        JMenuItem miManual = new JMenuItem("Manual Control");
        
        miHSV.setAccelerator(KeyStroke.getKeyStroke('H', defaultShortcutKey));
        miWB.setAccelerator(KeyStroke.getKeyStroke('W', defaultShortcutKey));
        miManual.setAccelerator(KeyStroke.getKeyStroke('M', defaultShortcutKey));
        
        miHSV.setActionCommand(AC_HSV);
        miWB.setActionCommand(AC_WB);
        miManual.setActionCommand(AC_MANUAL);
        
        miHSV.addActionListener(mil);
        miWB.addActionListener(mil);
        miManual.addActionListener(mil);
        
        tools.add(miHSV);
        tools.add(miWB);
        tools.addSeparator();
        tools.add(miManual);
        
        //Options menu
        JMenu options = new JMenu("Options");
        
        JCheckBoxMenuItem cbmiBottomCam = new JCheckBoxMenuItem("Bottom camera", false);
        cbmiBottomCam.setActionCommand(AC_BCAM);
        cbmiBottomCam.addActionListener(mil);
        
        options.add(cbmiBottomCam);
        
        //Help menu
        JMenu help = new JMenu("Help");
        
        JMenuItem miSettings = new JMenuItem("Settings");
        JMenuItem miHelp = new JMenuItem("Help");
        JMenuItem miAbout = new JMenuItem("About");
        
        miSettings.setAccelerator(KeyStroke.getKeyStroke('S', defaultShortcutKey));
        miHelp.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.ALT_DOWN_MASK));
        
        miSettings.setActionCommand(AC_SETTINGS);
        miHelp.setActionCommand(AC_HELP);
        miAbout.setActionCommand(AC_ABOUT);
        
        miSettings.addActionListener(mil);
        miHelp.addActionListener(mil);
        miAbout.addActionListener(mil);
        
        miHelp.setEnabled(false);
        
        help.add(miSettings);
        help.addSeparator();
        help.add(miHelp);
        help.add(miAbout);
        
        //Menu
        add(tools);
        add(options);
        add(help);
    }
}