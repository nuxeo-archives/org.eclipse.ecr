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

You can read more about the proposal on <http://www.eclipse.org/proposals/rt.ecr/>

## How to build and run

This project is build using [maven tycho](http://tycho.sonatype.org/). The only requirement is to use the 
3.0.3 version of maven. We used to isolate the tycho build from others using 
a dedicated local m2 repository. For building the p2 repository you have to 
invoke the following command line:

    $ mvn -f build/pom.xml -Dmaven.repo.local=xxx clean install

The repository built is located in `build/repository/target/repository`. At this stage,
you're able to load ECR in eclipse. Start by configuring your target platform, 
adding the directory as an update locations and selecting the Eclipse ECR category.

At this time, you're ready to work with Eclipse ECR. You can first have a try with
the server by configuring a new launch configuration. You should select at least the
following bundles:

* `org.eclipse.ecr.equinox` equinox OSGi framework
* `org.eclipse.ecr.native` native ECR services
* `org.eclipse.ecr.h2` h2 database connector

Enjoy. Your feedback is welcome ...

## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com/en/products/ep) and packaged applications for [document management](http://www.nuxeo.com/en/products/document-management), [digital asset management](http://www.nuxeo.com/en/products/dam) and [case management](http://www.nuxeo.com/en/products/case-management). Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

More information on: <http://www.nuxeo.com/>