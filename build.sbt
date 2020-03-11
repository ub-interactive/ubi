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
lazy val playSilhouetteVersion = "7.0.0"
lazy val weixinJavaVersion = "3.5.0"

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
  "com.mohiva" %% "play-silhouette" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-crypto-jca" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-persistence" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-testkit" % playSilhouetteVersion % "test",
  "com.mohiva" %% "play-silhouette-cas" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-totp" % playSilhouetteVersion,

  /** weixin api*/
  "com.github.binarywang" % "weixin-java-mp" % weixinJavaVersion,
  "com.github.binarywang" % "weixin-java-pay" % weixinJavaVersion,
  "com.github.binarywang" % "weixin-java-open" % weixinJavaVersion,
  "com.github.binarywang" % "weixin-java-miniapp" % weixinJavaVersion
)