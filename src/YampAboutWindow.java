/**
 * Copyright (C) 2015 YAMP Team

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
    USA
 */

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;


public class YampAboutWindow extends JFrame {
	// GUI components
	private JLabel lblIcon;
	private JLabel lblTitle;
	private JLabel lblVersion;
	private JLabel lblText;
	private JLabel lblLink;
	private JButton btnLicense;
	private JButton btnDonate;
	
	/**
	 * Constructor
	 */
	public YampAboutWindow() {
		initUI();
	}
	
	/**
	 * Initializer GUI
	 */
	public void initUI() {
		setTitle("About YAMP");
		setSize(400, 300);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);
		
		// Setup icon label
		lblIcon = new JLabel(new ImageIcon(getClass().getResource("/res/icon80.png")));
		lblIcon.setBounds(10, 10, 80, 80);
		add(lblIcon);
		
		// Setup title label
		lblTitle = new JLabel("<html>Yet Another<br>Music Player<html>");
		lblTitle.setFont(new Font("Sans", Font.BOLD, 36));
		lblTitle.setBounds(100, 10, 290, 80);
		add(lblTitle);
		
		// Setup version label
		lblVersion = new JLabel("Version 1.0");
		lblVersion.setFont(new Font("Sans", Font.PLAIN, 20));
		lblVersion.setVerticalAlignment(JLabel.TOP);
		lblVersion.setHorizontalAlignment(JLabel.CENTER);
		lblVersion.setBounds(10, 100, 380, 200);
		add(lblVersion);
		
		// Setup text label
		// Use HTML to format display
		lblText = new JLabel("<html><center><p>A simple and light-weight cross-platform MP3 player</p>"
						+ "<p>Copyright &copy; 2015 YAMP Team"
						+ "</center></html>");
		lblText.setFont(new Font("Serif", Font.PLAIN, 16));
		lblText.setVerticalAlignment(JLabel.TOP);
		lblText.setBounds(10, 130, 380, 60);
		add(lblText);
		
		// Setup link label
		lblLink = new JLabel("<html><center><p><a href=\"http://kevinlmh.github.io/YAMP/\">http://kevinlmh.github.io/YAMP</a></p></center></html>");
		lblLink.setFont(new Font("Serif", Font.PLAIN, 16));
		lblLink.setVerticalAlignment(JLabel.TOP);
		lblLink.setBounds(70, 190, 270, 20);
		lblLink.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			            desktop.browse(new URI("http://kevinlmh.github.io/YAMP"));
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}
		});
		add(lblLink);
		
		// Setup license button
		btnLicense = new JButton("License");
		btnLicense.setBounds(10, 230, 100, 30);
		btnLicense.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame licenseframe = new JFrame("License");
				licenseframe.setSize(400, 300);
				licenseframe.setResizable(false);
				licenseframe.setLocationRelativeTo(null);
				JTextPane textpane = new JTextPane();
				textpane.setText("       Copyright (C) 2015 YAMP Team\n\n" +
					    "This software is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.\n\n" +
					    "This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.\n\n" +
					    "You should have received a copy of the GNU Lesser General Public License along with this software; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA");
		        textpane.setEditable(false);
		        JScrollPane scrollpane = new JScrollPane(textpane);  
		        licenseframe.add(scrollpane); 
				licenseframe.setVisible(true);
			}	
		});
		add(btnLicense);
		
		// Setup donate button
		btnDonate = new JButton("Donate");
		btnDonate.setBounds(280, 230, 100, 30);
		btnDonate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			            desktop.browse(new URI("https://www.paypal.com/us/cgi-bin/webscr?cmd=_flow&SESSION=qHWTfeOfIu39LIhWjP8cTvDQfPe5C3Z2MuexSpQnBQ9nTIqD7Q7nijco62W&dispatch=5885d80a13c0db1f8e263663d3faee8d96f000117187ac9edec8a65b311f447e"));
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}	
		});
		add(btnDonate);
	}
}
