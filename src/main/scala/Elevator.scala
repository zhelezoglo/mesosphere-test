import akka.actor.{Actor, Props}

import scala.collection.mutable


object Elevator {

  def props(id: Int) = Props(classOf[Elevator], id)

}

class Elevator(val id: Int) extends Actor {

  import ControlSystemAsync._


  private var curFloor: Int = 1
  private var curGoalFloor: Int = 1

  // TODO: refactor to strategy trait

  private val goals = mutable.Queue[Int]()

  def addGoal(floor: Int, dir: Int): Unit = goals.enqueue(floor)

  def nextFloor(): Unit = {
    if (goals.isEmpty) ()
    else
      if (curFloor == curGoalFloor) curGoalFloor = goals.dequeue()

    if (curGoalFloor < curFloor) {
      curFloor = curFloor - 1
    } else if (curGoalFloor > curFloor) {
      curFloor = curFloor + 1
    } else ()
  }

  def getGoals = curGoalFloor :: goals.toList


  override def receive: Receive = {
    case StateRequest =>
      sender() ! State(id, curFloor, getGoals)
    case p @ PickUpRequest(floor, dir) =>
      addGoal(floor, dir)
    case Step =>
      nextFloor()
  }
}
