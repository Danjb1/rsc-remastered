# To Do

## Common

 - Share common code between client and server
    - Entity definitions
    - Resource loaders
    - Packet definitions
    - Utils
 - Use some build tool (e.g. Maven)

## Client

 - Grab resources from 235 client
    - Consider changing the current resource structure
 - Zooming
 - Don't render upper-storey walls that have no floors (e.g. chimneys)
 - Don't render roofs or upper storeys if player is indoors
 - Send disconnect when closing

## Server

 - Load world
 - Send data to client when they join
