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
	
	public void remove(int index) {
		playlist.remove(index);
	}
	
	public YampPlaylistElement get(int index) {
		return playlist.get(index);
	}
	
	public int size() {
		return playlist.size();
	}
	
	public void clear() {
		for (int i = playlist.size()-1; i >= 0; i--) {
			playlist.remove(i);
		}
	}
	
	public void debugPrint() {
		System.out.println("Playlist debug print");
		for (int i = 0; i < playlist.size(); i++) {
			System.out.println(playlist.get(i).getMp3File().getFilename());
		}
		
	}
}
