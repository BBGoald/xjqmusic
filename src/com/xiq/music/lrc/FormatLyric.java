package com.xiq.music.lrc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.xiq.music.lrc.TimedTextObject.TimedIndex;


public class FormatLyric {

	public static final String TAG = "xjq";
	
	public static TimedTextObject parseFile(InputStream inputStream, String encodeString) throws IOException {
		
		Log.i(TAG, "	--->TimedTextObject--->parseFile");
		TimedTextObject timedTextObject = new TimedTextObject();
		List<String> listStrings = new ArrayList<String>();
		String lineString = "";
		if (encodeString == null || encodeString.equals("")) {
			encodeString = "UTF-8";
		}
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encodeString);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			lineString = bufferedReader.readLine();
			int lineCounter = 0;
			while (lineString != null) {
				lineString = lineString.trim();//delete spacing among String
				if (lineString.indexOf("[ti:") > -1) {
					String titleString = lineString.substring(lineString.indexOf(":") + 1, lineString.length() - 1);
					timedTextObject.setTitle(titleString);
				} else if (lineString.indexOf("[ar:") > -1) {
					String artiString = lineString.substring(lineString.indexOf(":") + 1, lineString.length() - 1);
					timedTextObject.setArtistString(artiString);
				} else if (lineString.indexOf("[al:") > -1) {
					String albumString = lineString.substring(lineString.indexOf(":") + 1, lineString.length() - 1);
					timedTextObject.setAlbumString(albumString);
				} else if (lineString.indexOf("[offset:") > -1) {
					String offsetString = lineString.substring(lineString.indexOf(":") + 1, lineString.length() - 1);
					timedTextObject.setOffset(Integer.parseInt(offsetString));
				} else if (lineString.indexOf("[") > -1 && lineString.indexOf("]") > -1) {
					listStrings.add(lineString);
				}
				lineCounter++;
				lineString = bufferedReader.readLine();
				if (lineString == null) {
					lineString = bufferedReader.readLine();
					lineCounter++;
				}
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		Map<Integer, Lyric> tempMap = toHaspMap(listStrings);
		return toTimedIndex(tempMap, timedTextObject);
	}

	private static Map<Integer, Lyric> toHaspMap(List<String> list) {
		if (list == null) {
			return null;
		}
		int length = list.size();
		Map<Integer, Lyric> temp = new HashMap<Integer, Lyric>();
		String lineString = "";
		for (int i = 0; i < length; i++) {
			lineString = list.get(i);
			String[] timeSplitsStrings = lineString.split("]");
			if (timeSplitsStrings.length < 2) {
				continue;
			}
			String lineContentString = lineString.substring(lineString.lastIndexOf("]") + 1);
			if (lineContentString == null || lineContentString.equals("")) {
				continue;
			}
			for (int j = timeSplitsStrings.length - 2; j >= 0; j--) {
				String startString = timeSplitsStrings[j].substring(1);
				Time time = toTime(startString);
				if (!temp.containsKey(time.mSeconds)) {
					Lyric lyric = new Lyric();
					lyric.startTime = time;
					lyric.contentString = lineContentString;
					temp.put(time.mSeconds, lyric);
				}
			}
		}
		return temp;
	}
	
	private static Time toTime(String start) {
		String timeFormatString = "mm:ss.cs";
		if (start.length() <= 5) {
			timeFormatString = "mm:ss";
		}
		return new Time(timeFormatString, start);
	}
	
	private static TimedTextObject toTimedIndex(Map<Integer, Lyric> temp, TimedTextObject timedTextObject) {
		Object[] keyArray = temp.keySet().toArray();
		Arrays.sort(keyArray);
		List<Lyric> tempList = new ArrayList<Lyric>();
		for (Object keyObject : keyArray) {
			Lyric lyric = temp.get(keyObject);
			tempList.add(lyric);
		}
		
		for (int i = 0; i < tempList.size(); i++) {
			Lyric lyric = tempList.get(i);
			if (i == keyArray.length - 1) {
				Time time = new Time(lyric.startTime.mSeconds + 2000);
				lyric.endTime = time;
			} else {
				Lyric lyric2 = temp.get(keyArray[i+1]);
				lyric.endTime = lyric2.startTime;
			}
			TimedIndex index = new TimedIndex(lyric);
			if (!timedTextObject.lyricsMap.containsKey(index)) {
				timedTextObject.lyricsMap.put(index, lyric);
			}
		}
		return timedTextObject;
	}
}
