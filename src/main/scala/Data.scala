package cassandra_quill

import io.getquill._
import io.getquill.ast.Ast
import io.getquill.cassandrazio.Quill
import zio.Console.printLine
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

import java.time.Instant

object Data {

  case class User(
      username: String,
      dateAndTime: Instant,
      message: String,
      sessionId: String,
      status: String
  )

  case class QueryService(quill: Quill.Cassandra[SnakeCase]) {
    import quill._
    def people = quote {
      query[User]
    }

    def peopleByName = quote { (name: String) =>
      people.filter(p => p.username == name).allowFiltering
    }

    def insertPerson = quote { (user: User) =>
      query[User].insertValue(user)
    }

  }
  object QueryService {
    def live: ZLayer[Quill.Cassandra[SnakeCase], Nothing, QueryService] =
      ZLayer.fromFunction(QueryService(_))
  }

  case class DataService(queryService: QueryService) {
    import queryService.quill._
    import queryService.quill
    def getPeople: ZIO[Any, Throwable, List[User]] = quill.run(queryService.people)
    def getPeopleByName(name: String): ZIO[Any, Throwable, List[User]] =
      quill.run(queryService.peopleByName(lift(name)))
    def insertPerson(user: User): ZIO[Any, Throwable, Unit] = {
      quill.run(queryService.insertPerson(lift(user)))
    }
  }

  object DataService {
    def getPeople: ZIO[DataService, Throwable, List[User]] =
      ZIO.serviceWithZIO[DataService](_.getPeople)
    def getPeopleByName(name: String): ZIO[DataService, Throwable, List[User]] =
      ZIO.serviceWithZIO[DataService](_.getPeopleByName(name))
    def insertPerson(user: User): ZIO[DataService, Throwable, Unit] =
      ZIO.serviceWithZIO[DataService](_.insertPerson(user))

    def live: ZLayer[QueryService, Nothing, DataService] =
      ZLayer.fromFunction(DataService(_))
  }

}
