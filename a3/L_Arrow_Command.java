package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
public class L_Arrow_Command extends AbstractAction{
	private Camera camera;
	public L_Arrow_Command(Camera camera) {
		super();
		this.camera = camera;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.setPanAmount(camera.getPanAmount() - .25f);
		camera.panCamera();
		System.out.println("|LARROWCOMMAND|");

	}

}
