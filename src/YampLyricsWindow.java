import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class YampLyricsWindow extends JFrame {
	private JTextPane textpane;
	private JScrollPane scrollpane;
	private HashMap<Integer, String> hashmap;
	private int format = FORMAT_UNKNOWN;
	
	public static final int FORMAT_UNKNOWN = -1;
	public static final int FORMAT_TXT = 0;
	public static final int FORMAT_LRC = 1;
	
	public YampLyricsWindow() {
		hashmap = new HashMap<Integer, String>();
		initUI();
	}

	public void initUI() {
		setTitle("Lyrics");
		setSize(300,600);
		setLocationRelativeTo(null);
		textpane = new JTextPane();
        textpane.setEditable(false);
        scrollpane = new JScrollPane(textpane);  
        add(scrollpane); 
	}
	
	public int getFormat() {
		return format;
	}
	
	public HashMap<Integer, String> getHashMap() {
		return hashmap;
	}
	public void displayLyrics(File file, String ext) {
		textpane.setText(getLyrics(file, ext));
	}
	
	public void synchonizeLRC(int time) {
		textpane.setText(hashmap.get(time));
	}
	
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
				String header = line.substring(line.indexOf('['), line.indexOf(']'));
				if ( !Character.isDigit(header.charAt(1)) ) {
					continue;
				}
				String text = line.substring(line.indexOf(']')+1, line.length());
				int minute = Integer.parseInt( header.substring(line.indexOf('[')+1, line.indexOf(':')) );
				int second = Integer.parseInt( header.substring(line.indexOf(":")+1, line.indexOf(".")) );
				lyrics += (text + "\n");
				int time = minute * 60 + second;
//				System.out.println(minute + " " + second + " " + time);
				hashmap.put(time, text + '\n');
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
