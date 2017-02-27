package cs349.fotag;

import android.content.res.Resources;

import java.util.ArrayList;

public class ImageCollectionModel {
    private ArrayList<ImageModel> imageModels;
    private ArrayList<ImageModel> filteredImageModels;
    int filter;

    private Integer[] defaultImages = {
            R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e,
            R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j
    };

    public ImageCollectionModel() {
        imageModels = new ArrayList<ImageModel>();
        filteredImageModels = new ArrayList<ImageModel>();
        filter = 0;
    }

    public void loadDefault(Resources resources) {
        for (int i:defaultImages) {
            ImageModel imageModel = new ImageModel(this, i, resources);
            imageModels.add(imageModel);
        }
        filterImageModel();
    }

    public void filterImageModel() {
        filteredImageModels.clear();
        for(ImageModel imageModel:imageModels) {
            if(imageModel.getRating() >= filter) {
                filteredImageModels.add(imageModel);
            }
        }
    }

    public void setRating(float filter) {
        this.filter = (int)filter;
    }

    public ArrayList<ImageModel> getImages() {
        return imageModels;
    }

    public ArrayList<ImageModel> getFilteredImages() {
        return filteredImageModels;
    }

    public ImageModel getImageAtN(int n) {
        return imageModels.get(n);
    }

    public ImageModel getFilteredImageAtN(int n) {
        return filteredImageModels.get(n);
    }

    public void updateRating(int index, float rating) {
        imageModels.get(index).setRating(rating);
    }

    public void clearAll() {
        imageModels.clear();
        filteredImageModels.clear();
    }
}
