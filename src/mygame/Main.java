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
 * @author normenhansen
 */
public class Main extends SimpleApplication {

	private BulletAppState bulletAppState;
	private Vector3f camPos;
	private Vector3f camView;
	private Vector3f originRockPos;
	private Spatial cylinder1;
	private Spatial cylinder2;
	private YLockControl cylinderPhy1;
	private YLockControl cylinderPhy2;

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

        Node sceneNode = (Node)floorScene;
        originRockPos = sceneNode.getChild("Origin").getLocalTranslation();
        camView = sceneNode.getChild("camView").getLocalTranslation();
//        camPos = sceneNode.getChild("camPos").getLocalTranslation();
        
        Spatial rigidFloor = sceneNode.getChild("RigidFloor");
        

        cylinder1 = assetManager.loadModel("Models/RockCylinder.glb");
        cylinder2 = assetManager.loadModel("Models/RockCylinder.glb");
        
        cylinder1.setLocalScale(3);
        cylinder2.setLocalScale(3);

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
        
        
        

        
        

        cylinder1.setMaterial(mat);
        cylinder2.setMaterial(mat);


        rootNode.attachChild(floorScene);
        rootNode.attachChild(cylinder1);
        rootNode.attachChild(cylinder2);

        cylinder1.setLocalTranslation(originRockPos.add(0, 3, 0));
        cylinder2.setLocalTranslation(originRockPos.add(-100.0f, 3, 0));
        
        

        cylinderPhy1 = new YLockControl(1f);
        cylinderPhy2 = new YLockControl(1f);
        
        
        

        cylinder1.addControl(cylinderPhy1);
        cylinder2.addControl(cylinderPhy2);
        
//        cylinderPhy1.setGravity(new Vector3f(0, -10f, 0));


        bulletAppState.getPhysicsSpace().add(cylinderPhy1);
        bulletAppState.getPhysicsSpace().add(cylinderPhy2);
        bulletAppState.getPhysicsSpace().add(sceneGeo);
        bulletAppState.getPhysicsSpace().add(floorControl);
        
//        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, 0));

        flyCam.setMoveSpeed(50f);
//        cam.setLocation(camPos);
        cam.lookAtDirection(camView, new Vector3f(0, 1, 0));
    }

    private ActionListener actionListenerThrow = new ActionListener(){
    	public void onAction(String name, boolean keyPressed, float tpf){
    		if(name.equals("throw") && !keyPressed){
    			throwRock();
    		}
    		if(name.equals("reset")){
    			resetPos(cylinder1, originRockPos, cylinderPhy1);
    			resetPos(cylinder2, originRockPos.add(-100.0f, 0, 0), cylinderPhy2);
    		}
    	}
    };

    public void throwRock(){
    	cylinderPhy1.setLinearVelocity(new Vector3f(-1, 0, 0).mult(50f));
    }

    public void resetPos(Spatial s, Vector3f origin, RigidBodyControl phy){
    	phy.setEnabled(paused);
    	s.setLocalTranslation(origin);
    	phy.setLinearVelocity(Vector3f.ZERO);
        phy.setAngularVelocity(Vector3f.ZERO);
    	phy.setEnabled(true);
    }

    @Override
    public void simpleUpdate(float tpf) {

    	inputManager.addListener(actionListenerThrow, "throw");
    	inputManager.addListener(actionListenerThrow, "reset");
        
        cylinderPhy1.prePhysicsTick(bulletAppState.getPhysicsSpace(), tpf);
        cylinderPhy2.prePhysicsTick(bulletAppState.getPhysicsSpace(), tpf);
        
        


//    	Vector3f linearVelocityVector1 = cylinderPhy1.getLinearVelocity();
//    	Vector3f linearVelocityVector2 = cylinderPhy2.getLinearVelocity();
//
//    	Vector3f angularVelocityVector1 = cylinderPhy1.getAngularVelocity();
//    	Vector3f angularVelocityVector2 = cylinderPhy2.getAngularVelocity();
////
//    	linearVelocityVector1.y = 0;
//    	linearVelocityVector2.y = 0;

//        cylinderPhy1.getPhysicsLocation().multLocal(1, 0, 1);
        
        

//    	angularVelocityVector1.x = 0;
//        angularVelocityVector1.y = 0;
//    	angularVelocityVector1.z = 0;
//    	angularVelocityVector2.x = 0;
//        angularVelocityVector2.y = 0;
//    	angularVelocityVector2.z = 0;
//
//    	cylinderPhy1.setLinearVelocity(linearVelocityVector1);
//    	cylinderPhy2.setLinearVelocity(linearVelocityVector2);

//    	cylinderPhy1.setAngularVelocity(Vector3f.ZERO);
//    	cylinderPhy2.setAngularVelocity(Vector3f.ZERO);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
