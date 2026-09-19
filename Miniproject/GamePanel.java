package Miniproject;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class GamePanel extends JPanel {
    public static final int STATE_MENU = 0;
    public static final int STATE_PLAYING = 1;
    public static final int STATE_LEVEL_WIN = 2;
    public static final int STATE_LEVEL_LOSE = 3;
    private static final String BGM_LEVEL_1 = "bgm.wav";
    private static final String BGM_LEVEL_2 = "bgm2.wav";
    private static final String BGM_LEVEL_3 = "bgm3.wav";
    private int currentState = STATE_MENU;

    private Image bgImage;       
    private Image bgImageLevel2; 
    private Image bgImageLevel3; 
    private Image menuBgImage;
    private Image imgHeart;
    private Image imgOrderBubble;
    private Image counterImage;       
    private Image counterImageLevel2; 
    private Image counterImageLevel3; 
    private Image imgCone, imgCup;
    private Image imgIceStrawberry, imgIceChoco, imgIceVanilla, imgIceMatcha, imgIceMint, imgIceThaiTea; 
    private Image imgFruitBanana, imgFruitStrawberry, imgFruitBlueberry, imgFruitCherry, imgFruitApple, imgFruitOrange;
    private Image imgToppingNuts, imgToppingRainbow, imgToppingChocoChip, imgToppingJelly;
    private Image imgSauceChoco, imgSauceStrawberry, imgSauceGrape, imgSauceCaramel;
    private Image imgInfoPanel;      
    private Image imgLosePanel;      
    private Image imgWinPanel;       
    private Image imgFinalWinPanel;  
    private Image[] customerImages = new Image[6];       
    private Image[] customerImagesLevel2 = new Image[6];  
    private Image[] customerImagesLevel3 = new Image[6];  

    private CustomerData[] customers = new CustomerData[4];

    private String currentContainer = "";
    private String currentFlavor = "";
    private String currentFruit = "";
    private String currentTopping = "";
    private String currentSauce = "";

    private double totalMoney = 0.0;
    private Timer gameLoopTimer;
    private Random random = new Random();

    private boolean gameStarted = false;
    private boolean isStartHovered = false;
    private long nextSpawnTime = 0;

    private int currentLevel = 1;                       
    private static final int MAX_LEVEL = 3;
    private int customersServed = 0;                    
    private static final int TARGET_CUSTOMERS = 15;      
    private long levelStartTime = 0;
    private static final long LEVEL_DURATION_MS = 5 * 60 * 1000L; 

    public static class SoundManager {
        private static Clip bgmClip;

        public static void playSound(String fileName) {
            new Thread(() -> {
                try {
                    URL soundUrl = SoundManager.class.getResource("/Miniproject/" + fileName);
                    if (soundUrl == null) {
                        soundUrl = SoundManager.class.getResource("/" + fileName);
                    }
                    if (soundUrl != null) {
                        AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundUrl);
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioInput);
                        clip.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        public static void playBGM(String fileName) {
            stopBGM();
            new Thread(() -> {
                try {
                    URL soundUrl = SoundManager.class.getResource("/Miniproject/" + fileName);
                    if (soundUrl == null) {
                        soundUrl = SoundManager.class.getResource("/" + fileName);
                    }
                    if (soundUrl != null) {
                        AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundUrl);
                        bgmClip = AudioSystem.getClip();
                        bgmClip.open(audioInput);
                        bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                        bgmClip.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        public static void stopBGM() {
            if (bgmClip != null && bgmClip.isRunning()) {
                bgmClip.stop();
                bgmClip.close();
            }
        }
    }

    private static class CustomerData {
        Image customerImg;
        String reqContainer, reqFlavor, reqFruit, reqTopping, reqSauce;
        int hearts = 5;
        long lastPatienceTick;

        CustomerData(Image img, String container, String flavor, String fruit, String topping, String sauce) {
            this.customerImg = img;
            this.reqContainer = container;
            this.reqFlavor = flavor;
            this.reqFruit = fruit;
            this.reqTopping = topping;
            this.reqSauce = sauce;
            this.lastPatienceTick = System.currentTimeMillis();
        }
    }

    public GamePanel() {
        setPreferredSize(new Dimension(1000, 750));

        bgImage = loadImage("bg.png");
        bgImageLevel2 = loadImage("bg2.png");
        bgImageLevel3 = loadImage("bg3.png");
        menuBgImage = loadImage("1.png");
        imgHeart = loadImage("19.png");
        imgCone = loadImage("10.png");
        counterImage = loadImage("27.png");
        counterImageLevel2 = loadImage("33.png");
        counterImageLevel3 = loadImage("44.png");
        imgOrderBubble = loadImage("28.png");
        imgCup = loadImage("11.png");

        imgIceStrawberry = loadImage("17.png");
        imgIceChoco = loadImage("16.png");
        imgIceVanilla = loadImage("18.png");
        imgIceMatcha = loadImage("15.png");
        imgIceThaiTea = loadImage("37.png");

        imgFruitBanana = loadImage("8.png");
        imgFruitStrawberry = loadImage("7.png");
        imgFruitBlueberry = loadImage("9.png");
        imgFruitCherry = loadImage("6.png");

        imgToppingNuts = loadImage("12.png");
        imgToppingRainbow = loadImage("26.png");

        imgSauceChoco = loadImage("14.png");
        imgSauceStrawberry = loadImage("13.png");

        imgFruitApple = loadImage("35.png");
        imgToppingChocoChip = loadImage("36.png");
        imgSauceGrape = loadImage("34.png");

        imgIceMint = loadImage("46.png");
        imgFruitOrange = loadImage("48.png");
        imgToppingJelly = loadImage("47.png");
        imgSauceCaramel = loadImage("45.png");

        imgInfoPanel = loadImage("32.png");
        imgLosePanel = loadImage("30.png");
        imgWinPanel = loadImage("31.png");
        imgFinalWinPanel = loadImage("29.png");

        for (int i = 0; i < 6; i++) {
            customerImages[i] = loadImage((20 + i) + ".png");
        }

        String[] lvl2CustomerFiles = {"38", "39", "40", "41", "42", "43"};
        for (int i = 0; i < lvl2CustomerFiles.length; i++) {
            customerImagesLevel2[i] = loadImage(lvl2CustomerFiles[i] + ".png");
        }

        String[] lvl3CustomerFiles = {"49", "50", "51", "52", "53", "54"};
        for (int i = 0; i < lvl3CustomerFiles.length; i++) {
            customerImagesLevel3[i] = loadImage(lvl3CustomerFiles[i] + ".png");
        }

        nextSpawnTime = Long.MAX_VALUE;

        gameLoopTimer = new Timer(100, e -> {
            if (currentState != STATE_PLAYING) return;

            long now = System.currentTimeMillis();

            long elapsed = now - levelStartTime;
            if (elapsed >= LEVEL_DURATION_MS) {
                if (customersServed >= TARGET_CUSTOMERS) {
                    currentState = STATE_LEVEL_WIN;
                } else {
                    currentState = STATE_LEVEL_LOSE;
                }
                repaint();
                return;
            }

            boolean hasEmptySlot = false;
            for (CustomerData c : customers) {
                if (c == null) {
                    hasEmptySlot = true;
                    break;
                }
            }

            if (hasEmptySlot && now >= nextSpawnTime) {
                spawnCustomer();
                scheduleNextSpawn();
            }

            for (int i = 0; i < customers.length; i++) {
                CustomerData c = customers[i];
                if (c != null) {
                    if (now - c.lastPatienceTick > 6000) {
                        c.hearts--;
                        c.lastPatienceTick = now;
                        if (c.hearts <= 0) {
                            customers[i] = null;
                            scheduleNextSpawnAfterLeave();
                        }
                    }
                }
            }
            repaint();
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentState == STATE_MENU) {
                    boolean hover = getStartButtonBounds().contains(e.getPoint());
                    if (hover != isStartHovered) {
                        isStartHovered = hover;
                        repaint();
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SoundManager.playSound("click.wav");
                int x = e.getX();
                int y = e.getY();
                
                if (currentState == STATE_MENU) {
                    if (getStartButtonBounds().contains(x, y)) {
                        startGame();
                    }
                    repaint();
                    return;
                }

                if (currentState == STATE_LEVEL_LOSE) {
                    if (getSignButtonBounds().contains(x, y)) {
                        retryLevel();
                    }
                    repaint();
                    return;
                }

                if (currentState == STATE_LEVEL_WIN) {
                    if (getSignButtonBounds().contains(x, y)) {
                        nextLevel();
                    }
                    repaint();
                    return;
                }

                if (currentState != STATE_PLAYING) {
                    return;
                }

                if (x >= 28 && x <= 173 && y >= 666 && y <= 730) {
                    resetCurrentIceCream();
                    repaint();
                    return;
                }

                if (x >= 680 && x <= 760 && y >= 500 && y <= 650) {
                    if (!currentContainer.isEmpty()) { repaint(); return; }
                    currentContainer = (y <= 575) ? "Cone" : "Cup";
                    repaint();
                    return;
                }

                if (currentLevel == 2) {
                    if ((y >= 558 && y <= 587 && x >= 335 && x <= 603) ||
                        (y >= 605 && y <= 635 && x >= 327 && x <= 506)) {
                
                        if (currentContainer.isEmpty() || !currentFlavor.isEmpty()) { repaint(); return; }
                        if (y >= 558 && y <= 587) {
                            if (x >= 335 && x <= 408) currentFlavor = "Strawberry";
                            else if (x >= 434 && x <= 506) currentFlavor = "Chocolate";
                            else if (x >= 531 && x <= 603) currentFlavor = "Thaitea";
                        } else if (y >= 605 && y <= 635) {
                            if (x >= 327 && x <= 401) currentFlavor = "Vanilla";
                            else if (x >= 431 && x <= 506) currentFlavor = "Matcha";
                        } 
                        repaint();
                        return;
                    }

                    if (currentContainer.isEmpty() || currentFlavor.isEmpty()) {
                        repaint();
                        return;
                    }

                    if ((y >= 547 && y <= 580 && ((x >= 59 && x <= 133) || (x >= 150 && x <= 225) || (x >= 219 && x <= 294))) ||
                        (y >= 590 && y <= 625 && ((x >= 35 && x <= 113) || (x >= 133 && x <= 203)))) {

                        if (!currentFruit.isEmpty()) { repaint(); return; }
                        
                        if (y >= 547 && y <= 580) {
                            if (x >= 47 && x <= 124) currentFruit = "Banana";
                            else if (x >= 134 && x <= 211) currentFruit = "Strawberry";
                            else if (x >= 217 && x <= 296) currentFruit = "Apple";
                        } else if (y >= 588 && y <= 623) {
                            if (x >= 27 && x <= 98) currentFruit = "Blueberry";
                            else if (x >= 113 && x <= 185) currentFruit = "Cherry";
                        }
                        repaint();
                        return;
                    }
                    if ((y >= 550 && y <= 580 && x >= 756 && x <= 897) ||
                        (y >= 588 && y <= 620 && x >= 855 && x <= 926)) {

                        if (!currentTopping.isEmpty()) { repaint(); return; }
                        
                        if (y >= 550 && y <= 580) {
                            if (x >= 831 && x <= 897) currentTopping = "Nuts";
                            else if (x >= 756 && x <= 825) currentTopping = "Choco Chip";
                        } else if (y >= 588 && y <= 620) {
                            if (x >= 855 && x <= 926) currentTopping = "Rainbow";
                        }
                        repaint();
                        return;
                    }
                    if ((y >= 477 && y <= 631 && x >= 899 && x <= 992)) {
                        if (!currentSauce.isEmpty()) { repaint(); return; }
                        if (y >= 476 && y<= 540) {
                            if (x >= 901 && x <= 935) currentSauce = "Choco Sauce";
                        } else if (y >= 541 && y<= 570) {
                            if (x >= 920 && x <= 960) currentSauce = "Strawberry Sauce";
                        } else if (y >= 572 && y<= 631) {
                            if (x >= 943 && x <= 990) currentSauce = "Blueberry Sauce";
                        }
                        repaint();
                        return;
                    }
                    
                } else if (currentLevel == 3) {
                    if (!currentContainer.isEmpty() && currentFlavor.isEmpty()) {
                        if (y >= 558 && y <= 587 && x >= 335 && x <= 408) {
                            currentFlavor = "Strawberry"; repaint(); return;
                        }
                        if (y >= 558 && y <= 587 && x >= 434 && x <= 506) {
                            currentFlavor = "Chocolate"; repaint(); return;
                        }
                        if (y >= 558 && y <= 587 && x >= 531 && x <= 603) {
                            currentFlavor = "Thaitea"; repaint(); return;
                        }
                        if (y >= 605 && y <= 635 && x >= 327 && x <= 401) {
                            currentFlavor = "Vanilla"; repaint(); return;
                        }
                        if (y >= 605 && y <= 635 && x >= 431 && x <= 506) {
                            currentFlavor = "Matcha"; repaint(); return;
                        }
                        if (y >= 605 && y <= 635 && x >= 533 && x <= 603) {
                            currentFlavor = "Mint"; repaint(); return;
                        }
                    }

                    if (currentContainer.isEmpty() || currentFlavor.isEmpty()) {
                        repaint();
                        return;
                    }

                    if ((y >= 547 && y <= 580 && x >= 3 && x <= 312) ||
                        (y >= 588 && y <= 623 && x >= 3 && x <= 312)) {

                        if (!currentFruit.isEmpty()) { repaint(); return; }
                        
                        if (y >= 547 && y <= 580) {
                            if (x >= 47 && x <= 124) currentFruit = "Banana";
                            else if (x >= 134 && x <= 211) currentFruit = "Strawberry";
                            else if (x >= 217 && x <= 296) currentFruit = "Apple";
                        } else if (y >= 588 && y <= 623) {
                            if (x >= 27 && x <= 98) currentFruit = "Blueberry";
                            else if (x >= 113 && x <= 185) currentFruit = "Cherry";
                            else if (x >= 199 && x <= 275) currentFruit = "Orange";
                        }
                        repaint();
                        return;
                    }
                    if (currentTopping.isEmpty()) {
                        if (y >= 550 && y <= 580 && x >= 831 && x <= 897) {
                            currentTopping = "Nuts"; repaint(); return;
                        }
                        if (y >= 550 && y <= 580 && x >= 756 && x <= 825) {
                            currentTopping = "Choco Chip"; repaint(); return;
                        }
                        if (y >= 588 && y <= 620 && x >= 855 && x <= 926) {
                            currentTopping = "Rainbow"; repaint(); return;
                        }
                        if (y >= 588 && y <= 620 && x >= 769 && x <= 839) {
                            currentTopping = "Jelly"; repaint(); return;
                        }
                    }
                    if (currentSauce.isEmpty()) {
                        if (y >= 474 && y <= 515 && x >= 860 && x <= 920) {
                            currentSauce = "Choco Sauce"; repaint(); return;
                        }
                        if (y >= 516 && y <= 560 && x >= 895 && x <= 940) {
                            currentSauce = "Strawberry Sauce"; repaint(); return;
                        }
                        if (y >= 561 && y <= 600 && x >= 919 && x <= 951) {
                            currentSauce = "Blueberry Sauce"; repaint(); return;
                        }
                        if (y >= 601 && y <= 641 && x >= 941 && x <= 985) {
                            currentSauce = "Caramel Sauce"; repaint(); return;
                        }
                    }
                } else {
                    if ((y >= 558 && y <= 587 && ((x >= 293 && x <= 362) || (x >= 410 && x <= 477))) ||
                        (y >= 605 && y <= 635 && ((x >= 279 && x <= 349) || (x >= 397 && x <= 468)))) {
                        if (currentContainer.isEmpty() || !currentFlavor.isEmpty()) { repaint(); return; }
                        if (y >= 558 && y <= 587) {
                            if (x >= 293 && x <= 362) currentFlavor = "Strawberry";
                            else if (x >= 410 && x <= 477) currentFlavor = "Chocolate";
                        } else if (y >= 605 && y <= 635) {
                            if (x >= 279 && x <= 349) currentFlavor = "Vanilla";
                            else if (x >= 397 && x <= 468) currentFlavor = "Matcha";
                        }
                        repaint();
                        return;
                    }

                    if (currentContainer.isEmpty() || currentFlavor.isEmpty()) {
                        repaint();
                        return;
                    }

                    if ((y >= 550 && y <= 578 && ((x >= 59 && x <= 133) || (x >= 150 && x <= 225))) ||
                        (y >= 590 && y <= 625 && ((x >= 35 && x <= 113) || (x >= 133 && x <= 203)))) {
                        if (!currentFruit.isEmpty()) { repaint(); return; }
                        if (y >= 550 && y <= 578) {
                            if (x >= 59 && x <= 133) currentFruit = "Banana";
                            else if (x >= 150 && x <= 225) currentFruit = "Strawberry";
                        } else if (y >= 590 && y <= 625) {
                            if (x >= 35 && x <= 113) currentFruit = "Blueberry";
                            else if (x >= 133 && x <= 203) currentFruit = "Cherry";
                        }
                    }

                    if ((y >= 550 && y <= 580 && x >= 813 && x <= 883) ||
                        (y >= 593 && y <= 620 && x >= 843 && x <= 915)) {
                        if (!currentTopping.isEmpty()) { repaint(); return; }
                        if (y >= 550 && y <= 580 && x >= 813 && x <= 883) currentTopping = "Nuts";
                        else if (y >= 593 && y <= 620 && x >= 843 && x <= 915) currentTopping = "Rainbow";
                    }

                    if ((y >= 496 && y <= 585 && x >= 914 && x <= 951) ||
                        (y >= 531 && y <= 625 && x >= 940 && x <= 983)) {
                        if (!currentSauce.isEmpty()) { repaint(); return; }
                        if (y >= 496 && y <= 585 && x >= 914 && x <= 951) currentSauce = "Choco Sauce";
                        else if (y >= 531 && y <= 625 && x >= 940 && x <= 983) currentSauce = "Strawberry Sauce";
                    }
                }

                if (y >= 150 && y <= 480) {
                    checkServeCustomer(x);
                }

                repaint();
            }
        });

        currentState = STATE_MENU;
    }

    public Rectangle getStartButtonBounds() {
        int panelW = getWidth() > 0 ? getWidth() : 1000;
        int imgW = 380;
        int imgH = 480;
        int imgX = (panelW - imgW) / 2;
        int imgY = 100;

        int btnW = 200;
        int btnH = 90;

        int btnX = imgX + (imgW - btnW) / 2;
        int btnY = imgY + 200;

        return new Rectangle(btnX, btnY, btnW, btnH);
    }

    private static final int SIGN_W = 640;
    private static final int SIGN_H = 452; 

    private int getSignX() {
        int panelW = getWidth() > 0 ? getWidth() : 1000;
        return (panelW - SIGN_W) / 2;
    }

    private int getSignY() {
        int panelH = getHeight() > 0 ? getHeight() : 750;
        return (panelH - SIGN_H) / 2;
    }

    private Rectangle getSignButtonBounds() {
        int signX = getSignX();
        int signY = getSignY();
        int bx = signX + (int) (SIGN_W * 0.142);
        int by = signY + (int) (SIGN_H * 0.495);
        int bw = (int) (SIGN_W * 0.739);
        int bh = (int) (SIGN_H * 0.240);
        return new Rectangle(bx, by, bw, bh);
    }

    private Image loadImage(String filename) {
        String[] paths = {
            filename,
            "src/" + filename,
            "src/Miniproject/" + filename,
            "Miniproject/" + filename,
            "bin/" + filename,
            "bin/Miniproject/" + filename
        };

        for (String p : paths) {
            File f = new File(p);
            if (f.exists()) {
                return new ImageIcon(p).getImage();
            }
            URL url = getClass().getResource("/" + p);
            if (url != null) return new ImageIcon(url).getImage();
            url = getClass().getResource(p);
            if (url != null) return new ImageIcon(url).getImage();
        }

        try {
            URL resource = getClass().getClassLoader().getResource(filename);
            if (resource != null) return new ImageIcon(resource).getImage();

            resource = getClass().getClassLoader().getResource("Miniproject/" + filename);
            if (resource != null) return new ImageIcon(resource).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void startGame() {
        currentLevel = 1;
        startLevel();
    }

    private void startLevel() {
        currentState = STATE_PLAYING;
        gameStarted = true;

        if (currentLevel == 1) {
            SoundManager.playBGM("bgm.wav");
        } else if (currentLevel == 2) {
            SoundManager.playBGM("bgm2.wav");
        } else if (currentLevel == 3) {
            SoundManager.playBGM("bgm3.wav");
        }

        java.util.Arrays.fill(customers, null);
        totalMoney = 0.0;
        customersServed = 0;
        resetCurrentIceCream();

        levelStartTime = System.currentTimeMillis();
        scheduleNextSpawn();

        if (gameLoopTimer != null && !gameLoopTimer.isRunning()) {
            gameLoopTimer.start();
        }

        repaint();
    }

    private void retryLevel() {
        startLevel();
    }

    private void nextLevel() {
        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            startLevel();
        } else {
            startGame();
        }
    }

    private void checkWinCondition() {
        if (currentState == STATE_PLAYING && customersServed >= TARGET_CUSTOMERS) {
            currentState = STATE_LEVEL_WIN;
        }
    }

    private int getSpawnMin() {
        if (currentLevel == 1) return 5000;
        if (currentLevel == 2) return 4000;
        return 2000;
    }

    private int getSpawnMax() {
        if (currentLevel == 1) return 8000;
        if (currentLevel == 2) return 6000;
        return 4000;
    }

    private Image getCurrentBgImage() {
        if (currentLevel == 2 && bgImageLevel2 != null) return bgImageLevel2;
        if (currentLevel == 3 && bgImageLevel3 != null) return bgImageLevel3;
        return bgImage;
    }

    private Image getCurrentCounterImage() {
        if (currentLevel == 2 && counterImageLevel2 != null) return counterImageLevel2;
        if (currentLevel == 3 && counterImageLevel3 != null) return counterImageLevel3;
        return counterImage;
    }

    private Image[] getCurrentCustomerImages() {
        if (currentLevel == 2 && customerImagesLevel2.length > 0) return customerImagesLevel2;
        if (currentLevel == 3 && customerImagesLevel3.length > 0) return customerImagesLevel3;
        return customerImages;
    }

    private void scheduleNextSpawn() {
        int min = getSpawnMin();
        int max = getSpawnMax();
        nextSpawnTime = System.currentTimeMillis() + min + random.nextInt(max - min + 1);
    }

    private void scheduleNextSpawnAfterLeave() {
        if (currentLevel == 2) {
            nextSpawnTime = System.currentTimeMillis() + 4000 + random.nextInt(2001);
        } else if (currentLevel == 3) {
            nextSpawnTime = System.currentTimeMillis() + 3000 + random.nextInt(2001);
        } else {
            scheduleNextSpawn();
        }
    }

    private String[] getFlavorsForLevel() {
        if (currentLevel == 1) return new String[]{"Strawberry", "Vanilla", "Chocolate", "Matcha"};
        if (currentLevel == 2) return new String[]{"Strawberry", "Vanilla", "Chocolate", "Matcha", "Thaitea"};
        return new String[]{"Strawberry", "Vanilla", "Chocolate", "Matcha", "Thaitea", "Mint"};
    }

    private String[] getFruitsForLevel() {
        if (currentLevel == 1) return new String[]{"", "Banana", "Strawberry", "Blueberry", "Cherry"};
        if (currentLevel == 2) return new String[]{"", "Banana", "Strawberry", "Blueberry", "Cherry", "Apple"};
        return new String[]{"", "Banana", "Strawberry", "Blueberry", "Cherry", "Apple", "Orange"};
    }

    private String[] getToppingsForLevel() {
        if (currentLevel == 1) return new String[]{"", "Nuts", "Rainbow"};
        if (currentLevel == 2) return new String[]{"", "Nuts", "Rainbow", "Choco Chip"};
        return new String[]{"", "Nuts", "Rainbow", "Choco Chip", "Jelly"};
    }

    private String[] getSaucesForLevel() {
        if (currentLevel == 1) return new String[]{"", "Choco Sauce", "Strawberry Sauce"};
        if (currentLevel == 2) return new String[]{"", "Choco Sauce", "Strawberry Sauce", "Blueberry Sauce"};
        return new String[]{"", "Choco Sauce", "Strawberry Sauce", "Blueberry Sauce", "Caramel Sauce"};
    }

    private void spawnCustomer() {
        if (currentState != STATE_PLAYING) return;

        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < customers.length; i++) {
            if (customers[i] == null) emptySlots.add(i);
        }

        if (!emptySlots.isEmpty()) {
            int targetSlot = emptySlots.get(random.nextInt(emptySlots.size()));

            String[] containers = {"Cone", "Cup"};
            String[] flavors = getFlavorsForLevel();
            String[] fruits = getFruitsForLevel();
            String[] toppings = getToppingsForLevel();
            String[] sauces = getSaucesForLevel();

            String c = containers[random.nextInt(containers.length)];
            String f = flavors[random.nextInt(flavors.length)];
            String fr = fruits[random.nextInt(fruits.length)];
            String t = toppings[random.nextInt(toppings.length)];
            String s = sauces[random.nextInt(sauces.length)];

            Image[] currentCustomerSet = getCurrentCustomerImages();
            Image randCustImg = currentCustomerSet[random.nextInt(currentCustomerSet.length)];
            customers[targetSlot] = new CustomerData(randCustImg, c, f, fr, t, s);

            SoundManager.playSound("come.wav");
        }
    }

    private void checkServeCustomer(int clickX) {
        if (currentFlavor.isEmpty() || currentContainer.isEmpty()) return;

        int index = -1;
        if (clickX >= 50 && clickX <= 250) index = 0;
        else if (clickX >= 251 && clickX <= 450) index = 1;
        else if (clickX >= 451 && clickX <= 650) index = 2;
        else if (clickX >= 651 && clickX <= 850) index = 3;

        if (index != -1 && index < customers.length && customers[index] != null) {
            CustomerData c = customers[index];

            boolean isCorrect = currentContainer.equalsIgnoreCase(c.reqContainer) &&
                                currentFlavor.equalsIgnoreCase(c.reqFlavor) &&
                                currentFruit.equalsIgnoreCase(c.reqFruit) &&
                                currentTopping.equalsIgnoreCase(c.reqTopping) &&
                                currentSauce.equalsIgnoreCase(c.reqSauce);

            double basePrice = 25.0;

            if (isCorrect) {
                SoundManager.playSound("success.wav");
                if (c.hearts >= 4) {
                    int tip = 10 + random.nextInt(21);
                    totalMoney += basePrice + tip;
                } else if (c.hearts >= 2) {
                    totalMoney += basePrice;
                } else if (c.hearts == 1) {
                    totalMoney += basePrice * 0.5;
                }

                customers[index] = null;
                customersServed++;
                resetCurrentIceCream();
                scheduleNextSpawnAfterLeave();
                checkWinCondition();
                repaint();

            } else {
                SoundManager.playSound("wrong.wav");

                if (totalMoney < 0) totalMoney = 0;

                customers[index] = null;
                resetCurrentIceCream();
                scheduleNextSpawnAfterLeave();
                checkWinCondition();
                repaint();
            }
        }
    }

    private void resetCurrentIceCream() {
        currentContainer = "";
        currentFlavor = "";
        currentFruit = "";
        currentTopping = "";
        currentSauce = "";
    }

    private Image getIngredientImage(String type, String name) {
        if (name == null || name.isEmpty()) return null;
        switch (type) {
            case "container":
                if (name.equals("Cup")) return imgCup;
                if (name.equals("Cone")) return imgCone;
                break;
            case "flavor":
                if (name.equals("Strawberry")) return imgIceStrawberry;
                if (name.equals("Chocolate")) return imgIceChoco;
                if (name.equals("Vanilla")) return imgIceVanilla;
                if (name.equals("Matcha")) return imgIceMatcha;
                if (name.equals("Mint")) return imgIceMint;
                if (name.equals("Thaitea")) return imgIceThaiTea;
                break;
            case "fruit":
                if (name.equals("Banana")) return imgFruitBanana;
                if (name.equals("Strawberry")) return imgFruitStrawberry;
                if (name.equals("Blueberry")) return imgFruitBlueberry;
                if (name.equals("Cherry")) return imgFruitCherry;
                if (name.equals("Apple")) return imgFruitApple;
                if (name.equals("Orange")) return imgFruitOrange;
                break;
            case "topping":
                if (name.equals("Nuts")) return imgToppingNuts;
                if (name.equals("Rainbow")) return imgToppingRainbow;
                if (name.equals("Choco Chip")) return imgToppingChocoChip;
                if (name.equals("Jelly")) return imgToppingJelly;
                break;
            case "sauce":
                if (name.equals("Choco Sauce")) return imgSauceChoco;
                if (name.equals("Strawberry Sauce")) return imgSauceStrawberry;
                if (name.equals("Blueberry Sauce")) return imgSauceGrape;
                if (name.equals("Caramel Sauce")) return imgSauceCaramel;
                break;
        }
        return null;
    }

    private int[] getTrayPosition() {
        if (currentLevel == 2) return new int[]{580, 530}; 
        if (currentLevel == 3) return new int[]{580, 530}; 
        return new int[]{580, 530};
    }

    private void drawCloudBubble(Graphics2D g2d, int x, int y, int width, int height) {
        if (imgOrderBubble != null) {
            g2d.drawImage(imgOrderBubble, x, y - 10, width + 200, height + 150, null);
        } else {
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x, y + 10, width - 20, height - 20);
            g2d.fillOval(x + 10, y, width - 30, height - 15);
            g2d.fillOval(x + 25, y + 10, width - 25, height - 20);

            g2d.fillOval(x + 20, y + height - 10, 15, 15);
            g2d.fillOval(x + 10, y + height + 2, 8, 8);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        if (currentState == STATE_MENU) {
            if (bgImage != null) {
                g2d.drawImage(bgImage, 0, 0, width, height, this);
            } else {
                g2d.setColor(new Color(255, 228, 225));
                g2d.fillRect(0, 0, width, height);
            }

            if (menuBgImage != null) {
                int imgW = 380;
                int imgH = 480;
                int imgX = (width - imgW) / 2;
                int imgY = 100;

                if (isStartHovered) {
                    g2d.drawImage(menuBgImage, imgX - 5, imgY - 5, imgW + 10, imgH + 10, this);
                } else {
                    g2d.drawImage(menuBgImage, imgX, imgY, imgW, imgH, this);
                }
            }
            return;
        }

        Image playingBg = getCurrentBgImage();
        if (playingBg != null) {
            g2d.drawImage(playingBg, 0, 0, width, height, this);
        }

        int[] positionsX = {100, 300, 500, 700};
        for (int i = 0; i < customers.length; i++) {
            CustomerData c = customers[i];
            if (c == null) continue;

            int cx = positionsX[i];
            int cy = 180;

            if (c.customerImg != null) {
                g2d.drawImage(c.customerImg, cx - 300, cy, 830, 750, this);
            } else {
                g2d.setColor(new Color(100, 150, 250));
                g2d.fillRect(cx - 40, cy + 50, 600, 550);
            }

            int bubbleX = cx;
            int bubbleY = cy - 40;
            drawCloudBubble(g2d, bubbleX, bubbleY, 125, 100);

            Image cImg = getIngredientImage("container", c.reqContainer);
            Image fImg = getIngredientImage("flavor", c.reqFlavor);
            Image frImg = getIngredientImage("fruit", c.reqFruit);
            Image tImg = getIngredientImage("topping", c.reqTopping);
            Image sImg = getIngredientImage("sauce", c.reqSauce);

            int imgX = bubbleX + 105;
            int imgY = bubbleY + 33;
            int imgSize = 60;

            if (cImg != null) { g2d.drawImage(cImg, imgX, imgY, imgSize, imgSize, this); imgX += 26; }
            if (fImg != null) { g2d.drawImage(fImg, imgX, imgY, imgSize, imgSize, this); imgX += 26; }
            if (frImg != null) { g2d.drawImage(frImg, imgX, imgY, imgSize, imgSize, this); imgX += 26; }
            imgX = bubbleX + 105;
            imgY = bubbleY + 70;
            if (tImg != null) { g2d.drawImage(tImg, imgX, imgY, imgSize, imgSize, this); imgX += 26; }
            if (sImg != null) { g2d.drawImage(sImg, imgX, imgY, imgSize, imgSize, this); }

            for (int h = 0; h < c.hearts; h++) {
                if (imgHeart != null) {
                    g2d.drawImage(imgHeart, cx - 30, cy + (h * 30) + 90, 100, 100, this);
                } else {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(cx, cy + (h * 30) + 40, 100, 100);
                }
            }
        }

        Image currentCounterImg = getCurrentCounterImage();
        if (currentCounterImg != null) {
            int counterY = 0;
            g2d.drawImage(currentCounterImg, 0, counterY, width, height - counterY, this);
        }

        int panelW = 340, panelH = 242;
        int panelX = width - panelW - 20;
        int panelY = -35;

        if (imgInfoPanel != null) {
            g2d.drawImage(imgInfoPanel, panelX, panelY, panelW, panelH, this);
        } else {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);
        }

        long remainingMs = LEVEL_DURATION_MS - (System.currentTimeMillis() - levelStartTime);
        if (remainingMs < 0) remainingMs = 0;
        int totalSec = (int) (remainingMs / 1000);
        int mm = totalSec / 60;
        int ss = totalSec % 60;
        String timeText = String.format("%02d:%02d", mm, ss);

        int valueX = panelX + (int) (panelW * 0.56);
        int timeY = panelY + (int) (panelH * 0.46);
        int moneyY = panelY + (int) (panelH * 0.55);
        int targetY = panelY + (int) (panelH * 0.646);

        g2d.setFont(new Font("Tahoma", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString(timeText, valueX, timeY);
        g2d.drawString("$" + String.format("%.2f", totalMoney), valueX, moneyY);
        g2d.drawString(customersServed + "/" + TARGET_CUSTOMERS, valueX, targetY);

        int[] trayPos = getTrayPosition();
        int tDrawX = trayPos[0];
        int tDrawY = trayPos[1];

        Image trayContainer = getIngredientImage("container", currentContainer);
        Image trayFlavor = getIngredientImage("flavor", currentFlavor);
        Image trayFruit = getIngredientImage("fruit", currentFruit);
        Image trayTopping = getIngredientImage("topping", currentTopping);
        Image traySauce = getIngredientImage("sauce", currentSauce);

        if (currentContainer.equalsIgnoreCase("Cup")) {
            if (trayContainer != null) {
                if (currentLevel == 2) {
                    g2d.drawImage(trayContainer, tDrawX + 5, tDrawY + 10, 150, 100, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(trayContainer, tDrawX + 2, tDrawY + 2, 150, 100, this);
                } else {
                    g2d.drawImage(trayContainer, tDrawX - 53, tDrawY, 150, 100, this);
                }
            }
            if (trayFlavor != null) {
                int scoopW = 100;
                int scoopH = 80;
                if (currentLevel == 2) {
                    g2d.drawImage(trayFlavor, tDrawX + 40, tDrawY + 5, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX + 20, tDrawY + 5, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX + 30, tDrawY - 5, scoopW, scoopH, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(trayFlavor, tDrawX + 38, tDrawY, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX + 18, tDrawY, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX + 28, tDrawY - 10, scoopW, scoopH, this);
                } else {
                    g2d.drawImage(trayFlavor, tDrawX - 40, tDrawY - 5, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX - 20, tDrawY - 5, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX - 30, tDrawY - 15, scoopW, scoopH, this);
                }
            }
            if (trayFruit != null) {
                if (currentLevel == 2) {
                    g2d.drawImage(trayFruit, tDrawX + 60, tDrawY + 10, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX + 40, tDrawY + 10, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX + 50, tDrawY, 60, 40, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(trayFruit, tDrawX + 58, tDrawY, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX + 38, tDrawY, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX + 48, tDrawY - 5, 60, 40, this);   
                } else {
                    g2d.drawImage(trayFruit, tDrawX - 20, tDrawY, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX, tDrawY, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX - 10, tDrawY - 10, 60, 40, this);   
                }
            }
            if (trayTopping != null) {
                if (currentLevel == 2) {
                    g2d.drawImage(trayTopping, tDrawX + 25, tDrawY, 110, 70, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(trayTopping, tDrawX + 25, tDrawY - 2, 110, 70, this);
                } else {
                    g2d.drawImage(trayTopping, tDrawX - 30, tDrawY - 8, 100, 60, this);
                }
            }
            if (traySauce != null) {
                if (currentLevel == 2) {
                    g2d.drawImage(traySauce, tDrawX + 25, tDrawY, 110, 70, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(traySauce, tDrawX + 28, tDrawY, 100, 60, this);
                } else {
                    g2d.drawImage(traySauce, tDrawX - 30, tDrawY - 10, 100, 60, this);
                }
            }
        } else {
            if (trayContainer != null) {
                if (currentLevel == 2) {
                    g2d.drawImage(trayContainer, tDrawX, tDrawY - 15, 150, 120, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(trayContainer, tDrawX, tDrawY - 15, 150, 120, this);
                } else {
                    g2d.drawImage(trayContainer, tDrawX - 55, tDrawY - 15, 150, 120, this);
                }
            }
            if (trayFlavor != null) {
                int scoopW = 100;
                int scoopH = 80;
                if (currentLevel == 2) {
                    g2d.drawImage(trayFlavor, tDrawX + 35, tDrawY - 30, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX + 15, tDrawY - 30, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX + 25, tDrawY - 40, scoopW, scoopH, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(trayFlavor, tDrawX + 35, tDrawY - 30, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX + 15, tDrawY - 30, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX + 25, tDrawY - 40, scoopW, scoopH, this);
                } else {
                    g2d.drawImage(trayFlavor, tDrawX - 40, tDrawY - 30, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX - 20, tDrawY - 30, scoopW, scoopH, this);
                    g2d.drawImage(trayFlavor, tDrawX - 30, tDrawY - 40, scoopW, scoopH, this);
                }
            }
            if (trayFruit != null) {
                if (currentLevel == 2) {
                    g2d.drawImage(trayFruit, tDrawX + 55, tDrawY - 25, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX + 35, tDrawY - 25, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX + 45, tDrawY - 35, 60, 40, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(trayFruit, tDrawX + 55, tDrawY - 25, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX + 35, tDrawY - 25, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX + 45, tDrawY - 35, 60, 40, this);
                } else {
                    g2d.drawImage(trayFruit, tDrawX - 20, tDrawY - 25, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX, tDrawY - 25, 60, 40, this);
                    g2d.drawImage(trayFruit, tDrawX - 10, tDrawY - 35, 60, 40, this);
                }
            }
            if (trayTopping != null) {
                if (currentLevel == 2) {
                    g2d.drawImage(trayTopping, tDrawX + 25, tDrawY - 33, 100, 60, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(trayTopping, tDrawX + 25, tDrawY - 33, 100, 60, this);
                } else {
                    g2d.drawImage(trayTopping, tDrawX - 30, tDrawY - 33, 100, 60, this);
                }
            }
            if (traySauce != null) {
                if (currentLevel == 2) {
                    g2d.drawImage(traySauce, tDrawX + 25, tDrawY - 35, 100, 60, this);
                } else if (currentLevel == 3) {
                    g2d.drawImage(traySauce, tDrawX + 25, tDrawY - 35, 100, 60, this);
                } else {
                    g2d.drawImage(traySauce, tDrawX - 30, tDrawY - 35, 100, 60, this);
                }
            }
        }

        if (currentState == STATE_LEVEL_LOSE) {
            drawSignOverlay(g2d, width, height, imgLosePanel, "หมดเวลา! ลูกค้าไม่ครบ / Try Again");

        } else if (currentState == STATE_LEVEL_WIN) {
            if (currentLevel < MAX_LEVEL) {
                drawSignOverlay(g2d, width, height, imgWinPanel, "ผ่านด่านแล้ว! / Next Level");
            } else {
                drawSignOverlay(g2d, width, height, imgFinalWinPanel, "ผ่านครบทุกด่าน! / Start Again");
            }
        }
    }

    private void drawSignOverlay(Graphics2D g2d, int width, int height, Image signImg, String fallbackText) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, width, height);

        int signX = getSignX();
        int signY = getSignY();

        if (signImg != null) {
            g2d.drawImage(signImg, signX, signY, SIGN_W, SIGN_H, this);
        } else {
            g2d.setColor(new Color(222, 184, 135));
            g2d.fillRoundRect(signX, signY, SIGN_W, SIGN_H, 30, 30);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Tahoma", Font.BOLD, 15));
            g2d.drawString(fallbackText, signX + 30, signY + SIGN_H / 2);
        }
    }
}