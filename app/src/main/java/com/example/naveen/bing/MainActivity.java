package com.example.naveen.bing;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int JOB_ID = 100;
    static Activity activity;
    static ImageView imageView;
    private JobScheduler mJobScheduler;

    public static void runTask() {
        MyTask myTask = new MyTask(activity);
        myTask.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        constructJob();
        imageView = (ImageView) findViewById(R.id.imageView);
        activity = MainActivity.this;
        runTask();
    }

    private void constructJob() {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,
                new ComponentName(this, MyService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(1000*3600*2);
        mJobScheduler.schedule(builder.build());
    }

}
