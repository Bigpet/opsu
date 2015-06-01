/*
 * opsu! - an open-source osu! client
 * Copyright (C) 2014, 2015 Jeffrey Han
 *
 * opsu! is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opsu! is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opsu!.  If not, see <http://www.gnu.org/licenses/>.
 */

package itdelatrisu.opsu.platform.slick;

import itdelatrisu.opsu.Container;
import itdelatrisu.opsu.GameData;
import itdelatrisu.opsu.OpsuStartup;
import itdelatrisu.opsu.Options;
import itdelatrisu.opsu.Utils;
import itdelatrisu.opsu.audio.MusicController;
import itdelatrisu.opsu.downloads.DownloadList;
import itdelatrisu.opsu.downloads.Updater;
import itdelatrisu.opsu.states.ButtonMenu;
import itdelatrisu.opsu.states.DownloadsMenu;
import itdelatrisu.opsu.states.Game;
import itdelatrisu.opsu.states.GamePauseMenu;
import itdelatrisu.opsu.states.GameRanking;
import itdelatrisu.opsu.states.MainMenu;
import itdelatrisu.opsu.states.OptionsMenu;
import itdelatrisu.opsu.states.SongMenu;
import itdelatrisu.opsu.states.Splash;
import itdelatrisu.opsu.ui.UI;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 * Main class.
 * <p>
 * Creates game container, adds all other states, and initializes song data.
 */
public class Opsu extends StateBasedGame {
        //@TODO: remove this
	private Container initedContainer;
    
        /** Game states. */
	public static final int
		STATE_SPLASH        = 0,
		STATE_MAINMENU      = 1,
		STATE_BUTTONMENU    = 2,
		STATE_SONGMENU      = 3,
		STATE_GAME          = 4,
		STATE_GAMEPAUSEMENU = 5,
		STATE_GAMERANKING   = 6,
		STATE_OPTIONSMENU   = 7,
		STATE_DOWNLOADSMENU = 8;

	/**
	 * Constructor.
	 * @param name the program name
	 */
	public Opsu(String name) {
		super(name);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new Splash(STATE_SPLASH));
		addState(new MainMenu(STATE_MAINMENU));
		addState(new ButtonMenu(STATE_BUTTONMENU));
		addState(new SongMenu(STATE_SONGMENU));
		addState(new Game(STATE_GAME));
		addState(new GamePauseMenu(STATE_GAMEPAUSEMENU));
		addState(new GameRanking(STATE_GAMERANKING));
		addState(new OptionsMenu(STATE_OPTIONSMENU));
		addState(new DownloadsMenu(STATE_DOWNLOADSMENU));
                //@TODO: remove this
                initedContainer = (Container) container;
	}

	@Override
	public boolean closeRequested() {
		int id = this.getCurrentStateID();

		// intercept close requests in game-related states and return to song menu
		if (id == STATE_GAME || id == STATE_GAMEPAUSEMENU || id == STATE_GAMERANKING) {
			// start playing track at preview position
			SongMenu songMenu = (SongMenu) this.getState(Opsu.STATE_SONGMENU);
			if (id == STATE_GAMERANKING) {
				GameData data = ((GameRanking) this.getState(Opsu.STATE_GAMERANKING)).getGameData();
				if (data != null && data.isGameplay()) {
					songMenu.resetGameDataOnLoad();
					songMenu.resetTrackOnLoad();
				}
			} else {
				songMenu.resetGameDataOnLoad();
				if (id == STATE_GAME) {
					MusicController.pause();
					MusicController.resume();
				} else
					songMenu.resetTrackOnLoad();
			}
			if (UI.getCursor().isSkinned())
				UI.getCursor().reset();
			this.enterState(Opsu.STATE_SONGMENU, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
			return false;
		}

		// show confirmation dialog if any downloads are active
		if (DownloadList.get().hasActiveDownloads() &&
		    UI.showExitConfirmation(DownloadList.EXIT_CONFIRMATION))
			return false;
		if (Updater.get().getStatus() == Updater.Status.UPDATE_DOWNLOADING &&
		    UI.showExitConfirmation(Updater.EXIT_CONFIRMATION))
			return false;

		return true;
	}

	/**
	 * Closes all resources.
	 */
	public static void close() {
		OpsuStartup.close();
	}
        
        @Override
        public void keyPressed(int key, char c)
        {   
            switch(key)
            {
                case Input.KEY_F7:
			Options.setNextFPS(initedContainer);
			break;
		case Input.KEY_F10:
			Options.toggleMouseDisabled();
			break;
		case Input.KEY_F12:
			Utils.takeScreenShot();
			break;
            }
            
            super.keyPressed(key, c);
        }
}
