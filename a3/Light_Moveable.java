package a3;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class Light_Moveable {
	
	private float amt = 0.0f;
	
	public Light_Moveable() {}
	
	Point3D lLoc     = new Point3D(0, 0, -2);
	
	Vector3D J       = new Vector3D(1, 0, 0);//X
	Vector3D K       = new Vector3D(0, 1, 0);//Y	    //light axes
	Vector3D L       = new Vector3D(0, 0, 1);//Z
	
	Point3D J_mov    = new Point3D(J.normalize());
	Point3D K_mov    = new Point3D(K.normalize());     //light movement
	Point3D L_mov    = new Point3D(L.normalize());
	
	Vector3D vector  = new Vector3D(lLoc);
	
	public Matrix3D ComputeLightView() {
		return null;
		
	}
	//-----LIGHT UP/DOWN-----//
	public void moveAlongK() {
		setLLoc(new Point3D(lLoc.getX(), lLoc.getY() + getAmount(), lLoc.getZ()));
		amt = 0;
		ComputeLightView();
	
	}
	//-----------------------//
	//---------WASD---------//
	public void moveAlongJ() {                //move L/R
		setLLoc(new Point3D(lLoc.getX() + getAmount(), lLoc.getY(), lLoc.getZ()));
		amt = 0;
		ComputeLightView();
	}
	public void moveAlongL() {                //move F/B
		setLLoc(new Point3D(lLoc.getX(), lLoc.getY(), lLoc.getZ() + getAmount()));
		amt = 0;
		ComputeLightView();
	}
	//----------------------//
	
	public Point3D getLLoc() {
		return lLoc;
	}
	public void setLLoc(Point3D lLoc) {
		this.lLoc = lLoc;
	}
	public float getAmount() {
		return amt;
	}
	public void setAmount(float amt) {
		this.amt = amt;
	}
	
}
