package com.ubi.ccat.controllers.api.web.search

import com.ubi.ccat.controllers.api.web.WebApiController
import com.ubi.ccat.controllers.api.{PaginationParameter, PaginationResponseData}
import com.ubi.ccat.entities.Tables._
import com.ubi.ccat.entities.Tables.profile.api._
import javax.inject.Inject
import play.api.{Environment, Logging}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class SearchController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider,
  val environment: Environment
)
  (implicit val ec: ExecutionContext) extends WebApiController with Logging {

  def search(
    keyword: String,
    page: PaginationParameter
  ): Action[AnyContent] = {
    Action.async { implicit request =>

      val courses = CourseRepository.rows.filter(_.title like s"%$keyword%")
      logger.info(courses.result.statements.mkString(","))

      for {
        total <- db.run(courses.size.result)
        courses <- db.run(courses.drop(page.offset).take(page.size).result)
      } yield SearchResponse(
        courses = courses.map { course =>
          SearchResponse.Course(
            courseId = course.courseId,
            title = course.title,
            subtitle = course.subtitle,
            thumbnailUrl = course.thumbnailUrl.map(_.absoluteURL),
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