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
package itdelatrisu.opsu.Ex;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import sun.security.krb5.JavaxSecurityAuthKerberosAccess;

/**
 *
 * @author Bigpet {@literal <dravorek@gmail.com>}
 */
public class ExRenderer implements GLEventListener  {
    float theta = 0.0f;
    
    @Override
    public void init(GLAutoDrawable glad) {
        
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        
    }

    private void update()
    {
        theta += 0.01;
        if(theta > 2*java.lang.Math.PI)
        {
            theta -= 2*java.lang.Math.PI;
        }
    }
    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();   // get the OpenGL graphics context

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);    // clear background
        gl.glLoadIdentity();                   // reset the model-view matrix    

          // Rendering code - draw a triangle
        float sine = (float)Math.sin(theta);
        float cosine = (float)Math.cos(theta);
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3f(1, 0, 0);
        gl.glVertex2d(-cosine, -cosine);
        gl.glColor3f(0, 1, 0);
        gl.glVertex2d(0, cosine);
        gl.glColor3f(0, 0, 1);
        gl.glVertex2d(sine, -sine);
        gl.glEnd();

        update();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        
    }
    
}
