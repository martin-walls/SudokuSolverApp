package com.example.sudokusolver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.DialogFragment;

public class ProgressDialog extends DialogFragment {
    private ProgressBar progressBar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View progressView = inflater.inflate(R.layout.dialog_progress, null);
        builder.setView(progressView);

        progressBar = progressView.findViewById(R.id.progress_bar);

        return builder.create();
    }
}
