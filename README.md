# CabifyTest
Solution for Cabify to https://gist.github.com/lurecas/49092e67f9a65eb99653

## Description

Application that loads different trip data from a stored JSON.

For each journey, it retrieves the travel information with osrm api and update a list (https://github.com/Project-OSRM/osrm-backend/wiki/Server-api)

Then, for a specific trip, it decodes the osrm polyline and draws it on Google Maps.

The application includes:

-One spinner to select the country<br>
-One listView to show the trips for each country with the detailed information required<br>
-One Google Map fragment that shows the route geometry when one list item is selected<br>
<br>

![Screenshot](https://github.com/Ranafugaz/CabifyTest/blob/master/app/src/main/res/drawable/screenshot.png)


