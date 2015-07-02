

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random


trait ElevatorControlSystem {
  def status(): Future[Seq[(Int, Int, Seq[Int])]]

  def pickup(floor: Int, dir: Int)

  def step()
}


class ControlSystem(val elevNo: Int) extends ElevatorControlSystem {

  import ControlSystemAsync._

  val context = TypedActor.context
  val controller: ActorRef =
    context.actorOf(Props(classOf[ControlSystemAsync], elevNo), "controller")


  implicit val timeout = Timeout(10 seconds)

  override def status(): Future[Seq[(Int, Int, Seq[Int])]] =
    (controller ? StateRequest).mapTo[Seq[(Int, Int, Seq[Int])]]


  override def pickup(floor: Int, dir: Int): Unit = controller ! PickUpRequest(floor, dir)

  override def step(): Unit = controller ! Step


}


object ControlSystemAsync {

  case object StateRequest

  case class State(elevatorId: Int, curFloor: Int, goals: List[Int])

  case class PickUpRequest(pickupFloor: Int, dir: Int)

  case object Step

}

// TODO: No of floors
class ControlSystemAsync(val elevatorsNo: Int) extends Actor {

  import ControlSystemAsync._

  def createElevator(id: Int) = context.actorOf(Elevator.props(id))

  val elevators: List[ActorRef] = List.range(1, elevatorsNo + 1) map createElevator

  def chooseElevator: Int = Random.nextInt(elevatorsNo)


  override def receive: Receive = {
    case StateRequest =>
      context.become(awaitingForStates(sender(), elevatorsNo - 1, Nil))
      elevators.foreach(_ ! StateRequest)

    case req@PickUpRequest(floor, dir) =>
      elevators(chooseElevator) ! req

    case Step => elevators.foreach(_ ! Step)

    case x =>
  }

  def awaitingForStates(client: ActorRef, awaitForStatesNo: Int, states: List[(Int, Int, List[Int])]): Receive = {
    case State(id, floor, goals) =>
      if (awaitForStatesNo > 0)
        context.become(awaitingForStates(client, awaitForStatesNo - 1, (id, floor, goals) :: states))
      else {
        client ! (id, floor, goals) :: states
        context.become(receive)
      }

    case x => self tell(x, sender())

  }
}