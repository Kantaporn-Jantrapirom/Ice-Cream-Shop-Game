package Miniproject;

import java.awt.*;
import javax.swing.*;

public class IceCreamGame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GamePanel gamePanel;

    public IceCreamGame() {
        setTitle("Ice Cream Shop - Mini Project");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // 1. หน้า Start Screen
        JPanel startPanel = new JPanel() {
            private Image bgImage;

            {
                try {
                    // ดึงภาพ 1.png จาก Class Path หรือโฟลเดอร์งาน
                    ImageIcon icon = new ImageIcon("1.png");
                    if (icon.getImageLoadStatus() != MediaTracker.COMPLETE || icon.getIconWidth() < 0) {
                        icon = new ImageIcon(getClass().getResource("1.png"));
                    }
                    bgImage = icon.getImage();
                } catch (Exception e) {
                    System.out.println("ไม่สามารถโหลดภาพ 1.png ได้: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // สีสำรองกรณีหาภาพไม่เจอ หน้าจอจะได้ไม่ขาวเปล่า
                    g.setColor(new Color(135, 206, 235));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Tahoma", Font.BOLD, 24));
                    g.drawString("กดตรงนี้เพื่อเริ่มเกม (หาภาพ 1.png ไม่เจอ)", 250, 300);
                }
            }
        };
        // ให้ทั้งหน้าจอ Start เป็นปุ่มกด (ไม่ต้อง hardcode ตำแหน่งปุ่มอีกต่อไป
        // ดังนั้นเวลาเปลี่ยนรูปพื้นหลังใหม่ ก็ยังกดเริ่มเกมได้ทันทีโดยไม่ต้องปรับพิกัด)
        startPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(mainContainer, "GameScreen");
                gamePanel.startGame();
            }
        });

        // 2. หน้า Game Screen
        gamePanel = new GamePanel();

        mainContainer.add(startPanel, "StartScreen");
        mainContainer.add(gamePanel, "GameScreen");

        add(mainContainer);
        cardLayout.show(mainContainer, "StartScreen");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new IceCreamGame().setVisible(true);
        });
    }
}