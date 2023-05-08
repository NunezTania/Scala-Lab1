package Web

import scalatags.Text.all._
import scalatags.Text.tags2._

/**
 * Assembles the method used to layout ScalaTags
 */
object Layouts:
    // You can use it to store your methods to generate ScalaTags.
    def homePage(msgList: List[(String, Option[String], String)]) = {
        html(
            header("/resources/css/main.css", "/resources/js/main.js"),
            homePageNav(),
            homePageBody(msgList)
        )
    }

    def header(cssPath : String, jsPath : String) = {
        head(
            link(rel := "stylesheet", href := cssPath),
            script(src := jsPath)
        )
    }

    def homePageNav() = {
        nav(
            a(`class` := "nav-brand")("Bot-tender"),
            div(`class` := "nav-item")(
                a(href := "/")("Log in"),
            ),
        )
    }

    def homePageBody(msgList: List[(String, Option[String], String)]) = {
        body(
            div(`class` := "content")(
                div(id := "boardMessage")(
                    if msgList.isEmpty then "Please wait, the message are loading !" else for mess <- msgList yield message(mess._1, mess._2, mess._3)),
                messagesForm()
            ),
        )
    }

    def message(author: String, mention: Option[String], message: String) = {
      div(`class` := "msg")(
        span(`class` := "author")(author),
        span(`class` := "msg-content")(
          span(`class` := "mention")(mention.getOrElse("")),
          message
        )
      )
    }

    def messagesForm() = {
      form(id := "msgForm", onsubmit := "submitMessageForm(); return false;")(
        div(id := "errorDiv", `class` := "errorMsg"),
        label(`for` := "msgInput")("Your message:"),
        input(`type` := "text", id := "msgInput", placeholder := "Write your message"),
        input(`type` := "submit", value := "Envoyer")
      )
    }

end Layouts
