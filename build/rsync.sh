#!/bin/bash

dir=$(dirname $0)
rsync -av --delete --progress $dir/repository/target/. osgi@osgi.nuxeo.org:~/www/p2/ecr/current/.
