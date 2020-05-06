import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/* This class makes a frame that can be used to display pictures.
 * 
 * Images are resized so that they will fit into the window, taking as
 * much space as possible without skewing the proportions.
 */
public class PictureViewer {

	private static final int WINDOW_HEIGHT = 540;
	private static final int WINDOW_WIDTH = 960;
	
	private static final int MIN_PIC_WIDTH = 200;
	private static final int MIN_PIC_HEIGHT = 150;

	private static MyPanel thePanel = new MyPanel();

	private static Image currentImage = null;
	private static String currentImageName = null;

	public static void showImage(String imageName) {
		try {
			String picName = imageName;
			picName = picName.replace(" ", "%20");  // spaces cause trouble
			URL imageURL = new URL(picName);
			BufferedImage temp = ImageIO.read(imageURL);
			if (temp != null && 
				temp.getHeight() >= MIN_PIC_HEIGHT && 
				temp.getWidth() >= MIN_PIC_WIDTH) {
				currentImage = temp;
				currentImageName = picName;
			}
			thePanel.repaint();
		} catch (Exception e) {    
			return;
		}
	}

	private static void setupPanel() {
		thePanel.setLayout(null);
		thePanel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		thePanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		thePanel.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		thePanel.setBounds(new java.awt.Rectangle(WINDOW_WIDTH, WINDOW_HEIGHT));
	}

	private static void setupFrame() {
		JFrame frame = new JFrame("Picture Viewer");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(thePanel);
		frame.pack();
		frame.setVisible(true);
	}

	private static class MyPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public MyPanel() {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Thread.currentThread().setPriority(Thread.MAX_PRIORITY);	
					setupPanel();
					setupFrame();
				}
			});
		}

		private static Font theFont = new Font("Arial", Font.BOLD, 18);

		public void paint(Graphics g) {
			super.paint(g);
			if (currentImage != null) {
				double scaleHeight = 1.0;
				double scaleWidth = 1.0;
				int h = currentImage.getHeight(null);
				int w = currentImage.getWidth(null);
				scaleHeight = (double)WINDOW_HEIGHT / h;
				scaleWidth = (double)WINDOW_WIDTH / w;
				double finalScale = scaleHeight > scaleWidth ? scaleWidth : scaleHeight;
				g.drawImage(currentImage, 0, 0, (int)(finalScale * w), (int)(finalScale * h), null);
			}
			if (currentImageName != null) {
				g.setFont(theFont);
				g.setColor(Color.GREEN);
				g.drawString(currentImageName, 0, 30); 
			}
		}
	}
}
