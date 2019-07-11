# Change Log
All notable changes to this project will be documented in this file, which follows the guidelines
on [Keep a CHANGELOG](http://keepachangelog.com/). This project adheres to
[Semantic Versioning](http://semver.org/).

## [Unreleased]

## [2.0.0-RC4] - 2019-07-11

### Changed
- Messaging adapter generator finds the event-sources.yaml from the the classpath rather than the paths, allowing a single event-sources.yaml file in a classified jar to be added as a dependency

## [2.0.0-RC3] - 2019-07-11

### Changed
- Update framework to 6.0.0-RC3

## [2.0.0-RC2] - 2019-07-04

### Changed
- Update framework to 6.0.0-RC2

## [2.0.0-RC1] - 2019-06-27

### Added
- New System database
- Generation of JmsCommandHandlerDestinationNameProvider for Command Handler component in messaging adapter generator
- Allow passing of additional properties in mulipart posts

### Changed
- Update common-bom to 2.3.0
- Update framework-api to 4.0.0
- Update framework to 6.0.0-RC1
- Updated utilities to 1.20.0
- Updated test-utils to 1.24.2
- Updated file-service to 1.17.9
- Updated generator-maven-plugin to 2.7.1
- Removed dependency on event-store
- Each generator has a corresponding Maven Mojo which allows independent dependency setting
- messageSelector removed from indexer jms generator
- Add unified search transformer cache
- Refactor: Moved TransformerApi from json-transformer project in framework-api
- Added Handler Generator for EVENT_INDEXER component
- Adding support for EVENT_INDEXER component
- Updated SubscriptionWrapperFileParserFactory to handle subscription prioritisation.

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



