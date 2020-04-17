package mygame;

import com.jme3.app.SimpleApplication;
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
import com.jme3.light.DirectionalLight;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;

    private Vector3f camPos;
    private Vector3f camView;
    private Vector3f originRockPos;
    private Vector3f centerPos;
    private Vector3f extremity;

    private Spatial floorScene;
    private RigidBodyControl sceneGeo;
    
    private Geometry arrowGeo;

    private Material dirtMat;
    private Material blue;
    private Material transMat;

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

    //Default rock is not in house
    boolean rockHouseTeam1 = false;
    boolean rockHouseTeam2 = false;

    //ArrayList of shotDone to add elements at the end of the array and check if a particular rock is thrown
    ArrayList<Boolean> shotDone = new ArrayList(Arrays.asList(new Boolean[8]));
    
    private float shotX = 0;
    private float shotY = 0;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //boolean value set at true to start first shot, all other values are currently false
        Collections.fill(shotDone, Boolean.FALSE);
        shotDone.set(0, true);
        


        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);

        setScene();
        setMaterials();
        setCoordinates();
        
        Arrow arrow = new Arrow(new Vector3f(-5, 0, 0));
        
//        arrow.setArrowExtent(new Vector3f(1,0,0));
        
        arrowGeo = new Geometry("Arrow", arrow);
        arrowGeo.setMaterial(blue);
        arrowGeo.setLocalTranslation(originRockPos.add(2, 2, 2));
        arrowGeo.setName("arrowGeo");
        
        

        
        //creation of command mapping
        inputManager.addMapping("throw", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("stop", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("get", new KeyTrigger(KeyInput.KEY_F));
        inputManager.setMouseCursor(null);
        inputManager.setCursorVisible(true);
           

        //creation of cylinder node for detection of collision inside house
        float cylinderRadius = centerPos.distance(extremity);
        Cylinder cylinder = new Cylinder(100, 100, cylinderRadius - 6f, 3f);
        Geometry cylin = new Geometry("Cylinder", cylinder);
        Quaternion x90 = new Quaternion();
        x90.fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0));
        MeshCollisionShape ghostShape = new MeshCollisionShape(cylin.getMesh());
        houseGhost = new GhostControl(ghostShape);
        cylin.addControl(houseGhost);
        cylin.setLocalRotation(x90);
        cylin.setLocalTranslation(centerPos);

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
//        flyCam.setMoveSpeed(50f);
        flyCam.setDragToRotate(true);
        flyCam.setEnabled(false);
        cam.lookAtDirection(new Vector3f(-1, -0.3f, 0), new Vector3f(0, 1, 0));
    }

    public void setMaterials() {
        dirtMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        dirtMat.setTexture("ColorMap", assetManager.loadTexture("Textures/dirt.jpg"));

        blue = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blue.setColor("Color", ColorRGBA.Blue);

        transMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        transMat.setColor("Color", new ColorRGBA(1, 0, 0, 0f));
        transMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
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
        originRockPos = sceneNode.getChild("Origin").getLocalTranslation();
        camView = sceneNode.getChild("camView").getLocalTranslation();
        centerPos = sceneNode.getChild("Center").getLocalTranslation();
        extremity = sceneNode.getChild("Extremity").getLocalTranslation();
        //camPos = sceneNode.getChild("camPos").getLocalTranslation();
    }

    public void getDistanceFromCenter(Vector3f centerPos) {
        for (int count = 0; count < 4; count++) {
            distanceFromCenterTeam1[count] =  rockTeam1[count].getDistanceFrom(centerPos);
            distanceFromCenterTeam2[count] = rockTeam2[count].getDistanceFrom(centerPos);
        }
    }

    //    public void checkRockHouse() {
    //        //check rocks team1
    //        if (houseGhost.getOverlappingObjects().contains(rockPhy)) {
    //            rock.setMaterial(blue);
    //            rockHouse = true;
    //        } else {
    //            rock.setMaterial(dirtMat);
    //            rockHouse = false;
    //        }
    //        //check rocks team2
    //        if (houseGhost.getOverlappingObjects().contains(rockPhy2)) {
    //            rock2.setMaterial(blue);
    //            rockHouse2 = true;
    //        } else {
    //            rock.setMaterial(dirtMat);
    //            rockHouse2 = false;
    //        }
    //    }
    
    //method for sorting both distanceFromCenter arrays to find which team will score in a particular round
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
    public void score(double[] distanceFromCenterTeam1, double[] distanceFromCenterTeam2, Rock[] rockTeam1, Rock[] rockTeam2, YLockControl[] controlTeam1, YLockControl[] controlTeam2) {
        selectionSort(distanceFromCenterTeam1, rockTeam1, controlTeam1);
        selectionSort(distanceFromCenterTeam2, rockTeam2, controlTeam2);

        //check if rock is in the house
        if (distanceFromCenterTeam1[0] <= distanceFromCenterTeam2[0]) {              //Case where team 1 scores
            if (distanceFromCenterTeam1[1] < distanceFromCenterTeam2[0]) {
                if (distanceFromCenterTeam1[2] < distanceFromCenterTeam2[0]) {
                    if (distanceFromCenterTeam1[3] < distanceFromCenterTeam2[0]) {
                        scoreboard.setTeam1RoundScore(4);
                    } else {
                        scoreboard.setTeam1RoundScore(3);
                    }
                } else {
                    scoreboard.setTeam1RoundScore(2);
                }
            } else {
                scoreboard.setTeam1RoundScore(1);
            }
        } else {                                                                    //Case where team 2 scores
            //check if rock is inside the house
            if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[1]) {
                if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[2]) {
                    if (distanceFromCenterTeam1[0] > distanceFromCenterTeam2[3]) {
                        scoreboard.setTeam2RoundScore(4);
                    } else {
                        scoreboard.setTeam2RoundScore(3);
                    }
                } else {
                    scoreboard.setTeam2RoundScore(2);
                }
            } else {
                scoreboard.setTeam2RoundScore(1);
            }
        }
    }

    public Rock createRock(int team, int index, Rock[] rockTeam, ArrayList<YLockControl> physTeam, float tpf) {
        Rock rock = new Rock(team);
        rock.setRockModel(assetManager.loadModel(rock.getModelPath()));
        rock.getRockModel().setLocalTranslation(originRockPos.add(2, 0, 2f));
        rock.getRockModel().setMaterial(dirtMat);
        rootNode.attachChild(rock.getRockModel());

        YLockControl rockPhy = new YLockControl(1f);
        rock.getRockModel().addControl(rockPhy);
        rockPhy.setRestitution(1f);
        rockPhy.setLinearDamping(0.20f);
        bulletAppState.getPhysicsSpace().add(rockPhy);

        rockTeam[index] = rock;
        physTeam.add(rockPhy);

        return rock;
    }

    private ActionListener actionListenerThrow = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("throw") && !keyPressed) {
                throwRock(physTeam);
                rootNode.getChild("arrowGeo").removeFromParent();
            }
            if (name.equals("stop") && !keyPressed){
                stopRock();
            }
            if (name.equals("throw") && keyPressed){
                setThrowValue();
                rootNode.attachChild(arrowGeo);
            }
//            if (name.equals("get") && !keyPressed){
//                setThrowValue();
//            }
//            if (name.equals("reset")) {
//                resetPos(rockTeam, originRockPos, rockPhy);
//            }
        }
    };

    public void throwRock(ArrayList<YLockControl> physTeam) {
        if (shotDone.get(physTeam.size() - 1) == false) {
            float currentX = inputManager.getCursorPosition().x;
            float currentY = inputManager.getCursorPosition().y;
            
            float velocityX;
            float velocityY;
            
            if(shotX - currentX > 300){
                velocityX = 300;
            }
            else if(shotX - currentX < -300){
                velocityX = -300;
            }
            else{
                velocityX = shotX - currentX;
            }
            
            if(shotY - currentY > 200){
                velocityY = 200;
            }
            else if(shotY - currentY < 0){
                velocityY = 0;
            }
            else{
                velocityY = shotY - currentY;
            }
            
            velocityX = velocityX/3;
            velocityY = velocityY/2;
            
            physTeam.get(physTeam.size() - 1).setLinearVelocity(new Vector3f(-velocityY, 0, -velocityX));

            shotDone.set(physTeam.size() - 1, true);
            scoreboard.setTotalShots(scoreboard.getTotalShots() + 1);
        }
    }
    
    public void setThrowValue(){
        shotX = inputManager.getCursorPosition().x;
        shotY = inputManager.getCursorPosition().y;
    }
    
    public void stopRock(){
        for(int i = 0; i < physTeam.size(); i++){
            physTeam.get(i).setLinearVelocity(Vector3f.ZERO);
        }
        shotDone.set(physTeam.size()-1, true);
    }
    
//**********shotDone

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
    
    public void updateInHouse(){
        for (int i = 0; i < 4; i++){
            if(houseGhost.getOverlappingObjects().contains(controlTeam1[i])){
                rockTeam1[i].setInHouse(true);
            }
            else{
                rockTeam1[i].setInHouse(false);
            }
            
            if(houseGhost.getOverlappingObjects().contains(controlTeam2[i])){
                rockTeam2[i].setInHouse(true);
            }
            else{
                rockTeam2[i].setInHouse(false);
            }
        }
    }

//    public void resetPos(Spatial [] rockTeam, Vector3f origin, RigidBodyControl phy) {
//        try {
//            phy.setEnabled(paused);
//            rockTeam[index].setLocalTranslation(origin);
//            phy.setAngularFactor(Vector3f.ZERO);
//            phy.setLinearVelocity(Vector3f.ZERO);
//            phy.setAngularVelocity(Vector3f.ZERO);
//            phy.setEnabled(true);
//
//        } catch (NullPointerException e) {
//            System.out.print("null");
//        }
//    }
    @Override
    public void simpleUpdate(float tpf) {
        
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
        
        Vector3f rockCamLocation = physTeam.get(physTeam.size()-1).getPhysicsLocation().add(15, 5, 0);
        cam.setLocation(rockCamLocation);

        //listener for the left and right click action
        inputManager.addListener(actionListenerThrow, "throw");
        inputManager.addListener(actionListenerThrow, "stop");
        inputManager.addListener(actionListenerThrow, "get");
        

//        //check if rocks are in the house
//        checkRockHouse();
        //get distance between rocks and center of the house
        try{
            if(scoreboard.getTotalShots() == 8 && noMouvement(physTeam)){
                getDistanceFromCenter(centerPos);
                System.out.println("getDistance");
                
                for (int i = 0, j = 0; i < physTeam.size() && j < 4; i += 2, j++){
                    controlTeam1[j] = physTeam.get(i);

                }
                
                for (int i = 1, j = 0; i < physTeam.size() && j < 4; i += 2, j++){
                    controlTeam2[j] = physTeam.get(i);

                }
                
                updateInHouse();
                

                
                
            }
        }
        catch(ClassCastException e){
            System.out.println("ClassCast");
        }
        
        //get the team that scores and the number of points they score in one particular round
        score(distanceFromCenterTeam1, distanceFromCenterTeam2, rockTeam1, rockTeam2, controlTeam1, controlTeam2);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
