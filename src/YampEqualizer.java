import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class YampEqualizer extends JPanel {
	// Constants
	public static final int LINEARDIST = 1;
	public static final int OVERDIST = 2;

	// Presents
	private int[] gainValue = { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 };
	private int[] PRESET_NORMAL = { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 };
	private int[] PRESET_CLASSICAL = { 50, 50, 50, 50, 50, 50, 70, 70, 70, 76 };
	private int[] PRESET_CLUB = { 50, 50, 42, 34, 34, 34, 42, 50, 50, 50 };
	private int[] PRESET_DANCE = { 26, 34, 46, 50, 50, 66, 70, 70, 50, 50 };
	private int[] PRESET_FULLBASS = { 26, 26, 26, 36, 46, 62, 76, 78, 78, 78 };
	private int[] PRESET_FULLBASSTREBLE = { 34, 34, 50, 68, 62, 46, 28, 22, 18, 18 };
	private int[] PRESET_FULLTREBLE = { 78, 78, 78, 62, 42, 24, 8, 8, 8, 8 };
	private int[] PRESET_LAPTOP = { 38, 22, 36, 60, 58, 46, 38, 24, 16, 14 };
	private int[] PRESET_LIVE = { 66, 50, 40, 36, 34, 34, 40, 42, 42, 42 };
	private int[] PRESET_PARTY = { 32, 32, 50, 50, 50, 50, 50, 50, 32, 32 };
	private int[] PRESET_POP = { 56, 38, 32, 30, 38, 54, 56, 56, 54, 54 };
	private int[] PRESET_REGGAE = { 48, 48, 50, 66, 48, 34, 34, 48, 48, 48 };
	private int[] PRESET_ROCK = { 32, 38, 64, 72, 56, 40, 28, 24, 24, 24 };
	private int[] PRESET_TECHNO = { 30, 34, 48, 66, 64, 48, 30, 24, 24, 28 };

	// private fields
	private int minGain = 0;
	private int maxGain = 100;
	private float[] bands = null;
	private int[] eqgains = null;
	private int eqdist = OVERDIST;
	private YampMain yampmain;
	// GUI componenets
	private JPopupMenu mainpopup = null;
	private JSlider[] sliders;

	public YampEqualizer() {
		super();
		eqgains = new int[10];
		sliders = new JSlider[10];
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
				gainValue[i + 1] = gains[i];
				sliders[i + 1].setValue(maxGain - gainValue[i + 1]);
			}
		}
	}

	/**
	 * Apply equalizer values.
	 */
	public void synchronizeEqualizer() {
		if (true/* equalizer is on */) {
			for (int j = 0; j < eqgains.length; j++) {
				eqgains[j] = -gainValue[j + 1] + maxGain;
			}
			updateBands(eqgains, minGain, maxGain);
		} else {
			for (int j = 0; j < eqgains.length; j++) {
				eqgains[j] = (maxGain - minGain) / 2;
			}
			updateBands(eqgains, minGain, maxGain);
		}
	}

	public void initUI() {
		// Add sliders
		for (int i = 0; i < sliders.length; i++) {
			sliders[i] = new JSlider(JSlider.VERTICAL, 0, 100, 50);
			sliders[i].setBounds(10+10*i, 10, 25, 100);
			sliders[i].addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					// TODO Auto-generated method stub
					for (int i = 0; i < sliders.length; i++)
					{
						gainValue[i] = maxGain - sliders[i].getValue();
					}
// 					if (ui.getSpline() != null) ui.getSpline().repaint();
					// Apply equalizer values.
					synchronizeEqualizer();
				}

			});
			add(sliders[i]);
		}
	}


}
