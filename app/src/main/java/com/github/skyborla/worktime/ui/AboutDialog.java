package com.github.skyborla.worktime.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.github.skyborla.worktime.R;

/**
 * Created by sebastian on 30.07.16.
 */
public class AboutDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_about, null);

        WebView webview = (WebView) v.findViewById(R.id.credits_view);
        webview.loadUrl("file:///android_asset/about.html");

        b.setView(v);

        return b.create();
    }
}
