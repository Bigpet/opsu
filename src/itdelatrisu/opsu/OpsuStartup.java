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

import itdelatrisu.opsu.Ex.OpsuEx;
import itdelatrisu.opsu.slickreplace.ResourceLoader;
import itdelatrisu.opsu.slickreplace.FileSystemLocation;
import itdelatrisu.opsu.log.Log;
import itdelatrisu.opsu.log.DefaultLogSystem;
import static itdelatrisu.opsu.Opsu.close;
import itdelatrisu.opsu.db.DBController;
import itdelatrisu.opsu.downloads.DownloadList;
import itdelatrisu.opsu.downloads.Updater;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;


/**
 * Opsu Startup class that initializes the log and configuration systems and
 * then dispatches startup according to the configuration values
 * @author Bigpet {@literal <dravorek@gmail.com>}
 */
public class OpsuStartup {
    /**
     * Server socket for restricting the program to a single instance.
     */
    private static ServerSocket SERVER_SOCKET;

    /**
     * Launches opsu!.
     */
    public static void main(String[] args) {
        // log all errors to a file
        Log.setVerbose(false);
        try {
            DefaultLogSystem.out = new PrintStream(new FileOutputStream(Options.LOG_FILE, true));
        } catch (FileNotFoundException e) {
            Log.error(e);
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                ErrorHandler.error("** Uncaught Exception! **", e, true);
            }
        });

        // parse configuration file
        Options.parseOptions();

        // only allow a single instance
        try {
            SERVER_SOCKET = new ServerSocket(Options.getPort());
        } catch (IOException e) {
            ErrorHandler.error(String.format("Another program is already running on port %d.", Options.getPort()), e, false);
            System.exit(1);
        }

        // set path for lwjgl natives - NOT NEEDED if using JarSplice
        File nativeDir = new File("./target/natives/");
        if (nativeDir.isDirectory()) {
            System.setProperty("org.lwjgl.librarypath", nativeDir.getAbsolutePath());
        }

        // set the resource paths
        ResourceLoader.addResourceLocation(new FileSystemLocation(new File("./res/")));

        // initialize databases
        try {
            DBController.init();
        } catch (UnsatisfiedLinkError e) {
            errorAndExit(e, "The databases could not be initialized.");
        }

        // check if just updated
        if (args.length >= 2) {
            Updater.get().setUpdateInfo(args[0], args[1]);
        }

        // check for updates
        new Thread() {
            @Override
            public void run() {
                try {
                    Updater.get().checkForUpdates();
                } catch (IOException e) {
                    Log.warn("Check for updates failed.", e);
                }
            }
        }.start();

        // start the game
        try {
            // loop until force exit
            if (Options.isExperimentalGUI()) {
                System.setProperty("newt.window.icons", "icon16.png icon32.png");
                OpsuEx opsu = new OpsuEx();
                opsu.start();
            } else {
                while (true) {
                    Opsu opsu = new Opsu("opsu!");
                    Container app = new Container(opsu);

                    // basic game settings
                    Options.setDisplayMode(app);
                    String[] icons = {"icon16.png", "icon32.png"};
                    app.setIcons(icons);
                    app.setForceExit(true);

                    app.start();

                    // run update if available
                    if (Updater.get().getStatus() == Updater.Status.UPDATE_FINAL) {
                        close();
                        Updater.get().runUpdate();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            errorAndExit(e, "An error occurred while creating the game container.");
        }
    }

    public static void close() {
        // close databases
        DBController.closeConnections();

        // cancel all downloads
        DownloadList.get().cancelAllDownloads();

        // close server socket
        if (SERVER_SOCKET != null) {
            try {
                SERVER_SOCKET.close();
            } catch (IOException e) {
                ErrorHandler.error("Failed to close server socket.", e, false);
            }
        }
    }

    /**
     * Throws an error and exits the application with the given message.
     *
     * @param e the exception that caused the crash
     * @param message the message to display
     */
    private static void errorAndExit(Throwable e, String message) {
		// JARs will not run properly inside directories containing '!'
        // http://bugs.java.com/view_bug.do?bug_id=4523159
        if (Utils.isJarRunning() && Utils.getRunningDirectory() != null
                && Utils.getRunningDirectory().getAbsolutePath().indexOf('!') != -1) {
            ErrorHandler.error("JARs cannot be run from some paths containing '!'. Please move or rename the file and try again.", null, false);
        } else {
            ErrorHandler.error(message, e, true);
        }
        System.exit(1);
    }
}
