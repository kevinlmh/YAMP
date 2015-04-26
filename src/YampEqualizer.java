import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class YampEqualizer extends JPanel {
	// Constants
	public static final int LINEARDIST = 1;
	public static final int OVERDIST = 2;
	private int PRESET_NORMAL = 0;
	private int PRESET_CLASSICAL = 1;
	private int PRESET_CLUB = 2;
	private int PRESET_DANCE = 3;
	private int PRESET_FULLBASS = 4;
	private int PRESET_FULLBASSTREBLE = 5;
	private int PRESET_FULLTREBLE = 6;
	private int PRESET_LAPTOP = 7;
	private int PRESET_LIVE = 8;
	private int PRESET_PARTY = 9;
	private int PRESET_POP = 10;
	private int PRESET_REGGAE = 11;
	private int PRESET_ROCK = 12;
	private int PRESET_TECHNO = 13;

	// Presents
	private int[] gainValue = { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 };
	private int[] GAIN_VALUE_NORMAL = { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 };
	private int[] GAIN_VALUE_CLASSICAL = { 50, 50, 50, 50, 50, 50, 70, 70, 70, 76 };
	private int[] GAIN_VALUE_CLUB = { 50, 50, 42, 34, 34, 34, 42, 50, 50, 50 };
	private int[] GAIN_VALUE_DANCE = { 26, 34, 46, 50, 50, 66, 70, 70, 50, 50 };
	private int[] GAIN_VALUE_FULLBASS = { 26, 26, 26, 36, 46, 62, 76, 78, 78, 78 };
	private int[] GAIN_VALUE_FULLBASSTREBLE = { 34, 34, 50, 68, 62, 46, 28, 22, 18, 18 };
	private int[] GAIN_VALUE_FULLTREBLE = { 78, 78, 78, 62, 42, 24, 8, 8, 8, 8 };
	private int[] GAIN_VALUE_LAPTOP = { 38, 22, 36, 60, 58, 46, 38, 24, 16, 14 };
	private int[] GAIN_VALUE_LIVE = { 66, 50, 40, 36, 34, 34, 40, 42, 42, 42 };
	private int[] GAIN_VALUE_PARTY = { 32, 32, 50, 50, 50, 50, 50, 50, 32, 32 };
	private int[] GAIN_VALUE_POP = { 56, 38, 32, 30, 38, 54, 56, 56, 54, 54 };
	private int[] GAIN_VALUE_REGGAE = { 48, 48, 50, 66, 48, 34, 34, 48, 48, 48 };
	private int[] GAIN_VALUE_ROCK = { 32, 38, 64, 72, 56, 40, 28, 24, 24, 24 };
	private int[] GAIN_VALUE_TECHNO = { 30, 34, 48, 66, 64, 48, 30, 24, 24, 28 };

	// private fields
	private int minGain = 0;
	private int maxGain = 100;
	private float[] bands = null;
	private int[] eqgains = null;
	private int eqdist = OVERDIST;
	private int currentPreset = 0;
	private YampMain yampmain;
	
	// GUI componenets
	private JPopupMenu mainpopup = null;
	private JSlider[] sliders;
	private JLabel[] sliderLabels;
	private JCheckBox ckbEnable;
	private JLabel lblEnable;
	private JLabel lblMax;
	private JLabel lblMid;
	private JLabel lblMin;
	private JComboBox cbbPresets;
	private JComboBox cbbDistribution;
	
	public YampEqualizer() {
		super();
		eqgains = new int[10];
		sliders = new JSlider[10];
		sliderLabels = new JLabel[10];
		initUI();
	}

	public void setMain(YampMain yampmain) {
		this.yampmain = yampmain;
	}

	/**
	 * Set bands array for equalizer.
	 *
	 * @param bands
	 */
	public void setBands(float[] bands) {
		this.bands = bands;
	}

	/**
	 * Return equalizer bands distribution.
	 * 
	 * @return
	 */
	public int getEqdist() {
		return eqdist;
	}

	/**
	 * Set equalizer bands distribution.
	 * 
	 * @param i
	 */
	public void setEqdist(int i) {
		eqdist = i;
	}
	
	/**
	 * Apply equalizer function.
	 *
	 * @param gains
	 * @param min
	 * @param max
	 */
	public void updateBands(int[] gains, int min, int max) {
		if ((gains != null) && (bands != null)) {
			int j = 0;
			float gvalj = (gains[j] * 2.0f / (max - min) * 1.0f) - 1.0f;
			float gvalj1 = (gains[j + 1] * 2.0f / (max - min) * 1.0f) - 1.0f;
			// Linear distribution : 10 values => 32 values.
			if (eqdist == LINEARDIST) {
				float a = (gvalj1 - gvalj) * 1.0f;
				float b = gvalj * 1.0f - (gvalj1 - gvalj) * j;
				// x=s*x'
				float s = (gains.length - 1) * 1.0f / (bands.length - 1) * 1.0f;
				for (int i = 0; i < bands.length; i++) {
					float ind = s * i;
					if (ind > (j + 1)) {
						j++;
						gvalj = (gains[j] * 2.0f / (max - min) * 1.0f) - 1.0f;
						gvalj1 = (gains[j + 1] * 2.0f / (max - min) * 1.0f) - 1.0f;
						a = (gvalj1 - gvalj) * 1.0f;
						b = gvalj * 1.0f - (gvalj1 - gvalj) * j;
					}
					// a*x+b
					bands[i] = a * i * 1.0f * s + b;
				}
			}
			// Over distribution : 10 values => 10 first value of 32 values.
			else if (eqdist == OVERDIST) {
				for (int i = 0; i < gains.length; i++) {
					bands[i] = (gains[i] * 2.0f / (max - min) * 1.0f) - 1.0f;
				}
			}
		}
	}

	/**
	 * Update sliders from gains array.
	 *
	 * @param gains
	 */
	public void updateSliders(int[] gains) {
		if (gains != null) {
			for (int i = 0; i < gains.length; i++) {
				gainValue[i] = gains[i];
				sliders[i].setValue(maxGain - gainValue[i]);
			}
		}
	}

	/**
	 * Apply equalizer values.
	 */
	public void synchronizeEqualizer() {
		if (ckbEnable.isSelected()) {
			for (int j = 0; j < eqgains.length; j++) {
				eqgains[j] = -gainValue[j] + maxGain;
			}
			updateBands(eqgains, minGain, maxGain);
		} else {
			for (int j = 0; j < eqgains.length; j++) {
				eqgains[j] = (maxGain - minGain) / 2;
			}
			updateBands(eqgains, minGain, maxGain);
		}
	}

	public void loadPreset(int i) {
        switch(i) {
            case 0: updateSliders(GAIN_VALUE_NORMAL);
                    			synchronizeEqualizer();
                    			currentPreset=i;
                    break;
            case 1: updateSliders(GAIN_VALUE_CLASSICAL);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 2: updateSliders(GAIN_VALUE_CLUB);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 3: updateSliders(GAIN_VALUE_DANCE);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 4: updateSliders(GAIN_VALUE_FULLBASS);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 5: updateSliders(GAIN_VALUE_FULLBASSTREBLE);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 6: updateSliders(GAIN_VALUE_FULLTREBLE);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 7: updateSliders(GAIN_VALUE_LAPTOP);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 8: updateSliders(GAIN_VALUE_LIVE);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 9:	updateSliders(GAIN_VALUE_PARTY);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 10:updateSliders(GAIN_VALUE_POP);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 11:updateSliders(GAIN_VALUE_REGGAE);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 12:updateSliders(GAIN_VALUE_ROCK);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
            case 13:updateSliders(GAIN_VALUE_TECHNO);
                    synchronizeEqualizer();
                    currentPreset=i;
                    break;
        }
}
	
	public void initUI() {
		setLayout(null);
		// Add on/off check button
		ckbEnable = new JCheckBox();
		ckbEnable.setSelected(false);
		ckbEnable.setBounds(10, 5, 30, 30);
		ckbEnable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronizeEqualizer();
			}
		});
		add(ckbEnable);
		// Add on/off label
		lblEnable = new JLabel("Enable");
		lblEnable.setBounds(40, 5, 60, 30);
		add(lblEnable);
		// Add gain labels
		lblMax = new JLabel("+20dB");
		lblMax.setBounds(10, 40, 50, 25);
		add(lblMax);
		lblMid = new JLabel("0dB");
		lblMid.setBounds(10, 105, 50, 25);
		add(lblMid);
		lblMin = new JLabel("-20dB");
		lblMin.setBounds(10, 165, 50, 25);
		add(lblMin);
		// Add distribution drop down
		String[] distributionStrings = {"Linear", "Over"};
		cbbDistribution = new JComboBox(distributionStrings);
		cbbDistribution.setBounds(120, 10, 80, 25);
		cbbDistribution.setSelectedIndex(1);
		cbbDistribution.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEqdist(cbbDistribution.getSelectedIndex() + 1);
                loadPreset(currentPreset);
			}			
		});
		add(cbbDistribution);
		// Add preset drop down
		String[] presetStrings = {"Normal", "Classical", "Club", "Dance", "Full Bass", "Full Bass Trebel", "Full Treble",
								"Laptop", "Live", "Party", "Pop", "Reggae", "Rock", "Techno"};
		cbbPresets = new JComboBox(presetStrings);
		cbbPresets.setBounds(230, 10, 150, 25);
		cbbPresets.setSelectedIndex(0);
		cbbPresets.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadPreset(cbbPresets.getSelectedIndex());
			}
		});
		add(cbbPresets);
		// Add sliders and slider labels
		String[] sliderStrings = {"60", "170", "310", "600", "1K", "3K", "6K", "12K", "14K", "16K"};
		for (int i = 0; i < sliders.length; i++) {
			sliders[i] = new JSlider(JSlider.VERTICAL, 0, 100, 50);
			sliders[i].setBounds(60 + 33*i, 40, 25, 150);
			sliders[i].addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					// TODO Auto-generated method stub
					for (int i = 0; i < sliders.length; i++) {
						gainValue[i] = maxGain - sliders[i].getValue();
					}
// 					if (ui.getSpline() != null) ui.getSpline().repaint();
					// Apply equalizer values.
					synchronizeEqualizer();
				}

			});
			add(sliders[i]);
			sliderLabels[i] = new JLabel(sliderStrings[i]);
			sliderLabels[i].setBounds(60 + 33*i, 185, 25, 25);
			add(sliderLabels[i]);
		}
	}


}
