# Banking System
TODO: define a precise Ubiquitous Language

account with transaction lines, belonging to a user, has an iban, is at a bank. Transactions are eventually consistent. Model ledger of transactions as an entity.

1. List customers by applying a flexible and complex filter.
2. List the orders when looking at a specific customer.
3. An order can have many different lines. An order can have many order lines, where each line describes a product and the number of that product that has been ordered.
4. Concurrency conflict detection is important.
5. A customer may not owe us more than a certain amount of money.
6. An order may not have a total value of more than one million Euro. This limit (unlike the previous one) is a system-wide rule.
7. Each order and customer should have a unique and user-friendly number. Gaps in the series are acceptable.
8. Before a new customer is considered acceptable, their credit will be checked with a credit institute.
9. An order must have a customer; an order line must have an order. There must not be any orders with an undefined customer. The same goes for order lines: they must belong to an order.
10. Saving an order and its lines should be atomic.
11. Orders have an acceptance status that is changed by the user. 