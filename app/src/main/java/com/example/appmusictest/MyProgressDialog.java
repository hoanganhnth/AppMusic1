package com.example.appmusictest;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyProgressDialog {
    private AlertDialog dialog;
    private ProgressBar progressBar;
    private TextView titleDialog, messageDialog;

    public MyProgressDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null);
        progressBar = view.findViewById(R.id.progress_bar);
        titleDialog = view.findViewById(R.id.titleDialog);
        messageDialog = view.findViewById(R.id.messageDialog);

        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
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
