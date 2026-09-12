import javax.swing.*;

public class GameFrame extends JFrame {

    //creates the dimensions and window format for the game

    GameFrame(){
        this.add(new GamePanel()); //shortcut to creating an instance of GamePanel
        this.setTitle("Snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
