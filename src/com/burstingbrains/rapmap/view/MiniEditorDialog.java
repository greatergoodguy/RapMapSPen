package com.burstingbrains.rapmap.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.burstingbrains.rapmap.R;

public class MiniEditorDialog extends DialogFragment {

	public MiniEditorDialog() {
	}

	/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sdk_example_mini_edit, container);
        return view;
    }
	*/
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Create a new Dialog and apply a transparent window background style
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.dialog_theme);                 
        AlertDialog dialog = builder.create();

        // Create the custom dialog layout and set view with initial spacing parameters to prevent black background
        View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.sdk_example_mini_edit, null);
        dialog.setView(dialogLayout, 0, 0, 0, 0);

        // Change the standard gravity of the dialog to Top | Right.
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();       
        wlmp.gravity = Gravity.TOP | Gravity.RIGHT;

        return dialog;
    }

}
