# Dependency Metrics Maven Plugin (DepMeter)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ranSprd_depmeter-maven-plugin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ranSprd_depmeter-maven-plugin) 
[![CodeScene Code Health](https://codescene.io/projects/25468/status-badges/code-health)](https://codescene.io/projects/25468)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ranSprd/depmeter-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ranSprd%22%20AND%20a:%22depmeter-maven-plugin%22)

This plugin is based on (or extendends) the well known [versions-maven-plugin](http://www.mojohaus.org/versions-maven-plugin/). It offers some
metrics about the overall age of project dependencies. Therefor several metrics for all dependencies are collected and afterwards combined into 
a single value.

## How to use

Add the plugin dependency into your pom.xml file

      <plugin>
        <groupId>io.github.ranSprd</groupId>
        <artifactId>depmeter-maven-plugin</artifactId>
        <version>0.5.0</version>
      </plugin>    

Then you can use it on command line

        mvn depmeter:dependency-metrics

and get an output similar to this

        The following dependency freshness metrics for the entire project were calculated (zero = best value)
            drift score 
                    overall : 2,510
                    package : 0,630
            sequence number 
                    overall : 121
                    package : 21
            versing number delta 
                    overall : VersionDelta {major=0, minor=16, patch=91}
                    package : n/a

# !!! WIP

## What is this metric about

A dependency metric can give you a sense of the technical age of an application. 
Normally, a high age also means a higher maintenance effort because the use of 
newer dependencies is more difficult or requires more extensive testing.

Currently the following metrics for single dependencies are implemented.

- Version Sequence Number [1]
- Version Number Delta [1]
- Drift Score [2]


## How a overall metric is calculated


To calculate an entire project’s score, we add up the scores of every package 
they depend on. That includes direct dependencies: packages you add specifically. 
But it also includes indirect dependencies which are packages included 
by the packages you include.

## Dependency metrics for a dependency

### Version Sequence Number

The difference between two separate versions of a dependency can be 
expressed by the difference of the version sequence numbers of two releases. 
This measurement does not necessarily take into account the version number of 
a dependency, but can also employ the release date of the dependency to order 
difference versions. I.e., for a dependency with the versions (dn, dn+1, dn+2) 
ordered by release date, the version sequence distance between dn and dn+2 is 2. 

Consideration: Dependencies with short release cycles are penalized 
by this measurement, as the version sequence distance is relatively high 
compared to other dependencies[1].

### Version Number Delta

This metric [1] is computed by comparing the version numbers of the 
releases of a dependency. Comparing two version number tuples can be
done by calculating the delta of all version number tuples between two 
releases. A version number is defined as a tuple (x, y, x) where x 
signifies the major version number, y the minor version number and x 
the patch version number. The function v returns the version numbers 
tuple for a version of a dependency.
The delta is defined as the absolute difference between the 
highest-order version number which has changed compared to the previous 
version number tuple. To compare multiple consecutive version number 
tuples, the deltas between individual versions are added like normal vectors. 

For example, two consecutive versions of a dependency 
v(dn) = (1, 2, 2) and v(dn+1) = (1, 3, 0) results in the version delta 
distance (0, 1, 0). 

### Drift Score

The drift score metric [2] is a weighted metric. For each version step a 
counter is increased. The value of that increase depends on the most 
significant version part (major, minor, patch). Whereby 

    major = 1.0
    minor = 0.1
    patch = 0.01 

Let’s say you are running version 2.4.5 of a given package and three 
newer versions are available. Here’s how the Drift Score plays out while 
calculating the total difference.

    2.4.5 is your version 
    2.4.5 to 2.4.6 is a 0.01 difference 
    2.4.6 to 2.5.0 is a 0.1 difference 
    2.5.0 to 3.0.0 is a 1.0 difference

The difference between each release gets summed up and your total Drift Score 
for that package 1.11.

### Lib Years or Version Release Date

_Version Release Date_ metric is defined as distance between two releases of a 
dependency. It can also be expressed by the number of days between the 
release dates. I.e., let _R_ be a function which returns the release date for 
a dependency version. The distance between _R(dn) = 10/3/2014_
and _R(dn + 2) = 30/6/2014_ is defined as 113 days. This measurement
can be calculated without knowledge of intermediate releases[1].

Based on that, you can calculate the [_Lib years_](https://libyear.com/) of a project.
It is defined as the sum of all _Version Release Dates_ of dependencies. 
Usually it is given in years[3].

*Unfortunately is it hard to get a release date of an artifact with the default 
maven functionality. For that reason, this metric is not implemented yet.*

## Resources

[1]: J. Cox, E. Bouwers, M. van Eekelen and J. Visser, Measuring Dependency Freshness in Software Systems. In Proceedings of the 37th International Conference on Software Engineering (ICSE 2015), May 2015 https://ericbouwers.github.io/papers/icse15.pdf

[2]: drift score https://nimbleindustries.io/2020/08/08/drift-score-the-dependency-drift-metric/

[3]: libyear https://libyear.com/
