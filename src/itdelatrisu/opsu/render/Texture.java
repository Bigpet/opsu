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

import org.lwjgl.opengl.GL11;

/**
 *
 * Wrapper around GPU textures. For now a wrapper around OpenGL textures
 */
public class Texture {
    private int textureID;
        
    public Texture(){
        textureID = GL11.glGenTextures();
    }
    
    public void bind(int target)
    {
        GL11.glBindTexture(target, textureID);
    }
    
    //use judiciously, try to avoid if possible and consider adding a method to
    //this class if you find yourself calling this repeatedly
    public int getID()
    {
        return textureID;
    }
}
