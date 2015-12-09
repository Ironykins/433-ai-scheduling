#! /bin/sh
#
# mktar.sh
# Copyright (C) 2015 konrad <konrad@serenity>
#
# Distributed under terms of the MIT license.
#


tar czfv CourseScheduler.tar.gz --exclude-vcs CourseScheduler --exclude=bin --exclude=deptinst1.txt --exclude=deptinst2.txt --exclude=TestInput --exclude=examples --exclude=input.txt
