import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;


public class YampAboutWindow extends JDialog {
	// GUI components
	private JLabel lblIcon;
	
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
		lblIcon = new JLabel(new ImageIcon(getClass().getResource("/res/icon200.png")));
		lblIcon.setBounds(10, 10, 200, 200);
		add(lblIcon);
	}
}
