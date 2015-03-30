package com.xiq.music.lrc;

public class Lyric {
	
	private Time startTime;
	private Time endTime;
	private String textContent;
	
	public String getTextContent() {
		if (textContent == null) {
			return "";
		}
		return null;
	}

	public void addTextContent(Lyric lyric) {
		if (this.startTime.mSeconds <= lyric.startTime.mSeconds) {
			
		}
	}
	
}
