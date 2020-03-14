package com.ubi.ccat.persistence.slick

object Tables extends Tables {
  val SCHEMA_NAME = "ubi_ccat"
  val profile = slick.jdbc.MySQLProfile
}

trait Tables
  extends CourseTable
    with SubjectTable
    with CourseSubjectTable
    with HomeSubjectTable {

  val profile: slick.jdbc.JdbcProfile

  import profile.api._

  lazy val schema: profile.SchemaDescription = List(
    CourseRepository.rows.schema,
    SubjectRepository.rows.schema,
    CourseSubjectRepository.rows.schema,
    HomeSubjectRepository.rows.schema
  ).reduceLeft(_ ++ _)

  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl: profile.DDL = {
    schema
  }
}
