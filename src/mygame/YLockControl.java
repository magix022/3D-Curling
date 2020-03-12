/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
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

/**
 *
 * @author emile
 */
public class YLockControl extends RigidBodyControl implements PhysicsTickListener {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    
    public YLockControl(float mass){
        super(mass);
    }
    
    @Override
    public void prePhysicsTick(PhysicsSpace space, float f){
        Vector3f linearVelocityVector = getLinearVelocity();

        Vector3f angularVelocityVector = getAngularVelocity();

        linearVelocityVector.y = 0;

        angularVelocityVector.x = 0;

        angularVelocityVector.z = 0;

        setLinearVelocity(linearVelocityVector);

        setAngularVelocity(angularVelocityVector);
    }
    
    @Override
    public void physicsTick(PhysicsSpace space, float f){
        System.out.println("tick");
    }

//    @Override
//    protected void controlUpdate(float tpf) {
//        //TODO: add code that controls Spatial,
//        //e.g. spatial.rotate(tpf,tpf,tpf);
//    }
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
