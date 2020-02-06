/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.scene.SceneGraphVisitor;
import java.util.Queue;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Node;



/**
 *
 * @author emile
 */
public class Rock{
    
    private double speedX;
    private double speedZ;
    private float positionX;
    private float positionZ;
    private double accelerationX;
    private double accelerationZ;
    
    private int team;
    
    private String modelPath;
    
    private RigidBodyControl rigidBodyControl;
    
    private CollisionShape rockCollisionShape;
    
    private Spatial rockModel;
    
    public Rock(int team){
        this.team = team;
        if(team == 1){
            modelPath = "Models/Rock_Model.j3o";
        }
        else
            modelPath = "Models/Rock_Model.j3o";
        rigidBodyControl = new RigidBodyControl(1f);
        
    }
    
    
    
    
    
    

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public double getSpeedZ() {
        return speedZ;
    }

    public void setSpeedZ(double speedZ) {
        this.speedZ = speedZ;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionZ() {
        return positionZ;
    }

    public void setPositionZ(float positionZ) {
        this.positionZ = positionZ;
    }

    public double getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(double accelerationX) {
        this.accelerationX = accelerationX;
    }

    public double getAccelerationZ() {
        return accelerationZ;
    }

    public void setAccelerationZ(double accelerationZ) {
        this.accelerationZ = accelerationZ;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String model) {
        modelPath = model;
    }
    
    public Spatial getRockModel(){
        return rockModel;
    }
    
    public void setRockModel(Spatial rockModel){
        this.rockModel = rockModel;
    }
    
    public RigidBodyControl getRigidBodyControl(){
        return rigidBodyControl;
    }
    
    public void setRigidBodyControl(RigidBodyControl r){
        rigidBodyControl = r;
    }
    
    public CollisionShape getCollisionShape(){
        return rockCollisionShape;
    }
    
    public void setCollisionShape(CollisionShape c){
        rockCollisionShape = c;
    }


    
    
}