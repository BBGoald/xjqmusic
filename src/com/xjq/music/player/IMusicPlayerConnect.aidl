package com.xjq.music.player;

import com.xjq.music.model.MusicInfomation;

interface IMusicPlayerConnect {
   void setPlayListAndPlay(in List<MusicInfomation> playList, int index);
   void setPlayList(in List<MusicInfomation> playList);
   void getFileList(out List<MusicInfomation> playList);
   MusicInfomation getCurrentMusicInfo();
   
   boolean rePlay();
   boolean play(int position);
   boolean pause();
   boolean playNext();
   boolean playPre();
   boolean seekTo(int rate);
   boolean stop();
   void exit();
   void destroy();
   
   int getCurPosition();
   int getDuration();
   int getPlayState();
   int getPlayMode();
   int getAudioSessionId();
   int getCurPlayIndex();
   int getMusicFileType();
   
   void setPlayMode(int mode);
   void sendPlayStateBrocast();
}