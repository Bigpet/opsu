/*
 * opsu! - an open-source osu! client
 * Copyright (C) 2014, 2015 Jeffrey Han
 *
 * opsu! is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opsu! is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opsu!.  If not, see <http://www.gnu.org/licenses/>.
 */

package itdelatrisu.opsu.objects.curves;

import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.Options;
import itdelatrisu.opsu.OsuHitObject;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

/**
 * Representation of a curve.
 *
 * @author fluddokt (https://github.com/fluddokt)
 */
public abstract class Curve {
	/** The associated OsuHitObject. */
	protected OsuHitObject hitObject;

	/** The color of this curve. */
	protected Color color;

	/** The scaled starting x, y coordinates. */
	protected float x, y;

	/** The scaled slider x, y coordinate lists. */
	protected float[] sliderX, sliderY;
        
        /** scaling factor for drawing. */
        protected static int scale;

        static protected final int DIVIDES = 30;
        static protected boolean mipmapsGenerated;
        static protected int program;
        static protected int attribLoc;
        static protected int texCoordLoc;
        static protected int colLoc;
        static{
            mipmapsGenerated = false;
            program = 0;
            attribLoc = 0;
            texCoordLoc = 0;
            colLoc = 0;
        }

	/**
	 * Constructor.
	 * @param hitObject the associated OsuHitObject
	 * @param color the color of this curve
	 */
	protected Curve(OsuHitObject hitObject, Color color) {
		this.hitObject = hitObject;
		this.x = hitObject.getScaledX();
		this.y = hitObject.getScaledY();
		this.sliderX = hitObject.getScaledSliderX();
		this.sliderY = hitObject.getScaledSliderY();
		this.color = color;
                this.scale = 100;
	}

	/**
	 * Returns the point on the curve at a value t.
	 * @param t the t value [0, 1]
	 * @return the point [x, y]
	 */
	public abstract float[] pointAt(float t);

	/**
	 * Draws the full curve to the graphics context.
	 */
	public abstract void draw();

	/**
	 * Returns the angle of the first control point.
	 */
	public abstract float getEndAngle();

	/**
	 * Returns the angle of the last control point.
	 */
	public abstract float getStartAngle();

	/**
	 * Returns the scaled x coordinate of the control point at index i.
	 * @param i the control point index
	 */
	public float getX(int i) { return (i == 0) ? x : sliderX[i - 1]; }

	/**
	 * Returns the scaled y coordinate of the control point at index i.
	 * @param i the control point index
	 */
	public float getY(int i) { return (i == 0) ? y : sliderY[i - 1]; }

	/**
	 * Set the scaling factor.
	 * @param factor the new scaling factor for the UI representation
	 */
	public static void setScale(int factor) {
            scale = factor;
        }

        /**
	 * Linear interpolation of a and b at t.
	 */
	protected float lerp(float a, float b, float t) {
		return a * (1 - t) + b * t;
	}
        
        protected void drawNewSliderCone(float x,float y)
        {
            
        }
        
        protected usedRenderState startRender()
        {
            usedRenderState state = new usedRenderState();
            GameImage.SLIDER_GRADIENT.getImage().bind();
            state.smoothedPoly = GL11.glGetBoolean(GL11.GL_POLYGON_SMOOTH);
            state.blendEnabled = GL11.glGetBoolean(GL11.GL_BLEND);
            state.depthEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
            state.depthWriteEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
            state.texEnabled = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
            state.texUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            state.oldProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
            state.oldArrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
            //boolean activeAttrib0 = GL20.glGetVertexAttrib(0, GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED);
            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            if (!mipmapsGenerated) {
                mipmapsGenerated = true;
                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            }
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GL20.glUseProgram(0);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();

            return state;
        }
        protected void endRender(usedRenderState state)
        {
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL20.glUseProgram(state.oldProgram);
            GL13.glActiveTexture(state.texUnit);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, state.oldArrayBuffer);
            if (!state.depthWriteEnabled) {
                GL11.glDepthMask(false);
            }
            if (!state.depthEnabled) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            if (state.texEnabled) {
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            if (state.smoothedPoly) {
                GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
            }
            if (!state.blendEnabled) {
                GL11.glDisable(GL11.GL_BLEND);
            }
        }

    protected void initShaderProgram() {
        if (program == 0) {
            program = GL20.glCreateProgram();
            int vtxShdr = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
            int frgShdr = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
            GL20.glShaderSource(vtxShdr, "#version 330\n" + "\n" + "layout(location = 0) in vec4 in_position;\n" + "layout(location = 1) in vec2 in_tex_coord;\n" + "\n" + "out vec2 tex_coord;\n" + "void main()\n" + "{\n" + "    gl_Position = in_position;\n" + "    tex_coord = in_tex_coord;\n" + "}");
            GL20.glCompileShader(vtxShdr);
            int res = GL20.glGetShaderi(vtxShdr, GL20.GL_COMPILE_STATUS);
            if (res != GL11.GL_TRUE) {
                String error = GL20.glGetShaderInfoLog(vtxShdr, 1024);
                Log.error("Vertex Shader compilation failed", new Exception(error));
            }
            GL20.glShaderSource(frgShdr, "#version 330\n" + "\n" + "uniform sampler2D tex;\n" + "uniform vec2 tex_size;\n" + "uniform vec3 col_tint;\n" + "\n" + "in vec2 tex_coord;\n" + "layout(location = 0) out vec4 out_colour;\n" + "\n" + "void main()\n" + "{\n" + "    vec4 in_color = texture(tex, tex_coord);\n" + "    float blend_factor = in_color.r-in_color.b;\n" + "    vec4 new_color = blend_factor * vec4(col_tint,1.0f) + (1-blend_factor)*in_color;\n" + "    out_colour = new_color;//vec4(0.0f,1.0f,0.0f,0.5f);//do whatever you want with in_color;\n" + "}");
            GL20.glCompileShader(frgShdr);
            res = GL20.glGetShaderi(frgShdr, GL20.GL_COMPILE_STATUS);
            if (res != GL11.GL_TRUE) {
                String error = GL20.glGetShaderInfoLog(frgShdr, 1024);
                Log.error("Fragment Shader compilation failed", new Exception(error));
            }
            GL20.glAttachShader(program, vtxShdr);
            GL20.glAttachShader(program, frgShdr);
            GL20.glLinkProgram(program);
            res = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
            if (res != GL11.GL_TRUE) {
                String error = GL20.glGetProgramInfoLog(program, 1024);
                Log.error("Program linking failed", new Exception(error));
            }
            attribLoc = GL20.glGetAttribLocation(program, "in_position");
            texCoordLoc = GL20.glGetAttribLocation(program, "in_tex_coord");
        }
    }
    
    protected void fillCone(FloatBuffer buff, float x1, float y1, final int DIVIDES) {
        float divx = Options.getLatestResolutionWidth() / 2.0f;
        float divy = Options.getLatestResolutionHeight() / 2.0f;
        float offx = -1.0f;
        float offy = 1.0f;
        buff.put(1.0f);
        buff.put(0.5f);
        //GL11.glTexCoord2d(1.0, 0.5);
        buff.put(offx + x1 / divx);
        buff.put(offy - y1 / divy);
        buff.put(0f);
        buff.put(1f);
        //GL11.glVertex4f(x, y, 0.0f, 1.0f);
        for (int j = 0; j < DIVIDES; ++j) {
            double phase = j * (float) Math.PI * 2 / DIVIDES;
            buff.put(0.0f);
            buff.put(0.5f);
            //GL11.glTexCoord2d(0.0, 0.5);
            float x = (x1 + 90 * (float) Math.sin(phase)) / divx;
            buff.put(offx + x);
            buff.put(offy - (y1 + 90 * (float) Math.cos(phase)) / divy);
            buff.put(1f);
            buff.put(1f);
            //GL11.glVertex4f(x + 90 * (float) Math.sin(phase), y + 90 * (float) Math.cos(phase), 1.0f, 1.0f);
        }
        buff.put(0.0f);
        buff.put(0.5f);
        //GL11.glTexCoord2d(0.0, 0.5);
        buff.put(offx + (x1 + 90 * (float) Math.sin(0.0)) / divx);
        buff.put(offy - (y1 + 90 * (float) Math.cos(0.0)) / divy);
        buff.put(1f);
        buff.put(1f);
        //GL11.glVertex4f(x + 90 * (float) Math.sin(0.0), y + 90 * (float) Math.cos(0.0), 1.0f, 1.0f);
    }

    protected void drawCone(float x1, float y1, final int DIVIDES) {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glTexCoord2d(1.0, 0.5);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glVertex4f(x1, y1, 0.0f, 1.0f);
        GL11.glTexCoord2d(0.0, 0.5);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int j = 0; j < DIVIDES; ++j) {
            double phase = j * (float) Math.PI * 2 / DIVIDES;
            GL11.glVertex4f(x1 + 90 * (float) Math.sin(phase), y1 + 90 * (float) Math.cos(phase), 1.0f, 1.0f);
        }
        GL11.glVertex4f(x1 + 90 * (float) Math.sin(0.0), y1 + 90 * (float) Math.cos(0.0), 1.0f, 1.0f);
        GL11.glEnd();
    }
}

class usedRenderState
{
    boolean smoothedPoly;
    boolean blendEnabled;
    boolean depthEnabled;
    boolean depthWriteEnabled;
    boolean texEnabled;
    int texUnit;
    int oldProgram;
    int oldArrayBuffer;
}
