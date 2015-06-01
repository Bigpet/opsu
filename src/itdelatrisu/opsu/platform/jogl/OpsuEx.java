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
package itdelatrisu.opsu.platform.jogl;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import static itdelatrisu.opsu.OpsuStartup.close;
import itdelatrisu.opsu.downloads.Updater;

/**
 *
 * @author Bigpet {@literal <dravorek@gmail.com>}
 */
public class OpsuEx {

    private static final int FPS = 60;
    public OpsuEx() {
    }

    public void start() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);

        GLWindow window = GLWindow.create(caps);
        
        // Create a animator that drives canvas' display() at the specified FPS.
        final FPSAnimator animator = new FPSAnimator(window, FPS, true);
        
        window.setSize(300, 300);
        window.setVisible(true);
        window.setTitle("opsu!");
        

        window.addGLEventListener(new ExRenderer());
        window.addWindowListener(new WindowAdapter() {
            public void windowDestroyNotify(WindowEvent arg0) {
                // Use a dedicate thread to make sure that the animator stops
                // and the updater runs before program exits. 
                new Thread() {
                    @Override
                    public void run() {
                        if (Updater.get().getStatus() == Updater.Status.UPDATE_FINAL) {
                            close();
                            Updater.get().runUpdate();
                        }
                        if (animator.isStarted())
                            animator.stop();    // stop the animator loop
                        System.exit(0);
                    }
                }.start();
            };
        });
        animator.start();
    }
    
}