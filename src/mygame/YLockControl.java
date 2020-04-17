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

public class YLockControl extends RigidBodyControl implements PhysicsTickListener {

    public YLockControl(float mass) {
        super(mass);
    }

    private float accelerationFactor = 4f;
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
        this.setAngularFactor(Vector3f.ZERO);
        this.setPhysicsLocation(translationVector);

        if (this.getLinearVelocity().length() < 0.5 && this.getLinearVelocity().length() > 0) {
            this.setLinearVelocity(Vector3f.ZERO);
        }
    }

    public float getAcceleration() {
        return accelerationFactor;
    }

    public void setAccelerationFactor(float a) {
        accelerationFactor = a;
    }
}
