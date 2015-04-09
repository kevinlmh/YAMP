import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jlgui.basicplayer.BasicPlayerEvent;


public class YampUI extends JFrame {
	// Cover art label size 
	private final int COVER_SIZE = 120;	
	// Yamp driver
	private YampDriver driver;
	// File chooser
	private JFileChooser fc;
	// User selected file
	private File selectedFile;
	// Yamp playlist
	private YampPlaylist playlist;
	// Total time of current song
	private int totalTime;
//	// Total number of bytes
//	private int totalBytes;
	private YampLyricsWindow lyricswindow;
	
	/* Swing components declaration */
	private JButton btnPlay;
	private JButton btnForward;
	private JButton btnRewind;
	private JButton btnStop;
	private JButton btnMute;
	private JLabel lblTitle;
	private JLabel lblArtist;
	private JLabel lblAlbum;
	private JLabel lblVolume;
	private JLabel lblDisplay;
	private JLabel lblRemain;
	private JLabel lblCoverArt;
	private JSlider sldVolume;
	private JSlider sldTime;

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
	
	/* Playlist components */
	private JTable table;
	private DefaultTableModel tablemodel;
	private JScrollPane scrollPane;
	private JButton btnAppend;
	private JButton btnRemove;
	
	
	/**
	 * Constructor
	 * 
	 * @param title The title of the window
	 * @param driver An instance of the YampDriver class
	 */
	public YampUI(String title, YampDriver driver, YampPlaylist playlist) {
		super(title);
		initUI();
		this.driver = driver;
		this.playlist = playlist;
	}
	
	/**
	 * 
	 * @param position of the song in seconds
	 */
	public void updateTime(int position) {
		lblDisplay.setText(String.format("%02d", position/60) + ":" + String.format("%02d", position%60));
		lblRemain.setText(String.format("%02d", (totalTime-position)/60) + ":" + String.format("%02d", (totalTime-position)%60));
		sldTime.setValue((int)(1000*position/totalTime));
	}
	
	/**
	 * 
	 * @param totalTime total time of the song in seconds
	 */
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	
	public void resetPlayButton() {
		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
	}

	/**
	 *  This method initializes the GUI elements
	 */
	public void initUI() {
		//setTitle("Yamp");
        setSize(600, 500);
        setResizable(false);
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
	        	fc.setMultiSelectionEnabled(false);
	        	int returnVal = fc.showOpenDialog(YampUI.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                selectedFile = fc.getSelectedFile();
	                //This is where a real application would open the file.
//	                System.out.println("Opening: " + selectedFile.getPath() + ".");
	                try {
						Mp3File mp3file = new Mp3File(selectedFile.getPath());
						if (mp3file.hasId3v2Tag()) {
							// Get id3v2 tags
		        			ID3v2 id3v2Tag = mp3file.getId3v2Tag();
		        			// Display title artist and album info in dashboard labels 
		        			lblTitle.setText("Title: " + id3v2Tag.getTitle());
		        			lblArtist.setText("Artist: " + id3v2Tag.getArtist());
		        			lblAlbum.setText("Album: " + id3v2Tag.getAlbum());
		        			// Resize and display cover art
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
	                // open the file in basicplayer
	                driver.open(selectedFile.getPath());
	                // Append to playlist
	                System.out.println("Appended to playlist: " + selectedFile.getPath() + ".");
    				YampPlaylistElement pelement = new YampPlaylistElement(selectedFile);
        			playlist.appendElement(pelement);
    				String[] rowdata = {String.format("%02d",playlist.size()), pelement.getID3v2Tag().getTitle(), pelement.getID3v2Tag().getArtist(), pelement.getID3v2Tag().getAlbum()};
        			tablemodel.addRow(rowdata);
	                // Change play pause button icon
	                btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
	            } else {
//	                System.out.println("Open command cancelled by user.");
	            }
	          }
	        });
		mnFile.add(mntmOpen);
		
		mntmOpenLyrics = new JMenuItem("Open Lyrics");
		mntmOpenLyrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setMultiSelectionEnabled(false);
				int returnVal = fc.showOpenDialog(YampUI.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                selectedFile = fc.getSelectedFile();
	                String filename = selectedFile.getName();
	                // Get the extension of the file
	                String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
	                lyricswindow = new YampLyricsWindow();
	                lyricswindow.displayLyrics(selectedFile, extension);
	                lyricswindow.setVisible(true);
	            } 
	            else {
//	                System.out.println("Open command cancelled by user.");
	            }
			}
		});
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
            		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
            		mntmPause.setText("Resume");
            	}
            	if (driver.getState() == BasicPlayerEvent.PAUSED) {
            		driver.resume();
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
            	btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
            	mntmPause.setText("Pause");
            	lblDisplay.setText("00:00");
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
		mntmAddToPlaylist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		// Allow multiple files to be selected
        		fc.setMultiSelectionEnabled(true);
        		int returnVal = fc.showOpenDialog(YampUI.this);
        		if (returnVal == JFileChooser.APPROVE_OPTION) {
        			File[] selectedFiles = fc.getSelectedFiles();
        			// For each file append to playlist and jtable model
        			for (File file : selectedFiles) {
        				System.out.println("Appended to playlist: " + file.getPath() + ".");
        				YampPlaylistElement pelement = new YampPlaylistElement(file);
	        			playlist.appendElement(pelement);
        				String[] rowdata = {String.format("%02d",playlist.size()), pelement.getID3v2Tag().getTitle(), pelement.getID3v2Tag().getArtist(), pelement.getID3v2Tag().getAlbum()};
	        			tablemodel.addRow(rowdata);
	        			
        			}
        		} else {
//        			System.out.println("Open command cancelled by user.");
        		}
            }
          });
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
        lblTitle.setBounds(140, 40, 450, 25);
        add(lblTitle);
        
        // Setup artist label
        lblArtist = new JLabel("Artist: ");
        lblArtist.setBounds(140, 65, 450, 25);
        add(lblArtist);
        
        // Setup album label
        lblAlbum = new JLabel("Album: ");
        lblAlbum.setBounds(140, 90, 450, 25);
        add(lblAlbum);
        
        // Setup time display label
        lblDisplay = new JLabel("00:00");
        lblDisplay.setBounds(140, 10, 100, 30);
        lblDisplay.setFont(new Font("Dialog", Font.BOLD, 30));
        add(lblDisplay);
        
        // Setup the time remaining label
        lblRemain = new JLabel("00:00");
        lblRemain.setBounds(490, 10, 100, 30);
        lblRemain.setFont(new Font("Dialog", Font.BOLD, 30));
        add(lblRemain);
        
        // Setup Play button
        btnPlay = new JButton();
        btnPlay.setBounds(50, 140, 60, 60);
        btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (driver.getState() == BasicPlayerEvent.PLAYING | driver.getState() == BasicPlayerEvent.RESUMED) {
            		driver.pause();
            		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
            		mntmPause.setText("Resume");
            	} else if (driver.getState() == BasicPlayerEvent.PAUSED) {
            		driver.resume();
            		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/pause.png"));
            		mntmPause.setText("Pause");
            	} else if (driver.getState() == BasicPlayerEvent.STOPPED | driver.getState() == BasicPlayerEvent.OPENED) {
            		driver.play();
            		btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/pause.png"));
            		mntmPause.setText("Pause");
            	}
            }
          });
        add(btnPlay);
        
        // Setup FF button
        btnForward = new JButton();
        btnForward.setBounds(110, 150, 40, 40);
        btnForward.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/forward.png"));
//        btnForward.addActionListener(new ActionListener() {
//        	public void actionPerformed(ActionEvent e) {
//        		driver.forward(5);
//        	}
//        });
        Timer forwardTimer = new Timer(100, new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		driver.forward(1);
        	}
        });
        btnForward.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		driver.forward(3);
        	}
            @Override
            public void mousePressed(MouseEvent e) {
            	forwardTimer.start();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            	forwardTimer.stop();
            }
        });

        add(btnForward);
        
        // Setup RW button
        btnRewind = new JButton();
        btnRewind.setBounds(10, 150, 40, 40);
        btnRewind.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/rewind.png"));
        Timer rewindTimer = new Timer(100, new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		driver.rewind(10);
        	}
        });
        btnRewind.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		driver.rewind(5);
        	}
            @Override
            public void mousePressed(MouseEvent e) {
            	rewindTimer.start();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            	rewindTimer.stop();
            }
        });
        add(btnRewind);
        
        // Setup stop button
        btnStop = new JButton();
        btnStop.setBounds(170, 150, 40, 40);
        btnStop.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/stop.png"));
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	driver.stop();
            	btnPlay.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play.png"));
            	mntmPause.setText("Pause");
            	lblDisplay.setText("00:00");
            }
        });
        add(btnStop);
        
        // Setup mute button
        btnMute = new JButton();
        btnMute.setBounds(240, 150, 40, 40);
        btnMute.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/volume.png"));
        add(btnMute);
        
        // Setup Cover Art label
        lblCoverArt = new JLabel();
        lblCoverArt.setBounds(10, 10, COVER_SIZE, COVER_SIZE);
		lblCoverArt.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/placeholder120.png"));
        add(lblCoverArt);
        
        // Setup time slider
        sldTime = new JSlider();
        sldTime = new JSlider(0, 1000, 0);
        sldTime.setBounds(140, 110, 450, 25);
        sldTime.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
//                driver.jump(sldTime.getValue());
            }
        });
        add(sldTime);
        
        // Setup volume label
        lblVolume = new JLabel("Volume: 85");
        lblVolume.setBounds(305, 160, 85, 25);
        add(lblVolume);
        
        // Setup volume slider
        sldVolume = new JSlider();
        sldVolume = new JSlider(0, 100, 85);
        sldVolume.setBounds(390, 160, 200, 25);
        sldVolume.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                lblVolume.setText("Volume: " + sldVolume.getValue());
                driver.setVolume(sldVolume.getValue());
            }
        });
        add(sldVolume);
        
        // Setup playlist table and model
        tablemodel = new DefaultTableModel() {
        	// disable editing of cells by double click
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        table = new JTable(tablemodel);
        table.setDragEnabled(true);
//        table.setDropMode(DropMode.INSERT_ROWS);
//        table.setTransferHandler(new TableRowTransferHandler(table));
        String[] columnNames = {"No", "Title", "Artist", "Album"};
        tablemodel.setColumnIdentifiers(columnNames);
        // Set width of each column 
        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        
        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        scrollPane.setBounds(10, 210, 570, 200);
        add(scrollPane);
        
        // Setup Append Button
        btnAppend = new JButton("+");
        btnAppend.setBounds(10, 420, 50, 25);
        btnAppend.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		// Allow multiple files to be selected
        		fc.setMultiSelectionEnabled(true);
        		int returnVal = fc.showOpenDialog(YampUI.this);
        		if (returnVal == JFileChooser.APPROVE_OPTION) {
        			File[] selectedFiles = fc.getSelectedFiles();
        			// For each file append to playlist and listmodel
        			for (File file : selectedFiles) {
//        				System.out.println("Appended to playlist: " + file.getPath() + ".");
        				YampPlaylistElement pelement = new YampPlaylistElement(file);
	        			playlist.appendElement(pelement);
        				String[] rowdata = {String.format("%02d",playlist.size()), pelement.getID3v2Tag().getTitle(), pelement.getID3v2Tag().getArtist(), pelement.getID3v2Tag().getAlbum()};
	        			tablemodel.addRow(rowdata);
        			}
        		} else {
//        			System.out.println("Open command cancelled by user.");
        		}
        	}
        });
        add(btnAppend);
        
        //Setup Remove Button
        btnRemove = new JButton("-");
        btnRemove.setBounds(60, 420, 50, 25);
        btnRemove.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		// Get user-selected rows from JTable
        		int[] selectedRows = table.getSelectedRows();
        		// Remove each row
        		int numberRemoved = 0;
        		for (int i : selectedRows) {
//        			System.out.println("Removed from playlist: " + playlist.get(i).getMp3File().getFilename() + ".");
        			playlist.remove(i - numberRemoved);
        			tablemodel.removeRow(i - numberRemoved);
        			numberRemoved++;     	
        		}
        		// update indices
        		for (int j = 0; j < playlist.size(); j++) {
        				tablemodel.setValueAt(String.format("%02d", j+1), j, 0);
        		}
        	}
        });
        add(btnRemove);
	}


}
