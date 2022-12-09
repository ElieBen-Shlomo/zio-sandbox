import io.getquill.cassandrazio.Quill.CassandraZioSession
import zio.ZLayer
import zio.*
import io.getquill.*
import io.getquill.cassandrazio.Quill
import Data.{DataService, QueryService, User}
import zio.Console.printLine

object DBDriver {
  def run: ZIO[DataService, Throwable, Unit] =
    for {
      people <- DataService.getPeople()
      Elie <- DataService.getPeopleByName("Elie")
      _ <- printLine(s"People: ${people}")
      _ <- printLine(s"Elie: ${Elie}")
//      _ <- DataService.insertPerson(User("New User", "Some Text"))
      peopleAgain <- DataService.getPeople()
      _ <- printLine(s"People again: ${peopleAgain}")
    } yield ()
}
