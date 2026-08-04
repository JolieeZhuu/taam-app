Written by: Jolie

Please refer to DatabaseStructure.md for the structure of our database. Note that I will refer to every "table" as an entity.

## Users/Admins
User credentials are stored in Firebase authentication, while all non-sensitive data gets passed into the Firebase Realtime database. I decided that we should have a separate entity for admins, because marking users as admins within a field in the same entity seemed highly insecure. Admin entity merely stores the users who are admins. I read in the documentation that hashmaps (or key-value pairs) were a lot more efficient to handle than a list of values, which is why it's structured as userId: true.

## Artifacts/ExpandedView
The structure of artifacts is pretty self-explanatory, as we were given the fields. The important thing to note is that our keys are the lotNumbers themselves. This poses 2 issues that we have taken care of:

1. There are constraints on what characters can show up in an ID. We added these constraints when an admin adds a new artifact.
2. I'm pretty sure there will be an error thrown if the lotNumbers are ONLY numerical. This is because Firebase automatically sees sequential numbered keys as an arraylist, and will want to convert my data into an arraylist (although I expect a map). This will cause a mismatch in types :C

Now, why exactly do I have a separate entity for ExpandedView? Because we have to track like numbers, as well as the users who liked those artifacts. I thought that having like numbers in the same entity would make things a lot slower, because I would constantly be fetching and updating the same entity over and over again if users kept clicking the like button. Same reason why Comments entity isn't attached to ExpandedView.

## Collections/ArtifactCollections
Again, structure for collections is self-explanatory. I ordered it by userId instead of collectionId for the convenience of querying, and because we assume that every user only has ONE collection.

But why is there an artifactCollections? Well, what happens when an entire artifact gets deleted? There's no easy way to order collections by artifacts in the Collection entity, then remove the artifact from every collection that used to have it. That's why ArtifactCollections exist.

## Java Entities/Repositories
Through Android Studio documentation, the best way I've seen to handle data is to make separate files for the entities and data functions (aka repositories). This was my main job for this project too! I was able to simplify the programming for the other members so they didn't have to worry about how to handle the data fetching. All they had to do was call the function and catch any errors.

In terms of testing, I could have used Mockito but I ended up just using Integration+Unit testing, seen in the androidTest testing folder. This worked with the actual database so I could see where my logic issues were occurring, and if the database was actually running properly.
