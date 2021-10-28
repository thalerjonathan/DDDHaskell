Feature: Transfering money between accounts
  In order to manage my money more efficiently
  As a bank client
  I want to transfer money between accounts whenever I need to.

  Scenario: Transfer money from a Giro account to another Giro account
    Given a Giro account with Iban 'AT12 12345 01234567890' and a balance of 1000
    And a Giro account with Iban 'AT98 98765 09876543210' and a balance of 500
    When Transferring 200 from Iban 'AT12 12345 01234567890' to Iban 'AT98 98765 09876543210'
    Then There should be a balance of 800 in the Giro account with Iban 'AT12 12345 01234567890'
    And There should be a balance of 700 in my second Giro account with Iban 'AT98 98765 09876543210'

