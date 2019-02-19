 package a3;

import graphicslib3D.Point3D;

public class Pyramid {
	Point3D pLoc = new Point3D(.20, .30, 0.0);
	
	float[] pyramid_positions =
	{	-1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  0.0f,  1.0f,  0.0f, //front
		 1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,  0.0f,  1.0f,  0.0f, //right
		 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  0.0f,  1.0f,  0.0f, //back
		-1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  0.0f,  1.0f,  0.0f, //left
		-1.0f, -1.0f, -1.0f,  1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, //LF
		 1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f  //RR
	};
	float[] tex_coords={
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
	};
	
	public Pyramid() {}
	
	public Point3D getLocation() {
		return pLoc;
	}
	public void setLocation(Point3D pLoc) {
		this.pLoc = pLoc;
	}
	public float[] getPosition() {
		return pyramid_positions;
	}
	public void setPosition(float[] pyramid_positions) {
		this.pyramid_positions = pyramid_positions;
	}
	public float[] getTex() {
		return tex_coords;
	}
	public void setTex(float[] tex_coords) {
		this.tex_coords = tex_coords;
	}
	
}
