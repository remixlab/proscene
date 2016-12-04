/**************************************************************************************
 * ProScene_JS (version 3.0.0)
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Cesar Colorado, https://github.com/orgs/remixlab/people/cacolorador
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package main.eventjs;

import remixlab.bias.core.BogusEvent;


import com.google.gwt.core.client.JavaScriptObject;

/**
 * The Class JsMouseEvent.
 * Handles native javascript mouse event,
 * process modifiers and gets coordinates X, Y, and event type.
 */

public class JsMouseEvent extends JsEvent {
  /**
   * The Constant PRESS.
   */
  static public final int PRESS = 1;

  /**
   * The Constant RELEASE.
   */
  static public final int RELEASE = 2;

  /**
   * The Constant CLICK.
   */
  static public final int CLICK = 3;

  /**
   * The Constant DRAG.
   */
  static public final int DRAG = 4;

  /**
   * The Constant MOVE.
   */
  static public final int MOVE = 5;

  /**
   * The Constant ENTER.
   */
  static public final int ENTER = 6;

  /**
   * The Constant EXIT.
   */
  static public final int EXIT = 7;

  /**
   * The Constant WHEEL.
   */
  static public final int WHEEL = 8;


  /**
   * The is mouse pressed.
   */
  public boolean isMousePressed;

  /**
   * The element x.
   */
  private int elementX;

  /**
   * The element y.
   */
  private int elementY;


  /**
   * Instantiates a new js mouse event.
   *
   * @param event          the js event
   * @param canvas         the html canvas
   * @param isMousePressed is the mouse pressed?
   */
  public JsMouseEvent(JavaScriptObject event, JavaScriptObject canvas, boolean isMousePressed, short which) {
    super(event, which);
    this.canvas = canvas;
    init(event, canvas);
    this.isMousePressed = isMousePressed;
    processAction();
    setNewModifiers();
  }


  /**
   * Inits the parameters elementX and elementY of the event.
   *
   * @param event  the js event
   * @param canvas the html canvas
   */
  public native void init(JavaScriptObject event, JavaScriptObject canvas) /*-{
      var element = canvas;
      var offsetX = 0;
      var offsetY = 0;

      // Find element offset
      if (element.offsetParent) {
          do {
              offsetX += element.offsetLeft;
              offsetY += element.offsetTop;
          } while (!!(element = element.offsetParent));
      }

      // Find Scroll offset
      element = canvas;
      do {
          offsetX -= element.scrollLeft || 0;
          offsetY -= element.scrollTop || 0;
      } while (!!(element = element.parentNode));

      // Take into account any scrolling done
      offsetX += $wnd.pageXOffset;
      offsetY += $wnd.pageYOffset;

      this.@main.eventjs.JsMouseEvent::elementX = event.pageX - offsetX;
      this.@main.eventjs.JsMouseEvent::elementY = event.pageY - offsetY;

  }-*/;


  /**
   * Gets the x.
   *
   * @return the x
   */
  public int getX() {
    return elementX;
  }

  /**
   * Gets the y.
   *
   * @return the y
   */
  public int getY() {
    return elementY;
  }


  /**
   * Gets the count of clicks.
   *
   * @return the count
   */
  public short getCount() {
    if (getType().equals("click"))
      return 1;
    else if (getType().equals("dblclick"))
      return 2;
    else if (getType().equals("DOMMouseScroll") || getType().equals("mousewheel"))
      return WheelDelta();
    else
      return 0;
  }


  /*
   * Puts the event type
   */
  @Override
  protected void processAction() {
    if (getType().equals("click"))
      setAction(JsMouseEvent.CLICK);
    else if (getType().equals("dblclick"))
      setAction(JsMouseEvent.CLICK);
    else if (getType().equals("mousedown"))
      setAction(JsMouseEvent.PRESS);
    else if (getType().equals("mousemove") && isMousePressed)
      setAction(JsMouseEvent.DRAG);
    else if (getType().equals("mousemove") && !isMousePressed)
      setAction(JsMouseEvent.MOVE);
    else if (getType().equals("mouseover"))
      setAction(JsMouseEvent.ENTER);
    else if (getType().equals("mouseout"))
      setAction(JsMouseEvent.EXIT);
    else if (getType().equals("mouseup"))
      setAction(JsMouseEvent.RELEASE);
    else if (getType().equals("DOMMouseScroll") || getType().equals("mousewheel"))
      setAction(JsMouseEvent.WHEEL);
  }


  /**
   * Sets the new modifiers.
   */
  private void setNewModifiers() {
    modifiers ^= BogusEvent.NO_MODIFIER_MASK;
    if (this.isShiftDown())
      modifiers ^= BogusEvent.SHIFT;
    if (this.isControlDown())
      modifiers ^= BogusEvent.CTRL;
    if (this.isAltDown())
      modifiers ^= BogusEvent.ALT;
    if (this.isMetaDown())
      modifiers ^= BogusEvent.META;
    if (this.isRightMouseButton())
      modifiers ^= 4;
  }


  /* (non-Javadoc)
   * @see remixlab.util.Copyable#get()
   */
  @Override
  public JsMouseEvent get() {
    //return new JsMouseEvent(this.event, this.canvas , this.isMousePressed);
    return null;
  }
}