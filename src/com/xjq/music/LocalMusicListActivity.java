package com.xjq.music;

import java.io.File;
import java.util.List;

import com.xjq.music.model.MusicInfomation;
import com.xjq.music.player.MusicListAdapter;
import com.xjq.music.util.DatabaseHelper;
import com.xjq.music.util.FuncUtils;
import com.xjq.music.util.MediaScanner;
import com.xjq.xjqgraduateplayer.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LocalMusicListActivity extends BaseActivity implements OnClickListener{
	
	protected static final String TAG = "xjq";
	private Context mContext;
	private View lay_bottom_player;
	private ListView lv;
	private ImageView bg_img;
	private RelativeLayout lay_select_editor, lay_editor;
	private ImageView img_select_all;
	private ImageButton btn_song_editor;
	private TextView txt_cancel_editor;
	private Button btn_delete;
	private LinearLayout expandable_editor;
	private TextView local_song_num;
	private ImageButton btn_scan;
	private ImageButton imageBtnToLauncher;
	private ImageView imgToLauncherActivity;
	
	private String songNum;
	private int currentSelectedNum = 0;
	private boolean isSelected;
	
	private MusicListAdapter adapter;
	
	private List<MusicInfomation> audioList;

	private static int LOCAL_LOAD_FINISH = 1;
	private int LOCAL_SCAN_FINISH = 2;
	
	//private int currentCheckNumber = 0;


	//private List<MusicInfomation> musicList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = LocalMusicListActivity.this;
		initView();
		loadData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_local_music_list);
		setTitle("本地音乐");
		imageBtnToLauncher = (ImageButton) findViewById(R.id.btn_back);
		
		lv = (ListView) findViewById(R.id.ls_song);
		bg_img = (ImageView) findViewById(R.id.bg_img);
		lay_bottom_player = (View) findViewById(R.id.layout_bottom);
		
		local_song_num = (TextView) findViewById(R.id.local_song_num);
		btn_scan = (ImageButton) findViewById(R.id.local_scan);
		lay_editor = (RelativeLayout) findViewById(R.id.lay_editor);

		lay_select_editor = (RelativeLayout) findViewById(R.id.layout_select_editor);
		img_select_all = (ImageView) findViewById(R.id.img_select_all);
		btn_song_editor = (ImageButton) findViewById(R.id.btn_song_editor);
		txt_cancel_editor = (TextView) findViewById(R.id.txt_cancel_editor);
		expandable_editor = (LinearLayout) findViewById(R.id.expandable_editor);
		btn_delete = (Button) findViewById(R.id.btn_delete);

		imageBtnToLauncher.setOnClickListener(this);
		img_select_all.setOnClickListener(this);
		btn_song_editor.setOnClickListener(this);
		txt_cancel_editor.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		//bundle = getIntent().getExtras();
		//songNum = bundle.getString("songNum");
		btn_scan.setOnClickListener(this);

		adapter = new MusicListAdapter(this){

			@Override
			protected void addToPlayList(View view, int position) {
				// TODO Auto-generated method stub
				super.addToPlayList(view, position);
			}

			@Override
			protected void play(View view, int position) {
				// TODO Auto-generated method stub
				//super.play(view, position);
				audioList = adapter.getListData();
				if (audioList == null || audioList.size() == 0) {
					//showToast("歌曲不存在");
					Log.i(TAG, "歌曲不存在");
					return;
				}
				playMusic(audioList, position);
				FuncUtils.beginClickAnimation(LocalMusicListActivity.this,
						(View) view.getParent(),
						com.xjq.xjqgraduateplayer.R.drawable.ic_anim_music);
			}

			@Override
			protected void addSelected(boolean idAdd) {
				// TODO Auto-generated method stub
				Log.d(TAG, "	--->LocalMusicListActivity--->addSelected");
				//super.addSelected(idAdd);
				if (idAdd) {
					currentSelectedNum++;
				} else {
					currentSelectedNum--;
				}
				if (currentSelectedNum < audioList.size()) {
					img_select_all.setImageResource(R.drawable.btn_uncheck);
				} else {
					img_select_all.setImageResource(R.drawable.btn_check);
				}
				((TextView) findViewById(R.id.txt_selcect_summary))
							.setText(String.format(getResources().getString(R.string.txt_check_number), currentSelectedNum));
			}
			
		};
		lv.setAdapter(adapter);
	}

	private void loadData() {
		// TODO Auto-generated method stub
		Log.d(TAG, "		--->LocalMusicLIstActivity--->loadData");

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				audioList = DatabaseHelper.localMusicList();

				Log.d(TAG, "		--->LocalMusicListActivity--->sendEmptyMessage #musicList= " + audioList);
				handler.sendEmptyMessage(LOCAL_LOAD_FINISH);
			}
			
		}.start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			Log.d(TAG, "		--->LocalMusicListActivity--->handleMessage");
			if (msg.what == LOCAL_LOAD_FINISH) {
				if (audioList == null || audioList.size() == 0) {
					TextView msg_tv = (TextView) findViewById(R.id.msg);
					msg_tv.setText("没有本地歌曲");
					msg_tv.setVisibility(View.VISIBLE);
					bg_img.setVisibility(View.VISIBLE);
				    local_song_num.setText( "0首歌曲");
				} else {
					if (songNum != null && !songNum.equals("0")) {
						local_song_num.setText(songNum + "songs");
						local_song_num.setVisibility(View.VISIBLE);
					}
				}
				adapter.setListData(audioList);
			} else if (msg.what == LOCAL_SCAN_FINISH) {
				Log.d(TAG, "		--->LocalMusicListActivity--->handleMessage--->LOCAL_SCAN_FINISH");
				audioList.clear();
				audioList = scanLocalMusic();
				songNum = String.valueOf(audioList.size());
				if (audioList == null || audioList.size() == 0) {
					//提示没有本地歌曲
					TextView msg_tv = (TextView) findViewById(R.id.msg);
					msg_tv.setText("没有本地歌曲");
					msg_tv.setVisibility(View.VISIBLE);
					bg_img.setVisibility(View.VISIBLE);
					return;
				} else {
					findViewById(R.id.msg).setVisibility(View.GONE);
					bg_img.setVisibility(View.GONE);
					if (songNum != null && !songNum.equals("0")) {
						local_song_num.setText("总共 " + songNum + " 首歌曲");
						local_song_num.setVisibility(View.VISIBLE);
					}
				}
				adapter.setListData(audioList);
				lv.setVisibility(View.VISIBLE);
			} else {
				super.handleMessage(msg);
			}
		}

		private List<MusicInfomation> scanLocalMusic() {
			// TODO Auto-generated method stub
			Log.d(TAG, "		--->LocalMusicListActivity--->scanLocalMusic");
			return DatabaseHelper.scanLocalMusic();
		}
		
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != mMediaScanner) {
			mMediaScanner.destory();
			mMediaScanner = null;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.local_scan:
			startScanner();
			break;
		case R.id.btn_song_editor:
			selectSongEditor(v);
			break;
		case R.id.txt_cancel_editor:
			cancelSongEditor();
			break;
		case R.id.btn_back:
			backToLauncher(mContext);
			break;
		case R.id.img_select_all:
			addSelectedAll(v);
			break;
		case R.id.btn_delete:
			deleteHistory();
			break;

		default:
			break;
		}
	}

	private void deleteHistory() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(LocalMusicListActivity.this);
		builder.setTitle("delete dialog");
		builder.setMessage("Do you really want to delete this?");
		builder.setPositiveButton("sure", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				List<MusicInfomation> deleteList = adapter.getListData();
				for (int i = 0; i < deleteList.size(); i++) {
					if (deleteList.get(i).isSelected()) {
						if (isSelected) {
							File file = new File(deleteList.get(i).getPath());
							file.delete();
						}
						DatabaseHelper.deleteLocalDatas(audioList.get(i).getPath());
					}
				}
				cancelSongEditor();
				loadData();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void addSelectedAll(View v) {
		// TODO Auto-generated method stub
		if (currentSelectedNum == audioList.size()) {
			adapter.selectAll(false);
			((ImageView) v).setImageResource(R.drawable.btn_uncheck);
			currentSelectedNum = 0;
			((TextView) findViewById(R.id.txt_selcect_summary))
				.setText(String.format(getResources().getString(R.string.txt_check_number), currentSelectedNum));
		} else {
			adapter.selectAll(true);
			((ImageView) v).setImageResource(R.drawable.btn_check);
			currentSelectedNum = audioList.size();
			((TextView) findViewById(R.id.txt_selcect_summary))
				.setText(String.format(getResources().getString(R.string.txt_check_number), currentSelectedNum));
		}
	}

	private void backToLauncher(Context mContext2) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(mContext2, MainLauncherActivity.class);
		startActivity(intent);
	}

	private void cancelSongEditor() {
		// TODO Auto-generated method stub
		currentSelectedNum = 0;
		lay_editor.setVisibility(View.GONE);
		expandable_editor.setVisibility(View.GONE);
		lay_bottom_player.setVisibility(View.VISIBLE);
		lay_select_editor.setVisibility(View.VISIBLE);
		adapter.setEditor(false);
	}

	private void selectSongEditor(View v) {
		// TODO Auto-generated method stub
		currentSelectedNum = 0;
		lay_bottom_player.setVisibility(View.GONE);
		lay_select_editor.setVisibility(View.GONE);
		lay_editor.setVisibility(View.VISIBLE);
		expandable_editor.setVisibility(View.VISIBLE);
		img_select_all.setImageResource(R.drawable.btn_uncheck);
		adapter.setEditor(true);
	}

	private void startScanner() {
		// TODO Auto-generated method stub
		Log.d(TAG, "		--->LocalMusicActivity--->startScanner");

		((TextView) findViewById(R.id.msg)).setVisibility(View.GONE);
		bg_img.setVisibility(View.GONE);
		lv.setVisibility(View.GONE);
		
		String scanFilePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		if (null == mMediaScanner) {
			mMediaScanner = new MediaScanner(LocalMusicListActivity.this);
			mMediaScanner.setScanProcessListener(scanMusicFileListener);
		}
		mMediaScanner.start(scanFilePath);
	}
	private MediaScanner mMediaScanner;//=null
	//my define interface
	private MediaScanner.ScanProcessListener scanMusicFileListener = new MediaScanner.ScanProcessListener() {
		
		@Override
		public void onScanProcess(String dirName) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onScanFinish() {
			// TODO Auto-generated method stub
			Log.d(TAG, "		--->LocalMusicActivity--->onScanFinish");
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.sendEmptyMessageDelayed(LOCAL_SCAN_FINISH, 500);
				}
			}, 1000);
		}
		
		@Override
		public void onScanCompleted() {
			// TODO Auto-generated method stub
			
		}
	};
}
