name := "ubi"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.1"

maintainer := "83225506@qq.com"

updateOptions := updateOptions.value.withCachedResolution(true)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),

  /*for boostrap for play*/
  Resolver.sonatypeRepo("snapshots"),

  /*for Silhouette*/
  "Atlassian Releases" at "https://maven.atlassian.com/public/"
)

lazy val slickVersion = "3.3.2"
lazy val playSlickVersion = "4.0.2"
lazy val playSilhouette = playSilhouette

libraryDependencies ++= Seq(
  guice,
  ws,
  filters,
  specs2,
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  /** database */
  jdbc,
  evolutions,
  "mysql" % "mysql-connector-java" % "8.0.19",
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.typesafe.play" %% "play-slick" % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,

  /** redis */
  cacheApi,
  "com.github.etaty" %% "rediscala" % "1.9.0",

  /** auth */
  "com.mohiva" %% "play-silhouette" % playSilhouette,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % playSilhouette,
  "com.mohiva" %% "play-silhouette-crypto-jca" % playSilhouette,
  "com.mohiva" %% "play-silhouette-persistence" % playSilhouette,
  "com.mohiva" %% "play-silhouette-testkit" % playSilhouette % "test",
  "com.mohiva" %% "play-silhouette-cas" % playSilhouette,
  "com.mohiva" %% "play-silhouette-totp" % playSilhouette,

  /** weixin api*/
  "com.github.binarywang" % "weixin-java-mp" % "3.2.0",
  "com.github.binarywang" % "weixin-java-pay" % "3.2.0",
  "com.github.binarywang" % "weixin-java-open" % "3.2.0",
  "com.github.binarywang" % "weixin-java-miniapp" % "3.2.0"
)