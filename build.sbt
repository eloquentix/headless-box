scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-Xlint:_",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard"
)

libraryDependencies ++= Seq(
  "org.seleniumhq.selenium"   % "selenium-htmlunit-driver" % "2.52.0",
  "org.apache.httpcomponents" % "httpclient"               % "4.5.2",
  "io.argonaut"              %% "argonaut"                 % "6.1"
)
