package com.xjq.music.lyric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.util.Log;

import com.xjq.music.lyric.TimedTextObject.TimedIndex;
/**
 * 解析歌词，以时间作为索引，每一行歌词都对应一个开始的时间点，
 * 根据该时间点然后获取正在播放的歌曲时间，两者匹配就可以把对应歌词获取并显示出来
 * @author root
 *
 */
public class FormatLyric {

	public static final String TAG = "xjq";
	public static final Boolean DEBUG = false;

	//解析歌词
	public static TimedTextObject parseFile(InputStream inputStream,
			String encodeString) throws IOException {

		if (DEBUG)
			Log.i(TAG, "	--->FormatLyric--->parseFile ###inputStream= "
					+ inputStream + " ###encodeString= " + encodeString);
		TimedTextObject timedTextObject = new TimedTextObject();
		List<String> listStrings = new ArrayList<String>();
		String lineString = "";
		if (encodeString == null || encodeString.equals("")) {
			encodeString = "UTF-8";
		}
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, encodeString);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			// 需要将歌词文件中的[offset:]放到末尾才能读取全部行
			lineString = bufferedReader.readLine();
			if (DEBUG)
				Log.i(TAG, "	--->FormatLyric--->parseFile ###lineString= "
						+ lineString);
			int lineCounter = 0;
			while (lineString != null) {
				lineString = lineString.trim();// delete spacing among String
				// Log.i(TAG,
				// "	--->FormatLyric--->parseFile ###trim#lineString= " +
				// lineString);
				if (lineString.indexOf("[ti:") > -1) {
					// Log.i(TAG,
					// "	--->FormatLyric--->parseFile ###lineString.indexOf([ti:)= "
					// + lineString);
					String titleString = lineString.substring(
							lineString.indexOf(":") + 1,
							lineString.length() - 1);
					timedTextObject.setTitle(titleString);
				} else if (lineString.indexOf("[ar:") > -1) {
					// Log.i(TAG,
					// "	--->FormatLyric--->parseFile ###lineString.indexOf([ar:)= "
					// + lineString);
					String artiString = lineString.substring(
							lineString.indexOf(":") + 1,
							lineString.length() - 1);
					timedTextObject.setArtistString(artiString);
				} else if (lineString.indexOf("[al:") > -1) {
					// Log.i(TAG,
					// "	--->FormatLyric--->parseFile ###lineString.indexOf([al:)= "
					// + lineString);
					String albumString = lineString.substring(
							lineString.indexOf(":") + 1,
							lineString.length() - 1);
					timedTextObject.setAlbumString(albumString);
				} else if (lineString.indexOf("[offset:") > -1) {
					// String offsetString =
					// lineString.substring(lineString.indexOf(":") + 1,
					// lineString.length() - 1);
					// timedTextObject.setOffset(Integer.parseInt(offsetString));
				} else if (lineString.indexOf("[") > -1
						&& lineString.indexOf("]") > -1) {
					listStrings.add(lineString);
					// Log.i(TAG,
					// "	--->FormatLyric--->parseFile ###add#lineString.indexOf([ | ])== "
					// + lineString);
				}
				lineCounter++;
				lineString = bufferedReader.readLine();
				// Log.i(TAG,
				// "	--->FormatLyric--->parseFile ###bufferedReader.readLine()= "
				// + lineString);
				if (lineString == null) {
					lineString = bufferedReader.readLine();
					lineCounter++;
					// Log.i(TAG,
					// "	--->FormatLyric--->parseFile ###lineString == null ###lineCounter= "
					// + lineCounter);
				}
				// Log.i(TAG, "	--->FormatLyric--->parseFile ###lineCounter= " +
				// lineCounter);
			}
			if (DEBUG)
				Log.i(TAG, "	--->FormatLyric--->parseFile ###lineCounter= "
						+ lineCounter);
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
		// Log.i(TAG, "	--->FormatLyric--->parseFile ###listStrings= " +
		// listStrings);
		Map<Integer, Lyric> tempMap = toHaspMap(listStrings);
		return toTimedIndex(tempMap, timedTextObject);
	}

	//根据一行歌词的开始结束时间，将该歌词写进哈希表中，以后就可以根据该时间来获取对应行的歌词
	@SuppressLint("UseSparseArrays")
	private static Map<Integer, Lyric> toHaspMap(List<String> list) {
		// Log.i(TAG, "	--->FormatLyric--->toHaspMap ###list= " + list);
		if (list == null) {
			return null;
		}
		int length = list.size();
		Map<Integer, Lyric> temp = new HashMap<Integer, Lyric>();
		String lineString = "";
		for (int i = 0; i < length; i++) {
			lineString = list.get(i);
			String[] timeSplitsStrings = lineString.split("]");
			// Log.i(TAG, "	--->FormatLyric--->toHaspMap ###timeSplitsStrings= "
			// + timeSplitsStrings);
			if (timeSplitsStrings.length < 2) {
				continue;
			}
			String lineContentString = lineString.substring(lineString
					.lastIndexOf("]") + 1);
			// Log.i(TAG, "	--->FormatLyric--->toHaspMap ###lineContentString= "
			// + lineContentString);
			if (lineContentString == null || lineContentString.equals("")) {
				continue;
			}
			for (int j = timeSplitsStrings.length - 2; j >= 0; j--) {
				String startString = timeSplitsStrings[j].substring(1);
				// Log.i(TAG, "	--->FormatLyric--->toHaspMap ###startString= " +
				// startString);
				Time time = toTime(startString);
				if (!temp.containsKey(time.mSeconds)) {
					Lyric lyric = new Lyric();
					lyric.startTime = time;
					lyric.contentString = lineContentString;
					temp.put(time.mSeconds, lyric);
				}
			}
		}
		// Log.i(TAG, "	--->FormatLyric--->toHaspMap ###return temp= " + temp);
		return temp;
	}

	//获取每行歌词的开始时间
	private static Time toTime(String start) {
		String timeFormatString = "mm:ss.cs";
		if (start.length() <= 5) {
			timeFormatString = "mm:ss";
		}
		if (DEBUG)
			Log.i(TAG, "	--->FormatLyric--->toTime ##timeFormatString= "
					+ timeFormatString);
		return new Time(timeFormatString, start);
	}

	//将每行歌词的开始时间转换为索引
	private static TimedTextObject toTimedIndex(Map<Integer, Lyric> temp,
			TimedTextObject timedTextObject) {
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
				if (DEBUG)
					Log.i(TAG,
							"	--->FormatLyric--->toTimedIndex ##lyric.startTime.mSeconds= "
									+ lyric.startTime.mSeconds);
				Time time = new Time(lyric.startTime.mSeconds + 2000);
				lyric.endTime = time;
			} else {
				Lyric lyric2 = temp.get(keyArray[i + 1]);
				lyric.endTime = lyric2.startTime;
			}
			TimedIndex index = new TimedIndex(lyric);
			if (DEBUG)
				Log.i(TAG,
						"	--->FormatLyric--->toTimedIndex ##index.startIndex= "
								+ index.startIndex + " i= " + i);
			if (!timedTextObject.lyricsMap.containsKey(index)) {
				timedTextObject.lyricsMap.put(index, lyric);
			}
		}
		if (DEBUG)
			Log.i(TAG, "	--->FormatLyric--->toTimedIndex ###timedTextObject= "
					+ timedTextObject);
		return timedTextObject;
	}
}
