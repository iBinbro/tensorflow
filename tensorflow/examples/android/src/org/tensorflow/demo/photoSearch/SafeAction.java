package org.tensorflow.demo.photoSearch;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.*;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.tensorflow.demo.R;

/**
 * Created by mgo983 on 8/18/17.
 */

public class SafeAction extends DialogFragment {

    public static SafeAction newInstance() {
        SafeAction dialog = new SafeAction();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);

        dialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        return dialog;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.safe_action_dialog, container, false);
                //inflater.inflate(R.layout.safe_action_dialog, container, true);

        TextView textView = (TextView) rootView.findViewById(R.id.safety_question);

        Drawable myIconAccept = getResources().getDrawable( R.drawable.ic_done_black_24dp);
        ColorFilter filterMyIconAccept = new LightingColorFilter(Color.BLACK, getResources().getColor(R.color.abc_search_url_text_normal));
        myIconAccept.setColorFilter(filterMyIconAccept);

        Drawable myIconReject = getResources().getDrawable( R.drawable.ic_clear_black_24dp);
        ColorFilter filterMyIconReject = new LightingColorFilter(Color.BLACK, getResources().getColor(R.color.abc_search_url_text_normal));
        myIconReject.setColorFilter(filterMyIconReject);

        Bundle bundle = this.getArguments();
        String safetyQuestion = bundle.getString(GalleryActivity.EXTRA_SAFE_ACTION_MSG);
        final String safetyMenuID = bundle.getString(GalleryActivity.EXTRA_SAFE_ACTION_MENU_ITEM);
        textView.setText(safetyQuestion);


        Button btnOk = (Button) rootView.findViewById(R.id.safety_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnokOrCancel) getActivity()).okOrCancel(true, safetyMenuID );
                dismiss();
            }
        });

        Button btnCancel = (Button) rootView.findViewById(R.id.safety_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnokOrCancel) getActivity()).okOrCancel(false, safetyMenuID);
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

    //an interface that helps me to pass information about the button pressed
    //"ok" is passed when ok is pressed and cancel is passed when cancel is pressed.
    public interface OnokOrCancel{
        public void okOrCancel(boolean okOrCancel, String menuID);
    }
}
