/**************************************************************************************
 * ProScene (version 2.1.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import processing.core.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

/**
 * Default basic implementation of the Model interface provided for convenience.
 * <p>
 * ModelObjects provides default precise picking to pshapes without interactive behavior, i.e., third-parties should
 * implement that behavior ({@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)}) to provide pshapes with
 * interactivity. Refer to examples.
 * <p>
 * Refer to examples.Model.ApplicationControl for a nice illustration.
 * 
 * @see remixlab.bias.core.Grabber#performInteraction(BogusEvent)
 */
public abstract class ModelObject implements Model {
	protected Scene		scene;
	protected int			id;
	protected PShape	pshape;

	public ModelObject(Scene scn, PShape ps) {
		scene = scn;
		pshape = ps;
		scene.addModel(this);
		id = ++Scene.modelCount;
	}

	public ModelObject(Scene scn) {
		scene = scn;
		scene.addModel(this);
		id = ++Scene.modelCount;
	}

	public void setShape(PShape ps) {
		pshape = ps;
	}

	@Override
	public PShape shape() {
		return pshape;
	}

	public void draw() {
		if (shape() == null)
			return;
		PGraphics pg = scene.pg();
		draw(pg);
	}

	// TODO doc: remember to mention bind(false);
	@Override
	public void draw(PGraphics pg) {
		if (shape() == null)
			return;
		pg.pushStyle();
		if (pg == scene.pickingBuffer()) {
			shape().disableStyle();
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
		pg.shape(shape());
		pg.popMatrix();
		if (pg == scene.pickingBuffer())
			shape().enableStyle();
		pg.popStyle();
	}

	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		DOF2Event event2 = null;

		if (((event instanceof KeyboardEvent)) || (event instanceof DOF1Event))
			throw new RuntimeException("Grabbing a ModelObject is not possible with a "
					+ ((event instanceof KeyboardEvent) ? "Keyboard" : "DOF1") + "Event");

		if (event instanceof DOF2Event)
			event2 = ((DOF2Event) event).get();
		else if (event instanceof DOF3Event)
			event2 = ((DOF3Event) event).dof2Event();
		else if (event instanceof DOF6Event)
			event2 = ((DOF6Event) event).dof3Event().dof2Event();
		else if (event instanceof ClickEvent)
			event2 = new DOF2Event(((ClickEvent) event).x(), ((ClickEvent) event).y());

		scene.pickingBuffer().pushStyle();
		scene.pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) event2.y() * scene.width() + (int) event2.x();
		if ((0 <= index) && (index < scene.pickingBuffer().pixels.length))
			return scene.pickingBuffer().pixels[index] == getColor();
		scene.pickingBuffer().popStyle();
		return false;
	}

	@Override
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}

	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return scene.pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}
}
