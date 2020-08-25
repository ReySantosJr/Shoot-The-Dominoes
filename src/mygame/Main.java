package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.opengl.Display;

public class Main extends SimpleApplication {
    /*
    *   Fields
    */
    private String numberToMatch;
    private String dominoesClicked;
    private int amountCorrect = 0, amountWrong = 0;

    private Geometry timer_geom, domOne_geom, domTwo_geom, domThree_geom, domFour_geom, domFive_geom, domSix_geom;
    private Node shootables;
    private boolean isHit = false, canReset = false, timesUp = false;
    private BitmapText direc1, dominoHitText, sumText, resetText, youScoreText, amountRightText, amountWrongText;
    private BitmapText ch;
    private Random rand;
    private Vector3f move_player;

    private ArrayList<Geometry> geomList = null;
    private HashSet<String> domsClickedList = new HashSet<>();

    static private AppSettings settings;
    static private Main app;

    /*
    *   Main Method
    */
    public static void main(String[] args) {
        settings = new AppSettings(true);
        settings.setResolution(1000, 600);

        app = new Main();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    /*
    *   Simple Init Method
    */
    @Override
    public void simpleInitApp() {
        /* Sets up camera and window display */
        flyCam.setMoveSpeed(0);
        flyCam.setRotationSpeed(1);

        Display.setResizable(true);
        Display.setLocation(25, 25);
        Display.setTitle("Domino Sum");
        setDisplayFps(false);
        setDisplayStatView(false);

        /* Scene set up */
        // Background display
        viewPort.setBackgroundColor(ColorRGBA.LightGray);

        // Light set up
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);

        // Timer block
        Box timerBlock = new Box(.5f, .2f, .1f);
        timer_geom = new Geometry("timer block", timerBlock);
        timer_geom.setLocalTranslation(new Vector3f(7, 3, 0));
        Material timer_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        timer_mat.setColor("Color", ColorRGBA.Red);
        timer_geom.setMaterial(timer_mat);

        // Dominoes
        Box domOne = new Box(.5f, .5f, .1f);
        domOne_geom = new Geometry("dom 1", domOne);
        domOne_geom.setLocalTranslation(new Vector3f(-5, 0, 0));
        Material domOne_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        domOne_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/domino 1.png"));
        domOne_geom.setMaterial(domOne_mat);

        Box domTwo = new Box(.5f, .5f, .1f);
        domTwo_geom = new Geometry("dom 2", domTwo);
        domTwo_geom.setLocalTranslation(new Vector3f(-3, 0, 0));
        Material domTwo_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        domTwo_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/domino 2.png"));
        domTwo_geom.setMaterial(domTwo_mat);

        Box domThree = new Box(.5f, .5f, .1f);
        domThree_geom = new Geometry("dom 3", domThree);
        domThree_geom.setLocalTranslation(new Vector3f(-1, 0, 0));
        Material domThree_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        domThree_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/domino 3.png"));
        domThree_geom.setMaterial(domThree_mat);

        Box domFour = new Box(.5f, .5f, .1f);
        domFour_geom = new Geometry("dom 4", domFour);
        domFour_geom.setLocalTranslation(new Vector3f(1, 0, 0));
        Material domFour_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        domFour_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/domino 4.png"));
        domFour_geom.setMaterial(domFour_mat);

        Box domFive = new Box(.5f, .5f, .1f);
        domFive_geom = new Geometry("dom 5", domFive);
        domFive_geom.setLocalTranslation(new Vector3f(3, 0, 0));
        Material domFive_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        domFive_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/domino 5.png"));
        domFive_geom.setMaterial(domFive_mat);

        Box domSix = new Box(.5f, .5f, .1f);
        domSix_geom = new Geometry("dom 6", domSix);
        domSix_geom.setLocalTranslation(new Vector3f(5, 0, 0));
        Material domSix_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        domSix_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/domino 6.png"));
        domSix_geom.setMaterial(domSix_mat);

        // Add geom variables to ArrayList
        geomList = new ArrayList<>();
        geomList.add(domOne_geom);
        geomList.add(domTwo_geom);
        geomList.add(domThree_geom);
        geomList.add(domFour_geom);
        geomList.add(domFive_geom);
        geomList.add(domSix_geom);

        // Initialize crosshair
        initCrossHairs();

        // Initialize screen stats
        displayOnScreenStats();

        // Set spatials to nodes and root node
        rootNode.attachChild(timer_geom);
        shootables = new Node("shootables");
        rootNode.attachChild(shootables);
        shootables.attachChild(domOne_geom);
        shootables.attachChild(domTwo_geom);
        shootables.attachChild(domThree_geom);
        shootables.attachChild(domFour_geom);
        shootables.attachChild(domFive_geom);
        shootables.attachChild(domSix_geom);

        // Listener setup
        inputManager.addMapping("fire", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("fire stop", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("get sum", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("reset", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(fireDominoListener, "fire", "fire stop", "get sum", "reset");

    } // END OF simpleInitApp()

    /*
    *   Crosshair reticule for aiming
    */
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+");
        ch.setLocalTranslation(
                settings.getWidth() / 2 - ch.getLineWidth() / 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0
        );
        ch.setColor(ColorRGBA.Black);
        guiNode.attachChild(ch);
    }
    
    /*
    *   Displays screen stats
    */
    protected void displayOnScreenStats() {
        // Notifies player to start playing
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        direc1 = new BitmapText(guiFont, false);
        direc1.setSize(guiFont.getCharSet().getRenderedSize());
        direc1.setColor(ColorRGBA.Black);
        direc1.setText("Shoot the dominoes!");
        direc1.setLocalTranslation(300, 400, 0);
        guiNode.attachChild(direc1);
        
        // Notifies player what the sum should be
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        dominoHitText = new BitmapText(guiFont, false);
        dominoHitText.setSize(guiFont.getCharSet().getRenderedSize());
        dominoHitText.setColor(ColorRGBA.Black);
        dominoHitText.setLocalTranslation(300, 380, 0);
        guiNode.attachChild(dominoHitText);
        rand = new Random();
        int randNum = rand.nextInt(21);
        numberToMatch = String.valueOf(randNum);
        dominoHitText.setText("Reach the number: " + numberToMatch);
        
        // Displays if player's sum equals what the game presented
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        sumText = new BitmapText(guiFont, false);
        sumText.setSize(guiFont.getCharSet().getRenderedSize());
        sumText.setColor(ColorRGBA.Black);
        sumText.setLocalTranslation(300, 360, 0);
        guiNode.attachChild(sumText);
        
        // Notifies player to reset game when timer is passed
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        resetText = new BitmapText(guiFont, false);
        resetText.setSize(40f);
        resetText.setColor(ColorRGBA.Black);
        resetText.setLocalTranslation(350, 200, 0);
        guiNode.attachChild(resetText);
        
        // Displays final score when timer passes
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        youScoreText = new BitmapText(guiFont, false);
        youScoreText.setSize(40f);
        youScoreText.setColor(ColorRGBA.Black);
        youScoreText.setLocalTranslation(350, 400, 0);
        guiNode.attachChild(youScoreText);
        
        // Display amount of times the player's sum equals the game's
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        amountRightText = new BitmapText(guiFont, false);
        amountRightText.setSize(40f);
        amountRightText.setColor(ColorRGBA.Black);
        amountRightText.setLocalTranslation(350, 350, 0);
        guiNode.attachChild(amountRightText);
        
        // Display amount of times the player's sum doesn't equals the game's
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        amountWrongText = new BitmapText(guiFont, false);
        amountWrongText.setSize(40f);
        amountWrongText.setColor(ColorRGBA.Black);
        amountWrongText.setLocalTranslation(350, 300, 0);
        guiNode.attachChild(amountWrongText);
        
    } // END OF displayOnScreenStats()

    /*
    *   Timer Block
     */
    @Override
    public void simpleUpdate(float tpf) {
        // Sets up speed for timer block
        move_player = timer_geom.getLocalTranslation();
        timer_geom.setLocalTranslation(new Vector3f(move_player.x - 2 * tpf, move_player.y, move_player.z));

        // The end point of the timer block
        if (move_player.x < -6) {
            timer_geom.setLocalTranslation(new Vector3f(-7, 3, 0));
            timesUp = true;

            canReset = true;
        }

        // When time block reaches endpoint, clears the game and displays teh final score
        if (timesUp) {
            domsClickedList.removeAll(domsClickedList);
            rootNode.detachAllChildren();
            guiNode.detachAllChildren();

            youScoreText.setText("Your score:");
            amountRightText.setText("Amount correct: " + amountCorrect);
            amountWrongText.setText("Amount wrong: " + amountWrong);
            
            guiNode.attachChild(youScoreText);
            guiNode.attachChild(amountRightText);
            guiNode.attachChild(amountWrongText);
        }

    } // END OF simpleUpdate(float tpf)

    /*
    *   Action Listener
     */
    private final ActionListener fireDominoListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {

            if (name.equals("fire") && keyPressed) {
                isHit = true;
            }

            if (!name.equals("fire stop") && !keyPressed) {
                isHit = false;
                ch.setColor(ColorRGBA.Black);
            }
            
            // When player hits domino, it dissappears
            if (isHit) {
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                shootables.collideWith(ray, results);

                for (int i = 0; i < results.size(); i++) {
                    String hit = results.getCollision(i).getGeometry().getName();

                    for (int j = 0; j < geomList.size(); j++) {
                        if (hit.equals(geomList.get(j).getName())) {
                            ch.setColor(ColorRGBA.Red);
                            String n1 = String.valueOf(geomList.get(j).getName().charAt(4));
                            domsClickedList.add(n1);
                            
                            shootables.detachChild(geomList.get(j));
                        }
                    }
                }
            }

            // Adds the dominoes that are clicked
            if (name.equals("get sum") && keyPressed) {

                int sums = 0;

                Iterator<String> i = domsClickedList.iterator();
                while (i.hasNext()) {

                    int nums = Integer.parseInt(i.next());
                    sums += nums;

                }

                String dominoesClicked = String.valueOf(sums);

                // Determines if the sum is equal or not to the given number.
                if (numberToMatch.equals(dominoesClicked)) {
                    sumText.setText(String.valueOf("You've clicked " + dominoesClicked + ", Good Job!"));
                    amountCorrect++;

                    resetText.setText("Press the space bar.");
                    canReset = true;

                } else if (!numberToMatch.equals(dominoesClicked)) {
                    sumText.setText("You've clicked " + dominoesClicked + ", FAIL");
                    amountWrong++;

                    resetText.setText("Press the space bar.");
                    canReset = true;
                }
            }

            // Let's player reset the game after the input their sum
            if (canReset) {
                if (canReset && name.equals("reset") && keyPressed) {
                    domsClickedList.removeAll(domsClickedList);
                    rootNode.detachAllChildren();
                    guiNode.detachAllChildren();

                    // Initialize crosshair
                    initCrossHairs();

                    // Initialize screen stats
                    displayOnScreenStats();

                    // Set spatials to nodes
                    timer_geom.setLocalTranslation(new Vector3f(7, 3, 0));
                    rootNode.attachChild(timer_geom);
                    shootables = new Node("shootables");
                    rootNode.attachChild(shootables);
                    shootables.attachChild(domOne_geom);
                    shootables.attachChild(domTwo_geom);
                    shootables.attachChild(domThree_geom);
                    shootables.attachChild(domFour_geom);
                    shootables.attachChild(domFive_geom);
                    shootables.attachChild(domSix_geom);
                    canReset = false;

                }
            }
        } // END OF onAction()
    }; // END OF ActionListener fireDominoListener
}

