package com.xiaoying.opensource.gesturelock;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.xiaoying.opensouce.widget.GestureLockView;

public class MainActivity extends AppCompatActivity {

    private GestureLockView mGestureLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mGestureLockView = (GestureLockView) findViewById(R.id.glv_gesture_lock);
        mGestureLockView.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener() {
            @Override
            public void OnGestureFinish(boolean success, String key) {
                if (key.length() < 4) {
                    Toast.makeText(MainActivity.this, "绘制个数不能小于4个", Toast.LENGTH_SHORT).show();
                    return;
                }

//                String savedHandMd5 = getSharedPreferences("data", MODE_PRIVATE).getString("hpassword", "");
//
//                if(savedHandMd5==null||savedHandMd5.length()<1){
//                    Toast.makeText(CheckGestureLockToLoginActivity.this, "手势解锁优化了，请用登录密码登录后->设置->修改手势密码", Toast.LENGTH_LONG).show();
//                }
//                //验证失败
//                if(wrongTimes<MAX_TRY_TIMES){
//                    wrongTimes++;
//                }else{
//                    Intent i =new Intent();
//                    i.putExtra(IS_GESTURE_VALID, false);
//                    CheckGestureLockToLoginActivity.this.setResult(RESULT_OK, i);
//                    CheckGestureLockToLoginActivity.this.finish();
//                }
//                animation = new TranslateAnimation(-20, 20, 0, 0);
//                animation.setDuration(50);
//                animation.setRepeatCount(2);
//                animation.setRepeatMode(Animation.REVERSE);
//                tv_top.setTextColor(Color.parseColor("#FF2525"));
//                tv_top.setVisibility(View.VISIBLE);
//                tv_top.setText("密码错误");
//                tv_top.startAnimation(animation);
            }
        });
    }
}
