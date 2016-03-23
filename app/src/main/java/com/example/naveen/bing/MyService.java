package com.example.naveen.bing;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.drawable.Drawable;
import android.widget.Toast;


public class MyService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Toast.makeText(this, "onStartJob", Toast.LENGTH_SHORT).show();
        MainActivity.runTask();
        jobFinished(params, false);
        if (MainActivity.activity != null)
            MainActivity.imageView.setImageDrawable(Drawable.createFromPath(MyTask.imagePath));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
