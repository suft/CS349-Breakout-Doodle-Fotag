package cs349.fotag;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by lihec on 4/3/2016.
 */

public class ImageModel {
    private ImageCollectionModel ic_model;

    private Bitmap image;
    private int rating;

    public ImageModel(ImageCollectionModel ic_model, int id, Resources resources) {
        this.ic_model = ic_model;
        this.image = BitmapFactory.decodeResource(resources, id);
        this.rating = 0;
    }

    public void setRating(float rating) {
        this.rating = (int)rating;
        ic_model.filterImageModel();
    }

    public void resetRating() {
        this.rating = 0;
        ic_model.filterImageModel();
    }

    public int getRating() {
        return rating;
    }

    public Bitmap getImage() {
        return image;
    }
}
