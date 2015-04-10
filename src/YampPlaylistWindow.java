import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
	private JFileChooser fc;
	
	// Reference to driver
	private YampMain driver;
	
	public YampPlaylistWindow(YampMain driver) {
		super();
		initUI();
		this.driver = driver;
		this.fc = new JFileChooser();
		this.playlist = new ArrayList<YampPlaylistElement>();
	}
	
	public void appendToPlaylist() {
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
			//        			System.out.println("Open command cancelled by user.");
		}
	}
	
	public void removeSelections() {
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
	
	public void clearPlaylist() {
		for (int i = playlist.size()-1; i >= 0; i--) {
			playlist.remove(i);
		}
		for (int i = tablemodel.getRowCount()-1; i >= 0; i--) {
			tablemodel.removeRow(i);
		}
	}
	
	public void initUI() {
		setTitle("Playlist");
		setSize(600, 400);
	    setUndecorated(true);
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
			        }
			        
				}
			}
		});
		add(scrollPane);

		// Setup Append Button
		btnAppend = new JButton("+");
		btnAppend.setBounds(10, 365, 50, 25);
		btnAppend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				appendToPlaylist();
			}
		});
		add(btnAppend);

		//Setup Remove Button
		btnRemove = new JButton("-");
		btnRemove.setBounds(60, 365, 50, 25);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelections();
			}
		});
		add(btnRemove);
	}
}
