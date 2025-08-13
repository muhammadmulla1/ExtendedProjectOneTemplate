lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-template",
    scalaVersion := "2.13.8",

    resolvers += "HMRC-open-artefacts-maven2" at "https://open.artefacts.tax.service.gov.uk/maven2",

    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play"      %% "play-ws"               % "2.8.19",
      "com.typesafe.play"      %% "play-ahc-ws"           % "2.8.19",
      "org.mongodb.scala"      %% "mongo-scala-driver"    % "4.9.0",
      "uk.gov.hmrc.mongo"      %% "hmrc-mongo-play-28"    % "0.63.0",
      "com.typesafe.play"      %% "play-json"             % "2.9.2",
      "org.typelevel"          %% "cats-core"             % "2.3.0",

      // Testing libraries
      "org.scalatest"          %% "scalatest"             % "3.2.15" % Test,
      "org.scalamock"          %% "scalamock"             % "5.2.0"  % Test,
      "org.scalatestplus.play" %% "scalatestplus-play"    % "5.1.0"  % Test
    )
  )
