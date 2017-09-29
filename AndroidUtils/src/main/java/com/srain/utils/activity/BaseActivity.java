package com.srain.utils.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by liushuanglong on 2017/9/29.
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean hasExtra(String key) {
        return this.getIntent().hasExtra(key);
    }

    protected <T> T getExtra(String key) {
        if (hasExtra(key)) {
            Intent intent = this.getIntent();
            Object obj = intent.getExtras().get(key);
            return (T) obj;
        }
        return null;
    }
}
