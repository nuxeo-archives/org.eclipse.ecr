#!/bin/bash

rsync -av --delete --progress repository/target/. osgi@osgi.nuxeo.org:~/www/p2/ecr/current/.
