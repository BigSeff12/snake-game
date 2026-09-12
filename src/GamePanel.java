import java.awt.*;          //imported libraries that allow for specific predefined datatypes
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    // how big objects will be in our game
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;   //
    static final int DELAY = 75;
    //controls speed of the game
    final int x[] = new int[GAME_UNITS];
    //holds the x coordinates of the snake body
    final int y[] = new int[GAME_UNITS];
    //holds the y coordinates of the snake body
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    //initial direction of snake
    boolean running = false;
    boolean showHomeScreen = true;
    Timer timer;
    Random random;
    JButton playAgainButton;
    private Image startImage;
    private Image gameOverImage;
    // Image object to hold the start screen image


   //creates display outline for the game
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);  // Disable layout manager for absolute positioning
        this.addKeyListener(new MyKeyAdapter());
        loadStartImage();
        loadGameOverImage();// Load the start screen image
        createPlayAgainButton();
    }


    public void startGame() {
        newApple();  // Generate a new apple
        running = true;  // Set the game running state to true
        if (timer != null && timer.isRunning()) {
            timer.stop();  // Stop the timer if it's already running
        }
        timer = new Timer(DELAY, this);  // Create a new timer
        timer.start();  // Start the timer
    }


    // Paint the components, adds colors to items, words, and objects in game
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // If there's a start image, display it
        if (startImage != null && showHomeScreen) {
            g.drawImage(startImage, 0, 0, getWidth(), getHeight(), this);  // Resize to fit panel
        }

        // Show the home screen UI (if showHomeScreen is true)
        if (showHomeScreen) {
            showHomeScreen(g);
            drawCopyright(g);
        } else {
            draw(g);  // Draw the game if the flag is false
        }
    }


    // Draw the game
    public void draw(Graphics g) {
        if (running) {
            // Draw grid (optional, just for visual effect)
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);  // Head color
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.setColor(new Color(48, 115, 27));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Score display
            g.setColor(Color.RED);
            g.setFont(new Font("Times New Roman", Font.PLAIN, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }


    // Load the start image (GIF or PNG)
    private void loadStartImage() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/snake attack.gif"));
        startImage = icon.getImage();
    }


    // Load the Game Over snake GIF
    private void loadGameOverImage() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/die-snake.gif"));
        gameOverImage = icon.getImage();
    }



    // Show the home screen with text
    public void showHomeScreen(Graphics g) {
        String welcomeMessage = "Welcome to Snake Game!";
        String instructionMessage = "Press Enter to Start";

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(welcomeMessage, (SCREEN_WIDTH - metrics.stringWidth(welcomeMessage)) / 2, SCREEN_HEIGHT / 3);

        g.setFont(new Font("Arial", Font.PLAIN, 30));
        FontMetrics instructionMetrics = getFontMetrics(g.getFont());
        g.drawString(instructionMessage, (SCREEN_WIDTH - instructionMetrics.stringWidth(instructionMessage)) / 2, SCREEN_HEIGHT / 2 + 50);
    }

    // New method to draw the copyright message
    private void drawCopyright(Graphics g) {
        String copyrightText = "© 2024 Seth Games incorperated";

        g.setColor(Color.GREEN); // Set the color of the text to white
        g.setFont(new Font("Arial", Font.PLAIN, 12)); // Set a small font for the copyright
        FontMetrics metrics = getFontMetrics(g.getFont());

        // Draw the copyright text at the bottom right
        int xPosition = SCREEN_WIDTH - metrics.stringWidth(copyrightText) - 10; // 10px padding from the right
        int yPosition = SCREEN_HEIGHT - 10; // 10px padding from the bottom
        g.drawString(copyrightText, xPosition, yPosition);
    }


    // Create new apple at random position
    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE; //x coordinates of random apple
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;// Y coordinates of random apple
    }

    // Move the snake
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    // Check if the snake eats an apple
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    // Check for collisions
    public void checkCollisions() {
        // Check for collisions with the body
        for (int i = bodyParts - 1; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // Check for collisions with walls
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    // Handle game over graphics and display unit
    public void gameOver(Graphics g) {
        // Dynamic font sizing based on screen width
        int fontSize = Math.min(SCREEN_WIDTH / 20, 60);  // Adjust the font size
        g.setColor(Color.RED);
        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, fontSize));

        // Prepare the message and wrap it if necessary
        String gameOverText = "C'mon That's Your Best LOL? Game Over";

        // Calculate text width and height
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(gameOverText);
        int xPosition = (SCREEN_WIDTH - textWidth) / 2;  // Center horizontally
        int yPosition = SCREEN_HEIGHT / 3;  // Position vertically

        // Draw the "Game Over" text
        g.drawString(gameOverText, xPosition, yPosition);

        // Draw the score below the "Game Over" message
        String scoreText = "Score: " + applesEaten;
        int scoreTextWidth = metrics.stringWidth(scoreText);
        g.drawString(scoreText, (SCREEN_WIDTH - scoreTextWidth) / 2, yPosition + fontSize + 20);

        // Display game over image or gif
        if (gameOverImage != null) {
            g.drawImage(gameOverImage, (SCREEN_WIDTH - 200) / 2, yPosition + 100, 200, 200, this);
        }

        // Show Play Again button after game over
        playAgainButton.setVisible(true);

        // Add copyright notice at the bottom right
        String copyrightText = "© 2024 Seth Games Incorporated"; // Customize this text as needed
        g.setColor(Color.GREEN); // Set color for the copyright text (light gray)
        g.setFont(new Font("Arial", Font.PLAIN, 14)); // Smaller font size for copyright text

        // Calculate the position for the copyright text
        int copyrightTextWidth = metrics.stringWidth(copyrightText);
        int copyrightX = SCREEN_WIDTH - copyrightTextWidth - 10;  // 10 pixels padding from the right
        int copyrightY = SCREEN_HEIGHT - 10;  // 10 pixels padding from the bottom

        // Draw the copyright text
        g.drawString(copyrightText, copyrightX, copyrightY);

    }



    // Action performed on timer tick
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    // Handle key events
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (showHomeScreen) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    showHomeScreen = false;
                    startGame();
                }
            } else {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                }
            }
        }
    }

    // Create Play Again button after game over
    private void createPlayAgainButton() {
        playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds(SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + 100, 200, 50);
        playAgainButton.setFont(new Font("Ink Free", Font.BOLD, 20));
        playAgainButton.setBackground(Color.RED);
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setVisible(false); // Initially hidden

        // Add action listener for the play again functionality
        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        // Add the button to the panel
        this.add(playAgainButton);
    }

    // Reset the game state
    private void resetGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;

        // Reset snake position
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        newApple();
        playAgainButton.setVisible(false);

        this.revalidate();
        this.repaint();
        timer.restart();
        this.requestFocusInWindow();
    }
}
