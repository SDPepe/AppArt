# AppArt

A marketplace for university students looking for and renting apartments.

The aim of this application is to create a trusted and self-organised market place where university students may search for apartment rentals in Switzerland. It frequently happens in a student’s life that a substitute candidate is required by the leaseholder once the rental contract terminates or that rooms or studios remain empty for a large and contiguous part of the year. This self-organised market place would be a platform for students to broadcast their offers to find or rent their rooms or apartments.
The main feature which distinguishes this idea from a basic apartment market place is that it’s narrowed to university-related users: this both provides a friendly and trusted place for users to feel comfortable with their experience of the app.

How to use :

This app doesn't need any particular installation to run. However, if you want to be able to run the LoginTest and the DatabaseTest java classes, you will need the firebase emulators.

You can go to this link to install the emulator : https://firebase.google.com/docs/cli

After that you will need to log in, like explained in the aforementioned web page, with the credentials for the project.

Once you're logged in, you can run : firebase emulators:start from the root folder (the root of the git folder, with app, summaries, gradle/wrapper, etc.). Once this is done you can run the tests. The database test only works with a clean database. Therefore, after each run of the DatabaseTest class, you will need to go to the emulator UI (the domain and port are indicated in your terminal), go to the firestore tab and clean everything.

To run the app you will need to put the Google API key in the local.properties file, like this :

MAPS_API_KEY=${API_KEY} (without the $ and the braces, you need to put the raw key)

Finally, our "free trial" of the google apis end the 20th of July, 2021. After this, we don't know what features are disabled and how the app will behave.


## Badges
[![Build Status](https://api.cirrus-ci.com/github/SDPepe/AppArt.svg)](https://cirrus-ci.com/github/SDPepe/AppArt)
[![Maintainability](https://api.codeclimate.com/v1/badges/ad483ece588a128e99e3/maintainability)](https://codeclimate.com/github/SDPepe/AppArt/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/ad483ece588a128e99e3/test_coverage)](https://codeclimate.com/github/SDPepe/AppArt/test_coverage)



## Code Conventions

For all the view/layout objects we use: 

    - the name of the object
    - the name of the activity to which it belongs
    - the object type  
separated by underscore

## Resources ids example

< name > _ < activityName > _ < type > 

signIn_LoginActivity_button







