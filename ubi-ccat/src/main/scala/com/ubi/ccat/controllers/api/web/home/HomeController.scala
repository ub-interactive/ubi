package com.ubi.ccat.controllers.api.web.home

import com.ubi.ccat.controllers.api.web.WebApiController
import com.ubi.ccat.persistence.slick.Tables._
import com.ubi.ccat.persistence.slick.Tables.profile.api._
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class HomeController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def index: Action[AnyContent] = {
    Action.async { implicit request =>
      for {
        courses <- db.run(CourseRepository.rows.result)
      } yield GetHomeContentResponse(
        courses = courses.map { course =>
          GetHomeContentResponse.Course(
            courseId = course.courseId,
            title = course.title,
            subtitle = course.subtitle,
            thumbnailUrl = course.thumbnailUrl,
            price = course.price,
            promotionPrice = course.promotionPrice,
            saleType = course.saleType,
            tags = course.tags,
            flashSaleStartAt = course.flashSaleStartAt,
            flashSaleEndAt = course.flashSaleEndAt
          )
        }
      ).ok
    }
  }
}
