import java.util.ArrayList;


public class YampPlaylist {
	private ArrayList<YampPlaylistElement> playlist;
	
	public YampPlaylist() {
		playlist = new ArrayList<YampPlaylistElement>();
	}
	
	public void appendElement(YampPlaylistElement element) {
		playlist.add(element);
	}
	
	public void add(int index, YampPlaylistElement element) {
		playlist.add(index, element);
	}
}
