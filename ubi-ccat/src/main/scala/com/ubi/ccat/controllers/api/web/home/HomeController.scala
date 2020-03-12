package com.ubi.ccat.controllers.api.web.home

import java.util.UUID

import com.ubi.ccat.controllers.api.web.WebApiController
import com.ubi.ccat.enums.{BannerLinkType, SubjectDisplayStyle}
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
        .join(CourseSubjectRepository.rows).on(_.courseId === _.subjectId)
        .join(SubjectRepository.rows).on(_._2.subjectId === _.subjectId)
        .map { case ((course, _), subject) => (subject, course) }

      for {
        courses <- db.run(courses.result)
      } yield GetHomeContentResponse(
        banners = Seq(
          GetHomeContentResponse.Banner(
            coverUrl = "https://assets.xrkmm.cn/u/504ffc4f-2601-4bee-8f81-5b201c6b58ab.png?x-oss-process=image/resize,m_fill,w_1125,h_563",
            bannerLinkType = BannerLinkType.Subject,
            subjectId = UUID.fromString("1710adb1-c692-408c-922e-f918664289d4")
          ),
          GetHomeContentResponse.Banner(
            coverUrl = "https://assets.xrkmm.cn/u/87ecf63c-9763-42e5-b1c5-821075ca73fc.png?x-oss-process=image/resize,m_fill,w_1125,h_563",
            bannerLinkType = BannerLinkType.Subject,
            subjectId = UUID.fromString("1710adb1-c692-408c-922e-f918664289d4")
          )
        ),
        subjects = courses.groupBy(_._1).map { case (subject, courses) =>
          GetHomeContentResponse.Subject(
            subjectId = subject.subjectId,
            title = subject.title,
            displayStyle = SubjectDisplayStyle.OneColumn,
            courses = courses.map { case (_, course) =>
              GetHomeContentResponse.Subject.Course(
                courseId = course.courseId,
                title = course.title,
                subtitle = course.subtitle,
                thumbnailUrl = course.thumbnailUrl,
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