package de.yadrone.apps.cdio.gui;

import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import de.yadrone.apps.cdio.listeners.ButtonClickListener;
import de.yadrone.apps.cdio.listeners.StatusChangeListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Console extends JFrame implements IConsole, StatusChangeListener, ActionListener
{
    private final static int TOOLBAR_WIDTH  = 1280;
    private final static int TOOLBAR_HEIGHT = 40;

    private final static int VIDEO_WIDTH  = 1280;
    private final static int VIDEO_HEIGHT = 720;

    private final static int OUTPUT_WIDTH  = 1280;
    private final static int OUTPUT_HEIGHT = 160;

    private final Map<JButton, ButtonType> buttons = new HashMap<>();

    private final Map<ButtonType, List<ButtonClickListener>> listeners = new HashMap<>();

    private JButton start;
    private JButton stop;
    private JButton emergency;
    private JButton testing;

    private JLabel battery;
    private JLabel altitude;
    private JLabel strategy;

    private JPanel video;

    private JTextArea   textarea;
    private PrintStream output;

    private BufferedImage image;

    private Color standardButtonBackground;
    private boolean isTestingEnabled = false;

    public Console(String title)
    {
        super(title);

        /*
         * Ensure the window holds aspect ratio.
         */
        this.setResizable(true);

        /*
         * Set the close operation on the window.
         */
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /*
         * Builds the layout of the window.
         */
        this.add(this.buildComponents());

        /*
         * Sets up the output stream which can be written to and be
         * displayed in the console.
         */
        this.output = new PrintStream(new ConsoleOutputStream(this.textarea));
    }

    /**
     * Displays the window and requests focus from the OS.
     */
    public void display()
    {
        this.pack();
        this.setVisible(true);

        this.setLocationRelativeTo(null);

        if(this.isFocusable())
        {
            this.requestFocus();
        }
    }

    public void redirectSystemOutput()
    {
        System.setOut(this.output);
        System.setErr(this.output);
    }

    public void addButtonListener(ButtonType type, ButtonClickListener listener)
    {
        if(!this.listeners.containsKey(type))
        {
            //this.listeners.put(type, new ArrayList<>());
        }

        this.listeners.get(type).add(listener);
    }

    public PrintStream getOutputStream()
    {
        return this.output;
    }

    public void setIsTestingEnabled(boolean state)
    {
        this.isTestingEnabled = state;

        if(state)
        {
            this.testing.setBackground(Color.GREEN);
        }
        else
        {
            this.testing.setBackground(this.standardButtonBackground);
        }
    }

    @Override
    public void actionPerformed(ActionEvent action)
    {
        Object source = action.getSource();

        if(!(source instanceof JButton))
        {
            return;
        }

        ButtonType key = this.buttons.getOrDefault(source, null);

        if(key == null)
        {
            System.err.println("Couldn't determine correct button ID for clicked source!");

            return;
        }

        for(ButtonClickListener listener : this.listeners.get(key))
        {
            listener.onClick(this);
        }

        if(key == ButtonType.START)
        {
            this.testing.setEnabled(false);
            this.start.setEnabled(false);

            this.stop.setEnabled(true);
        }
        else if(key == ButtonType.STOP)
        {
            this.testing.setEnabled(true);
            this.start.setEnabled(true);

            this.stop.setEnabled(false);
        }
        else if(key == ButtonType.TESTING)
        {
            this.isTestingEnabled = !this.isTestingEnabled;
            this.setIsTestingEnabled(this.isTestingEnabled);

            if(this.isTestingEnabled)
            {
                System.out.println("Testing enabled");
                this.testing.setBackground(Color.GREEN);
            }
            else
            {
                System.out.println("Testing disabled");
                this.testing.setBackground(this.standardButtonBackground);
            }
        }
    }

    public void setImage(BufferedImage image)
    {
        this.image = image;
    }

    public void repaint()
    {
        this.video.repaint();
    }

    public void println(String message)
    {
        this.output.println(message);
    }

    @Override
    public void onBatteryChange(int percentage)
    {
        this.battery.setText("Battery: " + percentage + "%");
    }

    @Override
    public void onAltitudeChange(int altitude)
    {
        this.altitude.setText("Altitude: " + altitude + " cm");
    }

    @Override
    public void onStrategyChange(String name)
    {
        this.strategy.setText("State: " + name);
    }

    private JPanel buildComponents()
    {
        JPanel main = new JPanel(new BorderLayout(0, 15));

        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15,15));

        JPanel      toolbar = this.createToolbar();
        JPanel      video   = this.createVideo();
        JScrollPane output  = this.createTextarea();

        toolbar.setPreferredSize(new Dimension(TOOLBAR_WIDTH, TOOLBAR_HEIGHT));
        output.setPreferredSize(new Dimension(OUTPUT_WIDTH, OUTPUT_HEIGHT));

        main.add(toolbar, BorderLayout.PAGE_START);
        main.add(video,   BorderLayout.CENTER);
        main.add(output,  BorderLayout.PAGE_END);

        this.start.addActionListener(this);
        this.stop.addActionListener(this);
        this.emergency.addActionListener(this);
        this.testing.addActionListener(this);

        /*
         * Load default button background
         */
        this.standardButtonBackground = this.start.getBackground();

        return main;
    }

    private JPanel createToolbar()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));

        panel.setBorder(BorderFactory.createEmptyBorder(0, -10, 0, 0));

        this.start     = new JButton("START");
        this.stop      = new JButton("STOP");
        this.emergency = new JButton("EMERGENCY");
        this.testing   = new JButton("CHANGE TEST MODE");

        /*
         * Set button IDs as they are allocated dynamically.
         */
        this.buttons.put(this.start, ButtonType.START);
        this.buttons.put(this.stop, ButtonType.STOP);
        this.buttons.put(this.emergency, ButtonType.EMERGENCY);
        this.buttons.put(this.testing, ButtonType.TESTING);

        this.battery  = new JLabel("Battery");
        this.altitude = new JLabel("Altitude");
        this.strategy = new JLabel("Strategy");

        panel.add(this.start);
        panel.add(this.stop);
        panel.add(this.emergency);
        panel.add(this.testing);

        panel.add(this.battery);
        panel.add(this.altitude);
        panel.add(this.strategy);

        /*
         * Set distinct color of emergency button
         */
        this.emergency.setBackground(Color.PINK);

        /*
         * Disable stop button as standard.
         */
        this.stop.setEnabled(false);

        return panel;
    }

    private JPanel createVideo()
    {
        this.video = new JPanel()
        {
            @Override
            public synchronized void paint(Graphics graphics)
            {
                if(image == null)
                {
                    return;
                }

                /*
                 * Calculate the image scaling with a 16:9 ratio.
                 */
                int height = video.getHeight();
                int width  = (height * 16) / 9;

                Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

                graphics.drawImage(scaled, 0, 0, scaled.getWidth(null), scaled.getHeight(null), null);
            }
        };

        this.video.setPreferredSize(new Dimension(VIDEO_WIDTH, VIDEO_HEIGHT));
        this.video.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        return this.video;
    }

    private JScrollPane createTextarea()
    {
        this.textarea = new JTextArea();

        /*
         * Force the textarea to update the caret position when
         * content is written to it.
         */
        ((DefaultCaret) this.textarea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.textarea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane pane = new JScrollPane(this.textarea,
           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        pane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        return pane;
    }
}