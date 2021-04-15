# Summary of week 5


## Antoine

This week, I finally implemented the logout functionality. Also, I improved login robustness. Indeed, the app was crashing when given empty inputs in emails or passwords, this does not happen anymore. Finally, I added more tests for the LoginService. Especially, I wanted to test what happened on wrong inputs.
Last week, I participated in the refactor but I also added all the tests for the DatabseService, using the emulator once again. This was quite more difficult than I thought and I ecountered initialization issues with Firebase.

## Carlo (scrum master)
This week I concentrated on one important issue: **synch the authentication user with the database user**. The authentication user is the login user, the database user is the entry in firestore containing all the user information. 

This has been solved partially by Emilienne and Antoine by adding the fact that **on account creation**, also a user entry in firestore with **the same document id as the UID of the authentication user** is created. 

I created entries user entries in firestore for all **already existing accounts** (next accounts will have this process automatic thanks to the above explained update, now merged). 

**My task was to synch the user profile UI with the database**, which is now done. When a user taps the account button in toolbar there is no hard-coded user anymore. The actual user information gets retrieved using the userId from firestore and any modification on the UI now actually updates the user information on firestore. 

Furthermore I did a small refactoring on getProfileImage() method of the user which now returns the **user gender icon** if the profile picture is null (never inserted by the user or deleted) and I added a SimpleUserProfileUI to enable a user on an ad to view the information of the announcer with the same UI as its personal user UI (note: the user on SimpleUserProfileUI in hard-coded at the moment). 


## Émilien


## Filippo


## Lorenzo 
After finishing my part of the refactoring task, I kept on working on the page for creating an ad. I wrote the ViewModel, plus the activity and the layout. There wasn't much new for me so the coding part was without issues. I had troubles while testing the viewmodel, I lost a lot of time because I didn't know I needed a task executioner to correctly test livedata, and in the end it was even useless work because I removed all livedata from the class.
I also added an icon to the cards UI, so that the user can know if an ad comes with a virtual tour.

## Quentin


## Overall team

