package subscriber

import com.typesafe.scalalogging.StrictLogging
import io.lettuce.core.RedisClient

import scala.util.{Try, Using}

class RedisSubscriber extends StrictLogging {
  def subscribe(): Try[Unit] = {
    val redisClient: RedisClient = RedisClient.create("redis://localhost:6379")

    Using(redisClient.connectPubSub()) { conn =>
      try {
        val reactiveCommands = conn.reactive()
        reactiveCommands.subscribe("channel").block()

        val disposable = reactiveCommands
          .observeChannels()
          .doOnNext(channelMessage =>
            logger.info(
              String.format(
                "[%s] received message = %s",
                channelMessage.getChannel,
                channelMessage.getMessage
              )
            )
          )
          .subscribe()

        logger.info("start subscribe")

        System.console.readLine("> Enter stop.")

        reactiveCommands.unsubscribe("channel")
        disposable.dispose()
      } finally {
        redisClient.shutdown()
      }
    }
  }
}

object RedisSubscriber extends App with StrictLogging {
  val subscriber = new RedisSubscriber()
  subscriber.subscribe()
}
