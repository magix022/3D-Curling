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
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import java.awt.Color;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;
    private Vector3f camPos;
    private Vector3f camView;
    private Vector3f originRockPos;
    private Vector3f centerPos;
    private Spatial cylinder1;
    private Spatial cylinder2;
    private YLockControl cylinderPhy1;
    private YLockControl cylinderPhy2;
    private Spatial rock;
    private YLockControl rockPhy;
    private Spatial rock2;
    private YLockControl rockPhy2;

    private int countNbShots;
    private Vector3f[] vectorRockTeam1 = new Vector3f[4];
    private Vector3f[] vectorRockTeam2 = new Vector3f[4];
    private Vector3f[] vectorFromCenterTeam1 = new Vector3f[4];
    private Vector3f[] vectorFromCenterTeam2 = new Vector3f[4];
    private double[] distanceFromCenterTeam1 = new double[4];
    private double[] distanceFromCenterTeam2 = new double[4];
    
    //this should be here
    //private Rock[] rockTeam1 = new Rock[4];
    //private Rock[] rockTeam2 = new Rock[4];

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        Spatial floorScene = assetManager.loadModel("Scenes/ARENA.j3o");
        RigidBodyControl sceneGeo = new RigidBodyControl(0f);
        floorScene.setLocalTranslation(Vector3f.ZERO);
        floorScene.addControl(sceneGeo);
        sceneGeo.setPhysicsLocation(floorScene.getLocalTranslation());

        inputManager.addMapping("throw", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("reset", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        Node sceneNode = (Node) floorScene;
        originRockPos = sceneNode.getChild("Origin").getLocalTranslation();
        camView = sceneNode.getChild("camView").getLocalTranslation();
        centerPos = sceneNode.getChild("Center").getLocalTranslation();
        //        camPos = sceneNode.getChild("camPos").getLocalTranslation();

        rock = new Rock(1);
        rock = assetManager.loadModel(((Rock) rock).getModelPath());
        rootNode.attachChild(rock);
        rock.setLocalTranslation(originRockPos.add(0, 3, 0));
//            rock.setLocalScale(2);
        rockPhy = new YLockControl(1f);
        rock.addControl(rockPhy);

        rock2 = new Rock(1);
        rock2 = assetManager.loadModel(((Rock) rock2).getModelPath());
        rootNode.attachChild(rock2);
        rock2.setLocalTranslation(originRockPos.add(-100.0f, 3, 0));
//            rock2.setLocalScale(2);
        rockPhy2 = new YLockControl(1f);
        rock2.addControl(rockPhy2);

        Spatial rigidFloor = sceneNode.getChild("RigidFloor");

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/dirt.jpg"));

        Material transMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        transMat.setColor("Color", new ColorRGBA(1, 0, 0, 0f));
        transMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        rigidFloor.setQueueBucket(Bucket.Translucent);

        rigidFloor.setMaterial(transMat);

        RigidBodyControl floorControl = new RigidBodyControl(0);
        floorControl.setSpatial(rigidFloor);
        rigidFloor.addControl(floorControl);

        rock.setMaterial(mat);
        rock2.setMaterial(mat);

        rootNode.attachChild(floorScene);

        bulletAppState.getPhysicsSpace().add(sceneGeo);
        bulletAppState.getPhysicsSpace().add(floorControl);
        bulletAppState.getPhysicsSpace().add(rockPhy);
        bulletAppState.getPhysicsSpace().add(rockPhy2);

        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, 0));

        flyCam.setMoveSpeed(50f);
        //        cam.setLocation(camPos);
        cam.lookAtDirection(camView, new Vector3f(0, 1, 0));
    }
//make a method in rock.class that returns the distance between a point and itself
    //and call it in this method that adds all the distance to an array
    public void getDistanceFromCenter(Rock[] rockTeam1, Rock[] rockTeam2) {
        if (countNbShots == 4) {
            for (int count = 1; count <= 4; count++) {
                vectorRockTeam1[count].set(rockTeam1[count].getLocalTranslation());
                vectorRockTeam2[count].set(rockTeam2[count].getLocalTranslation());

                vectorFromCenterTeam1[count] = vectorRockTeam1[count].subtract(centerPos).setY(0);
                vectorFromCenterTeam2[count] = vectorRockTeam2[count].subtract(centerPos).setY(0);

                distanceFromCenterTeam1[count] = vectorFromCenterTeam1[count].length();
                distanceFromCenterTeam2[count] = vectorFromCenterTeam2[count].length();
            }
        }
    }

    private ActionListener actionListenerThrow = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("throw") && !keyPressed) {
                throwRock();
            }
            if (name.equals("reset")) {

                resetPos(rock, originRockPos, rockPhy);
                resetPos(rock2, originRockPos.add(-100.0f, 0, 0), rockPhy2);
            }
        }
    };

    public void throwRock() {
        rockPhy.setLinearVelocity(new Vector3f(-1, 0, 0).mult(50f));
        countNbShots += 1;
    }

    //this method is only for the command of resetting the position
    //nothing else should be in there
    
    public void resetPos(Spatial s, Vector3f origin, RigidBodyControl phy) {
        //I can only shoot once, after that it returns null. ???
        
        //This should not be in that method, otherwise you can't access it outside of the method
        //It should be a data field**
        Rock[] rockTeam1 = new Rock[4];
        Rock[] rockTeam2 = new Rock[4];
        try {
            
            //this should also be called in another method or in the simple update method
            for (int count = 1; count <= 4; count++) {

                rockTeam1[count] = (Rock)rock;

                //where to does rock2 go ??
                rockTeam2[count] = (Rock) rock2;
            }

//              this should be in the method called at the end of a round or something
//            getDistanceFromCenter(rockTeam1, rockTeam2);
            phy.setEnabled(paused);
            s.setLocalTranslation(origin);
            phy.setLinearVelocity(Vector3f.ZERO);
            phy.setAngularVelocity(Vector3f.ZERO);
            phy.setEnabled(true);

        } catch (Exception e) {
            System.out.print("null");
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //listener for the left and right click action
        inputManager.addListener(actionListenerThrow, "throw");
        inputManager.addListener(actionListenerThrow, "reset");

        //method that sets linera velocity and angular velocity in unwanted axis to 0        
        rockPhy.prePhysicsTick(bulletAppState.getPhysicsSpace(), tpf);
        rockPhy2.prePhysicsTick(bulletAppState.getPhysicsSpace(), tpf);

        //fix the position to original value in case of difference        
        rockPhy.physicsTick(bulletAppState.getPhysicsSpace(), tpf);
        rockPhy2.physicsTick(bulletAppState.getPhysicsSpace(), tpf);

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

}
