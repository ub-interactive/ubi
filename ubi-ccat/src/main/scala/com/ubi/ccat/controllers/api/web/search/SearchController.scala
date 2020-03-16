package com.ubi.ccat.controllers.api.web.search

import com.ubi.ccat.controllers.api.web.WebApiController
import com.ubi.ccat.controllers.api.{PaginationParameter, PaginationResponseData}
import javax.inject.Inject
import play.api.Logging
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}
import com.ubi.ccat.tables.Tables._
import com.ubi.ccat.tables.Tables.profile.api._
import scala.concurrent.ExecutionContext

class SearchController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController with Logging {

  def search(
    keyword: String,
    page: PaginationParameter
  ): Action[AnyContent] = {
    Action.async { implicit request =>
logger.info(keyword)

      val courses = CourseRepository.rows.filter(_.title like s"%$keyword%")

      for {
        total <- db.run(courses.size.result)
        courses <- db.run(courses.drop(page.offset).take(page.size).result)
      } yield SearchResponse(
        courses = courses.map { course =>
          SearchResponse.Course(
            courseId = course.courseId,
            title = course.title,
            subtitle = course.subtitle,
            thumbnailUrl = course.thumbnailUrl.map(com.ubi.ccat.controllers.routes.ApplicationController.file(_).absoluteURL()),
            price = course.price,
            promotionPrice = course.promotionPrice,
            saleType = course.saleType,
            tags = course.tags,
            flashSaleStartAt = course.flashSaleStartAt,
            flashSaleEndAt = course.flashSaleEndAt)
        },
        page = PaginationResponseData(
          page = page.page,
          size = page.size,
          totalRecords = total
        )
      ).ok
    }
  }
}