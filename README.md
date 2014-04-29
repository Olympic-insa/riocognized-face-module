Rio'Cognized Face Module
======================

Facial recognition module for Riocognized Webservice

## Setup OpenCV

- You need to compile OpenCV Library following OpenCV doc.
- `git clone https://github.com/Olympic-insa/riocognized-face-module.git`
- cd riocognized-face-module
- `mkdir lib` (create lib/ folder in this repertory)
- Copy opencv-248.jar and libopencv_java248* in lib directory (.so or .dll, filename may vary depending on your OS) to lib directory
- Add openCV JAR to maven local repository : `mvn install:install-file -Dfile=path_to_openCV_JAR/opencv-248.jar -DgroupId=org.opencv -DartifactId=OpenCV -Dversion=2.48 -Dpackaging=jar`
- Execute maven goal to compile `mvn clean install`
- Run it!
```
java -jar facedetector-1.0-SNAPSHOT.jar /path/to/your/image.jpg
```

## How it works

Load facedatabase, parsing CSV file
Detect athlete faces in a picture and frame them.
Try to recognize face using LBP, Eigenfaces and Fischerfaces with differetn parameters

Result is showed in the console and logged into rio.log file.




