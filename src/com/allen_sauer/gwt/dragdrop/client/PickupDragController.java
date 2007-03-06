/*
 * Copyright 2007 Fred Sauer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.allen_sauer.gwt.dragdrop.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.allen_sauer.gwt.dragdrop.client.util.Location;

/**
 * DragController used for drag-and-drop operations where a draggable widget
 * or drag proxy is temporarily picked up and dragged around the boundary panel.
 */
public class PickupDragController extends AbstractDragController {

  protected static final String STYLE_PROXY = "dragdrop-proxy";

  private Widget currentDraggable;
  private Widget draggableProxy;
  private boolean dragProxyEnabled = false;
  private Location initialDraggableBoundaryPanelLocation;
  private Widget initialDraggableParent;
  private Location initialDraggableParentLocation;

  /**
   * Create a new pickup-and-move style drag controller. Allows widgets or a suitable proxy
   * to be temporarily picked up and moved around the specified boundary panel.
   * 
   * @param boundaryPanel the desired boundary panel or null if entire page is to be included
   */
  public PickupDragController(AbsolutePanel boundaryPanel) {
    super(boundaryPanel);
  }

  public void dragEnd(Widget draggable, Widget dropTarget) {
    super.dragEnd(draggable, dropTarget);
    currentDraggable = null;
    if (draggableProxy != null) {
      draggableProxy.removeFromParent();
      draggableProxy = null;
    } else {
      if (dropTarget == null) {
        // move draggable to original location
        if (initialDraggableParent instanceof AbsolutePanel) {
          //      Location parentLocation = new Location(initialDraggableParent, boundaryPanel);
          ((AbsolutePanel) initialDraggableParent).add(draggable, initialDraggableParentLocation.getLeft(),
              initialDraggableParentLocation.getTop());
        } else {
          // TODO instead try to add to original parent panel in a different way
          getBoundaryPanel().add(draggable, initialDraggableBoundaryPanelLocation.getLeft(),
              initialDraggableBoundaryPanelLocation.getTop());
        }
      }
    }
  }

  public void dragStart(Widget draggable) {
    super.dragStart(draggable);
    currentDraggable = draggable;
    draggableProxy = maybeNewDraggableProxy(draggable);
    // Store initial draggable parent and coordinates in case we have to abort
    initialDraggableParent = draggable.getParent();
    initialDraggableParentLocation = new Location(draggable, initialDraggableParent);
    initialDraggableBoundaryPanelLocation = new Location(draggable, getBoundaryPanel());
    getBoundaryPanel().add(getMovableWidget(), initialDraggableBoundaryPanelLocation.getLeft(),
        initialDraggableBoundaryPanelLocation.getTop());
  }

  public Widget getMovableWidget() {
    return draggableProxy != null ? draggableProxy : currentDraggable;
  }

  public boolean isDragProxyEnabled() {
    return dragProxyEnabled;
  }

  public void setDragProxyEnabled(boolean dragProxyEnabled) {
    this.dragProxyEnabled = dragProxyEnabled;
  }

  protected Widget maybeNewDraggableProxy(Widget draggable) {
    if (isDragProxyEnabled()) {
      HTML proxy;
      proxy = new HTML("this is a Drag Proxy");
      proxy.addStyleName(STYLE_PROXY);
      proxy.setPixelSize(currentDraggable.getOffsetWidth(), currentDraggable.getOffsetHeight());
      return proxy;
    } else {
      return null;
    }
  }
}
