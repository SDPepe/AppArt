# Summary of week 10

## Quentin
This week I finished the place service API by implementing the missing parts of the system as the geocoding service and I created modules so that we can mock the HTTP query service (used in places services) and the geocoding service (also used in places services). I also added tests for all of that. I worked with Antoine about doing an UI for the thing (we only discuss it). In addition, this week I worked with Lorenzo on finding a bug and I reviewed Fillipo and Lorenzo PRs. 

## Carlo
This week I implemented the step counter activity. 
This activity introduces **sensor usage** in AppArt by counting the number of steps done by the user from when the start button was pressed. The main issue that was faced during this procedure was **making the step counter as more accurate/flexible as possible**. For example, the step counter still gets updated when the user pauses the activity by using the phone for other scopes, or when the user finishes the activity by navigating elsewhere on the app. Also it was managed the possiblility that the STEP_DETECTOR sensor could be missing on the device. In this case the user is informed and a less accurate implementation of the steps can still be computed using only the STEP_COUNTER sensor (which is available in almost all current androids). This progress was merged.

## Émilien
This week I finished implementing the user ads feature. There was still a few issues that I had to fix, so it took me a bit of time. Then I proceeded to begin the edit/delete feature. I've began the deleting part, but the two features are a bit ambitious to do alone in one week, so I'll try to split the task in two and leave one for someone else to do. Right now I'm working on the UI part, which means creating a new kind of card that has different buttons than the normal cards.

## Filippo
This week I finished the first part of the activity filter. Now the user can filter the ads by a range of prices. Next week I will finish the activity, allowing the user to filter the ads also based on a certain range from one place. Once finished the first part of the activity and after writing the tests and merge with the master, I continued the design refactor for a while. Specifically, I removed some unnecessary buttons and adapted the layout of the two activities (UserProfile, SimpleUSerProfile) in order to have a nice and coherent design. Unfortunately as always changing the layout of the activities, and considering the usual problems with cirrus, some tests do not pass anymore so I hope to be able to fix the errors and merge the refactor next week.

## Antoine

This week I improved my local database PR by implementing the suggestions of Lorenzo and Quentin. This consisted in splitting the LocalDatabse class into several modules. I created one file IO class, one class for everything related to paths and then classes for every element that can be read or written. Also, I transformed several syncrhonous operations into asynchronous ones. When writing, the writing of images "on disk" happens asynchronously, and the whole reading part happens asynchronously too. Once this was done, the PR was merged. Then, I helped Lorenzo a little bit on his PR because I realized that the Hilt module was not correctly implemented. I did not discover this before merging because I didn't make use of it. Once I fixed the compilation issues I worked a bit on Quentin's PR.

Next week, I plan to improve the map functionality, and design the UI for the nearby places feature. If have time, I would also to implement the translation of a location into an address when creating an ad.



## Lorenzo (scrum master)
This week I've been working a lot but with few results. The task ended up being way bigger than what I was imagining, and I also had several problems with hilt and adapting tests, Antoine and Quentin were very kind to help me out on that. To try and organize myself better for next week, I decided to split the task into smaller PRs so that it is easier to check that the various parts of the new feature work and the local database is updated and queried correctly. The task required so much time that I didn't even have time to be a good scrum master for the team. I hope next week I can manage to deliver great code for the task.

## Overall team
This week there hasn't been much communication between the members of the team. The tasks were usually independent from each other so there was no need for discussions. Still, members helped each other and the stand up meetings were good.
