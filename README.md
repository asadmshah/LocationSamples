# Using Android Location Services

A simple application showing three different uses of the Google Play Services Location API.

* Ongoing Location Detection: Grabs your current location every couple of seconds to display your coordinates, city, and country.
* Activity Detection: Displays a list of possible activities ranging from driving, biking, running, walking, or standing still.
* Geofencing: Displays a map which you can use to select a location and radius of your geofence. Entering and exiting the geofence will trigger notifications describing the event.

## Prerequisites
* Location must be enabled from Settings
* Google Maps API key should defined un
* Android API Level >= v16
* Google Support Repository

## Getting Started
This sample uses the Gradle build system. To build this project, use the `gradlew build` command or import the project into Android Studio. The Geofence Activity will not run properly unless you have a valid Google Maps API key assigned to `LocationSamplesMapsAPIKey` in `~/.gradle/gradle.properties`.