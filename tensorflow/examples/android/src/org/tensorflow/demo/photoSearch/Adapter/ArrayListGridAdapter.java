package org.tensorflow.demo.photoSearch.Adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import org.tensorflow.demo.photoSearch.AccessorsAndSetters.Color;
import org.tensorflow.demo.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by mgo983 on 11/15/17.
 */
/*
Named array list grid adapter because the other grid adapters did not accept an array of items
as a whole. THey were built to deal with an item at a time.
* */

public class ArrayListGridAdapter extends AphasiaAdapter {

    private final TextToSpeech myTTS;

    public Context context;

    public LayoutInflater inflater;

    public String[] imageUrls;

    public ArrayList mData = new ArrayList();

    public ArrayList mWord = new ArrayList();

    private Color availableColor = new Color();

    public ArrayListGridAdapter(Context context, int resource, TextToSpeech tts){
        super(context,resource);

        this.context = context;

        inflater = LayoutInflater.from(context);

        this.myTTS = tts;

    }



    public ArrayListGridAdapter(Context context, String[] imageUrls, TextToSpeech tts){
        super(context, R.layout.item_grid,imageUrls);

        this.context = context;

        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);

        this.myTTS = tts;

    }

    public ArrayListGridAdapter(Context context, int resource, String [] objects, TextToSpeech tts){
        super(context, resource, objects);

        this.context = context;

        inflater = LayoutInflater.from(context);

        this.myTTS = tts;

    }

    public void addItem(final String item, final String word){
        mData.add(item);
        mWord.add(word);
        notifyDataSetChanged();
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

    public void setImageUrls(String[] imageUrls){
        this.imageUrls = imageUrls;
    }

    public String getImageUrl(int position){
        return imageUrls[position];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView){
            convertView = inflater.inflate(R.layout.item_text_grid,parent,false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.film_fragment_image_view);

        TextView textView = (TextView) convertView.findViewById(R.id.image_text_view);
        textView.setVisibility(View.VISIBLE);
        String word = (String) mWord.get(position);
        textView.setText(word);

        imageDescriptionSpeak(imageView, word);

        String searchParam = (String) mData.get(position);

        isColor(searchParam, imageView);


        Glide
                .with(context)
                .load(searchParam)
                .into(imageView);

        return convertView;
    }



    private  void isColor(String searchParam, ImageView mImage){
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


    }

    private void imageDescriptionSpeak(ImageView imageView, final String word){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null);

            }
        });
    }
}
