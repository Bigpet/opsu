/*
 *  opsu! - an open-source osu! client
 *  Copyright (C) 2014, 2015 Jeffrey Han
 * 
 *  opsu! is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  opsu! is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with opsu!.  If not, see <http://www.gnu.org/licenses/>.
 */
package itdelatrisu.opsu.render;

import static itdelatrisu.opsu.render.DebugHelper.FBO_DEBUG_CHECKS;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

/**
 *
 * Represents a rendertarget. For now this maps to and OpenGL FBO via LWJGL
 */
public class Rendertarget {
    private boolean dummy;//NOTE: maybe dummyRT and created FBOs should be different classes implementing one interface
    private boolean complete;
    public final int width;
    public final int height;
    private int fboID;
    //@TODO: consider making render-to-texture rendertargets into seperate class
    private Texture texture;
            
    /**
     * This version constructs a wrapper around an FBO that was already created.
     * These wrappers are just for binding/unbinding and can't be modified.
     * 
     * @param existingFBOID the ID of an already created and complete FBO
     */
    public Rendertarget(int existingFBOID)
    {
        dummy = true;
        height = 0;
        width = 0;
        fboID = existingFBOID;
        complete = true;
        texture = null;
    }
    
    /**
     * Create a new FBO
     * 
     * @param width
     * @param height 
     */
    public Rendertarget(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.dummy = false;
        fboID = GL30.glGenFramebuffers();
        complete = false;
        texture = new Texture();
    }
    
    public void bind()
    {
        if(FBO_DEBUG_CHECKS)
        {
            int boundFBO = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            if(boundFBO == fboID){
                //TODO: consider different exception type
                throw new RuntimeException("FBO already bound, redundant bind");
            }
        }
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fboID);
        if(FBO_DEBUG_CHECKS)
        {
            if (complete) {
                int status = GL30.glCheckFramebufferStatus(GL30.GL_DRAW_FRAMEBUFFER);
                if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                    throw new RuntimeException("presumed complete framebuffer is not actually complete");
                }
            }
        }
    }
    
    //use judiciously, try to avoid if possible and consider adding a method to
    //this class if you find yourself calling this repeatedly
    public int getID()
    {
        return fboID;
    }
    
    //try not to use, could be moved into seperate class
    public int getTextureID()
    {
        return texture.getID();
    }
    
    //try not to use, could be moved into seperate class
    public Texture getTexture()
    {
        return texture;
    }
    
    public static void unbind()
    {
        if(FBO_DEBUG_CHECKS)
        {
            int boundFBO = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            if(boundFBO == 0){
                //TODO: consider different exception type
                throw new RuntimeException("default FBO already bound, redundant unbind");
            }
        }
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
    }
    
    public static Rendertarget createRTTFramebuffer(int wid, int hig)
    {
        Rendertarget buffer = new Rendertarget(wid,hig);
        buffer.bind();

        int fboTexture = buffer.texture.getID();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fboTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 4, wid, hig, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        int fboDepth = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, fboDepth);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, wid, hig);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, fboDepth);

        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, fboTexture, 0);
        GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);

        return buffer;
    }
    
    @Override
    public void finalize()throws Throwable{
        super.finalize();
        if(!dummy)
        GL30.glDeleteFramebuffers(fboID);
    }
}
