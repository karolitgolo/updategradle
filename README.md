# Update Application Gradle Plugin

## Description
This plugin can be use with gradle and java application. In gradle,
algorithm example send files of application for FTP server. In java
application, algorithm check new version of application and make
it update.

## Include plugin to your project
- In build script add repository:

```
maven {
    url "http://dl.bintray.com/itgolo/libs"
    }
````

- Apply plugin in ```build.gradle```:

```
apply plugin: 'pl.itgolo.libs.updategradle.UploadPlugin'
```

- Configuration plugin in ```build.gradle```:

```
UpdatePlugin {
    dirReleaseUnpackAppFiles = 'C:\\MyProject\\Build\\Relese'
    remoteDirApp = '/public_html/myApp'
    ftpHost = 'ftp.host'
    ftpUser = 'user'
    ftpPassword = 'password'
    urlApp = 'http://host/myApp'
    forceUpload = false
}
```

## Configuration plugin

#### dirReleaseUnpackAppFiles
All files of my release application. Example: directory with my exe file,
libraries and other files requires to run my application.

#### remoteDirApp
Directory in FTP server contains files from directory ```dirReleaseUnpackAppFiles```

## urlApp
Address url for file contains in directory ```remoteDirApp```

## forceUpload

- **true**: If current version application exist in server FTP,
gradle throw error about exist version i server FTP

- **false**: If current version application exist in server FTP,
plugin force replace old files to new files this some version application.

## Deploy plugin
Always before deploy plugin gradle, it is make tests unit,
integration and functional.



- Deploy plugin ```gradle bintrayUpload```