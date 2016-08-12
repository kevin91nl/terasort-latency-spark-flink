package kevin91nl.unduplicate

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Milliseconds, Seconds, StreamingContext}

// < /dev/urandom tr -dc 01 | fold -w2 | while read line; do echo "$line" `date +%s%3N` ; done | nc -lk 8888

/**
  * Created by kevin on 11.08.16.
  */
object SparkUnduplicate {

    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setMaster("local[2]").setAppName("SparkUnduplicate")
        val ssc = new StreamingContext(conf, Milliseconds(50))

        ssc.socketTextStream("localhost", 8888)
            .map((item: String) => {
                val Array(word, timestamp) = item.split(" ")
                System.currentTimeMillis() - timestamp.toLong
            })
                    .print()

        ssc.start()
        ssc.awaitTermination()

    }

}
