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

public class MyProgressDialog {
    private AlertDialog dialog;
    private ImageView progressBar;
    private TextView titleDialog, messageDialog;
    private ObjectAnimator objectAnimator;

    public MyProgressDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_progress, null);
        progressBar = view.findViewById(R.id.progress_bar);
        titleDialog = view.findViewById(R.id.titleDialog);
        messageDialog = view.findViewById(R.id.messageDialog);

        objectAnimator = ObjectAnimator.ofFloat(progressBar, "rotation", 0f, 360f);
        objectAnimator.setDuration(1500);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
        objectAnimator.setInterpolator(new LinearInterpolator());
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
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

    public void setMessage(String text) {
        messageDialog.setText(text);
    }


}
