# Filaments Counter

## Purpose
This FIJI Plugin expect input files in an arbitrary folder structure.  
The program will process all the .tif files and create the result.csv files for each folder recursively.  
The input file should contain a ROI (see the example below).  
![Input *.tif image example](./ImagesForDocumentation/Input.jpg)  
This plugin:
* Create linear ROIS inside the defined area. 
* It measures the intesity along these lines. 
* It calculate filaments density (1/mico m) by performing [BAR Find Peaks](https://imagej.net/plugins/find-peaks)'s peaks identification algorithm on these plot profiles.

## Install
* Clone the Maven project in this repository to your local drive.
* Build your *.jar files
* Copy the FilamentCounter-*.*.jar fil to the plugins folder of FIJI (for example: Program Files/FIJI/plugins)

## Use
* Run FIJI
* Choose the Plugin>Filament Counter option
![Choose Filament Counter Option](./ImagesForDocumentation/ChooseMenuOption.jpg)
* Provide the requred data for the peak identification
    * Pixel size in micro m  
	* Amplitude (Based on [BAR Find Peaks documentation](https://imagej.net/plugins/find-peaks) it is the smallest depth (in Y-axis units) that a qualified valley must exceed. By entering 0, it is set to one standard deviation of the data.)
    * Min peak height (The smallest value (in Y-axis units) a qualified peak must exceed.)
    * Minimal distance between peaks (The smallest separation (in micro m) between identified peaks.)  
![Choose Filament Counter Option](./ImagesForDocumentation/InputParameters.jpg) 
* Select a folder which contains the files need to be analyzed  
![Choose Folder](./ImagesForDocumentation/SelectFolder.jpg) 


