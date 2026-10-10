package Miniproject;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private long nextSpawnTime = Long.MAX_VALUE;

    private int currentLevel = 1;                       
    private static final int MAX_LEVEL = 3;
    private int customersServed = 0;                    
    private static final int TARGET_CUSTOMERS = 5;      
    private long levelStartTime = 0;
    private static final long LEVEL_DURATION_MS = 5 * 60 * 1000L; 

    // ตำแหน่งถาดวางไอศกรีม (เดิมเป็นเมธอด getTrayPosition ที่คืนค่าเดิมทุกด่าน)
    private static final int TRAY_X = 580;
    private static final int TRAY_Y = 530;

    
    // minLevel = ด่านต่ำสุดที่วัตถุดิบนี้เริ่มใช้ได้
    private static final class Zone {
        final String name;
        final int x1, y1, x2, y2, minLevel;

        Zone(String name, int x1, int y1, int x2, int y2) {
            this(name, x1, y1, x2, y2, 1);
        }

        Zone(String name, int x1, int y1, int x2, int y2, int minLevel) {
            this.name = name;
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
            this.minLevel = minLevel;
        }

        boolean contains(int x, int y) {
            return x >= x1 && x <= x2 && y >= y1 && y <= y2;
        }
    }

    private static final Zone[] FLAVOR_L1 = {
        new Zone("Strawberry", 293, 558, 362, 587), new Zone("Chocolate", 410, 558, 477, 587),
        new Zone("Vanilla", 279, 605, 349, 635),    new Zone("Matcha", 397, 605, 468, 635)
    };
    // ด่าน 2 และ 3 ใช้ตำแหน่งเดียวกัน (ด่าน 3 มี Mint เพิ่ม)
    private static final Zone[] FLAVOR_ADV = {
        new Zone("Strawberry", 335, 558, 408, 587), new Zone("Chocolate", 434, 558, 506, 587),
        new Zone("Thaitea", 531, 558, 603, 587, 2),
        new Zone("Vanilla", 327, 605, 401, 635),    new Zone("Matcha", 431, 605, 506, 635),
        new Zone("Mint", 533, 605, 603, 635, 3)
    };

    private static final Zone[] FRUIT_L1 = {
        new Zone("Banana", 59, 550, 133, 578), new Zone("Strawberry", 150, 550, 225, 578),
        new Zone("Blueberry", 35, 590, 113, 625), new Zone("Cherry", 133, 590, 203, 625)
    };
    private static final Zone[] FRUIT_ADV = {
        new Zone("Banana", 47, 547, 124, 580), new Zone("Strawberry", 134, 547, 211, 580),
        new Zone("Apple", 217, 547, 296, 580),
        new Zone("Blueberry", 27, 588, 98, 623), new Zone("Cherry", 113, 588, 185, 623),
        new Zone("Orange", 199, 588, 275, 623, 3)
    };

    private static final Zone[] TOPPING_L1 = {
        new Zone("Nuts", 813, 550, 883, 580), new Zone("Rainbow", 843, 593, 915, 620)
    };
    private static final Zone[] TOPPING_ADV = {
        new Zone("Nuts", 831, 550, 897, 580), new Zone("Choco Chip", 756, 550, 825, 580),
        new Zone("Rainbow", 855, 588, 926, 620),
        new Zone("Jelly", 769, 588, 839, 620, 3)
    };

    // ซอสแต่ละด่านวางตำแหน่งไม่เหมือนกัน เลยแยกเป็น 3 ชุด
    private static final Zone[] SAUCE_L1 = {
        new Zone("Choco Sauce", 914, 496, 951, 585), new Zone("Strawberry Sauce", 940, 531, 983, 625)
    };
    private static final Zone[] SAUCE_L2 = {
        new Zone("Choco Sauce", 901, 477, 935, 540), new Zone("Strawberry Sauce", 920, 541, 960, 570),
        new Zone("Blueberry Sauce", 943, 572, 990, 631)
    };
    private static final Zone[] SAUCE_L3 = {
        new Zone("Choco Sauce", 860, 474, 920, 515), new Zone("Strawberry Sauce", 895, 516, 940, 560),
        new Zone("Blueberry Sauce", 919, 561, 951, 600), new Zone("Caramel Sauce", 941, 601, 985, 641)
    };


    public static class SoundManager {
        private static Clip bgmClip;

        // ค้นหาไฟล์เสียง (เดิมเขียนซ้ำอยู่ในทั้ง SoundTask และ BGMTask)
        private static URL findSound(String fileName) {
            URL soundUrl = SoundManager.class.getResource("/Miniproject/" + fileName);
            if (soundUrl == null) {
                soundUrl = SoundManager.class.getResource("/" + fileName);
            }
            return soundUrl;
        }

        // 1. งานเล่นเสียงเอฟเฟกต์ (SFX)
        private static class SoundTask implements Runnable {
            private String fileName;

            public SoundTask(String fileName) {
                this.fileName = fileName;
            }

            @Override
            public void run() {
                try {
                    URL soundUrl = findSound(fileName);
                    if (soundUrl != null) {
                        AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundUrl);
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioInput);
                        clip.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 2. งานเล่นเพลง BGM
        private static class BGMTask implements Runnable {
            private String fileName;

            public BGMTask(String fileName) {
                this.fileName = fileName;
            }

            @Override
            public void run() {
                try {
                    URL soundUrl = findSound(fileName);
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
            }
        }

        public static void playSound(String fileName) {
            new Thread(new SoundTask(fileName)).start();
        }

        public static void playBGM(String fileName) {
            stopBGM();
            new Thread(new BGMTask(fileName)).start();
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

        // หน้าเมนูคือภาพ 1.png เต็มจอ คลิกตรงไหนก็เริ่มเกม (ย้ายมาจาก IceCreamGame)
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        gameLoopTimer = new Timer(100, e -> {
            if (currentState != STATE_PLAYING) return;

            long now = System.currentTimeMillis();

            if (now - levelStartTime >= LEVEL_DURATION_MS) {
                currentState = (customersServed >= TARGET_CUSTOMERS) ? STATE_LEVEL_WIN : STATE_LEVEL_LOSE;
                repaint();
                return;
            }

            if (now >= nextSpawnTime && spawnCustomer()) {
                scheduleNextSpawn();
            }

            for (int i = 0; i < customers.length; i++) {
                CustomerData c = customers[i];
                if (c != null && now - c.lastPatienceTick > 6000) {
                    c.hearts--;
                    c.lastPatienceTick = now;
                    if (c.hearts <= 0) {
                        customers[i] = null;
                        scheduleNextSpawnAfterLeave();
                    }
                }
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SoundManager.playSound("click.wav");
                int x = e.getX();
                int y = e.getY();

                switch (currentState) {
                    case STATE_MENU:
                        startGame();
                        break;
                    case STATE_LEVEL_LOSE:
                        if (getSignButtonBounds().contains(x, y)) retryLevel();
                        break;
                    case STATE_LEVEL_WIN:
                        if (getSignButtonBounds().contains(x, y)) nextLevel();
                        break;
                    case STATE_PLAYING:
                        handlePlayingClick(x, y);
                        break;
                }
                repaint();
            }
        });
    }

    // คืนชื่อวัตถุดิบที่ถูกคลิก (หรือ null ถ้าคลิกไม่โดน)
    private String pickZone(Zone[] zones, int x, int y) {
        for (Zone z : zones) {
            if (currentLevel >= z.minLevel && z.contains(x, y)) return z.name;
        }
        return null;
    }

    private void handlePlayingClick(int x, int y) {
        // ปุ่มล้างถาด
        if (x >= 28 && x <= 173 && y >= 666 && y <= 730) {
            resetCurrentIceCream();
            return;
        }

        // เลือกภาชนะ (Cone/Cup) เหมือนกันทุกด่าน
        if (x >= 680 && x <= 760 && y >= 500 && y <= 650) {
            if (currentContainer.isEmpty()) currentContainer = (y <= 575) ? "Cone" : "Cup";
            return;
        }

        // ต้องมีภาชนะก่อนถึงจะเลือกอย่างอื่นได้
        if (currentContainer.isEmpty()) return;

        // ต้องมีรสไอศกรีมก่อนถึงจะเลือกผลไม้/ท็อปปิ้ง/ซอส/เสิร์ฟได้
        if (currentFlavor.isEmpty()) {
            String flavor = pickZone(currentLevel == 1 ? FLAVOR_L1 : FLAVOR_ADV, x, y);
            if (flavor != null) currentFlavor = flavor;
            return;
        }

        if (currentFruit.isEmpty()) {
            String fruit = pickZone(currentLevel == 1 ? FRUIT_L1 : FRUIT_ADV, x, y);
            if (fruit != null) { currentFruit = fruit; return; }
        }

        if (currentTopping.isEmpty()) {
            String topping = pickZone(currentLevel == 1 ? TOPPING_L1 : TOPPING_ADV, x, y);
            if (topping != null) { currentTopping = topping; return; }
        }

        if (currentSauce.isEmpty()) {
            Zone[] sauces = (currentLevel == 1) ? SAUCE_L1 : (currentLevel == 2) ? SAUCE_L2 : SAUCE_L3;
            String sauce = pickZone(sauces, x, y);
            if (sauce != null) { currentSauce = sauce; return; }
        }

        // คลิกโซนลูกค้า = เสิร์ฟ
        if (y >= 150 && y <= 480) {
            checkServeCustomer(x);
        }
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
        URL url = getClass().getResource(filename);
        if (url != null) {
            return new ImageIcon(url).getImage();
        }
        // สำรอง: หาไฟล์จากโฟลเดอร์งาน (แบบที่ IceCreamGame เคยทำกับ 1.png)
        if (new File(filename).exists()) {
            return new ImageIcon(filename).getImage();
        }
        return null;
    }

    public void startGame() {
        currentLevel = 1;
        startLevel();
    }

    private void startLevel() {
        currentState = STATE_PLAYING;
        setCursor(Cursor.getDefaultCursor());

        if (currentLevel == 1) {
            SoundManager.playBGM(BGM_LEVEL_1);
        } else if (currentLevel == 2) {
            SoundManager.playBGM(BGM_LEVEL_2);
        } else if (currentLevel == 3) {
            SoundManager.playBGM(BGM_LEVEL_3);
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
        if (currentLevel == 2) return customerImagesLevel2;
        if (currentLevel == 3) return customerImagesLevel3;
        return customerImages;
    }

    private void scheduleNextSpawn() {
        int min = getSpawnMin();
        int max = getSpawnMax();
        nextSpawnTime = System.currentTimeMillis() + min + random.nextInt(max - min + 1);
    }

    private void scheduleNextSpawnAfterLeave() {
        // ด่าน 1-2 ใช้ช่วงเวลาเดียวกับ scheduleNextSpawn() อยู่แล้ว มีแค่ด่าน 3 ที่ต่างออกไป
        if (currentLevel == 3) {
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

    // คืน true ถ้าสร้างลูกค้าใหม่ได้ (มีช่องว่าง)
    private boolean spawnCustomer() {
        if (currentState != STATE_PLAYING) return false;

        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < customers.length; i++) {
            if (customers[i] == null) emptySlots.add(i);
        }
        if (emptySlots.isEmpty()) return false;

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
        return true;
    }

    // handlePlayingClick เช็คแล้วว่ามีภาชนะและรสแล้วก่อนเรียกเมธอดนี้
    private void checkServeCustomer(int clickX) {
        int index = -1;
        if (clickX >= 50 && clickX <= 250) index = 0;
        else if (clickX >= 251 && clickX <= 450) index = 1;
        else if (clickX >= 451 && clickX <= 650) index = 2;
        else if (clickX >= 651 && clickX <= 850) index = 3;

        if (index == -1 || customers[index] == null) return;
        CustomerData c = customers[index];

        boolean isCorrect = currentContainer.equalsIgnoreCase(c.reqContainer) &&
                            currentFlavor.equalsIgnoreCase(c.reqFlavor) &&
                            currentFruit.equalsIgnoreCase(c.reqFruit) &&
                            currentTopping.equalsIgnoreCase(c.reqTopping) &&
                            currentSauce.equalsIgnoreCase(c.reqSauce);

        if (isCorrect) {
            SoundManager.playSound("success.wav");
            double basePrice = 25.0;
            if (c.hearts >= 4) {
                int tip = 10 + random.nextInt(21);
                totalMoney += basePrice + tip;
            } else if (c.hearts >= 2) {
                totalMoney += basePrice;
            } else if (c.hearts == 1) {
                totalMoney += basePrice * 0.5;
            }
            customersServed++;
        } else {
            SoundManager.playSound("wrong.wav");
        }

        // ไม่ว่าถูกหรือผิด ลูกค้าก็กลับไปและเริ่มถาดใหม่เหมือนกัน
        customers[index] = null;
        resetCurrentIceCream();
        scheduleNextSpawnAfterLeave();
        checkWinCondition();
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

        int width = getWidth();
        int height = getHeight();

        if (currentState == STATE_MENU) {
            if (menuBgImage != null) {
                g2d.drawImage(menuBgImage, 0, 0, width, height, this);
            } else {
                // สีสำรองกรณีหาภาพไม่เจอ หน้าจอจะได้ไม่ขาวเปล่า
                g2d.setColor(new Color(135, 206, 235));
                g2d.fillRect(0, 0, width, height);
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Tahoma", Font.BOLD, 24));
                g2d.drawString("กดตรงนี้เพื่อเริ่มเกม (หาภาพ 1.png ไม่เจอ)", 250, 300);
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
        g2d.drawString("฿" + String.format("%.2f", totalMoney), valueX, moneyY);
        g2d.drawString(customersServed + "/" + TARGET_CUSTOMERS, valueX, targetY);

        int tDrawX = TRAY_X;
        int tDrawY = TRAY_Y;

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
            // Cone: ด่าน 2 กับ 3 วาดตำแหน่งเหมือนกันทุกชิ้น เลยรวมเป็นเงื่อนไขเดียว
            boolean adv = currentLevel >= 2;
            if (trayContainer != null) {
                g2d.drawImage(trayContainer, adv ? tDrawX : tDrawX - 55, tDrawY - 15, 150, 120, this);
            }
            if (trayFlavor != null) {
                int scoopW = 100;
                int scoopH = 80;
                if (adv) {
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
                if (adv) {
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
                g2d.drawImage(trayTopping, adv ? tDrawX + 25 : tDrawX - 30, tDrawY - 33, 100, 60, this);
            }
            if (traySauce != null) {
                g2d.drawImage(traySauce, adv ? tDrawX + 25 : tDrawX - 30, tDrawY - 35, 100, 60, this);
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