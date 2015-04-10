import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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
	// Lyrics window
	private YampLyricsWindow lyricswindow;

	// Total time of current song
	private int totalTime;
	// Volume state
	private boolean isMuted = false;
	// Volume
	private int volumeLevel = 85;


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
	private JLabel lblCoverArt;
	private JSlider sldVolume;
	private JSlider sldTime;
	private JProgressBar prbTime;

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
		prbTime.setValue((int)(1000*position/totalTime));
		//		prbTime.setString(String.format("%02d", position/60) + ":" + String.format("%02d", position%60)
		//						+ "                                                " 
		//						+ String.format("%02d", (totalTime-position)/60) + ":" + String.format("%02d", (totalTime-position)%60));
		prbTime.setString(String.format("%-90s", String.format("%02d", position/60) + ":" + String.format("%02d", position%60)) 
				+ String.format("%02d", totalTime/60) + ":" + String.format("%02d", totalTime%60));

	}

	/**
	 * 
	 * @param totalTime total time of the song in seconds
	 */
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
		//		prbTime.setString("00:00" + "                                                " 
		//				+ String.format("%02d", totalTime/60) + ":" + String.format("%02d", totalTime%60));
		prbTime.setString(String.format("%-90s", "00:00") + String.format("%02d", totalTime/60) + ":" + String.format("%02d", totalTime%60));

	}

	public void resetPlayButton() {
		btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
		//		btnPlay.setIcon(new ImageIcon(getClass().getResource("/png/play.png")));
		prbTime.setValue(0);
		prbTime.setString("00:00" + "                                                " + "00:00");
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
					} catch (UnsupportedTagException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InvalidDataException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (IOException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					// open the file in basicplayer
					driver.open(selectedFile.getPath());
					// Append to playlist
					YampPlaylistElement pelement = new YampPlaylistElement(selectedFile);
					playlist.appendElement(pelement);
					String[] rowdata = {String.format("%02d",playlist.size()), pelement.getID3v2Tag().getTitle(), pelement.getID3v2Tag().getArtist(), pelement.getID3v2Tag().getAlbum()};
					tablemodel.addRow(rowdata);
					// Play the song
					driver.play();
					// Change play pause button icon and menu
					//	                btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
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
				btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
				mntmPause.setText("Pause");
			}
		});
		mnControl.add(mntmPlay);

		mntmPause = new JMenuItem("Pause");
		mntmPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (driver.getState() == BasicPlayerEvent.PLAYING | driver.getState() == BasicPlayerEvent.RESUMED) {
					driver.pause();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
					mntmPause.setText("Resume");
				}
				if (driver.getState() == BasicPlayerEvent.PAUSED) {
					driver.resume();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				}
			}
		});
		mnControl.add(mntmPause);

		mntmStop = new JMenuItem("Stop");
		mntmStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				driver.stop();
				btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
				mntmPause.setText("Pause");
				prbTime.setValue(0);
				//        		prbTime.setString("00:00" + "                                                " + "00:00");
				prbTime.setString(String.format("%-90s", "00:00") + "00:00");
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
		mntmRemoveSelections.addActionListener(new ActionListener() {
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
		mnPlaylist.add(mntmRemoveSelections);

		mntmClearPlaylist = new JMenuItem("Clear Playlist");
		mntmClearPlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playlist.debugPrint();
				playlist.clear();
				for (int i = tablemodel.getRowCount()-1; i >= 0; i--) {
					tablemodel.removeRow(i);
				}
				playlist.debugPrint();
			}

		});
		mnPlaylist.add(mntmClearPlaylist);

		mntmSavePlaylist = new JMenuItem("Save Playlist");
		mnPlaylist.add(mntmSavePlaylist);

		mntmLoadPlaylist = new JMenuItem("Load Playlist");
		mnPlaylist.add(mntmLoadPlaylist);		

		// Setup title label
		lblTitle = new JLabel("Title: ");
		lblTitle.setBounds(140, 10, 450, 25);
		add(lblTitle);

		// Setup artist label
		lblArtist = new JLabel("Artist: ");
		lblArtist.setBounds(140, 35, 450, 25);
		add(lblArtist);

		// Setup album label
		lblAlbum = new JLabel("Album: ");
		lblAlbum.setBounds(140, 60, 450, 25);
		add(lblAlbum);

		// Setup Cover Art label
		lblCoverArt = new JLabel();
		lblCoverArt.setBounds(10, 10, COVER_SIZE, COVER_SIZE);
		lblCoverArt.setIcon(new ImageIcon(getClass().getResource("/res/placeholder120.png")));
		add(lblCoverArt);

		// Setup time progress bar
		prbTime = new JProgressBar(0, 1000);
		prbTime.setBounds(140, 100, 440, 25);
		prbTime.setValue(0);
		prbTime.setStringPainted(true);
		//        prbTime.setString("00:00" + "                                                " + "00:00");
		prbTime.setString(String.format("%-90s", "00:00") + "00:00");
		prbTime.addMouseListener(new MouseAdapter() {            
			public void mouseClicked(MouseEvent e) {
				int v = prbTime.getValue();
				//Retrieves the mouse position relative to the component origin.
				int mouseX = e.getX();
				//Computes how far along the mouse is relative to the component width then multiply it by the progress bar's maximum value.
				int progressBarVal = (int)Math.round(((double)mouseX / (double)prbTime.getWidth()) * prbTime.getMaximum());
				prbTime.setValue(progressBarVal);
				driver.jump((int)(driver.getDuration() * (progressBarVal / 1000.0)));
			}                                     
		});
		add(prbTime);

		// Setup Play button
		btnPlay = new JButton();
		btnPlay.setBounds(50, 140, 60, 60);
		btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (driver.getState() == BasicPlayerEvent.PLAYING | driver.getState() == BasicPlayerEvent.RESUMED) {
					driver.pause();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
					mntmPause.setText("Resume");
				} else if (driver.getState() == BasicPlayerEvent.PAUSED) {
					driver.resume();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				} else if (driver.getState() == BasicPlayerEvent.STOPPED | driver.getState() == BasicPlayerEvent.OPENED) {
					driver.play();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				}
			}
		});
		add(btnPlay);

		// Setup FF button
		btnForward = new JButton();
		btnForward.setBounds(110, 150, 40, 40);
		btnForward.setIcon(new ImageIcon(getClass().getResource("/res/forward.png")));
		add(btnForward);

		// Setup RW button
		btnRewind = new JButton();
		btnRewind.setBounds(10, 150, 40, 40);
		btnRewind.setIcon(new ImageIcon(getClass().getResource("/res/rewind.png")));
		add(btnRewind);

		// Setup stop button
		btnStop = new JButton();
		btnStop.setBounds(170, 150, 40, 40);
		btnStop.setIcon(new ImageIcon(getClass().getResource("/res/stop.png")));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				driver.stop();
				btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
				mntmPause.setText("Pause");
				prbTime.setValue(0);
				//        		prbTime.setString("00:00" + "                                                " + "00:00");
				prbTime.setString(String.format("%-90s", "00:00") + "00:00");
			}
		});
		add(btnStop);

		// Setup mute button
		btnMute = new JButton();
		btnMute.setBounds(240, 150, 40, 40);
		btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isMuted) {
					driver.setVolume(volumeLevel);
					sldVolume.setValue(volumeLevel);
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					isMuted = false;
				} else {
					volumeLevel = sldVolume.getValue();
					driver.setVolume(0);
					sldVolume.setValue(0);
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/mute.png")));
					isMuted = true;
				}
			}
		});
		add(btnMute);


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
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
			        int row = table.rowAtPoint(e.getPoint());
			        if (row != -1) {
			        	Mp3File mp3file = playlist.get(row).getMp3File();
			        	driver.open(mp3file.getFilename());
			        	ID3v2 id3v2Tag = mp3file.getId3v2Tag();
						// Display title artist and album info in dashboard labels 
						lblTitle.setText("Title: " + id3v2Tag.getTitle());
						lblArtist.setText("Artist: " + id3v2Tag.getArtist());
						lblAlbum.setText("Album: " + id3v2Tag.getAlbum());
						// Resize and display cover art
						BufferedImage coverart = null;
						try {
							coverart = ImageIO.read(new ByteArrayInputStream(id3v2Tag.getAlbumImage()));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						BufferedImage resizedimage = new BufferedImage(COVER_SIZE, COVER_SIZE, BufferedImage.TYPE_INT_RGB);
						Graphics g = resizedimage.createGraphics();
						g.drawImage(coverart, 0, 0, COVER_SIZE, COVER_SIZE, null);
						g.dispose();
						lblCoverArt.setIcon(new ImageIcon(resizedimage));
			        	driver.play();
			        	btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
						mntmPause.setText("Pause");
			        }
			        
				}
			}
		});
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
