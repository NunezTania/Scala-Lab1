package Web

import scalatags.Text.all._
import scalatags.Text.tags2._

/**
 * Assembles the method used to layout ScalaTags
 */
object Layouts:
    // You can use it to store your methods to generate ScalaTags.
    def homePage(msgList: List[(String, Option[List[String]], String)]) = {
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
                a(href := "/login")("Log in"),
            ),
        )
    }

    def homePageBody(msgList: List[(String, Option[List[String]], String)]) = {
        body(
            boardMessage(msgList)
        )
    }

    def boardMessage(msgList: List[(String, Option[List[String]], String)]) = {
        div(`class` := "content")(
                div(id := "boardMessage")(
                    if msgList.isEmpty then "Please wait, the message are loading !" 
                    else msgList.map((msg) => message(msg._1, msg._2, msg._3))),
                messagesForm()
            )
    }

    def message(author: String, mentions: Option[List[String]], message: String) = {
      div(`class` := "msg")(
        span(`class` := "author")(author),
        span(`class` := "msg-content")(
          span(`class` := "mention")(mentions.getOrElse(List()).foldLeft("")((a, b) => a + ", " + b)),
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

    def messagesForm(error : String) = {
      form(id := "msgForm", onsubmit := "submitMessageForm(); return false;")(
        div(id := "errorDiv", `class` := "errorMsg", value := error),
        label(`for` := "msgInput")("Your message:"),
        input(`type` := "text", id := "msgInput", placeholder := "Write your message"),
        input(`type` := "submit", value := "Envoyer")
      )
    }

    def loginPage() = {
        html(
            header("/resources/css/main.css", "/resources/js/main.js"),
            loginPageNav(),
            loginPageBody()
        )
    }

    def loginPageNav()= {
        nav(
            a(`class` := "nav-brand")("Bot-tender"),
            div(`class` := "nav-item")(
                a(href := "/")("Go to the message board"),
            ),
        )
    }

    def loginPageBody() = {
        body(
            loginBoard(),
            registerBoard()
        )
    }

    def loginBoard() = {
        div(`class` := "content")(
            h1("Login"),
          
            div(id := "loginBoard")(
                loginForm()
            )
        )
    }

    def registerBoard() = {
        div(`class` := "content")(
            h1("Register"),
            div(id := "registerBoard")(
                registerForm()
            )
        )
    }

    def customForm(id_ : String, action_ : String, formName : String) = {
      form(id := "loginForm", action := "/login", method :="POST")(
        div(id := "errorDiv", `class` := "errorMsg"),
        label(`for` := "loginInput")("Username:"),
        input(`type` := "text", id := "loginInput", placeholder := "Write your username"),
        input(`type` := "submit", value := "Envoyer")
      )
    }

    def loginForm() = {
      form(id := "loginForm", action := "/login", method :="POST")(
        div(id := "errorDiv", `class` := "errorMsg"),
        label(`for` := "loginInput")("Username:"),
        input(`type` := "text", id := "loginInput", placeholder := "Write your username"),
        input(`type` := "submit", value := "Envoyer")
      )
    }

    def registerForm() = {
      form(id := "registerForm", action := "/register", method := "POST")(
        div(id := "errorDiv", `class` := "errorMsg"),
        label(`for` := "registerInput")("Username:"),
        input(`type` := "text", id := "registerInput", placeholder := "Write your username"),
        input(`type` := "submit", value := "Envoyer")
      )
    }



end Layouts
