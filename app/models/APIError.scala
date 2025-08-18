package models

import play.api.http.Status

sealed abstract class APIError(val httpResponseStatus: Int, val reason: String)

object APIError {

  final case class NotFound(message: String) extends APIError(Status.NOT_FOUND, message)

  final case class BadAPIResponse(
                                   override val httpResponseStatus: Int,
                                   override val reason: String
                                 ) extends APIError(httpResponseStatus, reason)
}
