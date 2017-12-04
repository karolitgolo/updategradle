# Update Application Gradle Plugin

## Description
This plugin can be use with gradle and java application. In gradle,
algorithm example send files of application for FTP server. In java
application, algorithm check new version of application and make
it update.

## Include plugin to your project
- In begin ```build.gradle``` add:

```
buildscript {
    repositories {
        maven {
            url "http://dl.bintray.com/itgolo/libs"
        }
    }
    dependencies {
        classpath 'pl.itgolo.libs:updategradle:1.+'
    }
}

apply plugin: 'pl.itgolo.libs.updategradle' version '${updateGradleVersion}'
````

- or by DSL Plugin

```
plugins {
    id 'pl.itgolo.libs.updategradle'
}
```

- Configuration plugin in ```build.gradle```:

```
UpdatePlugin {
    dirReleaseUnpackAppFiles = 'C:\\MyProject\\Build\\Relese'
    remoteDirApp = '/public_html/myApp'
    newVersion = '1.0.0.0'
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

#### newVersion
New version of my application to upload.

#### urlApp
Address url for file contains in directory ```remoteDirApp```

#### forceUpload

- **true**: If current version application exist in server FTP,
gradle throw error about exist version i server FTP

- **false**: If current version application exist in server FTP,
plugin force replace old files to new files this some version application.

## How to work
In gradle must tun task ```deployApp```. In remote FTP directory ```remoteDirApp```
will be created ```upload.json``` file, contains newest version of my application,
declared in ```gradle.properties``` file.

In remote FTP directory ```remoteDirApp``` will be created directory ```files```
contains directories with names of version. The directory with names of version,
will be contains files of my application from ```dirReleaseUnpackAppFiles``` directory.

In remote FTP directory ```remoteDirApp``` will be created file with structure files
of current version, example ```1.0.0.0-structure.json```. The structure files will be
contains structure files of my current version application with MD5 checksum of files of
current version.

## Deploy application to FTP
If your all files of application exist in directory ```dirReleaseUnpackAppFiles```
run ```deployApp```

For one click deploy application can use ```publishNewVersionApp```
for tests task and generate file your application.

## Deploy plugin

#### Deploy plugin to ```Bintray``` by ```gradle publishPluginBintray```
```publishPluginBintray``` has depends on tests: unit, integration and functional.
Always after upload plugin to bintray, gradle run tests.

#### Or deploy plugin to ```jCenter``` by ```gradle publishPluginJCenter```
```publishPluginJCenter``` has depends on tests: unit, integration and functional.
Always after upload plugin to jCenter, gradle run tests.

You must add two authorization properties to file ```gradle.properties```
example in ```$HOME/.gradle/gradle.properties``` file.

Guide for new account in jCenter in this link:
https://guides.gradle.org/publishing-plugins-to-gradle-plugin-portal