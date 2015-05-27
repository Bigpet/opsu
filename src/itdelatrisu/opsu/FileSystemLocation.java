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
package itdelatrisu.opsu;

import itdelatrisu.opsu.log.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;import java.util.logging.Level;
import java.util.logging.Logger;
;

/**
 *
 * @author Bigpet {@literal <dravorek@gmail.com>}
 */
public class FileSystemLocation implements ResourceLocation{

    File root;
    
    public FileSystemLocation(File root)
    {
        this.root = root;
    }
    
    @Override
    public URL getResource(String res) {
        try {
            //Path.resolve doesn't look right for this
            File concat = new File(root.getPath()+res);
            if(concat.exists())
            {
                return concat.toURI().toURL();
            }
        } catch (MalformedURLException ex) {
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String res) {
        FileInputStream stream = null;
        try {
            File toOpen = root.toPath().resolve(res).toFile();
            stream = new FileInputStream(toOpen);
            return stream;
        } catch (FileNotFoundException ex) {
        }
        return null;
    }
    
}
