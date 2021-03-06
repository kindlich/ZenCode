/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.drawablegui.swing;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.List;

import org.openzen.drawablegui.DFont;
import org.openzen.drawablegui.DFontFamily;
import org.openzen.drawablegui.DFontMetrics;
import org.openzen.drawablegui.DIRectangle;
import org.openzen.drawablegui.DPath;
import org.openzen.drawablegui.DTransform2D;
import org.openzen.drawablegui.draw.DDrawSurface;
import org.openzen.drawablegui.draw.DDrawnRectangle;
import org.openzen.drawablegui.draw.DDrawnShape;
import org.openzen.drawablegui.draw.DDrawnText;
import org.openzen.drawablegui.draw.DSubSurface;
import org.openzen.drawablegui.style.DShadow;
import org.openzen.drawablegui.style.DStyleDefinition;
import org.openzen.drawablegui.style.DStylePath;

/**
 * @author Hoofdgebruiker
 */
public class SwingDrawSurface implements DDrawSurface {
	private final List<SwingDrawnElement> elements = new ArrayList<>();
	private final SwingGraphicsContext context;
	public int offsetX;
	public int offsetY;

	public SwingDrawSurface(SwingGraphicsContext context, int offsetX, int offsetY) {
		this.context = context;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public static void prepare(DFont font) {
		if (font.cached != null && font.cached instanceof Font)
			return;

		String baseFontName = font.family == DFontFamily.CODE ? "Consolas" : Font.DIALOG;
		int style = 0;
		if (font.bold)
			style |= Font.BOLD;
		if (font.italic)
			style |= Font.ITALIC;

		font.cached = Font.decode(baseFontName).deriveFont(style, font.size);
	}

	public static AffineTransform getTransform(DTransform2D transform) {
		return new AffineTransform(transform.xx, transform.xy, transform.yx, transform.yy, transform.dx, transform.dy);
	}

	// taken from http://www.java2s.com/Code/Java/Advanced-Graphics/GaussianBlurDemo.htm
	public static ConvolveOp getGaussianBlurFilter(int radius, boolean horizontal) {
		if (radius < 1) {
			throw new IllegalArgumentException("Radius must be >= 1");
		}

		int size = radius * 2 + 1;
		float[] data = new float[size];

		float sigma = radius / 3.0f;
		float twoSigmaSquare = 2.0f * sigma * sigma;
		float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
		float total = 0.0f;

		for (int i = -radius; i <= radius; i++) {
			float distance = i * i;
			int index = i + radius;
			data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
			total += data[index];
		}

		for (int i = 0; i < data.length; i++) {
			data[i] /= total;
		}

		Kernel kernel = null;
		if (horizontal) {
			kernel = new Kernel(size, 1, data);
		} else {
			kernel = new Kernel(1, size, data);
		}
		return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
	}

	public void setOffset(int offsetX, int offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public void paint(Graphics2D g, DIRectangle clip) {
		for (SwingDrawnElement element : elements)
			if (clip == null || clip.overlaps(element.getBounds()))
				element.paint(g, clip);
	}

	public DIRectangle calculateBounds() {
		if (elements.isEmpty())
			return DIRectangle.EMPTY;

		DIRectangle result = elements.get(0).getBounds();
		for (SwingDrawnElement element : elements)
			result = result.union(element.getBounds());
		return result;
	}

	@Override
	public SwingGraphicsContext getContext() {
		return context;
	}

	@Override
	public DStyleDefinition getStylesheet(DStylePath path) {
		return context.getStylesheets().get(context, path);
	}

	@Override
	public DFontMetrics getFontMetrics(DFont font) {
		return context.getFontMetrics(font);
	}

	@Override
	public float getScale() {
		return context.getScale();
	}

	@Override
	public float getTextScale() {
		return context.getTextScale();
	}

	@Override
	public DDrawnText drawText(int z, DFont font, int color, float x, float y, String text) {
		DFontMetrics fontMetrics = context.getFontMetrics(font);
		return addElement(new SwingDrawnText(this, z, x, y, color, font, text, fontMetrics.getAscent(), fontMetrics.getDescent(), fontMetrics.getWidth(text)));
	}

	@Override
	public DDrawnShape strokePath(int z, DPath path, DTransform2D transform, int color, float lineWidth) {
		return addElement(new SwingStrokedPath(this, z, transform, color, path, context.getPath(path), lineWidth));
	}

	@Override
	public DDrawnRectangle fillRect(int z, DIRectangle rectangle, int color) {
		return addElement(new SwingDrawnRectangle(this, z, rectangle, color));
	}

	@Override
	public DDrawnShape fillPath(int z, DPath path, DTransform2D transform, int color) {
		return addElement(new SwingFilledPath(this, z, transform, path, context.getPath(path), color));
	}

	@Override
	public DDrawnShape shadowPath(int z, DPath path, DTransform2D transform, int color, DShadow shadow) {
		if (shadow.color == 0 || shadow.radius == 0) {
			return fillPath(z, path, transform, color);
		}

		return addElement(new SwingShadowedPath(this, z, transform, path, context.getPath(path), color, shadow));
	}

	@Override
	public DSubSurface createSubSurface(int z) {
		return addElement(new SwingSubSurface(this, z));
	}

	public void remove(SwingDrawnElement element) {
		elements.remove(element);
		repaint(element.getBounds());
	}

	public void repaint(int x, int y, int width, int height) {
		context.repaint(x + offsetX, y + offsetY, width, height);
	}

	public void repaint(DIRectangle rectangle) {
		context.repaint(rectangle.offset(offsetX, offsetY));
	}

	private <T extends SwingDrawnElement> T addElement(T element) {
		int index = elements.size();
		while (index > 0 && element.z < elements.get(index - 1).z)
			index--;

		elements.add(index, element);
		repaint(element.getBounds());
		return element;
	}
}
