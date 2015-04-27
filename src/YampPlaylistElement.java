/**
 *  Copyright (C) 2015 YAMP Team
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.
    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
    USA
 */
import java.io.File;
import java.io.IOException;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;


public class YampPlaylistElement {
	//Playlist Element fields
	private File musicfile;
	private Mp3File mp3file;
	private ID3v2 id3v2tag;
	
	/**
	 * Constructor
	 * @param file File to put in new playlist element
	 */
	public YampPlaylistElement(File file) {
		this.musicfile = file;
		try {
			this.mp3file = new Mp3File(file);
		} catch (UnsupportedTagException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidDataException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		this.id3v2tag = mp3file.getId3v2Tag();
	}
	
	/**
	 * Return file musicfile
	 * @return musicfile File from playlist element
	 */
	public File getFile() {
		return musicfile;
	}
	
	/**
	 * Return Mp3File mp3file
	 * @return mp3file Mp3File from playlist element
	 */
	public Mp3File getMp3File() {
		return mp3file;
	}
	
	/**
	 * Return Tags
	 * @return id3v2tag Tags
	 */
	public ID3v2 getID3v2Tag() {
		return id3v2tag;
	}

}
