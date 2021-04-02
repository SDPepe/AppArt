
SOLUTION PROPOSAL TO STORE IMAGES ON FIREBASE

Pictures directory : Ads, Cards and Users

in each main directory we add a folder if we want to store images. The folder will have as name the id of the object we are storing for.

Example:
Given an Ad with id "xyz"
Given pictures p1, p2 and p3

After storing the Ad on firestore the Ads folder will contain a new folder called "xyz" containing <p1_id>.jpg, <p2_id>.jpg and <p3_id>.jpg

The same will apply for the Cards and the Users.


NAMING CONVENTION IN FIRESTORE PROPOSAL

**fields** : full words made of lower case letters only, separated by underscores.
Examples : picture_id, advertiser_id, an_important_field
Not valid : pictureId (camel case), prp (hard to understand) 

**name of pictures** : <id of the picture>.jpg
We will generate an id for each picture and will use it to name it inside the database.

NOTE : The same will apply with pictures from Cards and Users.


if this convention is accepted:
Task 1 -> re-name all the fields to match the convention
Task 2 -> re-organize the images in the database
Task 3 -> Ensure the tests are ALL working.

MOTIVATION :

- Our current design is not homogeneous => bugs can happens more easily.
- Not ready to add new users/ads/cards with this design.
- Hard to construct an API for the database without a proper first design in mind

 













