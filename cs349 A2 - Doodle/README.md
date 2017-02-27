CS 349 A2 - Doodle
Created By - Hecheng Li, 20428289

Welcome to Doodle, I hope everything works fine on your OS :D

How to run:
	Simply use command "make run" will start the program

Tested on OS: windows 10 and OSX.
Note: UI on both OS are different
		On windows color botton shows color
		On OSX color button shows text in color

Basic Info:
	Menu:
		File: (quick access - alt + f)
			New Doodle: (quick access - n, while file menu is open)
				creates a new doodle, prompts user to save current doodle
			Save as Binary: (quick access - b, while file menu is open)
				save current doodle as a binary file, extension .doodleBin
			Save as Text: (quick access - t, while file menu is open)
				save current doodle as a text file, extension .doodleTxt
			Load: (quick access - l, while file menu is open)
				load from a .doodleBin or .doodleTxt file
				attempt to load from other extension will cause error message to appear
			Exit: (quick access - e, while file menu is open)
				quits the program, will prompt user to save first
		View: (quick access - alt + v)
			Full-size: (quick access - u, while view menu is open)
				show doodle in actual size, can use slider if doodle out of boundary
			Fit: (quick access - i, while view menu is open)
				resize doodle to fit in canvas
	Color Palette:
		7 small color buttons, click each button to choose a basic color
		1 large color chooser, click to choose any color
		line stroke thickness slider, slide to choose thickness 1 to 5
		above slider is an estimate of what line will look like with selected color and thickness
	Play Bar:
		Play/Rewind button:
			button interchanges between play and rewind
			(ie. click play and it changes to rewind for next click, click agian to change back)
		Start button:
			change slider to beginning
		End button:
			change slider to End
		Slider:
			user can toggle slider to desired location, showing what is drawn at that point
	Canvas:
		shows doodle, canvas indicates whole drawing space, you can never draw beyond canvas
		resize canvas bigger for more drawing space

MVC:
	1 controller, 1 model, 3 views
	-controller handles all event, updates model on event
	-model contains all data, tells views to update whenever a data updates
	-paletteview controls color palette, it changes selected color, demo line when color/thickness changes
	-timelineview controls play function, it plays doodle from beginning to end or reversed and adds new ticks when model tells it to.
	-canvasview draws the doodle on screen, change between fit or fullsize depending on model
	-all views read data from model to know what to draw

Enhancement:
	-Accurate representation of time(somewhat)
		model remembers length of time each stroke is drawn
		problem: time is averagely spread between all points on a stroke
					ie. if u draw first part of the line really slow but last part very fast
						it might be shown differently
	-Ability to play animations both forward and backward

Special Notes:
	-Saving will check for existing file, if file already exists, program will prompt user for overwrite
	-Save as Binary and Save as Text automatically adds extension, if u add extension then there will be doule extension.
	-Saving upon exit or new doodle will not automatically add extension, so you must add .doodleBin or .doodleText, depending on which format you want.
		Saving upon exit or new doodle without valid extension will popup error box.

Bugs:
	-maximizing screen and unmaximizing screen messes up the layout, resizing will fix this problem.
	-on OSX play bar ticks are not drawn
	-possible bugs switching between fullsize and fit mode
	-more bugs to be discovered...
