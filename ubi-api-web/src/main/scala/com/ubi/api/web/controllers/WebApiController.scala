package com.ubi.api.web.controllers

trait WebApiController extends ApiController {
  //
  //  private def AppMetaAction: ActionRefiner[SecuredRequest, AppSecuredRequest] = new ActionRefiner[SecuredRequest, AppSecuredRequest] {
  //    override val executionContext: ExecutionContext = ec
  //
  //    /** get appMeta using query parameter
  //      */
  //    @Deprecated
  //    private def getFromBody[A](request: SecuredRequest[A], key: String): Option[String] = {
  //      request.body match {
  //        case body: JsValue => (body \ key).validate[String].asOpt
  //        case _             => None
  //      }
  //    }
  //
  //    def refine[A](sr: SecuredRequest[A]): Future[Either[Result, AppSecuredRequest[A]]] = {
  //      val queryString = sr.queryString
  //      val deviceModel = queryString.get("deviceModel").flatMap(_.headOption).orElse(getFromBody(sr, "deviceModel")).getOrElse("unknown")
  //      val appVersion = queryString.get("appVersion").flatMap(_.headOption).orElse(getFromBody(sr, "appVersion")).getOrElse("unknown")
  //      val deviceVersion = queryString.get("deviceVersion").flatMap(_.headOption).orElse(getFromBody(sr, "deviceVersion")).getOrElse("unknown")
  //      /* todo: remove platform, use platformC */
  //      val platformCString = queryString.get("platformC").orElse(queryString.get("platform")).flatMap(_.headOption).orElse(getFromBody(sr, "platformC")).orElse(getFromBody(sr, "platform")).getOrElse("android")
  //
  //      val asr = for {
  //        platformC <- MobileDevicePlatform.fromValue(platformCString) match {
  //          case Some(value) => Future.successful(value)
  //          case None        => Future.failed(UnexpectedException(Some(s"query string parameter platformC [$platformCString] is invalid")))
  //        }
  //      } yield new AppSecuredRequest(
  //        appMeta = AppMeta(
  //          deviceModel = deviceModel,
  //          appVersion = appVersion,
  //          deviceVersion = deviceVersion,
  //          platformC = platformC
  //        ),
  //        identity = sr.identity,
  //        request = sr.request
  //      )
  //
  //      asr.map(Right.apply).recover {
  //        case ex: Throwable => Left(s"invalid app meta info: ${ex.getLocalizedMessage}".error(sr))
  //      }
  //    }
  //  }
  //
  //  def AppSecuredAction(implicit securedAction: SecuredAction): ActionBuilder[AppSecuredRequest, AnyContent] =
  //    securedAction.andThen(AppMetaAction)
  //
  //  override def AuthorizedAction(allowedPermissions: Set[PermissionModel.PermissionValue])(implicit securedAction: SecuredAction): ActionBuilder[AppSecuredRequest, AnyContent] =
  //    super.AuthorizedAction(allowedPermissions).andThen(AppMetaAction)
  //
  //  def AuthorizedAction()(implicit securedAction: SecuredAction): ActionBuilder[AppSecuredRequest, AnyContent] =
  //    AuthorizedAction(Set(PermissionModel.MobileAppPermission.UseApp))

}
