 /**
  *   Copyright (C) 2015 YAMP Team

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
    USA Copyright (C) 2015 YAMP Team
  */

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

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class YampInfoWindow extends JFrame {
	// The mp3file to display info
	private Mp3File mp3file;
	// GUI components
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

	/**
	 * Constructor
	 * @param filename path to file
	 */
	public YampInfoWindow(String filename) {
		super("Song Info: " + filename);
		try {
			mp3file = new Mp3File(filename);
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
		initUI();
		parseTags();
	}
	
	/**
	 * Initialize GUI elements
	 */
	public void initUI() {
		setSize(400, 580);
		setResizable(false);
		// optional: this line set the window to the center of screen
		setLocationRelativeTo(null);
		// use flow layout
		panel = new JPanel();

		// Setup title label
		lblTitle = new JLabel("Title:");
		lblTitle.setPreferredSize(new Dimension(100, 20));
		txtTitle = new JTextField();
		txtTitle.setPreferredSize(new Dimension(250, 20));
		lblTitle.setLabelFor(txtTitle);
		panel.add(lblTitle);
		panel.add(txtTitle);

		// Setup artist label
		lblArtist = new JLabel("Artist:");
		lblArtist.setPreferredSize(new Dimension(100, 20));
		txtArtist = new JTextField();
		txtArtist.setPreferredSize(new Dimension(250, 20));
		lblArtist.setLabelFor(txtArtist);
		panel.add(lblArtist);
		panel.add(txtArtist);

		// Setup album label
		lblAlbum = new JLabel("Album:");
		lblAlbum.setPreferredSize(new Dimension(100, 20));
		txtAlbum = new JTextField();
		txtAlbum.setPreferredSize(new Dimension(250, 20));
		lblAlbum.setLabelFor(txtAlbum);
		panel.add(lblAlbum);
		panel.add(txtAlbum);

		// Setup genre label
		lblGenre = new JLabel("Genre:");
		lblGenre.setPreferredSize(new Dimension(100, 20));
		txtGenre = new JTextField();
		txtGenre.setPreferredSize(new Dimension(250, 20));
		lblGenre.setLabelFor(txtGenre);
		panel.add(lblGenre);
		panel.add(txtGenre);
		
		// Setup track label
		lblTrack = new JLabel("Track:");
		lblTrack.setPreferredSize(new Dimension(100, 20));
		txtTrack = new JTextField();
		txtTrack.setPreferredSize(new Dimension(250, 20));
		lblTrack.setLabelFor(txtTrack);
		panel.add(lblTrack);
		panel.add(txtTrack);
		
		// Setup year label
		lblYear = new JLabel("Year:");
		lblYear.setPreferredSize(new Dimension(100, 20));
		txtYear = new JTextField();
		txtYear.setPreferredSize(new Dimension(250, 20));
		lblYear.setLabelFor(txtYear);
		panel.add(lblYear);
		panel.add(txtYear);
		
		// Setup album artist label
		lblAlbumArtist = new JLabel("AlbumArtist:");
		lblAlbumArtist.setPreferredSize(new Dimension(100, 20));
		txtAlbumArtist = new JTextField();
		txtAlbumArtist.setPreferredSize(new Dimension(250, 20));
		lblAlbumArtist.setLabelFor(txtAlbumArtist);
		panel.add(lblAlbumArtist);
		panel.add(txtAlbumArtist);
		
		// Setup encoder label
		lblEncoder = new JLabel("Encoder:");
		lblEncoder.setPreferredSize(new Dimension(100, 20));
		txtEncoder = new JTextField();
		txtEncoder.setPreferredSize(new Dimension(250, 20));
		lblEncoder.setLabelFor(txtEncoder);
		panel.add(lblEncoder);
		panel.add(txtEncoder);
		
		// Setup comment label
		lblComment = new JLabel("Comment:");
		lblComment.setPreferredSize(new Dimension(100, 20));
		txtComment = new JTextField();
		txtComment.setPreferredSize(new Dimension(250, 80));
		lblComment.setLabelFor(txtComment);
		panel.add(lblComment);
		panel.add(txtComment);
		
		// Setup cover label
		lblCoverArt = new JLabel("Cover Art:");
		lblCoverArt.setPreferredSize(new Dimension(100, 20));
		lblPic = new JLabel();
		lblPic.setPreferredSize(new Dimension(250, 250));
		lblCoverArt.setLabelFor(lblPic);
		panel.add(lblCoverArt);
		panel.add(lblPic);

		add(panel);
	}

	/**
	 * Parge ID3V1 or ID3V2 tags
	 */
	public void parseTags() {
		if (mp3file.hasId3v2Tag()) {
			// Get ID3Tag sturcture from mp3 file
			ID3v2 id3v2Tag = mp3file.getId3v2Tag();
			// Get fields
			txtTitle.setText(id3v2Tag.getTitle());
			txtArtist.setText(id3v2Tag.getArtist());
			txtAlbum.setText(id3v2Tag.getAlbum());
			txtGenre.setText(id3v2Tag.getGenreDescription());
			txtTrack.setText(id3v2Tag.getTrack());
			txtAlbumArtist.setText(id3v2Tag.getAlbumArtist());
			txtEncoder.setText(id3v2Tag.getEncoder());
			txtComment.setText(id3v2Tag.getEncoder());
			// Draw album artwork
			BufferedImage coverart = null;
			if (id3v2Tag.getAlbumImage() != null) {
				try {
					coverart = ImageIO.read(new ByteArrayInputStream(id3v2Tag.getAlbumImage()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Resize cover image
				BufferedImage resizedimage = new BufferedImage(250, 250, BufferedImage.TYPE_INT_RGB);
				Graphics g = resizedimage.createGraphics();
				g.drawImage(coverart, 0, 0, 250, 250, null);
				g.dispose();
				lblPic.setIcon(new ImageIcon(resizedimage));
			} else {
				// If there is no cover, display placeholder
				lblPic.setIcon(new ImageIcon(getClass().getResource("res/placeholder250.png")));
			}
			
		} else if (mp3file.hasId3v1Tag()) {
			// Get ID3V1 tags from mp3 file
			ID3v1 id3v1Tag = mp3file.getId3v2Tag();
			// Display fields
			txtTitle.setText(id3v1Tag.getTitle());
			txtArtist.setText(id3v1Tag.getArtist());
			txtAlbum.setText(id3v1Tag.getAlbum());
			txtGenre.setText(id3v1Tag.getGenreDescription());
			txtTrack.setText(id3v1Tag.getTrack());
			lblPic.setIcon(new ImageIcon(getClass().getResource("res/placeholder250.png")));
		}
	}

}
