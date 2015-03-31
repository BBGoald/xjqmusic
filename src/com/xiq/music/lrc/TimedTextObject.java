package com.xiq.music.lrc;

import java.util.TreeMap;

public class TimedTextObject {

	private String titleString = "";
	private String artistString = "";
	private String albumString = "";
	private int offset = 0;
	
	TreeMap<TimedIndex, Lyric> lyricsMap;
	
	protected TimedTextObject() {
		lyricsMap = new TreeMap<TimedTextObject.TimedIndex, Lyric>();
	}
	
	public Lyric getLyric(TimedIndex index) {
		return lyricsMap.get(index);
	}

	public String getTitle() {
		return titleString;
	}
	public void setTitle(String titleString) {
		this.titleString = titleString;
	}
	public String getArtistString() {
		return artistString;
	}
	public void setArtistString(String artistString) {
		this.artistString = artistString;
	}
	public String getAlbumString() {
		return albumString;
	}
	public void setAlbumString(String albumString) {
		this.albumString = albumString;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	
	
	public static class TimedIndex implements Comparable<TimedIndex>{
		
		public int startIndex;
		public int endIndex;
		
		public TimedIndex(int startIndex, int endIndex) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		public TimedIndex(int startIndex) {
			this.startIndex = startIndex;
			this.endIndex = startIndex;
		}

		public TimedIndex(Lyric lyric) {
			this.startIndex = lyric.startTime.mSeconds;
			this.endIndex = lyric.endTime.mSeconds;
		}
		
		@Override
		public int compareTo(TimedIndex another) {
			// TODO Auto-generated method stub
			if (this.endIndex >= another.endIndex && this.startIndex <= another.startIndex
					|| this.endIndex <= another.endIndex && this.startIndex >= another.startIndex) {
				return 0;
			}
			if (this.startIndex > another.startIndex) {
				return 1;
			}
			return -1;
		}
		
	}
}
