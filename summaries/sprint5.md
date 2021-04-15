# Summary of week 5


## Antoine


## Carlo (scrum master)
This week I concentrated on one important issue: **synch the authentication user with the database user**. The authentication user is the login user the database user is the entry in firestore containing all the user information. 

This has been solved partially by Emilienne and Antoine by adding the fact that on account creation, also a user entry in firestore with the same document id as the UID of the authentication user is created. 

I created entries user entries in firestore for all already existing accounts (next accounts will have this process automatic thanks to the above explained update, now merged). 

**My task was to synch the user profile UI with the database**, which is now done. When a user taps the account button in toolbar there is no hard-coded user anymore. The actual user information gets retrieved using the userId from firestore and any modification on the UI now actually updates the user information on firestore. 

Furthermore I did a small refactoring on getProfileImage() method of the user which now returns the user icon if the profile picture is null (never inserted by the user or deleted) and I added a SimpleUserProfileUI to enable a user on an ad to view the information of the announcer with the same UI as its personal user UI (the user on SimpleUserProfileUI in hard-coded at the moment). 


## Émilien


## Filippo


## Lorenzo 


## Quentin


## Overall team

