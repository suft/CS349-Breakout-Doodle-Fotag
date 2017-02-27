import java.util.ArrayList;

public class ImageCollectionModel {
	ImageCollectionView ic_view;
	Toolbar toolbar;
	int rateFilter;
	ArrayList<String> filePaths = new ArrayList<String>();
	ArrayList<ImageModel> imageList = new ArrayList<ImageModel>();
	
	int mode = 0;//0-grid, 1-list
	
	public void addImageCollectionView(ImageCollectionView ic_view) {
		this.ic_view = ic_view;
	}
	
	public void addToolbar(Toolbar tb) {
		this.toolbar = tb;
	}
	
	public void setRateFilter(int n) {
		rateFilter = n;
		notifyViews();
	}
	
	public void addImage(ImageModel newModel, ImageView newView) {
		for (String filePath:filePaths) {
			if (newModel.getPath().equals(filePath)) {
				return;
			}
		}
		filePaths.add(newModel.getPath());
		imageList.add(newModel);
		ic_view.addView(newView);
		ic_view.updateView();
	}
	
	public void setMode(int n) {
		mode = n;
		notifyToolbar();
		notifyViews();
		for(ImageModel imageModel:imageList) {
			imageModel.notifyObserver();
		}
	}
	
	public int getRateFilter() {
		return rateFilter;
	}
	
	public int getMode() {
		return mode;
	}
	
	public ArrayList<ImageModel> getImageList() {
		return imageList;
	}
	
	public void notifyViews() {
		ic_view.updateView();
	}
	
	public void notifyToolbar() {
		toolbar.updateView();
	}
}
