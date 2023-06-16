package Chat
import Data.{AccountService, ProductService, Session}
import Chat.ExprTree.Order
import Utils.FutureOps
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnexpectedExprTreeException(msg: String) extends Exception(msg) {}

class AnalyzerService(productSvc: ProductService, accountSvc: AccountService):
  import ExprTree._

  /** Compute the price of the current node, then returns it. If the node is not
    * a computational node, the method returns 0.0. For example if we had a "+"
    * node, we would add the values of its two children, then return the result.
    * @return
    *   the result of the computation
    */
  def computePrice(t: ExprTree): Double =
    t match
      case And(left, right) => computePrice(left) + computePrice(right)
      case Or(left, right) =>
        if computePrice(left) < computePrice(right) then computePrice(left)
        else computePrice(right)
      case Product(quantity, productType, brand) =>
        productSvc.getPrice(
          productType,
          // Si la marque n'est pas spécifiée, on récupère celle par défaut
          brand.getOrElse(productSvc.getDefaultBrand(productType))
        ) * quantity
      case Order(products) => computePrice(products)
      case Price(products) => computePrice(products)
      case unexpected               => throw new UnexpectedExprTreeException(s"Expected: Products or Product, found: $unexpected")
    

  /** Return the output text of the current node, in order to write it in
    * console.
    * @return
    *   the output text of the current node
    */
  def reply(session: Session)(t: ExprTree): (String, Option[Future[String]]) =
    val inner: ExprTree => (String, Option[Future[String]]) = reply(session)
    t match
      case Thirsty =>
        ("Eh bien, la chance est de votre cote, car nous offrons les meilleures bieres de la region !", None)
      case Hungry =>  
        ("Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !", None)
      case Pseudo(name) =>
        if !accountSvc.isAccountExisting(name) then accountSvc.addAccount(name)
        session.setCurrentUser(name)
        (s"Bonjour, ${name.toLowerCase().tail} !", None)
      case Product(quantity, productType, brand) =>
        val price = computePrice(t)
        val b = brand.getOrElse(productSvc.getDefaultBrand(productType))
        (s" $quantity $productType $b", None)
      case And(left, right) => (inner(left)._1 + " et " + inner(right)._1, None)
      case Or(left, right) =>
        if (computePrice(left) < computePrice(right)) then inner(left)
        else inner(right)
      case Order(products) =>
        if session.getCurrentUser.isEmpty then
          ("Veuillez d'abord vous identifier !", None)
        else
          val prodlist = getProductList(products)
          val prepared = productSvc.prepareProducts(prodlist)
          val res = (prepared.flatMap( l =>
            val preparedProds : ExprTree = prodlist.zip(l)
            .map((prod, nPrepared) => Product(nPrepared, prod.productType, prod.brand))
            .filter(_.quantity > 0)
            .reduce((a, b) => And(a, b))
            val price = computePrice(preparedProds)
            if price > accountSvc.getAccountBalance(session.getCurrentUser.get) then Future("Vous n'avez pas assez d'argent !")
            else
              val part = l.reduce((a, b) => a + b) != prodlist.map(_.quantity).reduce((a, b) => a + b)
              val status = if part then " partiellement" else ""
              val partialCmd = if part then s" Voici ${inner(preparedProds)._1}" else ""
              Future(
                s"La commande de ${inner(products)._1} est" +
                  status +
                " prête." +
                partialCmd +
                s" Cela coute  $price.- et il vous reste ${accountSvc.purchase(session.getCurrentUser.get, price)} CHF sur votre compte.")))
          (s"Votre commande est en cours de préparation : ${inner(products)._1}", Some(res))
      case CheckBalance =>
        session.getCurrentUser match
          case Some(user) =>
            (s"Votre solde est de ${accountSvc.getAccountBalance(user)} CHF.", None)
          case None => ("Vous n'etes pas connecte !" , None)
      case Price(products) =>
        ("Cela coute " + computePrice(t) + " CHF.", None)

  def getProductList(order : ExprTree) : List[Product] =
    order match
      case Product(quantity, productType, brand) => List(Product(quantity, productType, brand))
      case And(left, right) => getProductList(left) ++ getProductList(right)
      case Or(left, right) => if computePrice(left) < computePrice(right) then getProductList(left) else getProductList(right)
      case unexpected => throw new UnexpectedExprTreeException(s"Expected: Products or Product, found: $unexpected")

  def productToSring(product : Product) : String =
    product.quantity + " " + product.productType + " " + product.brand.getOrElse(productSvc.getDefaultBrand(product.productType))
end AnalyzerService
