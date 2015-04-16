import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class YampMain extends JFrame implements BasicPlayerListener {
	/* All the modules */
	// BasicPlayer module
	private BasicPlayer player = null;
	// BasicController module
	private BasicController control = null;
	// Info Window
	private YampInfoWindow infowindow;
	// Playlist window
	private YampPlaylistWindow playlistwindow;
	// File chooser
	private JFileChooser fc;
	// Lyrics window
	private YampLyricsWindow lyricswindow;
	
	
	/* Fields of main window or player related stuff */
	// The current mp3 file
	private Mp3File currentMp3File = null;
	private int currentState;
	private int duration;
	private int totalBytes;
	private int bytesPerSecond;
	private int headerPosition;
	private int volumeLevel = 50;
	private boolean isMuted = false;

	/* UI components of the main window */
	/* Swing components declaration */
	private JButton btnPlay;
	private JButton btnNext;
	private JButton btnPrevious;
	private JButton btnStop;
	private JButton btnMute;
	private JToggleButton btnTogglePlaylist;
	private JToggleButton btnToggleLyrics;
	private JLabel lblTitle;
	private JLabel lblArtist;
	private JLabel lblAlbum;
	private JLabel lblVolume;
	private JLabel lblCoverArt;
	private JSlider sldVolume;
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
	private JMenu mnVolume;
	private JMenuItem mntmIncreaseVolume;
	private JMenuItem mntmDecreaseVolume;
	private JMenuItem mntmMute;
	private JMenu mnMode;
	private JCheckBoxMenuItem mntmRepeatPlaylist;
	private JCheckBoxMenuItem mntmRepeatOne;
	private JCheckBoxMenuItem mntmShuffle;
	private JMenu mnPlaylist;
	private JMenuItem mntmAddToPlaylist;
	private JMenuItem mntmRemoveSelections;
	private JMenuItem mntmClearPlaylist;
	private JMenuItem mntmSavePlaylist;
	private JMenuItem mntmLoadPlaylist;
	private JMenu mnView;
	private JMenuItem mntmPlaylist;
	private JMenuItem mntmVisualizer;
	private JMenuItem mntmEqualizer;
	private JMenuItem mntmLyrics;
	private JMenu mnHelp;
	private JMenuItem mntmWebsite;
	private JMenuItem mntmAbout;
	
	
	public YampMain() {
		// Instantiate BasicPlayer.
		player = new BasicPlayer();
		// BasicPlayer is a BasicController.
		control = (BasicController) player;
		player.addBasicPlayerListener(this);
		try {
			control.setGain(0.5);
			control.setPan(0.0);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Initialize main window
		initUI();
		// Initialize playlist window
		playlistwindow = new YampPlaylistWindow(YampMain.this);
		
	}

	@Override
	public void opened(Object stream, Map properties) {
		// TODO Auto-generated method stub
		System.out.println("opened : " + properties.toString());
		duration = (int) ((Long) (properties.get("duration")) / 1000000);
		totalBytes = (Integer) properties.get("mp3.length.bytes");
		bytesPerSecond = (int) ((Integer) properties.get("mp3.framesize.bytes") * (Float) properties
				.get("mp3.framerate.fps"));
		headerPosition = (Integer) properties.get("mp3.header.pos");
	}

	@Override
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
		// TODO Auto-generated method stub
//		System.out.println("progress : " + properties.toString());
		// Update progress bar and time display
		long position = (Long)properties.get("mp3.position.microseconds") / 1000000;
		prbTime.setValue((int)(1000 * position / duration ));
		prbTime.setString(String.format("%-90s", String.format("%02d", position/60) + ":" + String.format("%02d", position%60)) 
				+ String.format("%02d", (duration-position)/60) + ":" + String.format("%02d", (duration-position)%60));

	}

	@Override
	public void setController(BasicController controller) {
		// TODO Auto-generated method stub
		System.out.println("setController : " + controller);

	}

	@Override
	public void stateUpdated(BasicPlayerEvent event) {
		// TODO Auto-generated method stub
		System.out.println("stateUpdated : " + event.toString());
		if (event.getCode() != BasicPlayerEvent.GAIN
				| event.getCode() != BasicPlayerEvent.PAN
				| event.getCode() != BasicPlayerEvent.OPENING
				| event.getCode() != BasicPlayerEvent.SEEKING
				| event.getCode() != BasicPlayerEvent.UNKNOWN) {
			currentState = event.getCode();
		}
		if (event.getCode() == BasicPlayerEvent.EOM) {
			btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
			//		btnPlay.setIcon(new ImageIcon(getClass().getResource("/png/play.png")));
			prbTime.setValue(0);
			prbTime.setString(String.format("%-90s", "00:00") + "00:00");
			loadOnDeck(playlistwindow.next());
		}
	}

	public void open(File file) {
		try {
			control.open(file);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void play() {
		try {
			control.play();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			control.stop();
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pause() {
		try {
			control.pause();
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resume() {
		try {
			control.resume();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void seek(int seconds) {
		try {
			control.seek(headerPosition + bytesPerSecond * seconds);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setVolume(int percentage) {
		try {
			control.setGain((double) percentage / 100.0);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void displayInfo() {
		if (currentMp3File != null) {
			infowindow = new YampInfoWindow(currentMp3File.getFilename());
			infowindow.setVisible(true);
		}
	}
	
	public void loadOnDeck(File file) {
		try {
			currentMp3File = new Mp3File(file.getPath());
			if (currentMp3File.hasId3v2Tag()) {
				// Get id3v2 tags
				ID3v2 id3v2Tag = currentMp3File.getId3v2Tag();
				// Display title artist and album info in Dashboard labels
				lblTitle.setText("Title: " + id3v2Tag.getTitle());
				lblArtist.setText("Artist: " + id3v2Tag.getArtist());
				lblAlbum.setText("Album: " + id3v2Tag.getAlbum());
				// Resize and display cover art
				BufferedImage coverart = null;
				coverart = ImageIO.read(new
						ByteArrayInputStream(id3v2Tag.getAlbumImage()));
				BufferedImage resizedimage = new BufferedImage(120, 120, BufferedImage.TYPE_INT_RGB);
				Graphics g = resizedimage.createGraphics();
				g.drawImage(coverart, 0, 0, 120, 120, null);
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
		open(file);
		// Append to playlist
//		YampPlaylistElement pelement = new YampPlaylistElement(selectedFile);
//		playlist.appendElement(pelement);
//		String[] rowdata = {String.format("%02d",playlist.size()),
//				pelement.getID3v2Tag().getTitle(),
//				pelement.getID3v2Tag().getArtist(),
//				pelement.getID3v2Tag().getAlbum()};
//		tablemodel.addRow(rowdata);
		// Play the song
		play();
		// Change play pause button icon and menu
		btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
		mntmPause.setText("Pause");
	}
	
	/**
	 * This method initializes the GUI elements
	 */
	public void initUI() {
		setTitle("Yamp Mp3 Player");
		setSize(600, 260);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// optional: this line set the window to the center of screen
		setLocationRelativeTo(null);
		// No layout manager: absolute position
		setLayout(null);
		// Set icon
		ImageIcon icon = new ImageIcon(getClass().getResource("/res/icon.png"));
		setIconImage(icon.getImage());

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
				int returnVal = fc.showOpenDialog(YampMain.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					loadOnDeck(fc.getSelectedFile());
				} else {
					// System.out.println("Open command cancelled by user.");
				}
			}
		});
		mnFile.add(mntmOpen);

		mntmOpenLyrics = new JMenuItem("Open Lyrics");
		mntmOpenLyrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc_temp = new JFileChooser();
				fc_temp.addChoosableFileFilter(new FileNameExtensionFilter("LRC File","lrc"));
				fc_temp.addChoosableFileFilter(new FileNameExtensionFilter("Text File","txt"));
				fc_temp.setMultiSelectionEnabled(false);
				int returnVal = fc_temp.showOpenDialog(YampMain.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc_temp.getSelectedFile();
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
				displayInfo();
			}
		});
		mnFile.add(mntmFileInfo);

		mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmQuit);

		mnControl = new JMenu("Control");
		menuBar.add(mnControl);

		mntmPlay = new JMenuItem("Play");
		mntmPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				play();
				btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
				mntmPause.setText("Pause");
			}
		});
		mnControl.add(mntmPlay);

		mntmPause = new JMenuItem("Pause");
		mntmPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentState == BasicPlayerEvent.PLAYING | currentState == BasicPlayerEvent.RESUMED) {
					pause();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
					mntmPause.setText("Resume");
				}
				if (currentState == BasicPlayerEvent.PAUSED) {
					resume();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				}
			}
		});
		mnControl.add(mntmPause);

		mntmStop = new JMenuItem("Stop");
		mntmStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
				btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
				mntmPause.setText("Pause");
				prbTime.setValue(0);
				prbTime.setString(String.format("%-90s", "00:00") + "00:00");
			}
		});
		mnControl.add(mntmStop);

		mntmPrevious = new JMenuItem("Previous");
		mntmPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadOnDeck(playlistwindow.previous());
			}
		});
		mnControl.add(mntmPrevious);

		mntmNext = new JMenuItem("Next");
		mntmNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadOnDeck(playlistwindow.next());
			}
		});
		mnControl.add(mntmNext);
		
		mnVolume = new JMenu("Volume");
		menuBar.add(mnVolume);
		
		mntmIncreaseVolume = new JMenuItem("Increase Volume");
		mntmIncreaseVolume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int newVolume = sldVolume.getValue() + 10;
				newVolume = (newVolume > 100)? 100: newVolume;
				setVolume(newVolume);
				sldVolume.setValue(newVolume);
				lblVolume.setText(Integer.toString(newVolume));
				if (newVolume == 0) {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/mute.png")));
				} else {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
				}
			}
		});
		mnVolume.add(mntmIncreaseVolume);
		
		mntmDecreaseVolume = new JMenuItem("Decrease Volume");
		mntmDecreaseVolume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int newVolume = sldVolume.getValue() - 10;
				newVolume = (newVolume < 0)? 0: newVolume;
				setVolume(newVolume);
				sldVolume.setValue(newVolume);
				lblVolume.setText(Integer.toString(newVolume));
				if (newVolume == 0) {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/mute.png")));
				} else {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
				}
			}
		});
		mnVolume.add(mntmDecreaseVolume);
		
		mntmMute = new JMenuItem("Mute");
		mntmMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isMuted) {
					setVolume(volumeLevel);
					sldVolume.setValue(volumeLevel);
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					mntmMute.setText("Mute");
					isMuted = false;
				} else {
					volumeLevel = sldVolume.getValue();
					setVolume(0);
					sldVolume.setValue(0);
					btnMute.setIcon(new	ImageIcon(getClass().getResource("res/mute.png")));
					mntmMute.setText("Unmute");
					isMuted = true;
				}
			}
		});
		mnVolume.add(mntmMute);
		
		mnMode = new JMenu("Play Mode");
		menuBar.add(mnMode);
		
		mntmRepeatPlaylist = new JCheckBoxMenuItem("Repeate Playlist");
		mntmRepeatPlaylist.setState(true);
		mntmRepeatPlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mntmRepeatPlaylist.isSelected()) {
					playlistwindow.setPlayMode(YampPlaylistWindow.REPEATE_PLAYLIST);
					mntmRepeatOne.setState(false);
					mntmShuffle.setState(false);
				}
				
			}
		});
		mnMode.add(mntmRepeatPlaylist);		
		
		mntmRepeatOne = new JCheckBoxMenuItem("Repeate One");
		mntmRepeatOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mntmRepeatOne.isSelected()) {
					playlistwindow.setPlayMode(YampPlaylistWindow.REPEATE_ONE);
					mntmRepeatPlaylist.setState(false);
					mntmShuffle.setState(false);
				}
			}
		});
		mnMode.add(mntmRepeatOne);	
		
		mntmShuffle = new JCheckBoxMenuItem("Shuffle");
		mntmShuffle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mntmShuffle.isSelected()) {
					playlistwindow.setPlayMode(YampPlaylistWindow.SHUFFLE);
					mntmRepeatPlaylist.setState(false);
					mntmRepeatOne.setState(false);
				}
			}
		});
		mnMode.add(mntmShuffle);	

		mnPlaylist = new JMenu("Playlist");
		menuBar.add(mnPlaylist);

		mntmAddToPlaylist = new JMenuItem("Add to Playlist");
		mntmAddToPlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playlistwindow.append();
			}
		});
		mnPlaylist.add(mntmAddToPlaylist);

		mntmRemoveSelections = new JMenuItem("Remove Selections");
		mntmRemoveSelections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playlistwindow.remove();
			}
		});
		mnPlaylist.add(mntmRemoveSelections);

		mntmClearPlaylist = new JMenuItem("Clear Playlist");
		mntmClearPlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playlistwindow.clear();
			}

		});
		mnPlaylist.add(mntmClearPlaylist);

		mntmSavePlaylist = new JMenuItem("Save Playlist");
		mntmSavePlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playlistwindow.save();
			}
		});
		mnPlaylist.add(mntmSavePlaylist);

		mntmLoadPlaylist = new JMenuItem("Load Playlist");
		mntmLoadPlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playlistwindow.load();
			}
		});
		mnPlaylist.add(mntmLoadPlaylist);
		
		mnView = new JMenu("View");
		menuBar.add(mnView);
		
		mntmPlaylist = new JMenuItem("Show Playlist");
		mntmPlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (playlistwindow.isVisible()) {
					playlistwindow.setVisible(false);
					mntmPlaylist.setText("Show Playlist");
				} else {
					playlistwindow.setLocation(YampMain.this.getX(), YampMain.this.getY()+250);
					playlistwindow.setVisible(true);
					mntmPlaylist.setText("Hide Playlist");
				}

			}
		});
		mnView.add(mntmPlaylist);
		
		mntmVisualizer = new JMenuItem("Show Visualizer");
		mntmVisualizer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		mnView.add(mntmVisualizer);
		
		mntmEqualizer = new JMenuItem("Show Equalizer");
		mntmEqualizer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		mnView.add(mntmEqualizer);
		
		mntmLyrics = new JMenuItem("Show Lyrics");
		mntmLyrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		mnView.add(mntmLyrics);
		
		mnHelp= new JMenu("Help");
		menuBar.add(mnHelp);
		
		mntmWebsite = new JMenuItem("Yamp Website");
		mntmWebsite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			            desktop.browse(new URI("http://kevinlmh.github.io/YAMP/"));
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}
		});
		mnHelp.add(mntmWebsite);
		
		mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				YampAboutWindow aboutwindow = new YampAboutWindow();
				aboutwindow.setVisible(true);
			}
		});
		mnHelp.add(mntmAbout);
		
		

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
		lblCoverArt.setBounds(10, 10, 120, 120);
		lblCoverArt.setIcon(new ImageIcon(getClass().getResource("/res/placeholder120.png")));
		add(lblCoverArt);

		// Setup time progress bar
		prbTime = new JProgressBar(0, 1000);
		prbTime.setBounds(140, 100, 440, 25);
		prbTime.setValue(0);
		prbTime.setStringPainted(true);
		prbTime.setString(String.format("%-90s", "00:00") + "00:00");
		prbTime.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int v = prbTime.getValue();
				// Retrieves the mouse position relative to the component origin.
				int mouseX = e.getX();
				// Computes how far along the mouse is relative to the component width
				// then multiply it by the progress bar's maximum value.
				int progressBarVal = (int)Math.round(((double)mouseX / (double)prbTime.getWidth()) * prbTime.getMaximum());
				prbTime.setValue(progressBarVal);
				seek((int)(duration * (progressBarVal / 1000.0)));
			}
		});
		add(prbTime);

		// Setup Play button
		btnPlay = new JButton();
		btnPlay.setBounds(50, 140, 60, 60);
		btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentState == BasicPlayerEvent.PLAYING | currentState	== BasicPlayerEvent.RESUMED) {
					pause();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
					mntmPause.setText("Resume");
				} else if (currentState == BasicPlayerEvent.PAUSED) {
					resume();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				} else if (currentState == BasicPlayerEvent.STOPPED | currentState == BasicPlayerEvent.OPENED) {
					play();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				}
			}
		});
		add(btnPlay);

		// Setup next button
		btnNext = new JButton();
		btnNext.setBounds(110, 150, 40, 40);
		btnNext.setIcon(new ImageIcon(getClass().getResource(
				"/res/forward.png")));
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadOnDeck(playlistwindow.next());
			}
		});
		add(btnNext);

		// Setup previous button
		btnPrevious = new JButton();
		btnPrevious.setBounds(10, 150, 40, 40);
		btnPrevious.setIcon(new ImageIcon(getClass().getResource(
				"/res/rewind.png")));
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadOnDeck(playlistwindow.previous());
			}
		});
		add(btnPrevious);

		// Setup stop button
		btnStop = new JButton();
		btnStop.setBounds(160, 150, 40, 40);
		btnStop.setIcon(new ImageIcon(getClass().getResource("/res/stop.png")));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
				btnPlay.setIcon(new
						ImageIcon(getClass().getResource("/res/play.png")));
				mntmPause.setText("Pause");
				prbTime.setValue(0);
				prbTime.setString(String.format("%-90s", "00:00") + "00:00");
			}
		});
		add(btnStop);

		// Setup mute button
		btnMute = new JButton();
		btnMute.setBounds(420, 150, 40, 40);
		btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isMuted) {
					setVolume(volumeLevel);
					sldVolume.setValue(volumeLevel);
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					mntmMute.setText("Mute");
					isMuted = false;
				} else {
					volumeLevel = sldVolume.getValue();
					setVolume(0);
					sldVolume.setValue(0);
					btnMute.setIcon(new	ImageIcon(getClass().getResource("res/mute.png")));
					mntmMute.setText("Unmute");
					isMuted = true;
				}
			}
		});
		add(btnMute);

		// Setup volume label
		lblVolume = new JLabel("50");
		lblVolume.setBounds(465, 160, 25, 25);
		add(lblVolume);

		// Setup volume slider
		sldVolume = new JSlider();
		sldVolume = new JSlider(0, 100, 50);
		sldVolume.setBounds(490, 160, 100, 25);
		sldVolume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				lblVolume.setText(Integer.toString(sldVolume.getValue()));
				setVolume(sldVolume.getValue());
				if (sldVolume.getValue() == 0) {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/mute.png")));
				} else {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
				}
			}
		});
		add(sldVolume);
		
//		// Setup playlist toggle button
//		btnTogglePlaylist = new JToggleButton("P");
//		btnTogglePlaylist.setBounds(400, 150, 60, 40);
//		btnTogglePlaylist.addItemListener(new ItemListener() {
//			@Override
//			public void itemStateChanged(ItemEvent arg0) {
//				if (btnTogglePlaylist.isSelected()) {
//					playlistwindow.setLocation(YampMain.this.getX(), YampMain.this.getY()+250);
//					playlistwindow.setVisible(true);
//				} else {
//					playlistwindow.setVisible(false);
//				}
//				
//			}
//
//		});
//		add(btnTogglePlaylist);
		

	}

	public static void main(String[] args) {
		YampMain test = new YampMain();
		test.setVisible(true);
	}

}
