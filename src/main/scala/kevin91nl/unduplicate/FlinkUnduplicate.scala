package kevin91nl.unduplicate

import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.common.functions.Partitioner
import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.java.hadoop.mapreduce.HadoopOutputFormat
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.flink.api.scala._
import org.apache.flink.streaming.util.serialization.SimpleStringSchema

import scala.collection.mutable

// < /dev/urandom tr -dc 01 | fold -w2 | while read line; do echo "$line" `date +%s%3N` ; done | nc -lk 8888

/**
  * Created by kevin on 11.08.16.
  */
object FlinkUnduplicate {

    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        env.socketTextStream("localhost", 8888)
            .keyBy(x => "a")
            .filterWithState((element: String, state: Option[Set[String]]) => {
                val Array(word, timestamp) = element.split(" ")
                state match {
                    case None => (true, Some(Set(word)))
                    case Some(seenElements) => (true, Some(seenElements + word))
                }
            })
            .map(item => {
                val Array(word, timestamp) = item.split(" ")
                System.currentTimeMillis() - timestamp.toLong
            })
            .print()

        env.execute("FlinkUnduplicate")
    }

}
