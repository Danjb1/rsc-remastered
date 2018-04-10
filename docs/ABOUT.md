# About

RuneScape today is unrecognisable from its original implementation back in 2001. It has been through several engine overhauls, and has long since moved away from its 2.5D roots. "RuneScape Classic" is now used to refer to the original version of the engine, before the first major 3D update (originally known as "RuneScape 2").

OpenRSC is an attempt to recreate RuneScape Classic, in all its pixellated glory.

![RSC Screenshot](http://www.runescape.com/img/main/classic/screen-2.jpg "RuneScape Classic")

## FAQ

### What makes this different from other projects?

 - **Faithful:** No hacky, money-grabbing changes detracting from the classic RuneScape experience.
 - **Simple:** Heavily rewritten and drastically simplified source code.
 - **Standalone:** No need to maintain compatibility with existing client-server protocols.

### Which version of RuneScape Classic does this emulate?

Being a continually-updated game, RuneScape Classic has been through numerous iterations, so it is hard to say which is the "true" version.

Here are some of the more notable client versions:

 > **mudclient26** - Earliest reported version (8 April 2001)  
 > **[mudclient39](https://github.com/tomfitzhenry/RuneScape-classic-dump/blob/master/eXemplar's-collection/exemplar/rs1/rs1/mudclients/mudclient39.jar)** - Earliest version available for download  
 > **[mudclient196](https://github.com/tomfitzhenry/RuneScape-classic-dump/blob/master/eXemplar's-collection/exemplar/rs1/rs1/mudclients/mudclient196.jar)** - One of the later versions (28 June 2004)  
 > **[mudclient202](https://github.com/tomfitzhenry/runescape-classic-dump/tree/master/rs1/rs1/v202)** - A popular client version  
 > **[mudclient204](https://github.com/tomfitzhenry/RuneScape-classic-dump/blob/master/eXemplar's-collection/exemplar/rs1/rs1/mudclients/mudclient204.jar)** - Perhaps the most popular client version  
  > **[mudclient233](https://bitbucket.org/eggsampler/rsc/src/63e3a5c9482c27b3555b38b6a6b9453d2f1771c4/jars)** - Latest released version  

These are not to be confused with the RuneScape 2 versions, which follow a similar numbering scheme:

 > **[r194](http://rsclients.wikia.com/wiki/Revision_194)** - RuneScape 2 beta (1 December 2003)  
 > **[r289](http://rsclients.wikia.com/wiki/Revision_289)** - Early version popular with private servers (January 23 2005)  
 > **[r317](http://rsclients.wikia.com/wiki/Revision_317)** - Early version popular with private servers (May 5th 2005)  

Ideally, OpenRSC will include all the features from the latest RuneScape Classic client. Realistically, though, it depends on the availability of the game's data files (read on).

### Where does the game data come from?

The client is useless without the game data (maps, images, sounds, etc.).

Historically, the RuneScape game data was sent to the client in the form of "jag" and "mem" files. Fortunately, the [community](https://github.com/SamHammersley/JAG-Archive-Editor) [has](https://github.com/tomfitzhenry/RuneScape-classic-dump/blob/master/RSC%20Map%20Generator/RSC%20Map%20Generator/src/com/hikilaka/file/JagArchiveLoader.java) [managed](https://sites.google.com/site/commiesRuneScapedocumentation/cache/archives) to decipher these file formats, and has repackaged the necessary files into RSCD archives, the simplest of which are essentially ZIP files.

The following RSCD files are required by OpenRSC:

    Animations.rscd
    Doors.rscd
    Elevation.rscd
    Items.rscd
    Landscape.rscd
    NPCs.rscd
    Objects.rscd
    Prayers.rscd
    Spells.rscd
    Sprites.rscd
    Textures.rscd
    Tiles.rscd

It is my understanding that these files contain the game data from *mudclient204*, extracted from the following cache files:

    config85.jag
    entity24.jag
    entity24.mem
    filter2.jag
    jagex.jag
    land63.jag
    land63.mem
    maps63.jag
    maps63.mem
    media58.jag
    models36.jag
    sounds1.mem
    textures17.jag

## Links

### Resources

 - [RuneScape Classic Dump](https://github.com/tomfitzhenry/RuneScape-classic-dump) - A collection of lots of useful files and tools, including mudclient JARs, game data, source code, Landscape Editor, Map Generator and Sprite Editor.
 - [Game Updates](http://RuneScape.wikia.com/wiki/Game_updates) - Complete archive of RuneScape news and updates.
 - [RSWiki](https://rswiki.moparisthebest.com) - Useful information about game data and protocols.

### Source Code

 - [EasyRSC](https://www.rune-server.ee/runescape-development/rs-classic/tutorials/574938-easyrsc-eclipse.html) - Partially-refactored and modified *mudclient204*, with game data, and a simple server implementation.
 - [RSC 40, 127, 204, 233](https://bitbucket.org/eggsampler/rsc/src) - An assortment of partially-refactored client versions by the great eXemplar, with game data and documentation.
 - [OpenRSCD](https://github.com/Zlacki/OpenRSCD) - C++ server and eXemplar's largely-refactored *mudclient204*.
 - [RSC 135](https://bitbucket.org/_mthd0/rsc/src) - Partially-refactored *mudclient135*, with game data.
 - [RSC 140](https://bitbucket.org/Hikilaka/mudclient-140/src) - Partially-refactored *mudclient140*, with game data (accompanying server is [here](https://bitbucket.org/Hikilaka/140-gameserver/src)).
 - [RSC+](https://github.com/OrN/rscplus) - A really nice RuneScape Classic client mod with lots of features.
 - [MoparClassic](https://github.com/Lothy/MoparClassic) - A popular community server.
 - [RSC 204 Server](https://bitbucket.org/kjensenxz/rsc/src) - A server designed to work with *mudclient204*.
