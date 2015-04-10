import java.io.File;
import java.io.InputStream;
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
	// Playlist
	private YampPlaylist playlist = null;
	// Info Window
	private YampInfoWindow infowindow;

	// Path of current song that is opened
	private String currentSongPath;
	// Total number of bytes
	private int totalBytes;
	// Number of bytes per second
	private int bytesPerSecond;
	// Header position
	private int headerPosition;
	// Current byte
	private int currentByte;
	// Current player state
	private int currentState;
	// Duration of current song
	private int duration;

	/**
	 * Constructor.
	 */
	public YampDriver() {
		// Instantiate BasicPlayer.
		player = new BasicPlayer();
		// BasicPlayer is a BasicController.
		control = (BasicController) player;
		try {
			control.setGain(0.85);
			control.setPan(0.0);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Register BasicPlayerTest to BasicPlayerListener events.
		// It means that this object will be notified on BasicPlayer
		// events such as : opened(...), progress(...), stateUpdated(...)
		player.addBasicPlayerListener(this);
		playlist = new YampPlaylist();
		ui = new YampUI("Yet Another Music Player", YampDriver.this, playlist);
		ui.setVisible(true);
	}

	public static void main(String[] args) {
		YampDriver test = new YampDriver();
	}

	public void open(String filepath) {
		try {
			control.open(new File(filepath));
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentSongPath = filepath;
	}

	public void play() {
		try {
			control.play();
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
			e.printStackTrace();
		}
	}
	
	public void jump(int seconds) {
		try {
			control.seek(headerPosition + bytesPerSecond * seconds);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void forward(int seconds) {
		try {
			control.seek(currentByte + seconds * bytesPerSecond);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rewind(int seconds) {
		try {
			control.seek(currentByte - seconds * bytesPerSecond);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setVolume(int percentage) {
		try {
			control.setGain((double) percentage / 100.0);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	public void quit() {
		stop();
		System.exit(0);
	}

	public void displayInfo() {
		if (currentSongPath != null) {
			infowindow = new YampInfoWindow(currentSongPath);
			infowindow.setVisible(true);
		} else {
			System.out.println("File Info: No open file.");
		}

	}

	public int getState() {
		return currentState;
	}

	public int getbytesPerSecond() {
		return bytesPerSecond;
	}

	public int getTotalBytes() {
		return totalBytes;
	}
	
	public int getDuration() {
		return duration;
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
		duration = (int) ((Long) (properties.get("duration")) / 1000000);
		ui.setTotalTime(duration);
		totalBytes = (Integer) properties.get("mp3.length.bytes");
		bytesPerSecond = (int) ((Integer) properties.get("mp3.framesize.bytes") * (Float) properties
				.get("mp3.framerate.fps"));
		headerPosition = (Integer) properties.get("mp3.header.pos");

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
	public void progress(int bytesread, long microseconds, byte[] pcmdata,
			Map properties) {
		// Pay attention to properties. It depends on underlying JavaSound SPI
		// MP3SPI provides mp3.equalizer.
//		 System.out.println("progress : " + properties.toString());
		currentByte = bytesread;
		ui.updateTime((int) ((Long)properties.get("mp3.position.microseconds")/1000000));
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
		if (event.getCode() != BasicPlayerEvent.GAIN
				| event.getCode() != BasicPlayerEvent.PAN
				| event.getCode() != BasicPlayerEvent.OPENING
				| event.getCode() != BasicPlayerEvent.SEEKING
				| event.getCode() != BasicPlayerEvent.UNKNOWN) {
			currentState = event.getCode();
		}
		if (event.getCode() == BasicPlayerEvent.EOM){
			ui.resetPlayButton();
		}
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
//		System.out.println("setController : " + controller);
	}

}
