package org.tensorflow.demo.photoSearch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import org.tensorflow.demo.photoSearch.Adapter.GridAdapter;
import org.tensorflow.demo.photoSearch.GalleryActivity;
import org.tensorflow.demo.photoSearch.OpenGalleryObjectActivity;
import org.tensorflow.demo.R;

import java.util.ArrayList;

/**
 * Created by mgo983 on 5/4/17.
 */

public class ImageGridAdapter extends GridAdapter {

    //private Context context;

    //private LayoutInflater inflater;

    //private String[] imageUrls;

    //private Color availableColor = new Color();

    private boolean[] checked;

    //private CheckBox selectAllCheckBox;


    public ImageGridAdapter(Context context, String[] imageUrls){
        super(context, R.layout.item_grid,imageUrls);

        this.context = context;

        this.imageUrls = imageUrls;

        //this.checked = new boolean[imageUrls.length];

        inflater = LayoutInflater.from(context);


    }

    public ImageGridAdapter(Context context, String[] imageUrls, CheckBox checkBox){
        super(context, R.layout.item_grid,imageUrls);

        this.context = context;

        this.imageUrls = imageUrls;

        this.checked = new boolean[imageUrls.length];

        inflater = LayoutInflater.from(context);

        //this.selectAllCheckBox = checkBox;

    }

    public void addItem(final String item){

    }


    public ArrayList<Integer> getCheckedboxes(View view){
        ArrayList<Integer> checkedPositions = new ArrayList<>();
        GridView gridView = ((GridView) view);
        for (int i = 0; i < gridView.getCount() - 1; i++){
            FrameLayout gridItem = (FrameLayout) gridView.getChildAt(i);
            CheckBox checkBox = (CheckBox) gridItem.findViewById(R.id.image_checkbox);
            if (checkBox.isChecked())
                checkedPositions.add((Integer) i);
        }

        return checkedPositions;
    }



    public String getImageUrl(int position){
        return imageUrls[position];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView){
            convertView = inflater.inflate(R.layout.item_grid,parent,false);
        }

        FrameLayout child = (FrameLayout) convertView.findViewById(R.id.item_grid);

        ImageView imageView = (ImageView) child.findViewById(R.id.film_fragment_image_view);

        CheckBox checkBox = (CheckBox) child.findViewById(R.id.image_checkbox);

        CheckBox imageButton = (CheckBox) ((GalleryActivity) context).findViewById(R.id.btn_slide);

        String searchParam = imageUrls[position];

        //isColor(searchParam, imageView);

        setOnLongClickListener(child, checkBox, position, parent);
        setOnClickListener(child, imageView, position);
        if (GalleryActivity.ONLONGCLICKMODE)  {
            checkBox.setVisibility(View.VISIBLE);
            if (checked[position]) checkBox.setChecked(true);

        }else{
            checkBox.setVisibility(View.INVISIBLE);
//
        }

        if (GalleryActivity.SELECTALLIMAGES){
            if (imageButton != null && imageButton.isChecked()){
                checkBox.setChecked(true);
                checked[position] = true;
            }else {
                checkBox.setChecked(false);
                checked[position] = false;
            }
        }



        Glide
                .with(context)
                .load(imageUrls[position])
                .centerCrop()
                .into(imageView);

        return convertView;
    }


    private void setOnLongClickListener(final FrameLayout frameLayout, final CheckBox checkBox, final int position, final ViewGroup parent){
        frameLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                imageAnimation(frameLayout,position);
                final ActionBar actionBar = ((GalleryActivity) context).getSupportActionBar();
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                //final CheckBox imageButton = (CheckBox) ((GalleryActivity) context).findViewById(R.id.btn_slide);
                final CheckBox imageButton = (CheckBox) ((GalleryActivity) context).findViewById(R.id.image_checkbox);

                if(!GalleryActivity.ONLONGCLICKMODE){
                    ((GalleryActivity) context).invalidateOptionsMenu(); // this causes the onprepareOptionsMenu to be called
                    //imageButton = (CheckBox) findViewById(R.id.btn_slide); //select all button should be visible

                    //set current grid item checkbox to true

                    if (imageButton != null){
                        imageButton.setVisibility(View.VISIBLE);
                        GalleryActivity.ONLONGCLICKMODE = true;
                        checked[position] = true;
                        notifyDataSetChanged();
                    }

                }

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            GalleryActivity.SELECTALLIMAGES = true;
                            notifyDataSetChanged();
                    }
                });


                return true;
            }

        });
    }


    private void setOnClickListener(final FrameLayout frameLayout, final ImageView imageView, final int position){
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!GalleryActivity.ONLONGCLICKMODE){
                    Intent OpenGalleryActivityIntent = new Intent(context, OpenGalleryObjectActivity.class);

                    SharedPreferences sharedPreference;
                    SharedPreferences.Editor editor;
                    sharedPreference = context.getSharedPreferences(GalleryActivity.IMGFILENAME, context.MODE_PRIVATE);
                    editor = sharedPreference.edit();

                    editor.putString(GalleryActivity.IMGFILEKEY, imageUrls[position]);
                    editor.commit();
                    context.startActivity(OpenGalleryActivityIntent);
                }else{
                    imageAnimation(frameLayout, position);
                    final ActionBar actionBar = ((GalleryActivity) context).getSupportActionBar();
                    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                    checkCurrentItem(frameLayout.findViewById(R.id.image_checkbox));
                }
            }
        });
    }


    public void imageAnimation(final FrameLayout child, int position){
        //final FrameLayout child = (FrameLayout) gridView.getChildAt(position);
        if (child != null){
            child.animate().scaleX(0.8f);
            child.animate().scaleY(0.8f);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    child.animate().scaleX(1.0f);
                    child.animate().scaleY(1.0f);

                }
            }, 200);
        }
    }

    public void checkCurrentItem(View view){
        CheckBox checkBox = (CheckBox) view;
        checkBox.setVisibility(View.VISIBLE);
        if (checkBox.isChecked()){
            checkBox.setChecked(false);
        }else{
            checkBox.setChecked(true);
        }
    }

    /*private  void isColor(String searchParam, ImageView mImage){
        try {

            if (availableColor.searchColor(searchParam)){
                Class res = R.color.class;
                Field field = res.getField( searchParam );
                int colorId = field.getInt(null);
                if (availableColor.searchColor(searchParam)){
                    mImage.setBackgroundColor(context.getResources().getColor(colorId));

                }
            }

        }catch (NoSuchFieldException e){

        }catch (IllegalAccessException e){

        }


    }*/

}
