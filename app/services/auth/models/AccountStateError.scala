package services.auth.models

import com.mohiva.play.silhouette.api.exceptions.ProviderException

/**
  * @author liangliao at 2018/10/10 1:10 AM
  */
sealed trait AccountStateError

case object AccountNotExists$ extends AccountStateError

case object AccountAlreadyExists$ extends AccountStateError

case object AccountNotActivated$ extends AccountStateError

case object AccountDisabled$ extends AccountStateError

case object LoginInfoMissing extends AccountStateError

case object InvalidCredentials extends AccountStateError

case class ProviderError(e: ProviderException) extends AccountStateError