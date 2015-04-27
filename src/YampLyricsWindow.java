import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class YampLyricsWindow extends JFrame {
	// GUI comonents
	private JTextPane textpane;
	private JScrollPane scrollpane;
	// Data structure to hold lyrics
	private HashMap<Integer, String> hashmap;
	// Format of lyrics file
	private int format = FORMAT_UNKNOWN;
	// Constants
	public static final int FORMAT_UNKNOWN = -1;
	public static final int FORMAT_TXT = 0;
	public static final int FORMAT_LRC = 1;
	
	/**
	 * Constructor
	 */
	public YampLyricsWindow() {
		hashmap = new HashMap<Integer, String>();
		initUI();
	}

	/**
	 * Initializer GUI
	 */
	public void initUI() {
		setTitle("Lyrics");
		setSize(300,600);
		setLocationRelativeTo(null);
		textpane = new JTextPane();
        textpane.setEditable(false);
        scrollpane = new JScrollPane(textpane);  
        add(scrollpane); 
	}
	
	/**
	 * Get the format of lyrics file
	 * 
	 * @return a constant that represents the format of lyrics file
	 */
	public int getFormat() {
		return format;
	}
	
	/**
	 * Get the hashmap that stores time and lyrics text
	 * 
	 * @return a hashmap with time as keys and lyrics as values
	 */
	public HashMap<Integer, String> getHashMap() {
		return hashmap;
	}
	
	/**
	 * Dispaly lyrics in lyrics window
	 * 
	 * @param file	the lyrics file to display
	 * @param ext	extension of file
	 */
	public void displayLyrics(File file, String ext) {
		textpane.setText(getLyrics(file, ext));
	}
	
	/**
	 * Display the lyrics at time
	 * 
	 * @param time	time of the current song in seconds
	 */
	public void synchonizeLRC(int time) {
		textpane.setText(hashmap.get(time));
	}
	
	/**
	 * Get lyrics as a huge string
	 * 
	 * @param file	lyrics file
	 * @param ext	extension of lyrics file
	 * @return	a huge string that contains the lyrics
	 */
	public String getLyrics(File file, String ext) {
		if (ext.equals("lrc") || ext.equals("LRC")) {
			format = FORMAT_LRC;
			return processLRC(file);
		} else if (ext.equals("txt") || ext.equals("lrc")) {
			format = FORMAT_TXT;
			return processTXT(file);
		} else {
			format = FORMAT_UNKNOWN;
			return null;
		}
	}
	
	/**
	 * Parge TXT files
	 * 
	 * @param file TXT file to parse
	 * @return	a string that contains lyrics
	 */
	public String processTXT(File file) {
		String lyrics = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null){
				lyrics += (line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lyrics;
	}
	
	/**
	 * Parge LRC files and store time-text pair in hashmap
	 * 
	 * @param file	LRC file to parse
	 * @return	a string that contains lyrics
	 */
	public String processLRC(File file) {
		String lyrics = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				// Read lrc header
				String header = line.substring(line.indexOf('['), line.indexOf(']')+1);
				// If the character after [ is a digit
				if (Character.isDigit(header.charAt(header.indexOf('[')+1))) {
					// Get the lyrics text
					String text = line.substring(line.indexOf(']')+1, line.length());
					int minute, second;
					// If the time stamp is [00:00.00]
					if (header.length() >= 10) {
						minute = Integer.parseInt( header.substring(line.indexOf('[')+1, line.indexOf(':')) );
						second = Integer.parseInt( header.substring(line.indexOf(":")+1, line.indexOf(".")) );
					} else {
						// The time stamp is in [00:00] format
						minute = Integer.parseInt( header.substring(line.indexOf('[')+1, line.indexOf(':')) );
						second = Integer.parseInt( header.substring(line.indexOf(":")+1, line.indexOf("]")) );
					}
					lyrics += (text + "\n");
					int time = minute * 60 + second;
					// Use time as key and text as value, add to hashmap
					hashmap.put(time, text + '\n');
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lyrics;
	}
	
	
}
