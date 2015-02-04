/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.agent;

import remixlab.bias.branch.*;
import remixlab.bias.branch.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

//public class WheeledMotionAgent<A extends Action<?>> extends Agent {
public class MotionAgent<A extends Action<MotionAction>> extends Agent {
	protected AbstractScene																														scene;
	protected MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>>	eyeBranch;
	protected MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>>	frameBranch;
	protected PickingMode																															pMode;

	public enum PickingMode {
		MOVE, CLICK
	};

	public static DOF2Action dof2Action(DOF1Action dof1Action) {
		return DOF2Action.valueOf(dof1Action.toString());
	}

	public static DOF3Action dof3Action(DOF1Action dof1Action) {
		return DOF3Action.valueOf(dof1Action.toString());
	}

	public static DOF6Action dof6Action(DOF1Action dof1Action) {
		return DOF6Action.valueOf(dof1Action.toString());
	}

	public static DOF3Action dof3Action(DOF2Action dof2Action) {
		return DOF3Action.valueOf(dof2Action.toString());
	}

	public static DOF6Action dof6Action(DOF2Action dof2Action) {
		return DOF6Action.valueOf(dof2Action.toString());
	}

	public static DOF6Action dof6Action(DOF3Action dof3Action) {
		return DOF6Action.valueOf(dof3Action.toString());
	}

	//

	public static DOF1Action dof1Action(DOF2Action dof2Action) {
		DOF1Action dof1Action = null;
		try {
			dof1Action = DOF1Action.valueOf(dof2Action.toString());
		} catch (IllegalArgumentException e) {
			System.out.println("non-existant DOF1Action");
		}
		return dof1Action;
	}

	public static DOF1Action dof3Action(DOF3Action dof3Action) {
		DOF1Action dof1Action = null;
		try {
			dof1Action = DOF1Action.valueOf(dof3Action.toString());
		} catch (IllegalArgumentException e) {
			System.out.println("non-existant DOF1Action");
		}
		return dof1Action;
	}

	public static DOF1Action dof1Action(DOF6Action dof6Action) {
		DOF1Action dof1Action = null;
		try {
			dof1Action = DOF1Action.valueOf(dof6Action.toString());
		} catch (IllegalArgumentException e) {
			System.out.println("non-existant DOF1Action");
		}
		return dof1Action;
	}

	public static DOF2Action dof2Action(DOF2Action dof3Action) {
		DOF2Action dof2Action = null;
		try {
			dof2Action = DOF2Action.valueOf(dof3Action.toString());
		} catch (IllegalArgumentException e) {
			System.out.println("non-existant DOF2Action");
		}
		return dof2Action;
	}

	public static DOF2Action dof2Action(DOF6Action dof6Action) {
		DOF2Action dof2Action = null;
		try {
			dof2Action = DOF2Action.valueOf(dof6Action.toString());
		} catch (IllegalArgumentException e) {
			System.out.println("non-existant DOF2Action");
		}
		return dof2Action;
	}

	public static DOF3Action dof3Action(DOF6Action dof6Action) {
		DOF3Action dof3Action = null;
		try {
			dof3Action = DOF3Action.valueOf(dof6Action.toString());
		} catch (IllegalArgumentException e) {
			System.out.println("non-existant DOF3Action");
		}
		return dof3Action;
	}

	protected float	wSens	= 1f;

	public MotionAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeBranch = new MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>>(
				new MotionProfile<A>(),
				new ClickProfile<ClickAction>(), this, (n + "_eye_mouse_branch"));
		frameBranch = new MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>>(
				new MotionProfile<A>(),
				new ClickProfile<ClickAction>(), this, (n + "_frame_mouse_branch"));
		setPickingMode(PickingMode.MOVE);
	}

	@Override
	public float[] sensitivities(MotionEvent event) {
		if (event instanceof DOF1Event)
			return new float[] { wSens, 1f, 1f, 1f, 1f, 1f };
		else
			return new float[] { 1f, 1f, 1f, 1f, 1f, 1f };
	}

	/**
	 * Defines the {@link #wheelSensitivity()}.
	 */
	public void setWheelSensitivity(float sensitivity) {
		wSens = sensitivity;
	}

	/**
	 * Returns the wheel sensitivity.
	 * <p>
	 * Default value is 20.0. A higher value will make the wheel action more efficient (usually meaning a faster zoom).
	 * Use a negative value to invert the zoom in and out directions.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float wheelSensitivity() {
		return wSens;
	}

	@Override
	public boolean appendBranch(Branch<?, ?> branch) {
		if (branch instanceof MotionBranch)
			return super.appendBranch(branch);
		else {
			System.out.println("Branch should be instanceof MotionBranch to be appended");
			return false;
		}
	}

	public boolean addGrabber(InteractiveFrame frame) {
		return addGrabber(frame, frameBranch);
	}

	public boolean addGrabber(InteractiveEyeFrame frame) {
		// this.resetBranch(eyeBranch);
		return addGrabber(frame, eyeBranch);
	}

	@Override
	public void resetDefaultGrabber() {
		addGrabber(scene.eye().frame());
		this.setDefaultGrabber(scene.eye().frame());
	}

	public MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>> eyeBranch() {
		return eyeBranch;
	}

	public MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>> frameBranch() {
		return frameBranch;
	}

	protected MotionProfile<A> motionProfile() {
		if (inputGrabber() instanceof InteractiveEyeFrame)
			return eyeBranch.profile();
		if (inputGrabber() instanceof InteractiveFrame)
			return frameBranch().profile();
		return null;
	}

	protected ClickProfile<ClickAction> clickProfile() {
		if (inputGrabber() instanceof InteractiveEyeFrame)
			return eyeBranch.clickProfile();
		if (inputGrabber() instanceof InteractiveFrame)
			return frameBranch.clickProfile();
		return null;
	}

	/*
	 * protected MotionProfile<DOF1Action> wheelProfile() { if (inputGrabber() instanceof InteractiveEyeFrame) return
	 * eyeBranch.wheelProfile(); if (inputGrabber() instanceof InteractiveFrame) return frameBranch().wheelProfile();
	 * return null; }
	 */

	// TODO test all protected down here in stable before going on

	/**
	 * Profile defining InteractiveEyeFrame action bindings from {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	public MotionProfile<A> eyeProfile() {
		return eyeBranch.profile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	public MotionProfile<A> frameProfile() {
		return frameBranch.profile();
	}

	/**
	 * Profile defining InteractiveEyeFrame action bindings from {@link remixlab.bias.event.shortcut.ClickShortcut}s.
	 */
	public ClickProfile<ClickAction> eyeClickProfile() {
		return eyeBranch().clickProfile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.ClickShortcut}s.
	 */
	public ClickProfile<ClickAction> frameClickProfile() {
		return frameBranch().clickProfile();
	}

	/**
	 * Profile defining InteractiveEyeFrame action bindings from (wheel)
	 * {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	/*
	 * public MotionProfile<DOF1Action> eyeWheelProfile() { return eyeBranch.wheelProfile(); }
	 */

	/**
	 * Profile defining InteractiveFrame action bindings from (wheel) {@link remixlab.bias.event.shortcut.MotionShortcut}
	 * s.
	 */
	/*
	 * public MotionProfile<DOF1Action> frameWheelProfile() { return frameBranch.wheelProfile(); }
	 */

	// common api

	public void setPickingMode(PickingMode mode) {
		pMode = mode;
	}

	public PickingMode pickingMode() {
		return pMode;
	}

	/**
	 * Same as {@code return buttonModifiersFix(BogusEvent.NOMODIFIER_MASK, button)}.
	 * 
	 * @see #buttonModifiersFix(int, int)
	 */
	public int buttonModifiersFix(int button) {
		return buttonModifiersFix(BogusEvent.NO_MODIFIER_MASK, button);
	}

	/**
	 * Hack to deal with some platforms not reporting correctly the mouse event mask, such as with Processing:
	 * https://github.com/processing/processing/issues/1693
	 * <p>
	 * Default implementation simple returns the same mask.
	 */
	public int buttonModifiersFix(int mask, int button) {
		return mask;
	}

	// click

	/**
	 * Binds the mask-button-ncs (number-of-clicks) click-shortcut to the (click) dandelion action to be performed by the
	 * given {@code target} (EYE or FRAME).
	 */
	public void setClickBinding(Target target, int mask, int button, int ncs, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.setBinding(buttonModifiersFix(mask, button), button, ncs, action);
	}

	/**
	 * Binds the button-ncs (number-of-clicks) click-shortcut to the (click) dandelion action to be performed by the given
	 * {@code target} (EYE or FRAME).
	 */
	public void setClickBinding(Target target, int button, int ncs, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.setBinding(buttonModifiersFix(button), button, ncs, action);
	}

	/**
	 * Binds the single-clicked button shortcut to the (click) dandelion action to be performed by the given
	 * {@code target} (EYE or FRAME).
	 */
	public void setClickBinding(Target target, int button, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.setBinding(buttonModifiersFix(button), button, 1, action);
	}

	/**
	 * Removes the mask-button-ncs (number-of-clicks) click-shortcut binding from the
	 * {@link remixlab.dandelion.core.InteractiveEyeFrame} (if {@code eye} is {@code true}) or from the
	 * {@link remixlab.dandelion.core.InteractiveFrame} (if {@code eye} is {@code false}).
	 */
	public void removeClickBinding(Target target, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.removeBinding(buttonModifiersFix(mask, button), button, ncs);
	}

	/**
	 * Removes the button-ncs (number-of-clicks) click-shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeClickBinding(Target target, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.removeBinding(buttonModifiersFix(button), button, ncs);
	}

	/**
	 * Removes the single-clicked button shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeClickBinding(Target target, int button) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.removeBinding(buttonModifiersFix(button), button, 1);
	}

	/**
	 * Removes all click bindings from the given {@code target} (EYE or FRAME).
	 */
	public void removeClickBindings(Target target) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.removeBindings();
	}

	/**
	 * Returns {@code true} if the mask-button-ncs (number-of-clicks) click-shortcut is bound to the given {@code target}
	 * (EYE or FRAME).
	 */
	public boolean hasClickBinding(Target target, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return profile.hasBinding(buttonModifiersFix(mask, button), button, ncs);
	}

	/**
	 * Returns {@code true} if the button-ncs (number-of-clicks) click-shortcut is bound to the given {@code target} (EYE
	 * or FRAME).
	 */
	public boolean hasClickBinding(Target target, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return profile.hasBinding(buttonModifiersFix(button), button, ncs);
	}

	/**
	 * Returns {@code true} if the single-clicked button shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasClickBinding(Target target, int button) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return profile.hasBinding(buttonModifiersFix(button), button, 1);
	}

	/**
	 * Returns {@code true} if the mouse click action is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isClickActionBound(Target target, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return profile.isActionBound(action);
	}

	/**
	 * Returns the (click) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to
	 * the given mask-button-ncs (number-of-clicks) click-shortcut. Returns {@code null} if no action is bound to the
	 * given shortcut.
	 */
	public ClickAction clickAction(Target target, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return (ClickAction) profile.action(buttonModifiersFix(mask, button), button, ncs);
	}

	/**
	 * Returns the (click) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to
	 * the given button-ncs (number-of-clicks) click-shortcut. Returns {@code null} if no action is bound to the given
	 * shortcut.
	 */
	public ClickAction clickAction(Target target, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return (ClickAction) profile.action(buttonModifiersFix(button), button, ncs);
	}

	/**
	 * Returns the (click) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to
	 * the given single-clicked button shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public ClickAction clickAction(Target target, int button) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return (ClickAction) profile.action(buttonModifiersFix(button), button, 1);
	}
}