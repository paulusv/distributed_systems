#!/bin/sh
HOST_IP="35.246.243.109"
RMI_IP="10.156.0.2"
LOG_DIR="logs/experiment_1/instances_1"
HOME_DIR="distributed_systems/out/production/rmi-tact"

REPLICAS=(ReplicaA ReplicaB ReplicaC)
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

echo "=> Start Replica B on instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} B > ${LOG_DIR}/replicaB.log 2>&1 &
"

echo "=> Start Replica C on instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} C > ${LOG_DIR}/replicaC.log 2>&1 &
"
echo ""

# Wait for everything to start
sleep 5

# Random reads and writes on the replicas
echo "Random reads and writes"
cd ~/Development/Other/rmi-tact/out/production/rmi-tact/
for i in {1..100}
do
    RND_REPLICA=$((RANDOM % 3))
    REPLICA=${REPLICAS[$RND_REPLICA]}

    RND_READWRITE=$((RANDOM % 2))
    READORWRITE=${READWRITE[$RND_READWRITE]}

    RND_LETTERS=$((RANDOM % 3))
    LETTER=${LETTERS[$RND_LETTERS]}

    echo "($i / 100)"
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
echo "=> Replica A"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    scala main.scala.client.Client ${HOST_IP} ReplicaA read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaA read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaA read z;
"
echo "=> Replica B"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    scala main.scala.client.Client ${HOST_IP} ReplicaB read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaB read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaB read z;
"
echo "=> Replica C"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    scala main.scala.client.Client ${HOST_IP} ReplicaC read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaC read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaC read z;
"
echo ""

# Stop everything
echo "Stop the master and all the replicas..."
ssh sven@instance-01 "lsof -tc java | xargs --no-run-if-empty kill -9"
ssh sven@instance-02 "lsof -tc java | xargs --no-run-if-empty kill -9"
ssh sven@instance-03 "lsof -tc java | xargs --no-run-if-empty kill -9"
echo ""

# Wait for everything to start
sleep 5;
echo "Fetching logs..."
mkdir -p logs
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/master.log" > logs/master.log
sleep 1;
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/replicaA.log" > logs/replicaA.log
sleep 1;
ssh sven@instance-02 "cat ${HOME_DIR}/${LOG_DIR}/replicaB.log" > logs/replicaB.log
sleep 1;
ssh sven@instance-03 "cat ${HOME_DIR}/${LOG_DIR}/replicaC.log" > logs/replicaC.log
sleep 1;

echo "Done!"