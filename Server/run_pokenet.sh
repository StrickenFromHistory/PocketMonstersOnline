#!/bin/sh
pokerun=`ps -efa | egrep java * Pokenet.jar | grep -v grep`
	if [ "$pokerun" = "" ]; then
        echo "Starting Pokenet Server..." 
    	java -Xmx956m -jar /home/pokenet/Server/Pokenet.jar -s low -p 100 --nogui --autorun > pokenet.log &
        echo "Pokenet started!!!"
	else
        echo "Pokenet is already running!"
    fi
