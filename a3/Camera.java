package a3;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class Camera 
{
	
	private float amt      = 0.0f;
	private float pitchAmt = 0.0f;
	private float panAmt   = 0.0f;
	
	public Camera(){}
	
	
	Point3D cLoc     = new Point3D(0, 0, 15);	    //camera origin
	
	Vector3D U       = new Vector3D(1, 0, 0);
	Vector3D V       = new Vector3D(0, 1, 0);	    //camera axes
	Vector3D N       = new Vector3D(0, 0, 1);	
	
	Matrix3D U_rot   = new Matrix3D();
	Matrix3D V_rot   = new Matrix3D();              //camera rotation matrices
	Matrix3D N_rot   = new Matrix3D();
	
	Matrix3D tMatrix = new Matrix3D();              //Translation Matrix
	Matrix3D rMatrix = new Matrix3D();			    //Rotation Matrix
	
	Point3D U_mov    = new Point3D(U.normalize());
	Point3D V_mov    = new Point3D(V.normalize());  //camera movement 
	Point3D N_mov    = new Point3D(N.normalize());

	Vector3D vector  = new Vector3D(cLoc);
	
	
	public Matrix3D ComputeView() {                //handle view changes
		tMatrix.setCol(3, vector.mult(-1));
		tMatrix.setElementAt(3, 3, 1);
		
		rMatrix.setRow(0, U);
		rMatrix.setRow(1, V);
		rMatrix.setRow(2, N);
		
		rMatrix.concatenate(tMatrix);
		return rMatrix; 
	}
	
	//---------UP/DOWN------------//
	public void moveAlongV() {
		setCLoc(new Point3D(cLoc.getX(), cLoc.getY() + getAmount(), cLoc.getZ()));
		amt = 0;
		
		ComputeView();
	}
	//----------------------------//
	//-----------WASD-------------//
	public void moveAlongN() {    
		setCLoc(new Point3D(cLoc.getX(), cLoc.getY(), cLoc.getZ() + getAmount()));
		amt = 0;
		
		ComputeView();
	}
	public void moveAlongU() {
		setCLoc(new Point3D(cLoc.getX() + getAmount(), cLoc.getY(), cLoc.getZ()));
		amt = 0;
		
		ComputeView();
	}
	//----------------------------//
	//--------PITCH/PAN-----------//
	public void pitchCamera() {
		U_rot.rotate(pitchAmt, U);
		V = V.mult(U_rot);                  //pitch camera | U
		N = N.mult(U_rot);

		ComputeView();
	}

	public void panCamera() {
		V_rot.rotate(panAmt, V);
		U = U.mult(V_rot);                  //pan camera   | V
		N = N.mult(V_rot);
	
		ComputeView();
	}
	//----------------------------//

	public Point3D getCLoc() {
		return cLoc;
	}
	public void setCLoc(Point3D cLoc) {
		this.cLoc = cLoc;
	}
	
	public float getAmount() {
		return amt;
	}
	public void setAmount(float amt) {
		this.amt = amt;
	}
	public float getPitchAmount() {
		return pitchAmt;
	}
	public void setPitchAmount(float pitchAmt) {
		this.pitchAmt = pitchAmt;
	}
	public float getPanAmount() {
		return panAmt;
	}
	public void setPanAmount(float panAmt) {
		this.panAmt = panAmt;
	}
}
