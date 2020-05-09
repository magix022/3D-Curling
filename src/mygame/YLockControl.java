package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.GhostControl;

public class YLockControl extends RigidBodyControl implements PhysicsTickListener, PhysicsCollisionListener {

    private AudioNode collisionSound;
    private AudioNode rockToBoard;

    private GhostControl houseGhost;

    public YLockControl(float mass, AudioNode collisionSound, AudioNode rockToBoard, GhostControl houseGhost) {
        super(mass);
        this.collisionSound = collisionSound;
        this.rockToBoard = rockToBoard;
        this.houseGhost = houseGhost;
    }

    public float yLocation;

    @Override
    public void prePhysicsTick(PhysicsSpace space, float f) {

        yLocation = this.getPhysicsLocation().getY();

        Vector3f linearVelocityVector = getLinearVelocity();
        Vector3f translationVector = getPhysicsLocation();
        Vector3f angularVelocityVector = getAngularVelocity();

        linearVelocityVector.y = 0;

        angularVelocityVector.x = 0;
        angularVelocityVector.z = 0;

        setLinearVelocity(linearVelocityVector);
        setAngularVelocity(angularVelocityVector);
    }

    @Override
    public void physicsTick(PhysicsSpace space, float f) {

//        System.out.println("Physics thick");
        Vector3f translationVector = getPhysicsLocation();
        Vector3f angularVelocityVector = getAngularVelocity();

        angularVelocityVector.x = 0;
        angularVelocityVector.z = 0;

        setPhysicsLocation(translationVector);
        setAngularVelocity(angularVelocityVector);
    }

    protected void controlUpdate(float tpf, float yLocation) {
        Vector3f translationVector = this.getPhysicsLocation();
        translationVector.y = yLocation;
        this.setPhysicsLocation(translationVector);

        if (this.getLinearVelocity().length() < 0.5 && this.getLinearVelocity().length() > 0) {
            this.setLinearVelocity(Vector3f.ZERO);
        }

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        try {
            if (event.getNodeA().getControl(0).getClass().equals(event.getNodeB().getControl(0).getClass())) {
                if (event.getNodeA().getControl(0).equals(this)) {
                    if (event.getNodeA().getLocalTranslation().x > event.getNodeB().getLocalTranslation().x) {
                        collisionSound.playInstance();
                    }
                } else {
                    if (event.getNodeB().getLocalTranslation().x > event.getNodeA().getLocalTranslation().x) {
                        collisionSound.playInstance();
                    }
                }
            } else if (event.getNodeA().getControl(0).equals(houseGhost) || event.getNodeB().getControl(0).equals(houseGhost)) {
            } else {
                rockToBoard.setLocalTranslation(((Spatial) (this.getUserObject())).getLocalTranslation());
                rockToBoard.playInstance();
            }
        } catch (NullPointerException ex) {
            System.out.println();
        }
    }

//    
//    @Override
//    protected void controlRender(RenderManager rm, ViewPort vp) {
//        //Only needed for rendering-related operations,
//        //not called when spatial is culled.
//    }
//    
//    public Control cloneForSpatial(Spatial spatial) {
//        YLockControl control = new YLockControl();
//        //TODO: copy parameters to new Control
//        return control;
//    }
//    
//    @Override
//    public void read(JmeImporter im) throws IOException {
//        super.read(im);
//        InputCapsule in = im.getCapsule(this);
//        //TODO: load properties of this Control, e.g.
//        //this.value = in.readFloat("name", defaultValue);
//    }
//    @Override
//    public void write(JmeExporter ex) throws IOException {
//        super.write(ex);
//        OutputCapsule out = ex.getCapsule(this);
//        //TODO: save properties of this Control, e.g.
//        //out.write(this.value, "name", defaultValue);
//    }
}
