# OpenRSC

An open-source re-implementation of Runescape Classic.

# Background

Runescape today is unrecognisable from its original implementation back in 2001. It has been through several engine overhauls, and has long since moved away from its 2.5D roots. "Runescape Classic" is now used to refer to the original version of the engine, before the first major 3D update (originally known as "Runescape 2"). Of course, due to the continually-updated nature of the game, even Runescape Classic has probably been through hundreds of iterations, so it is hard to say which is the "true" version.

This project is an attempt to recreate the original game, for anyone who wants to relive it. Similar projects exist already, but often they are focused on making money and include ugly / hacky changes which detract from the classic Runescape experience. This project is intended primarily for personal use, and will not necessary include the security and optimisations typically required by larger servers.

This project will ultimately consist of 2 applications: the client and server. The client source code is based on previous community efforts to decompile and deobfuscate the original application, but significant sections of code have been rewritten from scratch to make them simpler and easier to understand.

# Current Status

### Client

 - Game data loaded from RSCD archives.
 - Game window displayed.
 - Basic game loop initialised.
 - Loading screen and title screen rendered.

### Server

 - Not yet implemented.

# Coming Soon...

### Client

 - Functional login screen.
 - Game world rendered.

### Server

 - Basic server with login capabilities.
