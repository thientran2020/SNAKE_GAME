import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

/*
 * @mainPanel: the main panel for the game
 * Panel has size PANEL_WIDTH * PANEL_HEIGHT - each small unit has size UNIT_SIZE * UNIT_SIZE
 * Attributes:
 * @X, Y: arrays contain the positions of the snake with length = @snakeLength
 * @defaultLength: the default length of the snake when app is run
 * @(giftX, giftY): the coordinates of the target
 * @direction: character implies 'U'p, 'D'own, 'R'ight, 'L'eft.
 * @moving: boolean variable shows the status of the snake (moving == False => the game is over)
 * @tempStop: boolean variable shows the status of the game (temporary stop / continue when Enter is pressed)
 * @DELAY_TIME: time to delay for timer (the smaller DELAY_TIME is, the faster the snake is moving)
 */
class mainPanel extends JPanel implements ActionListener{
	final int PANEL_WIDTH = 1000;
	final int PANEL_HEIGHT = 800;
	final int UNIT_SIZE = 20;
	final int DELAY_TIME = 70;
	final int[] X = new int[PANEL_WIDTH / UNIT_SIZE];
	final int[] Y = new int[PANEL_HEIGHT / UNIT_SIZE];
	final boolean[][] wall = new boolean[PANEL_WIDTH / UNIT_SIZE][PANEL_HEIGHT / UNIT_SIZE];
	int defaultLength = 7, snakeLength = 7;
	int giftX;
	int giftY;
	char direction = 'R';
	boolean moving = true;
	boolean tempStop = false;
	Timer timer;
	Random random = new Random();
	
	mainPanel() {
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setFocusable(true);
		this.setBackground(Color.black);
		this.addKeyListener(new MyKeyAdapter());
		this.addMouseListener(new MyMouseAdapter());
		start();
	}
	
	public void start() {
		int numUnitsX = (int) PANEL_WIDTH / UNIT_SIZE;
		int numUnitsY = (int) PANEL_WIDTH / UNIT_SIZE;
		X[0] = (random.nextInt((int) numUnitsX / 2) + (int) numUnitsX / 4) * UNIT_SIZE; 
		Y[0] = (random.nextInt((int) numUnitsY / 2) + (int) numUnitsY / 4) * UNIT_SIZE;
		for (int i = 1; i < snakeLength; i++) {
			X[i] = X[0];
			Y[i] = Y[0];
		}
		
		createGift();
		timer = new Timer(DELAY_TIME, this);
		timer.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawPanel(g);
		addWall(g);
	}
	
	public void addWall(Graphics g) {
		g.setColor(Color.MAGENTA);
		for (int i = 0; i < wall.length; i++) {
			for (int j = 0; j < wall[0].length; j++) {
				if (wall[i][j]) {
					g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
				}
			}
		}
	}
	
	public void drawPanel(Graphics g) {
		if (!moving) {
			stop(g);
			timer.stop();
		}
		
		g.setColor(Color.blue);
		g.fillOval(giftX, giftY, UNIT_SIZE, UNIT_SIZE);
		
		g.setColor(Color.red);
		g.fillRect(X[0], Y[0], UNIT_SIZE, UNIT_SIZE);
		for (int i = 1; i < snakeLength; i++) {
			g.setColor(Color.yellow);
			g.fillRect(X[i], Y[i], UNIT_SIZE, UNIT_SIZE);
		}
		displayScore(g);
	}
	
	public void createGift( ) {
		giftX = random.nextInt((int) PANEL_WIDTH / UNIT_SIZE); 
		giftY = random.nextInt((int) PANEL_HEIGHT / UNIT_SIZE);
		if (wall[giftX][giftY])
			createGift();
		giftX *= UNIT_SIZE;
		giftY *= UNIT_SIZE;
	}
	
	public void nextMove() {
		for (int i = snakeLength; i > 0; i--) {
			X[i] = X[i-1];
			Y[i] = Y[i-1];
		}
		if (direction == 'U') {
			Y[0] -= UNIT_SIZE;
		} else if (direction == 'D') {
			Y[0] += UNIT_SIZE;
		} else if (direction == 'R') {
			X[0] += UNIT_SIZE;
		} else {
			X[0] -= UNIT_SIZE;
		}
	}
	
	public void reachGift() {
		if (X[0] == giftX && Y[0] == giftY) {
			snakeLength += 1;
			createGift();
		}
	}
	
	public boolean hasCollision() {
		boolean hitEdge = X[0] >= PANEL_WIDTH || Y[0] >= PANEL_HEIGHT || X[0] < 0 || Y[0] < 0;
		boolean hitBody = false;
		boolean hitWall = false;
		if (!hitEdge) 
			hitWall = wall[X[0] / UNIT_SIZE][Y[0] / UNIT_SIZE];
		for (int i = 1; i < snakeLength; i++)
			if (X[0] == X[i] && Y[0] == Y[i]) {
				hitBody = true;
				break;
			}
		return !(hitEdge || hitBody || hitWall); 
	}
	
	public void displayScore(Graphics g) {
		g.setColor(Color.green);
		g.setFont(new Font("SANS_SERIF", Font.BOLD, 40));
		String score = "SCORE: " + (snakeLength - defaultLength);
		int offsetX = 12;
		int offsetY = 3;
		int scoreX = PANEL_WIDTH - offsetX * UNIT_SIZE;
		int scoreY = offsetY * UNIT_SIZE;
		g.drawString(score, scoreX, scoreY);
	}
	
	public void stop(Graphics g) {
		g.setColor(Color.red);
		g.setFont(new Font("SANS_SERIF", Font.BOLD, 80));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		String over = "GAME OVER";
		int overX = (int) (PANEL_WIDTH - metrics1.stringWidth(over)) / 2;
		int overY = (int) (PANEL_HEIGHT - g.getFont().getSize())/ 3;
		g.drawString(over, overX, overY);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (moving) {
			nextMove();
			reachGift();
			moving = hasCollision();
		}
		repaint();
	}
	
	// Custom KeyAdapter class
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_LEFT && direction != 'R') {
				direction = 'L';
			} else if (keyCode == KeyEvent.VK_RIGHT && direction != 'L') {
				direction = 'R';
			} else if (keyCode == KeyEvent.VK_UP && direction != 'D') {
				direction = 'U';
			} else if (keyCode == KeyEvent.VK_DOWN && direction != 'U') {
				direction = 'D';
			} else if (keyCode == KeyEvent.VK_ENTER) {
				if (tempStop) {
					timer.start();
				} else {
					timer.stop();
				}
				tempStop = !tempStop;
			}
		}
	}
	
	// Custom MouseAdapter class
	public class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			int x = ((int) e.getX() / UNIT_SIZE);
			int y = ((int) e.getY() / UNIT_SIZE);
		    wall[x][y] = true;
		}
	}
}
