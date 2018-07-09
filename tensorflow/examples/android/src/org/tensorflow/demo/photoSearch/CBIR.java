package org.tensorflow.demo.photoSearch;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.tensorflow.demo.R;
import org.tensorflow.demo.photoSearch.Adapter.ImageGridAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.api.request.input.SearchClause;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.SearchHit;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.input.image.ClarifaiURLImage;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;

/**
 * Created by mgo983 on 6/22/17.
 */
// Content Based Image Retrieval class
public class CBIR extends  AsyncTask<String[], Void, ArrayList<String[]>>{
    final ClarifaiClient client = new ClarifaiBuilder("oOH6jbTgfsWll9_X55goV5uZTIgb8L8fdmoM4UQr", "4xmzUBx_N201JpiR6jVTrQPA7tgwi0GTqgmnbYI_")
                                    .client(new OkHttpClient())
                                    .buildSync();
    private String searchString;
    //private String [] clarifaiImageUrls;
    private ImageGridAdapter adapter;
    private Context context;


    public CBIR(String new_searchString, ImageGridAdapter newAdapter, Context newContext){

        searchString = new_searchString;
        //adapter = newAdapter;
        context = newContext;
    }

    Collection<ClarifaiInput> images = new Collection<ClarifaiInput>() {
        List<ClarifaiInput> clarifaiInputs = new ArrayList<>();

        @Override
        public boolean add(ClarifaiInput clarifaiInput) {
            return clarifaiInputs.add(clarifaiInput);
        }

        @Override
        public boolean addAll(Collection<? extends ClarifaiInput> collection) {

            return clarifaiInputs.addAll(collection);
        }

        @Override
        public void clear() {
            clarifaiInputs.clear();

        }

        @Override
        public boolean contains(Object o) {
            return clarifaiInputs.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return clarifaiInputs.containsAll(collection);
        }

        @Override
        public boolean isEmpty() {
            return clarifaiInputs.isEmpty();
        }

        @NonNull
        @Override
        public Iterator<ClarifaiInput> iterator() {
            return clarifaiInputs.iterator();

        }

        @Override
        public boolean remove(Object o) {
            return clarifaiInputs.remove(o);

        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return clarifaiInputs.removeAll(collection);
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return clarifaiInputs.retainAll(collection);
        }

        @Override
        public int size() {
            return clarifaiInputs.size();
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return clarifaiInputs.toArray();
        }

        @NonNull
        @Override
        public <T> T[] toArray(T[] ts) {
            return clarifaiInputs.toArray(ts);
        }
    };

    @Override
    protected  ArrayList<String []> doInBackground(String []...Params){

            if (Params[0] == null)
                return null;

            client.deleteAllInputs().executeSync();

            for (int i = 0; i < Params[0].length; i++){
                client.addInputs()
                        .plus(ClarifaiInput.forImage(ClarifaiImage.of(Params[0][i])))
                        .executeSync();
            }

        ClarifaiResponse containsConcept =  client.searchInputs(SearchClause.matchConcept(Concept.forName(searchString.toLowerCase()))).getPage(1).executeSync();
        ClarifaiResponse noConcepts = client.searchInputs(SearchClause.matchConcept(Concept.forName(searchString.toLowerCase()).withValue(false))).getPage(1).executeSync();

        ArrayList<String []> orderedImageUrl = new ArrayList<>();
        if (containsConcept.isSuccessful() && noConcepts.isSuccessful()){
            orderedImageUrl = getOrderedImageUrl((ArrayList) containsConcept.get(), (ArrayList) noConcepts.get());
        }else{
            orderedImageUrl.add(Params[0]);
            return orderedImageUrl;
        }
            if (containsConcept.isSuccessful()){
                Log.v("another Prediction: ", containsConcept.get() + " number");

            }else{
                Log.v("another Prediction: ", searchString + "not found");

            }
        return orderedImageUrl;
    }

    private ArrayList<String[]> getOrderedImageUrl(ArrayList containsConcepts, ArrayList noConcepts){
        int length = containsConcepts.size() + noConcepts.size();
        String[] OrderedImageUrl =  new String[length];
        String[] confidenceUrls = new String[containsConcepts.size()];
        String[] noConfidenceUrls = new String[noConcepts.size()];

        Log.v("containsConcept: ", "size" + containsConcepts.size());
        Log.v("containsConcept: ", "size" + noConcepts.size());

            for (int i = 0; i < containsConcepts.size(); i++){
                SearchHit searchHit = (SearchHit) containsConcepts.get(i);
                confidenceUrls[i] = OrderedImageUrl[i] = ((ClarifaiURLImage) searchHit.input().image()).url().toString();
                Log.v("orderedImageUrl: ", OrderedImageUrl[i]);
            }
            int containsConceptsLength =  containsConcepts.size();
            for (int j = containsConceptsLength; j < length && noConcepts.size() > 0; j++){
                SearchHit searchHit = (SearchHit) noConcepts.get(j - containsConceptsLength);
                noConfidenceUrls[j-containsConceptsLength] = OrderedImageUrl[j] = ((ClarifaiURLImage) searchHit.input().image()).url().toString();
                Log.v("unorderedImageUrl: ", OrderedImageUrl[j]);
            }
        ArrayList<String []> AllUrls = new ArrayList<>();
        AllUrls.add(confidenceUrls);
        AllUrls.add(noConfidenceUrls);

        return AllUrls;
    }


    @Override
        protected void onPostExecute(ArrayList<String []> Result){
        String[] confidenceUrls = Result.get(0);
        if (confidenceUrls != null){
            setGridViewAdapter(confidenceUrls, (GridView) ((AppCompatActivity) context).findViewById(R.id.image_gridview));
        }
        if (Result.size() > 1){
            String[] noConfidenceUrls = Result.get(1);
            if (noConfidenceUrls != null){
                //setGridViewAdapter(noConfidenceUrls, (GridView) ((ActionBarActivity) context).findViewById(R.id.image_gridview1));
            }

        }
        ProgressBar progressBar = (ProgressBar) ((AppCompatActivity) context).findViewById(R.id.explanationProgress);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setGridViewAdapter(String[] ImageUrls, GridView currGridview){
        if (ImageUrls != null){
            adapter = new ImageGridAdapter(context, ImageUrls /*ImgStringArr*/);
            GridView gridView = currGridview;
            gridView.setAdapter(adapter);

        }
    }

}
