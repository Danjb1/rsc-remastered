# About

RuneScape today is unrecognisable from its original implementation back in 2001. It has been through several engine overhauls, and has long since moved away from its 2.5D roots. "RuneScape Classic" is now used to refer to the original version of the engine, before the first major 3D update (originally known as "RuneScape 2").

RSC Remastered is an attempt to recreate RuneScape Classic, in all its pixellated glory.

![RSC Screenshot](http://www.runescape.com/img/main/classic/screen-2.jpg "RuneScape Classic")

## FAQ

### What makes this different from other projects?

 - **Faithful:** No hacky, money-grabbing changes detracting from the classic RuneScape experience.
 - **Simple:** Heavily rewritten and drastically simplified source code.
 - **Easy:** No external tools required to build and run.

 > There are actually some fantastic projects out there - see the links below.

### Which version of RuneScape Classic does this emulate?

Being a continually-updated game, RuneScape Classic has been through numerous iterations. Many different client versions can be found online, each requiring different data files and a different client-server protocol.

RSC Remastered will aim to emulate the final released version of RuneScape Classic.

 > See **[versions](./versions.md)** for more information.

### Where does the game data come from?

The client is useless without the game data (maps, images, sounds, etc.).

Historically, the server would send these resources to the client, where they would be stored in the browser cache. These cache files contain Jagex Archives (`.jag` / `.mem` files), which have been analysed extensively by the community.

RSC Remastered currently uses resources originating from EasyRSC.

## Links

### Resources

 - [Client Versions](./versions.md) - List of client versions and associated source code.
 - [RuneScape Classic Dump](http://rscdump.com/) ([searchable version](https://github.com/tomfitzhenry/RuneScape-classic-dump)) - A collection of lots of useful files and tools, including mudclient JARs, game data, source code, Landscape Editor, Map Generator and Sprite Editor.
 - [News Archive](http://RuneScape.wikia.com/wiki/Game_updates) - Complete archive of RuneScape news and updates.
 - [Jagex Archive Loader](https://gitlab.openrsc.com/Logg/Game/blob/22a0b131f4d6c2e3787f6af36394dc4a439c36d9/Tools/Map%20Generator/src/com/hikilaka/file/JagArchiveLoader.java) - Java class that can load Jagex archives.
 - [Jagex Archive Documentation](https://sites.google.com/site/commiesRuneScapedocumentation/cache/archives) - In-depth explanation of the Jagex Archive format.
 - [eXemplar's Collection](https://bitbucket.org/eggsampler/rsc/src) - An assortment of documents and projects by the great eXemplar.

### Active Projects

 - [OpenRSC](https://openrsc.com/home) - An open-source RSC replica.
 - [RSC+](https://github.com/OrN/rscplus) - A really nice client mod with extra features and replay capabilities.
 - [2003Scape](https://github.com/2003scape) - JavaScript client and server for *mudclient204*.
 - [RSCGo](https://github.com/spkaeros/RSCGo) - Simple RSC server written in Go.

### Archived Projects

 - [EasyRSC](https://www.rune-server.ee/runescape-development/rs-classic/tutorials/574938-easyrsc-eclipse.html) - Partially-refactored and modified client, with game data and a simple server implementation.
 - [OpenRSCD](https://github.com/Zlacki/OpenRSCD) - C++ server and eXemplar's largely-refactored *mudclient204*.
 - [MoparClassic](https://github.com/Lothy/MoparClassic) - Java / Scala server.
 - [RSC 140 Server](https://bitbucket.org/Hikilaka/140-gameserver/src) - A Java-based server designed to work with *mudclient140*.
 - [RSC 204 Server](https://bitbucket.org/kjensenxz/rsc/src) - A JavaScript server designed to work with *mudclient204*.
