package org.tensorflow.demo.photoSearch;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.demo.R;
import org.tensorflow.demo.photoSearch.Adapter.ButtonTextAdapter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by mgo983 on 7/5/18.
 * EditCaption edits the full caption in the OpenGalleryObjectActivity
 * This is useful when there is no caption at all.
 */

public class EditCaptionDialog extends DialogFragment {

    public static EditCaptionDialog newInstance() {
        EditCaptionDialog dialog = new EditCaptionDialog();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);

        dialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.text_dialog, container, false);

        //header
        TextView textView = rootView.findViewById(R.id.dialog_prompt);

        //text to edit
        final EditText editText = rootView.findViewById(R.id.text_dialog_category);

        Bundle bundle = this.getArguments();

        String caption = bundle.getString(ButtonTextAdapter.EXTRA_SAFE_ACTION_RESULT);

        textView.setText("Enter a caption");
        editText.setText(caption);
        Log.d("caption", caption);

        Button cancelButton = (Button) rootView.findViewById(R.id.text_dialog_cancel);


        //switchCaller(rootView,bundle);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        //when caption is changed save caption

        Button okButton = rootView.findViewById(R.id.text_dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAdapter(editText);
                //updateCaption();
            }
        });

        // Inflate the layout to use as dialog or embedded fragment
        return rootView;
    }

    private void updateAdapter(EditText editText){
        //dismiss dialog
        dismiss();

        //first get fileName
        SharedPreferences sharedPreferences;
        sharedPreferences = getActivity().getSharedPreferences(OpenGalleryObjectActivity.IMGFILENAME, Context.MODE_PRIVATE);
        String FileName = sharedPreferences.getString(OpenGalleryObjectActivity.IMGFILEKEY, null);


        //next write data to file
        String newCaption = editText.getText().toString();
        String[] FileNameArray = FileName.split("/");
        String TxtFileName = FileNameArray[FileNameArray.length - 1].replace(".jpg", ".txt");
        writeToFile(newCaption, TxtFileName, getContext());

        SharedPreferences.Editor editor;

        //close current opengalleryactivity
        getActivity().finish();

        //start new activity
        Intent OpenGalleryActivityIntent = new Intent(getActivity(), OpenGalleryObjectActivity.class);
        startActivity(OpenGalleryActivityIntent);
    }


    public void writeToFile(String data, String fileName, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
