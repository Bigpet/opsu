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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author Bigpet {@literal <dravorek@gmail.com>}
 */
public class ResourceLoader {
    
    static ArrayList<ResourceLocation> locations = new ArrayList<>();
    
    public static void removeAllResourceLocations()
    {
        locations.clear();
    }
    
    public static void addResourceLocation(ResourceLocation location)
    {
        locations.add(location);
    }

    public static InputStream getResourceAsStream(String resource) {
        InputStream result = null;
        for(ResourceLocation loc : locations)
        {
            result = loc.getResourceAsStream(resource);
            if(result != null) break;
        }
        return result;
    }

    public static URL getResource(String resource) {
        URL result = null;
        for(ResourceLocation loc : locations)
        {
            result = loc.getResource(resource);
            if(result != null) break;
        }
        return result;
    }

    public static boolean resourceExists(String resource) {
        return getResource(resource)!=null;
    }
}
