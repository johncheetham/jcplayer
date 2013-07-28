/*   
    Playlist
    
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

import java.util.Random;

public class Playlist {	
	private int playlistPosition=0;	
	private int shufIndex=0;	
	private int playlistSize;
	private int[] shuffledPlaylist;
	boolean shuffle=false;
		
	void init(int playlistSize) {
		this.playlistSize=playlistSize;		
		shuffledPlaylist=new int[playlistSize];		
		for (int i=0;i<playlistSize;i++) {
			shuffledPlaylist[i]=i;
		}
		//shuffle
		Random randomGenerator = new Random();		
		for (int i=0;i<playlistSize;i++) {
			int randomInt = randomGenerator.nextInt(playlistSize); // generates random number in range 0 to (playlistSize-1)
			int temp=shuffledPlaylist[i];			
			shuffledPlaylist[i]=shuffledPlaylist[randomInt];
			shuffledPlaylist[randomInt]=temp;
		}	
		if (shuffle) {
			shufIndex=0;
			playlistPosition=shuffledPlaylist[shufIndex];
		} else {
			playlistPosition=0;
		}
	}
	
	int getPlaylistSize() {
		return playlistSize;
	}

	void setPosition(int plposn) {
		playlistPosition=plposn;
	}
	
	int getPosition() {	
		return playlistPosition;			
	}
	
	int incrementPosition() {
		if (shuffle) {
			shufIndex++;
			if (shufIndex > (playlistSize - 1)) {
				shufIndex=0;
			}
			playlistPosition=shuffledPlaylist[shufIndex];			
		} else {
			playlistPosition++;
			if (playlistPosition > (playlistSize - 1)) {
				playlistPosition=0;
			}				
		}
		return playlistPosition;
	}
	
	int decrementPosition() {
		if (shuffle) {
			shufIndex--;
			if (shufIndex < 0) {
				shufIndex=playlistSize - 1;
			}
			playlistPosition=shuffledPlaylist[shufIndex];			
		} else {
			playlistPosition--;
			if (playlistPosition < 0) {
				playlistPosition = playlistSize - 1;				
			}				
		}
		return playlistPosition;				
	}
	
	boolean endOfPlaylist() {
		if (shuffle) {
			if (shufIndex == playlistSize - 1) {
				return true;
			}
			else {
				return false;
			}
		} else {
			if (playlistPosition == playlistSize - 1) {
				return true;
			}
			else {
				return false;
			}
		}
		
	}
	
	boolean getShuffle() {
		return(shuffle);
	}
	
	void setShuffle(boolean shuf) {
		shuffle=shuf;
	}
	
}
