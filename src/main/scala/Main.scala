

import akka.actor.{ActorSystem, TypedActor, TypedProps}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App {

  val system = ActorSystem("Elevators")

  val elevatorControlSystem : ElevatorControlSystem =
  TypedActor(system).typedActorOf(TypedProps(classOf[ElevatorControlSystem], new ControlSystem(3)))

  elevatorControlSystem.pickup(10, 1)
  elevatorControlSystem.pickup(3, -1)
  elevatorControlSystem.pickup(2, -1)
  elevatorControlSystem.pickup(4, 1)
  elevatorControlSystem.pickup(3, 1)
  elevatorControlSystem.pickup(5, 2)


  system.scheduler.schedule(0 seconds, 2 seconds, new Runnable {
    override def run(): Unit = elevatorControlSystem.step()
  })

  elevatorControlSystem.status().onSuccess{ case x =>
    println(x)
  }


  system.scheduler.schedule(0 seconds, 3 seconds, new Runnable {
    override def run(): Unit =
      for (status <- elevatorControlSystem.status()) yield println(status)
  })







}
