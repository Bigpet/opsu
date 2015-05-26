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

/**
 *
 * @author Bigpet {@literal <dravorek@gmail.com>}
 */
public interface OpsuClient extends DisplayDevice {
    //@TODO: remove this, and figure out a way to either send generic "Option changed"
    //events to the client or to re-evalute what the SlickClient does here
    public void setPreferNonEnglish(boolean preferNonEnglish);
    
    public void setMusicVolume(float volume);
    public void setTargetFrameRate(int limit);
    public void setVSync(boolean enable);
}
