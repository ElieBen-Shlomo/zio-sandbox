import io.getquill._
import io.getquill.cassandrazio.Quill
import zio.Console.printLine
import zio.{ZIO, ZIOAppDefault, ZLayer}

import java.io.IOException
import java.sql.Timestamp
import java.time.Instant

object Data {

  case class User(
    username: String,
    date_and_time: Instant,
    message: String,
    session_id: String,
    status: String // replace with enum
  )

  case class QueryService(quill: Quill.Cassandra[Literal]) {
    import quill.*

    inline def people = quote {
      query[User]
    }
    inline def peopleByName = quote {
      (name: String) => people.filter(p => p.username == name).allowFiltering
    }
    inline def insertPerson = quote {
      (user: User) => query[User].insertValue(user)
    }
  }

  object QueryService {
    def live: ZLayer[Quill.Cassandra[Literal], Nothing, QueryService] =
      ZLayer.fromFunction(QueryService(_))
  }

  case class DataService(queryService: QueryService) {

    import queryService.quill
    import queryService.quill.*

    def getPeople(): ZIO[Any, Throwable, List[User]] = {
      quill.run(queryService.people)
    }

    def getPeopleByName(name: String): ZIO[Any, Throwable, List[User]] = quill.run(queryService.peopleByName(lift(name)))

    def insertPerson(user: User): ZIO[Any, Throwable, Unit] = {
      quill.run(queryService.insertPerson(lift(user)))
    }
  }

  object DataService {
    def getPeople(): ZIO[DataService, Throwable, List[User]] =
      ZIO.serviceWithZIO[DataService](_.getPeople())

    def getPeopleByName(name: String): ZIO[DataService, Throwable, List[User]] =
      ZIO.serviceWithZIO[DataService](_.getPeopleByName(name))

    def insertPerson(user: User): ZIO[DataService, Throwable, Unit] =
      ZIO.serviceWithZIO[DataService](_.insertPerson(user))

    def live: ZLayer[QueryService, Nothing, DataService] =
      ZLayer.fromFunction(DataService(_))
  }
}