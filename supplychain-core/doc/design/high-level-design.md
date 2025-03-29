# High-level design of the supply chain library

## Actor
Each organization in the supply chain library is an `Actor`. An actor is a communicating entity that can send and receive `Content`. Content can for instance be information, messages, or shipments with products. Actors store Content in a `ContentStore` to be able to access older content for business logic such as decision making or forecasting. A content store can be seen as the organization's ERP system. 

Actors that have to send invoices and pay bills are of the type `FinancingActor`. Financing actors have a `BankAccount` for the finances. A bank account is registered with a specific type of actor that implements the `BankingActor` interface and that fulfills the `BankingRole`. The bank takes care of interest rates, currency exchange, and loans. 

Actors that buy and sell products typically are of the type `InventoryActor`, which means that they can store products in their inventory. The business logic for storing and retreiving inventory is handled by the `InventoryRole`. 

Buying and selling is separated, because customers or markets in models typically only buy, and suppliers in a supply chain only sell. Being a buying and/or selling actor presuppose that the organization is a financing actor. This means that the ability to fulfill certain roles can be dependent on having certain other roles. Note that selling does not mean that the actor has its own inventoty: inventory can be held by another organization, such as an independent distribution center in a certain region.

The current ideas to implement actors and roles are as follows:

Actor
* BankingActor extends Actor
* InventoryActor extends Actor
* FinancingActor extends Actor
* BuyingActor extends FinancingActor
* SellingActor extends FinancingActor
* ConsumingActor extends Actor
* TransportingActor extends Actor
* ShippingActor extends InventoryActor
* ReceivingAcor extends InventoryActor
* ManufacturingActor extends InventoryActor
* GoverningActor extends Actor
* ConnectingActor extends Actor

For now, we will leave out repair and refurbishing.