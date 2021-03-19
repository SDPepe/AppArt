# Summary of week 2

## Antoine

This week I worked on implementing the login logic behind the login UI. It was quite involved as I had to rewrite part of the LoginService interface and add a few things to the UI. I learned to test with espresso. Lorenzo worked on the AdViewModel due to the fact the UI was taking me too much time. In total I spend a lot more than 8 hours the login implementation and when I decided to work on it I didn't expect that. I think hat this week brought me a lot of experience concerning work estimations. I did not manage to finish the ViewModel but I will continue to work on it this week.

## Carlo


## Émilien (scrum master)


## Filippo


## Lorenzo
This week I worked on the UI of the AdActivity. While it was fun to work on UI, it's been very time consuming because of several reasons: I had to learn my way around android views, and my work was stalled by the viewmodel so I had to spend time harcoding stuff in order to test the UI. I also helped Antoine work on the logic for the AdActivity.
For the reasons above I ended up spending much more time than the estimate on this sprint, but without those issues I think the estimate wasn't too off.
Thinking back on it, it would have been better to directly help Antoine on the viewmodel right away instead of spending time hardcoding stuff in order to test the UI.

## Quentin

I worked on dependency injection this week. I decided to go with hilt and setup the last version. I had to read all the documentation on google developper since the sdp tutorial is about an anciant 
version and is no longer the same.
Using hilt enabled us to mock the database and therefore to solve the espresso tests not passing last week due to a timing issue. It also makes more sense in the code as now only actitvities that need an injected class are consious about it. 
I also modified the interface of database to use futur. It made it more concise and readable both in the client code and in the database implementation itself. 
A key missing feature is a mock for the login. We are currently using the Emulator or the cloud service but I fill like a mock could allow us to test the ui without worrying about timings. I suggested that we get the three implementations : MockLoginService, CloudLoginService. The cloud one could transparantly use the emulator or not. I think its better because in my opinion the emulator is a system as a whole and should not interfer in our logic tests.


## Overall team

