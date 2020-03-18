package com.ubi.ccat.entities

import java.util.UUID

import com.ubi.ccat.enums.SubjectDisplayStyleValue

final case class HomeSubjectEntity(
  subjectId: UUID,
  subjectDisplayStyle: SubjectDisplayStyleValue,
  displayOrder: Int
)

trait HomeSubjectTable {

  self: Tables =>

  import profile.api._

  class HomeSubjectTable(tag: Tag) extends Table[HomeSubjectEntity](tag, Some(Tables.SCHEMA_NAME), "home_subject") {
    def * = {
      (subjectId, subjectDisplayStyle, displayOrder
      ) <> ((HomeSubjectEntity.apply _).tupled, HomeSubjectEntity.unapply)
    }

    def subjectId = {
      column[UUID]("subject_id")
    }

    def subjectDisplayStyle = {
      column[SubjectDisplayStyleValue]("subject_display_style", O.Length(255, varying = true))
    }

    def displayOrder = {
      column[Int]("display_order")
    }
  }

  object HomeSubjectRepository {
    lazy val rows: TableQuery[HomeSubjectTable] = TableQuery[HomeSubjectTable]
  }

}
