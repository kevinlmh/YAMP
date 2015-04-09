import java.util.ArrayList;


public class YampPlaylist {
	private ArrayList<YampPlaylistElement> playlist;
	
	public YampPlaylist() {
		playlist = new ArrayList<YampPlaylistElement>();
	}
	
	public void appendElement(YampPlaylistElement element) {
		playlist.add(element);
//		element.setIndex(playlist.size());
	}
	
	public void add(int index, YampPlaylistElement element) {
		playlist.add(index, element);
//		element.setIndex(index);
//		// change the index number of all the elements behind
//		for (int i = index + 1; i < playlist.size(); i++) {
//			playlist.get(i).setIndex(i);
//		}
	}
	
	public void remove(int index) {
		playlist.remove(index);
//		for (int i = index; i < playlist.size(); i++) {
//			playlist.get(i).setIndex(i);
//		}
	}
	
	public YampPlaylistElement get(int index) {
		return playlist.get(index);
	}
	
	public int size() {
		return playlist.size();
	}
}
