package com.xiq.music.lyric;

public class Lyric {
	
	public Time startTime;//在TimeTextObject中用到
	public Time endTime;
	public String contentString;
	
	public String getTextContent() {
		if (contentString == null) {
			return "";
		}
		
		String textString = contentString.replace("<br />", "\n");
		textString = textString.replace("\\<.*?\\>", "");
		return textString;
	}

	public void addTextContent(Lyric lyric) {
		if (this.startTime.mSeconds <= lyric.startTime.mSeconds) {
			contentString = contentString + "<br />" + lyric.contentString;//当前歌词 换行 添加下一行歌词
		}else {
			contentString = lyric.contentString + "<br />" + contentString;//当前歌词超前于添加的歌词
			this.startTime.mSeconds = lyric.startTime.mSeconds;
		}
		
		if (this.endTime.mSeconds < lyric.endTime.mSeconds) {
			this.endTime.mSeconds = lyric.endTime.mSeconds;
		}
	}
	
}
