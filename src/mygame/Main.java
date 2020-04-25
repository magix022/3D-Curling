package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
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
import com.jme3.light.SpotLight;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class Main extends SimpleApplication implements ScreenController {

    private BulletAppState bulletAppState;

    private Vector3f camPos;
    private Vector3f camView;
    private Vector3f originRockPos;
    private Vector3f centerPos;
    private Vector3f extremity;

    private Spatial floorScene;
    private RigidBodyControl sceneGeo;

    private Spatial arrowGeo;

    //Material for the scene
    private Material transMat;

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

    private Material blueMat;
    private Material redMat;

    private Node sceneNode;

    //use this to check if rock is in house
    private GhostControl houseGhost;

    //Initializers necessary for scoreboard and score
    private Rock[] rockTeam1 = new Rock[4];
    private Rock[] rockTeam2 = new Rock[4];

    private double[] distanceFromCenterTeam1 = new double[4];
    private double[] distanceFromCenterTeam2 = new double[4];

    private YLockControl[] controlTeam1 = new YLockControl[4];
    private YLockControl[] controlTeam2 = new YLockControl[4];

    //ArrayList of YLockControl to add elements at the end of the array
    ArrayList<YLockControl> physTeam = new ArrayList();

    //Create ScoreBoard object
    ScoreBoardClass scoreboard = new ScoreBoardClass();

    //ArrayList of shotDone to add elements at the end of the array and check if a particular rock is thrown
    ArrayList<Boolean> shotDone = new ArrayList(Arrays.asList(new Boolean[8]));

    private float shotX = 0;
    private float shotY = 0;
    private float velocityX;
    private float velocityY;

    private Quaternion arrowRotation = new Quaternion();
    private Quaternion firstArrowRotation = new Quaternion();

    boolean roundIsDone = true;
    boolean gameIsFinished = true;

    private Boolean camStatus;
    private Screen screen;
    private AudioNode audio;
    private Nifty nifty;
    private Boolean unlockCommands = false;
    private Boolean shotHasBeenSet = false;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

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

    @Override
    public void simpleInitApp() {

        Init_Nifty();
        setMaterials();
        setScene();
        setCoordinates();

        //set number of rounds
        scoreboard.setNumberOfRounds(10);
        scoreboard.setHammer((int) (Math.random()) + 1);
        //set initial round to 1
        scoreboard.setRound(0);

        //boolean value set at true to start first shot, all other values are currently false
        Collections.fill(shotDone, Boolean.FALSE);
        shotDone.set(0, true);

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);

        Arrow arrow = new Arrow(new Vector3f(-5, 0, 0));

//        arrow.setArrowExtent(new Vector3f(1,0,0));
//        arrowGeo = new Geometry("Arrow", arrow);
        arrowGeo = assetManager.loadModel("Models/arrow.j3o");
        arrowGeo.setMaterial(blueMat);
        arrowGeo.setLocalTranslation(originRockPos.add(2, 1, 2));
        arrowGeo.setName("arrowGeo");

        //creation of command mapping
        inputManager.addMapping("throw", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("stop", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("resetRound", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("get", new KeyTrigger(KeyInput.KEY_F));
        inputManager.setMouseCursor(null);
        inputManager.setCursorVisible(true);

        //creation of cylinder node for detection of collision inside house
        float cylinderRadius = centerPos.distance(extremity);
        Cylinder cylinder = new Cylinder(100, 100, cylinderRadius - 5f, 3f);
        Geometry cylin = new Geometry("Cylinder", cylinder);
        Quaternion x90 = new Quaternion();
        x90.fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0));
        MeshCollisionShape ghostShape = new MeshCollisionShape(cylin.getMesh());
        houseGhost = new GhostControl(ghostShape);
        cylin.addControl(houseGhost);
        cylin.setLocalRotation(x90);
        cylin.setLocalTranslation(centerPos);

        Quaternion y180 = new Quaternion();
        y180.fromAngleAxis(FastMath.PI, new Vector3f(0, 1, 0));
        firstArrowRotation = x90.mult(y180);

        //setting materials to spatials
        cylin.setQueueBucket(Bucket.Translucent);
        cylin.setMaterial(transMat);

        //attaching spatials to rootNode
        rootNode.attachChild(cylin);
        rootNode.attachChild(floorScene);
//        rootNode.attachChild(arrowGeo);

        //adding physics to physicsSpace
        bulletAppState.getPhysicsSpace().add(sceneGeo);
        bulletAppState.getPhysicsSpace().add(houseGhost);

        //camera parameters
        flyCam.setMoveSpeed(50f);
        flyCam.setDragToRotate(true);
        flyCam.setEnabled(false);
        cam.lookAtDirection(new Vector3f(-1, -0.3f, 0), new Vector3f(0, 1, 0));
    }

    public void setMaterials() {
        transMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        transMat.setColor("Color", new ColorRGBA(1, 0, 0, 0f));
        transMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        //Texture for the ice
        iceMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        iceMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/iceMat.jpg"));
//        ice.setReceivesShadows(true);

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

        //materials for the rocks
        redMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMat.setTexture("ColorMap", assetManager.loadTexture("Textures/redMat.jpg"));

        blueMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blueMat.setTexture("ColorMap", assetManager.loadTexture("Textures/blueMat.jpg"));
    }

    public void setScene() {
        floorScene = assetManager.loadModel("Scenes/ARENA.j3o");
        sceneGeo = new RigidBodyControl(0f);

        floorScene.setLocalTranslation(Vector3f.ZERO);
        floorScene.addControl(sceneGeo);

        sceneGeo.setPhysicsLocation(floorScene.getLocalTranslation());
        sceneGeo.setKinematicSpatial(false);
        //bounce
        sceneGeo.setRestitution(0.9f);
    }

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

        originRockPos = sceneNode.getChild("Origin").getLocalTranslation();
        camView = sceneNode.getChild("camView").getLocalTranslation();
        centerPos = sceneNode.getChild("Center").getLocalTranslation();
        extremity = sceneNode.getChild("Extremity").getLocalTranslation();
        //camPos = sceneNode.getChild("camPos").getLocalTranslation();

        sceneNode.getChild("big_ring_backHouse").removeControl(sceneGeo);
        sceneNode.getChild("big_ring_backHouse").move(0, 0.5f, 0);

        sceneNode.getChild("big_ring_frontHouse").removeControl(sceneGeo);
        sceneNode.getChild("big_ring_frontHouse").move(0, 0.5f, 0);

        sceneNode.getChild("small_ring_backHouse").removeControl(sceneGeo);
        sceneNode.getChild("small_ring_backHouse").move(0, 0.5f, 0);

        sceneNode.getChild("small_ring_frontHouse").removeControl(sceneGeo);
        sceneNode.getChild("small_ring_frontHouse").move(0, 0.5f, 0);

        //Add light to the scene
        DirectionalLight light = new DirectionalLight();
        light.setColor(ColorRGBA.White);
        light.setDirection(new Vector3f(2, -10, 4));
        rootNode.addLight(light);

        //Add shawdos for particular meshes in the scene
        final int SHADOWMAP_SIZE = 1024;
        DirectionalLightShadowRenderer shadow = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        shadow.setLight(light);
        shadow.setShadowIntensity(0.2f);
        shadow.setShadowZExtend(500f);
        viewPort.addProcessor(shadow);

        //Add filter for better and more realistic shadows
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
        fpp.addFilter(ssaoFilter);
        viewPort.addProcessor(fpp);

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
        sceneNode.getChild("small_ring_backHouse").setMaterial(smallRingMat);
        sceneNode.getChild("small_ring_frontHouse").setMaterial(smallRingMat);
        sceneNode.getChild("line_middle_frontHouse").setMaterial(linesMat);
        sceneNode.getChild("line_end_frontHouse").setMaterial(linesMat);
        sceneNode.getChild("line_middle_rink").setMaterial(linesMat);
        sceneNode.getChild("line_middle_backHouse").setMaterial(linesMat);
        sceneNode.getChild("line_end_backHouse").setMaterial(linesMat);
        sceneNode.getChild("line_start_backHouse").setMaterial(linesMat);
        sceneNode.getChild("line_start_frontHouse").setMaterial(linesMat);
    }

    public void getDistanceFromCenter(Vector3f centerPos) {
        for (int count = 0; count < 4; count++) {
            distanceFromCenterTeam1[count] = rockTeam1[count].getDistanceFrom(centerPos);
            distanceFromCenterTeam2[count] = rockTeam2[count].getDistanceFrom(centerPos);
        }
    }

//method for sorting all team rock arrays
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
        selectionSort(distanceFromCenterTeam1, rockTeam1, controlTeam1);
        selectionSort(distanceFromCenterTeam2, rockTeam2, controlTeam2);
        updateInHouse();

        if (distanceFromCenterTeam1[0] <= distanceFromCenterTeam2[0] && rockTeam1[0].getInHouse()) {              //Scoring for Team1
            if (distanceFromCenterTeam1[1] < distanceFromCenterTeam2[0] && rockTeam1[1].getInHouse()) {
                if (distanceFromCenterTeam1[2] < distanceFromCenterTeam2[0] && rockTeam1[2].getInHouse()) {
                    if (distanceFromCenterTeam1[3] < distanceFromCenterTeam2[0] && rockTeam1[3].getInHouse()) {
                        scoreboard.setTeam1RoundScore(scoreboard.getRound(), 4);
                    } else {
                        scoreboard.setTeam1RoundScore(scoreboard.getRound(), 3);
                    }
                } else {
                    scoreboard.setTeam1RoundScore(scoreboard.getRound(), 2);
                }
            } else {
                scoreboard.setTeam1RoundScore(scoreboard.getRound(), 1);
            }
        } else {
            scoreboard.setTeam1RoundScore(scoreboard.getRound(), 0);
        }

        if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[0] && rockTeam2[0].getInHouse()) {                  //Scoring for Team2
            if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[1] && rockTeam2[1].getInHouse()) {
                if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[2] && rockTeam2[2].getInHouse()) {
                    if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[3] && rockTeam2[3].getInHouse()) {
                        scoreboard.setTeam2RoundScore(scoreboard.getRound(), 4);
                    } else {
                        scoreboard.setTeam2RoundScore(scoreboard.getRound(), 3);
                    }
                } else {
                    scoreboard.setTeam2RoundScore(scoreboard.getRound(), 2);
                }
            } else {
                scoreboard.setTeam2RoundScore(scoreboard.getRound(), 1);
            }
        } else {
            scoreboard.setTeam2RoundScore(scoreboard.getRound(), 0);
        }

        scoreboard.calculateTotalScoreTeam1();
        scoreboard.calculateTotalScoreTeam2();
    }

    public void updateInHouse() {
        for (int i = 0; i < 4; i++) {
            if (houseGhost.getOverlappingObjects().contains(controlTeam1[i])) {
                rockTeam1[i].setInHouse(true);
            } else {
                rockTeam1[i].setInHouse(false);
            }

            if (houseGhost.getOverlappingObjects().contains(controlTeam2[i])) {
                rockTeam2[i].setInHouse(true);
            } else {
                rockTeam2[i].setInHouse(false);
            }
        }
    }

    public void displayScore() {
        System.out.println("This round's score is :");
        System.out.println("Team 1: " + scoreboard.getTeam1RoundScore(scoreboard.getRound())
                + "\tTeam 2: " + scoreboard.getTeam2RoundScore(scoreboard.getRound()));
        System.out.println(scoreboard.getRoundWinner());

        System.out.println("The total score after round " + (scoreboard.getRound() + 1) + " is :");
        System.out.println("Team 1: " + scoreboard.getTeam1TotalScore() + "\tTeam 2: " + scoreboard.getTeam2TotalScore() + "\n");
    }

    public Rock createRock(int team, int index, Rock[] rockTeam, ArrayList<YLockControl> physTeam, float tpf) {
        Rock rock = new Rock(team);
        rock.setRockModel(assetManager.loadModel(rock.getModelPath()));
        rock.getRockModel().setLocalTranslation(originRockPos.add(2, 0, 2f));

        if (team == 1) {
            rock.getRockModel().setMaterial(redMat);
        } else {
            rock.getRockModel().setMaterial(blueMat);
        }

        rootNode.attachChild(rock.getRockModel());

        YLockControl rockPhy = new YLockControl(1f);
        rock.getRockModel().addControl(rockPhy);
        rockPhy.setRestitution(1f);
        rockPhy.setLinearDamping(0.20f);
        bulletAppState.getPhysicsSpace().add(rockPhy);
        rock.getRockModel().setShadowMode(ShadowMode.CastAndReceive);
        rockTeam[index] = rock;
        physTeam.add(rockPhy);

        return rock;
    }

    private ActionListener actionListenerResetRound = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            System.out.println("input for space is received");
            if (name.equals("resetRound") && !keyPressed && scoreboard.getTotalShots() == 8 && noMouvement(physTeam)) {
                System.out.println("if statement executed");
                resetRound();
            }
        }
    };

    private ActionListener actionListenerThrow = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            try {

                if (name.equals("throw") && !keyPressed && unlockCommands && shotHasBeenSet) {
                    throwRock(physTeam);
                    System.out.println("Throw");
                    rootNode.getChild("arrowGeo").removeFromParent();
                }
                if (name.equals("stop") && !keyPressed && unlockCommands) {
                    stopRock();
                }
                if (name.equals("throw") && keyPressed && unlockCommands) {
                    setThrowValue();
                    shotHasBeenSet = true;
                    System.out.println("Set throw");
                    rootNode.attachChild(arrowGeo);
                }
//            if (name.equals("get") && !keyPressed){
//                setThrowValue();
//            }
//            if (name.equals("reset")) {
//                resetPos(rockTeam, originRockPos, rockPhy);
//            }
            } catch (NullPointerException ex) {
                System.out.print("t nul");

            }
        }
    };

    public void updateVelocityValue() {
        float currentX = inputManager.getCursorPosition().x;
        float currentY = inputManager.getCursorPosition().y;

        float tempVelocityX;
        float tempVelocityY;

        if (shotX - currentX > 300) {
            tempVelocityX = 300;
        } else if (shotX - currentX < -300) {
            tempVelocityX = -300;
        } else {
            tempVelocityX = shotX - currentX;
        }

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

    public void throwRock(ArrayList<YLockControl> physTeam) {
        if (shotDone.get(physTeam.size() - 1) == false) {

            physTeam.get(physTeam.size() - 1).setLinearVelocity(new Vector3f(-velocityY, 0, -velocityX));

            shotDone.set(physTeam.size() - 1, true);
            scoreboard.setTotalShots(scoreboard.getTotalShots() + 1);
        }
    }

    public void setThrowValue() {
        shotX = inputManager.getCursorPosition().x;
        shotY = inputManager.getCursorPosition().y;
    }

    public void stopRock() {
        for (int i = 0; i < physTeam.size(); i++) {
            physTeam.get(i).setLinearVelocity(Vector3f.ZERO);
        }
        shotDone.set(physTeam.size() - 1, true);
        
    }

//method to check if all rock in play are motionless
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
    public void simpleUpdate(float tpf) {
//        System.out.println(unlockCommands);
        if (scoreboard.getRound() < scoreboard.getNumberOfRounds()) {
            if (scoreboard.getHammer() == 2) {
                if (shotDone.get(0) == true && physTeam.isEmpty()) {
                    createRock(1, 0, rockTeam1, physTeam, tpf);
                    shotDone.set(0, false);
                }
                if (noMouvement(physTeam) && shotDone.get(0) == true) {
                    createRock(2, 0, rockTeam2, physTeam, tpf);
                    shotDone.set(0, false);
                }
                if (noMouvement(physTeam) && shotDone.get(1) == true) {
                    createRock(1, 1, rockTeam1, physTeam, tpf);
                    shotDone.set(1, false);
                }
                if (noMouvement(physTeam) && shotDone.get(2) == true) {
                    createRock(2, 1, rockTeam2, physTeam, tpf);
                    shotDone.set(2, false);
                }
                if (noMouvement(physTeam) && shotDone.get(3) == true) {
                    createRock(1, 2, rockTeam1, physTeam, tpf);
                    shotDone.set(3, false);
                }
                if (noMouvement(physTeam) && shotDone.get(4) == true) {
                    createRock(2, 2, rockTeam2, physTeam, tpf);
                    shotDone.set(4, false);
                }
                if (noMouvement(physTeam) && shotDone.get(5) == true) {
                    createRock(1, 3, rockTeam1, physTeam, tpf);
                    shotDone.set(5, false);
                }
                if (noMouvement(physTeam) && shotDone.get(6) == true) {
                    createRock(2, 3, rockTeam2, physTeam, tpf);
                    shotDone.set(6, false);
                }
                for (int i = 0; i < physTeam.size(); i++) {
                    physTeam.get(i).controlUpdate(tpf, originRockPos.getY());
                    physTeam.get(i).prePhysicsTick(bulletAppState.getPhysicsSpace(), tpf);
                    physTeam.get(i).physicsTick(bulletAppState.getPhysicsSpace(), tpf);
                }

                //roundIsDone is only to avoid this loop statement from be itterated every time and calling long methods to compute
                if (scoreboard.getTotalShots() == 8 && noMouvement(physTeam) && roundIsDone == true) {
                    for (int i = 0, j = 0; i < physTeam.size() && j < 4; i += 2, j++) {
                        controlTeam1[j] = physTeam.get(i);
                    }
                    for (int i = 1, j = 0; i < physTeam.size() && j < 4; i += 2, j++) {
                        controlTeam2[j] = physTeam.get(i);
                    }
                    //get distance between rocks and center of the house
                    getDistanceFromCenter(centerPos);
                    //get the team that scores and the number of points they score in one particular round
                    calculateScore(distanceFromCenterTeam1, distanceFromCenterTeam2, rockTeam1, rockTeam2, controlTeam1, controlTeam2);
                    displayScore();
                    roundIsDone = false;
                }
            } else {
                //case where team1 has the last shot
                if (shotDone.get(0) == true && physTeam.isEmpty()) {
                    createRock(2, 0, rockTeam2, physTeam, tpf);
                    shotDone.set(0, false);
                }
                if (noMouvement(physTeam) && shotDone.get(0) == true) {
                    createRock(1, 0, rockTeam1, physTeam, tpf);
                    shotDone.set(0, false);
                }
                if (noMouvement(physTeam) && shotDone.get(1) == true) {
                    createRock(2, 1, rockTeam2, physTeam, tpf);
                    shotDone.set(1, false);
                }
                if (noMouvement(physTeam) && shotDone.get(2) == true) {
                    createRock(1, 1, rockTeam1, physTeam, tpf);
                    shotDone.set(2, false);
                }
                if (noMouvement(physTeam) && shotDone.get(3) == true) {
                    createRock(2, 2, rockTeam2, physTeam, tpf);
                    shotDone.set(3, false);
                }
                if (noMouvement(physTeam) && shotDone.get(4) == true) {
                    createRock(1, 2, rockTeam1, physTeam, tpf);
                    shotDone.set(4, false);
                }
                if (noMouvement(physTeam) && shotDone.get(5) == true) {
                    createRock(2, 3, rockTeam2, physTeam, tpf);
                    shotDone.set(5, false);
                }
                if (noMouvement(physTeam) && shotDone.get(6) == true) {
                    createRock(1, 3, rockTeam1, physTeam, tpf);
                    shotDone.set(6, false);
                }
                for (int i = 0; i < physTeam.size(); i++) {
                    physTeam.get(i).controlUpdate(tpf, originRockPos.getY());
                    physTeam.get(i).prePhysicsTick(bulletAppState.getPhysicsSpace(), tpf);
                    physTeam.get(i).physicsTick(bulletAppState.getPhysicsSpace(), tpf);
                }

                //roundIsDone is only to avoid this loop statement from be itterated every time and calling long methods to compute
                if (scoreboard.getTotalShots() == 8 && noMouvement(physTeam) && roundIsDone == true) {
                    for (int i = 0, j = 0; i < physTeam.size() && j < 4; i += 2, j++) {
                        controlTeam2[j] = physTeam.get(i);
                    }
                    for (int i = 1, j = 0; i < physTeam.size() && j < 4; i += 2, j++) {
                        controlTeam1[j] = physTeam.get(i);
                    }
                    //get distance between rocks and center of the house
                    getDistanceFromCenter(centerPos);
                    //get the team that scores and the number of points they score in one particular round
                    calculateScore(distanceFromCenterTeam1, distanceFromCenterTeam2, rockTeam1, rockTeam2, controlTeam1, controlTeam2);
                    displayScore();
                    roundIsDone = false;
                }
            }

            updateVelocityValue();

            double angle = (velocityX / velocityY);
            Quaternion temp = new Quaternion();
            temp.fromAngleAxis((float) Math.atan(angle), new Vector3f(0, 0, -1));

            arrowRotation = firstArrowRotation.mult(temp);
            arrowGeo.setLocalRotation(arrowRotation);
            arrowGeo.setLocalScale(velocityY / 20);

            Vector3f rockCamLocation = physTeam.get(physTeam.size() - 1).getPhysicsLocation().add(15, 5, 0);
            cam.setLocation(rockCamLocation);

            //listener for the left and right click action
            inputManager.addListener(actionListenerThrow, "throw");
            inputManager.addListener(actionListenerThrow, "stop");
            inputManager.addListener(actionListenerResetRound, "resetRound");
            inputManager.addListener(actionListenerThrow, "get");
            
        } else {
            if (gameIsFinished) {
                System.out.println("Game is finished");
                System.out.println("The final score is");
                System.out.println("Team 1: " + scoreboard.getTeam1TotalScore() + "\tTeam 2: " + scoreboard.getTeam2TotalScore() + "\n");
                System.out.println(scoreboard.getGameWinner());

                gameIsFinished = false;
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    //reset all variables for following round
    private void resetRound() {
        roundIsDone = true;
        scoreboard.setTotalShots(0);
        for (int i = 0; i < rockTeam1.length; i++) {

            rockTeam1[i].getRockModel().removeControl(controlTeam1[i]);
            rockTeam2[i].getRockModel().removeControl(controlTeam2[i]);

            bulletAppState.getPhysicsSpace().remove(controlTeam1[i]);
            bulletAppState.getPhysicsSpace().remove(controlTeam2[i]);

            rockTeam1[i].getRockModel().removeFromParent();
            rockTeam2[i].getRockModel().removeFromParent();

            rockTeam1[i] = null;
            rockTeam2[i] = null;

            distanceFromCenterTeam1[i] = 0;
            distanceFromCenterTeam2[i] = 0;

            controlTeam1[i] = null;
            controlTeam2[i] = null;
        }

        scoreboard.setRound(scoreboard.getRound() + 1);
        physTeam.clear();

        Collections.fill(shotDone, Boolean.FALSE);
        shotDone.set(0, true);

    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
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

    public void teamSelection() {

        nifty.gotoScreen("hud");

        unlockCommands = true;

        // ImageSelect select = screen.findNiftyControl("imageSelect", ImageSelect.class);
        // int index = select.getSelectedImageIndex();
        //System.out.print(index);
    }

    public void unlockCommand() {

        try {

//        TimeUnit.SECONDS.sleep(5);
        } catch (Exception ex) {
            System.out.print("Delai exception");
        }
        unlockCommands = true;

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
}
