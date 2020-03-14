package com.ubi.ccat.controllers.api.web.home

import java.util.UUID

import com.ubi.ccat.controllers.api.web.WebApiController
import com.ubi.ccat.enums.BannerLinkType
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

      val courses = CourseRepository.rows
        .join(CourseSubjectRepository.rows).on(_.courseId === _.courseId)
        .join(SubjectRepository.rows).on(_._2.subjectId === _.subjectId)
        .join(HomeSubjectRepository.rows).on(_._2.subjectId === _.subjectId)
        .map { case (((course, _), subject), homeSubject) => ((subject, homeSubject.subjectDisplayStyle, homeSubject.displayOrder), course) }

      for {
        courses <- db.run(courses.result)
      } yield GetHomeContentResponse(
        banners = Seq(
          GetHomeContentResponse.Banner(
            coverUrl = com.ubi.ccat.controllers.routes.ApplicationController.file("banner_01.png").absoluteURL(),
            bannerLinkType = BannerLinkType.Subject,
            subjectId = UUID.fromString("3c04acd0-9e72-4be7-9a38-ae419da401ac")
          ),
          GetHomeContentResponse.Banner(
            coverUrl = com.ubi.ccat.controllers.routes.ApplicationController.file("banner_02.png").absoluteURL(),
            bannerLinkType = BannerLinkType.Subject,
            subjectId = UUID.fromString("1710adb1-c692-408c-922e-f918664289d4")
          )
        ),
        subjects = courses.groupBy(_._1).toSeq.sortBy(_._1._3).map { case ((subject, subjectDisplayStyle, _), courses) =>
          GetHomeContentResponse.Subject(
            subjectId = subject.subjectId,
            title = subject.title,
            displayStyle = subjectDisplayStyle,
            courses = courses.map { case (_, course) =>
              GetHomeContentResponse.Subject.Course(
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
            }
          )
        }
      ).ok
    }
  }
}