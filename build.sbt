lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "google-books",
    scalaVersion := "2.13.8",
    resolvers += "HMRC-open-artefacts-maven2" at "https://open.artefacts.tax.service.gov.uk/maven2",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.63.0",
      "org.typelevel" %% "cats-core" % "2.3.0",
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "org.scalamock" %% "scalamock" % "5.2.0" % Test,
      "org.mockito" %% "mockito-scala" % "1.17.29" % Test,
      "org.mockito" %% "mockito-scala-scalatest" % "1.17.29" % Test
    )
  )
