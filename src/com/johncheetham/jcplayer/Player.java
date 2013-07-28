/*
    Player   
   
    This file is part of JCPlayer

    JCPlayer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JCPlayer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JCPlayer.  If not, see <http://www.gnu.org/licenses/>.
   
 */
 
package com.johncheetham.jcplayer;

import java.io.*;
import java.util.*;
import javazoom.jlgui.basicplayer.*;

public class Player implements BasicPlayerListener {
	
	private BasicController control;
	private boolean repeat;
	private JCPlayer jcPlayer;
	private Playlist playlist;	
	
	Player(JCPlayer jcPlayer, Playlist playlist) {
		this.jcPlayer=jcPlayer;
		this.playlist=playlist;
	}
			
	void play(PlaylistElement playlistElement)
	{
		System.out.println("playing "+playlistElement);
		int songNumber = playlist.getPosition() + 1;
		jcPlayer.setStatusBarText("Playing "+ songNumber + "/" + playlist.getPlaylistSize());			
		// Instantiate BasicPlayer.
		BasicPlayer player = new BasicPlayer();
		// BasicPlayer is a BasicController.
		control = (BasicController) player;
		// Register BasicPlayerTest to BasicPlayerListener events.
		// It means that this object will be notified on BasicPlayer
		// events such as : opened(...), progress(...), stateUpdated(...)
		player.addBasicPlayerListener(this);

		try
		{			
			// Open file, or URL or Stream (shoutcast) to play.
			control.open(new File(playlistElement.getPath()));						
			// control.open(new URL("http://yourshoutcastserver.com:8000"));
			
			// Start playback in a thread.
			control.play();
			
			// Set Volume (0 to 1.0).
			// setGain should be called after control.play().
			control.setGain(0.85);
			
			// Set Pan (-1.0 to 1.0).
			// setPan should be called after control.play().
			control.setPan(0.0);

			// If you want to pause/resume/pause the played file then
			// write a Swing player and just call control.pause(),
			// control.resume() or control.stop().			
			// Use control.seek(bytesToSkip) to seek file
			// (i.e. fast forward and rewind). seek feature will
			// work only if underlying JavaSound SPI implements
			// skip(...). True for MP3SPI (JavaZOOM) and SUN SPI's
			// (WAVE, AU, AIFF).
			
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		} 
	}
	
	public void setController(BasicController controller) {
		display("setController : "+controller);
	}
	
	public void stateUpdated(BasicPlayerEvent event) {
		// Notification of BasicPlayer states (opened, playing, end of media, ...)
		display("stateUpdated : "+event.toString());		
		if (event.getCode()==BasicPlayerEvent.EOM) {
			stop();	
			// If end of playlist and repeat not checked then stop
			if (!repeat && playlist.endOfPlaylist()) {						
				jcPlayer.setListIndex(0);		
				return;
			}
			playNext();			
		}
	}
	
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
		// Pay attention to properties. It depends on underlying JavaSound SPI
		// MP3SPI provides mp3.equalizer.
		//display("progress : "+properties.toString());
	}
	
	public void opened(Object stream, Map properties) {
		// Pay attention to properties. It's useful to get duration, 
		// bitrate, channels, even tag such as ID3v2.
		display("opened : "+properties.toString());		
	}
	
	private void display(String msg) {
		//if (out != null) out.println(msg);
		//System.out.println(msg);
	}
	
	boolean getRepeat() {
		return(repeat);
	}
	
	void setRepeat(boolean rep) {
		repeat=rep;
	}
	
	private void playNext() {			
		int plposn=playlist.incrementPosition();			
		jcPlayer.setListIndex(plposn);	
		jcPlayer.ensureIndexVisible(plposn);	
		play(jcPlayer.getPlaylistElement());		
	}
	
	void stop() {
		try {
			control.stop();
		}
		catch (Exception ex) {							
		}
		jcPlayer.setStatusBarText("Stopped");
	}	
}
