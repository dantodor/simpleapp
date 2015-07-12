#/bin/bash
java -server -XX:+UseNUMA -XX:+UseCondCardMark -XX:-UseBiasedLocking -XX:+UseParallelGC -Xms1g -Xmx8g -Xss1M -cp .:/home/user/tester/tester.jar com.example.Tester /home/user/tester/tester.conf

