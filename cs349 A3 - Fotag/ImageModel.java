import java.io.File;
import java.text.SimpleDateFormat;

// View interface
interface IView {
	public void updateView();
}

public class ImageModel {
	// all views of this model
	private IView view;
	String imagePath;
	int rating = 0;
	String fileName;
	String date;
	ImageCollectionModel ic_model;
	
	ImageModel(String path, ImageCollectionModel ic_model) {
		this.imagePath = path;
		File file = new File(path);
		fileName = file.getName();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		date = sdf.format(file.lastModified());
		this.ic_model = ic_model;
	}
	
	// set the view observer
	public void addView(IView view) {
		this.view = view;
		view.updateView();
	}
	
	public void setRating(int n) {
		rating = n;
	}
	
	public String getPath() {
		return imagePath;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getDate() {
		return date;
	}
	
	public int getRating() {
		return rating;
	}
	
	// notify the IView observer
	public void notifyObserver() {
		view.updateView();
		ic_model.notifyViews();
	}
}
