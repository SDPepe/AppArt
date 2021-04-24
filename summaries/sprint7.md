# Summary of week 6

## Antoine
This week I worked on the map features of the app. It actually consists in two separate features. First, we want the user to be able to see all ads on a map, and click on the markers to get the relevant informations. Secondly, we want the user to be able to see the location of an ad on the ad page. For now, this is done through a see location button that launches an entirely different activity but the end goal is to have it directly on the ad page. 

I didn't realize implementing this would take me so long. I had to create a location service, which I didn't plan to add at first, and get familiar with the Google maps APIs.

Right now, the UI is not nice looking and not particularly user friendly but I think this can be improved quite easily.

## Carlo
This week I mainly worked on synching the contact info on ad with the actual firestore user which posted the ad. This is now done and **tested**. Now, once a user taps the **contact info** button on ad the announcer's information gets fetched from firestore and displayed on the SimpleUserProfileActivity. 
I also worked with Filippo on the storing and retrieving of images in database for the user profile: this task was longer and more cumbersome than expected initially because of code adaptations, thus will be continued through next week. 

In the upcoming week I will merge the PR introducing sync between contact info and firestore (which is already done) and I will keep working on the per-user storage of images in firestore.

## Émilien
This week I've been working on a new favorite feature. The goal is to allow the user to save its favorites announces and check them when they want. For now, there is multiple issues with firestore, especially because for now the users in the database seem to not be updated when a new favorite is given, even though they should be. Aside from that, I've been working on doing the serializers, as suggested in the coding review. There are still a few issues with those, but it shouldn't take too much time to fix it. 

## Filippo (scrum master)
This week I worked on several parts. First of all, I added the possibility to use the application with Launch Options that allow you to automate the login on the emulator. Secondly I finished the CameraActivity which can be used in different situations. Finally I worked with Lorenzo, Carlo to allow you to add photos to your Ad or your Profile. For the moment we have found a solution to manage the database when we store an Ad but in the coming weeks we will have to do a refactor.

## Lorenzo 
This sprint has been the worst one by far. It's been full of unexpected issues that we had a hard time fixing. On both my tasks I had to spend a lot of extra time to make things work. 
My first task was about expanding the panorama activity to support navigation through multiple panorama images. The task looks simple but I got stuck on an error coming from an external library we use. The strange thing is that the activity was still working fine, and many hours later I got told that error was already present before I started working on the activity. The second task was about modifying the adcreation activity and the firestoredatabaseservice so that the images chosen from the camera activity are uploaded to firebase storage. Also here we got stuck on several issues due to lack of knowledge on several technical aspects of dealing with files and we also had to modify the ad class structure. Overall due to time constraints we had to settle on fairly poorly written code and low testing coverage, and it's quite demotivating to know during the next sprint we will have to work again on these things to clean up everything and finalize features that weren't put on halt.

## Quentin

## Overall team
This week we worked on several components of the application. In general we continued the work with the tour overview by offering the possibility to create a parkour, synchronized the creation of the announcements with the database and offered the possibility to see a map.

