import com.typesafe.scalalogging.StrictLogging
import io.lettuce.core.RedisClient
import reactor.core.scala.publisher.{SFlux => Flux}
import reactor.core.scheduler.Schedulers

import scala.util.{Try, Using}
import scala.concurrent.duration._
import scala.language.postfixOps

class RedisPublisher extends StrictLogging {
  def publish(): Try[Unit] = {
    val redisClient: RedisClient = RedisClient.create("redis://localhost:6379")
    Using(redisClient.connectPubSub()) { conn =>
      try {
        val reactiveCommands = conn.reactive()
        val disposable = Flux
          .interval(1 second)
          .subscribe(count =>
            reactiveCommands
              .publish("channel", "message-" + count)
              .subscribe()
          )
        System.console.readLine("> Enter stop.")

        disposable.dispose()
        Schedulers.shutdownNow()
      } finally {
        redisClient.shutdown()
      }
    }
  }
}

object RedisPublisher extends App {
  val publisher = new RedisPublisher
  publisher.publish()
}
