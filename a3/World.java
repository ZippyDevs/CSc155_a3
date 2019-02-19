package a3;

import graphicslib3D.*;
import graphicslib3D.light.*;
import graphicslib3D.GLSLUtils.*;
import graphicslib3D.shape.*;

import java.nio.*;
import javax.swing.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL2GL3.GL_POINT;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;

public class World extends JFrame implements GLEventListener
{	private GLCanvas myCanvas;
	private Material thisMaterial;
	private String[] vBlinn1ShaderSource, vBlinn2ShaderSource, fBlinn2ShaderSource;
	private int rendering_program1, rendering_program2;
	private int vao[] = new int[1];
	private int vbo[] = new int[13];
	private int mv_location, proj_location, vertexLoc, n_location;
	private float aspect;
	private GLSLUtils util = new GLSLUtils();
	
	private boolean toggleAxis = false;
	Light_Moveable light   = new Light_Moveable();
	// location of world objects
	private Point3D torusLoc   = new Point3D(1.6, 0.0, -0.3);
	private Point3D pyrLoc     = new Point3D(-1.0, 0.1, 0.3);
	private Point3D cameraLoc  = new Point3D(0.0, 0.2, 6.0);
	//private Point3D lightLoc   = new Point3D(-3.8f, 2.2f, 1.1f);
	private Point3D lightLoc   = new Point3D(0.0, 1.0, .25);
	private Point3D dolphinLoc = new Point3D(-1.8f, 0.0f, 1.8f);
	private Point3D shuttleLoc = new Point3D(1.0f, 1.0f, 1.0f);
	
	private Matrix3D m_matrix    = new Matrix3D();
	private Matrix3D v_matrix    = new Matrix3D();
	private Matrix3D mv_matrix   = new Matrix3D();
	private Matrix3D proj_matrix = new Matrix3D();
	
	// light stuff
	private float [] globalAmbient       = new float[] { 0.7f, 0.7f, 0.7f, 1.0f };
	private PositionalLight staticLight  = new PositionalLight();
	private PositionalLight currentLight = new PositionalLight();
	
	// shadow stuff
	private int scSizeX, scSizeY;
	private int [] shadow_tex      = new int[1];
	private int [] shadow_buffer   = new int[1];
	private Matrix3D lightV_matrix = new Matrix3D();
	private Matrix3D lightP_matrix = new Matrix3D();
	private Matrix3D shadowMVP1    = new Matrix3D();
	private Matrix3D shadowMVP2    = new Matrix3D();
	private Matrix3D b             = new Matrix3D();

	// model stuff
	private ImportedModel pyramid = new ImportedModel("pyr.obj");
	private ImportedModel dolphin = new ImportedModel("dolphinLowPoly.obj");
	private ImportedModel shuttle = new ImportedModel("shuttle.obj");
	
	
	private Torus myTorus = new Torus(1.2f, 0.2f, 48);
	private int numPyramidVertices, numTorusVertices, numShuttleVertices, numDolphinVertices;
	private Material pearl, redPlastic, yellowPlastic, greenPlastic;
	
	Camera camera = new Camera();
	private W_Command        wKeystroke = new W_Command      (camera);
	private A_Command        aKeystroke = new A_Command      (camera);
	private S_Command        sKeystroke = new S_Command      (camera);
	private D_Command        dKeystroke = new D_Command      (camera);
	private Q_Command        qKeystroke = new Q_Command      (camera);
	private E_Command        eKeystroke = new E_Command      (camera);
	private U_Arrow_Command  uKeystroke = new U_Arrow_Command(camera);
	private D_Arrow_Command  nKeystroke = new D_Arrow_Command(camera);
	private L_Arrow_Command laKeystroke = new L_Arrow_Command(camera);
	private R_Arrow_Command raKeystroke = new R_Arrow_Command(camera);
	private Space_Command    _Keystroke = new Space_Command  (this);
	

	private O_Command       oKeystroke  = new O_Command(light);
	private K_Command       kKeystroke  = new K_Command(light);
	private J_Command       jKeystroke  = new J_Command(light);   //new light stuff
	private L_Command       lKeystroke  = new L_Command(light);
	private P_Command       pKeystroke  = new P_Command(light);
	private I_Command       iKeystroke  = new I_Command(light);
	
	private Point3D axisLocation = new Point3D(0.0f, 0.0f, 0.0f);
	
	public World()
	{	setTitle("Zachary Schuett | Assignment 3");
		setSize(800, 800);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		getContentPane().add(myCanvas);
		setVisible(true);
		FPSAnimator animator = new FPSAnimator(myCanvas, 30);
		animator.start();
		
		//---------------------KEY COMMANDS-------------------------//
		JComponent contentPane = (JComponent)this.getContentPane();
		int        mapName     = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap   imap        = contentPane.getInputMap (mapName);
		ActionMap  amap        = contentPane.getActionMap();
		
		KeyStroke wKey     = KeyStroke.getKeyStroke(    'w');
		KeyStroke aKey     = KeyStroke.getKeyStroke(    'a');
		KeyStroke sKey     = KeyStroke.getKeyStroke(    's');
		KeyStroke dKey     = KeyStroke.getKeyStroke(    'd');
		KeyStroke qKey     = KeyStroke.getKeyStroke(    'q');
		KeyStroke eKey     = KeyStroke.getKeyStroke(    'e');
		KeyStroke uKey     = KeyStroke.getKeyStroke(   "UP");
		KeyStroke nKey     = KeyStroke.getKeyStroke( "DOWN");
		KeyStroke laKey    = KeyStroke.getKeyStroke( "LEFT");
		KeyStroke raKey    = KeyStroke.getKeyStroke("RIGHT");
		KeyStroke _Key	   = KeyStroke.getKeyStroke("SPACE");
		
		KeyStroke oKey     = KeyStroke.getKeyStroke(    'o');
		KeyStroke kKey     = KeyStroke.getKeyStroke(    'k');
		KeyStroke jKey     = KeyStroke.getKeyStroke(    'j');   //new light stuff
		KeyStroke lKey     = KeyStroke.getKeyStroke(    'l');
		KeyStroke pKey     = KeyStroke.getKeyStroke(    'p');
		KeyStroke iKey     = KeyStroke.getKeyStroke(    'i');

		imap.put(wKey,  "wCom");
		imap.put(aKey,  "aCom");
		imap.put(sKey,  "sCom");
		imap.put(dKey,  "dCom");
		imap.put(qKey,  "qCom");
		imap.put(eKey,  "eCom");
		imap.put(uKey,  "uCom");
		imap.put(nKey,  "nCom");
		imap.put(laKey,"laCom");
		imap.put(raKey,"raCom");
		imap.put(_Key,  "_Com");
		
		imap.put(oKey,  "oCom");
		imap.put(kKey,  "kCom");
		imap.put(jKey,  "jCom");   //new light stuff
		imap.put(lKey,  "lCom");
		imap.put(pKey,  "pCom");
		imap.put(iKey,  "iCom");
		
		amap.put("wCom",   wKeystroke);
		amap.put("aCom",   aKeystroke);
		amap.put("sCom",   sKeystroke);
		amap.put("dCom",   dKeystroke);
		amap.put("qCom",   qKeystroke);
		amap.put("eCom",   eKeystroke);
		amap.put("uCom",   uKeystroke);
		amap.put("nCom",   nKeystroke);
		amap.put("laCom", laKeystroke);
		amap.put("raCom", raKeystroke);
		amap.put("_Com",   _Keystroke);
		
		amap.put("oCom",   oKeystroke);
		amap.put("kCom",   kKeystroke);
		amap.put("jCom",   jKeystroke);   //new light stuff
		amap.put("lCom",   lKeystroke);
		amap.put("pCom",   pKeystroke);
		amap.put("iCom",   iKeystroke);
		
		this.requestFocus();
      //------------------------------------------------------------//	
	  //------------------------MATERIALS---------------------------//
		pearl = new Material();
		float[] pearlAmb = new float[] {0.25f, 0.20725f, 0.20725f, 1.0f};
		float[] pearlDif = new float[] {1.0f,  0.829f,   0.829f,   1.0f};
		float[] pearlSpc = new float[] {0.296648f, 0.296648f, 0.296648f, 1.0f};
		float   pearlShn = 0.088f;
		pearl.setAmbient(pearlAmb);
		pearl.setDiffuse(pearlDif);
		pearl.setSpecular(pearlSpc);
		pearl.setShininess(pearlShn*128);
		
		redPlastic = new Material();
		float[] rpAmb = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
		float[] rpDif = new float[] {0.5f, 0.0f, 0.0f, 1.0f};
		float[] rpSpc = new float[] {0.7f, 0.6f, 0.6f, 1.0f };
		float   rpShn = 0.25f;
		pearl.setAmbient(rpAmb);
		pearl.setDiffuse(rpDif);
		pearl.setSpecular(rpSpc);
		pearl.setShininess(rpShn*128);
		
		greenPlastic = new Material();
		float[] grAmb = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
		float[] grDif = new float[] {0.1f, 0.35f, 0.1f, 1.0f};
		float[] grSpc = new float[] {0.45f, 0.55f, 0.45f, 1.0f};
		float   grShn = 0.25f;
		pearl.setAmbient(grAmb);
		pearl.setDiffuse(grDif);
		pearl.setSpecular(grSpc);
		pearl.setShininess(grShn*128);

		yellowPlastic = new Material();
		float[] ylAmb = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
		float[] ylDif = new float[] {0.5f, 0.5f, 0.0f, 1.0f};
		float[] ylSpc = new float[] {0.6f, 0.6f, 0.5f, 1.0f};
		float   ylShn = 0.25f;
		pearl.setAmbient(ylAmb);
		pearl.setDiffuse(ylDif);
		pearl.setSpecular(ylSpc);
		pearl.setShininess(ylShn*128);
	  //------------------------------------------------------------//
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		staticLight.setPosition(lightLoc);
		currentLight.setPosition(light.getLLoc());
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		proj_matrix = perspective(50.0f, aspect, 0.1f, 1000.0f);
		
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

		gl.glBindFramebuffer(GL_FRAMEBUFFER, shadow_buffer[0]);
		gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadow_tex[0], 0);
	
		gl.glDrawBuffer(GL_NONE);
		gl.glEnable(GL_DEPTH_TEST);

		gl.glEnable(GL_POLYGON_OFFSET_FILL);	// for reducing
		gl.glPolygonOffset(2.0f, 4.0f);			//  shadow artifacts

		passOne();
		
		gl.glDisable(GL_POLYGON_OFFSET_FILL);	// artifact reduction, continued
		
		gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shadow_tex[0]);
	
		gl.glDrawBuffer(GL_FRONT);
		
		passTwo();
	}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void passOne()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		gl.glUseProgram(rendering_program1);
		
		Point3D origin = new Point3D(0.0, 0.0, 0.0);
		Vector3D up = new Vector3D(0.0, 1.0, 0.0);
		lightV_matrix.setToIdentity();
		lightP_matrix.setToIdentity();
	
		lightV_matrix = lookAt(currentLight.getPosition(), origin, up);	// vector from light to origin
		//lightV_matrix = lookAt(light.getLLoc(), origin, up);
		lightP_matrix = perspective(50.0f, aspect, 0.1f, 1000.0f);

		// draw the torus
		
		m_matrix.setToIdentity();
		m_matrix.translate(torusLoc.getX(),torusLoc.getY(),torusLoc.getZ());
		m_matrix.rotateX(25.0);
		m_matrix.rotateZ(37.0);
		
		shadowMVP1.setToIdentity();
		shadowMVP1.concatenate(lightP_matrix);
		shadowMVP1.concatenate(lightV_matrix);
		shadowMVP1.concatenate(m_matrix);
		int shadow_location = gl.glGetUniformLocation(rendering_program1, "shadowMVP");
		gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);
		
		// set up torus vertices buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);	
	
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numTorusVertices);

		// ---- draw the pyramid
		//  build the MODEL matrix
		m_matrix.setToIdentity();
		m_matrix.translate(pyrLoc.getX(),pyrLoc.getY(),pyrLoc.getZ());
		m_matrix.rotateX(30.0);
		m_matrix.rotateY(40.0);
		m_matrix.rotateZ(75.0);

		shadowMVP1.setToIdentity();
		shadowMVP1.concatenate(lightP_matrix);
		shadowMVP1.concatenate(lightV_matrix);
		shadowMVP1.concatenate(m_matrix);

		gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);
		
		// set up vertices buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, pyramid.getNumVertices());
		
		//----------------------DRAW THE THIRD OBJECT---------------------------//
		//  build the MODEL matrix
		m_matrix.setToIdentity();
		m_matrix.translate(dolphinLoc.getX(), dolphinLoc.getY(), dolphinLoc.getZ());
		m_matrix.rotateX(30.0);
		m_matrix.rotateY(40.0);

		shadowMVP1.setToIdentity();
		shadowMVP1.concatenate(lightP_matrix);
		shadowMVP1.concatenate(lightV_matrix);
		shadowMVP1.concatenate(m_matrix);

		gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);
		
		// set up vertices buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, dolphin.getNumVertices());
		//----------------------------------------------------------------------//
		//----------------------DRAW THE FOURTH OBJECT--------------------------//
		//  build the MODEL matrix
		m_matrix.setToIdentity();
		m_matrix.translate(shuttleLoc.getX(), shuttleLoc.getY(), shuttleLoc.getZ());
		m_matrix.rotateX(30.0);
		m_matrix.rotateY(40.0);

		shadowMVP1.setToIdentity();
		shadowMVP1.concatenate(lightP_matrix);
		shadowMVP1.concatenate(lightV_matrix);
		shadowMVP1.concatenate(m_matrix);

		gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);
		
		// set up vertices buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, shuttle.getNumVertices());
		//----------------------------------------------------------------------//
		//----------------------------AXIS STUFF--------------------------------//
		//-------------------------------X AXIS---------------------------------//
		m_matrix.setToIdentity();
        v_matrix.translate(axisLocation.getX(), axisLocation.getY(), axisLocation.getZ());
        
        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightP_matrix);
        shadowMVP1.concatenate(lightV_matrix);
        shadowMVP1.concatenate(m_matrix);
        
        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_LINES, 0, 2);
        //-----------------------------------------------------------------------//
        //---------------------------Y AXIS--------------------------------------//
        m_matrix.setToIdentity();
        v_matrix.translate(axisLocation.getX(), axisLocation.getY(), axisLocation.getZ());
        
        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightP_matrix);
        shadowMVP1.concatenate(lightV_matrix);
        shadowMVP1.concatenate(m_matrix);
        
        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_LINES, 0, 2);
        //-----------------------------------------------------------------------//
        //---------------------------Z AXIS--------------------------------------//
        m_matrix.setToIdentity();
        v_matrix.translate(axisLocation.getX(), axisLocation.getY(), axisLocation.getZ());
        
        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightP_matrix);
        shadowMVP1.concatenate(lightV_matrix);
        shadowMVP1.concatenate(m_matrix);
        
        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_LINES, 0, 2);
        //------------------------------------------------------------------------//
        //--------------------------------T COORDS--------------------------------//
        m_matrix.setToIdentity();
        v_matrix.translate(axisLocation.getX(), axisLocation.getY(), axisLocation.getZ());
        
        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightP_matrix);
        shadowMVP1.concatenate(lightV_matrix);
        shadowMVP1.concatenate(m_matrix);
        
        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_LINES, 0, 2);
		//----------------------------------------------------------------------//
		//------------------draw the light-----------------------//
		m_matrix.setToIdentity();
		m_matrix.translate(light.getLLoc().getX(), light.getLLoc().getY(), light.getLLoc().getZ());
		
		shadowMVP1.setToIdentity();
		shadowMVP1.concatenate(lightP_matrix);
		shadowMVP1.concatenate(lightV_matrix);
		shadowMVP1.concatenate(m_matrix);
		
		gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	 
		gl.glDrawArrays(GL_POINT, 0, 2);
		//-------------------------------------------------------//
	}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void passTwo()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		gl.glUseProgram(rendering_program2);

		// draw the torus
		
		thisMaterial = graphicslib3D.Material.BRONZE;		
		
		mv_location = gl.glGetUniformLocation(rendering_program2, "mv_matrix");
		proj_location = gl.glGetUniformLocation(rendering_program2, "proj_matrix");
		n_location = gl.glGetUniformLocation(rendering_program2, "normalMat");
		int shadow_location = gl.glGetUniformLocation(rendering_program2,  "shadowMVP");
		
		//  build the MODEL matrix
		m_matrix.setToIdentity();
		m_matrix.translate(torusLoc.getX(),torusLoc.getY(),torusLoc.getZ());
		m_matrix.rotateX(25.0);
		m_matrix.rotateZ(37.0);


		//  build the VIEW matrix
		v_matrix.setToIdentity();
		v_matrix.rotateX(camera.getPitchAmount());
		v_matrix.rotateY(camera.getPanAmount());
		v_matrix.translate(-camera.getCLoc().getX(),-camera.getCLoc().getY(),-camera.getCLoc().getZ());
		
		installLights(rendering_program2, v_matrix);
		
		//  build the MODEL-VIEW matrix
		mv_matrix.setToIdentity();
		mv_matrix.concatenate(v_matrix);
		mv_matrix.concatenate(m_matrix);
		
		shadowMVP2.setToIdentity();
		shadowMVP2.concatenate(b);
		shadowMVP2.concatenate(lightP_matrix);
		shadowMVP2.concatenate(lightV_matrix);
		shadowMVP2.concatenate(m_matrix);
		
		//  put the MV and PROJ matrices into the corresponding uniforms
		gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
		gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
		gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
		gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);
		
		// set up torus vertices buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// set up torus normals buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);	
	
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, numTorusVertices);

		// draw the pyramid
		
		thisMaterial = graphicslib3D.Material.GOLD;		
		installLights(rendering_program2, v_matrix);
		
		//  build the MODEL matrix
		m_matrix.setToIdentity();
		m_matrix.translate(pyrLoc.getX(),pyrLoc.getY(),pyrLoc.getZ());
		m_matrix.rotateX(30.0);
		m_matrix.rotateY(40.0);
		m_matrix.rotateZ(75.0);
		
		//  build the VIEW matrix
		v_matrix.setToIdentity();
		v_matrix.rotateX(camera.getPitchAmount());
		v_matrix.rotateY(camera.getPanAmount());
		v_matrix.translate(-camera.getCLoc().getX(),-camera.getCLoc().getY(),-camera.getCLoc().getZ());
		
		installLights(rendering_program2, v_matrix);

		//  build the MODEL-VIEW matrix
		mv_matrix.setToIdentity();
		mv_matrix.concatenate(v_matrix);
		mv_matrix.concatenate(m_matrix);
		
		shadowMVP2.setToIdentity();
		shadowMVP2.concatenate(b);
		shadowMVP2.concatenate(lightP_matrix);
		shadowMVP2.concatenate(lightV_matrix);
		shadowMVP2.concatenate(m_matrix);
		gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

		//  put the MV and PROJ matrices into the corresponding uniforms
		gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
		gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
		gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
		
		// set up vertices buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// set up normals buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, pyramid.getNumVertices());
		
		//----------------------DRAW THE THIRD OBJECT---------------------------//
		thisMaterial = greenPlastic;		
		installLights(rendering_program2, v_matrix);
		
		//  build the MODEL matrix
		m_matrix.setToIdentity();
		m_matrix.translate(dolphinLoc.getX(), dolphinLoc.getY(), dolphinLoc.getZ());
		m_matrix.rotateX(30.0);
		m_matrix.rotateY(90.0);
		
		//  build the VIEW matrix
		v_matrix.setToIdentity();
		v_matrix.rotateX(camera.getPitchAmount());
		v_matrix.rotateY(camera.getPanAmount());
		v_matrix.translate(-camera.getCLoc().getX(),-camera.getCLoc().getY(),-camera.getCLoc().getZ());
		
		installLights(rendering_program2, v_matrix);

		//  build the MODEL-VIEW matrix
		mv_matrix.setToIdentity();
		mv_matrix.concatenate(v_matrix);
		mv_matrix.concatenate(m_matrix);
		
		shadowMVP2.setToIdentity();
		shadowMVP2.concatenate(b);
		shadowMVP2.concatenate(lightP_matrix);
		shadowMVP2.concatenate(lightV_matrix);
		shadowMVP2.concatenate(m_matrix);
		gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

		//  put the MV and PROJ matrices into the corresponding uniforms
		gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
		gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
		gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
		
		// set up vertices buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// set up normals buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, dolphin.getNumVertices());
		
		//----------------------------------------------------------------------//
		
		//----------------------DRAW THE FOURTH OBJECT--------------------------//
		thisMaterial = greenPlastic;		

		
		//  build the MODEL matrix
		m_matrix.setToIdentity();
		m_matrix.translate(shuttleLoc.getX(), shuttleLoc.getY(), shuttleLoc.getZ());
		m_matrix.rotateX(30.0);
		m_matrix.rotateY(40.0);
		
		//  build the VIEW matrix
		v_matrix.setToIdentity();
		v_matrix.rotateX(camera.getPitchAmount());
		v_matrix.rotateY(camera.getPanAmount());
		v_matrix.translate(-camera.getCLoc().getX(),-camera.getCLoc().getY(),-camera.getCLoc().getZ());
		
		installLights(rendering_program2, v_matrix);

		//  build the MODEL-VIEW matrix
		mv_matrix.setToIdentity();
		mv_matrix.concatenate(v_matrix);
		mv_matrix.concatenate(m_matrix);
		
		shadowMVP2.setToIdentity();
		shadowMVP2.concatenate(b);
		shadowMVP2.concatenate(lightP_matrix);
		shadowMVP2.concatenate(lightV_matrix);
		shadowMVP2.concatenate(m_matrix);
		gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

		//  put the MV and PROJ matrices into the corresponding uniforms
		gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
		gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
		gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
		
		// set up vertices buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// set up normals buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, shuttle.getNumVertices());
		
		//----------------------------------------------------------------------//
		if(toggleAxis) {			
	    //----------------------------X AXIS---------------------------------------//
			thisMaterial = redPlastic;
	        
			installLights(rendering_program2, v_matrix);
	        m_matrix.setToIdentity();
	        v_matrix.translate(axisLocation.getX(), axisLocation.getY(), axisLocation.getZ());
	        
	        mv_matrix.setToIdentity();
	        mv_matrix.concatenate(v_matrix);
	        mv_matrix.concatenate(m_matrix);
	        
	        shadowMVP2.setToIdentity();
	        shadowMVP2.concatenate(b);
	        shadowMVP2.concatenate(lightP_matrix);
	        shadowMVP2.concatenate(lightV_matrix);
	        shadowMVP2.concatenate(m_matrix);
	        
	        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
	        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(1);
	        
	        gl.glEnable(GL_CULL_FACE);
	        gl.glFrontFace(GL_CCW);
	        gl.glEnable(GL_DEPTH_TEST);
	        gl.glDepthFunc(GL_LEQUAL);
	        gl.glDrawArrays(GL_LINES, 0, 2);
	   //-------------------------------------------------------------------------//
	   //----------------------------Y AXIS---------------------------------------//  
	        thisMaterial = yellowPlastic;
	        
	        installLights(rendering_program2, v_matrix);
	        m_matrix.setToIdentity();
	        v_matrix.translate(axisLocation.getX(), axisLocation.getY(), axisLocation.getZ());
	        
	        mv_matrix.setToIdentity();
	        mv_matrix.concatenate(v_matrix);
	        mv_matrix.concatenate(m_matrix);
	        
	        shadowMVP2.setToIdentity();
	        shadowMVP2.concatenate(b);
	        shadowMVP2.concatenate(lightP_matrix);
	        shadowMVP2.concatenate(lightV_matrix);
	        shadowMVP2.concatenate(m_matrix);
	        
	        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
	        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(1);
	        
	        gl.glEnable(GL_CULL_FACE);
	        gl.glFrontFace(GL_CCW);
	        gl.glEnable(GL_DEPTH_TEST);
	        gl.glDepthFunc(GL_LEQUAL);
	        gl.glDrawArrays(GL_LINES, 0, 2);
	  //-------------------------------------------------------------------------//
	  //----------------------------Z AXIS---------------------------------------//
	        thisMaterial = greenPlastic;
	        
	        installLights(rendering_program2, v_matrix);
	        m_matrix.setToIdentity();
	        v_matrix.translate(axisLocation.getX(), axisLocation.getY(), axisLocation.getZ());
	        
	        mv_matrix.setToIdentity();
	        mv_matrix.concatenate(v_matrix);
	        mv_matrix.concatenate(m_matrix);
	        
	        shadowMVP2.setToIdentity();
	        shadowMVP2.concatenate(b);
	        shadowMVP2.concatenate(lightP_matrix);
	        shadowMVP2.concatenate(lightV_matrix);
	        shadowMVP2.concatenate(m_matrix);
	        
	        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
	        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(1);
	        
	        gl.glEnable(GL_CULL_FACE);
	        gl.glFrontFace(GL_CCW);
	        gl.glEnable(GL_DEPTH_TEST);
	        gl.glDepthFunc(GL_LEQUAL);
	        gl.glDrawArrays(GL_LINES, 0, 2);
	   //-----------------------------------------------------------------//
	   //------------------------T COORDS---------------------------------//        
	        thisMaterial = yellowPlastic;
	        
	        installLights(rendering_program2, v_matrix);
	        m_matrix.setToIdentity();
	        v_matrix.translate(axisLocation.getX(), axisLocation.getY(), axisLocation.getZ());
	        
	        mv_matrix.setToIdentity();
	        mv_matrix.concatenate(v_matrix);
	        mv_matrix.concatenate(m_matrix);
	        
	        shadowMVP2.setToIdentity();
	        shadowMVP2.concatenate(b);
	        shadowMVP2.concatenate(lightP_matrix);
	        shadowMVP2.concatenate(lightV_matrix);
	        shadowMVP2.concatenate(m_matrix);
	        
	        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
	        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(1);
	        
	        gl.glEnable(GL_CULL_FACE);
	        gl.glFrontFace(GL_CCW);
	        gl.glEnable(GL_DEPTH_TEST);
	        gl.glDepthFunc(GL_LEQUAL);
	        gl.glDrawArrays(GL_LINES, 0, 2);
	  //--------------------------------------------------------------//
	  //-------------------draw the light-----------------------------//
	        thisMaterial = yellowPlastic;
	        
	        installLights(rendering_program2, v_matrix);
	        m_matrix.setToIdentity();
	        v_matrix.translate(light.getLLoc().getX(), light.getLLoc().getY(), light.getLLoc().getZ());
	        
	        mv_matrix.setToIdentity();
	        mv_matrix.concatenate(v_matrix);
	        mv_matrix.concatenate(m_matrix);
	        
	        shadowMVP2.setToIdentity();
	        shadowMVP2.concatenate(b);
	        shadowMVP2.concatenate(lightP_matrix);
	        shadowMVP2.concatenate(lightV_matrix);
	        shadowMVP2.concatenate(m_matrix);
	        
	        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
	        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(1);
	        
	        gl.glEnable(GL_CULL_FACE);
	        gl.glFrontFace(GL_CCW);
	        gl.glEnable(GL_DEPTH_TEST);
	        gl.glDepthFunc(GL_LEQUAL);
	        gl.glDrawArrays(GL_POINT, 0, 2); 
	  //--------------------------------------------------------------//
		}
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		createShaderPrograms();
		setupVertices();
		setupShadowBuffers();
				
		b.setElementAt(0,0,0.5);b.setElementAt(0,1,0.0);b.setElementAt(0,2,0.0);b.setElementAt(0,3,0.5f);
		b.setElementAt(1,0,0.0);b.setElementAt(1,1,0.5);b.setElementAt(1,2,0.0);b.setElementAt(1,3,0.5f);
		b.setElementAt(2,0,0.0);b.setElementAt(2,1,0.0);b.setElementAt(2,2,0.5);b.setElementAt(2,3,0.5f);
		b.setElementAt(3,0,0.0);b.setElementAt(3,1,0.0);b.setElementAt(3,2,0.0);b.setElementAt(3,3,1.0f);
		
		// may reduce shadow border artifacts
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}
	
	public void setupShadowBuffers()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		scSizeX = myCanvas.getWidth();
		scSizeY = myCanvas.getHeight();
	
		gl.glGenFramebuffers(1, shadow_buffer, 0);
	
		gl.glGenTextures(1, shadow_tex, 0);
		gl.glBindTexture(GL_TEXTURE_2D, shadow_tex[0]);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32,
						scSizeX, scSizeY, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
	}

// -----------------------------
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		setupShadowBuffers();
	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
	//-----------------------POSITION ARRAYS--------------------------//
	float[] x_pos = new float[]{ -1000.0f, 0.0f, 0.0f, 1000.0f, 0.0f, 0.0f };
	float[] y_pos = new float[]{ 0.0f, -1000.0f, 0.0f, 0.0f, 1000.0f, 0.0f };
	float[] z_pos = new float[]{ 0.0f, 0.0f, -1000.0f, 0.0f, 0.0f, 1000.0f };
	float[] tCoordinates = {0.0f, 0.0f, 1.0f, 0.0f,  1.0f,  1.0f};
	float[] lCoordinates = {(float)(light.getLLoc().getX()), (float)(light.getLLoc().getY()), (float)(light.getLLoc().getZ())};
	//-----------------------------------------------------//
	
		// pyramid definition
		Vertex3D[] pyramid_vertices = pyramid.getVertices();
		numPyramidVertices = pyramid.getNumVertices();

		float[] pyramid_vertex_positions = new float[numPyramidVertices*3];
		float[] pyramid_normals = new float[numPyramidVertices*3];

		for (int i=0; i<numPyramidVertices; i++)
		{	pyramid_vertex_positions[i*3]   = (float) (pyramid_vertices[i]).getX();			
			pyramid_vertex_positions[i*3+1] = (float) (pyramid_vertices[i]).getY();
			pyramid_vertex_positions[i*3+2] = (float) (pyramid_vertices[i]).getZ();
			
			pyramid_normals[i*3]   = (float) (pyramid_vertices[i]).getNormalX();
			pyramid_normals[i*3+1] = (float) (pyramid_vertices[i]).getNormalY();
			pyramid_normals[i*3+2] = (float) (pyramid_vertices[i]).getNormalZ();
		}
		//Torus Definition
		Vertex3D[] torus_vertices = myTorus.getVertices();
		
		int[] torus_indices = myTorus.getIndices();	
		float[] torus_fvalues = new float[torus_indices.length*3];
		float[] torus_nvalues = new float[torus_indices.length*3];
		
		for (int i=0; i<torus_indices.length; i++)
		{	torus_fvalues[i*3]   = (float) (torus_vertices[torus_indices[i]]).getX();			
			torus_fvalues[i*3+1] = (float) (torus_vertices[torus_indices[i]]).getY();
			torus_fvalues[i*3+2] = (float) (torus_vertices[torus_indices[i]]).getZ();
			
			torus_nvalues[i*3]   = (float) (torus_vertices[torus_indices[i]]).getNormalX();
			torus_nvalues[i*3+1] = (float) (torus_vertices[torus_indices[i]]).getNormalY();
			torus_nvalues[i*3+2] = (float) (torus_vertices[torus_indices[i]]).getNormalZ();
		}
		
		numTorusVertices = torus_indices.length;
		
		//Dolphin Definition
		Vertex3D[] dolphin_vertices = dolphin.getVertices();
		numDolphinVertices = dolphin.getNumVertices();
		
		float[] dolphin_vertex_positions = new float[numDolphinVertices*3];
		float[] dolphin_normals = new float[numDolphinVertices*3];
		
		for(int i = 0; i<numDolphinVertices; i++) {
			dolphin_vertex_positions[i*3]   = (float) (dolphin_vertices[i]).getX();			
			dolphin_vertex_positions[i*3+1] = (float) (dolphin_vertices[i]).getY();
			dolphin_vertex_positions[i*3+2] = (float) (dolphin_vertices[i]).getZ();
			
			dolphin_normals[i*3]   = (float) (dolphin_vertices[i]).getNormalX();
			dolphin_normals[i*3+1] = (float) (dolphin_vertices[i]).getNormalY();
			dolphin_normals[i*3+2] = (float) (dolphin_vertices[i]).getNormalZ();
		}
		
		//Shuttle Definition
		Vertex3D[] shuttle_vertices = shuttle.getVertices();
		numShuttleVertices = shuttle.getNumVertices();
		
		float[] shuttle_vertex_positions = new float[numShuttleVertices*3];
		float[] shuttle_normals = new float[numShuttleVertices*3];
		
		for(int i = 0; i<numShuttleVertices; i++) {
			shuttle_vertex_positions[i*3]   = (float) (shuttle_vertices[i]).getX();			
			shuttle_vertex_positions[i*3+1] = (float) (shuttle_vertices[i]).getY();
			shuttle_vertex_positions[i*3+2] = (float) (shuttle_vertices[i]).getZ();
			
			shuttle_normals[i*3]   = (float) (shuttle_vertices[i]).getNormalX();
			shuttle_normals[i*3+1] = (float) (shuttle_vertices[i]).getNormalY();
			shuttle_normals[i*3+2] = (float) (shuttle_vertices[i]).getNormalZ();
		}

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		//  put the Torus vertices into the first buffer,
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(torus_fvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);
		
		//  load the pyramid vertices into the second buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer pyrVertBuf = Buffers.newDirectFloatBuffer(pyramid_vertex_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrVertBuf.limit()*4, pyrVertBuf, GL_STATIC_DRAW);
		
		// load the torus normal coordinates into the third buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer torusNorBuf = Buffers.newDirectFloatBuffer(torus_nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, torusNorBuf.limit()*4, torusNorBuf, GL_STATIC_DRAW);
		
		// load the pyramid normal coordinates into the fourth buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer pyrNorBuf = Buffers.newDirectFloatBuffer(pyramid_normals);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrNorBuf.limit()*4, pyrNorBuf, GL_STATIC_DRAW);
		
		// THIRD OBJECT VERTICES
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer dolphinVertBuf = Buffers.newDirectFloatBuffer(dolphin_vertex_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, dolphinVertBuf.limit()*4, dolphinVertBuf, GL_STATIC_DRAW);
		
		// FOURTH OBJECT VERTICES
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer shuttleVertBuf = Buffers.newDirectFloatBuffer(shuttle_vertex_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, shuttleVertBuf.limit()*4, shuttleVertBuf, GL_STATIC_DRAW);
		
		// THIRD OBJECT NORMALS
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer dolphinNorBuf = Buffers.newDirectFloatBuffer(dolphin_normals);
		gl.glBufferData(GL_ARRAY_BUFFER, dolphinNorBuf.limit()*4, dolphinNorBuf, GL_STATIC_DRAW);
		
		// FOURTH OBJECT NORMALS
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer shuttleNorBuf = Buffers.newDirectFloatBuffer(shuttle_normals);
		gl.glBufferData(GL_ARRAY_BUFFER, shuttleNorBuf.limit()*4, shuttleNorBuf, GL_STATIC_DRAW);
		
		//XAXIS
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		FloatBuffer xBuf = Buffers.newDirectFloatBuffer(x_pos);
		gl.glBufferData(GL_ARRAY_BUFFER, xBuf.limit()*4, xBuf, GL_STATIC_DRAW);
		
		//YAXIS
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		FloatBuffer yBuf = Buffers.newDirectFloatBuffer(y_pos);
		gl.glBufferData(GL_ARRAY_BUFFER, yBuf.limit()*4, yBuf, GL_STATIC_DRAW);
				
		//ZAXIS
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		FloatBuffer zBuf = Buffers.newDirectFloatBuffer(z_pos);
		gl.glBufferData(GL_ARRAY_BUFFER,zBuf.limit()*4, zBuf, GL_STATIC_DRAW);
		
		//TCOORDS
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		FloatBuffer tBuf = Buffers.newDirectFloatBuffer(tCoordinates);
		gl.glBufferData(GL_ARRAY_BUFFER,tBuf.limit()*4, tBuf, GL_STATIC_DRAW);
		
		//LIGHT_MOVEABLE
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		FloatBuffer lightMBuf = Buffers.newDirectFloatBuffer(lCoordinates);
		gl.glBufferData(GL_ARRAY_BUFFER,lightMBuf.limit()*4, lightMBuf, GL_STATIC_DRAW);
		
	}
	
	private void installLights(int rendering_program, Matrix3D v_matrix)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		Material currentMaterial = new Material();
		currentMaterial = thisMaterial;
		
		Point3D lightP = currentLight.getPosition();
		Point3D lightPv = lightP.mult(v_matrix);
		
		float [] currLightPos = new float[] { (float) lightPv.getX(),
			(float) lightPv.getY(),
			(float) lightPv.getZ() };

		// get the location of the global ambient light field in the shader
		int globalAmbLoc = gl.glGetUniformLocation(rendering_program, "globalAmbient");
	
		// set the current globalAmbient settings
		gl.glProgramUniform4fv(rendering_program, globalAmbLoc, 1, globalAmbient, 0);

		// get the locations of the light and material fields in the shader
		int ambLoc = gl.glGetUniformLocation(rendering_program, "light.ambient");
		int diffLoc = gl.glGetUniformLocation(rendering_program, "light.diffuse");
		int specLoc = gl.glGetUniformLocation(rendering_program, "light.specular");
		int posLoc = gl.glGetUniformLocation(rendering_program, "light.position");

		int MambLoc = gl.glGetUniformLocation(rendering_program, "material.ambient");
		int MdiffLoc = gl.glGetUniformLocation(rendering_program, "material.diffuse");
		int MspecLoc = gl.glGetUniformLocation(rendering_program, "material.specular");
		int MshiLoc = gl.glGetUniformLocation(rendering_program, "material.shininess");

		// set the uniform light and material values in the shader
		gl.glProgramUniform4fv(rendering_program, ambLoc, 1, currentLight.getAmbient(), 0);
		gl.glProgramUniform4fv(rendering_program, diffLoc, 1, currentLight.getDiffuse(), 0);
		gl.glProgramUniform4fv(rendering_program, specLoc, 1, currentLight.getSpecular(), 0);
		gl.glProgramUniform3fv(rendering_program, posLoc, 1, currLightPos, 0);
	
		gl.glProgramUniform4fv(rendering_program, MambLoc, 1, currentMaterial.getAmbient(), 0);
		gl.glProgramUniform4fv(rendering_program, MdiffLoc, 1, currentMaterial.getDiffuse(), 0);
		gl.glProgramUniform4fv(rendering_program, MspecLoc, 1, currentMaterial.getSpecular(), 0);
		gl.glProgramUniform1f(rendering_program, MshiLoc, currentMaterial.getShininess());
	}

	public static void main(String[] args) { new World(); }

	@Override
	public void dispose(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) drawable.getGL();
		gl.glDeleteVertexArrays(1, vao, 0);
	}

//-----------------
	private void createShaderPrograms()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];

		vBlinn1ShaderSource = util.readShaderSource("a3/blinnVert1.shader");
		vBlinn2ShaderSource = util.readShaderSource("a3/blinnVert2.shader");
		fBlinn2ShaderSource = util.readShaderSource("a3/blinnFrag2.shader");

		int vertexShader1 = gl.glCreateShader(GL_VERTEX_SHADER);
		int vertexShader2 = gl.glCreateShader(GL_VERTEX_SHADER);
		int fragmentShader2 = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vertexShader1, vBlinn1ShaderSource.length, vBlinn1ShaderSource, null, 0);
		gl.glShaderSource(vertexShader2, vBlinn2ShaderSource.length, vBlinn2ShaderSource, null, 0);
		gl.glShaderSource(fragmentShader2, fBlinn2ShaderSource.length, fBlinn2ShaderSource, null, 0);

		gl.glCompileShader(vertexShader1);
		gl.glCompileShader(vertexShader2);
		gl.glCompileShader(fragmentShader2);

		rendering_program1 = gl.glCreateProgram();
		rendering_program2 = gl.glCreateProgram();

		gl.glAttachShader(rendering_program1, vertexShader1);
		gl.glAttachShader(rendering_program2, vertexShader2);
		gl.glAttachShader(rendering_program2, fragmentShader2);

		gl.glLinkProgram(rendering_program1);
		gl.glLinkProgram(rendering_program2);
	}

//------------------
	private Matrix3D perspective(float fovy, float aspect, float n, float f)
	{	float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0,0,A);
		r.setElementAt(1,1,q);
		r.setElementAt(2,2,B);
		r.setElementAt(3,2,-1.0f);
		r.setElementAt(2,3,C);
		r.setElementAt(3,3,0.0f);
		return r;
	}

	private Matrix3D lookAt(Point3D eye, Point3D target, Vector3D y)
	{	Vector3D eyeV = new Vector3D(eye);
		Vector3D targetV = new Vector3D(target);
		Vector3D fwd = (targetV.minus(eyeV)).normalize();
		Vector3D side = (fwd.cross(y)).normalize();
		Vector3D up = (side.cross(fwd)).normalize();
		Matrix3D look = new Matrix3D();
		look.setElementAt(0,0, side.getX());
		look.setElementAt(1,0, up.getX());
		look.setElementAt(2,0, -fwd.getX());
		look.setElementAt(3,0, 0.0f);
		look.setElementAt(0,1, side.getY());
		look.setElementAt(1,1, up.getY());
		look.setElementAt(2,1, -fwd.getY());
		look.setElementAt(3,1, 0.0f);
		look.setElementAt(0,2, side.getZ());
		look.setElementAt(1,2, up.getZ());
		look.setElementAt(2,2, -fwd.getZ());
		look.setElementAt(3,2, 0.0f);
		look.setElementAt(0,3, side.dot(eyeV.mult(-1)));
		look.setElementAt(1,3, up.dot(eyeV.mult(-1)));
		look.setElementAt(2,3, (fwd.mult(-1)).dot(eyeV.mult(-1)));
		look.setElementAt(3,3, 1.0f);
		return(look);
	}
	
	public boolean getToggleAxis() {
		return toggleAxis;
	}
	public void toggleAxis() {
		this.toggleAxis = !toggleAxis;
	}
}