Lunch Menu App
=======================
**Platform:** Android                                                                   
**Language:** Java                                                               
**Environment:** Eclipse , Android SDK


This android app is an agenda calendar that fetches information about the Lunch Menu for a Local Elementary School.
It is a regular calendar consisting school's daily lunch menu updated on a regular basis. User preferences in the app are also set to notify the user when the food item of his/her own choice is updated in the menu for a particular day.
The updation of Menu items is taken care by the school and this app pulls the content from the web in the form of Json and parses it into readable format.

![](http://i.imgur.com/CuoqWHm.jpg)

The app consists of the Main Activity and a service thread running in the background.

Main Activity consists of the following:
========================================
1. Calendar View
2. Selected day's Menu
3. List of all Menu items for all the days with dates
4. User Preferences to set favorites.
5. Manual Update option
6. General Information about the app
7. Information about the developer
8. Deserialize object files and save them to HashMap
9. OnClick function to return Menu values based on the selected date
10. Dialog boxes for all the Menu Options.

Service Class consists of the following:
========================================
1. Function to check for network connection and fetch Json content from google calendar.
2. Function to parse Json content and store it in a file locally.
3. Store the content in a hashmap with key value pairs of date and menus.
4. Serialize the hashmap to be used by the Main Activity.
5. Function to check today's date and compare with the user preferences
6. Push Notifications based on the user prefrences.

Design
======
Layout are customized using the XML files which store the properties of the designs.


Lunch Menu app start screen
A Smiley face is shown for the favourite food                                                                   
![](http://i.imgur.com/sgRqWhc.png)

Options Menu                                                                                        
![](http://i.imgur.com/4muWpkl.png)

User preferences are set from the options                                                         
![](http://i.imgur.com/yJ1JvHI.png)

The smiley disappears since veggies are not in the user preferences                                         
![](http://i.imgur.com/OO2TluQ.png) 

The user can choose to update the app manually from the options                                       
![](http://i.imgur.com/XTCLAxA.png)

General Information about the app                                                                     
![](http://i.imgur.com/p7mWUc3.png)

Info about Me                                                                                           
![](http://i.imgur.com/WnJINzn.png)

Complete Menu List                                                                                  
![](http://i.imgur.com/Mj6idRt.png)

Push Notifications on user's favourites                                                                   
![](http://i.imgur.com/rxn2sGz.png)

The app was successfully completed in a month                                                             
![](http://i.imgur.com/mT28LIT.png)
