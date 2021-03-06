package com.ubi.qdog.controllers.api.web.course

import java.util.UUID

import com.ubi.qdog.controllers.api.web.WebApiController
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}
import com.ubi.qdog.entities.Tables._
import com.ubi.qdog.entities.Tables.profile.api._
import play.api.Environment

import scala.concurrent.ExecutionContext

class CourseController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider,
  val environment: Environment
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def getCourse(courseId: UUID): Action[AnyContent] = {
    Action.async { implicit request =>

      db.run(CourseRepository.rows.filter(_.courseId === courseId).result.headOption).map {
        case Some(value) => GetCourseResponse(
          courseId = value.courseId,
          title = value.title,
          subtitle = value.subtitle,
          coverUrl = value.coverUrl.absoluteURL,
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