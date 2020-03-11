package com.ubi.api.web.persistence.slick

object Tables extends Tables {
  val SCHEMA_NAME = "ubi"
  val profile = slick.jdbc.MySQLProfile
}

trait Tables
  extends ClassTable {

  val profile: slick.jdbc.JdbcProfile

  import profile.api._

  lazy val schema: profile.SchemaDescription = List(
    ClassRepository.rows.schema,
  ).reduceLeft(_ ++ _)

  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl: profile.DDL = {
    schema
  }
}
