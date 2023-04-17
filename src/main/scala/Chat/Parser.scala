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
                etatAme()
              case PSEUDO =>
                pseudo()
              case _ => expected(ASSOIFFE, AFFAME, PSEUDO)
          case ME =>
            readToken()
            if curToken == APPELER then
              readToken()
              pseudo()
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
                    solde()
                  else expected(SOLDE)
                else expected(MON)
              case COMMANDER =>
                commande()
              case _ => expected(COMMANDER, CONNAITRE)
          case _ => expected(ETRE, ME, VOULOIR)
      case QUEL | COMBIEN =>
        prix()
      case _ => expected(BONJOUR, JE, QUEL, COMBIEN)

  def pseudo(): ExprTree =
    val pseudo = eat(PSEUDO)
    Pseudo(pseudo)

  def produit(): ExprTree =
    val quantite = eat(NUM)
    val typeProduit = eat(PRODUIT)
    val marque = curToken match
      case MARQUE =>
        Some(eat(MARQUE))
      case _ => None
    Product(quantite.toInt, typeProduit, marque)

  def etatAme(): ExprTree =
    curToken match
      case ASSOIFFE =>
        readToken()
        Thirsty
      case AFFAME =>
        readToken()
        Hungry
      case _ => expected(ASSOIFFE, AFFAME)

  def produits(): ExprTree =
    // Introduction d'une méthode interne récursive avec accumulateur pour associativité gauche
    def _produits(acc : ExprTree): ExprTree =
      curToken match
        case ET =>
          readToken()
          val prod = produit()
          _produits(And(acc, prod))
        case OU =>
          readToken()
          val prod = produit()
          _produits(Or(acc, prod))
        case _ => acc
    // On passe le premier produit lu comme accumulateur pour construire l'arbre de gauche à droite
    _produits(produit())
  
  def commande(): ExprTree =
    eat(COMMANDER)
    Order(produits())

  def solde(): ExprTree = CheckBalance
  
  def prix(): ExprTree =
    curToken match
      case QUEL =>
        readToken()
        val etre = eat(ETRE)
        val le = eat(LE)
        val prix = eat(PRIX)
        val de = eat(DE)
        Price(produits())
      case COMBIEN =>
        readToken()
        val couter = eat(COUTER)
        Price(produits())
      case _ => expected(QUEL, COMBIEN)