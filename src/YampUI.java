import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jlgui.basicplayer.BasicPlayerEvent;


public class YampUI extends JFrame {
	
	// Swing components declaration
	private JButton btnPlay;
	private JButton btnForward;
	private JButton btnRewind;
//	private JButton btnSkip;
	private JLabel lblTitle;
	private JLabel lblArtist;
	private JLabel lblAlbum;
	private JLabel lblVolume;
	private JLabel lblTime;
	private JLabel lblTotal;
	private JLabel lblDisplay;
	private JLabel lblCoverArt;
	private JSlider sldVolume;
	private JSlider sldTime;
//	private JTextField txtSkip;
	// File chooser
	private JFileChooser fc;

	private final int COVER_SIZE = 120;
	// Yamp driver
	private YampDriver driver;
	// User selected file
	private File selectedFile;
	// Total time of current song
	private int totalTime;
	// Total number of bytes
	private int totalBytes;

	/* Menu bar swing components */
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmOpen;
	private JMenuItem mntmOpenLyrics;
	private JMenuItem mntmFileInfo;
	private JMenuItem mntmQuit;
	private JMenu mnControl;
	private JMenuItem mntmPlay;
	private JMenuItem mntmPause;
	private JMenuItem mntmStop;
	private JMenuItem mntmPrevious;
	private JMenuItem mntmNext;
	private JMenu mnPlaylist;
	private JMenuItem mntmAddToPlaylist;
	private JMenuItem mntmRemoveSelections;
	private JMenuItem mntmClearPlaylist;
	private JMenuItem mntmSavePlaylist;
	private JMenuItem mntmLoadPlaylist;
	
	
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
		lblTime.setText(String.format("%02d", min) + ":" + String.format("%02d", sec));
		lblDisplay.setText(String.format("%02d", min) + ":" + String.format("%02d", sec));
		sldTime.setValue(100*seconds/totalTime);
	}
	
	public void setTotalTime(int seconds) {
		totalTime = seconds;
		int min = seconds/60;
		int sec = seconds%60;
		lblTotal.setText(String.format("%02d", min) + ":" + String.format("%02d", sec));
	}
	
	public void setTotalBytes(int totalBytes) {
		this.totalBytes = totalBytes;
	}
	

	/**
	 *  This method initializes the GUI elements
	 */
	public void initUI() {
		//setTitle("Yamp");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // optional: this line set the window to the center of screen
        setLocationRelativeTo(null);
        // No layout manager: absolute position
        setLayout(null);
        
        // Initialize file chooser
        fc = new JFileChooser();
        
        // Setup menu bar
        menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	            // when open button is clicked open a file chooser dialog
	        	int returnVal = fc.showOpenDialog(YampUI.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                selectedFile = fc.getSelectedFile();
	                //This is where a real application would open the file.
	                System.out.println("Opening: " + selectedFile.getPath() + ".");
	                try {
						Mp3File mp3file = new Mp3File(selectedFile.getPath());
						if (mp3file.hasId3v2Tag()) {
		        			ID3v2 id3v2Tag = mp3file.getId3v2Tag();
		        			lblTitle.setText("Title: " + id3v2Tag.getTitle());
		        			lblArtist.setText("Artist: " + id3v2Tag.getArtist());
		        			lblAlbum.setText("Album: " + id3v2Tag.getAlbum());
		        			BufferedImage coverart = null;
		        			coverart = ImageIO.read(new ByteArrayInputStream(id3v2Tag.getAlbumImage()));
		        			BufferedImage resizedimage = new BufferedImage(COVER_SIZE, COVER_SIZE, BufferedImage.TYPE_INT_RGB);
		        			Graphics g = resizedimage.createGraphics();
		        			g.drawImage(coverart, 0, 0, COVER_SIZE, COVER_SIZE, null);
		        			g.dispose();
		        			lblCoverArt.setIcon(new ImageIcon(resizedimage));
						}
					} catch (UnsupportedTagException | InvalidDataException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                driver.open(selectedFile.getPath());
//	                btnPlay.setText("Play");
	                btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
	            } else {
	                System.out.println("Open command cancelled by user.");
	            }
	          }
	        });
		mnFile.add(mntmOpen);
		
		mntmOpenLyrics = new JMenuItem("Open Lyrics");
		mnFile.add(mntmOpenLyrics);
		
		mntmFileInfo = new JMenuItem("File Info");
		mntmFileInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	driver.displayInfo();
            }
          });
		mnFile.add(mntmFileInfo);
		
		mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				driver.quit();
			}
		});
		mnFile.add(mntmQuit);
		
		mnControl = new JMenu("Control");
		menuBar.add(mnControl);
		
		mntmPlay = new JMenuItem("Play");
		mntmPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	driver.play();
//            	btnPlay.setText("Pause");
            	btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/pause.png"));
            	mntmPause.setText("Pause");
            }
          });
		mnControl.add(mntmPlay);
		
		mntmPause = new JMenuItem("Pause");
		mntmPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (driver.getState() == BasicPlayerEvent.PLAYING | driver.getState() == BasicPlayerEvent.RESUMED) {
            		driver.pause();
//            		btnPlay.setText("Resume");
            		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
            		mntmPause.setText("Resume");
            	}
            	if (driver.getState() == BasicPlayerEvent.PAUSED) {
            		driver.resume();
//            		btnPlay.setText("Pause");
            		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/pause.png"));
            		mntmPause.setText("Pause");
            	}
            }
          });
		mnControl.add(mntmPause);
		
		mntmStop = new JMenuItem("Stop");
		mntmStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	driver.stop();
//            	btnPlay.setText("Play");
            	btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
            	mntmPause.setText("Pause");
            }
          });
		mnControl.add(mntmStop);
		
		mntmPrevious = new JMenuItem("Previous");
		mnControl.add(mntmPrevious);
		
		mntmNext = new JMenuItem("Next");
		mnControl.add(mntmNext);
		
		mnPlaylist = new JMenu("Playlist");
		menuBar.add(mnPlaylist);
		
		mntmAddToPlaylist = new JMenuItem("Add to Playlist");
		mnPlaylist.add(mntmAddToPlaylist);
		
		mntmRemoveSelections = new JMenuItem("Remove Selections");
		mnPlaylist.add(mntmRemoveSelections);
		
		mntmClearPlaylist = new JMenuItem("Clear Playlist");
		mnPlaylist.add(mntmClearPlaylist);
		
		mntmSavePlaylist = new JMenuItem("Save Playlist");
		mnPlaylist.add(mntmSavePlaylist);
		
		mntmLoadPlaylist = new JMenuItem("Load Playlist");
		mnPlaylist.add(mntmLoadPlaylist);
        
        
		// Setup title label
        lblTitle = new JLabel("Title: ");
        lblTitle.setBounds(300, 40, 300, 25);
        add(lblTitle);
        
        // Setup artist label
        lblArtist = new JLabel("Artist: ");
        lblArtist.setBounds(300, 65, 300, 25);
        add(lblArtist);
        
        // Setup album label
        lblAlbum = new JLabel("Album: ");
        lblAlbum.setBounds(300, 90, 300, 25);
        add(lblAlbum);
        
        // Setup time display label
        lblDisplay = new JLabel("00:00");
        lblDisplay.setBounds(300, 10, 100, 30);
        lblDisplay.setFont(new Font("Dialog", Font.BOLD, 30));
        add(lblDisplay);
        
        // Setup Play button
        btnPlay = new JButton();
        btnPlay.setBounds(50, 30, 60, 60);
        btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (driver.getState() == BasicPlayerEvent.PLAYING | driver.getState() == BasicPlayerEvent.RESUMED) {
            		driver.pause();
//            		btnPlay.setText("Resume");
            		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
            		mntmPause.setText("Resume");
            	} else if (driver.getState() == BasicPlayerEvent.PAUSED) {
            		driver.resume();
//            		btnPlay.setText("Pause");
            		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/pause.png"));
            		mntmPause.setText("Pause");
            	} else if (driver.getState() == BasicPlayerEvent.STOPPED | driver.getState() == BasicPlayerEvent.OPENED) {
            		driver.play();
//            		btnPlay.setText("Pause");
            		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/pause.png"));
            		mntmPause.setText("Pause");
            	}
            }
          });
        add(btnPlay);
        
        // Setup FF button
        btnForward = new JButton();
        btnForward.setBounds(110, 40, 40, 40);
        btnForward.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/forward.png"));
        add(btnForward);
        
        // Setup RW button
        btnRewind = new JButton();
        btnRewind.setBounds(10, 40, 40, 40);
        btnRewind.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/rewind.png"));
        add(btnRewind);
        
        // Setup Cover Art label
        lblCoverArt = new JLabel();
        lblCoverArt.setBounds(290-COVER_SIZE, 0, COVER_SIZE, COVER_SIZE);
        add(lblCoverArt);

        // Setup time label
        lblTime = new JLabel("00:00");
        lblTime.setBounds(50, 120, 40, 25);
        add(lblTime);
        
        // Setup remaining time label
        lblTotal = new JLabel("??:??");
        lblTotal.setBounds(380, 120, 40, 25);
        add(lblTotal);
        
        // Setup time slider
        sldTime = new JSlider();
        sldTime = new JSlider(0, 100, 0);
        sldTime.setBounds(50, 140, 360, 25);
        sldTime.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
//                driver.jump(sldTime.getValue());
            }
        });
        add(sldTime);
        
        // Setup volume label
        lblVolume = new JLabel("Volume level: 85");
        lblVolume.setBounds(50, 170, 140, 25);
        add(lblVolume);
        
        // Setup volume slider
        sldVolume = new JSlider();
        sldVolume = new JSlider(0, 100, 85);
        sldVolume.setBounds(180, 170, 200, 25);
        sldVolume.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                lblVolume.setText("Volume level: " + sldVolume.getValue());
                driver.setVolume(sldVolume.getValue());
            }
        });
        add(sldVolume);
        
        
        
	}

}
