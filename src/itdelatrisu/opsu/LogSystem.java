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
 * Simple log system interface copied from Slick2D
 * @author Bigpet {@literal <dravorek@gmail.com>}
 */
public interface LogSystem {
    public void debug(String message);
    public void error(String message);
    public void error(String message, Throwable e);
    public void error(Throwable e);
    public void info(String message);
    public void warn(String message);
    public void warn(String message, Throwable e);        
}
