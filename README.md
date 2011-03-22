# Eclipse Enterprise Content Repository

The Eclipse Enterprise Content Repository Project (ECR) is a proposed
open source project under the Eclipse Runtime Project.

# Abstract

The scope of the project is to provide developers with a reference
implementation for a content repository that is accessible through
APIs.  It will serve as a solid and efficient base for developing
Content Applications.  By "Content Applications," we mean any
application with a primary function of managing structured or
semi-structured content in any way.  These could be Document
Management applications, Digital Asset Management applications, Case
or Record Management applications, Web Content Management
applications, or simply business-specific applications.

# How to build and run

This project is build using maven tycho. The only requirement is to use the 
3.0.3 version of maven. For building the p2 repository you just have to invoke
the build using the following command line :

$ mvn -f build/pom.xml clean install

The repository built is located in repository/target/repository. At this stage,
you're able to load ECR in eclipse. Start by configuring your target platform, 
adding the directory as an update locations and selecting the Eclipse ECR category.

At this time, you're ready to work with Eclipse ECR. You can first have a try with the server
by configuring a new launch configuration. You should select at least the following
bundles :

* org.eclipse.ecr...

Enjoy, feedbacks are welcome ...