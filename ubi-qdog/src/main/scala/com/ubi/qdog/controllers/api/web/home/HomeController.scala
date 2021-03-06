package com.ubi.qdog.controllers.api.web.home

import java.util.UUID

import com.ubi.qdog.controllers.api.web.WebApiController
import com.ubi.qdog.controllers.api.{PaginationParameter, PaginationResponseData}
import com.ubi.qdog.enums.BannerLinkType
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}
import com.ubi.qdog.entities.Tables._
import com.ubi.qdog.entities.Tables.profile.api._
import play.api.{Environment, Mode}

import scala.concurrent.ExecutionContext

class HomeController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider,
  val environment: Environment
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def index: Action[AnyContent] = {
    Action { implicit request =>
      GetHomeContentResponse(
        banners = Seq(
          GetHomeContentResponse.Banner(
            coverUrl = "banner_01.png".absoluteURL,
            bannerLinkType = BannerLinkType.Subject,
            subjectId = UUID.fromString("3c04acd0-9e72-4be7-9a38-ae419da401ac")
          ),
          GetHomeContentResponse.Banner(
            coverUrl = "banner_02.png".absoluteURL,
            bannerLinkType = BannerLinkType.Subject,
            subjectId = UUID.fromString("1710adb1-c692-408c-922e-f918664289d4")
          )
        )
      ).ok
    }
  }

  def listSubject(page: PaginationParameter): Action[AnyContent] = {
    Action.async { implicit request =>
      val homeSubjects = HomeSubjectRepository.rows.sortBy(_.displayOrder.asc)

      def courses(subjectIds: Seq[UUID]) = CourseRepository.rows
        .join(CourseSubjectRepository.rows).on(_.courseId === _.courseId)
        .join(SubjectRepository.rows).on(_._2.subjectId === _.subjectId)
        .join(HomeSubjectRepository.rows).on(_._2.subjectId === _.subjectId)
        .filter(_._2.subjectId inSet subjectIds)
        .map { case (((course, _), subject), homeSubject) => ((subject, homeSubject.subjectDisplayStyle, homeSubject.displayOrder), course) }

      for {
        total <- db.run(homeSubjects.size.result)
        subjectIds <- db.run(homeSubjects.drop(page.offset).take(page.limit).map(_.subjectId).result)
        courses <- db.run(courses(subjectIds).result)
      } yield GetSubjectResponse(
        subjects = courses.groupBy(_._1).toSeq.sortBy(_._1._3).map {
          case ((subject, displayStyle, _), courses) =>
            GetSubjectResponse.Subject(
              subjectId = subject.subjectId,
              title = subject.title,
              displayStyle = displayStyle,
              courses = courses.map { case (_, course) =>
                GetSubjectResponse.Subject.Course(
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
              }
            )
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