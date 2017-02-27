package cs349.fotag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.view.View.OnClickListener;

import java.util.ArrayList;

/**
 * Created by lihec on 4/3/2016.
 */
public class ViewAdapter extends ArrayAdapter<ImageModel> {

    private final Activity context;
    private ImageCollectionModel ic_model;
    private RatingBar ratingBar;
    private ImageButton imageButton;
    private ImageButton resetButton;

    public ViewAdapter(Activity context, ImageCollectionModel model) {
        super(context, R.layout.image, model.getFilteredImages());

        this.context = context;
        this.ic_model = model;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View imageView = inflater.inflate(R.layout.image, null, true);

        imageButton = (ImageButton) imageView.findViewById(R.id.image);
        imageButton.setImageBitmap(Bitmap.createScaledBitmap(ic_model.getFilteredImageAtN(position).getImage(), 248, 248, false));
        imageButton.setTag(position);

        ratingBar = (RatingBar) imageView.findViewById(R.id.ratingBar);
        ratingBar.setStepSize((float) 1.0);
        ratingBar.setMax(5);
        ratingBar.setNumStars(5);
        ratingBar.setRating(ic_model.getFilteredImageAtN(position).getRating());
        ratingBar.setTag(position);

        resetButton = (ImageButton) imageView.findViewById(R.id.reset);

        Bitmap image = ic_model.getFilteredImageAtN(position).getImage();

        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: popup image
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ic_model.updateRating((Integer) ratingBar.getTag(), rating);
            }
        });

        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ratingBar.setRating((float) 0);
                ic_model.getFilteredImageAtN((Integer) ratingBar.getTag()).resetRating();
                ic_model.filterImageModel();
            }
        });
        return imageView;
    };
}
