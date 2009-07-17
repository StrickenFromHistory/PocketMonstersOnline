#!/bin/sh
pokerun=`ps U pokenet | grep -v grep | grep Pokenet.jar`
        if [ "$pokerun" = "" ]; then
        echo "Starting Pokenet Server..." 
        java -Xmx956m -jar /home/pokenet/Server/Pokenet.jar -s low -p 100 --nogui --autorun > pokenet.log &
        echo "Pokenet started!!!"
        else
        echo "Pokenet is already running!"
    fi

