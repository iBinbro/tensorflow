package org.tensorflow.demo.photoSearch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import org.tensorflow.demo.photoSearch.AccessorsAndSetters.Color;
import org.tensorflow.demo.photoSearch.CommonDetailOpen;
import org.tensorflow.demo.photoSearch.DetailActivity;
import org.tensorflow.demo.photoSearch.EditCaptionDialog;
import org.tensorflow.demo.photoSearch.FetchClipArt;
import org.tensorflow.demo.photoSearch.ImageDialog;
import org.tensorflow.demo.photoSearch.JSONHandler;
import org.tensorflow.demo.photoSearch.OpenClipArtJSONHandler;
import org.tensorflow.demo.photoSearch.PixabayJSONHandler;
import org.tensorflow.demo.R;
import org.tensorflow.demo.photoSearch.SafeAction;

import org.json.JSONException;
import org.tensorflow.demo.photoSearch.SelectActionFragment;
import org.tensorflow.demo.photoSearch.WordCategoriesActivity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by mgo983 on 4/3/17.
 */



public class ButtonTextAdapter extends AphasiaAdapter {

    private final Context context;
    private final TextToSpeech myTTS;
    private String searchEngine;
    private Color availableColor = new Color();

    //private ArrayList mData = new ArrayList();
    public ArrayList mData = new ArrayList();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_RESULT = 2;
    private static final int TYPE_MAX_COUNT= TYPE_RESULT + 1;

    public final static String EXTRA_SAFE_ACTION_MSG = "com.example.android.sunshine.safeActionMessage";
    public final static String EXTRA_SAFE_ACTION_MENU_ITEM = "com.example.android.sunshine.safeActionMenuItem";
    public final static String EXTRA_SAFE_ACTION_RESULT = "com.example.android.sunshine.safeActionResult";
    public final static String EXTRA_SAFE_ACTION_ADAPTER = "com.example.android.sunshine.buttonTextAdapter";


    private TreeSet imageSet = new TreeSet();
    private TreeSet resultSet = new TreeSet();

    public final static String SEARCH_PARAM = "SEARCH_PARAM";
    private final static String EXTRA_DIALOG_IMAGE = "com.example.android.sunshine.extraImage";

    public int EDITED_POSITION;

    private List<ImageButton> editButtons = new LinkedList<>();
    private List<ImageButton> deleteButtons = new LinkedList<>();


    private LayoutInflater inflater;
    public ButtonTextAdapter(Context context,
                         TextToSpeech myTTS, String searchEngine) {
        super(context, R.layout.list_item_search);
        this.context = context;
        this.myTTS = myTTS;
        this.searchEngine = searchEngine;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public void addItem(final String item){
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addImage(final String imageUrl){
        mData.add(imageUrl);
        imageSet.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    public void addResult(final String searchResult){
        mData.add(searchResult);
        resultSet.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    private void makeItemsEditDeleteVisible(){

        for(ImageButton btn : editButtons){
            btn.setVisibility(View.VISIBLE);
        }

        for(ImageButton btn : deleteButtons){
            btn.setVisibility(View.VISIBLE);
        }

    }

    public void makeItemsEditDeleteInvisible(){
        for(ImageButton btn : editButtons){
            btn.setVisibility(View.INVISIBLE);
        }

        for(ImageButton btn : deleteButtons){
            btn.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public int getItemViewType (int position){
        if (imageSet.contains(position)){
            return TYPE_IMAGE;
        }
        else if(resultSet.contains(position)){
            return TYPE_RESULT;
        }
        else{
            return TYPE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount(){
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount(){
        return mData.size();
    }

    @Override
    public String getItem(int position){
        return (String) mData.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View view, final ViewGroup parent) {

        int type = getItemViewType(position);
        final ViewHolder mHolder;
        if (null == view){
            switch (type){
                case TYPE_ITEM:
                    view = inflater.inflate(R.layout.list_item_search, null);
                    break;
                case TYPE_IMAGE:
                    view = inflater.inflate(R.layout.list_item_image, null);
                    break;
                case TYPE_RESULT:
                    view = inflater.inflate(R.layout.list_item_result, null);
            }
        }
            switch (type){
                case TYPE_ITEM:
                    mHolder = new ViewHolder();
                    mHolder.mText =  view.findViewById(R.id.list_item_word_textview);
                    final String[] newString = mData.get(position).toString().split("&&");

                    String capNewString = newString[0].substring(0,1).toUpperCase() + newString[0].substring(1);

                    mHolder.mText.setText(capNewString);

                    mHolder.mImage =  view.findViewById(R.id.search_image);
                    mHolder.mImageButton =  view.findViewById(R.id.list_delete_button);
                    mHolder.mImageButtonEdit =  view.findViewById(R.id.list_edit_button);
                    mHolder.mImgBtnAcceptEdit =  view.findViewById(R.id.list_accept_edit_button);
                    mHolder.mImgBtnRejectEdit =  view.findViewById(R.id.list_reject_edit_button);
                    mHolder.mEditText =  view.findViewById(R.id.list_item_word_editview);

                    glideLoadImage(newString[1], newString[0] ,mHolder);

                    makeProgressBarInvisible(mHolder, view);
                    setImageOnClickListener(mHolder, newString[0]);
                    setTextOnClickListener(mHolder);

                    setDeletBtnClickListener(mHolder, position);
                    setEditBtnClickListener(mHolder, position);

                    setEditAcceptClickListener(mHolder);
                    setEditRejectClickListener(mHolder);

                    editButtons.add(mHolder.mImageButton);
                    deleteButtons.add(mHolder.mImageButtonEdit);

                    setTextToListenForLongClick(mHolder.mText);
                    if (((CommonDetailOpen) context).ONLONGCLICKMODE){
                        makeItemsEditDeleteVisible();
                    }

                    view.setTag(mHolder);
                    break;
                case TYPE_IMAGE:
                    mHolder = new ViewHolder();

                    final File imgFile = new  File(mData.get(position).toString().replace("file://",""));

                    mHolder.mImage =  view.findViewById(R.id.list_captured_image);
                    mHolder.mImageButtonEdit = view.findViewById(R.id.edit_image_caption);

                    Glide.with(context).load(imgFile).into(mHolder.mImage);

                    mHolder.mImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (((CommonDetailOpen) context).ONLONGCLICKMODE) return;
                            DialogFragment newFragment = new ImageDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_DIALOG_IMAGE, imgFile.toString());

                            newFragment.setArguments(bundle);
                            newFragment.show(((AppCompatActivity) context).getSupportFragmentManager(),"what?");

                        }
                    });

                    setImageEditBtnClickListener(mHolder);
                    Log.d("fileName",imgFile.toString());

                    //setResultRefreshClickListener(mHolder);

                    view.setTag(mHolder);
                    break;
                case TYPE_RESULT:
                    mHolder = new ViewHolder();
                    mHolder.mText = view.findViewById(R.id.list_search_result_text);
                    final String resultString = mData.get(position).toString();

                    String capResultString = resultString.substring(0,1).toUpperCase() + resultString.substring(1);
                    mHolder.mText.setText(capResultString);
                    setTextOnClickListener(mHolder);
                    view.setTag(mHolder);
                    break;

            }



        ((ListView) parent).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!((CommonDetailOpen) context).ONLONGCLICKMODE && !((CommonDetailOpen) context).ONEDITMODE){
                    ((CommonDetailOpen) context).ONLONGCLICKMODE = true;
                    makeItemsEditDeleteVisible();
                }
                return true;
            }
        });


        return view;
    }


    private static class ImageViewHolder{
        public ImageView mImage;
        public ImageButton mImageButtonEdit;

    }

    private static class ViewHolder extends ImageViewHolder{
        private TextView mText;
        private EditText mEditText;
        private ImageButton mImageButton;
        private ImageButton mImgBtnAcceptEdit;
        private ImageButton mImgBtnRejectEdit;
        private ProgressBar mProgressBar;
    }


    private  void makeProgressBarInvisible(ViewHolder mHolder, View view){
        mHolder.mProgressBar = view.findViewById(R.id.image_load_complete);
        mHolder.mProgressBar.setVisibility(View.INVISIBLE);
    }
    private void setImageOnClickListener(ViewHolder mHolder, final String focusWord){
        mHolder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CommonDetailOpen) context).ONLONGCLICKMODE ) return;
                myTTS.speak(focusWord, TextToSpeech.QUEUE_FLUSH, null);

                Intent wordCategoryActivity = new Intent(getContext(), WordCategoriesActivity.class);
                wordCategoryActivity.putExtra(SEARCH_PARAM, focusWord);
                context.startActivity(wordCategoryActivity);

                /*Intent ImageExplanationActivity = new Intent(getContext(), com.example.android.sunshine.app.ImageExplanationActivity.class);
                ImageExplanationActivity.putExtra(SEARCH_PARAM, focusWord);
                context.startActivity(ImageExplanationActivity);*/
            }
        });
    }

    private void setTextOnClickListener(ViewHolder mHolder){
        final String  speech = mHolder.mText.getText().toString();
        mHolder.mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

            }
        });
    }

    private void setDeletBtnClickListener(final ViewHolder mHolder, final int position){
        mHolder.mImageButton.setOnClickListener(new View.OnClickListener() {
            private String message = "Delete this item?";
            private String editOrDelete = "delete";
            @Override
            public void onClick(View view) {
                EDITED_POSITION = position;
                SafeAction newFragment = SafeAction.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_SAFE_ACTION_MSG, message);
                bundle.putString(EXTRA_SAFE_ACTION_MENU_ITEM, editOrDelete);

                newFragment.setArguments(bundle);
                newFragment.show(((AppCompatActivity) context).getFragmentManager(),"SAFE_ACTION_MSG");
            }
        });
    }

    private void setEditBtnClickListener(final ViewHolder mHolder, final int position){
        mHolder.mImageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHolder.mText.setVisibility(View.INVISIBLE);
                mHolder.mEditText.setText(mHolder.mText.getText());
                mHolder.mEditText.hasFocus();
                makeItemsEditDeleteInvisible();
                ((CommonDetailOpen) context).ONLONGCLICKMODE = false;
                ((CommonDetailOpen) context).ONEDITMODE = true;

                mHolder.mEditText.setVisibility(View.VISIBLE);
                mHolder.mImgBtnAcceptEdit.setVisibility(View.VISIBLE);
                mHolder.mImgBtnRejectEdit.setVisibility(View.VISIBLE);
                EDITED_POSITION = position;


            }
        });
    }

    private void setEditAcceptClickListener(final ViewHolder mHolder){
        final String message = "Accept edit?";
        final String editOrDelete = "edit";
        mHolder.mImgBtnAcceptEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SafeAction newFragment = SafeAction.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_SAFE_ACTION_MSG, message);
                bundle.putString(EXTRA_SAFE_ACTION_MENU_ITEM, editOrDelete);

                newFragment.setArguments(bundle);
                newFragment.show(((AppCompatActivity) context).getFragmentManager(),"SAFE_ACTION_MSG");
            }
        });

    }

    private void setEditRejectClickListener(final ViewHolder mHolder){
        mHolder.mImgBtnRejectEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideEditElements(
                        mHolder.mEditText,
                        mHolder.mImgBtnRejectEdit,
                        mHolder.mImgBtnAcceptEdit,
                        ((CommonDetailOpen) context),
                        mHolder.mText
                );

            }
        });
    }

    public void hideEditElements(
            EditText mEditText,
            ImageButton mImgBtnRejectEdit,
            ImageButton mImgBtnAcceptEdit,
            CommonDetailOpen mContext,
            TextView mText) {
        mEditText.setVisibility(View.INVISIBLE);
        mImgBtnRejectEdit.setVisibility(View.INVISIBLE);
        mImgBtnAcceptEdit.setVisibility(View.INVISIBLE);
        mContext.ONEDITMODE = false;

        mText.setVisibility(View.VISIBLE);
    }


    private void setTextToListenForLongClick(TextView mText){
        mText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!((CommonDetailOpen) context).ONLONGCLICKMODE && !((CommonDetailOpen) context).ONEDITMODE){
                    ((CommonDetailOpen) context).ONLONGCLICKMODE = true;
                    makeItemsEditDeleteVisible();
                }
                return true;
            }
        });

    }


    //Edit whole caption (result)

    private  void setImageEditBtnClickListener(ViewHolder mHolder){
        final String message = "Edit current caption?";
        final String editOrDelete = "edit";
        final String result = getResult();
        mHolder.mImageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditCaptionDialog newFragment = EditCaptionDialog.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_SAFE_ACTION_MSG, message);
                bundle.putString(EXTRA_SAFE_ACTION_MENU_ITEM, editOrDelete);
                bundle.putString(EXTRA_SAFE_ACTION_RESULT, result);

                newFragment.setArguments(bundle);
                newFragment.show(((AppCompatActivity) context).getFragmentManager(),"SAFE_ACTION_MSG");
            }
        });
    }
// getCurrent result if it exists
    private String getResult(){
        String result = "";

        if (resultSet.size() > 0){
            for (Object value : resultSet){
                result += mData.get((Integer) value);
            }
        }
        return result;
    }


    private String getImageLocation(){
        String stringUri = "";
        if (imageSet.size() > 0){
            for (Object value : imageSet){
                stringUri += mData.get((Integer) value);
            }
        }
        return stringUri;
    }



    private void setResultRefreshClickListener(ViewHolder mHolder, String targetFilename){
        Intent detailFragment = new Intent(getContext(), DetailActivity.class);
        Uri mLocationForPhotos = Uri.parse(getImageLocation());
        detailFragment.putExtra(SelectActionFragment.EXTRA_IMAGE,mLocationForPhotos);
        detailFragment.putExtra(SelectActionFragment.EXTRA_TARGET, targetFilename);
        getContext().startActivity(detailFragment);

    }
    private String[] getImageUrl (String searchEngine, String JSONString){

        String[] imageUrl = {};
        JSONHandler jsonHandler;
        final int FIRST_POSITION = 0;

        if (JSONString != null){
            Log.v("In PixaBay section", this.searchEngine);
            boolean localSearch = checkForFireBaseUrl(JSONString);
            if (!localSearch){
                try{
                    switch (searchEngine){
                        case FetchClipArt.ENGINE_PIXABAY:
                            jsonHandler = new PixabayJSONHandler();
                            imageUrl = jsonHandler.getImageUrl(JSONString);
                            break;
                        default:
                            jsonHandler =new OpenClipArtJSONHandler();
                            imageUrl = jsonHandler.getImageUrl(JSONString);
                            break;
                    }
                    if (imageUrl == null){
                        imageUrl = new String[1];
                        imageUrl[FIRST_POSITION] = JSONString;
                    }

                }catch (JSONException e){
                    Log.e("JSONException ", e + "");
                }

            }else
            {
                imageUrl = new String[1];
                imageUrl[FIRST_POSITION] = JSONString;
            }

        }

        return imageUrl;
    }

    private boolean checkForFireBaseUrl(String JSONString){
        return JSONString == null;
    }

    private void glideLoadImage(String JSONString, String searchString, ViewHolder mHolder){
        final int FIRST_POSITION = 0;
        String color = searchString.toLowerCase();
        if (availableColor.searchColor(color)){
            Log.v("search color content", JSONString + "and" + searchString);
            //Glide.with(context).load(R.drawable.colorwheel).centerCrop().into(mHolder.mImage);
            //Glide.with(context).load()
            setImageGridColor(mHolder.mImage, color);
            return;
        }
            String[] ImageUrl;

            ImageUrl = this.getImageUrl(this.searchEngine,JSONString);
            if (ImageUrl != null){
                Glide.with(context).load(Uri.parse(ImageUrl[FIRST_POSITION])).centerCrop().into(mHolder.mImage);
            }

    }

    private  void setImageGridColor(ImageView imageView, String color){

        try{

            Class res = R.color.class;
            Field field = res.getField( color );
            int colorId = field.getInt(null);
            if (availableColor.searchColor(color)){
                imageView.setBackgroundColor(context.getResources().getColor(colorId));

            }


        }catch(NoSuchFieldException e){

        }catch(IllegalAccessException e){

        }

    }
}
