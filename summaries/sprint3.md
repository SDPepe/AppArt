# Summary of week 3

## Antoine

This week I worked on the backend required for the AnnounceActivity ViewModel. This consisted in fetching the adId from the cardId passed to the activity and then retrieving the information for the corresponding ad. I also needed to implement the log out back end when the toolbar was finished but this it isn't merged yet. Next week, I plan on refactoring some classes, adding tests and improve the login UI.
## Carlo
This week I finished and merged the user profile activity. A large chunk of my time was utilised to re-create the user profile layout since the previous implementation did not adapt to all screen sizes and elements in the layout popped out of the view once the emulatore changed. The activity is now implemented and a user can edit its personal information on the app. Next week I will add interaction with the FirebaseDB class which updates information inside Firestore database so that editing is persistent. Furthermore I will create a SimpleUserProfileActivity: this activity will be used on the ads to enable the user to see all public information on the announcers profile.


## Émilien
This week I finished the adding of the toolbar. It was really tedious, as there was various bugs that I had to find and delete. This took most of my time, as new errors kept emerging when I fixed the previous ones. I also refactored some of the code, to implement the idea that an activity has a toolbar. Thus, all activities fitting that description will have the same behaviour, thus removing some duplicate code.


## Filippo
This week I worked on the CameraActivity. In practice I have implemented an activity that allows the use of the camera for take picture and allows the access at the gallery to take the image. Before the user can access to the camera/gallery permission is asked. Communication with the database must be implemented in the coming weeks.


## Lorenzo (scrum master)
This week I worked on a bit of code maintainability and coverage, and on the logic of the ad creation. The ad creation viewmodel took less time to implement than expected, but it might take some time to adapt it once the activity and layout are up. On the other hand, the database extension had been quite slow, mainly because I had to get used to the conventions already used in the class and had to learn a few things more about firestore.
I still have to improve a lot on the time management, I often lose time while coding because I don't think enough beforehand about how to implement things.

## Quentin


## Overall team
This week we had a couple of issues on big tasks coming from last week, so the overall work on the tasks of this week wasn't good enough. The communication between us improved quite a lot and the work on PRs is also improving. Starting next week we will try to limit the number of new tasks we take on, so that we can finish more easily what we have left from this week.
