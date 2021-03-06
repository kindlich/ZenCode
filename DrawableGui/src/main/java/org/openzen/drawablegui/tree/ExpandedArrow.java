/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.drawablegui.tree;

import org.openzen.drawablegui.DDrawable;
import org.openzen.drawablegui.DTransform2D;
import org.openzen.drawablegui.draw.DDrawTarget;

/**
 * @author Hoofdgebruiker
 */
public class ExpandedArrow implements DDrawable {
	public static final ExpandedArrow INSTANCE = new ExpandedArrow();

	private ExpandedArrow() {
	}

	@Override
	public void draw(DDrawTarget target, int z, DTransform2D transform) {
		ExpandedColoredArrow.INSTANCE.draw(target, z, transform, 0xFF000000);
	}

	@Override
	public float getNominalWidth() {
		return 24;
	}

	@Override
	public float getNominalHeight() {
		return 24;
	}
}
