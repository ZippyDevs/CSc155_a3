package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
public class U_Arrow_Command extends AbstractAction {
	private Camera camera;
	public U_Arrow_Command(Camera camera) {
		super();
		this.camera = camera;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.setPitchAmount(camera.getPitchAmount() + .25f);
		camera.pitchCamera();
		System.out.println("|UARROWCOMMAND|");

	}

}
