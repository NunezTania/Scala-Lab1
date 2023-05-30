package Web

import Chat.{AnalyzerService, TokenizerService}
import Data.{MessageService, AccountService, SessionService, Session}
import scalatags.Text.all.stringFrag
import scalatags.Text.all._
import castor.Context.Simple.global
import MessageService.{Username, MsgContent}
import pprint.StringPrefix
import Chat.Parser
import Chat.UnexpectedTokenException
import Chat.ExprTree.Order

/** Assembles the routes dealing with the message board:
  *   - One route to display the home page
  *   - One route to send the new messages as JSON
  *   - One route to subscribe with websocket to new messages
  *
  * @param log
  */
class MessagesRoutes(
    tokenizerSvc: TokenizerService,
    analyzerSvc: AnalyzerService,
    msgSvc: MessageService,
    accountSvc: AccountService,
    sessionSvc: SessionService
)(implicit val log: cask.Logger)
    extends cask.Routes:
  import Decorators.getSession

  var openConnections = Set.empty[cask.WsChannelActor]

  @getSession(
    sessionSvc
  ) // This decorator fills the `(session: Session)` part of the `index` method.
  @cask.get("/")
  def index()(session: Session) = {
    Layouts.homePage(msgSvc.getLatestMessages(20), None, session.getCurrentUser.isDefined)
  }

  @getSession(sessionSvc)
  @cask.postJson("/send")
  def postMessage(msg: String)(session: Session) = {

    session.getCurrentUser match
      case Some(user) => {
        if (msg.isEmpty()) {
          ujson.Obj("success" -> false, "err" -> "The message is empty")
        } else {
          // check for bot mention
          if (msg.startsWith("@bot")) then {
            try {
              val message = msg.stripPrefix("@bot").toLowerCase
              val tokenize = tokenizerSvc.tokenize(message)
              val parser = new Parser(tokenize)
              val expr = parser.parsePhrases()
              val reply = analyzerSvc.reply(session)(expr)
              val id =
                msgSvc.add(user, message, Some("bot"), Option(expr), None)
              expr match
                case Order(products) =>
                  msgSvc.add( // Préparation de la commande
                    "bot",
                    "Votre commande est en cours de préparation : " + products.toString(),
                    Some(user),
                    None,
                    Option(id)
                  )
                  Thread.sleep(5000)
                case _ =>
                  msgSvc.add(
                    "bot",
                    reply,
                    Some(user),
                    None,
                    Option(id)
                  )

              openConnections.foreach(displayMessages(_))

              msgSvc.add(
                "bot",
                reply,
                Some(user),
                None,
                Option(id)
              )

              openConnections.foreach(displayMessages(_))
              ujson.Obj("success" -> true, "err" -> "")
            } catch
              case e : Exception =>
                ujson.Obj("success" -> false, "err" -> e.getMessage())
          } else {

            // verify other mention
            val mention = if (msg.startsWith("@")) then {
              val mention = msg.split(" ")(0).stripPrefix("@")
              Some(mention)
            } else {
              None
            }
            msgSvc.add(user, msg, mention, None, None)
            openConnections.foreach(displayMessages(_))
            ujson.Obj("success" -> true, "err" -> "")
          }
        }
      }
      case None => {
        ujson.Obj(
          "success" -> false,
          "err" -> "You must be logged in to send a message"
        )
      }
  }

  @cask.websocket("/subscribe")
  def subscribe() = {
    cask.WsHandler { connection =>

      openConnections += connection
      displayMessages(connection)

      cask.WsActor {
        case cask.Ws.Close(_, _) => {
          openConnections -= connection
        }
      }
    }
  }

  private def displayMessages(
      connection: cask.WsChannelActor
  ) = {

    val layout =
      if msgSvc.getLatestMessages(20).isEmpty then
        div("No messages have been sent yet").toString
      else
        msgSvc
          .getLatestMessages(20)
          .map((author, content) => Layouts.message(author, content).toString)
          .reduceLeft(_ + _)

    connection.send(
      cask.Ws
        .Text(
          layout
        )
    )
  }

  @getSession(sessionSvc)
  @cask.get("/clearHistory")
  def clearHistory()(session: Session) = {
    msgSvc.deleteHistory()
    Layouts.homePage(List())
  }

  initialize()
end MessagesRoutes
