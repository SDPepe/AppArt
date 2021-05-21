# Summary of week 10

## Quentin

## Carlo
This week I implemented the step counter activity. 
This activity introduces **sensor usage** in AppArt by counting the number of steps done by the user from when the start button was pressed. The main issue that was faced during this procedure was **making the step counter as more accurate/flexible as possible**. For example, the step counter still gets updated when the user pauses the activity by using the phone for other scopes, or when the user finishes the activity by navigating elsewhere on the app. Also it was managed the possiblility that the STEP_DETECTOR sensor could be missing on the device. In this case the user is informed and a less accurate implementation of the steps can still be computed using only the STEP_COUNTER sensor (which is available in almost all current androids). This progress was merged.

## Émilien

## Filippo

## Antoine



## Lorenzo (scrum master)
This week I've been working a lot but with few results. The task ended up being way bigger than what I was imagining, and I also had several problems with hilt and adapting tests, Antoine and Quentin were very kind to help me out on that. To try and organize myself better for next week, I decided to split the task into smaller PRs so that it is easier to check that the various parts of the new feature work and the local database is updated and queried correctly. The task required so much time that I didn't even have time to be a good scrum master for the team. I hope next week I can manage to deliver great code for the task.

## Overall team
This week there hasn't been much communication between the members of the team. The tasks were usually independent from each other so there was no need for discussions. Still, members helped each other and the stand up meetings were good.
