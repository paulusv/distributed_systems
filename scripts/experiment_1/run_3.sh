#!/bin/sh
HOST_IP="35.246.243.109"
RMI_IP="10.156.0.2"
LOG_DIR="logs/experiment_1/instances_3"
HOME_DIR="distributed_systems/out/production/rmi-tact"

REPLICAS=(ReplicaA ReplicaB ReplicaC ReplicaD ReplicaE ReplicaF ReplicaG ReplicaH ReplicaI ReplicaJ ReplicaK ReplicaL ReplicaM ReplicaN ReplicaO)
READWRITE=(read write)
LETTERS=(x y z)

# Start replica on instance-01
echo "Initialize experiment"

echo "=> Setup instance-01"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    rm -rf ${LOG_DIR};
    mkdir -p ${LOG_DIR}
"

echo "=> Setup instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    rm -rf ${LOG_DIR};
    mkdir -p ${LOG_DIR}
"

echo "=> Setup instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    rm -rf ${LOG_DIR};
    mkdir -p ${LOG_DIR}
"
echo ""

# Stat master and 3 replicas
echo "Start master and replicas"
echo "=> Start master on instance-01"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala -Djava.rmi.server.hostname=${HOST_IP} main.scala.history.MasterReplica > ${LOG_DIR}/master.log 2>&1 &
"

echo "=> Start Replica A on instance-01"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} A > ${LOG_DIR}/replicaA.log 2>&1 &
"
echo "=> Start Replica B on instance-01"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} B > ${LOG_DIR}/replicaB.log 2>&1 &
"
echo "=> Start Replica C on instance-01"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} C > ${LOG_DIR}/replicaC.log 2>&1 &
"
echo "=> Start Replica D on instance-01"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} D > ${LOG_DIR}/replicaD.log 2>&1 &
"
echo "=> Start Replica E on instance-01"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} E > ${LOG_DIR}/replicaE.log 2>&1 &
"

echo "=> Start Replica F on instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} F > ${LOG_DIR}/replicaF.log 2>&1 &
"
echo "=> Start Replica G on instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} G > ${LOG_DIR}/replicaG.log 2>&1 &
"
echo "=> Start Replica H on instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} H > ${LOG_DIR}/replicaH.log 2>&1 &
"
echo "=> Start Replica I on instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} I > ${LOG_DIR}/replicaI.log 2>&1 &
"
echo ""
echo "=> Start Replica J on instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} J > ${LOG_DIR}/replicaJ.log 2>&1 &
"
echo ""

echo "=> Start Replica F on instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} K > ${LOG_DIR}/replicaK.log 2>&1 &
"
echo "=> Start Replica G on instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} L > ${LOG_DIR}/replicaL.log 2>&1 &
"
echo "=> Start Replica H on instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} M > ${LOG_DIR}/replicaM.log 2>&1 &
"
echo "=> Start Replica I on instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} N > ${LOG_DIR}/replicaN.log 2>&1 &
"
echo ""
echo "=> Start Replica J on instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} O > ${LOG_DIR}/replicaO.log 2>&1 &
"
echo ""

# Wait for everything to start
sleep 5

# Random reads and writes on the replicas
echo "Random reads and writes"
cd ~/Development/Other/rmi-tact/out/production/rmi-tact/
for i in {1..250}
do
    RND_REPLICA=$((RANDOM % 9))
    REPLICA=${REPLICAS[$RND_REPLICA]}

    RND_READWRITE=$((RANDOM % 2))
    # READORWRITE=${READWRITE[$RND_READWRITE]}
    READORWRITE="write"

    RND_LETTERS=$((RANDOM % 3))
    LETTER=${LETTERS[$RND_LETTERS]}

    echo "($i/250)"
    echo "=> Action: ${READORWRITE} ${LETTER} at ${REPLICA}"

    ssh sven@instance-01 "
        source /home/sven/.sdkman/bin/sdkman-init.sh;
        cd ${HOME_DIR};
        scala main.scala.client.Client ${HOST_IP} ${REPLICA} ${READORWRITE} ${LETTER} 1
    "

    SLEEP=$(bc -l <<< "scale=4 ; ${RANDOM}/32767")
    echo "=> Sleep for ${SLEEP} seconds"
    sleep ${SLEEP}s
    echo ""
done
echo ""

# Fetch the results
echo "Results:"
echo "=> Replica A, B and C"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    scala main.scala.client.Client ${HOST_IP} ReplicaA read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaA read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaA read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaB read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaB read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaB read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaC read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaC read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaC read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaD read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaD read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaD read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaE read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaE read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaE read z;
"
echo "=> Replica D, E and F"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    scala main.scala.client.Client ${HOST_IP} ReplicaF read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaF read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaF read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaG read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaG read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaG read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaH read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaH read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaH read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaI read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaI read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaI read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaJ read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaJ read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaJ read z;
"
echo "=> Replica G, H and I"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    scala main.scala.client.Client ${HOST_IP} ReplicaK read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaK read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaK read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaL read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaL read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaL read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaM read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaM read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaM read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaN read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaN read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaN read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaO read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaO read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaO read z;
"
echo ""

# Stop everything
echo "Stop the master and all the replicas..."
ssh sven@instance-01 "lsof -tc java | xargs --no-run-if-empty kill -9"
ssh sven@instance-02 "lsof -tc java | xargs --no-run-if-empty kill -9"
ssh sven@instance-03 "lsof -tc java | xargs --no-run-if-empty kill -9"
echo ""

# Wait for everything to start
sleep 10;
echo "Fetching logs..."
mkdir -p $LOG_DIR
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/master.log" > $LOG_DIR/master.log

ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/replicaA.log" > $LOG_DIR/replicaA.log
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/replicaB.log" > $LOG_DIR/replicaB.log
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/replicaC.log" > $LOG_DIR/replicaC.log
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/replicaD.log" > $LOG_DIR/replicaD.log
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/replicaE.log" > $LOG_DIR/replicaE.log

ssh sven@instance-02 "cat ${HOME_DIR}/${LOG_DIR}/replicaF.log" > $LOG_DIR/replicaF.log
ssh sven@instance-02 "cat ${HOME_DIR}/${LOG_DIR}/replicaG.log" > $LOG_DIR/replicaG.log
ssh sven@instance-02 "cat ${HOME_DIR}/${LOG_DIR}/replicaH.log" > $LOG_DIR/replicaH.log
ssh sven@instance-02 "cat ${HOME_DIR}/${LOG_DIR}/replicaI.log" > $LOG_DIR/replicaI.log
ssh sven@instance-02 "cat ${HOME_DIR}/${LOG_DIR}/replicaJ.log" > $LOG_DIR/replicaI.log

ssh sven@instance-03 "cat ${HOME_DIR}/${LOG_DIR}/replicaK.log" > $LOG_DIR/replicaK.log
ssh sven@instance-03 "cat ${HOME_DIR}/${LOG_DIR}/replicaL.log" > $LOG_DIR/replicaL.log
ssh sven@instance-03 "cat ${HOME_DIR}/${LOG_DIR}/replicaM.log" > $LOG_DIR/replicaM.log
ssh sven@instance-03 "cat ${HOME_DIR}/${LOG_DIR}/replicaN.log" > $LOG_DIR/replicaN.log
ssh sven@instance-03 "cat ${HOME_DIR}/${LOG_DIR}/replicaO.log" > $LOG_DIR/replicaO.log

echo "Done!"