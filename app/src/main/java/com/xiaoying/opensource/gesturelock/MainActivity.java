/*
 * Copyright (C) 2016. The Android Open Source Project.
 *
 *          yinglovezhuzhu@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.xiaoying.opensource.gesturelock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
                Log.i("MainActivity", "GestureLock---" + key);
                if (key.length() < 4) {
                    Toast.makeText(MainActivity.this, "绘制个数不能小于4个", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!"24678".equals(key)) {
                    mGestureLockView.showError();
                    Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
                    mGestureLockView.reset();
                }
            }
        });
    }
}
