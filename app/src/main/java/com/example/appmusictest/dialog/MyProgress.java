package com.example.appmusictest.dialog;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.appmusictest.R;

public class MyProgress {
    private AlertDialog dialog;
    private ImageView progressBar;
    private ObjectAnimator objectAnimator;

    public MyProgress(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_progress2, null);
        progressBar = view.findViewById(R.id.progress_bar);

        objectAnimator = ObjectAnimator.ofFloat(progressBar, "rotation", 0f,360f);
        objectAnimator.setDuration(1500);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
        objectAnimator.setInterpolator(new LinearInterpolator());
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }



}
