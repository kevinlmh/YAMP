import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;


public class YampAboutWindow extends JDialog {
	// GUI components
	private JLabel lblIcon;
	private JLabel lblYamp;
	private JLabel lblParagraph;
	
	public YampAboutWindow() {
		initUI();
	}
	
	public void initUI() {
		setTitle("About YAMP");
		setSize(500, 300);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);
		
		// Setup icon label
		lblIcon = new JLabel(new ImageIcon(getClass().getResource("/res/icon120.png")));
		lblIcon.setBounds(10, 10, 120, 120);
		add(lblIcon);
		
		// Setup yamp label
		lblYamp = new JLabel("YAMP");
		lblYamp.setFont(new Font("Serif", Font.BOLD, 60));
		lblYamp.setForeground(Color.ORANGE);
		lblYamp.setBounds(150, 10, 250, 60);
		add(lblYamp);
		
//		// Setup paragraph label
//		lblParagraph = new JLabel();
//		lblParagraph.setText("Yet Another Music Player is made by \n"
//							+ "Pranav Bhandari, Minghui Liu and \n"
//							+ "Jack Wallace as a class project.");
//		lblParagraph.setForeground(Color.ORANGE);
//		lblParagraph.setBounds(150, 30, 340, 150);
//		add(lblParagraph);
	}
}
