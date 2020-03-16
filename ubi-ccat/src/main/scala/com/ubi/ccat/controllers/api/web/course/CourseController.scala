package com.ubi.ccat.controllers.api.web.course

import java.util.UUID

import com.ubi.ccat.controllers.api.web.WebApiController
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}
import com.ubi.ccat.tables.Tables._
import com.ubi.ccat.tables.Tables.profile.api._
import scala.concurrent.ExecutionContext

class CourseController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def getCourse(courseId: UUID): Action[AnyContent] = {
    Action.async { implicit request =>

      db.run(CourseRepository.rows.filter(_.courseId === courseId).result.headOption).map {
        case Some(value) => GetCourseResponse(
          courseId = value.courseId,
          title = value.title,
          subtitle = value.subtitle,
          coverUrl = com.ubi.ccat.controllers.routes.ApplicationController.file(value.coverUrl).absoluteURL(),
          price = value.price,
          promotionPrice = value.promotionPrice,
          saleType = value.saleType,
          tags = value.tags,
          courseIntro = value.courseIntro,
          courseMenu = value.courseMenu,
          courseInfo = value.courseInfo,
          flashSaleStartAt = value.flashSaleStartAt,
          flashSaleEndAt = value.flashSaleEndAt,
          saleStock = value.saleStock
        ).ok
        case None => NotFound
      }
    }
  }

}