# TeraSort

TeraSort for Apache Spark and Flink.

# Build
Make sure to use Java 7. In order to build the package, execute the following code:

`sbt package`

# Run TeraSort using Spark on Yarn

The below shows an example of a bash script to execute TeraSort on Spark on top of Yarn.

```
#!/usr/bin/env bash

EXECUTORS=10
EXECUTOR_CORES=4
EXECUTOR_MEMORY=1G
SCALA_VERSION=2.10
HDFS=hdfs://[hdfs-master]:8020
INDIR=/experiment/input
OUTDIR=/experiment/spark_out
PARTITIONS=$[42*12]

spark-submit --master yarn --num-executors ${EXECUTORS} --executor-cores ${EXECUTOR_CORES} --executor-memory ${EXECUTOR_MEMORY} --class kevin91nl.terasort.SparkTeraSort target/scala-${SCALA_VERSION}/terasort_${SCALA_VERSION}-0.0.1.jar ${HDFS} ${INDIR} ${OUTDIR} ${PARTITIONS}
```


# Run TeraSort using Flink on Yarn

## execute job manager and task managers

The below shows an example of a command to execute job manager and 42 task managers.

```
yarn-session.sh -n 10 -tm 512
```

yarn-session.sh will show you the address of job manager via stdout as follows:
```
...
Flink JobManager is now running on slave1:33970
...
```

## Submit a TeraSort job to job manager 

The below shows an example of a bash script to execute TeraSort on Flink.

```
#!/usr/bin/env bash

JOBMANAGER=[slave]:33970
PARTITIONS=$[42*12]
SCALA_VERSION=2.10
HDFS=hdfs://[hdfs-master]:54321
INDIR=/experiment/input
OUTDIR=/experiment/spark_out

flink run -m ${JOBMANAGER} -p ${PARTITIONS} -c kevin91nl.terasort.FlinkTeraSort target/scala-${SCALA_VERSION}/terasort_${SCALA_VERSION}-0.0.1.jar ${HDFS} ${INDIR} ${OUTDIR} ${PARTITIONS}
```
