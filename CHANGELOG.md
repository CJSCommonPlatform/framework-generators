# Change Log
All notable changes to this project will be documented in this file, which follows the guidelines
on [Keep a CHANGELOG](http://keepachangelog.com/). This project adheres to
[Semantic Versioning](http://semver.org/).

## [Unreleased]

## [2.4.4] - 2020-04-23
### Changed
- Update framework to 6.4.2

## [2.4.3] - 2020-04-22
### Changed
- UnifiedSearchAdapter passes transformed payload wrapped in an envelope to the UnifiedSearchIndexer

## [2.4.2] - 2020-04-14
### Changed
- Update framework to 6.4.1
- Update file-service to 1.17.13

## [2.4.0] - 2019-11-13
### Changed
- Update framework to 6.4.0

## [2.3.0] - 2019-11-07
### Changed
- Update framework to 6.3.0

## [2.2.3] - 2019-11-04
### Changed
- Update framework to 6.2.5

## [2.2.2] - 2019-10-24
### Changed
- Update framework to 6.2.2
- Update file-service to 1.17.12
- Update utilities to 1.20.3

## [2.2.1] - 2019-10-18
- Update framework to 6.2.1

## [2.2.0] - 2019-10-15
- Update framework to 6.2.0

## [2.1.0] - 2019-10-01
- Update framework to 6.1.1

## [2.0.12] - 2019-09-23
- Update framework to 6.0.16

## [2.0.11] - 2019-09-19
- Update framework-api to 4.1.0
- Update framework to 6.0.15

## [2.0.10] - 2019-09-11
- Update framework to 6.0.14

## [2.0.9] - 2019-09-08
- Update framework to 6.0.12

## [2.0.8] - 2019-08-30
- Update framework to 6.0.11

## [2.0.7] - 2019-08-28
### Changed
- Update framework to 6.0.10

## [2.0.6] - 2019-08-21
### Changed
- Update framework to 6.0.9

## [2.0.5] - 2019-08-21
### Changed
- Update framework to 6.0.8
- Update framework-generators to 2.7.3

## [2.0.4] - 2019-08-19
### Changed
- Update framework to 6.0.6

## [2.0.3] - 2019-08-19
### Changed
- Update framework to 6.0.5

## [2.0.2] - 2019-08-16
### Changed
- Update framework to 6.0.4

## [2.0.1] - 2019-08-16
### Changed
- Update framework to 6.0.3

## [2.0.0] - 2019-08-15
### Added
- Added Handler Generator for EVENT_INDEXER component
- Adding support for EVENT_INDEXER component
- Add unified search transformer cache
- New System database
- Generation of JmsCommandHandlerDestinationNameProvider for Command Handler component in messaging adapter generator
- Allow passing of additional properties in mulipart posts
- Generation of messaging clients from subscriptions-descriptor.yaml files 

### Changed
- Each generator has a corresponding Maven Mojo which allows independent dependency setting
- Refactor: Moved TransformerApi from json-transformer project in framework-api
- Updated SubscriptionWrapperFileParserFactory to handle subscription prioritisation.
- Messaging adapter generator finds the event-sources.yaml from the the classpath rather than the paths, allowing a single event-sources.yaml file in a classified jar to be added as a dependency
- Update framework-api to 4.0.1
- Update framework to 6.0.2
- Update common-bom to 2.4.0
- Update utilities to 1.20.2
- Updated test-utils to 1.24.3
- Updated file-service to 1.17.11
- Updated generator-maven-plugin to 2.7.2

### Removed
- messageSelector removed from indexer jms generator
- Removed dependency on event-store

## [1.1.3] - 2019-02-04
### Changed
- Updated common-bom to 1.29.0
- Updated even-store to 1.1.3
- Updated framework-api to 3.2.0
- Updated framework to 5.1.1
- Updated generatopr-maven-plugin to 2.6.2
- Updated raml-maven-plugin to 1.6.5
- Updated test-utils to 1.22.0
- Updated utilities to 1.16.4
- Updated file-service to 1.17.4

## [1.1.2] - 2019-01-22
### Added
- Service component name to MDC logging data
- Updated event-store to 1.1.2

## [1.1.1] - 2019-01-15
### Changed
- Updated event-store to 1.1.1 for release

## [1.1.0-M6] - 2019-01-08
### Changed
- Updated event-store to 1.1.0-M8

## [1.1.0-M5] - 2019-01-03
### Changed
- Updated event-store to 1.1.0-M7

## [1.1.0-M4] - 2019-01-02
### Changed
- Updated event-store tp 1.1.0-M6

## [1.1.0-M3] - 2018-12-28
### Changed
- Update event-store to 1.1.0-M5

## [1.1.0-M2] - 2018-12-17
### Changed
- Update event-store to 1.1.0-M2

## [1.1.0-M1] - 2018-12-10

### Changed
- Updated framework-api to 3.1.0-M2
- Updated framework to 5.1.0-M3
- Updated event-store to 1.1.0-M1
- Updated test-utils to 1.19.0

## [1.0.2] - 2018-11-16

### Changed
- Updated framework-api to 3.0.1
- Updated framework to 5.0.4
- Updated event-store to 1.0.4

## [1.0.1] - 2018-11-13

### Changed
- Updated event-store to 1.0.3

## [1.0.0] - 2018-11-07

### Changed
- First release of framework-generators 1.0.0

## [1.0.0-M4] - 2018-11-06

### Changed
- Updated framework to 5.0.0-M3

## [1.0.0-M3] - 2018-11-02

### Changed
- Updated to latest event-store version 1.0.0-M5

## [1.0.0-M2] - 2018-10-31

### Changed
- Updated to latest framework version 5.0.0-M2
- Updated to latest event-store version 1.0.0-M2

### Added
- framework-generators bom 

## [1.0.0-M1] - 2018-10-26

### Added
- Extracted project from related modules in Microservices Framework 5.0.0-M1: https://github.com/CJSCommonPlatform/microservice_framework



