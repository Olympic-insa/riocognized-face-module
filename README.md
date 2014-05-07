Rio'Cognized Face Module
======================

Facial recognition module for Riocognized Webservice

## Setup OpenCV, JavaCV & JavaCPP
*And a proper dev environement :smile:*

- You need to get OpenCV sources and compile Library following OpenCV doc :  [OpenCV Installation Guide][1]
- Install JavaCPP wrapper : [new doc available here][3]
- Install JavaCPP wrapper : [new doc available here][2]
- Restart your labptob

## Get Riocongized Face Module and compile it, using your favorite IDE
*Could be terminal too :heart:*

- `git clone https://github.com/Olympic-insa/riocognized-face-module.git`
- cd riocognized-face-module
- `mkdir lib` (create lib/ folder in this repertory)
- Copy opencv-248.jar and libopencv_java248* in lib directory (.so or .dll, filename may vary depending on your OS) to lib directory
- Add openCV JAR to maven local repository : `mvn install:install-file -Dfile=path_to_openCV_JAR/opencv-248.jar -DgroupId=org.opencv -DartifactId=OpenCV -Dversion=2.48 -Dpackaging=jar`
- Execute maven goal to compile `mvn clean install`

##  Run it!
```
java -jar facedetector-1.0-SNAPSHOT.jar /path/to/your/image.jpg /path/to/face/db.csv
```

## How it works
- Load facedatabase, parsing CSV file using second arg. Defautl value is provied if arg is null (see sources)
- Load image using first arg.
- Detect athlete faces in the picture and frame it.
- Try to recognize face using LBP, Eigenfaces and Fischerfaces with different parameters

Result is showed in the console and logged into rio.log file.

## Developers and Contributors
*If your not familiar with .git and Github :octocat:*
- Watch this [video][5].
- Please, read this [git cheat sheet][6].
- Have a look at OpenCV user mailingist [answers.opencv.org][7].
- Suscribe to JavaCV dev mailingist : [Google Groups][4].
- Ask questions if needed. :email:

![Alt Text](https://pbs.twimg.com/media/BmoofE-IAAEmrcn.jpg)

[1]: http://docs.opencv.org/trunk/doc/tutorials/introduction/linux_install/linux_install.html
[2]: https://github.com/bytedeco/javacv/blob/master/README.md
[3]: https://github.com/bytedeco/javacpp/blob/master/README.md
[4]: https://groups.google.com/forum/#!forum/javacv
[5]: https://vimeo.com/72955426
[6]: https://github.com/Olympic-insa/github-cheat-sheet/blob/master/README.md
[7]: http://answers.opencv.org/questions/

