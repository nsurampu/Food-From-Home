# Food From Home
An android app built as part of the Object Oriented Programming course at BITS Pilani

**Author:** Naren Surampudi

The current project implements and Android application with the title “Food From Home”. This application is a concept, with the health of the users in mind and enabling the users to order healthy home-made food from the comfort of their residence instead of food from the restaurants and other food outlets. The application can not only be used for ordering food, but can also be used for making and uploading food as well as delivering the meals. This introduces the concept of a “variable fleet” in addition to the “set fleet” of the application, thus giving more options in terms of logistics.

### Build Specifications

The application has been built in Android Studio using API 25 as the development base. The application was mainly tested on the Pixel 2 emulator and uses Java as its main language.

### Project Structure

- **History**- The History class defines the History object. This class is used for - making objects for keeping track of all the meals ordered by any user and accordingly store it in a database
- **MainActivity**- The MainActivity class is the entry point of the application. It describes the main activity of the application and houses the login functionality of the application. Here the user can choose to login using the credentials he used while registering for the application, or choose to make a new account. Additionally, the user can also choose to use Facebook to register and subsequently login to the application. This activity also keeps track of who has logged into the application locally using shared preferences. This enables the application to bypass the login screen and directly dive into the application without the user manually entering the credentials every time he wishes to use the application
- **Meal**- The Meal class defines the Meal object. This class is used for making objects of meals uploaded by a user, and subsequently keep track of who is going to deliver the meal, from where, to where, to whom, the cost of the meal and the OPT for finishing the delivery
- **RegularWork**- The RegularWork class extends the WorkManager class available in Android. This class is used to keep track of all the meals that were assigned as regular deliveries by the users while ordering and accordingly assigns a delivery person as and when required. This continues to run in the background even after the application is closed. Meals are automatically assigned as delivery person and the user receives an email notification for every 24 hours after a meal has been assigned as regular until it has been cleared manually by the user
- **RemoveNonAssigned**- The RemoveNonAssigned class extends the WorkManager class available in Android. This class is used to keep track of all the meals that were not requested for delivery. A meal is cleared off the list of the person after 15 minutes of accepting the delivery task, thus enabling the delivery persons to choose a different meal to deliver
- **SQLiteDatabaseHandler**- The SQLiteDatabaseHandler class extends the SQLiteOpenHelper and is used for describing the SQL tables required for the operation of the application. The class describes the tables for user credentials, user meals and user history. It also describes functions for creating, storing, and updating these tables
- **User**- The User class defines the User object. This class is used for making objects of the user when a profile is created
- **UserHistory**- The UserHistory class defines the activity for viewing all the meals a user has ordered using the current account
- **UserHome**- The UserHome class defines the activity that acts as the home or the dashboard for all the other operations. Here the user can view his current delivery task as well as, through a menu, access other functionalities like Profile, Deliveries, Meal Uploads and History. This view also gives the user an option to sign out the account
**UserMeal** – The UserMeal class defines the activity that a user can use to upload a home-cooked meal up for delivering it to someone. The user has to define the recipe, the pickup location and the size of the meal packet and upload the meal
- **UserProfile** – The UserProfile class defines the activity that enables the user to provide details like his name, address and contact. These details are used when a user is accomplishing a task and can be edited anytime
- **UserRegistration** – The UserRegistration class defines the activity for the registration of the user. It is one of the two screens, the other being the UserProfile, that is presented to a user at the time of registration. These details currently cannot be updated for the purpose of the demo
- **UserTask** – The UserTask class defines the activity for creating delivery tasks i.e. the user can either choose to deliver a meal or order a meal. The user can also choose to view all meals that are available for each task within the same community

#### NOTE
The application uses Google API for the calculation of distance, that is subsequently used for cost calculation. In the GitHub version of the application, the API key has been removed to prevent over usage of the API. In addition, the password for email used for sending the receipts has also been removed for privacy reasons. Anyone who wishes to use the app for demo purposes may download the source code and make changes accordingly for personal usage.

### Screenshots

#### NOTE
The details shown in the screenshots are only for representational purposes and are completely random. Details like locations and contact numbers 
have been hidden to prevent any un-related privacy intrusion.</br></br>

***Login Screen***
<br>
![Login Screen](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/login.png)
<br>
***Facebook Login***
<br>
![Facebook Login](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/fblogin.png)
<br>
***Email Registration and Profile Completion***
<br>
![Email Registration](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/register.png)
![Profile Completion](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/profile.png)
<br>
***Home Screen and Home Screen Menu***
<br>
![Home Screen](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/home.png)
![Home Screen Menu](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/homeMenu.png)
<br>
***Complete User Profile***
<br>
![Complete User Profile](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/completeProfile.png)
<br>
***Upload Meal***
<br>
![Upload Meal](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/upload.png)
<br>
***Blank Task Page***
<br>
![Blank Task Page](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/taskEntry.png)
<br>
***All meals for making delivery***
<br>
![All Meals Delivery](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/allMeals.png)
<br>
***Nearby Meals***
<br>
![Nearby Meals](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/nearMeals.png)
<br>
***Delivery Non-Assigned***
<br>
![Delivery Non-Assigned](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/deliveryNonAssigned.png)
<br>
***Get Meal***
<br>
![Get Meal](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/getMeals.png)
<br>
***Meal Confirmation***
<br>
![Meal Confirmation](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/mealConf.png)
<br>
***Order Confirmation Email***
<br>
![Order Confirmation Email](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/emailConf.png)
<br>
***Completing a Delivery***
<br>
![Completing Delivery](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/deliveryConf.png)
<br>
***Blank History and History after making orders***
<br>
![Blank History](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/blacnkHistory.png)
![History](https://github.com/nsurampu/Food-From-Home/blob/master/Screenshots/newHistory.png)
