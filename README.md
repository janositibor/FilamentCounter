# Filaments Counter

## Purpose
This FIJI Plugin expect input files in an arbitrary folder structure.  
The program will process all the .tif files and create the result.csv files for each folder recursively.  
The input file should contain a ROI (see the example below).  
![Input *.tif image example](./ImagesForDocumentation/Input.jpg)  
This plugin:
* Create linear ROIS inside the defined area. 
* It measures the intesity via these lines. 
* It calculate filaments density (1/mico m) by counting the peaks on these plot profiles.

## Install
* Clone the Maven project in this repository to your local drive.
* Build your *.jar files
* Copy the FilamentCounter-*.*.jar fil to the plugins folder of FIJI (for example: Program Files/FIJI/plugins)
