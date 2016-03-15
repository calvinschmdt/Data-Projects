Author: Calvin Schmidt

A program that allows for visualization for a list of zip codes. Currently set
up to work in the continental US, though values can be plotted for all the US.
This work utilizes the coordinate plotting mechanism introduced in Stanford's
CS106A SeeTheUS program during its Fun with Files lecture. It relies on the
graphics package included in Stanford's Eclipse version, though I do not know
if that is included in every java installation. 

The map created does not match perfectly with the US map, due to difficulty 
representing spherical coordinates on a 2D surface. I do not yet know how to 
save the map that is created.

File containing zip code coordinatess should have the zip codes in the first 
column, latitude values in the second and longitude values in the third, with 
each separated by a variable.

File containing test zip codes should have a each zip code on a new line, with 
no spaces after the zip code. It can be separated from other values with a 
comma, but those values will not be read.

