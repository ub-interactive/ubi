package com.ubi.modules

import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import com.mohiva.play.silhouette.api.crypto.{AuthenticatorEncoder, Base64AuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.{AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.{PlayCacheLayer, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.{CacheAuthenticatorRepository, DelegableAuthInfoRepository}
import play.api.Configuration
import play.api.libs.ws.WSClient
import services.auth.authorizations.{Has, HasPermission}
import services.auth.environments.JWTEnv
import services.auth.{AccountIdentityService, PasswordAuthInfoDAO}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration

class SilhouetteModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Clock]).toInstance(Clock())
    bind(classOf[EventBus]).toInstance(EventBus())

    bind(classOf[AuthenticatorEncoder]).to(classOf[Base64AuthenticatorEncoder]).asEagerSingleton()
    bind(classOf[IDGenerator]).toInstance(new SecureRandomIDGenerator())

    bind(new TypeLiteral[DelegableAuthInfoDAO[PasswordInfo]] {}).to(new TypeLiteral[PasswordAuthInfoDAO] {}).asEagerSingleton()
    bind(new TypeLiteral[IdentityService[JWTEnv#I]] {}).to(classOf[AccountIdentityService]).asEagerSingleton()
    bind(classOf[PasswordHasher]).toInstance(new BCryptPasswordHasher())
    bind(new TypeLiteral[Silhouette[JWTEnv]] {}).to(new TypeLiteral[SilhouetteProvider[JWTEnv]] {}).asEagerSingleton()

    bind(classOf[com.mohiva.play.silhouette.api.actions.SecuredErrorHandler]).to(classOf[services.auth.errorHandlers.SecuredErrorHandler]).asEagerSingleton()
    bind(classOf[com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler]).to(classOf[services.auth.errorHandlers.UnsecuredErrorHandler]).asEagerSingleton()

    bind(classOf[Has]).to(classOf[HasPermission]).asEagerSingleton()
  }

  @Provides
  def provideEnvironment(
    identityService: AccountIdentityService,
    authenticatorService: AuthenticatorService[JWTAuthenticator],
    eventBus: EventBus
  ): Environment[JWTEnv] = {
    Environment[JWTEnv](identityService, authenticatorService, Seq(), eventBus)
  }

  @Provides
  def provideAuthenticatorService(
    configuration: Configuration,
    idGenerator: IDGenerator,
    base64AuthenticatorEncoder: AuthenticatorEncoder,
    clock: Clock,
    playCacheLayer: PlayCacheLayer
  ): AuthenticatorService[JWTAuthenticator] = {
    val jwtAuthenticatorConfigure = configuration.get[Configuration]("silhouette.authenticator.jwt")
    val sharedSecret = jwtAuthenticatorConfigure.get[String]("sharedSecret")
    val issuer = jwtAuthenticatorConfigure.get[String]("issuerClaim")
    val expiry = jwtAuthenticatorConfigure.get[Duration]("authenticatorExpiry")
    val fieldName = jwtAuthenticatorConfigure.get[String]("fieldName")

    // we do not encrypt subject, as we do not transmit sensitive data AND it'd have to be decryptable across services
    val jwtSettings = JWTAuthenticatorSettings(
      fieldName = fieldName,
      issuerClaim = issuer,
      authenticatorExpiry = Duration.fromNanos(expiry.toNanos),
      sharedSecret = sharedSecret
    )

    new JWTAuthenticatorService(settings = jwtSettings,
      repository = Some(new CacheAuthenticatorRepository[JWTAuthenticator](playCacheLayer)),
      authenticatorEncoder = base64AuthenticatorEncoder,
      idGenerator = idGenerator,
      clock = clock)
  }

  @Provides
  def provideCredentialsProvider(
    authInfoRepository: AuthInfoRepository,
    passwordHasher: PasswordHasher
  ): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, PasswordHasherRegistry(passwordHasher))
  }

  @Provides
  def provideAuthInfoRepository(passwordInfoDao: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordInfoDao)
  } //  social services.auth
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = {
    new PlayHTTPLayer(client)
  }
}
