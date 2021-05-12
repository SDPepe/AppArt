# Summary of week 8

## Quentin

This week I finilized my work on syncronizing the panoramas images with firebase and allows user to post ads with panoramas images and to show them in the created ad. I created a class StoragePathBuilder that allows creating path with less boiler plate code. It's really a wrapper of StringBuilder but speciailized in build path for the database. I aslo created a comparator for firebase images. When we retrieve the images from firebase we compare thier index to show them in some order. This class uses regex to check that the images name is well formed according to our conventions and it allows sorting an array containing such images.

## Carlo
This week I worked on fixing the tests regarding all the functionalities mentioned in last sprint (Uploading and retrieving user images from Firebase Storage). Various problems were encountered while testing this. Two majors problems were a bug in the user profile layout which caused tests that passed locally to fail on cirrus and simulating the activity result for the camera which was non trivial. Then various issues with cirrus also slowed down the process. At the end of the week I managed to finish the tests and merge the PR. Next week I will start working with Quentin to the sensor usage implementation. 

## Émilien
This week, I finally finished the favorite feature. I had to test the feature before merging, which I did. It took way more time than anticipated, as we had a lot of problems with Cirrus. It was really frustrating, as I feel like I worked a lot for not much, as trying to make Cirrus work wasn't really rewarding and adding much to the project. At least it is now done. 

## Filippo
This week I worked on the design refactor. I started changing the colors and fonts of the buttons by creating primary styles and secondary styles based on the importance of the buttons, plus I made sure that every button is the same size. As a second thing I changed the various layouts of the application to make sure that the margins are the same for everyone and in particulary now all that the sizes are not in a fixed number of pixels but they fit with any size of screen. lastly I had to adapt the various UI tests for the new layouts. In general I thought it would take less, but since I had to do a complete refactor it took me a lot longer than expected and in addition due to cirrus problems I have not yet been able to merge my work into the master. Next week I think I will continue the refactor design for the new activities in order to have a very user friendly application.

## Lorenzo
The issues with cirrus we had this week hugely delayed everyone's work, in particular Emilien and Antoine couldn't get their PRs merged and so I did not start working on my task since I had no code to work on. I still started reading doc that I think I will need and brainstorming what I should do once I get my hands on the code. Other than that I tried to spend as much time as possible on reiviewing PRs and commenting about other members code. I hope the issues with cirrus will be soon solved so that the workflow of the team is back to the good levels we had before.

## Antoine (scrum master)

This week I worked on two features. First, I tried to improve the map functionality. I managed to improve the UI of the card that appears when you click on a marker on a map. Also, I changed the way we retrieve locations from String since the previous way was not stable enough. Then, I started the work on the local database reading and writing. This consists mostly in handling how to store the information about an ad on disk and how to then get it back when we are offline. I plan to continue and finish this work for next week.

Unfortunately, most of my work was slowed down by the issues with cirrus.

## Overall team

This week the team communicated very well. We tried to help each other when dealing with Cirrus issues, which affected all of us. Even though this week was tough, the team managed to produce a great amount of work despite the situation. I hope we manage to fix these issues so we can continue to progress on the app.
