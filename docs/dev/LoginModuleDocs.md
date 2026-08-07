Written by: Yile :)
This doc covers design decisions made when writing the login/signup related classes, and why they were taken.


Login and sign up are implemented using single shared MVP (model view presenter) interface, shared between two screens rather than built as two independent features. Login and signup are pretty much structurally identical (email + password field, a main action button, a secondary "switch fragments" button, a loading state, error display), so both fragments are children of the same view class to avoid duplicating fragment wiring code. This architecture does come with some tradeoffs which will be mentioned later. 

  

##  1. LoginView/Model/Presenter

The loginView class is meant to be an abstract base fragment, handling wiring and fragment navigation for both login and sign up. The two screens are very similar, as mentioned previously, so having a parameterized abstract base is the natural choice. Note that the reset password fragment is stand alone, as it does not share many needs with login/sign up. In general, the model is responsible for database checks (account creation/credential verification), while the presenter is responsible for mapping user inputs (from view) to backend calls (from model). The model and presenter interfaces are shared across the login and sign up modules, as the needs for both are very similar. Login and sign up both need:
1. In model: a async database call
2. In model: an error to show if said call fails
3. In model: a defined action to take if said call succeeds
4. In presenter: a defined action to take for pressing both main buttons

The one spot where needs for sign up and login differ is in the model, login requires some way to verify admins, while sign up has no concept of "an admin". This is why adminCheckable is defined in a separate optional interface, instead of being nested inside of model.
  

##  2. More on shared interfaces

  
Note that login also has no concept of a username, as we only need the email and password to log a user in, so, we technically don't need the username field in login's authenticateUser.
  

```java
//MVPInterface.model
void  authenticateUser(String email,  String password,  String username, callback callback);
```
LoginPresenter and SignUpPresenter both depend on the same MVPInterface.model type, so the shared View base can hold a single MVPInterface.presenter field and never needs to know whether it's being controlled a login or signup presenter.
 LoginModel's authenticateUser accepts a username purely so the method signature matches the "broader" SignUpModel.authenticateUser, which does use it. All so that loginView can support both fragments.

Because of this, we need a slight workaround, an empty string placeholder for username gets used twice in login, once in LoginPresenter's call to authenticatUser, and once in loginView's clickListener.
 
  

##  3. instanceof AdminCheckable check in LoginPresenter

  

```java
//in loginPresenter
if  (m instanceof MVPInterface.AdminCheckable)  {

((MVPInterface.AdminCheckable) m).checkAdmin(user, isAdmin ->  {

if  (isAdmin) v.navigateToAdmin();

else v.navigateToHome();

});

}  else  {

v.navigateToHome();

}

```

  

This looks like useless code, since in production LoginPresenter is only ever constructed with a LoginModel, which always implements AdminCheckable...

The check was from earlier in development, LoginModel didn't implement AdminCheckable yet, and this conditional let the login flow stay demoable (everyone routes to the regular home screen) before admin checking was built. It was kept rather than removed because:

1. It costs nothing at runtime.

2. It keeps future options open, will work without AdminCheckable too.

LoginPresenter depends only on MVPInterface.model, and optionally upgrades its behavior if the model also happens to implement AdminCheckable. If for some reason, we no longer want admins in the future, or want some different design paradigm, the code will still be usable/demoable so long as model is there.
  

##  4. Input Validation in Two Places?

  

Account validation during account creation happens in two places, for two different reasons:

 **In NewUserFragment "inside" the fragment:** only checks that the password and confirm password fields match. This check must live in the fragment because it needs access to a second EditText (confirmPassword) that doesn't exist anywhere in the shared MVPInterface contract, the shared LoginView only knows about one password field, as we don't need a second password field for login's fargment.

  

**In SignUpModel.authenticateUser:** Every other check related to the validity of an account happens here, ideally the matching password check would happen here too, but that would not be possible without a workaround, which would be worse than simply checking matching passwords in the fragment. 
 
In other words, if it could be avoided, no logic checking would take place in the fragment code.


**Small note:** Technically, the signupPresenter empty check for username is redundant because of the regex check, but it isn't worth fixing because its so tiny.


##  5. Username uniqueness: case insensitive, and why user class needs a second field

  It was decided that usernames should preserve their original capitalization in the comments, while username uniqueness should be case insensitive. For instance, if a user, "personA" commented, their displayed name would be "personA", but if someone were to attempt to register under the username "persona", it would be denied. This is why User stores two versions of the username. 
 
```java

private  String  username;  // for display

private  String  usernameLower;  // for uniqueness lookup
```
Note that for this check to work, we need read access to usernameLower for everybody, not just logged in users, however, this may not be the best security practice...
  
##  6. Extra Notes

  

Two constructors are intentionally not unit tested:

  

```java

public  LoginModel()  {  ... FirebaseDatabase.getInstance(...)  ...  }

public  LoginPresenter(MVPInterface.view v)  {  this.m =  new  LoginModel();  ...  }

```

Both require a real Firebase instance, which casues Junit and Mockito to freak out. So they were not tested. In tests, we use a testing specific constructor. Also, view is not testable using just Mockito and Junit.
