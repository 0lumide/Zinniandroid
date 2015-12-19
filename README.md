[![Circle CI](https://circleci.com/gh/0lumide/Zinniandroid.svg?style=svg)](https://circleci.com/gh/0lumide/Zinniandroid)
#Zinniadroid
This fork is the gradle version of the fork by @wkpark which this was forked from, with a few changes

* This fork moves the model file from the apps asset folder to a centralized location
* This fork would reuse the any model files already present that has the same name

##Zinnia Documentation
For documentation on zinnia see the [documentation page](https://rawgit.com/taku910/zinnia/master/zinnia/doc/index.html)

##How to use Zinniadroid
* ###To add zinniadroid to your project:

  1. **Add the path to your ndk directory to your projects local.properties [more info](http://stackoverflow.com/questions/23321680/android-studio-ndk-dir-issue)**
  2. ~~Download this repo and add to your gradle project as a module~~
    Now available in jCenter and mavenCentral
    ```
    compile 'co.mide.zinniandroid:zinniandroid:0.0.3'`
    ```
  3. import 'import org.xdump.android.zinnia.Zinnia;'

* ###To use ziniadroid
  1. You may need to add the following permissions to your AndroidManifest.xml
    ```xml
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    ```
  2. Create a Zinnia object by passing a reference of your applications context to the constructor
  3. Create a zinnia recognizer by passing the name of your modek file in your assets directory, note that this throws a `ModelDoesNotExistException` Exception
