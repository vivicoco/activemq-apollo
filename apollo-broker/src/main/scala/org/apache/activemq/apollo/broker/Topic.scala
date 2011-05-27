/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.apollo.broker

import org.apache.activemq.apollo.util._
import scala.collection.immutable.List
import org.apache.activemq.apollo.util.path.Path
import org.apache.activemq.apollo.dto._
import security.SecurityContext
import collection.mutable.{HashMap, ListBuffer}

/**
 * <p>
 * A logical messaging topic
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class Topic(val router:LocalRouter, val destination_dto:TopicDestinationDTO, val config:TopicDTO, val id:String) extends DomainDestination {

  var producers = ListBuffer[BindableDeliveryProducer]()
  var consumers = ListBuffer[DeliveryConsumer]()
  var durable_subscriptions = ListBuffer[Queue]()
  var consumer_queues = HashMap[DeliveryConsumer, Queue]()

  import OptionSupport._

  def virtual_host: VirtualHost = router.virtual_host

  def slow_consumer_policy = config.slow_consumer_policy.getOrElse("block")

  def bind (destination: DestinationDTO, consumer:DeliveryConsumer) = {
    destination match {
      case null=> // unified queue case

        consumers += consumer
        val list = List(consumer)
        producers.foreach({ r=>
          r.bind(list)
        })

      case destination:TopicDestinationDTO=>
        var target = consumer
        slow_consumer_policy match {
          case "queue" =>

            // create a temp queue so that it can spool
            val queue = router._create_queue(new TempQueueBinding(consumer))
            queue.dispatch_queue.setTargetQueue(consumer.dispatch_queue)
            queue.bind(List(consumer))

            consumer_queues += consumer->queue
            target = queue

          case "block" =>
            // just have dispatcher dispatch directly to them..
        }

        consumers += target
        val list = target :: Nil
        producers.foreach({ r=>
          r.bind(list)
        })

    }
  }

  def unbind (consumer:DeliveryConsumer, persistent:Boolean) = {

    consumer_queues.remove(consumer) match {
      case Some(queue)=>

        queue.unbind(List(consumer))

        queue.binding match {
          case x:TempQueueBinding =>

            val list = List(queue)
            producers.foreach({ r=>
              r.unbind(list)
            })
            router._destroy_queue(queue.id, null)

        }

      case None=>

        // producers are directly delivering to the consumer..
        val original = consumers.size
        consumers -= consumer
        if( original!= consumers.size ) {
          val list = List(consumer)
          producers.foreach({ r=>
            r.unbind(list)
          })
        }
    }

  }

  def bind_durable_subscription(destination: DurableSubscriptionDestinationDTO, queue:Queue)  = {
    if( !durable_subscriptions.contains(queue) ) {
      durable_subscriptions += queue
      val list = List(queue)
      producers.foreach({ r=>
        r.bind(list)
      })
      consumer_queues.foreach{case (consumer, q)=>
        if( q==queue ) {
          bind(destination, consumer)
        }
      }
    }
  }

  def unbind_durable_subscription(destination: DurableSubscriptionDestinationDTO, queue:Queue)  = {
    if( durable_subscriptions.contains(queue) ) {
      durable_subscriptions -= queue
      val list = List(queue)
      producers.foreach({ r=>
        r.unbind(list)
      })
      consumer_queues.foreach{case (consumer, q)=>
        if( q==queue ) {
          unbind(consumer, false)
        }
      }
    }
  }

  def connect (destination:DestinationDTO, producer:BindableDeliveryProducer) = {
    producers += producer
    producer.bind(consumers.toList ::: durable_subscriptions.toList)
  }

  def disconnect (producer:BindableDeliveryProducer) = {
    producers = producers.filterNot( _ == producer )
    producer.unbind(consumers.toList ::: durable_subscriptions.toList)
  }

}
