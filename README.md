# Who's There
At its core, this app allows users to connect with friends and see how far away these friends are. In addition, a notification will be sent to friends who are within a certain distance of each other. This distance can be changed in a settings menu. Along with the basic functionality, our group has a many ideas that we could implement to augment the user experience and depth of the app if time permits. Some of these ideas include sms integration so that users can quickly send a text to a nearby friend, an incognito mode in which the user would be hidden from their friends’ maps, homescreen widgets to locate a specific friend and create groups to manage friends.
Bigger ideas and functions include a feature to schedule meet up events for friends. You and your friends could all agree on a location and future date/time when all of you want to meet up and the map will keep track of the location and the time. An alarm go off N minutes before the scheduled meet up, where N depends on how far you are away from the location so that you can make it there on time. Also, a single friend can put an event on the map that indicates where they are planning to be on a specific date/time so that all of his/her friends would know where to expect them to be and potentially meet up with them there.

## Built With
* Firebase: Manage and store all information, from usernames and passwords to profile pictures and current location. Users would not be able to interact directly with the database, but can modify their own information via the “edit contact information” activity.
* Google maps api: The main activity that allows users to interact this. For instance, users can click on points see information, zoom in or zoom out.
* Permissions  (location, contacts, sms, storage, network connecting): These are the potential permissions that we will need to use in our app.
* Notifications: Users will receive a notification when a friend is nearby/within a specified radius. 
* Shared Preferences: Ensures that users will not have to login every time they open the app. Likewise, certain settings such as visibility will be retained even when the app is closed.


## Contributing
* Faho Shubladze	fshublad@terpmail.umd.edu 
* Hammad Shah	hshah14@terpmail.umd.edu	
* Inigo Jiron		ijiron@terpmail.umd.edu	
* John Nguyen		johnnyn1261@gmail.com
* Arthur Shao		eleven032@gmail.com
