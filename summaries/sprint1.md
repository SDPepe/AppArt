# Summary of week 1

## Antoine (scrum master)
This week I worked on the login backend and the login logic. The login backend has been ready for quite a while but was not mergeable due to the total test coverage being too low for the whole project. I also worked with Emilien and Carlo on implementing the login logic for the UI and a create account panel. This also works fine but is not part of any branch for now because some things need to be perfected like the popup window we want to appear on login failure and the handling of several other types of failures. We also need to write some tests for this part. I also spent a lot of time discussing the architecture for the login backend (the discussion is in the PR), and reviewing several of my teammates pull requests. I under estimated the work to be done by quite a margin. This was due in part, by not taking into account the need for tests. However, most of the time was spent re-implementing the backend several time until a suitable structure / architecture was found.

Next week I plan on merge the login backend and login logic for the UI into the master branch. Also, I will work on the Ad ViewModel.

## Carlo
My job for this week was to implement the General User Interface, the AppUser class and the UniversityEmailDatabase (see issue #20). The part that clearly took the most of the time was implementing the AppUser class. The first task was to understand and list what this class should offer in terms of attributes and methods since it should not overlap with another class which manages user's interactions with Firebase. I worked with Antoine (in charge of FirebaseLoginService) in order to correctly conceptualise the different role of our tasks. I then coded the AppUser class and User interface. The UniversityEmailDatabase simply informs us if a given email provider is a university, useful fot the AppUser class. I then created tests for all my committed code. Since my task was fairly simpler then others I managed to finish it in the expected timing. Therefore I then slightly helped Emilien and Antoine on impementing the login logic UI. 

Next week I plan on better exploring the UI parts of the project. I will implement a user profile activity which enables the user to edit its personal information. This implementation should interact with the FirebaseService to update the user's information in database.

## Émilien

## Filippo

## Lorenzo
I was in charge of setting up Firebase as back-end of of app. I then worked with Filippo on the implementation of the models and logic related to the ScrollingActivity. This inlcuded the database classes for the communication with Firestore. We then worked with the help of Quentin on the implementation of the scrolling ViewModel.
The time estimate for the task was slightly optimistic but not too far off. On my side I spent a bit too much time going over PRs. Quite some time was also spent on setting up dependencies for the project. The PR merge ended up being late because of difficulties finding a way to unit test the database.
Starting week two I will try spending my time more efficiently on PRs and maximizing individual work.

## Quentin

## Overall team

This week was really preparation and architecture focused. We didn't really know how we wanted our class to look like and how to design them. Moreover, the meetings were too long and not as productive as we wanted them to be.

We still managed to produce useful work in a reasonable quantity. Now that we are more organized I think the meetings will be more productive and shorter. The more we progress on building the foundations that are required for every feature to work the more we can separate our work and actually implement these features.
The login system will soon be complete and the Database system is. We also have UIs for several elements, such as the scrollig activity with all the ads, the login activity and the reset password activity. 
