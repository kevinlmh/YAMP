
import java.io.*;
import java.util.*;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author Jack
 */
public class YampPlaylist2 {

    protected ArrayList<File> playlist;

    //Constructor
    public YampPlaylist2() {
        playlist = new ArrayList<File>();
    }

    //Load items to playlist from M3U file
    public void loadFromM3U() throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new FileReader("/Users/Jack/Desktop/list1.m3u"));

        String line = null;
        String songName = null;
        String songFile = null;


//        while ((line = br.readLine()) != null) {
//            if (line.trim().length() == 0) {
//                continue;
//            }
//            //SONG INFO FROM M3U FILE
//            if (line.startsWith("#")) {
//                if (line.toUpperCase().startsWith("#EXTINF")) {
//                    int indA = line.indexOf(",", 0);
//                    if (indA != -1) {
//                        songName = line.substring(indA + 1, line.length());
//                    }
//                }
//            } //SONG FILE FROM M3U FILE
//            else {
//                songFile = line;
//                File f = new File(songFile);
//
//                playlist.add(f);
//                songFile = null;
//                songName = null;
//            }
//        }
    }

    public void appendElement(File f) {
        playlist.add(f);
    }
}
