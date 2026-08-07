Written by Elina
This doc covers design decisions made when writing the add/edit artifact & collection-related code, and why they were taken.

## Add/Edit Artifacts
When adding a new artifact, there are rules for the lot number so it cannot have symbols like . or $. Since we are using the lot 
numbers as the entry names on the database, we had to make sure that nothing which would break the naming conventions (like '.' and '$' not 
be allowed) FireBase has would be broken. I made the Lot Number read only on the edit fragment so the admin would not change the entry. The reason 
I did this instead of just letting them change it is that since FireBase is using the lot number as a key for retrieval, you cannot just simply 
edit it, you would have to copy the exiting artifact details, make a new node, and give it a new lot number. This is unnecessarily complicated so 
I just made it read only.

## Collections
Since a really big factor of the catalogue screen was that it was versatile, I added onto it by having variables like ARG_SELECTION_PURPOSE,
PURPOSE_COLLECTION, and PURPOSE_UNSAVE. This way you could specify in the functions what the expected outcome was and it would show it to you. 
This eliminates the chance of code duplication, which preserves the versatility of the catalogue screen.

