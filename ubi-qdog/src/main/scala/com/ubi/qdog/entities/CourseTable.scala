package com.ubi.qdog.entities

import java.time.Instant
import java.util.UUID

import com.ubi.qdog.enums.CourseSaleTypeValue
import CourseEntity.Tags
import play.api.libs.json.Json
import slick.jdbc.JdbcType

final case class CourseEntity(
  courseId: UUID,
  title: String,
  subtitle: Option[String],
  thumbnailUrl: Option[String],
  coverUrl: String,
  price: Int,
  promotionPrice: Option[Int],
  saleType: CourseSaleTypeValue,
  tags: Tags,
  courseIntro: Option[String],
  courseMenu: Option[String],
  courseInfo: Option[String],
  flashSaleStartAt: Option[Instant],
  flashSaleEndAt: Option[Instant],
  saleStock: Option[Int]
)

object CourseEntity {
  type Tags = Seq[String]
}

trait CourseTable {

  self: Tables =>

  import profile.api._

  class CourseTable(tag: Tag) extends Table[CourseEntity](tag, Some(Tables.SCHEMA_NAME), "course") {
    def * = {
      (courseId,
        title,
        subtitle,
        thumbnailUrl,
        coverUrl,
        price,
        promotionPrice,
        saleType,
        tags,
        courseIntro,
        courseMenu,
        courseInfo,
        flashSaleStartAt,
        flashSaleEndAt,
        saleStock
      ) <> ((CourseEntity.apply _).tupled, CourseEntity.unapply)
    }

    implicit def columnMapper(implicit profile: slick.jdbc.JdbcProfile): JdbcType[Tags] = {
      import profile.api._
      MappedColumnType.base[Tags, String]({ v => Json.toJson(v).toString() }, { v => Json.parse(v).validate[Vector[String]].get })
    }

    def courseId = {
      column[UUID]("course_id", O.PrimaryKey)
    }

    def title = {
      column[String]("title", O.Length(255, varying = true))
    }

    def subtitle = {
      column[Option[String]]("subtitle", O.Length(255, varying = true))
    }

    def thumbnailUrl = {
      column[Option[String]]("thumbnail_url")
    }

    def coverUrl = {
      column[String]("cover_url")
    }

    def price = {
      column[Int]("price")
    }

    def promotionPrice = {
      column[Option[Int]]("promotion_price")
    }

    def saleType = {
      column[CourseSaleTypeValue]("sale_type")
    }

    def tags = {
      column[Tags]("tags")
    }

    def courseIntro = {
      column[Option[String]]("course_intro")
    }

    def courseMenu = {
      column[Option[String]]("course_menu")
    }

    def courseInfo = {
      column[Option[String]]("course_info")
    }

    def flashSaleStartAt = {
      column[Option[Instant]]("flash_sale_start_at")
    }

    def flashSaleEndAt = {
      column[Option[Instant]]("flash_sale_end_at")
    }

    def saleStock = {
      column[Option[Int]]("sale_stock")
    }
  }

  object CourseRepository {
    lazy val rows: TableQuery[CourseTable] = TableQuery[CourseTable]
  }

}
