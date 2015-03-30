import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class YampInfoWindow extends JFrame {
	private Mp3File mp3file;

	private JPanel panel;
	
	private JLabel lblTitle;
	private JLabel lblArtist;
	private JLabel lblAlbum;
	private JLabel lblGenre;
	private JLabel lblTrack;
	private JLabel lblYear;
	private JLabel lblAlbumArtist;
	private JLabel lblEncoder;
	private JLabel lblComment;
	private JLabel lblCoverArt;
	private JLabel lblPic;
	
	private JTextField txtTitle;
	private JTextField txtArtist;	
	private JTextField txtAlbum;	
	private JTextField txtGenre;
	private JTextField txtTrack;
	private JTextField txtYear;
	private JTextField txtAlbumArtist;
	private JTextField txtEncoder;
	private JTextField txtComment;

	public YampInfoWindow(String filename) {
		super("Song Info: " + filename);
		try {
			mp3file = new Mp3File(filename);
		} catch (UnsupportedTagException | InvalidDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initUI();
		parseTags();
	}

	public void initUI() {
		setSize(400, 580);
		setResizable(false);
		// optional: this line set the window to the center of screen
		setLocationRelativeTo(null);
		// use flow layout
		panel = new JPanel();

		lblTitle = new JLabel("Title:");
		lblTitle.setPreferredSize(new Dimension(100, 20));
		txtTitle = new JTextField();
		txtTitle.setPreferredSize(new Dimension(250, 20));
		lblTitle.setLabelFor(txtTitle);
		panel.add(lblTitle);
		panel.add(txtTitle);

		lblArtist = new JLabel("Artist:");
		lblArtist.setPreferredSize(new Dimension(100, 20));
		txtArtist = new JTextField();
		txtArtist.setPreferredSize(new Dimension(250, 20));
		lblArtist.setLabelFor(txtArtist);
		panel.add(lblArtist);
		panel.add(txtArtist);

		lblAlbum = new JLabel("Album:");
		lblAlbum.setPreferredSize(new Dimension(100, 20));
		txtAlbum = new JTextField();
		txtAlbum.setPreferredSize(new Dimension(250, 20));
		lblAlbum.setLabelFor(txtAlbum);
		panel.add(lblAlbum);
		panel.add(txtAlbum);

		lblGenre = new JLabel("Genre:");
		lblGenre.setPreferredSize(new Dimension(100, 20));
		txtGenre = new JTextField();
		txtGenre.setPreferredSize(new Dimension(250, 20));
		lblGenre.setLabelFor(txtGenre);
		panel.add(lblGenre);
		panel.add(txtGenre);
		
		lblTrack = new JLabel("Track:");
		lblTrack.setPreferredSize(new Dimension(100, 20));
		txtTrack = new JTextField();
		txtTrack.setPreferredSize(new Dimension(250, 20));
		lblTrack.setLabelFor(txtTrack);
		panel.add(lblTrack);
		panel.add(txtTrack);
		
		lblYear = new JLabel("Year:");
		lblYear.setPreferredSize(new Dimension(100, 20));
		txtYear = new JTextField();
		txtYear.setPreferredSize(new Dimension(250, 20));
		lblYear.setLabelFor(txtYear);
		panel.add(lblYear);
		panel.add(txtYear);
		
		lblAlbumArtist = new JLabel("AlbumArtist:");
		lblAlbumArtist.setPreferredSize(new Dimension(100, 20));
		txtAlbumArtist = new JTextField();
		txtAlbumArtist.setPreferredSize(new Dimension(250, 20));
		lblAlbumArtist.setLabelFor(txtAlbumArtist);
		panel.add(lblAlbumArtist);
		panel.add(txtAlbumArtist);
		
		lblEncoder = new JLabel("Encoder:");
		lblEncoder.setPreferredSize(new Dimension(100, 20));
		txtEncoder = new JTextField();
		txtEncoder.setPreferredSize(new Dimension(250, 20));
		lblEncoder.setLabelFor(txtEncoder);
		panel.add(lblEncoder);
		panel.add(txtEncoder);
		
		lblComment = new JLabel("Comment:");
		lblComment.setPreferredSize(new Dimension(100, 20));
		txtComment = new JTextField();
		txtComment.setPreferredSize(new Dimension(250, 80));
		lblComment.setLabelFor(txtComment);
		panel.add(lblComment);
		panel.add(txtComment);
		
		lblCoverArt = new JLabel("Cover Art:");
		lblCoverArt.setPreferredSize(new Dimension(100, 20));
		lblPic = new JLabel();
		lblPic.setPreferredSize(new Dimension(250, 250));
		lblCoverArt.setLabelFor(lblPic);
		panel.add(lblCoverArt);
		panel.add(lblPic);

		add(panel);

	}

	public void parseTags() {
		if (mp3file.hasId3v2Tag()) {
			ID3v2 id3v2Tag = mp3file.getId3v2Tag();
			txtTitle.setText(id3v2Tag.getTitle());
			txtArtist.setText(id3v2Tag.getArtist());
			txtAlbum.setText(id3v2Tag.getAlbum());
			txtGenre.setText(id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")" );
			txtTrack.setText(id3v2Tag.getTrack());
			txtAlbumArtist.setText(id3v2Tag.getAlbumArtist());
			txtEncoder.setText(id3v2Tag.getEncoder());
			txtComment.setText(id3v2Tag.getEncoder());
			BufferedImage coverart = null;
			try {
				coverart = ImageIO.read(new ByteArrayInputStream(id3v2Tag.getAlbumImage()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedImage resizedimage = new BufferedImage(250, 250, BufferedImage.TYPE_INT_RGB);
			Graphics g = resizedimage.createGraphics();
			g.drawImage(coverart, 0, 0, 250, 250, null);
			g.dispose();
			lblPic.setIcon(new ImageIcon(resizedimage));
		}
	}

}
