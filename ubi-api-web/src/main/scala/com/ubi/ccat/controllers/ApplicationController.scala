package com.ubi.ccat.controllers

import java.time.Instant
import java.util.UUID

import com.ubi.ccat.controllers.api.web.WebApiController
import com.ubi.ccat.enums.CourseSaleType
import com.ubi.ccat.persistence.slick.CourseEntity
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class ApplicationController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def index: Action[AnyContent] = {
    Action.async { implicit request =>
      import com.ubi.ccat.persistence.slick.Tables._
      import com.ubi.ccat.persistence.slick.Tables.profile.api._

      for {
        _ <- db.run(CourseRepository.rows += CourseEntity(
        courseId = UUID.randomUUID(),
        title = "title",
        subtitle = Some("subtitle"),
        thumbnailUrl = Some("thumbnailUrl"),
        coverUrl = "coverUrl",
        price = 99,
        promotionPrice = Some(88),
        saleType = CourseSaleType.Standard,
        tags = Seq("1", "2"),
        courseIntro = Some("courseIntro"),
        courseMenu = Some("courseMenu"),
        courseInfo = Some("courseInfo"),
        flashSaleStartAt = Some(Instant.now()),
        flashSaleEndAt = Some(Instant.now()),
        saleStock = Some(100)
        ))
        result <- db.run(CourseRepository.rows.map(_.courseId).result)
      } yield Ok(result.map(_.toString).mkString("<br/>"))

    }
  }
}
