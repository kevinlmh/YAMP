import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JList;
import javax.swing.ImageIcon;

import java.awt.Font;


public class YampUIWB extends JFrame {
	public YampUIWB() {
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		JMenuItem mntmOpenLyrics = new JMenuItem("Open Lyrics");
		mnFile.add(mntmOpenLyrics);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnFile.add(mntmQuit);
		
		JMenu mnControl = new JMenu("Control");
		menuBar.add(mnControl);
		
		JMenuItem mntmPlay = new JMenuItem("Play");
		mnControl.add(mntmPlay);
		
		JMenuItem mntmPause = new JMenuItem("Pause");
		mnControl.add(mntmPause);
		
		JMenuItem mntmStop = new JMenuItem("Stop");
		mnControl.add(mntmStop);
		
		JMenuItem mntmPrevious = new JMenuItem("Previous");
		mnControl.add(mntmPrevious);
		
		JMenuItem mntmNext = new JMenuItem("Next");
		mnControl.add(mntmNext);
		
		JMenu mnPlaylist = new JMenu("Playlist");
		menuBar.add(mnPlaylist);
		
		JMenuItem mntmAddToPlaylist = new JMenuItem("Add to Playlist");
		mnPlaylist.add(mntmAddToPlaylist);
		
		JMenuItem mntmRemoveSelections = new JMenuItem("Remove Selections");
		mnPlaylist.add(mntmRemoveSelections);
		
		JMenuItem mntmClearPlaylist = new JMenuItem("Clear Playlist");
		mnPlaylist.add(mntmClearPlaylist);
		
		JMenuItem mntmSavePlaylist = new JMenuItem("Save Playlist");
		mnPlaylist.add(mntmSavePlaylist);
		
		JMenuItem mntmLoadPlaylist = new JMenuItem("Load Playlist");
		mnPlaylist.add(mntmLoadPlaylist);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnPrev = new JButton("");
		btnPrev.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/rewind10.png"));
		panel.add(btnPrev);
		
		JButton btnPlayPause = new JButton("");
		btnPlayPause.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/play87.png"));
		panel.add(btnPlayPause);
		
		JButton btnNext = new JButton("");
		btnNext.setIcon(new ImageIcon("/home/kevin/workspace/YAMP/png/forward2.png"));
		panel.add(btnNext);
		
		JPanel panel_1 = new JPanel();
		
		JLabel label = new JLabel("Title");
		
		JLabel label_1 = new JLabel("Artist");
		
		JList list = new JList<Object>();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(list, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE))
					.addGap(33)
					.addComponent(list, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(26, Short.MAX_VALUE))
		);
		
		JLabel lblAlbum = new JLabel("Album");
		
		JLabel lblPlaytime = new JLabel("00:00");
		lblPlaytime.setFont(new Font("Dialog", Font.BOLD, 24));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(12)
					.addComponent(lblPlaytime, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAlbum, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
						.addComponent(label))
					.addGap(71))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblPlaytime, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
						.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
							.addContainerGap()
							.addComponent(label)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_1)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblAlbum)
					.addContainerGap(35, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		getContentPane().setLayout(groupLayout);
	}
}
