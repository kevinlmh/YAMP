/**
 *  Copyright (C) 2015 YAMP Team

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
    USA
 */

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.sound.sampled.SourceDataLine;
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
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.mpatric.mp3agic.ID3v1;
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
	/* Modules */
	// BasicPlayer module
	private BasicPlayer player = null;
	// BasicController module
	private BasicController control = null;
	// Mini Mode Window
	private JFrame miniUI;
	// File chooser
	private JFileChooser fc;
	// Info Window
	private YampInfoWindow infowindow;
	// Playlist window
	private YampPlaylistWindow playlistwindow;
	// Lyrics window
	private YampLyricsWindow lyricswindow;
	// Visualizer
	private SpectrumTimeAnalyzer visualizer;
	// Visualizer window
	private JFrame visualizerwindow;
	// Equalizer
	private YampEqualizer equalizer;
	// Euqalizer window
	private JFrame equalizerwindow;
	
	
	/* Information of current file */
	// The current mp3 file
	private Mp3File currentMp3File = null;
	// audioInfo of current file
	private Map audioInfo;
	// Current state of the music player
	private int currentState;
	// Duration of current song
	private int duration;
	// Length in bytes of current song
	private int totalBytes;
	// Bytes per second
	private int bytesPerSecond;
	// Start of mp3 header
	private int headerPosition;
	// Current volume level
	private int volumeLevel = 50;
	// Is the player muted
	private boolean isMuted = false;

	/* UI components of the main window */
	/* Swing components declaration */
	private JButton btnPlay;
	private JButton btnNext;
	private JButton btnPrevious;
	private JButton btnStop;
	private JButton btnMute;
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
	private JMenuItem mntmMiniMode;
	private JMenu mnHelp;
	private JMenuItem mntmWebsite;
	private JMenuItem mntmAbout;
	
	/* Mini Mode Window components */
	private JButton btnMiniPlay;
	private JButton btnMiniNext;
	private JButton btnMiniPrevious;
	private JButton btnMiniStop;
	private JButton btnMiniMute;
	private JButton btnFull;
	private JLabel lblMiniInfo;
	private JLabel lblMiniTime;
	private JLabel lblMiniVolume;
	private JSlider sldMiniVolume;

	/* Constructor */
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
		// Initialize visualizer
		visualizer = new SpectrumTimeAnalyzer();
		// Initialize equalizer
		equalizer  = new YampEqualizer();
		// Initialize main window UI
		initUI();
	}

	@Override
	public void opened(Object stream, Map properties) {
//		System.out.println("opened : " + properties.toString());
		// Save audo file properties
		audioInfo = properties;
		duration = (int) ((Long) (properties.get("duration")) / 1000000);
		totalBytes = (Integer) properties.get("mp3.length.bytes");
		bytesPerSecond = (int) ((Integer) properties.get("mp3.framesize.bytes") * (Float) properties
				.get("mp3.framerate.fps"));
		headerPosition = (Integer) properties.get("mp3.header.pos");
	}

	@Override
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
//		System.out.println("progress : " + properties.toString());
		// Update progress bar and time display
		long position = (Long)properties.get("mp3.position.microseconds") / 1000000;
		prbTime.setValue((int)(1000 * position / duration ));
		prbTime.setString(String.format("%-90s", String.format("%02d", position/60) + ":" + String.format("%02d", position%60)) 
				+ String.format("%02d", (duration-position)/60) + ":" + String.format("%02d", (duration-position)%60));
		lblMiniTime.setText(String.format("%02d", position/60) + ":" + String.format("%02d", position%60) + "/-"
				+ String.format("%02d", (duration-position)/60) + ":" + String.format("%02d", (duration-position)%60));
		// Write DSP data array to visualizer
		visualizer.writeDSP(pcmdata);
		// Send bands to Equalizer
		if (properties.containsKey("mp3.equalizer"))
			equalizer.setBands((float[]) properties.get("mp3.equalizer"));
		// Lyrics synchonization
		if (lyricswindow.getFormat() == YampLyricsWindow.FORMAT_LRC) {
			if (lyricswindow.getHashMap().get((int)position) != null) {
				lyricswindow.synchonizeLRC((int)position);
			}
		}
		
	}

	@Override
	public void setController(BasicController controller) {
//		System.out.println("setController : " + controller);
	}

	@Override
	public void stateUpdated(BasicPlayerEvent event) {
//		System.out.println("stateUpdated : " + event.toString());
		// Ignore state changes caused by chaning gain, pan, opening and seeking file
		if (event.getCode() != BasicPlayerEvent.GAIN
				| event.getCode() != BasicPlayerEvent.PAN
				| event.getCode() != BasicPlayerEvent.OPENING
				| event.getCode() != BasicPlayerEvent.SEEKING
				| event.getCode() != BasicPlayerEvent.UNKNOWN) {
			currentState = event.getCode();
		}
		// If end of music file is reached
		if (event.getCode() == BasicPlayerEvent.EOM) {
			// Change icons and texts of related GUI elements
			btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
			btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
			prbTime.setValue(0);
			prbTime.setString(String.format("%-90s", "00:00") + "00:00");
			lblMiniTime.setText("00:00/-00:00");
			// Load next song
			loadOnDeck(playlistwindow.next());
		} else if (event.getCode() == BasicPlayerEvent.PLAYING) {
			// Start visualizer
			visualizer.setupDSP((SourceDataLine) audioInfo.get("basicplayer.sourcedataline"));
            visualizer.startDSP((SourceDataLine) audioInfo.get("basicplayer.sourcedataline"));
		} else if (event.getCode() == BasicPlayerEvent.STOPPED) {
			// Stop visualizer
			visualizer.stopDSP();
            visualizer.repaint();
		}
	}

	/**
	 * Open a music file
	 * 
	 * @param file MP3 file to open
	 */
	public void open(File file) {
		try {
			control.open(file);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Play the opened file
	 */
	public void play() {
		try {
			control.play();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop the opened file
	 */
	public void stop() {
		try {
			control.stop();
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Pause the music file
	 */
	public void pause() {
		try {
			control.pause();
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Resume music playback
	 */
	public void resume() {
		try {
			control.resume();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Jump to a specific time of the music file
	 * 
	 * @param seconds time to jump to
	 */
	public void seek(int seconds) {
		try {
			control.seek(headerPosition + bytesPerSecond * seconds);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Set volume (0 to 100)
	 * @param percentage a value between 0 and 100
	 */
	public void setVolume(int percentage) {
		try {
			control.setGain((double) percentage / 100.0);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Display file information in file info window
	 */
	public void displayInfo() {
		if (currentMp3File != null) {
			infowindow = new YampInfoWindow(currentMp3File.getFilename());
			infowindow.setVisible(true);
		}
	}
	
	/**
	 * Load a song, display its info album artwork in dashboard and start playback
	 * 
	 * @param file MP3 file to load
	 */
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
				lblMiniInfo.setText(id3v2Tag.getArtist() + " - " + id3v2Tag.getTitle());
				// Resize and display cover art
				BufferedImage coverart = null;
				if (id3v2Tag.getAlbumImage() != null) {
					coverart = ImageIO.read(new ByteArrayInputStream(id3v2Tag.getAlbumImage()));
					BufferedImage resizedimage = new BufferedImage(120, 120, BufferedImage.TYPE_INT_RGB);
					Graphics g = resizedimage.createGraphics();
					g.drawImage(coverart, 0, 0, 120, 120, null);
					g.dispose();
					lblCoverArt.setIcon(new ImageIcon(resizedimage));
				} else {
					// Display placeholder cover art
					lblCoverArt.setIcon(new ImageIcon(getClass().getResource("res/placeholder120.png")));
				}
			} else if (currentMp3File.hasId3v1Tag()) {
				ID3v1 id3v1Tag = currentMp3File.getId3v1Tag();
				// Display title artist and album info in Dashboard labels
				lblTitle.setText("Title: " + id3v1Tag.getTitle());
				lblArtist.setText("Artist: " + id3v1Tag.getArtist());
				lblAlbum.setText("Album: " + id3v1Tag.getAlbum());
				lblMiniInfo.setText(id3v1Tag.getArtist() + " - " + id3v1Tag.getTitle());
				// Display placeholder cover art
				lblCoverArt.setIcon(new ImageIcon(getClass().getResource("res/placeholder120.png")));
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
		// Change the duration label
		prbTime.setString(String.format("%-90s", "00:00")
						+ String.format("%02d", duration/60) + ":" + String.format("%02d", duration%60));
		lblMiniTime.setText("00:00/-" + String.format("%02d", duration/60) + ":" + String.format("%02d", duration%60));
		// Play the song
		play();
		// Change play pause button icon and menu
		btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
		btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
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

		
		// Setup playlist window
		playlistwindow = new YampPlaylistWindow(YampMain.this);
		playlistwindow.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            mntmPlaylist.setText("Show Playlist");
	        }
		});
		
		// Setup visualizer window
		visualizerwindow = new JFrame("Visualizer");
		visualizerwindow.setContentPane(visualizer);
		visualizerwindow.setSize(400,200);
		visualizerwindow.setResizable(false);
		visualizerwindow.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            mntmVisualizer.setText("Show Visualizer");
	        }
	    });
	
		// Setup equalizer window
		equalizerwindow = new JFrame("Equalizer");
		equalizerwindow.setContentPane(equalizer);
		equalizerwindow.setSize(400,240);
		equalizerwindow.setResizable(false);
		equalizerwindow.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent event) {
	            mntmEqualizer.setText("Show Equalizer");
	        }
	    });
		
		// Setup lyrics window
		lyricswindow = new YampLyricsWindow();
		lyricswindow.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent event) {
	            mntmLyrics.setText("Show Lyrics");
	        }
	    });
		

		// Setup menu bar
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		// Setup File menu
		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc = new JFileChooser();
				fc.setFileFilter(new FileNameExtensionFilter("MP3 File", "mp3"));
				// when open button is clicked open a file chooser dialog
				fc.setMultiSelectionEnabled(false);
				int returnVal = fc.showOpenDialog(YampMain.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					playlistwindow.appendFile(fc.getSelectedFile());
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
				fc = new JFileChooser();
				fc.setFileFilter(new FileNameExtensionFilter("LRC File", "lrc"));
				fc.addChoosableFileFilter(new FileNameExtensionFilter("Text File","txt"));
				fc.setMultiSelectionEnabled(false);
				int returnVal = fc.showOpenDialog(YampMain.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					String filename = selectedFile.getName();
					// Get the extension of the file
					String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
					lyricswindow.displayLyrics(selectedFile, extension);
					lyricswindow.setVisible(true);
					mntmLyrics.setText("Hide Lyrics");
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

		// Setup Control menu
		mnControl = new JMenu("Control");
		menuBar.add(mnControl);

		mntmPlay = new JMenuItem("Play");
		mntmPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				play();
				btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
				btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
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
					btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
					mntmPause.setText("Resume");
				} else if (currentState == BasicPlayerEvent.PAUSED) {
					resume();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
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
				btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
				mntmPause.setText("Pause");
				prbTime.setValue(0);
				// Change the duration label
				prbTime.setString(String.format("%-90s", "00:00")
								+ String.format("%02d", duration/60) + ":" + String.format("%02d", duration%60));
				lblMiniTime.setText("00:00/-" + String.format("%02d", duration/60) + ":" + String.format("%02d", duration%60));
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
		
		// Setup Volume menu
		mnVolume = new JMenu("Volume");
		menuBar.add(mnVolume);
		
		mntmIncreaseVolume = new JMenuItem("Increase Volume");
		mntmIncreaseVolume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int newVolume = sldVolume.getValue() + 10;
				newVolume = (newVolume > 100)? 100: newVolume;
				setVolume(newVolume);
				sldVolume.setValue(newVolume);
				sldMiniVolume.setValue(newVolume);
				lblVolume.setText(Integer.toString(newVolume));
				lblMiniVolume.setText(Integer.toString(newVolume));
				if (newVolume == 0) {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/mute.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/mute.png")));
				} else {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/volume.png")));
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
				sldMiniVolume.setValue(newVolume);
				lblVolume.setText(Integer.toString(newVolume));
				lblMiniVolume.setText(Integer.toString(newVolume));
				if (newVolume == 0) {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/mute.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/mute.png")));
				} else {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/volume.png")));
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
					sldMiniVolume.setValue(volumeLevel);
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/volume.png")));
					mntmMute.setText("Mute");
					isMuted = false;
				} else {
					volumeLevel = sldVolume.getValue();
					setVolume(0);
					sldVolume.setValue(0);
					btnMute.setIcon(new	ImageIcon(getClass().getResource("res/mute.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/mute.png")));
					mntmMute.setText("Unmute");
					isMuted = true;
				}
			}
		});
		mnVolume.add(mntmMute);
		
		
		// Setup Play Mode menu
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

		// Setup Playlist menu
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
		
		// Setup View menu
		mnView = new JMenu("View");
		menuBar.add(mnView);
		
		mntmPlaylist = new JMenuItem("Show Playlist");
		mntmPlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (playlistwindow.isVisible()) {
					playlistwindow.setVisible(false);
					mntmPlaylist.setText("Show Playlist");
				} else {
					playlistwindow.setLocation(YampMain.this.getX(), YampMain.this.getY()+260);
					playlistwindow.setVisible(true);
					mntmPlaylist.setText("Hide Playlist");
				}

			}
		});
		mnView.add(mntmPlaylist);
		
		mntmVisualizer = new JMenuItem("Show Visualizer");
		mntmVisualizer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (visualizerwindow.isVisible()) {
					visualizerwindow.setVisible(false);
					mntmVisualizer.setText("Show Visualizer");
				} else {
					visualizerwindow.setLocation(YampMain.this.getX()-400, YampMain.this.getY());
					visualizerwindow.setVisible(true);
					mntmVisualizer.setText("Hide Visualizer");
				}
			}
		});
		mnView.add(mntmVisualizer);
		
		mntmEqualizer = new JMenuItem("Show Equalizer");
		mntmEqualizer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (equalizerwindow.isVisible()) {
					equalizerwindow.setVisible(false);
					mntmEqualizer.setText("Show Equalizer");
				} else {
					equalizerwindow.setLocation(YampMain.this.getX()-400, YampMain.this.getY()+200);
					equalizerwindow.setVisible(true);
					mntmEqualizer.setText("Hide Equalizer");
				}
			}
		});
		mnView.add(mntmEqualizer);
		
		mntmLyrics = new JMenuItem("Show Lyrics");
		mntmLyrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lyricswindow.isVisible()) {
					lyricswindow.setVisible(false);
					mntmLyrics.setText("Show Lyrics");
				} else {
					lyricswindow.setLocation(YampMain.this.getX()+600, YampMain.this.getY());
					lyricswindow.setVisible(true);
					mntmLyrics.setText("Hide Lyrics");
				}
			}
		});
		mnView.add(mntmLyrics);
		
		mntmMiniMode = new JMenuItem("Mini Mode");
		mntmMiniMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				YampMain.this.setVisible(false);
				miniUI.setVisible(true);
			}
		});
		mnView.add(mntmMiniMode);
		
		// Setup Help menu
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
					btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
					mntmPause.setText("Resume");
				} else if (currentState == BasicPlayerEvent.PAUSED) {
					resume();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				} else if (currentState == BasicPlayerEvent.STOPPED | currentState == BasicPlayerEvent.OPENED) {
					play();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				}
			}
		});
		add(btnPlay);

		// Setup next button
		btnNext = new JButton();
		btnNext.setBounds(110, 150, 40, 40);
		btnNext.setIcon(new ImageIcon(getClass().getResource("/res/forward.png")));
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadOnDeck(playlistwindow.next());
			}
		});
		add(btnNext);

		// Setup previous button
		btnPrevious = new JButton();
		btnPrevious.setBounds(10, 150, 40, 40);
		btnPrevious.setIcon(new ImageIcon(getClass().getResource("/res/rewind.png")));
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
				btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
				btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
				mntmPause.setText("Pause");
				prbTime.setValue(0);
				// Change the duration label
				prbTime.setString(String.format("%-90s", "00:00")
								+ String.format("%02d", duration/60) + ":" + String.format("%02d", duration%60));
				lblMiniTime.setText("00:00/-" + String.format("%02d", duration/60) + ":" + String.format("%02d", duration%60));
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
					sldMiniVolume.setValue(volumeLevel);
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/volume.png")));
					mntmMute.setText("Mute");
					isMuted = false;
				} else {
					volumeLevel = sldVolume.getValue();
					setVolume(0);
					sldVolume.setValue(0);
					sldMiniVolume.setValue(0);
					btnMute.setIcon(new	ImageIcon(getClass().getResource("res/mute.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/mute.png")));
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
				lblMiniVolume.setText(Integer.toString(sldVolume.getValue()));
				sldMiniVolume.setValue(sldVolume.getValue());
				setVolume(sldVolume.getValue());
				if (sldVolume.getValue() == 0) {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/mute.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/mute.png")));
				} else {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/volume.png")));
				}
			}
		});
		add(sldVolume);

		/* Setup Mini Mode Window */
		miniUI = new JFrame("YAMP Mini");
		miniUI.setSize(600, 85);
		miniUI.setResizable(false);
		miniUI.setLayout(null);
		miniUI.setDefaultCloseOperation(EXIT_ON_CLOSE);
		miniUI.setLocationRelativeTo(null);
		
		btnMiniPlay = new JButton(new ImageIcon(getClass().getResource("res/play.png")));
		btnMiniPlay.setBounds(50, 10, 40, 40);
		btnMiniPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentState == BasicPlayerEvent.PLAYING | currentState	== BasicPlayerEvent.RESUMED) {
					pause();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
					btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
					mntmPause.setText("Resume");
				} else if (currentState == BasicPlayerEvent.PAUSED) {
					resume();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				} else if (currentState == BasicPlayerEvent.STOPPED | currentState == BasicPlayerEvent.OPENED) {
					play();
					btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/pause.png")));
					mntmPause.setText("Pause");
				}
			}
		});
		miniUI.add(btnMiniPlay);
		

		// Setup next button on mini window
		btnMiniNext = new JButton(new ImageIcon(getClass().getResource("/res/forward.png")));
		btnMiniNext.setBounds(90, 10, 40, 40);
		btnMiniNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadOnDeck(playlistwindow.next());
			}
		});
		miniUI.add(btnMiniNext);

		// Setup previous button on mini window
		btnMiniPrevious = new JButton(new ImageIcon(getClass().getResource("/res/rewind.png")));
		btnMiniPrevious.setBounds(10, 10, 40, 40);
		btnMiniPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadOnDeck(playlistwindow.previous());
			}
		});
		miniUI.add(btnMiniPrevious);

		// Setup stop button on mini mode window
		btnMiniStop = new JButton(new ImageIcon(getClass().getResource("/res/stop.png")));
		btnMiniStop.setBounds(130, 10, 40, 40);
		btnMiniStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
				btnPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
				btnMiniPlay.setIcon(new ImageIcon(getClass().getResource("/res/play.png")));
				mntmPause.setText("Pause");
				prbTime.setValue(0);
				// Change the duration label
				prbTime.setString(String.format("%-90s", "00:00")
								+ String.format("%02d", duration/60) + ":" + String.format("%02d", duration%60));
				lblMiniTime.setText("00:00/-" + String.format("%02d", duration/60) + ":" + String.format("%02d", duration%60));
			}
		});
		miniUI.add(btnMiniStop);

		// Setup mute button on mini mode window
		btnMiniMute = new JButton(new ImageIcon(getClass().getResource("res/volume.png")));
		btnMiniMute.setBounds(170, 10, 40, 40);
		btnMiniMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isMuted) {
					setVolume(volumeLevel);
					sldVolume.setValue(volumeLevel);
					sldMiniVolume.setValue(volumeLevel);
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/volume.png")));
					mntmMute.setText("Mute");
					isMuted = false;
				} else {
					volumeLevel = sldVolume.getValue();
					setVolume(0);
					sldVolume.setValue(0);
					btnMute.setIcon(new	ImageIcon(getClass().getResource("res/mute.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/mute.png")));
					mntmMute.setText("Unmute");
					isMuted = true;
				}
			}
		});
		miniUI.add(btnMiniMute);

		// Setup volume label on mini mode window
		lblMiniVolume = new JLabel("50");
		lblMiniVolume.setBounds(220, 35, 25, 25);
		miniUI.add(lblMiniVolume);

		// Setup volume slider on mini mode window
		sldMiniVolume = new JSlider();
		sldMiniVolume = new JSlider(0, 100, 50);
		sldMiniVolume.setBounds(250, 35, 100, 25);
		sldMiniVolume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				lblMiniVolume.setText(Integer.toString(sldMiniVolume.getValue()));
				lblVolume.setText(Integer.toString(sldMiniVolume.getValue()));
				sldVolume.setValue(sldMiniVolume.getValue());
				setVolume(sldMiniVolume.getValue());
				if (sldVolume.getValue() == 0) {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/mute.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/mute.png")));
				} else {
					btnMute.setIcon(new ImageIcon(getClass().getResource("res/volume.png")));
					btnMiniMute.setIcon(new ImageIcon(getClass().getResource("/res/volume.png")));
				}
			}
		});
		miniUI.add(sldMiniVolume);
		
		// Setup info label on mini mode window
		lblMiniInfo = new JLabel();
		lblMiniInfo.setBounds(220, 10, 320, 25);
		miniUI.add(lblMiniInfo);
		
		// Setup time label on mini mode window
		lblMiniTime = new JLabel("00:00/-00:00");
		lblMiniTime.setBounds(380, 35, 100, 25);
		miniUI.add(lblMiniTime);
		
		//Setup return to full mode button on mini mode window
		btnFull = new JButton(new ImageIcon(getClass().getResource("res/expand.png")));
		btnFull.setBounds(540, 10, 40, 40);
		btnFull.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				miniUI.setVisible(false);
				YampMain.this.setVisible(true);
			}
		});
		miniUI.add(btnFull);
		
	}

	public static void main(String[] args) {
		YampMain test = new YampMain();
		test.setVisible(true);
	}

}
