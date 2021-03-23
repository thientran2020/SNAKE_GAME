/* Thien Tran
 * Date: 03/22/2021
 * Purpose: Practice Java GUI using Java AWT & Swing.
 */

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class snake {
	public static void main(String[] args) {
		new snakeGame();
	}
}

class snakeGame extends JFrame{
	snakeGame() {
		this.add(new mainPanel());
		this.setTitle("SNAKE GAME");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.setVisible(true);
		ImageIcon img = new ImageIcon("logo.jpg");
		this.setIconImage(img.getImage());
	}
}

