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
public class Rock extends Spatial{
    
//    private float speedX;
//    private float speedZ;
//    private float positionX;
//    private float positionZ;
    private float accelerationX;
    private float accelerationZ;
    
    private int team;
    
    private String modelPath;
    
//    private YLockControl yLockControl;
    
    private CollisionShape rockCollisionShape;
    
    private Spatial rockModel;
    
    public Rock(int team){
        super();
        this.team = team;
//        this.yLockControl = new YLockControl(1);
        if(team == 1){
            modelPath = "Models/RockCylinder.glb";
            
        }
        else
            modelPath = "Models/RockCylinder.glb";
//        yLockControl = new YLockControl(1f);
        
        
    }
    
//    public void updateFields(){
//        speedX = yLockControl.getLinearVelocity().x;
//        speedZ = yLockControl.getLinearVelocity().z;
//        positionX = yLockControl.getPhysicsLocation().x;
//        positionZ = yLockControl.getPhysicsLocation().z;
//    }
    
    public void accelerationEffect(){
        
    }
    
    
    
    
    
    
//
//    public double getSpeedX() {
//        return speedX;
//    }
//
//    public void setSpeedX(float speedX) {
//        this.speedX = speedX;
//    }
//
//    public double getSpeedZ() {
//        return speedZ;
//    }
//
//    public void setSpeedZ(float speedZ) {
//        this.speedZ = speedZ;
//    }
//
//    public float getPositionX() {
//        return positionX;
//    }
//
//    public void setPositionX(float positionX) {
//        this.positionX = positionX;
//    }
//
//    public float getPositionZ() {
//        return positionZ;
//    }
//
//    public void setPositionZ(float positionZ) {
//        this.positionZ = positionZ;
//    }

    public double getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(float accelerationX) {
        this.accelerationX = accelerationX;
    }

    public double getAccelerationZ() {
        return accelerationZ;
    }

    public void setAccelerationZ(float accelerationZ) {
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
    
//    public YLockControl getRigidBodyControl(){
//        return yLockControl;
//    }
//    
//    public void setRigidBodyControl(YLockControl r){
//        yLockControl = r;
//    }
    
    public CollisionShape getCollisionShape(){
        return rockCollisionShape;
    }
    
    public void setCollisionShape(CollisionShape c){
        rockCollisionShape = c;
    }

    @Override
    public void updateModelBound() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setModelBound(BoundingVolume modelBound) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getVertexCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getTriangleCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void depthFirstTraversal(SceneGraphVisitor visitor, DFSMode mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void breadthFirstTraversal(SceneGraphVisitor visitor, Queue<Spatial> queue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int collideWith(Collidable other, CollisionResults results) throws UnsupportedCollisionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
    
}