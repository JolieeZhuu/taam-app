Overview: The catalogue screen is essentially this app's way of standardizing how we open 
recyclerview. It has two main modes, view (default) and select. Note that the "selection" does not
have intrinsic meaning, rather it is entirely context dependent. An example would be when you are
batch adding items to a collection, you would first identify the collection, then ask the user to 
select `selectionLimit` items, which you would then read as a List<Artifacts>.


"Why do you have so many interfaces?"
In order to support many different contexts for when you want to see multiple objects at once,
(which, as you can imagine is a pretty common request), we will standardize the display layer in 
the fragment. But the behaviour of the artifacts should change depending on the context.

"Why is pagination controlled by this screen, and in this screen's special preferences?"
Two reasons: 1. SharedPreferences are probably not going to be accessed anywhere else, 2. it feels
natural if the entire app agrees to only batch display artifacts with the catalogue screen, to 
have pagination as an attribute that belongs to this screen. 

"How do I choose artifacts to display?"
By default we open according to the main filterset. However, the screen is designed in such a way 
that if you update the artifactList, everything should sort itself out afterwards. You can easily
do this by calling the "populateFromList" public method, and passing a List<Artifact> type. 