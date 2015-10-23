package com.mandaptak.android.Login;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.R;

public class IntroVideoActivity extends Activity {
  VideoView vv;
  FloatingActionButton fab;
  TextView introText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_intro_video);

    final int mUIFlag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    vv = (VideoView) findViewById(R.id.videoView);
    introText = (TextView) findViewById(R.id.intro_text);
    fab = (FloatingActionButton) findViewById(R.id.fab);

    String uri = "android.resource://" + getPackageName() + "/" + R.raw.intro;
    vv.setVideoURI(Uri.parse(uri));

    vv.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        vv.pause();
        fab.setVisibility(View.VISIBLE);
        return true;
      }
    });

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        vv.start();
        introText.setVisibility(View.GONE);
        getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
        fab.setVisibility(View.GONE);
      }
    });

    vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mediaPlayer) {
        onBackPressed();
      }
    });
  }

  @Override
  protected void onPause() {
    vv.stopPlayback();
    super.onPause();
  }

  @Override
  protected void onStop() {
    vv.stopPlayback();
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    vv.stopPlayback();
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    startActivity(new Intent(IntroVideoActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
  }
}
