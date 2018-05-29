/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.drawablegui.border;

import org.openzen.drawablegui.DRectangle;
import org.openzen.drawablegui.DCanvas;

/**
 *
 * @author Hoofdgebruiker
 */
public class DEmptyBorder implements DBorder {
	public static final DEmptyBorder INSTANCE = new DEmptyBorder();

	@Override
	public void paint(DCanvas canvas, DRectangle bounds) {
		
	}

	@Override
	public int getPaddingLeft() {
		return 0;
	}

	@Override
	public int getPaddingRight() {
		return 0;
	}

	@Override
	public int getPaddingTop() {
		return 0;
	}

	@Override
	public int getPaddingBottom() {
		return 0;
	}
}