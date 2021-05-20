# Summary of week 9

## Quentin
This week I worked on fixing cirrus and implementing the service of querying places with the google API for the destabilization week. It will be soon finished. I had to use the Geocoder from google to convert location to addresses and vice versa since the google API query that I used is working only with locations and we have addresses.

## Carlo
This week I started the sensor implementation. This implementation enables the user to use a step counter activity on the app which can be started when the user press start. The activity will count the steps the user has been doing since the start button was pressed (and also shows the number of total steps done from the last boot of the phone). Unfortunately I encountered a non quickly solvable problem: the **android emulator does not have the activity recognition sensors**. This activity has thus to be tested/verified on a real android phone which slowed down the process a lot. Once I managed to find an available android phone for testing I ran into a bug in our permission request protocol which caused our app to crash on API versions < 29. @rovati then solved this bug in a fix-PR. Next week I will keep working on the sensor usage and hopefully have it done.

## Antoine

This week I worked on the local database. First, I started to think about the archtiecture and discussed it a lot with Lorenzo. He helped me design the API. Then, I worked on the implementation which took me quite a lot of time. I didn't plan on the local database being so large and complex to write. I managed to get it to a ready for review state Thursday. Then, Lorenzo and Quentin reviewed it very well. For next week, I will work on the suggestions they left and their requests. The whole local database and online/offline synchronization (Lorenzo's part) should be done by the end of the next sprint.

In my opinion, Lorenzo and I managed to communicate very well on the local database issues to build something that would be actually usable not only by me but by the others member of the team, especially Lorenzo.

## Filippo
This week I finally finish the design refactor. Now the app have a nice and coherent design, the activity has now the same button style, margine, etc. so we can easily change the style of all button or easily change the percentage of the margin size. There are still some little minor changes to be made due to new feature implemented, but we will adapt in the coming weeks. Before merging the design refactor I also adapted the various tests that failed due to the layout change. As last week the work of change the design took me longer than expected due to all the activities that I had to change.


## Lorenzo
I kept on working on the logic for linking server and local database. I finished laying down the code, now I only need the localDB api code from Antoine and the tests on the PR, and my task will be then finished.
THere has been really good communication between Antoined and I for the structure of the lcoalDB, he put a lot of effort into writing code that could be easily usable by me. I really appreciated him taking the time to go off his ideas so that we could settle on a design that would fit the need of both.

## Émilien (scrum master)

This week I worked on multiple things. First, I finished merging the favorites feature. It is now fully tested and implemented. Then, I worked on a new feature: an activity where the user can see the ads they have posted. This has been done and now needs just a bit more of testing. Finally, I also worked on an issue with the ad creation, as they were crashes happening when trying to create an ad without any picture. This too has been fully done and now just needs to be merged.

# Overall team
Overall the team communicated quite well this week, planning ahead and not doing last minute changes. Pretty much every merge was already done the day before, and the work advanced pretty well, despite the issues with Cirrus we still had at the beginning of the week. 
