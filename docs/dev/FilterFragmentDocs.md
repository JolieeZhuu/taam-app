Written by: Andy

Overview: 
The filter fragment is designed to be a screen / fragment roughly half the size of the page. 
It will allow the user to view and modify the current FilterState main_filters. That will be 
maintained by the main_activity globally. 

* "Why do you hardcode main filters?"
- Right now the filter screen can only interact with main filters, future support can be added to
view and update an arbitrary FilterState, but for resource allocations I'd like to force it to be
one FilterState for now.

* "Why did you suppress ___ Warning?"
- Field to Variable for buttons: 
    I have suppressed warnings of field to local variable for the buttons, as they could feasibly be 
    referenced later in other functions if we wanted to expand on their functionality. 
- All warnings on some helper functions: 
    refList needs to be passed as such to be async-safe. But the compiler doesn't like that. 


* "Is it unsafe to expose a public filterset as seen in FilterState?"
I am working on the assumption that the only thing instantiating a FilterState will be the 
filterFragment. It is possible to implement key checks but because we are hard coding filter values, 
and likely only filtering by these 3 values, it makes more sense to just make sure it's called
correctly everywhere. 

* "Why are we going through the trouble of buffering / maintaining this global state?"
This is to cache the filterset so that a user can navigate in and out of screens that maintain a 
FilterState without worrying about if they're going to need to re-input filters. It is also 
potentially useful when wanting to track other expensive operations, e.g. eventual timestamping of
FilterState + artifact repository snapshot to decide if a new database query is necessary. 