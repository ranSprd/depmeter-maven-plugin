# Dependency Metrics Maven Plugin (DepMeter)

This plugin is based on (or extendends) the well known [versions-maven-plugin](http://www.mojohaus.org/versions-maven-plugin/). It offers some
metrics about the overall age of project dependencies. Therefor several metrics for all dependencies are collected and afterwards combined into 
a single value.


# !!! WIP

## What is this metric about

A dependency metric can give you a sense of the technical age of an application. 
Normally, a high age also means a higher maintenance effort because the use of 
newer dependencies is more difficult or requires more extensive testing.

Currently the following metrics for single dependencies are implemented.

    - Version Sequence Number [1]
    - Version Number Delta [1]
    - Drift Score [2]

After calculation, for each dependency such metrics are available. The plugin
calculates a overall value(s) from all dependencies in the next step. 
This value can be used constantly in every build to get an impression about the 
technical gap in your dependencies.

## How a overall metric is calculated

## Dependency metrics for a dependency

### Version Sequence Number

### Version Number Delta

### Drift Score


### Lib Years or Version Release Date

_Version Release Date_ metric is defined as distance between two releases of a 
dependency. It can also be expressed by the number of days between the 
release dates. I.e., let _R_ be a function which returns the release date for 
a dependency version. The distance between _R(dn) = 10/3/2014_
and _R(dn + 2) = 30/6/2014_ is defined as 113 days. This measurement
can be calculated without knowledge of intermediate releases.[1]

[_Lib years_](https://libyear.com/) of a project is defined as the sum of all 
_Version Release Dates_ of dependencies. Usually it is given in years. [3]

Unfortunately is it hard to get a release date of an artifact with the default 
maven functionality. For that reason, this metric is not implemented yet. 

## Resources

    [1]: J. Cox, E. Bouwers, M. van Eekelen and J. Visser, Measuring Dependency Freshness in Software Systems. In Proceedings of the 37th International Conference on Software Engineering (ICSE 2015), May 2015 https://ericbouwers.github.io/papers/icse15.pdf
    [2]: https://nimbleindustries.io/2020/08/08/drift-score-the-dependency-drift-metric/
    [3]: https://libyear.com/