package com.ubi.api.web.persistence.slick

final case class ClassEntity(
  hotelId: String,
  projectId: String
)

trait ClassTable {

  self: Tables =>

  import profile.api._

  class ClassTable(tag: Tag) extends Table[ClassEntity](tag, Some(Tables.SCHEMA_NAME), "hotel") {
    def * = {
      (hotelId, projectId) <> ((ClassEntity.apply _).tupled, ClassEntity.unapply)
    }

    def hotelId: Rep[String] = {
      column[String]("hotel_id", O.Length(255, varying = true), O.PrimaryKey, O.SqlType("NVARCHAR"))
    }

    def projectId: Rep[String] = {
      column[String]("project_id", O.Length(255, varying = true), O.SqlType("NVARCHAR"))
    }

  }

  object ClassRepository {
    lazy val rows: TableQuery[ClassTable] = TableQuery[ClassTable]
  }

}
