package Chat

/** This sealed trait represents a node of the tree.
  */
sealed trait ExprTree

/** Declarations of the nodes' types.
  */
object ExprTree:
  // TODO - Part 2 Step 3

  case class Pseudo(name: String) extends ExprTree

  sealed trait StateOfMind extends ExprTree
  case object Thirsty extends StateOfMind
  case object Hungry extends StateOfMind

  sealed trait ProductAndLogic extends ExprTree
  case class Product(
      quantity: Int,
      productType: String,
      brand: Option[String]
  ) extends ExprTree

  case class And(left: ExprTree, right: ExprTree)
      extends ExprTree
  case class Or(left: ExprTree, right: ExprTree)
      extends ExprTree

  case class Order(products: ExprTree) extends ExprTree

  case object CheckBalance extends ExprTree

  case class Price(products: ExprTree) extends ExprTree
