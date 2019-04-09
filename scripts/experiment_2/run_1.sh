#!/bin/sh
HOST_IP="35.246.243.109"
RMI_IP="10.156.0.2"
LOG_DIR="logs/experiment_1/instances_1"
HOME_DIR="distributed_systems/out/production/rmi-tact"

REPLICAS=(ReplicaA ReplicaB ReplicaC ReplicaD ReplicaE ReplicaF ReplicaG ReplicaH ReplicaI)
READWRITE=(read write)
LETTERS=(x y z)

#########################################################################
#                                                                       #
# Initalize experiment                                                  #
#                                                                       #
#########################################################################

echo "Initialize experiment"

# Initalize instance-01
echo "=> Setup instance-01"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    rm -rf ${LOG_DIR};
    mkdir -p ${LOG_DIR}
"

# Initalize instance-02
echo "=> Setup instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    rm -rf ${LOG_DIR};
    mkdir -p ${LOG_DIR}
"

# Initalize instance-03
echo "=> Setup instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    rm -rf ${LOG_DIR};
    mkdir -p ${LOG_DIR}
"
echo ""

#########################################################################
#                                                                       # 
# Start master and 2 replicas                                           #
#                                                                       #
#########################################################################

# Master
echo "Start master and replicas"
echo "=> Start master on instance-01"
ssh sven@instance-01 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala -Djava.rmi.server.hostname=${HOST_IP} main.scala.history.MasterReplica > ${LOG_DIR}/master.log 2>&1 &
"

# Instance-01
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
# Instance-02
echo "=> Start Replica C on instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} C > ${LOG_DIR}/replicaC.log 2>&1 &
"
echo "=> Start Replica D on instance-02"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} D > ${LOG_DIR}/replicaD.log 2>&1 &
"

# Instance-03
echo "=> Start Replica E on instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} E > ${LOG_DIR}/replicaE.log 2>&1 &
"
echo "=> Start Replica F on instance-03"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    nohup scala main.scala.replica.TactReplica ${RMI_IP} F > ${LOG_DIR}/replicaF.log 2>&1 &
"

# Wait for everything to start
sleep 5

#########################################################################
#                                                                       # 
# Write simulation                                                      #
#                                                                       #
#########################################################################

# Random writes to the replicas
echo "Random reads and writes"
cd ~/Development/Other/rmi-tact/out/production/rmi-tact/
for i in {1..250}
do
    REPLICA="ReplicaA"
    READORWRITE="write"

    RND_LETTERS=$((RANDOM % 3))
    LETTER=${LETTERS[$RND_LETTERS]}

    echo "($i/250)"
    echo "=> Action: ${READORWRITE} ${LETTER} at ${REPLICA}"

    ssh sven@instance-01 "
        source /home/sven/.sdkman/bin/sdkman-init.sh;
        cd ${HOME_DIR};
        scala main.scala.client.Client ${HOST_IP} ${REPLICA} ${READORWRITE} ${LETTER} 1;
    "

    echo ""
done
echo ""

#########################################################################
#                                                                       # 
# Fetching results                                                      #
#                                                                       #
#########################################################################

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
"
echo "=> Replica D, E and F"
ssh sven@instance-02 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    scala main.scala.client.Client ${HOST_IP} ReplicaD read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaD read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaD read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaE read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaE read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaE read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaF read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaF read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaF read z;
"
echo "=> Replica G, H and I"
ssh sven@instance-03 "
    source /home/sven/.sdkman/bin/sdkman-init.sh;
    cd ${HOME_DIR};
    scala main.scala.client.Client ${HOST_IP} ReplicaG read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaG read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaG read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaH read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaH read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaH read z;
    scala main.scala.client.Client ${HOST_IP} ReplicaI read x;
    scala main.scala.client.Client ${HOST_IP} ReplicaI read y;
    scala main.scala.client.Client ${HOST_IP} ReplicaI read z;
"
echo ""

#########################################################################
#                                                                       #
# Kill the master and replicas                                          #
#                                                                       #
#########################################################################

echo "Stop the master and all the replicas..."
ssh sven@instance-01 "lsof -tc java | xargs --no-run-if-empty kill -9"
ssh sven@instance-02 "lsof -tc java | xargs --no-run-if-empty kill -9"
ssh sven@instance-03 "lsof -tc java | xargs --no-run-if-empty kill -9"
echo ""


#########################################################################
#                                                                       #
# Fetch generated logs                                                  #
#                                                                       #
#########################################################################

sleep 5;
echo "Fetching logs..."
mkdir -p logs/run_2/
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/master.log" > logs/run_2/master.log

ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/replicaA.log" > logs/run_2/replicaA.log
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/replicaB.log" > logs/run_2/replicaB.log
ssh sven@instance-01 "cat ${HOME_DIR}/${LOG_DIR}/replicaC.log" > logs/run_2/replicaC.log

ssh sven@instance-02 "cat ${HOME_DIR}/${LOG_DIR}/replicaD.log" > logs/run_2/replicaD.log
ssh sven@instance-02 "cat ${HOME_DIR}/${LOG_DIR}/replicaE.log" > logs/run_2/replicaE.log
ssh sven@instance-02 "cat ${HOME_DIR}/${LOG_DIR}/replicaF.log" > logs/run_2/replicaF.log

ssh sven@instance-03 "cat ${HOME_DIR}/${LOG_DIR}/replicaG.log" > logs/run_2/replicaG.log
ssh sven@instance-03 "cat ${HOME_DIR}/${LOG_DIR}/replicaH.log" > logs/run_2/replicaH.log
ssh sven@instance-03 "cat ${HOME_DIR}/${LOG_DIR}/replicaI.log" > logs/run_2/replicaI.log

echo "Done!"