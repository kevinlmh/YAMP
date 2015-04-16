import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class YampLyricsWindow extends JFrame {
	private JTextArea textarea;
	private JScrollPane scrollpane;
	
	public YampLyricsWindow() {
		initUI();
	}

	public void initUI() {
		setTitle("Lyrics");
		setSize(300,600);
		setLocationRelativeTo(null);
		textarea = new JTextArea();
        textarea.setEditable(false);
        scrollpane = new JScrollPane(textarea);  
        add(scrollpane);
	}
	
	public void displayLyrics(File file, String ext) {
		textarea.setText(getLyrics(file, ext));
	}
	
	public String getLyrics(File lyricsFile, String ext){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(lyricsFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String aLineFromFile = null;
        String lyricsAll = null;
        int count = 0;
        try {
			while ((aLineFromFile = br.readLine()) != null){
					if (ext.equals("lrc")){
						aLineFromFile = removelrc(aLineFromFile, count);	
					}
			       if (count == 0)
			    	   lyricsAll = aLineFromFile;
			       else
			    	   lyricsAll = lyricsAll + aLineFromFile;
			       lyricsAll = lyricsAll + "\n";
			       count ++;
			        
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
        
        try {
			br.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return lyricsAll;
	}
	
	
	public String removelrc(String line, int x) {
		String toReturn = " ";
		int start = line.indexOf('[');
		if (start == 0){
			char[] arr = line.toCharArray();
			int in = line.indexOf(']');
			for (int i = in+1; i<arr.length;i++){
				toReturn = toReturn + arr[i];
			}
		}
		return toReturn;
	}
}
