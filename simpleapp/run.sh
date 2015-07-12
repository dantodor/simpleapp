#/bin/bash
java -server -XX:+UseNUMA -XX:+UseCondCardMark -XX:-UseBiasedLocking -XX:+UseParallelGC -Xms1g -Xmx8g -Xss1M -cp .:/home/user/simpleapp/simpleapp.jar com.example.app.App /home/user/simpleapp/app.conf

