# Summary of week 7

## Antoine

This week I worked on the map testing. This consists in testing the MapActivity when used to display all available apartments and in testing the MapaActivity when it is used to display the location of a specific apartment. I started by trying to test the location service with no success. Everything worked on my local machine but it didn't work on cirrus. I then moved to map testing. I had to change the emulator used in cirrus because it is necessary to have the google_apis for the map to work. I had lots of issues while trying to test this feature and it wasn't merged for this week.

The tests now works and will soon be merged.

## Carlo

## Émilien

## Filippo

## Lorenzo
This week I worked on refactoring the database, creating helper classes to down/up-load documents and adding constants to easily create paths for objects in the database. The work has been smooth, the only issues have been about merging other PRs and adapting the code a lot because there have been multiple PRs opened in parallel and that worked on the same files.
Other than that I made minor UI changes and added a functionality to open ad image fullscreen, so that we can see the whole photo instead of only the square-cropped version. These features weren't hard to implement, they just required a bit of doc reading to learn about gesture detectors and such.
Overall it's been a good week and I feel like there's not much to improve about it, only the communication with other members to better organize the flow between different PRs.

## Quentin (scrum master)

## Overall team

