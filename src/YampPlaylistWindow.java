import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;


public class YampPlaylistWindow extends JFrame {
	private ArrayList<YampPlaylistElement> playlist;
	
	/* Playlist components */
	private JTable table;
	private DefaultTableModel tablemodel;
	private JScrollPane scrollPane;
	private JButton btnAppend;
	private JButton btnRemove;
	private JButton btnUp;
	private JButton btnDown;
	private JFileChooser fc;
	private int mode = REPEATE_PLAYLIST;
	private int currentSongIndex;
	
	// Reference to driver
	private YampMain driver;
	
	// constant definition
	public static final int REPEATE_PLAYLIST = 0;
	public static final int REPEATE_ONE = 1;
	public static final int SHUFFLE = 2;
	
	public YampPlaylistWindow(YampMain driver) {
		super();
		initUI();
		this.driver = driver;
		this.fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("M3U File", "m3u"));
		this.playlist = new ArrayList<YampPlaylistElement>();
	}
	
	public void append() {
		// Allow multiple files to be selected
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(YampPlaylistWindow.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = fc.getSelectedFiles();
			// For each file append to playlist and listmodel
			for (File file : selectedFiles) {
				YampPlaylistElement pelement = new YampPlaylistElement(file);
				playlist.add(pelement);
				String[] rowdata = {String.format("%02d",playlist.size()), pelement.getID3v2Tag().getTitle(), pelement.getID3v2Tag().getArtist(), pelement.getID3v2Tag().getAlbum()};
				tablemodel.addRow(rowdata);
			}
		} else {
			// System.out.println("Open command cancelled by user.");
		}
	}
	
	public void appendFile(File file) {
		YampPlaylistElement pelement = new YampPlaylistElement(file);
		playlist.add(pelement);
		String[] rowdata = {String.format("%02d",playlist.size()), pelement.getID3v2Tag().getTitle(), pelement.getID3v2Tag().getArtist(), pelement.getID3v2Tag().getAlbum()};
		tablemodel.addRow(rowdata);
	}
	
	public void remove() {
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
	
	public void clear() {
		for (int i = playlist.size()-1; i >= 0; i--) {
			playlist.remove(i);
		}
		for (int i = tablemodel.getRowCount()-1; i >= 0; i--) {
			tablemodel.removeRow(i);
		}
	}

	public void save() {
		fc.setDialogTitle("Save Playlist");
		int returnVal = fc.showSaveDialog(YampPlaylistWindow.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = new File(fc.getSelectedFile() + ".m3u");
			try {
				FileWriter fw = new FileWriter(file.getPath());
				fw.write("#EXTM3U\n");
				int i;
				for (i = 0; i < playlist.size(); i++) {
					fw.write("#EXTINF:" + playlist.get(i).getMp3File().getLengthInSeconds() 
							+ "," + playlist.get(i).getID3v2Tag().getArtist() + " - " 
							+ playlist.get(i).getID3v2Tag().getTitle() + "\n");
					fw.write(playlist.get(i).getFile().getPath() + "\n");
				}
				fw.flush();
				fw.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	public void load() {
		// Open File Chooser dialog for loading a playlist
		fc.setDialogTitle("Load Playlist");
		fc.setMultiSelectionEnabled(false);
		int returnVal = fc.showOpenDialog(YampPlaylistWindow.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fc.getSelectedFile();
			try {
                BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                String line = null;
                clear();
                while ((line = br.readLine()) != null) {
                    if (line.trim().length() == 0) {
                        continue;
                    }
                    //SONG INFO FROM M3U FILE
                    if (line.startsWith("#")) {
//                        if (line.toUpperCase().startsWith("#EXTINF")) {
//                            int index = line.indexOf(",", 0);
//                            if (index != -1) {
//                                title = line.substring(index + 1, line.length());
//                            }
//                        }
                    } //SONG FILE FROM M3U FILE
                    else {
                        String path = line;
                        YampPlaylistElement pelement = new YampPlaylistElement(new File(path));
                        playlist.add(pelement);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
			// Load file to listModel
			for (int i=0; i < playlist.size(); i++) {
				String[] rowdata ={String.valueOf(i+1), playlist.get(i).getID3v2Tag().getTitle(), playlist.get(i).getID3v2Tag().getArtist(), playlist.get(i).getID3v2Tag().getAlbum()};
    			tablemodel.addRow(rowdata);
			}
		} else {
			System.out.println("Open command cancelled by user.");
		}
	}
	
	public File previous() {
		if (mode == REPEATE_PLAYLIST) {
			currentSongIndex = (currentSongIndex - 1 < 0)? 0 : currentSongIndex - 1;
		} else if (mode == SHUFFLE) {
			currentSongIndex = (int)(Math.random() * playlist.size());
		}
		table.setRowSelectionInterval(currentSongIndex, currentSongIndex);
		return playlist.get(currentSongIndex).getFile();
	}
	
	public File next() {
		if (mode == REPEATE_PLAYLIST) {
			currentSongIndex = (currentSongIndex + 1) % playlist.size();
		} else if (mode == SHUFFLE) {
			currentSongIndex = (int)(Math.random() * playlist.size());
		}
		table.setRowSelectionInterval(currentSongIndex, currentSongIndex);
		return playlist.get(currentSongIndex).getFile();
	}
	
	public void moveUp() {
	    int[] selectedRows = table.getSelectedRows();
	    for (int row : selectedRows) {
	    	if (row != 0) {
	    		tablemodel.moveRow(row, row, row-1);
	    		table.removeRowSelectionInterval(row, row);
	    		table.addRowSelectionInterval(row-1, row-1);
	    	}
	    }
	}
	
	public void moveDown() {
	    int[] selectedRows = table.getSelectedRows();
	    for (int i = selectedRows.length-1; i >=0; i--) {
	    	int row = selectedRows[i];
	    	if (row != tablemodel.getRowCount()-1) {
	    		tablemodel.moveRow(row, row, row+1);
	    		table.removeRowSelectionInterval(row, row);
	    		table.addRowSelectionInterval(row+1, row+1);
	    	}
	    }	
	}
	
	public void setCurrentSongIndex(int index) {
		currentSongIndex = index;
	}
	
	public void setPlayMode(int playmode) {
		mode = playmode;
	}
		
	public void debugPrint() {
		System.out.println("**Playlist debug print**");
		for (int i = 0; i < playlist.size(); i++) {
			System.out.print(i+1 + "; ");
			System.out.println(playlist.get(i).getMp3File().getFilename());
		}
	}
	
	public void initUI() {
		setTitle("Playlist");
		setSize(600, 420);
//	    setUndecorated(true);
	    setLayout(null);
	    
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
		scrollPane.setBounds(10, 10, 580, 350);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					int row = table.rowAtPoint(e.getPoint());
					if (row != -1) {
						driver.loadOnDeck(playlist.get(row).getFile());
						setCurrentSongIndex(row);
					}

				}
			}
		});
		new FileDrop(table, new FileDrop.Listener() {
			@Override
			public void filesDropped(File[] files) {
				for (File file : files) {
					String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
					if (extension.equals("mp3")) {
						YampPlaylistElement pelement = new YampPlaylistElement(file);
						playlist.add(pelement);
						String[] rowdata = {String.format("%02d",playlist.size()), pelement.getID3v2Tag().getTitle(), pelement.getID3v2Tag().getArtist(), pelement.getID3v2Tag().getAlbum()};
						tablemodel.addRow(rowdata);
					}
					if (extension.equals("m3u")) {
						try {
			                BufferedReader br = new BufferedReader(new FileReader(file));
			                String line = null;
			                clear();
			                while ((line = br.readLine()) != null) {
			                    if (line.trim().length() == 0) {
			                        continue;
			                    }
			                    //SONG INFO FROM M3U FILE
			                    if (line.startsWith("#")) {
//			                        if (line.toUpperCase().startsWith("#EXTINF")) {
//			                            int index = line.indexOf(",", 0);
//			                            if (index != -1) {
//			                                title = line.substring(index + 1, line.length());
//			                            }
//			                        }
			                    } //SONG FILE FROM M3U FILE
			                    else {
			                        String path = line;
			                        YampPlaylistElement pelement = new YampPlaylistElement(new File(path));
			                        playlist.add(pelement);
			                    }
			                }
			            } catch (IOException e1) {
			                e1.printStackTrace();
			            }
						// Load file to listModel
						for (int i=0; i < playlist.size(); i++) {
							String[] rowdata ={String.valueOf(i+1), playlist.get(i).getID3v2Tag().getTitle(), playlist.get(i).getID3v2Tag().getArtist(), playlist.get(i).getID3v2Tag().getAlbum()};
			    			tablemodel.addRow(rowdata);
						}
					}
					
				}
			}		
		});
		add(scrollPane);

		// Setup Append Button
		btnAppend = new JButton();
		btnAppend.setIcon(new ImageIcon(getClass().getResource("res/add.png")));
		btnAppend.setBounds(10, 365, 50, 25);
		btnAppend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				append();
			}
		});
		add(btnAppend);

		//Setup Remove Button
		btnRemove = new JButton();
		btnRemove.setIcon(new ImageIcon(getClass().getResource("res/minus.png")));
		btnRemove.setBounds(60, 365, 50, 25);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove();
			}
		});
		add(btnRemove);
		
		// Setup up button
		btnUp = new JButton();
		btnUp.setIcon(new ImageIcon(getClass().getResource("res/up.png")));
		btnUp.setBounds(480, 365, 50, 25 );
		btnUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveUp();
			}
		});
		add(btnUp);
		
		// Setup down button
		btnDown = new JButton();
		btnDown.setIcon(new ImageIcon(getClass().getResource("res/down.png")));
		btnDown.setBounds(530, 365, 50, 25);
		btnDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveDown();
			}
		});
		add(btnDown);
	}
}
