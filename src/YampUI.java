import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class YampUI extends JFrame {
	
	// Swing compoenents declaration
	private JButton btnOpen;
	private JButton btnPlay;
	private JButton btnPause;
	private JButton btnResume;
	private JButton btnStop;
//	private JButton btnSkip;
	private JLabel lblFilePath;
	private JLabel lblVolume;
	private JLabel lblTime;
	private JLabel lblTotal;
	private JFileChooser fc;
	private JTextField txtFilePath;
//	private JTextField txtSkip;
	private JSlider sldVolume;
	private JSlider sldTime;
	// Yamp driver
	private YampDriver driver;
	// User selected file
	private File selectedFile;
	// Total time of current song
	private int totalTime;
	// Total number of bytes
	private int totalBytes;
	
	/**
	 * Constructor
	 * 
	 * @param title The title of the window
	 * @param driver An instance of the YampDriver class
	 */
	public YampUI(String title, YampDriver driver) {
		super(title);
		initUI();
		this.driver = driver;
	}
	
	public void updateTime(int seconds) {
		int min = seconds/60;
		int sec = seconds%60;
		lblTime.setText(Integer.toString(min) + ":" + Integer.toString(sec));
		sldTime.setValue(100*seconds/totalTime);
	}
	
	public void setTotalTime(int seconds) {
		totalTime = seconds;
		int min = seconds/60;
		int sec = seconds%60;
		lblTotal.setText(Integer.toString(min) + ":" + Integer.toString(sec));
	}
	
	public void setTotalBytes(int totalBytes) {
		this.totalBytes = totalBytes;
	}
	

	/**
	 *  This method initializes the GUI elements
	 */
	public void initUI() {
		//setTitle("Yamp");
        setSize(460, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // optional: this line set the window to the center of screen
        setLocationRelativeTo(null);
        // No layout manager: absolute position
        setLayout(null);
        
        // Initialize file chooser
        fc = new JFileChooser();
        
        // Setup file path label
        lblFilePath = new JLabel("File opened:");
        lblFilePath.setBounds(50, 20, 200, 25);
        add(lblFilePath);
        
        // Setup text field
        txtFilePath = new JTextField(40);
        txtFilePath.setBounds(50, 50, 360, 25);
        add(txtFilePath);
        
        // Setup Open button
        btnOpen = new JButton("Open");
        btnOpen.setBounds(20, 130, 80, 25);
        btnOpen.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // when open button is clicked open a file chooser dialog
        	int returnVal = fc.showOpenDialog(YampUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Opening: " + selectedFile.getPath() + ".");
                txtFilePath.setText(selectedFile.getName());
                driver.open(selectedFile.getPath());
            } else {
                System.out.println("Open command cancelled by user.");
                txtFilePath.setText("No file selected");
            }
          }
        });
        add(btnOpen);

        // Setup Play button
        btnPlay = new JButton("Play");
        btnPlay.setBounds(100, 130, 80, 25);
        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// when play button is clicked play the current open song
            	driver.play();
            }
          });
        add(btnPlay);

        // Setup pause button
        btnPause = new JButton("Pause");
        btnPause.setBounds(180, 130, 80, 25);
        btnPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// when pause button is clicked pause the current open song
            	driver.pause();
            }
          });
        add(btnPause);
       
        // Setup resume button
        btnResume = new JButton("Resume");
        btnResume.setBounds(260, 130, 100, 25);
        btnResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// when stop button is clicked stop the current open song
            	driver.resume();
            }
          });
        add(btnResume);
        
        // Setup stop button
        btnStop = new JButton("Stop");
        btnStop.setBounds(360, 130, 80, 25);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// when stop button is clicked stop the current open song
            	driver.stop();
            }
          });
        add(btnStop);
        
        // Setup volume label
        lblVolume = new JLabel("Volume level: 85");
        lblVolume.setBounds(50, 160, 140, 25);
        add(lblVolume);
        
        // Setup volume slider
        sldVolume = new JSlider();
        sldVolume = new JSlider(0, 100, 85);
        sldVolume.setBounds(180, 160, 200, 25);
        sldVolume.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                lblVolume.setText("Volume level: " + sldVolume.getValue());
                driver.setVolume(sldVolume.getValue());
            }
        });
        add(sldVolume);

        // Setup time label
        lblTime = new JLabel("00:00");
        lblTime.setBounds(50, 80, 40, 25);
        add(lblTime);
        
        // Setup remaining time label
        lblTotal = new JLabel("??:??");
        lblTotal.setBounds(380, 80, 40, 25);
        add(lblTotal);
        
        // Setup time slider
        sldTime = new JSlider();
        sldTime = new JSlider(0, 100, 0);
        sldTime.setBounds(50, 100, 360, 25);
        sldTime.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
//                driver.jump(sldTime.getValue());
            }
        });
        add(sldTime);
        
        // Setup text field
//        txtSkip = new JTextField(40);
//        txtSkip.setBounds(50, 210, 80, 25);
//        add(txtSkip);
//        
//        // Setup skip button
//        btnSkip = new JButton("Skip");
//        btnSkip.setBounds(140, 210, 80, 25);
//        btnSkip.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//            	driver.jump(Integer.parseInt(txtSkip.getText()));
//            }
//          });
//        add(btnSkip);
        
     // Setup Info button
        btnStop = new JButton("File Info");
        btnStop.setBounds(20, 200, 120, 25);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	driver.displayInfo();
            }
          });
        add(btnStop);
        
        
	}

}
