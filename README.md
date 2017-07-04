# OpenRSC

An open-source re-implementation of Runescape Classic.

# Background

Runescape today is unrecognisable from its original implementation back in 2001. It has been through several engine overhauls, and has long since moved away from its 2.5D roots. "Runescape Classic" is now used to refer to the original version of the engine, before the first major 3D update (originally known as "Runescape 2").

OpenRSC is an attempt to faithfully recreate the original game, for anyone who wants to relive it. Similar projects exist already, but often they are focused on making money and include ugly / hacky changes which detract from the classic Runescape experience.

# FAQ

## Which version of Runescape Classic does this emulate?

Being a continually-updated game, Runescape Classic has probably been through hundreds of iterations, so it is hard to say which is the "true" version. Fortunately this project is still a long way off from having to make that decision.

## Where does this code come from?

This project will ultimately consist of 2 applications: the client and server.

The **client** source code is based on previous community efforts to decompile and deobfuscate the original application, but significant sections of code have been rewritten from scratch to make them simpler and easier to understand.

The **server** source code will likely be based on existing community server implementations, but again, hopefully greatly simplified.

## What about the game data?

The game data (maps, sprites, models, etc.) exists in RSCD archives, which are basically just ZIP files. They won't be included in this project, but they can be found online. I'm a little fuzzy about the origins of these files myself, but I believe they may have come from a cache dump of the original Runescape client.

The following files are required:

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

## Can I run my owner server?

Yes, in theory. OpenRSC is intended primarily for personal use, and will not necessarily include the security and optimisations typically required by larger servers.

# Current Status

### Client

 - Loading screen which loads game data from RSCD archives.
 - Basic title screen.
 - Landscape rendering (in progress).

### Server

 - Not yet implemented.
