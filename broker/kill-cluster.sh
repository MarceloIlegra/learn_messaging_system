#!/bin/bash
# Put down cluster
#netstat -tulpn | grep :2181 |  awk '{ print $7 }'

GetPID () {
  PROCESS=$(netstat -tulpn | grep :$1 |  awk '{ print $7 }')
  if [ ! -z "$PROCESS" ]; then   
    PID=${PROCESS:0:-5} 
    echo $PID
  fi
}

kafka_hosts=( "9092" "9093" "9094" "2181") 
for i in "${kafka_hosts[@]}"
do
  kill -9 $(GetPID $i)
done


