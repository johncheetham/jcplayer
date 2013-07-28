/*
    JCPlayer 0.1.9    August 2008

    Copyright (C) 2008 John Cheetham    
    
    web   : http://www.johncheetham.com/projects/jcplayer
    email : developer@johncheetham.com
    
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.prefs.Preferences;

import javazoom.jlgui.basicplayer.*;

public class JCPlayer extends JFrame implements ActionListener, MouseListener {
	private static final String VERNO="0.1.9";
	private JFrame me;
	
	private JButton buttonPlay = new JButton("Play");
	private JButton buttonStop = new JButton("Stop");
	private JButton buttonPrev = new JButton("Prev");	
	private JButton buttonNext = new JButton("Next");
	
	private JCheckBox cbRepeat;
	private JCheckBox cbShuffle;
	private JScrollPane pane;	
	private JList list;	
	private DefaultListModel listModel;	
	private Player player;	
	private Playlist playlist;
	private JLabel statusLabel;	
			
	private JCPlayer() {
		super("JCPlayer");	
						
		File playerHome = new File(System.getProperty("user.home") + File.separator + ".JCPlayer");
		if(!playerHome.exists()) {
			playerHome.mkdir();
		}
		File playlistFile = new File(playerHome, "playlist.m3u");
		if (playlistFile.exists()) {
			BufferedReader plReader=null;
			try {
				plReader = new BufferedReader(new FileReader(playlistFile));
				String line=null;
				while ((line = plReader.readLine()) != null) {
					System.out.println("line="+line);
				}
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}			
		}
		
		playlist=new Playlist();
		player=new Player(this, playlist);		
		
		player.setRepeat(Preferences.userNodeForPackage(getClass()).getBoolean(PREF_REPEAT, true));
		cbRepeat = new JCheckBox("Repeat", player.getRepeat());
		cbRepeat.addItemListener(new CBRepeatListener());

		playlist.setShuffle(Preferences.userNodeForPackage(getClass()).getBoolean(PREF_SHUFFLE, true));
		cbShuffle = new JCheckBox("Shuffle", playlist.getShuffle());
		cbShuffle.addItemListener(new CBShuffleListener());
		
		// Add a panel to top of screen with buttons on
		JPanel jp1 = new JPanel(new FlowLayout());
		jp1.add(buttonPlay);
		jp1.add(buttonStop);
		jp1.add(buttonPrev);
		jp1.add(buttonNext);
		jp1.add(cbRepeat);	
		jp1.add(cbShuffle);	
		getContentPane().add(jp1, BorderLayout.NORTH);
		
		// Add a status panel to bottom of the screen
		
		statusLabel=new JLabel("Stopped");		
		Font statusLabelFont = statusLabel.getFont();		
		Font statusLabelNewFont = statusLabelFont.deriveFont(9.0f); 
		statusLabel.setFont(statusLabelNewFont);		
				
		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusPanel.add(statusLabel);
		getContentPane().add(statusPanel, BorderLayout.SOUTH);
		
		buttonPlay.addActionListener(new PlayButtonListener());
		buttonStop.addActionListener(new StopButtonListener());
		buttonNext.addActionListener(new NextButtonListener());
		buttonPrev.addActionListener(new PrevButtonListener());	
		disableButtons();		
				
		listModel = new DefaultListModel();		
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
		pane = new JScrollPane(list);
		getContentPane().add(pane, BorderLayout.CENTER);
		list.addMouseListener(this);
				
		// Add the menu bar
		JMenuBar menubar = new JMenuBar();
		JMenu file, help;
		this.setJMenuBar(menubar);
		
		// File menu		
		JMenuItem playFile = new JMenuItem("Play File");
		file = new JMenu("File");
		file.add(playFile);
		playFile.addActionListener(new PlayFilesListener());
		
		JMenuItem playDir = new JMenuItem("Play Directory");		
		file.add(playDir);
		playDir.addActionListener(new PlayDirectoryListener());
				
		file.addSeparator();
		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		exit.addActionListener(this);
	 	menubar.add(file);
			
		// Help menu
		help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		help.add(about);
		menubar.add(help);		
		help.addActionListener(this);
		about.addActionListener(this);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
				
		setSize(new Dimension(500, 500));
		pack();		
		setVisible(true);		
	}
	
	public void mouseExited(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {			
			player.stop();
			int plposn=list.getSelectedIndex();
			playlist.setPosition(plposn);			
			player.play((PlaylistElement)listModel.getElementAt(plposn));		
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		//Handles both button and menu item requests
		String command = e.getActionCommand();		
		
		if (command.equals("Exit")) {			
			this.dispose();
			System.exit(0);			
		}
		else if (command.equals("About")) {
			//ImageIcon icon = new ImageIcon(this.getClass().getResource("resources/gplv3-88x31.png"));
			String msg="JCPlayer " +VERNO+"\nAudio Player\nCopyright (C) 2008 John Cheetham\n"
			+ "\nThis program comes with ABSOLUTELY NO WARRANTY."
			+ "\nThis is free software, and you are welcome to redistribute it"
			+ "\nunder certain conditions. See the file COPYING for details\n\n";
			//JOptionPane.showMessageDialog (me,msg,"About JCPlayer",JOptionPane.PLAIN_MESSAGE,icon);   
			JOptionPane.showMessageDialog (me,msg,"About JCPlayer",JOptionPane.PLAIN_MESSAGE);   
		}
	}	
	
	void playNext() {
		player.stop();		
		int plposn=playlist.incrementPosition();		
		list.setSelectedIndex(plposn);	
		list.ensureIndexIsVisible(plposn);		
		player.play((PlaylistElement)listModel.getElementAt(plposn));		
	}
	
	void ensureIndexVisible(int index) {
		list.ensureIndexIsVisible(index);		
	}
	
	PlaylistElement getPlaylistElement() {
		return((PlaylistElement)listModel.getElementAt(list.getSelectedIndex()));		
	}
	
	private void playPrev() {
		player.stop();		
		int plposn=playlist.decrementPosition();
		list.ensureIndexIsVisible(plposn);	
		list.setSelectedIndex(plposn);		
		player.play((PlaylistElement)listModel.getElementAt(plposn));		
	}
					
	private void disableButtons() {
		buttonPlay.setEnabled(false);
		buttonStop.setEnabled(false);
		buttonNext.setEnabled(false);
		buttonPrev.setEnabled(false);			
	}
	
	private void enableButtons() {
		buttonPlay.setEnabled(true);
		buttonStop.setEnabled(true);
		buttonNext.setEnabled(true);
		buttonPrev.setEnabled(true);			
	}
	
	private void playSelected() {
		player.stop();
		int plposn=list.getSelectedIndex();	
		playlist.setPosition(plposn);		
		player.play((PlaylistElement)listModel.getElementAt(plposn));		
	}
	
	void setListIndex(int position) {
		list.setSelectedIndex(position);		
	}

	void setStatusBarText(String text) {
		statusLabel.setText(text);		
	}
		
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.
				getSystemLookAndFeelClassName());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
				
		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new JCPlayer();
				}
		});				
	}
	
	 /** constants to identify an item in the application preferences */
	 private static final String PREF_DIRECTORY = "prefDirectory";
	 private static final String PREF_FILE = "prefFile";
	 
	 private static final String PREF_REPEAT = "prefRepeat";
	 private static final String PREF_SHUFFLE = "prefShuffle";

	 class CBRepeatListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {			
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				player.setRepeat(false);
				Preferences.userNodeForPackage(getClass()).putBoolean(PREF_REPEAT, false);							
			}
			else {
				player.setRepeat(true);
				Preferences.userNodeForPackage(getClass()).putBoolean(PREF_REPEAT, true);							
			}				
			try {
				Preferences.userNodeForPackage(getClass()).flush();
			} catch (Exception ae) {
			}
		}
	 }
	 
	 class CBShuffleListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {			
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				playlist.setShuffle(false);
				Preferences.userNodeForPackage(getClass()).putBoolean(PREF_SHUFFLE, false);							
			}
			else {
				playlist.setShuffle(true);
				Preferences.userNodeForPackage(getClass()).putBoolean(PREF_SHUFFLE, true);							
			}				
			try {
				Preferences.userNodeForPackage(getClass()).flush();
			} catch (Exception ae) {
			}
		}
	 }
	 
	  class PlayButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			player.stop();			
			playSelected();
		}
	 }
	 
	 class StopButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			player.stop();			
		}
	 }
	 
	 class NextButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			playNext();			
		}
	 }
	 
	 class PrevButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			playPrev();			
		}
	 }
	 
	class PlayFilesListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String dir=Preferences.userNodeForPackage(getClass()).get(PREF_FILE, System.getProperty("user.home"));
			JFileChooser chooser;
			chooser =  new JFileChooser(dir);		
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"mp3, flac, ogg & wav files", "mp3", "flac", "ogg", "wav");
			chooser.setFileFilter(filter);				
			int retval = chooser.showDialog(me, null);
			if(retval == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();	
				dir=chooser.getSelectedFile().getAbsolutePath();		
				String fs=System.getProperty("file.separator");
				Preferences.userNodeForPackage(getClass()).put(PREF_FILE, dir.substring(0,dir.lastIndexOf(fs)));				
				listModel.clear();
				listModel.addElement(new PlaylistElement(file));	
				list.setSelectedIndex(0);
				playlist.init(listModel.size());				
				// stop anything already playing
				player.stop();				
				player.play((PlaylistElement)listModel.getElementAt(0));
				enableButtons();					
			}
		}
	
	}
	
	class PlayDirectoryListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {			
			String fs=System.getProperty("file.separator");
			String dir=Preferences.userNodeForPackage(getClass()).get(PREF_DIRECTORY, System.getProperty("user.home"));			
			JFileChooser chooser;
			chooser =  new JFileChooser(dir);			
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);				
			int retval = chooser.showDialog(me, null);
			if(retval == JFileChooser.APPROVE_OPTION) {													
				dir=chooser.getSelectedFile().getAbsolutePath();					
				Preferences.userNodeForPackage(getClass()).put(PREF_DIRECTORY, dir.substring(0,dir.lastIndexOf(fs)));				
				try {
					Preferences.userNodeForPackage(getClass()).flush();
				} catch (Exception ae) {
				}
				File fdir=new File(dir);				
				listModel.clear();				
				addToPlayList(fdir);				
				if (listModel.size() == 0) {
					disableButtons();
					player.stop();
					return;
				}											
				playlist.init(listModel.size());
				int plposn=playlist.getPosition();
				list.setSelectedIndex(plposn);
				list.ensureIndexIsVisible(plposn);	
				// stop anything already playing
				player.stop();		
				int songNumber = plposn + 1;
				setStatusBarText("Playing "+ songNumber + "/" + playlist.getPlaylistSize());
				player.play((PlaylistElement)listModel.getElementAt(plposn));
				enableButtons();				
			}		
		}
		
		void addToPlayList(File dir) {		
			File [] files;
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File file) {
					String fname = file.getName().toLowerCase();
					if (fname.endsWith(".mp3") || fname.endsWith(".flac") || fname.endsWith(".ogg") || fname.endsWith(".wav")) {
						return true;
					} else if (file.isDirectory()) {
						return true;
					}
					return false;								
				}
				public boolean accept(File dir, String name) {
					String fname = name.toLowerCase();
					File file = new File(dir,name);	
					if (fname.endsWith(".mp3") || fname.endsWith(".flac") || fname.endsWith(".ogg") || fname.endsWith(".wav")) {					
						return true;
					} else if (file.isDirectory()) {
						return true;
					}											
					return false;
				}							
			};
			files=dir.listFiles(filter);			
			if (files.length == 0) {				
				return;
			}
			Arrays.sort(files);				
			for(int i = 0; i < files.length; i++) {				
				if (files[i].isDirectory()) {
					addToPlayList(files[i]);
				} else {				
					listModel.addElement(new PlaylistElement(files[i]));	
				}
			}							
		}
	
				
	}	
}


