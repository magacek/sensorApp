Project 10: SensorApp

Description of the Project

Project 10: SensorApp is an innovative Android application that showcases the integration and utilization of various device sensors and location services. This app leverages the accelerometer sensor to control a dynamic UI element, and the ambient temperature and pressure sensors to display environmental data. It also integrates location services to fetch and display the user's current city and state. The application is designed with Jetpack Compose, providing a modern and responsive user interface.

Functionality

The application includes the following functionalities:

Sensor Ball Activity: Utilizes the accelerometer sensor to control the movement of a ball on the screen.
Temperature and Pressure Display: Shows current temperature and air pressure using the device's sensors.
Location Display: Fetches and displays the current city and state using location services and Geocoder.
Gesture Detection: Includes a gesture activity to demonstrate the detection and logging of various user gestures.
LiveData Observers: Uses LiveData to observe and display sensor and location data dynamically.
Extensions Implemented

SensorManager: For accessing and managing device sensors.
Geocoder: To convert geographical coordinates into city and state names.
Jetpack Compose: For modern, declarative UI development.
LiveData: For responsive and lifecycle-aware data handling.
Android ViewModel: To manage UI-related data in a lifecycle-conscious way.
Video Walkthrough

Here's a walkthrough of implemented user stories:

![project10%20gesture%20final](https://github.com/magacek/sensorApp/assets/70607808/4b0553fd-17e5-4394-8f20-c6612354c07b)


Notes

The development of Project 10 provided insights into the effective use of Android's SensorManager and how to integrate it with a modern UI built using Jetpack Compose. Challenges included handling sensor data in real-time and integrating location services for geolocation features.


