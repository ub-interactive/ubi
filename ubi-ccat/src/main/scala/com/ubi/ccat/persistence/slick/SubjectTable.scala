package com.ubi.ccat.persistence.slick

import java.util.UUID

final case class SubjectEntity(
  subjectId: UUID,
  title: String
)

trait SubjectTable {

  self: Tables =>

  import profile.api._

  class SubjectTable(tag: Tag) extends Table[SubjectEntity](tag, Some(Tables.SCHEMA_NAME), "subject") {
    def * = {
      (subjectId,
        title
      ) <> ((SubjectEntity.apply _).tupled, SubjectEntity.unapply)
    }

    def subjectId = {
      column[UUID]("subject_id", O.PrimaryKey)
    }

    def title = {
      column[String]("title")
    }
  }

  object SubjectRepository {
    lazy val rows: TableQuery[SubjectTable] = TableQuery[SubjectTable]
  }

}
