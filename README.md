Continuous Integration Game [![Build Status](https://travis-ci.org/patka/IntegrationGame.png)](https://travis-ci.org/patka/IntegrationGame)
===============

Continuous Integration Game Plugin for TeamCity.

You will earn points for successful commits or repairing the build. You will loose points for breaking the build.
Find out who makes the best commits in your team and improve your commit behavior.

Currently the plugin is developed against the version 8 of the TeamCity libraries. I guess it will also work with
TeamCity 7 but I did not test this.

### Build
Issue 'mvn package' command from the root project to build the plugin.
Resulting package integration-game.zip will be placed in 'target' directory.

### Install
To install the plugin, put the zip archive to 'plugins' dir under TeamCity data directory and restart the server.

### Current State
What is implemented is a very minimum viable product:

Once installed you will have a new tab "CI-Game" in the build details. It will show you in a very simple way the
current score for all users. Currently, you earn one point for a good build and you loose five points for breaking
the build. It is a very simple check, so if you commit in a broken build and it stays broken you will again loose
five points. 

Currently, there is no user mapping, so the username of the VCS needs to match the username in TeamCity for this
to work.

The plugin is automatically enabled for all projects and the user score is global.
