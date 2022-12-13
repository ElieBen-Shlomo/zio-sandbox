# To Reproduce Issue

A repo to demonstrate that issue https://github.com/zio/zio-protoquill/issues/217 can occur even with ZIO version < 2.0.3

Here we use zio (2.0.2) and zio-http (0.0.3) to reproduce the problem.

To reproduce:
```
bash setup.sh
```
Should result in - after allowing the Cassandra a minute to initialize - the following error
```
timestamp=2022-12-13T21:47:09.216125Z level=ERROR thread=#zio-fiber-0 message="" cause="Exception in thread "zio-fiber-6,4" java.lang.Error: Defect in zio.ZEnvironment: Could not find Quill::Cassandra[+Literal] inside ZEnvironment(Quill$::Cassandra[+Literal] -> Cassandra(io.getquill.Literal$@5a18c1b1,CassandraZioSession(com.datastax.oss.driver.internal.core.session.DefaultSession@33024403,100)))
	at zio.ZEnvironment$$anon$1.get(ZEnvironment.scala:167)
	at zio.ZIO$ServiceWithZIOPartiallyApplied$.apply$extension$$anonfun$13$$anonfun$1(ZIO.scala:5075)
	at main.Data.QueryService.live(Data.scala:38)
	at main.Main.run(Main.scala:22)
	at main.Main.run(Main.scala:22)
	at main.Main.run(Main.scala:22)
	at main.Main.run(Main.scala:22)
	at main.Main.run(Main.scala:22)
	at main.Main.run(Main.scala:22)
	at main.Main.run(Main.scala:22)
	at main.Main.run(Main.scala:22)"
```

Comment: Interestingly enough, when I switch zio-http to the `io.d11` branch (https://mvnrepository.com/artifact/io.d11/zhttp) with `2.0.0-RC11`, I notice that using zio < 2.0.3 does in fact solve the problem. However, when using the stable branch (`dev.zio` 0.0.3) it does not.




Disclaimer: Reproduced only on MacOS