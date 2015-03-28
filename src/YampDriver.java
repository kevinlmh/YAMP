import java.io.File;
import java.io.IOException;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class YampDriver implements BasicPlayerListener {
	// BasicPlayer module
	private BasicPlayer player = null;
	// BasicController module
	private BasicController control = null;
	// UI module
	private YampUI ui = null;
	
	// Path of current song that is opened
	private String currentSongPath;
        
        //******
        //****** PLAYLIST
        private YampPlaylist2 testList = null;
        //******
        //******
	
	/** 
	 * Constructor. 
	 */
	public YampDriver() throws IOException {
		// Instantiate BasicPlayer.
		player = new BasicPlayer();
		// BasicPlayer is a BasicController.
		control = (BasicController) player;	
		// Register BasicPlayerTest to BasicPlayerListener events.
		// It means that this object will be notified on BasicPlayer
		// events such as : opened(...), progress(...), stateUpdated(...)
		player.addBasicPlayerListener(this);
		
                //********
                //******** INIT PLAYLIST
                testList = new YampPlaylist2();
                testList.loadFromM3U();
                //********
                //********
                
                
		ui = new YampUI("Yet Another Music Player", YampDriver.this, testList);
		ui.setVisible(true);
                
                
	}
	
	
	public static void main(String[] args) throws IOException {
		YampDriver test = new YampDriver();
		//test.play("/home/kevin/test.mp3");
		//test.play(args[0]);
                
	}
	
	public void play(String filename) {
		try { 
			// Open file, or URL or Stream (shoutcast, icecast) to play.
			control.open(new File(filename));

			control.play();

			// Set Volume (0 to 1.0).
			control.setGain(0.85);
			// Set Pan (-1.0 to 1.0).
			control.setPan(0.0);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			control.stop();
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void pause() {
		try {
			control.pause();
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void resume() {
		try {
			control.resume();
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void skip(long bytes) {
		try {
			control.seek(bytes);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setCurrentSong(String filepath) {
		currentSongPath = filepath;
	}
	
	public void setVolume(int percentage) {
		try {
			control.setGain((double)percentage/100.0);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Implementation of functions in BasicPlayerListener interface */
	
	
	/**
	 * Open callback, stream is ready to play.
	 *
	 * properties map includes audio format dependent features such as bit rate,
	 * duration, frequency, channels, number of frames, VBR flag, ...
	 *
	 * @param stream
	 *            could be File, URL or InputStream
	 * @param properties
	 *            audio stream properties.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void opened(Object stream, Map properties) {
		// Pay attention to properties. It's useful to get duration,
		// bit rate, channels, even tag such as ID3v2.
		System.out.println("opened : " + properties.toString());
                System.out.println("[Title]: " + properties.get("title"));
		int seconds = (int)((Long)(properties.get("duration"))/1000000);
		ui.setTotalTime(seconds);
	}

	/**
	 * * Progress callback while playing.
	 *
	 * This method is called severals time per seconds while playing. properties
	 * map includes audio format features such as instant bit rate, microseconds
	 * position, current frame number, ...
	 *
	 * @param bytesread
	 *            from encoded stream.
	 * @param microseconds
	 *            elapsed (<b>reseted after a seek !</b>).
	 * @param pcmdata
	 *            PCM samples.
	 * @param properties
	 *            audio stream parameters.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
		// Pay attention to properties. It depends on underlying JavaSound SPI
		// MP3SPI provides mp3.equalizer.
//		System.out.println("progress : " + properties.toString());
//		System.out.println("bytesread: " + bytesread + ", microseconds: " + microseconds);
		int seconds = (int) (microseconds / 1000000);
		ui.updateTime(seconds);
	}

	/**
	 * Notification callback for basicplayer events such as opened, eom ...
	 *
	 * @param event
	 */
	@Override
	public void stateUpdated(BasicPlayerEvent event) {
		// Notification of BasicPlayer states (opened, playing, end of media,
		// ...)
		System.out.println("stateUpdated : " + event.toString());
	}

	/**
	 * A handle to the BasicPlayer, plugins may control the player through the
	 * controller (play, stop, ...)
	 * 
	 * @param controller
	 *            : a handle to the player
	 */
	@Override
	public void setController(BasicController controller) {
		System.out.println("setController : " + controller);
	}
	
}
