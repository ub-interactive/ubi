package forms

import play.api.data.Form
import play.api.data.Forms._
import services.auth.request.AuthRequest

package object auth {

  val signIn = Form(
    mapping(
      "identifier" -> nonEmptyText,
      "password" -> nonEmptyText
    )(AuthRequest.apply)(AuthRequest.unapply)
  )

}
