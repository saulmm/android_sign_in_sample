Android sign in sample
=============================

Screenshots:
----------------

![Alt text](https://googledrive.com/host/0B62SZ3WRM2R2MGFiQ09UeTlWR00/sign_in_plane.png)
 . ![Alt text](https://googledrive.com/host/0B62SZ3WRM2R2MGFiQ09UeTlWR00/sign_google_dialog.png) 



Google stuff:
----------------

First of all, to access to the google+ integration api you have to create a new project 
in the https://code.google.com/apis/console and generate a new access key.

In the services section you have to activate the __google+ api__ toggle.

![Alt text](https://googledrive.com/host/0B62SZ3WRM2R2MGFiQ09UeTlWR00/api_dashboard2.png)


After that, access to the section _Api Access_ to generate the access ID to the service,

![Alt text](https://googledrive.com/host/0B62SZ3WRM2R2MGFiQ09UeTlWR00/sign_dialog.png)


in the __client ID dialog__ you have to put your __debug signature key__, you can generate a __SHA1__ certificate with:

```
{YOUR JDK PATH}/Contents/Home/bin/keytool -list -v -keystore {YOUR USER FOLDER}/debug.keystore
```

in my case....

```
/Library/Java/JavaVirtualMachines/jdk1.7.0_21.jdk/Contents/Home/bin/keytool -list -v -keystore /Users/lolete/.android/debug.keystore
```

the project has been build with __android studio__ and __gradle__, to get all necesary dependencies, 
first you have to download the __google repository__ that you can find in the __sdk manager__, after, 
you have to put this line in your __build.gradle__ file, in my case is the _3.1.36_ version of __google play services__.

```
compile 'com.google.android.gms:play-services:3.1.36'
```

You can see your available __play services__ versions in your sdk folder: 
```
{YOUR ANDROID STUDIO INSTALL}/sdk/extras/google/m2repository/com/google/android/gms/play-services
```
__


Features
------------
- Google+ sign in
- Google+ log out
- Google+ revoke permissions

Refs
------------
https://developers.google.com/+/api/moment-types/
http://www.sgoliver.net/blog/?p=4150
