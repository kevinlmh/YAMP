import java.io.File;
import java.io.IOException;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;


public class YampPlaylistElement {
//	private int index;
	private File musicfile;
	private Mp3File mp3file;
	private ID3v2 id3v2tag;
	
	public YampPlaylistElement(File file) {
		this.musicfile = file;
		try {
			this.mp3file = new Mp3File(file);
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
		this.id3v2tag = mp3file.getId3v2Tag();
	}
	
	public File getFile() {
		return musicfile;
	}
	
	public Mp3File getMp3File() {
		return mp3file;
	}
	
	public ID3v2 getID3v2Tag() {
		return id3v2tag;
	}
	
//	public int getIndex() {
//		return index;
//	}
//	
//	public void setIndex(int index) {
//		this.index = index;
//	}
	

}
