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
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class Rock {
    private int team;
    private String modelPath;
    private CollisionShape rockCollisionShape;
    private Spatial rockModel;
    private boolean insideHouse = false;

    public Rock(int team) {
        super();
        this.team = team;
        if (team == 1) {
            modelPath = "Models/RockCylinder.glb";
        } else {
            modelPath = "Models/RockCylinder.glb";
        }
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String model) {
        modelPath = model;
    }

    public Spatial getRockModel() {
        return rockModel;
    }

    public void setRockModel(Spatial rockModel) {
        this.rockModel = rockModel;
    }

    public CollisionShape getCollisionShape() {
        return rockCollisionShape;
    }

    public void setCollisionShape(CollisionShape c) {
        rockCollisionShape = c;
    }

    public double getDistanceFrom(Vector3f vector) {
        return this.rockModel.getLocalTranslation().distance(vector);
    }
    
    public void setInHouse(boolean b){
        insideHouse = b;
    }
    
    public boolean getInHouse(){
        return insideHouse;
    }
    
    public void setTeam(int team){
        this.team = team;
    }
    
    public int getTeam(){
        return team;
    }
}
