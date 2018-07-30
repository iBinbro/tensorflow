package org.tensorflow.demo.photoSearch;

import android.app.Dialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.tensorflow.demo.R;

/**
 * Created by mgo983 on 6/13/17.
 */

public class ImageDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_dialog, container, false);

        ImageView imageView = (ImageView)  rootView.findViewById(R.id.new_dialog);
        Bundle bundle = this.getArguments();
        String imageUrl = bundle.getString(ImageExplanationActivity.EXTRA_DIALOG_IMAGE);
        Glide
                .with(getActivity())
                .load(imageUrl)
                .centerCrop()
                .into(imageView);

        //dismiss dialog on click x button
        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.close_dialog);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Inflate the layout to use as dialog or embedded fragment
        return rootView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.


        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        return dialog;
    }

}
