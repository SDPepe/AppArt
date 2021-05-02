# Summary of week 7

## Antoine

This week I worked on the map testing. This consists in testing the MapActivity when used to display all available apartments and in testing the MapaActivity when it is used to display the location of a specific apartment. I started by trying to test the location service with no success. Everything worked on my local machine but it didn't work on cirrus. I then moved to map testing. I had to change the emulator used in cirrus because it is necessary to have the google_apis for the map to work. I had lots of issues while trying to test this feature and it wasn't merged for this week.

The tests now works and will soon be merged.

## Carlo
This week I worked on synching **Firebase Storage** with the **UserProfileActivity** in order to enable the user to upload, remove, update its profile picture. This needed work on the Activity, on the ViewModel and **new functions on the FirestoreDatabaseService**. These changes were introduces in ImageHelper for FirestoreDatabase. 

I also worked on refactoring hardcoded strings. In our codebase, activities communciate through **putExtra** and all the strings used until now were hardcoded. I wrote a ActivityCommunicationLayout file where all the communication messages are stored and used globally through the code. I then refactored all the existing putExtra calls to fit the new layout. Overall this has been a very productive week and a lot was implemented/solved.

## Émilien

## Filippo
This week I worked on the search bar on the top of the home page. So, I made minor change to the **ScrollingActivity** for display the search bar and I added a new function to the **FirestoreDatabaseService** for retrive only the card form a specific location. These feature with even the associated tests took me the estimated time to implement. Secondly, I helped Carlo a little in managing the profile image and the comuncation with the **Firebase Storage**. 

## Lorenzo
This week I worked on refactoring the database, creating helper classes to down/up-load documents and adding constants to easily create paths for objects in the database. The work has been smooth, the only issues have been about merging other PRs and adapting the code a lot because there have been multiple PRs opened in parallel and that worked on the same files.
Other than that I made minor UI changes and added a functionality to open ad image fullscreen, so that we can see the whole photo instead of only the square-cropped version. These features weren't hard to implement, they just required a bit of doc reading to learn about gesture detectors and such.
Overall it's been a good week and I feel like there's not much to improve about it, only the communication with other members to better organize the flow between different PRs.

## Quentin (scrum master)

## Overall team

