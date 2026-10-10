package Miniproject;

import javax.swing.*;

public class IceCreamGame extends JFrame {

    public IceCreamGame() {
        setTitle("Ice Cream Shop - Mini Project");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        
        add(new GamePanel());
    }

    public static void main(String[] args) {
        new IceCreamGame().setVisible(true);
    }
}