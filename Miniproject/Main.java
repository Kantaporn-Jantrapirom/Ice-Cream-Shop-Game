package Miniproject;

import javax.swing.SwingUtilities;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IceCreamGame game = new IceCreamGame();
            game.setResizable(false); 
            game.setLocationRelativeTo(null); 
            game.setVisible(true);
        });
    }
}