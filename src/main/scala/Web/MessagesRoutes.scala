package Web

import Chat.{AnalyzerService, TokenizerService}
import Data.{MessageService, AccountService, SessionService, Session}
import scalatags.Text.all.stringFrag
import castor.Context.Simple.global
import cask.util.Ws.Event

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

  @getSession(
    sessionSvc
  ) // This decorator fills the `(session: Session)` part of the `index` method.
  @cask.get("/")
  def index()(session: Session) = {
    // TODO - Part 3 Step 2: Display the home page (with the message board and the form to send new messages)
    Layouts.homePage(msgSvc.getLatestMessages(20), session.getCurrentUser)
  }
  // session.getCurrentUser.map(u => s"You are logged in as ${u} !")
  //      .getOrElse("You are not logged in !")

  // TODO - Part 3 Step 4b: Process the new messages sent as JSON object to `/send`. The JSON looks
  //      like this: `{ "msg" : "The content of the message" }`.
  //
  //      A JSON object is returned. If an error occurred, it looks like this:
  //      `{ "success" : false, "err" : "An error message that will be displayed" }`.
  //      Otherwise (no error), it looks like this:
  //      `{ "success" : true, "err" : "" }`
  //
  //      The following are treated as error:
  //      - No user is logged in
  //      - The message is empty
  //
  //      If no error occurred, every other user is notified with the last 20 messages

  @getSession(sessionSvc)
  @cask.postJson("/send")
  def postMessage(msg: ujson.Value)(session: Session) = {
    session.getCurrentUser match
      case Some(user) => {
        if (msg.isNull) {
          ujson.Obj("success" -> false, "err" -> "The message is empty")
        } else {
          // separate the msg into msgContent, mention, exprType, replyToId
          // val msgContent = ujson.read(msg)("msg").str
          msgSvc.add(user, "AsgContent", None, None, None)

          // notify every other user with the last 20 messages
          openConnections.foreach(_.send(cask.Ws.Text(msg.toString)))
          ujson.Obj("success" -> true, "err" -> "")
        }
      }
      case None => {
        ujson.Obj(
          "success" -> false,
          "err" -> "You must be logged in to send a message"
        )
      }
  }

  // TODO - Part 3 Step 4c: Process and store the new websocket connection made to `/subscribe`
  var openConnections = Set.empty[cask.WsChannelActor]

  @cask.websocket("/subscribe")
  def subscribe() = {
    cask.WsHandler { connection =>
      if (!openConnections.contains(connection)) openConnections += connection
      cask.WsActor {
        case cask.Ws.Text(msg) =>
          if (msg == "ping") {
            connection.send(cask.Ws.Text("pong"))
          } else {
            connection.send(cask.Ws.Text("Error: unknown message"))
          }
        case cask.Ws.Binary(data) => connection.send(cask.Ws.Binary(data))
        case cask.Ws.Close(_, _) => openConnections -= connection
      }
    }
  }

  // TODO - Part 3 Step 4d: Delete the message history when a GET is made to `/clearHistory`
  //
  @getSession(sessionSvc)
  @cask.get("/clearHistory")
  def clearHistory()(session: Session) = {
    msgSvc.deleteHistory()
    Layouts.homePage(List())
  }
  // TODO - Part 3 Step 5: Modify the code of step 4b to process the messages sent to the bot (message
  //      starts with `@bot `). This message and its reply from the bot will be added to the message
  //      store together.
  //
  //      The exceptions raised by the `Parser` will be treated as an error (same as in step 4b)

  initialize()
end MessagesRoutes
