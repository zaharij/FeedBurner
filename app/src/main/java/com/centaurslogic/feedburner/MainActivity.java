package com.centaurslogic.feedburner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.centaurslogic.feedburner.ui.feeds.FeedsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new FeedsFragment()).commit();
        }
    }
}
