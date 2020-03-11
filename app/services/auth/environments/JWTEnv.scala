package services.auth.environments

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import entities.user.AccountEntity

/**
  * Environment used by Silhouette.
  * Specified type of our User class and what Authenticator do we use.
  */
trait JWTEnv extends Env {
  type I = AccountEntity
  type A = JWTAuthenticator
}
