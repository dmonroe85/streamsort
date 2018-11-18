package streamsort.merge

import streamsort.queue.DataQueue

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer

class Merger[T](nextInSequence: Option[T => T] = None)(implicit ord: Ordering[T]) {

  var inputs: Map[Int, DataQueue[T]] = new HashMap()

  def getQs(is: List[Int]): Set[DataQueue[T]] = {
    is.map(inputs(_)).toSet
  }

  def createInput(newQ: DataQueue[T]): Unit = {
    val maxI = if (inputs.keys.nonEmpty) inputs.keys.max else 0
    inputs += (maxI + 1 -> newQ)
  }

  def enqueue(i: Int)(v: T): Unit = {
    inputs(i).enqueue(v)
  }

  def findMins(lastVal: T): Set[DataQueue[T]] = {
    findMins(Some(lastVal))
  }

  def findMins(lastVal: Option[T] = None): Set[DataQueue[T]] = {
    val matchVal =
      lastVal match {
        case None =>
          if (inputs.forall{ case(i, q) => q.peek().nonEmpty }) {
            val (minI, minQ): (Int, DataQueue[T]) = inputs.minBy{ case (_, q) => q.peek().get }
            minQ.peek()
          } else {
            None
          }

        case x => x
      }

    inputs.collect{ case(i, q) if q.peek().nonEmpty && q.peek() == matchVal => q }.toSet
  }

  def merge(): List[T] = {
    if (nextInSequence.nonEmpty) {
      // Merges until at least one input has a null value
      var lb: ListBuffer[T] = ListBuffer() ++ mergeWithLastVal()

      // Continues merging as long as there are sequential values to take
      var tmpOut: List[T] = List()
      do {
        tmpOut = mergeWithLastVal(nextInSequence.get(lb.last))
        lb ++= tmpOut
      } while (tmpOut.nonEmpty)

      lb.toList
    } else {
      mergeWithLastVal()
    }
  }

  def mergeWithLastVal(lastVal: Option[T] = None): List[T] = {
    var qs = List[DataQueue[T]]()
    val out = ListBuffer[T]()

    do {
      qs = findMins(lastVal).toList

      val minVal = qs.headOption.map(_.peek().get)

      out ++=
        qs.flatMap(q => {
          val lb = ListBuffer[T]()
          while (q.peek() == minVal) {
            lb += q.dequeue().get
          }
          lb
        })

    } while (qs.nonEmpty)

    out.toList
  }

  def mergeWithLastVal(lastVal: T): List[T] = {
    mergeWithLastVal(Some(lastVal))
  }

}

object Merger {

  def apply[T](inputs: List[DataQueue[T]] = List(),
               nextInSequence: Option[T => T] = None)(implicit ord: Ordering[T]): Merger[T] = {
    val m = new Merger[T](nextInSequence)
    inputs.foreach(m.createInput)
    m
  }

}