package com.xjq.music.util;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import com.xjq.music.model.MusicInfomation;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MediaScanner {
	
	private static final String TAG = "bangliang";
	private Context context;
	private MusicSannerClient client = null;
	private MediaScannerConnection mediaScanConn = null;

	private String sourceFilePath = null;
	private String fileType = "audio/*";

	public MediaScanner(Context context) {
		Log.d(TAG, "******instance MediaScanner");
		Log.d(TAG, "MediaScanner this= " + this.hashCode());
		Log.d(TAG, "MediaScanner.this= " + MediaScanner.this.hashCode());
		Log.d(TAG, "MediaScanner current Thread= " + Thread.currentThread().getId());//主线程=1
		Log.d(TAG, "LocalActivity current Process= " + android.os.Process.myPid());
		this.context = context;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		Log.d(TAG, "		--->MediaScanner--->init");
		
		// 创建MusicSannerClient
		if (client == null) {
			Log.d(TAG, "		--->MediaScanner--->new MusicSannerClient()");
			client = new MusicSannerClient();
		}
		
		if (mediaScanConn == null) {
			Log.d(TAG, "		--->MediaScanner--->new MediaScannerConnection(context, client)");
			mediaScanConn = new MediaScannerConnection(context, client);
		}
	}
	

	class MusicSannerClient implements MediaScannerConnection.MediaScannerConnectionClient {

		public void onMediaScannerConnected() {
			Log.d(TAG, "		--->MediaScanner--->onMediaScannerConnected");
			
			new Thread(){
				public void run() {
					List<MusicInfomation> musicList = LocalMusicUtil.getLocalAudioList(context);
					if (null != musicList) {
						for (int i = 0; i < musicList.size(); i++) {
							try {
								File tf = new File(musicList.get(i).getPath());
								if (tf.exists()) {
									continue;
								}
								LocalMusicUtil.deleteMediaStoreFile(context, musicList.get(i).getPath());
								sleep(100);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
								try {
									mediaStartScanFile(musicList.get(i).getPath());
								} catch (Exception e2) {
									// TODO: handle exception
								}
							}
						}
					}
					
					scanFileProcess(new File(sourceFilePath));

					sourceFilePath = null;
					fileType = null;

					mediaScanConn.disconnect();
					if (null != mScanProcessListener) {
						mScanProcessListener.onScanCompleted();
						mScanProcessListener.onScanFinish();
					}
				};
			}.start();
		}

		private void scanFileProcess(java.io.File rootScanFile){
			Log.d(TAG, "		--->MediaScanner--->scanFileProcess");

			if(null == sourceFilePath){
				return;
			}
			Log.d("---tag", "----------------------------------");
			if(rootScanFile == null || !rootScanFile.exists()){
				return;
			}
			if(null != mScanProcessListener){
				Log.d("---tag", rootScanFile.getAbsolutePath());
				Log.d(TAG, "		--->MediaScanner--->scanFileProcess--->rootScanFile.getAbsolutePath()=" + rootScanFile.getAbsolutePath());

				mScanProcessListener.onScanProcess(rootScanFile.getAbsolutePath());
			}
			String showFilePath = "";
			if (!rootScanFile.isDirectory()) {
				showFilePath = rootScanFile.getAbsolutePath();

				mediaStartScanFile(showFilePath);
				if(null != mScanProcessListener){
					Log.d("---tag", showFilePath);
					Log.d(TAG, "		--->MediaScanner--->scanFileProcess--->showFilePath=" + showFilePath);

				}
				return;
			}
			
//			File[] files = rootScanFile.listFiles();// 将指定文件夹下面的文件全部列出来
			File[] files = rootScanFile.listFiles(new MusicFileFilter(".mp3"));
			
			if (files == null) {
				return;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					scanFileProcess(files[i]);
				} else {
					// 调用mediaScannerConnection.scanFile()方法，更新指定类型的文件到数据库中"audio/mpeg"
					showFilePath = files[i].getAbsolutePath();
					mediaStartScanFile(showFilePath);
					if(null != mScanProcessListener){
						Log.d("---tag", showFilePath);
					}
				}
			}
		}
		
		public void onScanCompleted(String path, Uri uri) {
			// TODO Auto-generated method stub
			mediaScanConn.disconnect();
			Log.d(TAG, "		--->MediaScanner--->onScanCompleted");
			/*if(null != mScanProcessListener){
				mScanProcessListener.onScanCompleted();
			}*/
		}
	}

	/**
	 * 扫描文件标签信息
	 * 
	 * @param sourceFilePath 文件路径 eg:/sdcard/MediaPlayer/dahai.mp3
	 * @param fileType 文件类型 eg: audio/mp3 media/* application/ogg
	 * */
	public void start(final String filepath) {
		Log.i(TAG, "		--->MediaScanner--->start");

		sourceFilePath = filepath;
		//Log.i(TAG, "		--->MediaScanner--->start #sourceFilePath= " + sourceFilePath);

		// 连接之后调用MusicSannerClient的onMediaScannerConnected()方法
		mediaScanConn.connect();
	}
	
	class MusicFileFilter implements FileFilter {
		private String condition = ".mp3";
		
		public MusicFileFilter(String condition) {  
	        this.condition = condition.toUpperCase();  
	    } 
		
		@Override
		public boolean accept(File pathname) {
			// TODO Auto-generated method stub
			if (pathname.isDirectory()) {
				return true;
			}
			
			if (MediaFile.isAudioFileType(pathname.getAbsolutePath())) {
				return true;
			}
			
			String filename = pathname.getName().toUpperCase();
			if (filename.lastIndexOf(condition) != -1) {
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	private synchronized void mediaStartScanFile(String filePath) {
		Log.d(TAG, "		--->MediaScanner--->mediaStartScanFile");

		if (null == mediaScanConn || TextUtils.isEmpty(filePath)) {
			return;
		}
		File f = new File(filePath);
		try {
			if (f == null || !f.exists()) {
				return;
			}
			Log.d(TAG, "		--->MediaScanner--->mediaStartScanFile--->scanFile");

			mediaScanConn.scanFile(filePath, fileType);
			return;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("--tag", "the Exception filepath is="+filePath);
			/*String mimeType = "";
			try {
				mimeType = MediaFile.getFileType(filePath).mimeType;
				mediaScanConn.scanFile(filePath, mimeType);
			} catch (Exception e2) {
				e2.printStackTrace();
				Log.e("--tag", "the Exception mimeType is="+mimeType);
			}*/
			
			MediaScannerConnection.scanFile(context, 
					new String[]{
					filePath}, 
					null, null);
			
			Intent scanIntent = new Intent(
			Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			String str = filePath;
			if(f.getParentFile() != null){
				str = f.getParentFile().getAbsolutePath();
			}
			Log.e("--tag", "the Exception str is="+str);
			scanIntent.setData(Uri.fromFile(new File(str)));
			context.sendBroadcast(scanIntent);
			
			mScanProcessListener.onScanProcess(filePath);
		}
	}
	
	public void destory() {
		if (mediaScanConn != null && mediaScanConn.isConnected()) {
			mediaScanConn.disconnect();
			mediaScanConn = null;
			client = null;
		}
	}
	
	public void setScanProcessListener(ScanProcessListener mScanProcessListener) {
		Log.d(TAG, "		--->MediaScanner--->setScanProcessListener");
		this.mScanProcessListener = mScanProcessListener;
	}
	
	private ScanProcessListener mScanProcessListener;
	public interface ScanProcessListener {
		public void onScanProcess(String dirName);
		public void onScanCompleted();
		public void onScanFinish();
	}
}
