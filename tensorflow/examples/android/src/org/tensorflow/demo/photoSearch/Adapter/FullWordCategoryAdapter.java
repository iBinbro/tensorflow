package org.tensorflow.demo.photoSearch.Adapter;

import android.content.Context;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tensorflow.demo.R;

/**
 * Created by mgo983 on 1/30/18.
 */

/**
 * This adapter is used to populate the Listview of the gallery activity. It is populated with all the word categories
 * categories is an array that will hold all the different word categories from the firebase database
 */

public class FullWordCategoryAdapter extends GridAdapter {

    private Context context;
    private String [] categories;

// The list item is a text view
    public FullWordCategoryAdapter(Context _context, String [] _categories){
        super(_context,R.layout.list_item_image_gallery);
        context = _context;
        categories = _categories;
    }


    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null) { convertView = inflater.inflate(R.layout.item_category,null);}

        TextView textView = (TextView) convertView.findViewById(R.id.category_name);
        textView.setText(categories[position]);



        return convertView;

    }

}
