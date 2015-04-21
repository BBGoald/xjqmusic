package com.xjq.music.model;

import java.util.ArrayList;
import java.util.List;

import com.xjq.xjqgraduateplayer.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 音乐列表适配器
 * @author root
 *
 */
public class MusicListAdapter extends BaseAdapter{

	private static final String TAG = "bangliang";
	Context context;
	private List<MusicInfomation> list = new ArrayList<MusicInfomation>();
	private int currentPositon = -1;
	private boolean isEditor = false;

	
	public MusicListAdapter(Context context) {
		//super();
		Log.d(TAG, "******instance FavoritesAdapter");
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->FavoritesAdapter--->getCount #list == null ? 0 : list.size()= " + (list == null ? 0 : list.size()));
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->MusicListAdapter--->getView");

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_local_songs_list, null);
			holder.layoutPLay = (LinearLayout) convertView.findViewById(R.id.lay_play);
			holder.moreImageButton = (ImageButton) convertView.findViewById(R.id.btn_more);
			holder.txtSinger = (TextView) convertView.findViewById(R.id.txtSinger);
			holder.txtSongName = (TextView) convertView.findViewById(R.id.txt_name);
			holder.btnAddToPlayOrDeleteList = (ImageButton) convertView.findViewById(R.id.btn_add_to_list);
			holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.expandable);
			holder.myFavoriteLayout = (LinearLayout) convertView.findViewById(R.id.layout_play_fav);
			holder.downLoadLayout = (LinearLayout) convertView.findViewById(R.id.btn_download);
			
			holder.layoutPLay.setClickable(true);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MusicInfomation musicInfomation = list.get(position);
		Log.d(TAG, "	--->MusicListAdapter--->getView #musicInfomation.getArtist()= " + musicInfomation.getArtist());

		holder.txtSinger.setText(musicInfomation.getArtist());
		holder.txtSongName.setText(musicInfomation.getName());
		
		holder.layoutPLay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "	--->MusicListAdapter--->holder.layoutPLay.setOnClickListener");
				Log.d(TAG, "	--->--->--->--->click--->--->--->--->");
				play(v, position);
			}
		});
		
		holder.moreImageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMore(position);
			}
		});
		
		holder.btnAddToPlayOrDeleteList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isEditor) {
					if (list.get(position).isSelected()) {
						((ImageButton) v).setImageResource(R.drawable.btn_uncheck);
						list.get(position).setSelected(false);
						addSelected(false);
					} else {
						((ImageButton) v).setImageResource(R.drawable.btn_check);
						list.get(position).setSelected(true);
						addSelected(true);
					}
				} else {
					addToPlayList(v, position);
				}
			}
		});
		
		if (getCurrentPositon() == position) {
			holder.linearLayout.setVisibility(View.VISIBLE);
			holder.myFavoriteLayout.setClickable(true);
			holder.myFavoriteLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addToMyFavoriteSongs(position);
				}
			});
			holder.downLoadLayout.setClickable(true);
			holder.downLoadLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addToDownLoad(v, position);
				}
			});
		} else {
			holder.linearLayout.setVisibility(View.GONE);
			holder.myFavoriteLayout.setClickable(false);
			holder.downLoadLayout.setClickable(false);
		}
		
		if (isEditor) {
			if (list.get(position).isSelected()) {
				Log.d(TAG, "	--->MusicListAdapter--->getView--->R.drawable.btn_check ");
				holder.btnAddToPlayOrDeleteList.setImageResource(R.drawable.btn_check);
			} else {
				Log.d(TAG, "	--->MusicListAdapter--->getView--->R.drawable.btn_uncheck ");
				holder.btnAddToPlayOrDeleteList.setImageResource(R.drawable.btn_uncheck);
			}
		} else {
			holder.btnAddToPlayOrDeleteList.setImageResource(R.drawable.list_btn_add);
		}
		
		holder.moreImageButton.setVisibility(View.INVISIBLE);
		return convertView;
	}
	
	protected void addSelected(boolean idAdd) {
		
	}
	
	public void selectAll(boolean isSelected) {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setSelected(isSelected);
		}
		notifyDataSetChanged();//notify the holderView to refresh itself...
	}
	
	protected void addToPlayList(View view, int position) {

	}
	
	protected void addToMyFavoriteSongs(int position) {

	}
	
	protected void addToDownLoad(View view, int position) {

	}
	
	protected void play(View view, int position) {
		Log.d(TAG, "	--->MusicListAdapter--->play");
		setCurrentPositon(-1);
		notifyDataSetChanged();
	}
	
	public void setCurrentPositon(int currentPositon) {
		Log.d(TAG, "	--->MusicListAdapter--->setCurrentPositon ######currentPositon= " + currentPositon);
		this.currentPositon = currentPositon;
	}

	public int getCurrentPositon() {
		Log.d(TAG, "	--->MusicListAdapter--->getCurrentPositon ######currentPositon= " + currentPositon);
		return currentPositon ;
	}
	
	public void setEditor(boolean isEditor) {
		Log.d(TAG, "	--->MusicListAdapter--->getCurrentPositon ######isEditor= " + isEditor);
		this.isEditor = isEditor;
		if (!isEditor) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setSelected(false);
			}
		}
		currentPositon = -1;
		notifyDataSetChanged();//notify the holderView to refresh itself...
	}
	
	protected void showMore(int position) {
		Log.d(TAG, "	--->MusicListAdapter--->showMore");
		if (getCurrentPositon() == position) {
			setCurrentPositon(-1);
		} else {
			setCurrentPositon(position);
		}
		notifyDataSetChanged();
	}
	
	public void setListData(List<MusicInfomation> list) {
		Log.d(TAG, "	--->MusicListAdapter--->setListData #list= " + list);
		this.list .clear();
		this.list = list;
		notifyDataSetChanged();
	}
	
	public List<MusicInfomation> getListData() {
		Log.d(TAG, "	--->MusicListAdapter--->getListData #list= " + list);

		return list;
	}
	
	public static class ViewHolder {
		private LinearLayout layoutPLay;
		private TextView txtSongName;
		private TextView txtSinger;
		private ImageButton moreImageButton;
		private LinearLayout linearLayout;
		private ImageButton btnAddToPlayOrDeleteList;
		private LinearLayout myFavoriteLayout;
		private LinearLayout downLoadLayout;
	}
}
