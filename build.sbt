val scalaV = "2.12.2"

lazy val server = (project in file("server")).settings(
    scalaVersion := scalaV,
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
        "com.vmunier" %% "scalajs-scripts" % "1.1.1",
        guice,
        specs2 % Test,
        "com.typesafe.slick" %% "slick" % "3.2.1",
        "org.slf4j" % "slf4j-nop" % "1.6.4",
        "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
        "org.postgresql" % "postgresql" % "42.0.0",
        "org.slf4j" % "slf4j-nop" % "1.6.4",
        "com.lihaoyi" %%% "upickle" % "0.4.4"
    ),
    // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
    EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(PlayScala).
    dependsOn(sharedJvm)

val scalaCSSVersion     = "0.5.3"

lazy val client = (project in file("client")).settings(
    scalaVersion := scalaV,
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-dom" % "0.9.1",
        "com.github.japgolly.scalajs-react" %%% "core" % "1.1.0",
        "com.github.japgolly.scalajs-react" %%% "extra" % "1.1.0",
        "com.github.japgolly.scalacss" %%% "core"      % scalaCSSVersion,
        "com.github.japgolly.scalacss" %%% "ext-react" % scalaCSSVersion,
        "com.lihaoyi" %%% "upickle" % "0.4.4"
    ),
    jsDependencies ++= Seq(
        "org.webjars.bower" % "react" % "15.3.2" / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
        "org.webjars.bower" % "react" % "15.3.2" / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
        "org.webjars.bower" % "react" % "15.3.2" / "react-dom-server.js" minified  "react-dom-server.min.js" dependsOn "react-dom.js" commonJSName "ReactDOMServer"
    )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
    dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
    settings(scalaVersion := scalaV).
    jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
