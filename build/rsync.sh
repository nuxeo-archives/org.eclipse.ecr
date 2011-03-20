#!/bin/bash

rsync -av --delete --progress ../repository/target/repository osgi@osgi.nuxeo.org:~/www/p2/ecr/current
