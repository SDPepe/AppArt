# Summary of week 11

## Quentin
This week I worked on the UI with Antoine. We implemented a great looking UI with cards and a spinner widget to allow the user to select which type of place is located near the address of the ad he's looking for. I had to create various components to make it work but this was not an easy week as I had a lot of job to do and the tests are not finished.

## Carlo (scrum master)
This week I mainly worked on creating a mock environment to test the StepCounterActivity. I encoutered various difficulties doing this: first I implemented a mock SensorManager and mock Sensor class to simulate the STEP_DETECTOR and STEP_COUNTER sensor. Because of an issue with mockito simulating hardware sensor classes other approaches had to be considered. In the end I implemented the testing by passing a parameter which informs the StepCounterActivity to mock the **onSensorChanged(...)** function. Furthermore I fixed a bug in the activity which was caused by the use of static fields and had do modify the UI in order to match the new behaviour.

## Émilien
This week I worked on implementing the deleting of ads. The main challenge was implementing it in the database, as for now there wasn't any way of deleting it from Firestore. To be honest, of one the most difficult points was to understand how the database was working, as to find what to delete and where. I initially tried to delete way too many things before understanding that it's a bit easier than I thought, as all documents are together. I'm now working on testing the feature, and after that I'll be done with it. 

## Filippo
This week I work on several different task. First I finish the filter activity. Now the user can filter the ads by price and by location range or reset simply all filter. Secondly, I implemented the redirection on a phone call or on the email when the user want contact the advertiser. I choose to redirect automatically on the mail (that is mandatory for use the app) if the advertiser don’t have a phone number. If the advertiser have the two (mail and phone number) an alert box is show and the user can decide which of two contact redirection choose. As a last thing I continued the work on the design refactor making small change, in particular I update the standard margin in some activity for let adapt activity with the size of the screen phone, and change some font button. 

## Antoine

This week I worked on the destabilization feature and on the map. First, I helped Quentin a bit on implementing features for the place service. More specifically, we now query places based on their distance from the user and not specifically in a specific radius. This way we always get the smae number of results for each type of place.  We also built the UI together.
I improved the way apartments are displayed in the main map view. Now, the app zooms in on the user postition and only displays the apartments that are 50 kms or less from the current user position.

## Lorenzo
This week I finally finished my task on the syncrhonization between server and local database. Now the user can login automatically if they didn't log out during the previous session, and also the offline login is working. When opening the favorite activity the ads are saved locally and can be accessed offline too (with images and info about the advertiser). Despite being very tight on time this week I had the feeling of spending my work time well and in a very productive way. The meetings were good and quick.

## Overall team
This week the team mainly worked on the last features of AppArt. The local database feature was updated and implemented in various activities. Major progress on the destabilization sprint which is now finished and just has to be merged. Some test coverage was added and also the implementation of last features as forwarding to email/call app on phone to contact an announcer, filter ads by location and personal ads management view/delete. Outside of the regular meetings communication was constant throught the week but limited to sub-groups which worked on similar fronts.
