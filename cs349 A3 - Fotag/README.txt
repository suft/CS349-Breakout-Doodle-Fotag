CS 349 A3 - Fotag
Created By - Hecheng Li, 20428289

Welcome to Fotag, I hope everything works fine on your OS :D

How to run: Simply use command "make run" in console will start the program

Platform: windows 10
Java version: 1.8

Basic info:
	Toolbar:
		-On the left of the toolbar is grid/list view, grid view is selected by default
		-On the right is filter, by selecting stars the program will only show pictures with equal or more stars.
		-On the right of filter is reset button, clicking will reset the filter to 0 stars selected (show all pictures)
		-On the left of filter is open image, clicking it will load pictures to the program
	Canvas:
		-Each image is shown with a thumbnail, name of file, last modified date, and rating
		-You may rate a picture from 1 to 5 stars, right clicking any star will reset the filter
		-Clicking thumbnail will open a 800x600 window with the picture filled inside.
	Minimum window size is 320x400
	Default window size is 800x600
	No maximum window size

MVC:
	Controller:
		- 1 controller, mainly incharge of toolbar buttons(except reset button)
	View:
		- 2 views
		- ImageView, displays one image
		- All ImageView are collected in ImageCollectionView for management and updating
		- Toolbar, displays toolbar
	Model:
		- ImageModel, each ImageView is connected to one ImageModel
		- All ImageModel are collected in ImageCollectionModel for management and updating
	How clicking thumbnail works:
		1. Controller gets event from individual ImageView
		2. Controller goes to ImageCollectionModel to get list of ImageModel
		3. Controller checks for each ImageModel for the thumbnail clicked(by checking image name)
		4. If controller finds ImageModel with same image name, it opens a new window with the image inside