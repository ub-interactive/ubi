package com.ubi.persistence.slick

object Tables extends {
  val SCHEMA_NAME = "ubi"
  val profile = slick.jdbc.MySQLProfile
} with Tables

trait Tables
  extends OrderTable
    with RoomTable
    with HotelTable
    with ApiInvocationTable {
  val profile: slick.jdbc.JdbcProfile

  lazy val schema: profile.SchemaDescription = List(
    OrderRepository.rows.schema,
    RoomRepository.rows.schema,
    HotelRepository.rows.schema,
    ApiInvocationRepository.rows.schema
  ).reduceLeft(_ ++ _)

  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl: profile.DDL = {
    schema
  }
}
