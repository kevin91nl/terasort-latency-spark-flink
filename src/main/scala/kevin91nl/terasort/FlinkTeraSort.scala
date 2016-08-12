package kevin91nl.terasort

import org.apache.flink.api.common.functions.Partitioner
import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.java.hadoop.mapreduce.HadoopOutputFormat
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job

class OptimizedFlinkTeraPartitioner(underlying: TotalOrderPartitioner) extends Partitioner[Text] {
    def partition(key: Text, numPartitions: Int): Int = {
        underlying.getPartition(key)
    }
}


object FlinkTeraSort {

    implicit val textOrdering = new Ordering[Text] {
        override def compare(a: Text, b: Text) = a.compareTo(b)
    }

    def main(args: Array[String]) {
        if (args.size != 4) {
            println("Usage: FlinkTeraSort hdfs inputPath outputPath #partitions ")
            return
        }

        val env = ExecutionEnvironment.getExecutionEnvironment
        //val env = ExecutionEnvironment.createRemoteEnvironment("188.184.93.239", 43368, "/home/kevin/projects/terasort/target/scala-2.10/terasort_2.10-0.0.1.jar")
        env.getConfig.enableObjectReuse()

        val hdfs = args(0)
        val inputPath = hdfs + args(1)
        val outputPath = hdfs + args(2)
        val partitions = args(3).toInt

        val mapredConf = new JobConf()
        mapredConf.set("fs.defaultFS", hdfs)
        mapredConf.setBoolean("fs.hdfs.impl.disable.cache", true)
        mapredConf.set("mapreduce.input.fileinputformat.inputdir", inputPath)
        mapredConf.set("mapreduce.output.fileoutputformat.outputdir", outputPath)
        mapredConf.setInt("mapreduce.job.reduces", partitions)

        val partitionFile = new Path(outputPath, TeraInputFormat.PARTITION_FILENAME)
        val jobContext = Job.getInstance(mapredConf)
        TeraInputFormat.writePartitionFile(jobContext, partitionFile)
        val partitioner = new OptimizedFlinkTeraPartitioner(new TotalOrderPartitioner(mapredConf, partitionFile))

        env
            .readHadoopFile(new TeraInputFormat(), classOf[Text], classOf[Text], inputPath)
            .partitionCustom(partitioner, 0).sortPartition(0, Order.ASCENDING)
            .output(new HadoopOutputFormat[Text, Text](new TeraOutputFormat(), jobContext))

        env.execute("TeraSort")
    }
}
