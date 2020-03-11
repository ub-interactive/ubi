package com.ubi.ccat.persistence.slick

final case class CourseEntity(
  courseId: String,
  title: String,
  subtitle: Option[String],
  thumbnailUrl: Option[String],
  coverUrl: String,
  price: String,
  promotionPrice: Option[Int],
  saleType: String,
  tags: String,
  courseIntro: String,
  courseMenu: String,
  courseInfo: String,
  flashSaleStartAt: String,
  flashSaleEndAt: String,
  saleStock: String
)

trait CourseTable {

  self: Tables =>

  import profile.api._

  class CourseTable(tag: Tag) extends Table[CourseEntity](tag, Some(Tables.SCHEMA_NAME), "course") {
    def * = {
      (hotelId, projectId) <> ((CourseEntity.apply _).tupled, CourseEntity.unapply)
    }

    def hotelId: Rep[String] = {
      column[String]("hotel_id", O.Length(255, varying = true), O.PrimaryKey, O.SqlType("NVARCHAR"))
    }

    def projectId: Rep[String] = {
      column[String]("project_id", O.Length(255, varying = true), O.SqlType("NVARCHAR"))
    }

  }

  object CourseRepository {
    lazy val rows: TableQuery[CourseTable] = TableQuery[CourseTable]
  }

}
