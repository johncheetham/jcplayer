/*
    PlaylistElement
    
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
import java.net.*;

public class PlaylistElement {
	
	private File file=null;	
	
	public PlaylistElement(File file) {		
		this.file=file;
	}
		
	String getPath() {
		return file.getPath();
	}	
	
	// override toString so that filename shows in the JList component
	@Override public String toString() {
		return file.getName();		
	}
}
