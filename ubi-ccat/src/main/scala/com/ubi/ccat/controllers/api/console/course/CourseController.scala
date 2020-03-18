package com.ubi.ccat.controllers.api.console.course

import java.util.UUID

import akka.Done
import com.ubi.ccat.controllers.api.ApiRequest
import com.ubi.ccat.controllers.api.web.WebApiController
import com.ubi.ccat.entities
import com.ubi.ccat.entities.CourseEntity
import com.ubi.ccat.entities.Tables._
import com.ubi.ccat.entities.Tables.profile.api._
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class CourseController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def createCourse: Action[JsValue] = {
    Action.async(parse.json) { implicit request =>
      request.body.validate[ApiRequest[CreateCourseRequest]].withData { createCourseRequest =>
        val CreateCourseRequest(title, subtitle, thumbnailUrl, coverUrl, price, promotionPrice, saleType, tags, courseIntro, courseMenu, courseInfo, flashSaleStartAt, flashSaleEndAt, saleStock) = createCourseRequest
        val courseId = UUID.randomUUID()
        db.run(CourseRepository.rows += entities.CourseEntity(
          courseId = courseId,
          title = title,
          subtitle = subtitle,
          thumbnailUrl = thumbnailUrl,
          coverUrl = coverUrl,
          price = price,
          promotionPrice = promotionPrice,
          saleType = saleType,
          tags = tags,
          courseIntro = courseIntro,
          courseMenu = courseMenu,
          courseInfo = courseInfo,
          flashSaleStartAt = flashSaleStartAt,
          flashSaleEndAt = flashSaleEndAt,
          saleStock = saleStock
        )).map(_ => CreateCourseResponse(
          courseId = courseId,
          title = title,
          subtitle = subtitle,
          thumbnailUrl = thumbnailUrl,
          coverUrl = coverUrl,
          price = price,
          promotionPrice = promotionPrice,
          saleType = saleType,
          tags = tags,
          courseIntro = courseIntro,
          courseMenu = courseMenu,
          courseInfo = courseInfo,
          flashSaleStartAt = flashSaleStartAt,
          flashSaleEndAt = flashSaleEndAt,
          saleStock = saleStock
        )).ok
      }
    }
  }

  def updateCourse(courseId: UUID): Action[JsValue] = {
    Action.async(parse.json) { implicit request =>
      request.body.validate[ApiRequest[UpdateCourseRequest]].withData { updateCourseRequest =>
        val UpdateCourseRequest(title, subtitle, thumbnailUrl, coverUrl, price, promotionPrice, saleType, tags, courseIntro, courseMenu, courseInfo, flashSaleStartAt, flashSaleEndAt, saleStock) = updateCourseRequest
        db.run(CourseRepository.rows.filter(_.courseId === courseId).update(CourseEntity(
          courseId = courseId,
          title = title,
          subtitle = subtitle,
          thumbnailUrl = thumbnailUrl,
          coverUrl = coverUrl,
          price = price,
          promotionPrice = promotionPrice,
          saleType = saleType,
          tags = tags,
          courseIntro = courseIntro,
          courseMenu = courseMenu,
          courseInfo = courseInfo,
          flashSaleStartAt = flashSaleStartAt,
          flashSaleEndAt = flashSaleEndAt,
          saleStock = saleStock
        ))).map(_ => UpdateCourseResponse(
          courseId = courseId,
          title = title,
          subtitle = subtitle,
          thumbnailUrl = thumbnailUrl,
          coverUrl = coverUrl,
          price = price,
          promotionPrice = promotionPrice,
          saleType = saleType,
          tags = tags,
          courseIntro = courseIntro,
          courseMenu = courseMenu,
          courseInfo = courseInfo,
          flashSaleStartAt = flashSaleStartAt,
          flashSaleEndAt = flashSaleEndAt,
          saleStock = saleStock
        )
        ).ok
      }
    }
  }

  def listCourse: Action[AnyContent] = {
    Action.async { implicit request =>
      for {
        courses <- db.run(CourseRepository.rows.result)
      } yield ListCourseResponse(
        courses = courses.map { course =>
          ListCourseResponse.Course(
            courseId = course.courseId,
            title = course.title,
            subtitle = course.subtitle,
            thumbnailUrl = course.thumbnailUrl,
            coverUrl = course.coverUrl,
            price = course.price,
            promotionPrice = course.promotionPrice,
            saleType = course.saleType,
            tags = course.tags,
            courseIntro = course.courseIntro,
            courseMenu = course.courseMenu,
            courseInfo = course.courseInfo,
            flashSaleStartAt = course.flashSaleStartAt,
            flashSaleEndAt = course.flashSaleEndAt,
            saleStock = course.saleStock
          )
        }
      ).ok
    }
  }

  def deleteCourse(courseId: UUID): Action[AnyContent] = {
    Action.async { implicit request =>
      db.run(CourseRepository.rows.filter(_.courseId === courseId).delete).map(_ => Done).ok
    }
  }
}
