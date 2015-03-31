package com.xiq.music.lrc;

public class Lyric {
	
	public Time startTime;//在TimeTextObject中用到
	public Time endTime;
	private String textContent;
	
	public String getTextContent() {
		if (textContent == null) {
			return "";
		}
		
		String textString = textContent.replace("<br />", "\n");
		textString = textString.replace("\\<.*?\\>", "");
		return textString;
	}

	public void addTextContent(Lyric lyric) {
		if (this.startTime.mSeconds <= lyric.startTime.mSeconds) {
			textContent = textContent + "<br />" + lyric.textContent;//当前歌词 换行 添加下一行歌词
		}else {
			textContent = lyric.textContent + "<br />" + textContent;//当前歌词超前于添加的歌词
			this.startTime.mSeconds = lyric.startTime.mSeconds;
		}
		
		if (this.endTime.mSeconds < lyric.endTime.mSeconds) {
			this.endTime.mSeconds = lyric.endTime.mSeconds;
		}
	}
	
}
