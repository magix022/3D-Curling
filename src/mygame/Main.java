package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    private BulletAppState bulletAppState;
    private Vector3f camPos1;
    private Vector3f camView1;
    private Vector3f originRockPos;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        Spatial floorScene = assetManager.loadModel("Scenes/Scene.j3o");
        RigidBodyControl sceneGeo = new RigidBodyControl(0.0f);
        floorScene.addControl(sceneGeo);
        
        inputManager.addMapping("throw", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "throw");
        
        
        
        Node sceneNode = (Node)floorScene;
//        originRockPos = sceneNode.getChild("Origin").getLocalTranslation();
        
//        Material wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        wallMat.setTexture("ColorMap", assetManager.loadTexture("Textures/walls_text.jpg"));
//        floorScene.setMaterial(wallMat);
        
        Rock rock = new Rock(1);
        Spatial rockModel = assetManager.loadModel(rock.getModelPath());
        rock.setRockModel(rockModel);
        rock.getRockModel().addControl(rock.getRigidBodyControl());
        
//        rock.getRockModel().setLocalTranslation(originRockPos);
        
        
        
        

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        rock.getRockModel().setMaterial(mat);

        rootNode.attachChild(rock.getRockModel());
        rootNode.attachChild(floorScene);
        
        sceneGeo.setFriction(1);

        
        bulletAppState.getPhysicsSpace().add(rock.getRigidBodyControl());
        bulletAppState.getPhysicsSpace().add(sceneGeo);
        
        flyCam.setMoveSpeed(25f);
    }
    
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean keyPressed, float tpf){
            if (name.equals("throw") && !keyPressed){
                throwRock();
            }
        }
    };
    
    public void throwRock(){
//        Rock rock = new Rock(1);
        Sphere ball = new Sphere(32, 32, 0.4f, true, false);
        Geometry ballGeo = new Geometry("ball", ball);
        
//        Spatial rockModel = assetManager.loadModel(rock.getModelPath());
//        rock.setRockModel(rockModel);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/dirt.jpg"));
        ballGeo.setMaterial(mat);
        
        rootNode.attachChild(ballGeo);
        ballGeo.setLocalTranslation(cam.getLocation());
        
        RigidBodyControl ballPhy = new RigidBodyControl(2f);
        
        ballGeo.addControl(ballPhy);
        
        bulletAppState.getPhysicsSpace().add(ballPhy);
        ballPhy.setLinearVelocity(cam.getDirection().mult(25));
        
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
