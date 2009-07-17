#!/bin/sh
pokerun=`ps -efa | egrep java * Pokenet.jar | grep -v grep`
	if [ "$pokerun" = "" ]; then
        echo "Starting Pokenet Server..." 
    	java -Xmx2056m -jar /home/pokenet/Server/Pokenet.jar -s low -p 500 --nogui --autorun &
        echo "Pokenet started!!!"
	else
        echo "Pokenet is already running!"
    fi
