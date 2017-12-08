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

## How work upload new version application
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

## Integration with external application

Must be plugin deployed to FTP, for begin integrate with external application.
For integrate with external application, publish update plugin
in maven repository, example by ```bintrayUpload```.

Import plugin update to project. In ```build.gradle``` add dependency and repository.

```
repositories {
    maven {
        url "http://dl.bintray.com/itgolo/libs"
    }
}
dependencies {
    compile 'pl.itgolo.libs:updategradle:1.0.0.0f'
}
```

In external application add code for run update plugin:

```
String appTitle = "TitleApp"
URL urlDirUpdatePlugin = new URL("http://host/updategradle");
File appDir = new File(".");
String appCurrentVersion = "1.0.0.0";
String urlDirApp = "http://host/myapp";
String commandReturnAfterUpdated = "C:\\app\\myApp.exe --args";
Boolean silent = true;
String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

LaunchUpdateApp launchUpdateApp = new LaunchUpdateApp(
        appTitle, urlDirUpdatePlugin, appDir, appCurrentVersion,
        urlDirApp, commandReturnAfterUpdated, silent, pid);
launchUpdateApp.launch();
```

For launch update, close external application example:

```
System.exit(0);
```

Update plugin be waiting for closed application external.

After update external application, update plugin run external
application with argument ```--updated```. If application external
was updated, then argument equal ```true``` or ```false``` for otherwise.

Can be set timeout waiting close external application by:

```
launchUpdateApp.setTimeoutWaitClose(120);
```

For debug mode add:

```
launchUpdateApp.setDebug(true);
```

All logs contains in ```app/update/logs``` directory of application

For detect new version external application in remote server, use in your
application:

```
String updateJsonRemote = "http://host/appDir/update.json";
String currentOldVersion = "1.0.0.0";
RemoteNewVersion remoteNewVersion = new RemoteNewVersion(updateJsonRemote, 25);
if (remoteNewVersion.hasNewVersion(currentOldVersion)){
    // launch update app
}
```

####
Arguments for run jar update plugin:

```
--silent, --versionToCompare=1.0.0.0 --appDir=C:\appDir
--remoteUrl=http://host/appDir --timeoutWaitClose=120 --pid=12345
--commandReturnMainApp="java -jar C:\appDir\myApp.jar --arg"
```

## Deploy plugin (for dev of this plugin)

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