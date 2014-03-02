Continuous Integration Game [![Build Status](https://travis-ci.org/patka/IntegrationGame.png)](https://travis-ci.org/patka/IntegrationGame)
===============

Continuous Integration Game Plugin for TeamCity.

You will earn points for successful commits or repairing the build. You will loose points for breaking the build.
Find out who makes the best commits in your team and improve your commit behavior.

Currently the plugin is developed against the version 8 of the TeamCity libraries. Unfortunately, it is not compatible with version 7.

### Build
Issue 'mvn package' command from the root project to build the plugin.
Resulting package integration-game.zip will be placed in 'target' directory.

### Install
To install the plugin, put the zip archive to 'plugins' dir under TeamCity data directory
(on Unix like systems ~/.BuildServer) and restart the server.

### Current State
What is implemented is a very minimum viable product:

Once installed you will have a new tab "CI-Game" on the build view for every build configuration the plugin is
enabled for. It will show you in a very simple way the current score for all users. Currently, you earn one
point for a good build and you loose five points for breaking the build. It is a very simple check, so if
you commit in a broken build and it stays broken you will again loose five points. 

For this to work correctly, you need to have the users configured in TeamCity and correctly map the TeamCity user
to the corresponding VCS name (e.g. Git name).

Although there is only one global score, you can configure in the administration section for which
build configurations scoring is enabled.
