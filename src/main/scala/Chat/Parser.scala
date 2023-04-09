package Chat

class UnexpectedTokenException(msg: String) extends Exception(msg) {}

class Parser(tokenized: Tokenized):
  import ExprTree._
  import Chat.Token._

  // Start the process by reading the first token.
  var curTuple: (String, Token) = tokenized.nextToken()

  def curValue: String = curTuple._1
  def curToken: Token = curTuple._2

  /** Reads the next token and assigns it into the global variable curTuple */
  def readToken(): Unit = curTuple = tokenized.nextToken()

  /** "Eats" the expected token and returns it value, or terminates with an
    * error.
    */
  private def eat(token: Token): String =
    if token == curToken then
      val tmp = curValue
      readToken()
      tmp
    else expected(token)

  /** Complains that what was found was not expected. The method accepts
    * arbitrarily many arguments of type Token
    */
  private def expected(token: Token, more: Token*): Nothing =
    expected(more.prepended(token))
  private def expected(tokens: Seq[Token]): Nothing =
    val expectedTokens = tokens.mkString(" or ")
    throw new UnexpectedTokenException(
      s"Expected: $expectedTokens, found: $curToken"
    )

  /** the root method of the parser: parses an entry phrase */
  // TODO - Part 2 Step 4
  def parsePhrases(): ExprTree =

    if curToken == BONJOUR then readToken()

    curToken match
      case JE =>
        readToken()
        curToken match

          case ETRE =>
            readToken()
            curToken match
              case ASSOIFFE | AFFAME =>
                parseStateOFMind()
              case PSEUDO =>
                parsePseudo()
              case _ => expected(ASSOIFFE, AFFAME, PSEUDO)

          case ME =>
            readToken()
            if curToken == APPELER then
              readToken()
              parsePseudo()
            else expected(APPELER)

          case VOULOIR =>
            readToken()
            curToken match
              case CONNAITRE =>
                readToken()
                if curToken == MON then
                  readToken()
                  if curToken == SOLDE then
                    readToken()
                    CheckBalance
                  else expected(SOLDE)
                else expected(MON)
              case COMMANDER =>
                readToken()
                Order(parseCommand())
              case _ => expected(COMMANDER, CONNAITRE)

          case _ => expected(ETRE, ME, VOULOIR)

      case QUEL =>
        readToken()
        if curToken == ETRE then
          readToken()
          if curToken == LE then
            readToken()
            if curToken == PRIX then
              readToken()
              Price(parseCommand())
            else expected(PRIX)
          else expected(LE)
        else expected(ETRE)

      case COMBIEN =>
        readToken()
        if curToken == COUTER then
          readToken()
          Price(parseCommand())
        else expected(COUTER)

      case _ => expected(BONJOUR, JE, QUEL, VOULOIR)

  def parseStateOFMind(): ExprTree =
    curToken match
      case ASSOIFFE =>
        readToken()
        Thirsty
      case AFFAME =>
        readToken()
        Hungry
      case _ => expected(ASSOIFFE, AFFAME)

  def parsePseudo(): ExprTree =
    val pseudo = eat(PSEUDO)
    Pseudo(pseudo)

  def parseCommand(): ExprTree.ProductAndLogic =
    if curToken == NUM then
      val num = eat(NUM).toInt
      if curToken == PRODUIT then
        val productType = eat(PRODUIT)
        val brand: Option[String] =
          if curToken == MARQUE then Option(eat(MARQUE))
          else None
        val prod = Product(num, productType, brand)
        curToken match
          case ET =>
            readToken()
            And(prod, parseCommand())
          case OU =>
            readToken()
            Or(prod, parseCommand())
          case _ =>
            prod
      else expected(PRODUIT)
    else expected(NUM)
