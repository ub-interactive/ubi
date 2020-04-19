package com.ubi.qdog.controllers.api.web.subject

import java.util.UUID

import com.ubi.qdog.controllers.api.web.WebApiController
import com.ubi.qdog.controllers.api.{PaginationParameter, PaginationResponseData}
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}
import com.ubi.qdog.entities.Tables._
import com.ubi.qdog.entities.Tables.profile.api._
import play.api.Environment

import scala.concurrent.ExecutionContext

class SubjectController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider,
  val environment: Environment
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def getSubject(
    subjectId: UUID,
    page: PaginationParameter
  ): Action[AnyContent] = {
    Action.async { implicit request =>

      val courses = CourseRepository.rows
        .join(CourseSubjectRepository.rows).on(_.courseId === _.courseId)
        .join(SubjectRepository.rows).on(_._2.subjectId === _.subjectId)
        .filter(_._2.subjectId === subjectId)
        .map { case ((course, _), subject) => (subject, course) }

      for {
        total <- db.run(courses.size.result)
        courses <- db.run(courses.drop(page.offset).take(page.limit).result)
      } yield courses.groupBy(_._1).headOption match {
        case Some(value) =>
          val (subject, courses) = value
          GetSubjectResponse(
            subjectId = subject.subjectId,
            title = subject.title,
            courses = courses.map { case (_, course) =>
              GetSubjectResponse.Course(
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
        case None => NotFound
      }
    }
  }
}