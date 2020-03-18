
name := "ubi"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.1"

maintainer := "83225506@qq.com"

updateOptions := updateOptions.value.withCachedResolution(true)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)

resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),

  /*for boostrap for play*/
  Resolver.sonatypeRepo("snapshots"),

  /*for Silhouette*/
  "Atlassian Releases" at "https://maven.atlassian.com/public/"
)

lazy val slickVersion = "3.3.2"
lazy val playSlickVersion = "4.0.2"
lazy val playSilhouetteVersion = "7.0.0"
lazy val weixinJavaVersion = "3.5.0"

lazy val `ubi` = project in file(".")

/* ubi */
lazy val `ubi-ccat` = (project in file("ubi-ccat"))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    scalaVersion := "2.13.1",
    maintainer := "83225506@qq.com",
    sources in(Compile, doc) := Seq.empty,
    publishArtifact in(Compile, packageDoc) := false,
    organization := "com.ubi.api",
    name := "ubi-ccat",
    libraryDependencies ++= Seq(
      lagomScaladslClient,
      guice,
      ws,
      filters,
      specs2,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.gnieh" % "logback-journal" % "0.3.0",

      /** database */
      //      jdbc,
      //      evolutions,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.typesafe.slick" %% "slick" % slickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
      "com.typesafe.play" %% "play-slick" % playSlickVersion,
      "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,

      /** redis */
      cacheApi,
      "com.github.etaty" %% "rediscala" % "1.9.0",

      /** auth */
      "com.mohiva" %% "play-silhouette" % playSilhouetteVersion,
      "com.mohiva" %% "play-silhouette-password-bcrypt" % playSilhouetteVersion,
      "com.mohiva" %% "play-silhouette-crypto-jca" % playSilhouetteVersion,
      "com.mohiva" %% "play-silhouette-persistence" % playSilhouetteVersion,
      "com.mohiva" %% "play-silhouette-testkit" % playSilhouetteVersion % "test",
      "com.mohiva" %% "play-silhouette-cas" % playSilhouetteVersion,
      "com.mohiva" %% "play-silhouette-totp" % playSilhouetteVersion,

      /** weixin api */
      "com.github.binarywang" % "weixin-java-mp" % weixinJavaVersion,
      "com.github.binarywang" % "weixin-java-pay" % weixinJavaVersion,
      "com.github.binarywang" % "weixin-java-open" % weixinJavaVersion,
      "com.github.binarywang" % "weixin-java-miniapp" % weixinJavaVersion,

      "com.aliyun" % "aliyun-java-sdk-core" % "4.1.0"
    )
  )
  .dependsOn(`ubi-crm-api`)

/* crm */
lazy val `ubi-crm-api` = (project in file("ubi-crm-api"))
  .settings(scalaVersion := "2.13.1",
    maintainer := "83225506@qq.com",
    sources in(Compile, doc) := Seq.empty,
    publishArtifact in(Compile, packageDoc) := false,
    organization := "com.ubi.crm",
    name := "ubi-crm-api",
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `ubi-crm-impl` = (project in file("ubi-crm-impl"))
  .enablePlugins(LagomScala)
  .settings(
    scalaVersion := "2.13.1",
    maintainer := "83225506@qq.com",
    sources in(Compile, doc) := Seq.empty,
    publishArtifact in(Compile, packageDoc) := false,
    organization := "com.ubi.crm",
    name := "ubi-crm-impl",
    mappings in Universal ++= {
      mapFiles((resourceDirectory in Compile).value, "conf")
    },
    libraryDependencies ++= Seq(
      lagomScaladslKafkaBroker,
      lagomScaladslPersistenceJdbc,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided",
      "com.github.etaty" %% "rediscala" % "1.9.0",
      "org.gnieh" % "logback-journal" % "0.3.0",
    )
  )
  .dependsOn(`ubi-crm-api`)

/* order */
lazy val `ubi-order-api` = (project in file("ubi-order-api"))
  .settings(scalaVersion := "2.13.1",
    maintainer := "83225506@qq.com",
    sources in(Compile, doc) := Seq.empty,
    publishArtifact in(Compile, packageDoc) := false,
    organization := "com.ubi.order",
    name := "ubi-order-api",
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `ubi-order-impl` = (project in file("ubi-order-impl"))
  .enablePlugins(LagomScala)
  .settings(
    scalaVersion := "2.13.1",
    maintainer := "83225506@qq.com",
    sources in(Compile, doc) := Seq.empty,
    publishArtifact in(Compile, packageDoc) := false,
    organization := "com.ubi.order",
    name := "ubi-order-impl",
    mappings in Universal ++= {
      mapFiles((resourceDirectory in Compile).value, "conf")
    },
    libraryDependencies ++= Seq(
      lagomScaladslKafkaBroker,
      lagomScaladslPersistenceJdbc,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided",
      "com.github.etaty" %% "rediscala" % "1.9.0",
      "org.gnieh" % "logback-journal" % "0.3.0",
    )
  )
  .dependsOn(`ubi-order-api`)

/* finance */
lazy val `ubi-finance-api` = (project in file("ubi-finance-api"))
  .settings(scalaVersion := "2.13.1",
    maintainer := "83225506@qq.com",
    sources in(Compile, doc) := Seq.empty,
    publishArtifact in(Compile, packageDoc) := false,
    organization := "com.ubi.finance",
    name := "ubi-finance-api",
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `ubi-finance-impl` = (project in file("ubi-finance-impl"))
  .enablePlugins(LagomScala)
  .settings(
    scalaVersion := "2.13.1",
    maintainer := "83225506@qq.com",
    sources in(Compile, doc) := Seq.empty,
    publishArtifact in(Compile, packageDoc) := false,
    organization := "com.ubi.finance",
    name := "ubi-finance-impl",
    mappings in Universal ++= {
      mapFiles((resourceDirectory in Compile).value, "conf")
    },
    libraryDependencies ++= Seq(
      lagomScaladslKafkaBroker,
      lagomScaladslPersistenceJdbc,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided",
      "com.github.etaty" %% "rediscala" % "1.9.0",
      "org.gnieh" % "logback-journal" % "0.3.0",
    )
  )
  .dependsOn(`ubi-finance-api`)

/** helpers */
def mapFiles(
  sourceFolder: File,
  targetFolderName: String
): Seq[(File, String)] = {
  for {
    file <- (sourceFolder ** AllPassFilter).get
    relative <- file.relativeTo(sourceFolder)
    mapping = file -> (targetFolderName.stripSuffix("/") + "/" + relative.getPath)
  } yield mapping
}