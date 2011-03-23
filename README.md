# Eclipse Enterprise Content Repository

The Eclipse Enterprise Content Repository Project (ECR) is a proposed
open source project under the Eclipse Runtime Project.

## Abstract

The scope of the project is to provide developers with a reference
implementation for a content repository that is accessible through
APIs.  It will serve as a solid and efficient base for developing
Content Applications.  By "Content Applications," we mean any
application with a primary function of managing structured or
semi-structured content in any way. These could be Document
Management applications, Digital Asset Management applications, Case
or Record Management applications, Web Content Management
applications, or simply business-specific applications.

You can read more about on http://www.eclipse.org/proposals/rt.ecr/

## How to build and run

This project is build using maven tycho. The only requirement is to use the
3.0.3 version of maven. For building the p2 repository you have to
invoke the following command line:

    $ mvn -f build/pom.xml clean install

If you need to use both maven 2 and maven 3 on the same machine you should consider using

    $ mvn -f build/pom.xml -Dmaven.repo.local=/path/to/m3/repository clean install

to use another local repository for maven 3. Or you may want to make an alias and use different maven settings.xml files.

The repository built is located in `build/repository/target/repository`. At this stage,
you're able to load ECR in eclipse.

## Configuring Eclipse IDE

To configure your Eclipse you need to create a target platform that will be used when developing ECR plugins.
To create the ECR target platform:

* Open Eclipse Preferences.
* Go to `Install/Update > Available Software Sites`. Click on Add ...
* Put a name like `Local ECR` and click on `Local..` button
* Choose the repository you builded in `build/repository/target/repository`
* Then go to `Plug-in Development` preference page.
* Click on `Add ...` to define your new target platform.
* Select `Nothing: Start with an empty ...` then put a name like `Local ECR` in the text box at the top.
* Click on `Add ...` button and then select `Software Site` and choose the local repository site you created before.
* Here you may want to select both categories: ECR and ECR SDK. The SDK categroty is useful only if you need ECR sources. If no catergies are listed make sure you checked the `Group by Category` option.
* Before pressing `Finish` make sure you *UNCHECK* `Include requires software`!
* Press Finsih, Activate your new Target Platform and Apply you changes.

Now you can start working with ECR, and create your first ECR plugin.

## Launching ECR inside Eclipse

After activating the ECR target platform you can launch the ECR product directly in your Eclipse.
For this you should create a new Run Configuration as follow:

* Open `Run > Run Configurations ...`
* Create a new OSGi Framework run configuration.
* Put a name let say `ECR`.
* Set `Default Auto-Start` to *false*.
* Select the `org.eclipse.ecr.application` bundle from the list and set its autostart flag to *true*.

You are done. Launch your server now.
By default ECR will create a ~/.nxserver-osgi directory as its working directory.
Also, the Jetty HTTP server will listen at port 8080. You can change these properties
by defining some system properties in the VM args of your launch configuration:

* `nuxeo.home` - to control; the working directory location
* `org.eclipse.equinox.http.jetty.http.port` - to control the jetty port.

You can also use any property defined by Jetty bundle to configure the HTTP server.

Enjoy!


## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com/en/products/ep) and packaged applications for [document management](http://www.nuxeo.com/en/products/document-management), [digital asset management](http://www.nuxeo.com/en/products/dam) and [case management](http://www.nuxeo.com/en/products/case-management). Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

More information on: <http://www.nuxeo.com/>
