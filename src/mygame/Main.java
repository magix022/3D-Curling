package mygame;

//usefull imports
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Cylinder;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ImageSelectSelectionChangedEvent;
import de.lessvoid.nifty.controls.slider.SliderControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.Popup;

//main class
public class Main extends SimpleApplication implements ScreenController {

    //define et initialise data fields
    private BulletAppState bulletAppState;

    //initial positions on the scene
    private Vector3f camPos;
    private Vector3f camView;
    private Vector3f originRockPos;
    private Vector3f centerPos;
    private Vector3f extremity;
    private Vector3f rockCamLocation;
    private Vector3f firstLight;

    //Data fields for the scene
    private Spatial floorScene;
    private RigidBodyControl sceneGeo;
    private Node sceneNode;
    private Spatial arrowGeo;

    //Materials for the scene
    private Material iceMat;
    private Material sideBoardsMat;
    private Material topBoardsMat;
    private Material wallsMat;
    private Material roofMat;
    private Material bigRingMat;
    private Material smallRingMat;
    private Material linesMat;
    private Material standsMat1;
    private Material standsMat2;
    private Material sideRoofMat;

    //Materials for the rocks
    private Material blueMat;
    private Material redMat;

    //Used to check if rock is in house
    private GhostControl houseGhost;
    //Transparent material
    private Material transMat;

    //Initializers necessary for scoreboard and score
    private Rock[] rockTeam1 = new Rock[4];
    private Rock[] rockTeam2 = new Rock[4];
    private double[] distanceFromCenterTeam1 = new double[4];
    private double[] distanceFromCenterTeam2 = new double[4];
    private YLockControl[] controlTeam1 = new YLockControl[4];
    private YLockControl[] controlTeam2 = new YLockControl[4];
    private int initialHammer;
    private int ENTERIsPressed = 0;
    private int temp = 0;
    private boolean isDone = true;

    //ArrayList of YLockControl to add elements at the end of the physics array
    ArrayList<YLockControl> physTeam = new ArrayList();

    //Create ScoreBoard object
    ScoreBoardClass scoreboard = new ScoreBoardClass();

    /*ArrayList of shotDone to add elements at the end of the array and check 
    if a particular rock is thrown*/
    ArrayList<Boolean> shotDone = new ArrayList(Arrays.asList(new Boolean[8]));

    //Data fields for the power and direction of throw
    private float shotX = 0;
    private float shotY = 0;
    private float velocityX;
    private float velocityY;

    private float cylinderRadius;

    //Used to check if the rocks are in house
    private Quaternion arrowRotation = new Quaternion();
    private Quaternion firstArrowRotation = new Quaternion();

    //Boolean values to optimise the if statements in th simpleUpdate method
    boolean roundIsDone = true;
    boolean gameIsStarted = false;
    boolean gameIsFinished = false;

    //Initialisers and default values for the GUI
    private Boolean camStatus;
    private Screen screen;
    private AudioNode audio;
    private Nifty nifty;
    private Boolean unlockCommands = false;
    private Boolean shotHasBeenSet = false;
    private Boolean alternateCamAngle = false;

    ImageSelectSelectionChangedEvent event1;
    ImageSelectSelectionChangedEvent event2;
    Screen ScreenHud;
    String team1Name = "Italy";
    String team2Name = "Italy";

    private AudioNode collisionSound;
    private AudioNode rockToBoard;
    private AudioNode backgroundSound;

    private Element niftyTurn;

    public static void main(String[] args) {
        //create Main object
        Main app = new Main();
        //run the program
        app.start();
    }

    //Override the simpleInitApp and call other methods
    @Override
    public void simpleInitApp() {
        //Call nifty method to create GUI
        Init_Nifty();

        //Call other methods to create the scene
        setMaterials();
        setScene();
        setCoordinates();
        initMapping();
        setSpatials();

        //set number of rounds
        scoreboard.setNumberOfRounds(2);

        //randomly chose which team has the hammer
        initialHammer = (Math.random() <= 0.5) ? 1 : 2;
        scoreboard.setHammer(initialHammer);
        scoreboard.setTeam1Name(team1Name);
        scoreboard.setTeam2Name(team2Name);
        //set initial round
        scoreboard.setRound(0);

        //Set at true to start first shot, all other values are currently false
        Collections.fill(shotDone, Boolean.FALSE);
        shotDone.set(0, true);

        collisionSound = new AudioNode(assetManager, "Sounds/rock_collision.wav", DataType.Buffer);
        collisionSound.setPositional(false);
        collisionSound.setLooping(false);
        collisionSound.setVolume(0.07f);

        backgroundSound = new AudioNode(assetManager, "Sounds/curling_background.wav", DataType.Buffer);
        backgroundSound.setPositional(false);
        backgroundSound.setLooping(true);
        backgroundSound.setVolume(1);
        rootNode.attachChild(backgroundSound);
//        backgroundSound.play();

        rockToBoard = new AudioNode(assetManager, "Sounds/thud.wav", DataType.Buffer);
        rockToBoard.setPositional(true);
        rockToBoard.setLooping(false);
        rockToBoard.setVolume(0.1f);

        //Enable physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);

        //adding physics to physicsSpace
        bulletAppState.getPhysicsSpace().add(sceneGeo);

        //camera parameters
        flyCam.setMoveSpeed(50f);
        flyCam.setDragToRotate(true);
    }

    //Game loop that update throughout the game
    @Override
    public void simpleUpdate(float tpf) {
        //display score in GUI
        if (gameIsStarted) {
            scoreTeam1();
            scoreTeam2();
        }

        if (physTeam.size() > 0) {
            //set particular cam location
            rockCamLocation = physTeam.get(physTeam.size() - 1).getPhysicsLocation().add(15, 5, 0);
            //set direction and position of alternate camera angle1
            if (!alternateCamAngle) {
                cam.setLocation(rockCamLocation);
                cam.lookAtDirection(new Vector3f(-1, -0.3f, 0), new Vector3f(0, 1, 0));
            }
        }

        //check if there are still rounds to be played
        if (scoreboard.getRound() < scoreboard.getNumberOfRounds()) {
            //check which team has the hammer (the last shot)
            if (scoreboard.getHammer() == 2) { //case where team2 has the last shot
                //create initial rock, and other rocks once the first one is motionless after its shot
                if (shotDone.get(0) == true && physTeam.isEmpty()) {           //create rock 1 for team 1
                    createRock(1, 0, rockTeam1, physTeam, tpf);
                    shotDone.set(0, false);
                } else if (noMouvement(physTeam) && shotDone.get(0) == true) { //create rock 1 for team 2
                    createRock(2, 0, rockTeam2, physTeam, tpf);
                    playerTurn(2);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(0, false);
                } else if (noMouvement(physTeam) && shotDone.get(1) == true) { //create rock 2 for team 1
                    createRock(1, 1, rockTeam1, physTeam, tpf);
                    playerTurn(1);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(1, false);
                } else if (noMouvement(physTeam) && shotDone.get(2) == true) { //create rock 2 for team 2
                    createRock(2, 1, rockTeam2, physTeam, tpf);
                    playerTurn(2);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(2, false);
                } else if (noMouvement(physTeam) && shotDone.get(3) == true) { //create rock 3 for team 1
                    createRock(1, 2, rockTeam1, physTeam, tpf);
                    playerTurn(1);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(3, false);
                } else if (noMouvement(physTeam) && shotDone.get(4) == true) { //create rock 3 for team 2
                    createRock(2, 2, rockTeam2, physTeam, tpf);
                    playerTurn(2);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(4, false);
                } else if (noMouvement(physTeam) && shotDone.get(5) == true) { //create rock 4 for team 1
                    createRock(1, 3, rockTeam1, physTeam, tpf);
                    playerTurn(1);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(5, false);
                } else if (noMouvement(physTeam) && shotDone.get(6) == true) { //create rock 4 for team 2
                    createRock(2, 3, rockTeam2, physTeam, tpf);
                    playerTurn(2);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(6, false);
                }

                //if all shots have been completed and the rocks are motionless, the round is done
                if (scoreboard.getTotalShots() == 8 && noMouvement(physTeam) && roundIsDone == true) {
                    //assigns physics controls objects for team1's rocks to another YLockControl array, for sorting
                    for (int i = 0, j = 0; i < physTeam.size() && j < 4; i += 2, j++) {
                        controlTeam1[j] = physTeam.get(i);
                    }
                    //assigns physics controls objects for team2's rocks to another YLockControl array, for sorting
                    for (int i = 1, j = 0; i < physTeam.size() && j < 4; i += 2, j++) {
                        controlTeam2[j] = physTeam.get(i);
                    }
                    //get distance between rocks and center of the house
                    getDistanceFromCenter(centerPos);
                    //get the team that scores and the number of points they score in one particular round
                    calculateScore(distanceFromCenterTeam1, distanceFromCenterTeam2, rockTeam1, rockTeam2, controlTeam1, controlTeam2);
                    //show end of round message
                    showEndOfRoundMessage();
                    //display score after each round
                    displayScore();
                    roundIsDone = false;
                }
            } else {
                //***create rocks similarly as in IF block, where team2 has the hammer***
                //case where team1 has the last shot
                if (shotDone.get(0) == true && physTeam.isEmpty()) {
                    createRock(2, 0, rockTeam2, physTeam, tpf);
                    shotDone.set(0, false);
                } else if (noMouvement(physTeam) && shotDone.get(0) == true) {
                    createRock(1, 0, rockTeam1, physTeam, tpf);
                    playerTurn(1);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(0, false);
                } else if (noMouvement(physTeam) && shotDone.get(1) == true) {
                    createRock(2, 1, rockTeam2, physTeam, tpf);
                    playerTurn(2);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(1, false);
                } else if (noMouvement(physTeam) && shotDone.get(2) == true) {
                    createRock(1, 1, rockTeam1, physTeam, tpf);
                    playerTurn(1);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(2, false);
                } else if (noMouvement(physTeam) && shotDone.get(3) == true) {
                    createRock(2, 2, rockTeam2, physTeam, tpf);
                    playerTurn(2);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(3, false);
                } else if (noMouvement(physTeam) && shotDone.get(4) == true) {
                    createRock(1, 2, rockTeam1, physTeam, tpf);
                    playerTurn(1);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(4, false);
                } else if (noMouvement(physTeam) && shotDone.get(5) == true) {
                    createRock(2, 3, rockTeam2, physTeam, tpf);
                    playerTurn(2);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(5, false);
                } else if (noMouvement(physTeam) && shotDone.get(6) == true) {
                    createRock(1, 3, rockTeam1, physTeam, tpf);
                    playerTurn(1);
                    physTeam.get(physTeam.size() - 2).setLinearDamping(0.25f);
                    shotDone.set(6, false);
                }

                //***same as in previous IF block, when team2 has the hammer***
                if (scoreboard.getTotalShots() == 8 && noMouvement(physTeam) && roundIsDone == true) {
                    for (int i = 0, j = 0; i < physTeam.size() && j < 4; i += 2, j++) {
                        controlTeam2[j] = physTeam.get(i);
                    }
                    for (int i = 1, j = 0; i < physTeam.size() && j < 4; i += 2, j++) {
                        controlTeam1[j] = physTeam.get(i);
                    }
                    getDistanceFromCenter(centerPos);
                    calculateScore(distanceFromCenterTeam1, distanceFromCenterTeam2, rockTeam1, rockTeam2, controlTeam1, controlTeam2);
                    showEndOfRoundMessage();
                    displayScore();
                    roundIsDone = false;
                }
            }
            //update rock velocity value
            updateVelocityValue();

            listener.setLocation(cam.getLocation());
            listener.setRotation(cam.getRotation());

            //turn quaternion temp object
            double angle = (velocityX / velocityY);
            Quaternion temp = new Quaternion();
            temp.fromAngleAxis((float) Math.atan(angle), new Vector3f(0, 0, -1));

            //update arrow control
            arrowRotation = firstArrowRotation.mult(temp);
            arrowGeo.setLocalRotation(arrowRotation);
            arrowGeo.setLocalScale(velocityY / 20);

        } else {
            //when game is finished, display final score
            unlockCommands = false;
            showEndOfRoundMessage();
        }

        for (int i = 0; i < physTeam.size(); i++) {
            physTeam.get(i).controlUpdate(tpf, originRockPos.getY());
            physTeam.get(i).prePhysicsTick(bulletAppState.getPhysicsSpace(), tpf);
            physTeam.get(i).physicsTick(bulletAppState.getPhysicsSpace(), tpf);
        }

        //arrange rock friction when there is no brushing
        //listener for in-game the mouse and keyboard actions
        inputManager.addListener(actionListener, "throw");
        inputManager.addListener(actionListener, "stop");
        inputManager.addListener(actionListener, "resetRound");
        inputManager.addListener(actionListener, "damping");
        inputManager.addListener(actionListener, "get1");
        inputManager.addListener(actionListener, "get2");
        inputManager.addListener(actionListener, "get3");

    }

    /*call distance from center method for every curling rock and attribute 
    the value to a array of double values*/
    public void getDistanceFromCenter(Vector3f centerPos) {
        for (int count = 0; count < 4; count++) {
            distanceFromCenterTeam1[count] = rockTeam1[count].getDistanceFrom(centerPos);
            distanceFromCenterTeam2[count] = rockTeam2[count].getDistanceFrom(centerPos);
        }
    }

    //Synchronously sort rockTeam, distanceFromCenter and YLockControl arrays so that indexes match
    //Use selection sort algorithm
    public void selectionSort(double[] array, Rock[] rockTeam, YLockControl[] controlTeam) {
        for (int i = 0; i < array.length - 1; i++) {
            //find the minimum distance value in the array [i ... array.length-1]
            double currentMin = array[i];
            int currentMinIndex = i;
            Rock minRock = rockTeam[i];
            YLockControl minControlTeam = controlTeam[i];

            for (int j = i + 1; j < array.length; j++) {
                if (currentMin > array[j]) {
                    currentMin = array[j];
                    currentMinIndex = j;
                    minRock = rockTeam[j];
                    minControlTeam = controlTeam[j];
                }
            }
            //swap array[i] with list[currentMinIndex] if necessary
            if (currentMinIndex != i) {
                array[currentMinIndex] = array[i];
                array[i] = currentMin;
                rockTeam[currentMinIndex] = rockTeam[i];
                rockTeam[i] = minRock;
                controlTeam[currentMinIndex] = controlTeam[i];
                controlTeam[i] = minControlTeam;
            }
        }
    }

    //determine which team scores and how many points they score in one particular round
    public void calculateScore(double[] distanceFromCenterTeam1, double[] distanceFromCenterTeam2,
            Rock[] rockTeam1, Rock[] rockTeam2, YLockControl[] controlTeam1, YLockControl[] controlTeam2) {
        //before calculating score, sort all arrays for each team with selectionSort method
        selectionSort(distanceFromCenterTeam1, rockTeam1, controlTeam1);
        selectionSort(distanceFromCenterTeam2, rockTeam2, controlTeam2);
        //call method to check if particular rocks are in the house, and eligible to score points
        updateInHouse();

        //Case where team 1 scores in this round
        if (distanceFromCenterTeam1[0] <= distanceFromCenterTeam2[0] && rockTeam1[0].getInHouse()) {
            if (distanceFromCenterTeam1[1] < distanceFromCenterTeam2[0] && rockTeam1[1].getInHouse()) {
                if (distanceFromCenterTeam1[2] < distanceFromCenterTeam2[0] && rockTeam1[2].getInHouse()) {
                    if (distanceFromCenterTeam1[3] < distanceFromCenterTeam2[0] && rockTeam1[3].getInHouse()) {
                        scoreboard.setTeam1RoundScore(scoreboard.getRound(), 4); //team 1 scores 4 pts
                    } else {
                        scoreboard.setTeam1RoundScore(scoreboard.getRound(), 3); //team 1 scores 3 pts
                    }
                } else {
                    scoreboard.setTeam1RoundScore(scoreboard.getRound(), 2); //team 1 scores 2 pts
                }
            } else {
                scoreboard.setTeam1RoundScore(scoreboard.getRound(), 1); //team 1 scores 1 pts
            }
        } else {
            scoreboard.setTeam1RoundScore(scoreboard.getRound(), 0); //team 1 does not score
        }
        //Case where team 2 score in this round
        if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[0] && rockTeam2[0].getInHouse()) {
            if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[1] && rockTeam2[1].getInHouse()) {
                if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[2] && rockTeam2[2].getInHouse()) {
                    if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[3] && rockTeam2[3].getInHouse()) {
                        scoreboard.setTeam2RoundScore(scoreboard.getRound(), 4); //team 2 scores 4 pts
                    } else {
                        scoreboard.setTeam2RoundScore(scoreboard.getRound(), 3); //team 2 scores 3 pts
                    }
                } else {
                    scoreboard.setTeam2RoundScore(scoreboard.getRound(), 2); //team 2 scores 2 pts
                }
            } else {
                scoreboard.setTeam2RoundScore(scoreboard.getRound(), 1); //team 2 scores 1 pts
            }
        } else {
            scoreboard.setTeam2RoundScore(scoreboard.getRound(), 0); //team 2 does not score
        }
        //call scoreboard class methods to calculate total score for each team
        scoreboard.calculateTotalScoreTeam1();
        scoreboard.calculateTotalScoreTeam2();
    }

    //method that assigns boolean value to rocks if in (true) or out (false) of the house
    //use houseGost object to check if rocks from both teams overlap on the house
    public void updateInHouse() {
        for (int i = 0; i < 4; i++) {

            if (rockTeam1[i].getRockModel().getLocalTranslation().distance(centerPos) < cylinderRadius) {
                rockTeam1[i].setInHouse(true);
            } else {
                rockTeam1[i].setInHouse(false);
            }

            if (rockTeam2[i].getRockModel().getLocalTranslation().distance(centerPos) < cylinderRadius) {
                rockTeam2[i].setInHouse(true);
            } else {
                rockTeam2[i].setInHouse(false);
            }
        }
    }

    //Method to display score after each round
    public void displayScore() {
        System.out.println("This round's score is :");
        System.out.println("Team 1: " + scoreboard.getTeam1RoundScore(scoreboard.getRound())
                + "\tTeam 2: " + scoreboard.getTeam2RoundScore(scoreboard.getRound()));
        System.out.println(scoreboard.getRoundWinner());

        System.out.println("The total score after round " + (scoreboard.getRound() + 1) + " is :");
        System.out.println("Team 1: " + scoreboard.getTeam1TotalScore() + "\tTeam 2: " + scoreboard.getTeam2TotalScore() + "\n");
    }

    //method to create and return rocks
    public Rock createRock(int team, int index, Rock[] rockTeam, ArrayList<YLockControl> physTeam, float tpf) {
        //create rock object
        Rock rock = new Rock(team);
        //assign model to rock
        rock.setRockModel(assetManager.loadModel(rock.getModelPath()));
        //assign initial position ot rock
        rock.getRockModel().setLocalTranslation(originRockPos.add(2, 0, 2f));

        //attach rock to scene
        rootNode.attachChild(rock.getRockModel());

        //add physics control for new rock
        YLockControl rockPhy = new YLockControl(1f, collisionSound, rockToBoard, houseGhost);

        rock.getRockModel().addControl(rockPhy);
        rockPhy.setRestitution(1f);
        //set initial linear speed damping factor for rock
        rockPhy.setLinearDamping(0.25f);
        bulletAppState.getPhysicsSpace().add(rockPhy);
        bulletAppState.getPhysicsSpace().addCollisionListener(rockPhy);
        rockPhy.setAngularFactor(new Vector3f(0, 1, 0));
        rockPhy.setAngularDamping(0.20f);

        //set shadows for rocks
        rock.getRockModel().setShadowMode(ShadowMode.CastAndReceive);
        //add rock and physics control to arrays
        rockTeam[index] = rock;
        physTeam.add(rockPhy);

        return rock;
    }

    //create action listener for in-game controls
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            try {
                //pressing ENTER after a round will call resetRound() method to clear the scene, update score and skip to next round
                if (name.equals("resetRound") && !keyPressed && scoreboard.getTotalShots() == 8 && noMouvement(physTeam) && unlockCommands) {
                    resetRound();
                    ENTERIsPressed++;
                }
                //setting arrow control for power and aim of the throw
                if (name.equals("throw") && keyPressed && unlockCommands) {
                    setThrowValue();
                    shotHasBeenSet = true;
                    rootNode.attachChild(arrowGeo);
                }
                //remove arrow control once shot is done
                if (name.equals("throw") && !keyPressed && unlockCommands && shotHasBeenSet) {
                    throwRock(physTeam);
                    rootNode.getChild("arrowGeo").removeFromParent();
                }
                //stops rock
                if (name.equals("stop") && !keyPressed && unlockCommands) {
                    stopRock();
                }
                //pressing 1 will switch to alternate camera angle 1
                if (name.equals("get1") && keyPressed) {
                    cam.setLocation(new Vector3f(-154.96962f, 59.868954f, -6.3394666f));
                    cam.lookAtDirection(new Vector3f(-0.06880015f, -0.99767405f, 0), new Vector3f(0, 1, 0));
                    alternateCamAngle = true;
                    flyCam.setEnabled(false);
                }
                if (name.equals("get1") && !keyPressed) {
                    alternateCamAngle = false;
                }
                //pressing 2 will switch to alternate camera angle 2
                if (name.equals("get2") && keyPressed) {
                    cam.setLocation(new Vector3f(-35.68889f, 38.272602f, -6.9257717f));
                    cam.lookAtDirection(new Vector3f(-0.86378217f, -0.50366956f, -0.015845418f), new Vector3f(0, 1, 0));
                    alternateCamAngle = true;
                    flyCam.setEnabled(false);
                }
                if (name.equals("get2") && !keyPressed) {
                    alternateCamAngle = false;
                }
                //pressing 3 will switch to alternate camera angle 2
                if (name.equals("get3") && keyPressed) {
                    cam.setLocation(new Vector3f(67.61219f, 18.359352f, -56.51864f));
                    cam.lookAtDirection(new Vector3f(-0.904703f, -0.17295352f, 0.38940513f), new Vector3f(0, 1, 0));
                    alternateCamAngle = true;
                    flyCam.setEnabled(false);
                }
                if (name.equals("get3") && !keyPressed) {
                    alternateCamAngle = false;
                }
                //pessing SPACEBAR will reduce friction between ice and rock when sliding
                if (name.equals("damping") && !keyPressed && unlockCommands) {
                    if (physTeam.get(physTeam.size() - 1).getLinearDamping() > 0.17f) {
                        physTeam.get(physTeam.size() - 1).setLinearDamping(physTeam.get(physTeam.size() - 1).getLinearDamping() - 0.005f);
                    }
                }
                //catch NullPointerExcetion errors
            } catch (NullPointerException ex) {
                System.out.print("null");
            }
        }
    };

    //method to set initial rock velocy when clicking and dragging to shoot
    public void updateVelocityValue() {
        //get final cursor coordinates
        float currentX = inputManager.getCursorPosition().x;
        float currentY = inputManager.getCursorPosition().y;

        float tempVelocityX;
        float tempVelocityY;

        //update velocity in X
        if (shotX - currentX > 300) {
            tempVelocityX = 300;
        } else if (shotX - currentX < -300) {
            tempVelocityX = -300;
        } else {
            tempVelocityX = shotX - currentX;
        }
        //update velocity in Y
        if (shotY - currentY > 200) {
            tempVelocityY = 200;
        } else if (shotY - currentY < 0) {
            tempVelocityY = 0;
        } else {
            tempVelocityY = shotY - currentY;
        }
        velocityX = tempVelocityX / 3;
        velocityY = tempVelocityY / 2;
    }

    //method to throw rock
    public void throwRock(ArrayList<YLockControl> physTeam) {
        if (shotDone.get(physTeam.size() - 1) == false) {
            physTeam.get(physTeam.size() - 1).setLinearVelocity(new Vector3f(-velocityY, 0, -velocityX));
            float random = (float) ((Math.random() <= 0.5) ? (Math.random() * -5) : (Math.random() * 5));
            physTeam.get(physTeam.size() - 1).setAngularVelocity(new Vector3f(0, random, 0));
            shotDone.set(physTeam.size() - 1, true);
            scoreboard.setTotalShots(scoreboard.getTotalShots() + 1);
        }
    }

    //method to get initial cursor coordinates
    public void setThrowValue() {
        shotX = inputManager.getCursorPosition().x;
        shotY = inputManager.getCursorPosition().y;
    }

    //method to stock all rocks in motion
    public void stopRock() {
        for (int i = 0; i < physTeam.size(); i++) {
            physTeam.get(i).setLinearVelocity(Vector3f.ZERO);
        }
        shotDone.set(physTeam.size() - 1, true);
    }

    /*method to check if all rock in play are motionless, 
    returns TRUE if there are no rocks in motion*/
    public boolean noMouvement(ArrayList<YLockControl> physTeam) {
        int allTrue = 0;
        for (int i = 0; i < physTeam.size(); i++) {
            if (physTeam.get(i).getLinearVelocity().length() == 0) {
                allTrue += 1;
            }
        }
        return (allTrue == physTeam.size());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //simpleRender method, not used
    }

 
    //reset all variables for following round, when SPACEBAR is pressed after each round
    private void resetRound() {
        //reset values
        roundIsDone = true;
        scoreboard.setTotalShots(0);
        for (int i = 0; i < rockTeam1.length; i++) {
            //remove all controls and spatials from the scene
            rockTeam1[i].getRockModel().removeControl(controlTeam1[i]);
            rockTeam2[i].getRockModel().removeControl(controlTeam2[i]);

            bulletAppState.getPhysicsSpace().remove(controlTeam1[i]);
            bulletAppState.getPhysicsSpace().remove(controlTeam2[i]);

            rockTeam1[i].getRockModel().removeFromParent();
            rockTeam2[i].getRockModel().removeFromParent();

            //clear all arrays
            rockTeam1[i] = null;
            rockTeam2[i] = null;
            distanceFromCenterTeam1[i] = 0;
            distanceFromCenterTeam2[i] = 0;
            controlTeam1[i] = null;
            controlTeam2[i] = null;
        }
        //update round
        scoreboard.setRound(scoreboard.getRound() + 1);
        //clear physics arrayList
        physTeam.clear();
        //update boolean values for shots
        Collections.fill(shotDone, Boolean.FALSE);
        shotDone.set(0, true);
        //update round number showed on hud screen
        if (scoreboard.getRound() < scoreboard.getNumberOfRounds()) {
            updateRoundDisplayed();
            updateHammer();
            discardEndOfRoundMessage();
        }
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        ScreenHud = screen;
        System.out.println("bind( " + screen.getScreenId() + ")");
    }

    @Override
    public void onStartScreen() {
        System.out.println("onStartScreen");
    }

    @Override
    public void onEndScreen() {
        System.out.println("onEndScreen");
    }

    public void quit() {
        nifty.gotoScreen("end");

    }

    public void startGame(String nextScreen) {
        System.out.print("startgame");

        nifty.gotoScreen(nextScreen);

    }

    public void teamSelection(String nextScreen) {

        nifty.gotoScreen("hud");
        unlockCommand();
        team1();
        team2();
        scoreTeam1();
        scoreTeam2();
        gameIsStarted = true;

        if (scoreboard.getHammer() == 2) {
            playerTurn(1);
        } else {
            playerTurn(2);
        }

    }

    public void closePopup() {
        nifty.gotoScreen("hud");

    }

    @NiftyEventSubscriber(id = "imageSelect1")
    public void image1(String id, ImageSelectSelectionChangedEvent event) {
        event1 = event;
        System.out.println("team 1 selected " + event.getSelectedIndex());
        team1Name = getTeam1Name();

    }

    @NiftyEventSubscriber(id = "imageSelect2")
    public void image2(String id, ImageSelectSelectionChangedEvent event) {
        event2 = event;
        System.out.println("team 2 selected " + event.getSelectedIndex());
        team2Name = getTeam2Name();

    }
    //team 1 country selection

    public void team1() {

        // find old text
        Element niftyElement = nifty.getScreen("hud").findElementById("team1");
        // swap old with new text
        niftyElement.getRenderer(TextRenderer.class).setText(team1Name);

    }//team 1 score

    public void scoreTeam1() {
        int score = scoreboard.getTeam1TotalScore();
        String team1Score = Integer.toString(score);

        // find old text
        Element niftyElement = nifty.getScreen("hud").findElementById("scoreTeam1");
        // swap old with new text
        niftyElement.getRenderer(TextRenderer.class).setText(team1Score);

    }
    //team 2 country selection

    public void team2() {

        // find old text
        Element niftyElement = nifty.getScreen("hud").findElementById("team2");
        // swap old with new text
        niftyElement.getRenderer(TextRenderer.class).setText(team2Name);

    }
    //team 2 score 

    public void scoreTeam2() {
        int score = scoreboard.getTeam2TotalScore();

        String team2Score = Integer.toString(score);

        // find old text
        Element niftyElement = nifty.getScreen("hud").findElementById("scoreTeam2");
        // swap old with new text
        niftyElement.getRenderer(TextRenderer.class).setText(team2Score);
    }

    public String getTeam1Name() {
        String team1Name;
        switch (event1.getSelectedIndex()) {
            case 0:
                team1Name = "Italy";
                scoreboard.setTeam1Name("Italy");
                break;
            case 1:
                team1Name = "Finland";
                scoreboard.setTeam1Name("Finland");
                break;
            case 2:
                team1Name = "China";
                scoreboard.setTeam1Name("China");
                break;
            case 3:
                team1Name = "USA";
                scoreboard.setTeam1Name("USA");
                break;
            case 4:
                team1Name = "Sweden";
                scoreboard.setTeam1Name("Sweden");
                break;
            default:
                team1Name = "Italy";
                scoreboard.setTeam1Name("Italy");
        }

        return team1Name;
    }

    public String getTeam2Name() {
        String team2Name;
        switch (event2.getSelectedIndex()) {
            case 0:
                team2Name = "Italy";
                scoreboard.setTeam2Name("Italy");
                break;
            case 1:
                team2Name = "Finland";
                scoreboard.setTeam2Name("Finland");
                break;
            case 2:
                team2Name = "China";
                scoreboard.setTeam2Name("China");
                break;
            case 3:
                team2Name = "USA";
                scoreboard.setTeam2Name("USA");
                break;
            case 4:
                team2Name = "Sweden";
                scoreboard.setTeam2Name("Sweden");
                break;
            default:
                team2Name = "Italy";
                scoreboard.setTeam2Name("Italy");
        }

        return team2Name;
    }

    //sends the round number to the hud
    public void updateRoundDisplayed() {
        // find old text
        Element niftyElement = nifty.getScreen("hud").findElementById("roundNumber");
        // swap old with new text
        niftyElement.getRenderer(TextRenderer.class).setText("Round " + Integer.toString(scoreboard.getRound() + 1));
    }

    public void unlockCommand() {

        unlockCommands = true;

    }

    //MAXIME: METHOD THAT SAYS TO USER WHO HAS THE HAMMER!!!!!!!!!!!!!***********************************************************
    /* Max j'imagine que tu seras pas capable de faire de pop ups pr le hammer, alors j'ai pensé
    que tu pourrais juste mettre une petite boite en bas a droite pi dire qqch comme:
    "TEAM USA has the hammer this round" pi tu le update a chaque round en mm temps que le round number*/
 /* 
    
    Ce que tu as à faire Max:
        1) Mettre le message de qui a le Hammer (avec updateHammer() juste en bas)
        2) Mettre le nom des équipes en couleur dans le scoreboard (team1 = roches rouges; team2 = roches jaunes)
        3) Mettre un message pour qui a gagné la partie après les 10 rondes (voir la methode showEndOfRoundMessage()) 
        4) Si possible, essaye de dire, au début de chaque manche qui commence par lancer. Fait comme ca
            /*  if(scoreboard.getHammer()== 2){
                //print: setText("TEAM " + scoreboard.getTeam1Name() + " starts this round");
                } else{
                //print: setText("TEAM " + scoreboard.getTeam2Name() + " starts this round");
                }   
    
    j'ai déja commencé plusieurs methodes mais jsp comment les faire au complet
     */
    //mettre ce texte dans le coin en bas à droite, j'ai déjà call la method en haut, en mm temps que updateRound
    public void updateHammer() {
        //check if the game is finished
        if (scoreboard.getRound() < scoreboard.getNumberOfRounds()) {
            if (scoreboard.getHammer() == 2) {
                //print: .setText("Team " + scoreboard.getTeam2Name() + " has the hammer this round");
            } else {
                //print: .setText("Team " + scoreboard.getTeam1Name() + " has the hammer this round");
            }
        } else {
            //if the game is finished do not update the hammer
            //print: "null"
        }
    }

   //show end of round message
    public void showEndOfRoundMessage() {
        //check if the game is finished
        if (scoreboard.getRound() < (scoreboard.getNumberOfRounds() - 1)) {
            // find old text
            Element niftyElement = nifty.getScreen("hud").findElementById("pressEnter");
            // swap old with new text
            niftyElement.getRenderer(TextRenderer.class).setText(scoreboard.getRoundWinner() + "\nPress 1, 2 or 3 to view the final rock placement"
                    + "\n\nPress ENTER to begin next round");
        } else if (isDone) {
            // find old text
            Element niftyElement = nifty.getScreen("hud").findElementById("pressEnter");
            niftyElement.getRenderer(TextRenderer.class).setText("");
            // swap old with new text
            niftyElement.getRenderer(TextRenderer.class).setText(scoreboard.getRoundWinner() + "\nPress 1, 2 or 3 to view the final rock placement"
                    + "\n\nPress ENTER to finish the game");
            isDone = false;
            temp = ENTERIsPressed;

        } else if (!isDone && (ENTERIsPressed > temp)) {
            cam.setLocation(new Vector3f(67.61219f, 18.359352f, -56.51864f));
            cam.lookAtDirection(new Vector3f(-0.904703f, -0.17295352f, 0.38940513f), new Vector3f(0, 1, 0));
            alternateCamAngle = true;
            flyCam.setEnabled(false);
            // find old text
            Element niftyElement = nifty.getScreen("hud").findElementById("pressEnter");
            // swap old with new text
            niftyElement.getRenderer(TextRenderer.class).setText("");
            niftyElement.getRenderer(TextRenderer.class).setText(scoreboard.getGameWinner());
        }
    }

    //discard end of round message
    public void discardEndOfRoundMessage() {
        // find old text
        Element niftyElement = nifty.getScreen("hud").findElementById("pressEnter");
        // swap old with new text
        niftyElement.getRenderer(TextRenderer.class).setText("");
    }

    public void option(String nextScreen) {
        nifty.gotoScreen(nextScreen);

    }

    public void mainMenu() {
        nifty.gotoScreen("start");

    }

    public void sound() {
        nifty.gotoScreen("sound");
    }

    public void quitGame() {
        System.exit(0);
    }

    //Method to initialise all textures in the scene
    public void setMaterials() {
        //set transparent material for the ghost detection shape
        transMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        transMat.setColor("Color", new ColorRGBA(1, 0, 0, 0f));
        transMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        //Texture for the ice
        iceMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        iceMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/iceMat.jpg"));

        //Texture for rink boards
        sideBoardsMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture sideBoardsTex = assetManager.loadTexture(new TextureKey("Textures/sideBoardsMat.jpg", false));
        sideBoardsTex.setWrap(Texture.WrapMode.Repeat);
        sideBoardsMat.setTexture("ColorMap", sideBoardsTex);
        topBoardsMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture topBoardsTex = assetManager.loadTexture(new TextureKey("Textures/blueMat.jpg", false));
        topBoardsTex.setWrap(Texture.WrapMode.Repeat);
        topBoardsMat.setTexture("ColorMap", topBoardsTex);

        //Texture for rink walls
        wallsMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture wallsTex = assetManager.loadTexture(new TextureKey("Textures/wallsMat.jpg", false));
        wallsTex.setWrap(Texture.WrapMode.Repeat);
        wallsMat.setTexture("ColorMap", wallsTex);

        //Texture for roof
        roofMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture roofTex = assetManager.loadTexture(new TextureKey("Textures/roofMat.jpg", false));
        roofTex.setWrap(Texture.WrapMode.Repeat);
        roofMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        roofMat.setTexture("ColorMap", roofTex);

        //Texture for roof walls
        sideRoofMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture sideRoofTex = assetManager.loadTexture(new TextureKey("Textures/sideRoofMat.jpg", false));
        sideRoofTex.setWrap(Texture.WrapMode.Repeat);
        sideRoofMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        sideRoofMat.setTexture("ColorMap", sideRoofTex);

        //Textures for house circles
        bigRingMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture bigRingTex = assetManager.loadTexture(new TextureKey("Textures/bigRingMat.jpg", false));
        bigRingTex.setWrap(Texture.WrapMode.Repeat);
        bigRingMat.setTexture("ColorMap", bigRingTex);
        smallRingMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture smallRingTex = assetManager.loadTexture(new TextureKey("Textures/smallRingMat.jpg", false));
        smallRingTex.setWrap(Texture.WrapMode.Repeat);
        smallRingMat.setTexture("ColorMap", smallRingTex);

        //Texture for rink stands
        standsMat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture standsTex1 = assetManager.loadTexture(new TextureKey("Textures/standsMat1.jpg", false));
        standsTex1.setWrap(Texture.WrapMode.Repeat);
        standsMat1.setTexture("ColorMap", standsTex1);
        standsMat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture standsTex2 = assetManager.loadTexture(new TextureKey("Textures/standsMat2.jpg", false));
        standsTex2.setWrap(Texture.WrapMode.Repeat);
        standsMat2.setTexture("ColorMap", standsTex2);

        //Texture for rink lines
        linesMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        linesMat.setTexture("ColorMap", assetManager.loadTexture("Textures/linesMat.jpg"));

        //Texture for curling rocks
        redMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMat.setTexture("ColorMap", assetManager.loadTexture("Textures/redMat.jpg"));
        blueMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blueMat.setTexture("ColorMap", assetManager.loadTexture("Textures/blueMat.jpg"));
    }

    //Method to set all the textures to the objects in the scene
    public void setCoordinates() {

        sceneNode = (Node) floorScene;

        //Material for the rink's ice
        sceneNode.getChild("Ice").setMaterial(iceMat);

        //Materials for the rink's walls
        sceneNode.getChild("rink_entrance").setMaterial(sideBoardsMat);
        ((Geometry) sceneNode.getChild("rink_entrance")).getMesh().scaleTextureCoordinates(new Vector2f(2.8f, 4f));
        sceneNode.getChild("entrance_wall").setMaterial(wallsMat);
        ((Geometry) sceneNode.getChild("entrance_wall")).getMesh().scaleTextureCoordinates(new Vector2f(1, 4));
        sceneNode.getChild("back_wall").setMaterial(wallsMat);
        ((Geometry) sceneNode.getChild("back_wall")).getMesh().scaleTextureCoordinates(new Vector2f(1, 4));
        sceneNode.getChild("left_wall").setMaterial(wallsMat);
        ((Geometry) sceneNode.getChild("left_wall")).getMesh().scaleTextureCoordinates(new Vector2f(1, 40));
        sceneNode.getChild("right_wall").setMaterial(wallsMat);
        ((Geometry) sceneNode.getChild("right_wall")).getMesh().scaleTextureCoordinates(new Vector2f(1, 40));

        //Materials for the rink's roof
        sceneNode.getChild("back_wall_roof").setMaterial(sideRoofMat);
        ((Geometry) sceneNode.getChild("back_wall_roof")).getMesh().scaleTextureCoordinates(new Vector2f(2, 2));
        sceneNode.getChild("entrance_wall_roof").setMaterial(sideRoofMat);
        ((Geometry) sceneNode.getChild("entrance_wall_roof")).getMesh().scaleTextureCoordinates(new Vector2f(2, 2));
        sceneNode.getChild("roof").setMaterial(roofMat);
        ((Geometry) sceneNode.getChild("roof")).getMesh().scaleTextureCoordinates(new Vector2f(5, 10));

        //Materials for the house rings
        sceneNode.getChild("big_ring_backHouse").setMaterial(bigRingMat);
        ((Geometry) sceneNode.getChild("big_ring_backHouse")).getMesh().scaleTextureCoordinates(new Vector2f(40, 40));
        sceneNode.getChild("big_ring_frontHouse").setMaterial(bigRingMat);
        ((Geometry) sceneNode.getChild("big_ring_frontHouse")).getMesh().scaleTextureCoordinates(new Vector2f(40, 40));
        sceneNode.getChild("small_ring_backHouse").setMaterial(smallRingMat);
        ((Geometry) sceneNode.getChild("small_ring_backHouse")).getMesh().scaleTextureCoordinates(new Vector2f(40, 40));
        sceneNode.getChild("small_ring_frontHouse").setMaterial(smallRingMat);
        ((Geometry) sceneNode.getChild("small_ring_frontHouse")).getMesh().scaleTextureCoordinates(new Vector2f(40, 40));

        //Materials for the rink lines
        sceneNode.getChild("line_middle_frontHouse").setMaterial(linesMat);
        sceneNode.getChild("line_end_frontHouse").setMaterial(linesMat);
        sceneNode.getChild("line_middle_rink").setMaterial(linesMat);
        sceneNode.getChild("line_middle_backHouse").setMaterial(linesMat);
        sceneNode.getChild("line_end_backHouse").setMaterial(linesMat);
        sceneNode.getChild("line_start_backHouse").setMaterial(linesMat);
        sceneNode.getChild("line_start_frontHouse").setMaterial(linesMat);

        //Materials for the boards
        sceneNode.getChild("top_right_board").setMaterial(topBoardsMat);
        sceneNode.getChild("top_left_board").setMaterial(topBoardsMat);
        sceneNode.getChild("right_board").setMaterial(sideBoardsMat);
        ((Geometry) sceneNode.getChild("right_board")).getMesh().scaleTextureCoordinates(new Vector2f(2.8f, 90f));
        sceneNode.getChild("left_board").setMaterial(sideBoardsMat);
        ((Geometry) sceneNode.getChild("left_board")).getMesh().scaleTextureCoordinates(new Vector2f(2.8f, 90f));

        //Materials for the stands
        sceneNode.getChild("step1_right_stands").setMaterial(standsMat2);
        ((Geometry) sceneNode.getChild("step1_right_stands")).getMesh().scaleTextureCoordinates(new Vector2f(3, 100));
        sceneNode.getChild("step2_right_stands").setMaterial(standsMat1);
        ((Geometry) sceneNode.getChild("step2_right_stands")).getMesh().scaleTextureCoordinates(new Vector2f(3, 100));
        sceneNode.getChild("step3_right_stands").setMaterial(standsMat2);
        ((Geometry) sceneNode.getChild("step3_right_stands")).getMesh().scaleTextureCoordinates(new Vector2f(3, 100));
        sceneNode.getChild("step4_right_stands").setMaterial(standsMat1);
        ((Geometry) sceneNode.getChild("step4_right_stands")).getMesh().scaleTextureCoordinates(new Vector2f(3, 100));
        sceneNode.getChild("step1_left_stands").setMaterial(standsMat2);
        ((Geometry) sceneNode.getChild("step1_left_stands")).getMesh().scaleTextureCoordinates(new Vector2f(3, 100));
        sceneNode.getChild("step2_left_stands").setMaterial(standsMat1);
        ((Geometry) sceneNode.getChild("step2_left_stands")).getMesh().scaleTextureCoordinates(new Vector2f(3, 100));
        sceneNode.getChild("step3_left_stands").setMaterial(standsMat2);
        ((Geometry) sceneNode.getChild("step3_left_stands")).getMesh().scaleTextureCoordinates(new Vector2f(3, 100));
        sceneNode.getChild("step4_left_stands").setMaterial(standsMat1);
        ((Geometry) sceneNode.getChild("step4_left_stands")).getMesh().scaleTextureCoordinates(new Vector2f(3, 100));

        //set nodes in the scene
        originRockPos = sceneNode.getChild("Origin").getLocalTranslation();
        camView = sceneNode.getChild("camView").getLocalTranslation();
        centerPos = sceneNode.getChild("Center").getLocalTranslation();
        extremity = sceneNode.getChild("Extremity").getLocalTranslation();
        cylinderRadius = centerPos.distance(extremity);

        //Reposition the houses' circles in the scene to be over the ice surface)
        sceneNode.getChild("big_ring_backHouse").removeControl(sceneGeo);
        sceneNode.getChild("big_ring_backHouse").move(0, 0.1f, 0);
        sceneNode.getChild("big_ring_frontHouse").removeControl(sceneGeo);
        sceneNode.getChild("big_ring_frontHouse").move(0, 0.1f, 0);
        sceneNode.getChild("small_ring_backHouse").removeControl(sceneGeo);
        sceneNode.getChild("small_ring_backHouse").move(0, 0.1f, 0);
        sceneNode.getChild("small_ring_frontHouse").removeControl(sceneGeo);
        sceneNode.getChild("small_ring_frontHouse").move(0, 0.1f, 0);

//        Add directional light to the scene
        PointLight light = new PointLight();
        light.setColor(ColorRGBA.White);
        light.setPosition(new Vector3f(0, 20, -10.340717f));
        light.setRadius(150f);
        rootNode.addLight(light);

        PointLight light2 = new PointLight();
        light2.setColor(ColorRGBA.White);
        light2.setPosition(new Vector3f(171.03542f, 15f, -10.140717f));
        light2.setRadius(1000f);
        rootNode.addLight(light2);

        PointLight light3 = new PointLight();
        light3.setColor(ColorRGBA.White);
        light3.setPosition(centerPos.add(0, 20, 0));
        light3.setRadius(1000f);
        rootNode.addLight(light3);

        DirectionalLight dLight = new DirectionalLight();
        dLight.setColor(ColorRGBA.White);
        dLight.setDirection(new Vector3f(1, 0f, 0));
        rootNode.addLight(dLight);

        //Create shadow object and add it to the scene
        final int SHADOWMAP_SIZE = 1024;
        PointLightShadowRenderer shadow = new PointLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
        shadow.setLight(light);
        shadow.setShadowIntensity(0.2f);
        shadow.setShadowZExtend(500f);
        viewPort.addProcessor(shadow);

//        //Add shadow filter for better and more realistic shadows
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
//        fpp.addFilter(ssaoFilter);
//        viewPort.addProcessor(fpp);
        //Add shadows for particular objects in the scene
        rootNode.setShadowMode(ShadowMode.Off);
        sceneNode.getChild("Ice").setShadowMode(ShadowMode.Receive);
        sceneNode.getChild("rink_entrance").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("top_right_board").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("top_left_board").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("right_board").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("left_board").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("step1_right_stands").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("step2_right_stands").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("step3_right_stands").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("step4_right_stands").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("step1_left_stands").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("step2_left_stands").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("step3_left_stands").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("step4_left_stands").setShadowMode(ShadowMode.CastAndReceive);
        sceneNode.getChild("big_ring_backHouse").setShadowMode(ShadowMode.Receive);
        sceneNode.getChild("big_ring_frontHouse").setShadowMode(ShadowMode.Receive);
    }

    //Method to prepare the scene
    public void setScene() {
        //load the scene model
        floorScene = assetManager.loadModel("Scenes/ARENA.j3o");
        sceneGeo = new RigidBodyControl(0f);
        floorScene.setLocalTranslation(Vector3f.ZERO);
        floorScene.addControl(sceneGeo);
        sceneGeo.setPhysicsLocation(floorScene.getLocalTranslation());
        sceneGeo.setKinematicSpatial(false);

        //set the bouncing factor for collisions
        sceneGeo.setRestitution(0.9f);

    }

    //Method for GUI
    public void Init_Nifty() {
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/HelloJme.xml", "start", this);

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);

    }

    public void initMapping() {
        //creation of command mapping for in-game keyboard and mouse controls
        inputManager.addMapping("throw", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("stop", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("resetRound", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("get1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("get2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("get3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("damping", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.setMouseCursor(null);
        inputManager.setCursorVisible(true);
    }

    public void setSpatials() {
        //create 3D arrow for direction of throw
        Arrow arrow = new Arrow(new Vector3f(-5, 0, 0));
        arrowGeo = assetManager.loadModel("Models/arrow.j3o");
        arrowGeo.setMaterial(blueMat);
        arrowGeo.setLocalTranslation(originRockPos.add(2, 1, 2));
        arrowGeo.setName("arrowGeo");

        Quaternion x90 = new Quaternion();
        x90.fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0));

        Quaternion y180 = new Quaternion();
        y180.fromAngleAxis(FastMath.PI, new Vector3f(0, 1, 0));
        firstArrowRotation = x90.mult(y180);

        //attaching spatials to rootNode
        rootNode.attachChild(floorScene);

    }

    public void playerTurn(int team) {
        niftyTurn = nifty.getScreen("hud").findElementById("playerTurnText");
        niftyTurn.disable();
        if (team == 1) {
            niftyTurn.getRenderer(TextRenderer.class).setText("Player 1's turn");
        } else {
            niftyTurn.getRenderer(TextRenderer.class).setText("Player 2's turn");
        }
        niftyTurn.enable();
    }

}
