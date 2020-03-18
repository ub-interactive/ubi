package com.ubi.ccat.entities

import java.util.UUID

final case class CourseSubjectEntity(
  courseId: UUID,
  subjectId: UUID
)

trait CourseSubjectTable {

  self: Tables =>

  import profile.api._

  class CourseSubjectTable(tag: Tag) extends Table[CourseSubjectEntity](tag, Some(Tables.SCHEMA_NAME), "course_subject") {
    def * = {
      (courseId, subjectId
      ) <> ((CourseSubjectEntity.apply _).tupled, CourseSubjectEntity.unapply)
    }

    def courseId = {
      column[UUID]("course_id")
    }

    def subjectId = {
      column[UUID]("subject_id")
    }

  }

  object CourseSubjectRepository {
    lazy val rows: TableQuery[CourseSubjectTable] = TableQuery[CourseSubjectTable]
  }

}
