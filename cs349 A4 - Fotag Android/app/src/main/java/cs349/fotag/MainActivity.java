package cs349.fotag;

import android.os.Bundle;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.GridView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;


public class MainActivity extends AppCompatActivity {

    private ImageCollectionModel ic_model;
    private GridView gridView;
    private ViewAdapter adapter;
    private RatingBar filter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.load_default:
                Resources resources = getResources();
                if (ic_model.getImages().size() == 0) {
                    ic_model.loadDefault(resources);
                    ic_model.filterImageModel();
                    gridView.setAdapter(adapter);
                }
                break;
            case R.id.reset_filter:
                filter.setRating(0);
                ic_model.filterImageModel();
                gridView.setAdapter(adapter);
                break;
            case R.id.clear_images:
                ic_model.clearAll();
                gridView.setAdapter(adapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                gridView.setNumColumns(1);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                gridView.setNumColumns(2);
                break;
        }
    }

    private void init() {
        ic_model = new ImageCollectionModel();
        adapter = new ViewAdapter(this, ic_model);
        gridView = (GridView) findViewById(R.id.gridView);
        filter = (RatingBar) findViewById(R.id.filter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null )
            this.setSupportActionBar(toolbar);

        if (filter != null) {
            filter.setStepSize((float) 1.0);
            filter.setMax(5);
            filter.setNumStars(5);
            filter.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ic_model.setRating(rating);
                    ic_model.filterImageModel();
                    gridView.setAdapter(adapter);
                }
            });
        }
    }
}
