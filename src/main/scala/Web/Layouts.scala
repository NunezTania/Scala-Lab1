package Web

import scalatags.Text.all._
import scalatags.Text.tags2.nav
// import the type Username from MessageService
import Data.MessageService.Username
import Data.MessageService.MsgContent

/** Assembles the method used to layout ScalaTags
  */
object Layouts:
  // Ce fichier n'est pas du tout optimisé. Les fonctions sont répétitives et
  // il y a beaucoup de code dupliqué. Il est possible de factoriser le code,
  // mais nous manquons de temps pour le faire.

  // --------------------- homePage ---------------------
  def homePage(
      msgList: Seq[(Username, MsgContent)],
      error: Option[String] = None,
      logged : Boolean = false
  ) = {
    html(
      header("/resources/css/main.css", "/resources/js/main.js"),
      homePageNav(logged),
      homePageBody(msgList, error)
    )
  }

  def header(cssPath: String, jsPath: String) = {
    head(
      link(rel := "stylesheet", href := cssPath),
      script(src := jsPath)
    )
  }

  def homePageNav(logged : Boolean = false) = {
    nav(
      a(`class` := "nav-brand")("Bot-tender"),
      div(`class` := "nav-item")(
        if logged then a(href := "/logout")("Log out")
        else a(href := "/login")("Log in")
      )
    )
  }

  def homePageBody(
      msgList: Seq[(Username, MsgContent)],
      error: Option[String] = None
  ) = {
    body(
      boardMessage(msgList, error)
    )
  }

  def boardMessage(
      msgList: Seq[(Username, MsgContent)],
      error: Option[String] = None
  ) = {
    div(`class` := "content")(
      div(id := "boardMessage")(
        if msgList.isEmpty then "There's no message"
        else msgList.map((msg) => message(msg._1, msg._2))
      ),
      messagesForm(error)
    )
  }

  def message(author: Username, message: MsgContent) = {
    div(`class` := "msg")(
      span(`class` := "author")(author),
      span(`class` := "msg-content")(message)
    )
  }

  def messagesForm(error: Option[String] = None) = {
    div(id := "messagesForm")(
      if error.isDefined then
        div(id := "errorDiv", `class` := "errorMsg")(error.get)
      else div(),
      form(
        id := "msgForm",
        attr("onsubmit") := "submitMessageForm(); return false;",
        div(id := "errorDiv", `class` := "errorMsg"),
        label("Your message:", attr("for") := "messageInput"),
        input(
          attr("type") := "text",
          placeholder := "Write your message",
          id := "messageInput"
        ),
        input(attr("type") := "submit")
      )
    )
  }

  // ----------------- Login page -----------------

  def loginPage(error: Option[(Int, String)]) = {
    html(
      header("/resources/css/main.css", "/resources/js/main.js"),
      loginPageNav(),
      loginPageBody(error: Option[(Int, String)])
    )
  }

  def loginPageNav() = {
    nav(
      a(`class` := "nav-brand")("Bot-tender"),
      div(`class` := "nav-item")(
        a(href := "/")("Go to the message board")
      )
    )
  }

  def loginPageBody(error: Option[(Int, String)]) = {
    body(
      loginBoard(error: Option[(Int, String)]),
      registerBoard(error: Option[(Int, String)])
    )
  }

  def loginBoard(error: Option[(Int, String)]) = {
    div(`class` := "content")(
      h1("Login"),
      if error.isDefined && error.get._1 == 0 then
        div(id := "errorDiv", `class` := "errorMsg")(error.get._2)
      else div(),
      div(id := "loginBoard")(
        loginForm()
      )
    )
  }

  def registerBoard(error: Option[(Int, String)]) = {
    div(`class` := "content")(
      h1("Register"),
      if error.isDefined && error.get._1 == 1 then
        div(id := "errorDiv", `class` := "errorMsg")(error.get._2)
      else div(),
      div(id := "registerBoard")(
        registerForm()
      )
    )
  }

  def loginForm() = {
    form(id := "loginForm", action := "/login", method := "post")(
      div(id := "errorDiv", `class` := "errorMsg"),
      label(`for` := "loginInput")("Username:"),
      input(
        `type` := "text",
        id := "loginInput",
        name := "username",
        placeholder := "Write your username"
      ),
      input(`type` := "submit", value := "Envoyer")
    )
  }

  def registerForm() = {
    form(id := "registerForm", action := "/register", method := "post")(
      div(id := "errorDiv", `class` := "errorMsg"),
      label(`for` := "registerInput")("Username:"),
      input(
        `type` := "text",
        id := "registerInput",
        name := "username",
        placeholder := "Write your username"
      ),
      input(`type` := "submit", value := "Envoyer")
    )
  }

  // ----------------- Successful login page -----------------

  def successfulLoginPage(username: String) = {
    html(
      header("/resources/css/main.css", "/resources/js/main.js"),
      successfulLoginPageNav(),
      successfulLoginPageBody(username)
    )
  }

  def successfulLoginPageNav() = {
    nav(
      a(`class` := "nav-brand")("Bot-tender"),
      div(`class` := "nav-item")(
        a(href := "/")("Go to the message board")
      )
    )
  }

  def successfulLoginPageBody(username: String) = {
    body(
      div(`class` := "content")(
        h1("Welcome " + username),
        p("You have successfully logged in !")
      )
    )
  }

  // ----------------- Successful register page -----------------
  def successfulRegisterPage(username: String) = {
    html(
      header("/resources/css/main.css", "/resources/js/main.js"),
      successfulRegisterPageNav(),
      successfulRegisterPageBody(username)
    )
  }

  def successfulRegisterPageNav() = {
    nav(
      a(`class` := "nav-brand")("Bot-tender"),
      div(`class` := "nav-item")(
        a(href := "/")("Go to the message board")
      )
    )
  }

  def successfulRegisterPageBody(username: String) = {
    body(
      div(`class` := "content")(
        h1("Welcome " + username + " !"),
        p("You have successfully registered !")
      )
    )
  }

  // ----------------- Logout page -----------------
  def logoutPage() = {
    html(
      header("/resources/css/main.css", "/resources/js/main.js"),
      logoutPageBody()
    )
  }

  def logoutPageBody() = {
    body(
      div(`class` := "content")(
        a(href := "/")("Go to the message board"),
        h1("You have successfully logged out !"),
        p("See you soon")
      )
    )
  }

end Layouts
