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
package itdelatrisu.opsu.log;

/**
 * Basic global log system. The Interface is copied from Slick2D which was originally used
 * @author Bigpet {@literal <dravorek@gmail.com>}
 */
public final class Log {
    private static boolean verbose = false;
    private static LogSystem currentLogSystem = new DefaultLogSystem();
    /**
     * Enables or disables verbose logging. When verbose logging is enabled
     * "Debug" and "Info" messages are logged in addition to the default "Error"
     * and "Warning" levels.
     * @param verbose true to enable verbose logging, false to disable
     */
    public static void setVerbose(boolean verbose)
    {
        Log.verbose = verbose;
    }
    
    public static void setLogSystem(LogSystem newSystem)
    {
        currentLogSystem = newSystem;
    }
    
    public static void error(String message)
    {
        currentLogSystem.error(message);
    }

    public static void error(String message, Throwable e)
    {
        currentLogSystem.error(message, e);        
    }
    public static void error(Throwable e)
    {
        currentLogSystem.error(e);        
    }

    public static void warn(String message)
    {
        currentLogSystem.warn(message);
    }

    public static void warn(String message, Throwable e)
    {
        currentLogSystem.warn(message, e);
    }
    
    public static void info(String message)
    {
        if(Log.verbose)
        {
            currentLogSystem.info(message);
        }
    }
    
    public static void debug(String message)
    {
        if(Log.verbose)
        {
            currentLogSystem.debug(message);
        }        
    }
}
